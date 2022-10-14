const BlockUtility = require('./block-utility');
const BlocksExecuteCache = require('./blocks-execute-cache');
const log = require('../util/log');
const Thread = require('./thread');
const {Map} = require('immutable');
const cast = require('../util/cast');
const constants = require('./scratch-blocks-constants');

/**
 * Single BlockUtility instance reused by execute for every pritimive ran.
 * @const
 */
const blockUtility = new BlockUtility();

/**
 * Profiler frame name for block functions.
 * @const {string}
 */
const blockFunctionProfilerFrame = 'blockFunction';

/**
 * Profiler frame ID for 'blockFunction'.
 * @type {number}
 */
let blockFunctionProfilerId = -1;

/**
 * Utility function to determine if a value is a Promise.
 * @param {*} value Value to check for a Promise.
 * @return {boolean} True if the value appears to be a Promise.
 */
const isPromise = function (value) {
    return (
        value !== null &&
        typeof value === 'object' &&
        typeof value.then === 'function'
    );
};

/**
 * Handle any reported value from the primitive, either directly returned
 * or after a promise resolves.
 * @param {*} resolvedValue Value eventually returned from the primitive.
 * @param {!Sequencer} sequencer Sequencer stepping the thread for the ran
 * primitive.
 * @param {!Thread} thread Thread containing the primitive.
 * @param {!string} currentBlockId Id of the block in its thread for value from
 * the primitive.
 * @param {!string} opcode opcode used to identify a block function primitive.
 * @param {!boolean} isHat Is the current block a hat?
 */
// @todo move this to callback attached to the thread when we have performance
// metrics (dd)
const handleReport = function (resolvedValue, sequencer, thread, blockCached, lastOperation) {
    const currentBlockId = blockCached.id;
    const opcode = blockCached.opcode;
    const isHat = blockCached._isHat;

    thread.pushReportedValue(resolvedValue);
    if (isHat) {
        // Hat predicate was evaluated.
        if (sequencer.runtime.getIsEdgeActivatedHat(opcode)) {
            // If this is an edge-activated hat, only proceed if the value is
            // true and used to be false, or the stack was activated explicitly
            // via stack click
            if (!thread.stackClick) {
                const hasOldEdgeValue = thread.target.hasEdgeActivatedValue(currentBlockId);
                const oldEdgeValue = thread.target.updateEdgeActivatedValue(
                    currentBlockId,
                    resolvedValue
                );

                const edgeWasActivated = hasOldEdgeValue ? (!oldEdgeValue && resolvedValue) : resolvedValue;
                if (!edgeWasActivated) {
                    sequencer.retireThread(thread);
                }
            }
        } else if (!resolvedValue) {
            // Not an edge-activated hat: retire the thread
            // if predicate was false.
            sequencer.retireThread(thread);
        }
    } else {
        // In a non-hat, report the value visually if necessary if
        // at the top of the thread stack.
        if (lastOperation && typeof resolvedValue !== 'undefined' && thread.atStackTop()) {
            if (thread.stackClick) {
                sequencer.runtime.visualReport(currentBlockId, resolvedValue);
            }
            if (thread.updateMonitor) {
                const targetId = sequencer.runtime.monitorBlocks.getBlock(currentBlockId).targetId;
                if (targetId && !sequencer.runtime.getTargetById(targetId)) {
                    // Target no longer exists
                    return;
                }
                sequencer.runtime.requestUpdateMonitor(Map({
                    id: currentBlockId,
                    spriteName: targetId ? sequencer.runtime.getTargetById(targetId).getName() : null,
                    value: resolvedValue
                }));
            }
        }
        // Finished any yields.
        thread.status = Thread.STATUS_RUNNING;
    }
};

const handlePromise = (primitiveReportedValue, sequencer, thread, blockCached, lastOperation) => {
    if (thread.status === Thread.STATUS_RUNNING) {
        // Primitive returned a promise; automatically yield thread.
        thread.status = Thread.STATUS_PROMISE_WAIT;
    }
    // Promise handlers
    primitiveReportedValue.then(resolvedValue => {
        handleReport(resolvedValue, sequencer, thread, blockCached, lastOperation);
        // If it's a command block or a top level reporter in a stackClick.
        if (lastOperation) {
            let stackFrame;
            let nextBlockId;
            do {
                // In the case that the promise is the last block in the current thread stack
                // We need to pop out repeatedly until we find the next block.
                const popped = thread.popStack();
                if (popped === null) {
                    return;
                }
                nextBlockId = thread.target.blocks.getNextBlock(popped);
                if (nextBlockId !== null) {
                    // A next block exists so break out this loop
                    break;
                }
                // Investigate the next block and if not in a loop,
                // then repeat and pop the next item off the stack frame
                stackFrame = thread.peekStackFrame();
            } while (stackFrame !== null && !stackFrame.isLoop);

            thread.pushStack(nextBlockId);
        }
    }, rejectionReason => {
        // Promise rejected: the primitive had some error.
        // Log it and proceed.
        log.warn('Primitive rejected promise: ', rejectionReason);
        thread.status = Thread.STATUS_RUNNING;
        thread.popStack();
    });
};

/**
 * A execute.js internal representation of a block to reduce the time spent in
 * execute as the same blocks are called the most.
 *
 * With the help of the Blocks class create a mutable copy of block
 * information. The members of BlockCached derived values of block information
 * that does not need to be reevaluated until a change in Blocks. Since Blocks
 * handles where the cache instance is stored, it drops all cache versions of a
 * block when any change happens to it. This way we can quickly execute blocks
 * and keep perform the right action according to the current block information
 * in the editor.
 *
 * @param {Blocks} blockContainer the related Blocks instance
 * @param {object} cached default set of cached values
 */
class BlockCached {
    constructor (blockContainer, cached) {
        /**
         * Block id in its parent set of blocks.
         * @type {string}
         */
        this.id = cached.id;

        /**
         * Block operation code for this block.
         * @type {string}
         */
        this.opcode = cached.opcode;

        /**
         * Original block object containing argument values for static fields.
         * @type {object}
         */
        this.fields = cached.fields;

        /**
         * Original block object containing argument values for executable inputs.
         * @type {object}
         */
        this.inputs = cached.inputs;

        /**
         * Procedure mutation.
         * @type {?object}
         */
        this.mutation = cached.mutation;

        /**
         * Is the opcode a hat (event responder) block.
         * @type {boolean}
         */
        this._isHat = false;

        /**
         * The block opcode's implementation function.
         * @type {?function}
         */
        this._blockFunction = null;

        /**
         * Is the block function defined for this opcode?
         * @type {boolean}
         */
        this._definedBlockFunction = false;

        /**
         * Is this block a block with no function but a static value to return.
         * @type {boolean}
         */
        this._isShadowBlock = false;

        /**
         * The static value of this block if it is a shadow block.
         * @type {?any}
         */
        this._shadowValue = null;

        /**
         * A copy of the block's fields that may be modified.
         * @type {object}
         */
        this._fields = Object.assign({}, this.fields);

        /**
         * A copy of the block's inputs that may be modified.
         * @type {object}
         */
        this._inputs = Object.assign({}, this.inputs);

        /**
         * An arguments object for block implementations. All executions of this
         * specific block will use this objecct.
         * @type {object}
         */
        this._argValues = {
            mutation: this.mutation
        };

        /**
         * The inputs key the parent refers to this BlockCached by.
         * @type {string}
         */
        this._parentKey = null;

        /**
         * The target object where the parent wants the resulting value stored
         * with _parentKey as the key.
         * @type {object}
         */
        this._parentValues = null;

        /**
         * A sequence of shadow value operations that can be performed in any
         * order and are easier to perform given that they are static.
         * @type {Array<BlockCached>}
         */
        this._shadowOps = [];

        /**
         * A sequence of non-shadow operations that can must be performed. This
         * list recreates the order this block and its children are executed.
         * Since the order is always the same we can safely store that order
         * and iterate over the operations instead of dynamically walking the
         * tree every time.
         * @type {Array<BlockCached>}
         */
        this._ops = [];

        const {runtime} = blockUtility.sequencer;

        const {opcode, fields, inputs} = this;

        // Assign opcode isHat and blockFunction data to avoid dynamic lookups.
        this._isHat = runtime.getIsHat(opcode);
        this._blockFunction = runtime.getOpcodeFunction(opcode);
        this._definedBlockFunction = typeof this._blockFunction !== 'undefined';

        // Store the current shadow value if there is a shadow value.
        const fieldKeys = Object.keys(fields);
        this._isShadowBlock = (
            !this._definedBlockFunction &&
            fieldKeys.length === 1 &&
            Object.keys(inputs).length === 0
        );
        this._shadowValue = this._isShadowBlock && fields[fieldKeys[0]].value;

        // Store the static fields onto _argValues.
        for (const fieldName in fields) {
            if (
                fieldName === 'VARIABLE' ||
                fieldName === 'LIST' ||
                fieldName === 'BROADCAST_OPTION' ||
                fieldName === 'IMAGE_OPTION'
            ) {
                this._argValues[fieldName] = {
                    id: fields[fieldName].id,
                    name: fields[fieldName].value
                };
            } else {
                this._argValues[fieldName] = fields[fieldName].value;
            }
        }

        // Remove custom_block. It is not part of block execution.
        delete this._inputs.custom_block;

        if ('BROADCAST_INPUT' in this._inputs) {
            // BROADCAST_INPUT is called BROADCAST_OPTION in the args and is an
            // object with an unchanging shape.
            this._argValues.BROADCAST_OPTION = {
                id: null,
                name: null
            };

            // We can go ahead and compute BROADCAST_INPUT if it is a shadow
            // value.
            const broadcastInput = this._inputs.BROADCAST_INPUT;
            if (broadcastInput.block === broadcastInput.shadow) {
                // Shadow dropdown menu is being used.
                // Get the appropriate information out of it.
                const shadow = blockContainer.getBlock(broadcastInput.shadow);
                const broadcastField = shadow.fields.BROADCAST_OPTION;
                this._argValues.BROADCAST_OPTION.id = broadcastField.id;
                this._argValues.BROADCAST_OPTION.name = broadcastField.value;

                // Evaluating BROADCAST_INPUT here we do not need to do so
                // later.
                delete this._inputs.BROADCAST_INPUT;
            }
        }

        if ('IMAGE_INPUT' in this._inputs) {
            // BROADCAST_INPUT is called BROADCAST_OPTION in the args and is an
            // object with an unchanging shape.
            this._argValues.IMAGE_OPTION = {
                id: null,
                name: null
            };

            // We can go ahead and compute BROADCAST_INPUT if it is a shadow
            // value.
            const imageInput = this._inputs.IMAGE_INPUT;
            if (imageInput.block === imageInput.shadow) {
                // Shadow dropdown menu is being used.
                // Get the appropriate information out of it.
                const shadow = blockContainer.getBlock(imageInput.shadow);
                const imageField = shadow.fields.IMAGE_OPTION;
                this._argValues.IMAGE_OPTION.id = imageField.id;
                this._argValues.IMAGE_OPTION.name = imageField.value;

                // Evaluating BROADCAST_INPUT here we do not need to do so
                // later.
                delete this._inputs.IMAGE_INPUT;
            }
        }

        // Cache all input children blocks in the operation lists. The
        // operations can later be run in the order they appear in correctly
        // executing the operations quickly in a flat loop instead of needing to
        // recursivly iterate them.
        for (const inputName in this._inputs) {
            const input = this._inputs[inputName];
            if (input.block) {
                const inputCached = BlocksExecuteCache.getCached(blockContainer, input.block, BlockCached);

                if (inputCached._isHat) {
                    continue;
                }

                this._shadowOps.push(...inputCached._shadowOps);
                this._ops.push(...inputCached._ops);
                inputCached._parentKey = inputName;
                inputCached._parentValues = this._argValues;

                // Shadow values are static and do not change, go ahead and
                // store their value on args.
                if (inputCached._isShadowBlock) {
                    if (runtime.getCode && inputCached.opcode === 'text' && runtime.isArduinoMode) {
                        this._argValues[inputName] = `"${inputCached._shadowValue}"`;
                    } else {
                        this._argValues[inputName] = inputCached._shadowValue;
                    }
                }
            }
        }

        // The final operation is this block itself. At the top most block is a
        // command block or a block that is being run as a monitor.
        if (!this._isHat && this._isShadowBlock) {
            this._shadowOps.push(this);
        } else if (this._definedBlockFunction) {
            this._ops.push(this);
        }
    }
}

/**
 * Execute a block.
 * @param {!Sequencer} sequencer Which sequencer is executing.
 * @param {!Thread} thread Thread which to read and execute.
 */
const execute = function (sequencer, thread) {
    const runtime = sequencer.runtime;
    thread.blockContainer.runtime.currentTopBlock = thread.topBlock;
    // store sequencer and thread so block functions can access them through
    // convenience methods.
    blockUtility.sequencer = sequencer;
    blockUtility.thread = thread;

    // Current block to execute is the one on the top of the stack.
    const currentBlockId = thread.peekStack();
    if(currentBlockId == constants.PROC_CALL_END){
        if(runtime.UDFs[runtime.UDFsUnderGeneration[runtime.UDFsUnderGeneration.length - 1]]){
           runtime.UDFs[runtime.UDFsUnderGeneration.pop()]["completed"] = true;
        }
        if(runtime.UDFsUnderGeneration.length == 0){
            runtime.inUDF = false;
        }
        return;
    }

    /**
     * added
     * Implementation support for C-type blocks.
     * start
     */
    if (currentBlockId == constants.HARDWARE_CLOSE_BRACE) {
        runtime.decreaseNestBlockCount();
        runtime.codeGenerateHelper(constants.HARDWARE_CLOSE_BRACE);
        thread.popStack();
        return;
    }
    if (currentBlockId == constants.HARDWARE_ELSE_STATEMENT) {
        runtime.codeGenerateHelper(constants.HARDWARE_ELSE_STATEMENT);
        thread.popStack();
        return;
    }
    /**
     * end
     */
    const currentStackFrame = thread.peekStackFrame();

    let blockContainer = thread.blockContainer;
    let blockCached = BlocksExecuteCache.getCached(blockContainer, currentBlockId, BlockCached);
    if (blockCached === null) {
        blockContainer = runtime.flyoutBlocks;
        blockCached = BlocksExecuteCache.getCached(blockContainer, currentBlockId, BlockCached);
        // Stop if block or target no longer exists.
        if (blockCached === null) {
            // No block found: stop the thread; script no longer exists.
            sequencer.retireThread(thread);
            return;
        }
    }

    const ops = blockCached._ops;
    const length = ops.length;
    let i = 0;

    if (currentStackFrame.reported !== null) {
        const reported = currentStackFrame.reported;
        // Reinstate all the previous values.
        for (; i < reported.length; i++) {
            const {opCached: oldOpCached, inputValue} = reported[i];

            const opCached = ops.find(op => op.id === oldOpCached);

            if (opCached) {
                const inputName = opCached._parentKey;
                const argValues = opCached._parentValues;

                if (inputName === 'BROADCAST_INPUT') {
                    // Something is plugged into the broadcast input.
                    // Cast it to a string. We don't need an id here.
                }
                else if ( inputName === 'IMAGE_INPUT' ){
                    argValues.IMAGE_OPTION.id = null;
                    argValues.IMAGE_OPTION.name = cast.toString(inputValue);
                }
                else {
                    argValues[inputName] = inputValue;
                }
            }
        }

        // Find the last reported block that is still in the set of operations.
        // This way if the last operation was removed, we'll find the next
        // candidate. If an earlier block that was performed was removed then
        // we'll find the index where the last operation is now.
        if (reported.length > 0) {
            const lastExisting = reported.reverse().find(report => ops.find(op => op.id === report.opCached));
            if (lastExisting) {
                i = ops.findIndex(opCached => opCached.id === lastExisting.opCached) + 1;
            } else {
                i = 0;
            }
        }

        // The reporting block must exist and must be the next one in the sequence of operations.
        if (thread.justReported !== null && ops[i] && ops[i].id === currentStackFrame.reporting) {
            const opCached = ops[i];
            const inputValue = thread.justReported;

            thread.justReported = null;

            const inputName = opCached._parentKey;
            const argValues = opCached._parentValues;

            if (inputName === 'BROADCAST_INPUT') {
                // Something is plugged into the broadcast input.
                // Cast it to a string. We don't need an id here.
                argValues.BROADCAST_OPTION.id = null;
                argValues.BROADCAST_OPTION.name = cast.toString(inputValue);
            } else if (inputName === 'IMAGE_INPUT') {
                // Something is plugged into the broadcast input.
                // Cast it to a string. We don't need an id here.
                argValues.IMAGE_OPTION.id = null;
                argValues.IMAGE_OPTION.name = cast.toString(inputValue);
            } else {
                argValues[inputName] = inputValue;
            }
            // else {
            //     if (argValues !== null) {
            //         argValues[inputName] = inputValue;
            //     }
            // }

            i += 1;
        }

        currentStackFrame.reporting = null;
        currentStackFrame.reported = null;
    }

    for (; i < length; i++) {
        const lastOperation = i === length - 1;
        const opCached = ops[i];

        const blockFunction = opCached._blockFunction;

        // Update values for arguments (inputs).
        const argValues = opCached._argValues;

        if(runtime.getCode){
            if(opCached["opcode"] === "procedures_definition"){
                runtime.UDFsUnderGeneration.push(thread.blockContainer._blocks[opCached["inputs"]["custom_block"]["block"]]["mutation"]["proccode"]);
                runtime.codeGenerateHelper(blockUtility.generateProcedureDefinition(thread.blockContainer._blocks, opCached));
            }

            if(runtime.blocksToStopGeneratingInUDF[runtime.blocksToStopGeneratingInUDF.length - 1] === opCached["id"]){
                runtime.blocksToStopGeneratingInUDF.pop();
                runtime.UDFs[runtime.UDFsUnderGeneration.pop()]["completed"] = true;
            }

            if(runtime.UDFsUnderGeneration.length == 0){
                runtime.inUDF = false;
            }

            for(input in opCached._inputs){
                if(thread.blockContainer._blocks[opCached._inputs[input]["block"]] !== undefined){
                    if(["argument_reporter_string_number", "argument_reporter_boolean"].indexOf(
                            thread.blockContainer._blocks[opCached._inputs[input]["block"]]["opcode"]) + 1){
                        argValues[opCached._inputs[input]["name"]] = thread.blockContainer._blocks[opCached._inputs[input]["block"]]["fields"]["VALUE"]["value"];
                    }
                }
            }

            if(opCached["opcode"] === "procedures_call"){
                runtime.codeGenerateHelper(blockUtility.generateProcedureCall(opCached, argValues));

                if(thread.blockContainer._blocks[opCached["id"]]["next"] !== null){
                    runtime.blocksToStopGeneratingInUDF.push(thread.blockContainer._blocks[opCached["id"]]["next"]);
                }
                //When procedure call is last block inside the c shaped block then to determine when
                //the procedure call has ended we provide a dummy next block name
                else{
                    thread.blockContainer._blocks[opCached["id"]]["next"] = constants.PROC_CALL_END;
                }
                runtime.inUDF = true;
            }
        }
        // Fields are set during opCached initialization.

        // Blocks should glow when a script is starting,
        // not after it has finished (see #1404).
        // Only blocks in blockContainers that don't forceNoGlow
        // should request a glow.
        if (!blockContainer.forceNoGlow) {
            thread.requestScriptGlowInFrame = true;
        }

        // Inputs are set during previous steps in the loop.

        let primitiveReportedValue = null;
        if (runtime.profiler === null) {
            primitiveReportedValue = blockFunction(argValues, blockUtility);
        } else {
            const opcode = opCached.opcode;
            if (blockFunctionProfilerId === -1) {
                blockFunctionProfilerId = runtime.profiler.idByName(blockFunctionProfilerFrame);
            }
            // The method commented below has its code inlined
            // underneath to reduce the bias recorded for the profiler's
            // calls in this time sensitive execute function.
            //
            // runtime.profiler.start(blockFunctionProfilerId, opcode);
            runtime.profiler.records.push(
                runtime.profiler.START, blockFunctionProfilerId, opcode, 0);

            primitiveReportedValue = blockFunction(argValues, blockUtility);

            // runtime.profiler.stop(blockFunctionProfilerId);
            runtime.profiler.records.push(runtime.profiler.STOP, 0);
        }

        // If it's a promise, wait until promise resolves.
        if (isPromise(primitiveReportedValue)) {
            handlePromise(primitiveReportedValue, sequencer, thread, opCached, lastOperation);

            // Store the already reported values. They will be thawed into the
            // future versions of the same operations by block id. The reporting
            // operation if it is promise waiting will set its parent value at
            // that time.
            thread.justReported = null;
            currentStackFrame.reporting = ops[i].id;
            currentStackFrame.reported = ops.slice(0, i).map(reportedCached => {
                const inputName = reportedCached._parentKey;
                const reportedValues = reportedCached._parentValues;

                if (inputName === 'BROADCAST_INPUT') {
                    return {
                        opCached: reportedCached.id,
                        inputValue: reportedValues[inputName].BROADCAST_OPTION.name
                    };
                }
                if (inputName === 'IMAGE_INPUT') {
                    return {
                        opCached: reportedCached.id,
                        inputValue: reportedValues[inputName].IMAGE_OPTION.name
                    };
                }
                return {
                    opCached: reportedCached.id,
                    inputValue: reportedValues[inputName]
                };
            });

            // We are waiting for a promise. Stop running this set of operations
            // and continue them later after thawing the reported values.
            break;
        } else if (thread.status === Thread.STATUS_RUNNING) {
            if (lastOperation) {
                handleReport(primitiveReportedValue, sequencer, thread, opCached, lastOperation);
            } else {
                // By definition a block that is not last in the list has a
                // parent.
                const inputName = opCached._parentKey;
                const parentValues = opCached._parentValues;

                if (inputName === 'BROADCAST_INPUT') {
                    // Something is plugged into the broadcast input.
                    // Cast it to a string. We don't need an id here.
                    parentValues.BROADCAST_OPTION.id = null;
                    parentValues.BROADCAST_OPTION.name = cast.toString(primitiveReportedValue);
                } else if (inputName === 'IMAGE_INPUT') {
                    // Something is plugged into the broadcast input.
                    // Cast it to a string. We don't need an id here.
                    parentValues.IMAGE_OPTION.id = null;
                    parentValues.IMAGE_OPTION.name = cast.toString(primitiveReportedValue);
                }else {
                    parentValues[inputName] = primitiveReportedValue;
                }
            }
        }
    }
};

module.exports = execute;
