const dispatch = require('../dispatch/central-dispatch');
const log = require('../util/log');
const maybeFormatMessage = require('../util/maybe-format-message');
const getExtensionURLOfThisVersion = require('./previous-extensions-support').getExtensionURLOfThisVersion;
const BlockType = require('./block-type');

// These extensions are currently built into the VM repository but should not be loaded at startup.
// TODO: move these out into a separate repository?
// TODO: change extension spec so that library info, including extension ID, can be collected through static methods

const builtinExtensions = {
    // This is an example that isn't loaded with the other core blocks,
    // but serves as a reference for loading core blocks as extensions.
    coreExample: () => require('../blocks/scratch3_core_example'),
    // These are the non-core built-in extensions.
    // pen: () => require('../extensions/scratch3_pen'),
    // wedo2: () => require('../extensions/scratch3_wedo2'),
    // music: () => require('../extensions/scratch3_music'),
    // microbit: () => require('../extensions/scratch3_microbit'),
    // text2speech: () => require('../extensions/scratch3_text2speech'),
    // translate: () => require('../extensions/scratch3_translate'),
    // videoSensing: () => require('../extensions/scratch3_video_sensing'),
    // ev3: () => require('../extensions/scratch3_ev3'),
    // makeymakey: () => require('../extensions/scratch3_makeymakey'),
    // boost: () => require('../extensions/scratch3_boost'),
    // gdxfor: () => require('../extensions/scratch3_gdx_for'),
    // evive
    // evive: () => require('../extensions/evive/evive'),
    // eviveDisplay: () => require('../extensions/evive/evive_display'),

    // Arduino Mega
    // arduinoMega: () => require('../extensions/arduinoMega/arduino_mega'),

    // Arduino Uno
    // arduinoUno: () => require('../extensions/arduinoUno/arduino_uno'),

    // Arduino Nano
    // arduinoNano: () => require('../extensions/arduinoNano/arduino_nano'),

    // Tecbits
    // tecBits: () => require('../extensions/tecBits/tecbits'),

    // esp32
    // esp32: () => require('../extensions/esp32/esp32'),

    // quon
    // quon: () => require('../extensions/quon/quon'),
    // quonSensors: () => require('../extensions/quon/sensors'),
    // quonDabble: () => require('../extensions/quon/dabble'),
    // quonDisplay: () => require('../extensions/quon/display'),
    // quonRobot: () => require('../extensions/quon/robot'),
    // quonLightning: () => require('../extensions/quon/lightning'),
    // quonIot: () => require('../extensions/quon/iot'),

    // t_watch
    // tWatch: () => require('../extensions/t-watch/t-watch'),
    // tWatchDisplay: () => require('../extensions/t-watch/display'),

    //quarky
    // quarky: () => require('../extensions/quarky/quarky'),
    // quarkyDisplay: () => require('../extensions/quarky/display'),
    // quarkySensors: () => require('../extensions/quarky/quarkySensors'),
    // quarkyRobot: () => require('../extensions/quarky/quarkyRobot'),
    // quarkySpeaker: () => require('../extensions/quarky/speaker'),
    // quarkyDebug: () => require('../extensions/quarky/quarkyDebug'),
    // quarkyMarsRoverRobot: () => require('../extensions/quarky/quarkyMarsRoverRobot'),

    // imageOverlay: () => require('../extensions/image-overlay'),

    // Common Extensions
    // aiServices: () => require('../extensions/common-extension-blocks/ai-services'),
    // actuators: () => require('../extensions/common-extension-blocks/actuators'),
    // sensors: () => require('../extensions/common-extension-blocks/sensors'),
    // lightning: () => require('../extensions/common-extension-blocks/lightning'),
    // communication: () => require('../extensions/common-extension-blocks/communication'),
    // dabble: () => require('../extensions/common-extension-blocks/dabble'),
    // displayModule: () => require('../extensions/common-extension-blocks/displayModule'),
    // advanceSensor: () => require('../extensions/common-extension-blocks/advanceSensor'),
    // humanoidRobot: () => require('../extensions/common-extension-blocks/humanoidRobot'),
    // roboticArm: () => require('../extensions/common-extension-blocks/roboticArm'),
    // stepperMotor: () => require('../extensions/common-extension-blocks/stepperMotor'),
    // neuralNetwork: () => require('../extensions/common-extension-blocks/neuralNetwork'),
    // csvFormatter: () => require('../extensions/common-extension-blocks/csvFormatter'),
    // iot: () => require('../extensions/common-extension-blocks/iot'),
    // weatherIoT: () => require('../extensions/common-extension-blocks/weatherIoT'),
    // teachableMachine: () => require('../extensions/common-extension-blocks/teachable-machine'),
    // posenet: () => require('../extensions/common-extension-blocks/posenet'),
    // objectDetection: () => require('../extensions/common-extension-blocks/objectDetection'),
    // faceDetection: () => require('../extensions/common-extension-blocks/faceDetection'),
    // naturalLanguageProcessing: () => require('../extensions/common-extension-blocks/naturalLanguageProcessing'),
    // qrCodeScanner: () => require('../extensions/common-extension-blocks/qrCodeScanner'),
    // customObjectDetection: () => require('../extensions/common-extension-blocks/customObjectDetection'),
    // physicsEngine: () => require('../extensions/common-extension-blocks/physicsEngine'),
    // mobileSensor: () => require('../extensions/common-extension-blocks/mobileSensor')
    
};


/**
 * @typedef {object} ArgumentInfo - Information about an extension block argument
 * @property {ArgumentType} type - the type of value this argument can take
 * @property {*|undefined} default - the default value of this argument (default: blank)
 */

/**
 * @typedef {object} ConvertedBlockInfo - Raw extension block data paired with processed data ready for scratch-blocks
 * @property {ExtensionBlockMetadata} info - the raw block info
 * @property {object} json - the scratch-blocks JSON definition for this block
 * @property {string} xml - the scratch-blocks XML definition for this block
 */

/**
 * @typedef {object} CategoryInfo - Information about a block category
 * @property {string} id - the unique ID of this category
 * @property {string} name - the human-readable name of this category
 * @property {string|undefined} blockIconURI - optional URI for the block icon image
 * @property {string} color1 - the primary color for this category, in '#rrggbb' format
 * @property {string} color2 - the secondary color for this category, in '#rrggbb' format
 * @property {string} color3 - the tertiary color for this category, in '#rrggbb' format
 * @property {Array.<ConvertedBlockInfo>} blocks - the blocks, separators, etc. in this category
 * @property {Array.<object>} menus - the menus provided by this category
 */

/**
 * @typedef {object} PendingExtensionWorker - Information about an extension worker still initializing
 * @property {string} extensionURL - the URL of the extension to be loaded by this worker
 * @property {Function} resolve - function to call on successful worker startup
 * @property {Function} reject - function to call on failed worker startup
 */

class ExtensionManager {
    constructor(runtime) {
        /**
         * The ID number to provide to the next extension worker.
         * @type {int}
         */
        this.nextExtensionWorker = 0;

        /**
         * FIFO queue of extensions which have been requested but not yet loaded in a worker,
         * along with promise resolution functions to call once the worker is ready or failed.
         *
         * @type {Array.<PendingExtensionWorker>}
         */
        this.pendingExtensions = [];

        /**
         * Map of worker ID to workers which have been allocated but have not yet finished initialization.
         * @type {Array.<PendingExtensionWorker>}
         */
        this.pendingWorkers = [];

        /**
         * Set of loaded extension URLs/IDs (equivalent for built-in extensions).
         * @type {Set.<string>}
         * @private
         */
        this._loadedExtensions = new Map();

        /**
         * Keep a reference to the runtime so we can construct internal extension objects.
         * TODO: remove this in favor of extensions accessing the runtime as a service.
         * @type {Runtime}
         */
        this.runtime = runtime;

        /**
         * Extension name which are loaded in background for this course of selected board
         */
        this.extensionsLoadedInBackground = [];

        dispatch.setService('extensions', this).catch(e => {
            log.error(`ExtensionManager was unable to register extension service: ${JSON.stringify(e)}`);
        });
    }

    /**
     * Check whether an extension is registered or is in the process of loading. This is intended to control loading or
     * adding extensions so it may return `true` before the extension is ready to be used. Use the promise returned by
     * `loadExtensionURL` if you need to wait until the extension is truly ready.
     * @param {string} extensionID - the ID of the extension.
     * @returns {boolean} - true if loaded, false otherwise.
     */
    isExtensionLoaded(extensionID) {
        return this._loadedExtensions.has(extensionID);
    }

    /**
     * Synchronously load an internal extension (core or non-core) by ID. This call will
     * fail if the provided id is not does not match an internal extension.
     * @param {string} extensionId - the ID of an internal extension
     */
    loadExtensionIdSync(extensionId) {
        if (!builtinExtensions.hasOwnProperty(extensionId)) {
            log.warn(`Could not find extension ${extensionId} in the built in extensions.`);
            return;
        }

        /** @TODO dupe handling for non-builtin extensions. See commit 670e51d33580e8a2e852b3b038bb3afc282f81b9 */
        if (this.isExtensionLoaded(extensionId)) {
            const message = `Rejecting attempt to load a second extension with ID ${extensionId}`;
            log.warn(message);
            return;
        }

        const extension = builtinExtensions[extensionId]();
        const extensionInstance = new extension(this.runtime);
        const serviceName = this._registerInternalExtension(extensionInstance);
        this._loadedExtensions.set(extensionId, serviceName);
    }

    /**
     * Load an extension by URL or internal extension ID
     * @param {string} extensionURL - the URL for the extension to load OR the ID of an internal extension
     * @param {object} extensionInternalData - extension object
     * @returns {Promise} resolved once the extension is loaded and initialized or rejected on failure
     */
    loadExtensionURL(extensionURL, isBackgroundMenu, extensionInternalData) {
        let extensionData = getExtensionURLOfThisVersion(extensionURL);
        extensionURL = extensionData.extension;
        if (extensionData.isCommonExtension == true) {
            this.runtime.addExtensionIdToLoadedExtension(extensionURL);
        }
        if (typeof isBackgroundMenu === 'undefined' && this.isExtensionFromOtherBoard(extensionURL)) {
            isBackgroundMenu = true;
        }
        if (builtinExtensions.hasOwnProperty(extensionURL)) {
            /** @TODO dupe handling for non-builtin extensions. See commit 670e51d33580e8a2e852b3b038bb3afc282f81b9 */
            if (!extensionInternalData && this.isExtensionLoaded(extensionURL) && (typeof extensionData.isCommonExtension === 'undefined' || !extensionData.isCommonExtension)) {
                // Todo
                // This needs to be changes #changedExtensionFunctionality
                // Changed scratch usual behaviour.
                const serviceName = this._loadedExtensions.get(extensionURL);
                return dispatch.call('extensions', 'registerExtensionService', serviceName, isBackgroundMenu);
                // const message = `Rejecting attempt to load a second extension with ID ${extensionURL}`;
                // log.warn(message);
                // return Promise.reject(new Error(message));
            }

            const extension = builtinExtensions[extensionURL]();
            let extensionInstance = null;
            let serviceName = null;

            // Check if the extension has the data
            // if (extensionInternalData && extensionInternalData[extensionURL]) {
            //     if (extensionURL == "teachableMachine") {
            //         // teachableMachine - tachableMachine extension model object
            //         const { teachableMachine } = extensionInternalData;
            //         if (teachableMachine.model instanceof Promise) {
            //             if (teachableMachine.type === 'image') {
            //                 return Promise.resolve(teachableMachine.model).then(model => {
            //                     extensionInstance = new extension(this.runtime, model, teachableMachine.url, teachableMachine.type);
            //                     extensionInstance.MODEL_CLASSES = model.getClassLabels();
            //                     serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                     this._loadedExtensions.set(extensionURL, serviceName);
            //                 }).catch(err => {
            //                 }).finally(() => {
            //                     return Promise.resolve();
            //                 })
            //             } else if (teachableMachine.type === 'audio') {
            //                 return Promise.resolve(teachableMachine.model).then(() => {
            //                     extensionInstance = new extension(this.runtime, teachableMachine.recognizer, teachableMachine.url, teachableMachine.type);
            //                     extensionInstance.MODEL_CLASSES = teachableMachine.recognizer.wordLabels();
            //                     serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                     this._loadedExtensions.set(extensionURL, serviceName);
            //                 }).catch(err => {
            //                     console.log(err)
            //                 }).finally(() => {
            //                     return Promise.resolve();
            //                 })
            //             } else {
            //                 return Promise.resolve(teachableMachine.model).then(model => {
            //                     extensionInstance = new extension(this.runtime, model, teachableMachine.url, teachableMachine.type);
            //                     extensionInstance.MODEL_CLASSES = model.getMetadata().labels;
            //                     serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                     this._loadedExtensions.set(extensionURL, serviceName);
            //                 }).catch(err => {
            //                 }).finally(() => {
            //                     return Promise.resolve();
            //                 })
            //             }
            //         } else {
            //             if (teachableMachine.type === 'image') {
            //                 extensionInstance = new extension(this.runtime, teachableMachine.model, teachableMachine.url, teachableMachine.type);
            //                 extensionInstance.MODEL_CLASSES = teachableMachine.model.getClassLabels();
            //                 serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                 this._loadedExtensions.set(extensionURL, serviceName);
            //                 return Promise.resolve();
            //             } else if (teachableMachine.type === 'audio') {
            //                 extensionInstance = new extension(this.runtime, teachableMachine.model, teachableMachine.url, teachableMachine.type);
            //                 extensionInstance.MODEL_CLASSES = teachableMachine.model.wordLabels();
            //                 serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                 this._loadedExtensions.set(extensionURL, serviceName);
            //                 return Promise.resolve();
            //             }
            //             else {
            //                 extensionInstance = new extension(this.runtime, teachableMachine.model, teachableMachine.url, teachableMachine.type);
            //                 extensionInstance.MODEL_CLASSES = teachableMachine.model.getMetadata().labels;
            //                 serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //                 this._loadedExtensions.set(extensionURL, serviceName);
            //                 return Promise.resolve();
            //             }
            //         }
            //     } else if (extensionURL == "faceDetection") {
            //         const { faceDetection } = extensionInternalData;
            //         extensionInstance = new extension(this.runtime,
            //             typeof faceDetection.faceComparision !== 'undefined' &&
            //                 faceDetection.faceComparision.length > 0 ? faceDetection.faceComparision : []);
            //         serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
            //         this._loadedExtensions.set(extensionURL, serviceName);
            //         return Promise.resolve();
            //     }
            // } else {
                extensionInstance = new extension(this.runtime);
                serviceName = this._registerInternalExtension(extensionInstance, isBackgroundMenu);
                this._loadedExtensions.set(extensionURL, serviceName);
                return Promise.resolve();
            // }
        }


        // To check the reason of the error being occurred, just uncomment the following line.
        // console.log("extensionURL", extensionURL);
        this.runtime.emitProjectVersionError();
        return new Promise((resolve, reject) => {
            // If we `require` this at the global level it breaks non-webpack targets, including tests
            const ExtensionWorker = require('./worker-loader-extension-worker.js');

            this.pendingExtensions.push({ extensionURL, resolve, reject });
            dispatch.addWorker(new ExtensionWorker());
        });

    }

    loadHardwareExtensions(board) {
        // console.log("loadHardwareExtensions", board);
        // dispatch.removeService([...this._loadedExtensions.values()]);
        // this._loadedExtensions.clear();
        // Backup blockInfo to use if workspace gets refreshed.
        this.runtime._oldBlockInfo = this.runtime._blockInfo;
        this.runtime._blockInfo = [];
        this.runtime.defaultHardwareExtensions[board].forEach(extensionURL => {
            if (this.runtime.isExtensionForBackground(board, extensionURL)) {
                this.loadExtensionURL(extensionURL, true);
                // this.extensionsLoadedInBackground.push(extensionURL);
            } else {
                this.loadExtensionURL(extensionURL, false);
            }
        });
        // Load extension previously loaded from extension library.
        if (this.runtime.commonExtensionLoadedFromExtensionLibrary.length > 0) {
            this.runtime.commonExtensionLoadedFromExtensionLibrary.map(extensionURL => {
                if (this.runtime.commonExtensionAmongBoards.hasOwnProperty(board) && this.runtime.commonExtensionAmongBoards[board].includes(extensionURL) || this.runtime.defaultExtensions.includes(extensionURL))
                    this.loadExtensionURL(extensionURL, false);
            });
        }
    }

    /**
     * Checks if the extensionURL belongs to the given board or not.
     * @param {*} extensionURL the URL for the extension to load OR the ID of an internal extension.
     * @returns {Boolean} true if the extensionURL is in the list of the board extension.
     */
    isExtensionFromOtherBoard(boardExtensionURL) {
        let board = this.runtime.boardSelected;
        if (!board || this.runtime.commonHardwareExtensions.includes(boardExtensionURL)) return false;
        this.runtime.defaultHardwareExtensions[board].forEach(extensionURL => {
            if (boardExtensionURL === extensionURL) {
                return false;
            }
        });
        if (this.runtime.backgorundHardwareExtensions.hasOwnProperty(board)) {
            this.runtime.backgorundHardwareExtensions[board].forEach(extensionURL => {
                if (boardExtensionURL === extensionURL) {
                    return true;
                }
            });
        }
        return false;
    }

    /**
     * Regenerate blockinfo for any loaded extensions
     * @returns {Promise} resolved once all the extensions have been reinitialized
     */
    refreshBlocks() {
        const allPromises = Array.from(this._loadedExtensions.values()).map(serviceName =>
            dispatch.call(serviceName, 'getInfo')
                .then(info => {
                    info = this._prepareExtensionInfo(serviceName, info);
                    dispatch.call('runtime', '_refreshExtensionPrimitives', info);
                })
                .catch(e => {
                    log.error(`Failed to refresh built-in extension primitives: ${JSON.stringify(e)}`);
                })
        );
        return Promise.all(allPromises);
    }

    allocateWorker() {
        const id = this.nextExtensionWorker++;
        const workerInfo = this.pendingExtensions.shift();
        this.pendingWorkers[id] = workerInfo;
        return [id, workerInfo.extensionURL];
    }

    /**
     * Synchronously collect extension metadata from the specified service and begin the extension registration process.
     * @param {string} serviceName - the name of the service hosting the extension.
     */
    registerExtensionServiceSync(serviceName, isBackgroundMenu) {
        const info = dispatch.callSync(serviceName, 'getInfo');
        info.isBackgroundMenu = isBackgroundMenu;
        this._registerExtensionInfo(serviceName, info);
    }

    /**
     * Collect extension metadata from the specified service and begin the extension registration process.
     * @param {string} serviceName - the name of the service hosting the extension.
     */
    registerExtensionService(serviceName, isBackgroundMenu) {
        dispatch.call(serviceName, 'getInfo').then(info => {
            info.isBackgroundMenu = isBackgroundMenu;
            this._registerExtensionInfo(serviceName, info);
        });
    }

    /**
     * Called by an extension worker to indicate that the worker has finished initialization.
     * @param {int} id - the worker ID.
     * @param {*?} e - the error encountered during initialization, if any.
     */
    onWorkerInit(id, e) {
        const workerInfo = this.pendingWorkers[id];
        delete this.pendingWorkers[id];
        if (e) {
            workerInfo.reject(e);
        } else {
            workerInfo.resolve(id);
        }
    }

    /**
     * Register an internal (non-Worker) extension object
     * @param {object} extensionObject - the extension object to register
     * @returns {Promise} resolved once the extension is fully registered or rejected on failure
     */
    _registerInternalExtension(extensionObject, isBackgroundMenu) {
        const extensionInfo = extensionObject.getInfo();
        const fakeWorkerId = this.nextExtensionWorker++;
        const serviceName = `extension_${fakeWorkerId}_${extensionInfo.id}`;
        dispatch.setServiceSync(serviceName, extensionObject);
        dispatch.callSync('extensions', 'registerExtensionServiceSync', serviceName, isBackgroundMenu);
        return serviceName;
    }

    /**
     * Sanitize extension info then register its primitives with the VM.
     * @param {string} serviceName - the name of the service hosting the extension
     * @param {ExtensionInfo} extensionInfo - the extension's metadata
     * @private
     */
    _registerExtensionInfo(serviceName, extensionInfo) {
        extensionInfo = this._prepareExtensionInfo(serviceName, extensionInfo);
        dispatch.call('runtime', '_registerExtensionPrimitives', extensionInfo).catch(e => {
            log.error(`Failed to register primitives for extension on service ${serviceName}:`, e);
        });
    }

    /**
     * Modify the provided text as necessary to ensure that it may be used as an attribute value in valid XML.
     * @param {string} text - the text to be sanitized
     * @returns {string} - the sanitized text
     * @private
     */
    _sanitizeID(text) {
        return text.toString().replace(/[<"&]/, '_');
    }

    /**
     * Apply minor cleanup and defaults for optional extension fields.
     * TODO: make the ID unique in cases where two copies of the same extension are loaded.
     * @param {string} serviceName - the name of the service hosting this extension block
     * @param {ExtensionInfo} extensionInfo - the extension info to be sanitized
     * @returns {ExtensionInfo} - a new extension info object with cleaned-up values
     * @private
     */
    _prepareExtensionInfo(serviceName, extensionInfo) {
        extensionInfo = Object.assign({}, extensionInfo);
        if (!/^[a-z0-9]+$/i.test(extensionInfo.id)) {
            throw new Error('Invalid extension id');
        }
        extensionInfo.name = extensionInfo.name || extensionInfo.id;
        extensionInfo.blocks = extensionInfo.blocks || [];
        extensionInfo.targetTypes = extensionInfo.targetTypes || [];
        extensionInfo.blocks = extensionInfo.blocks.reduce((results, blockInfo) => {
            try {
                let result;
                if (blockInfo === '---') {
                    result = '---';
                } else if (blockInfo.message) {
                    result = blockInfo;
                } else {
                    result = this._prepareBlockInfo(serviceName, blockInfo);
                }
                // switch (blockInfo) {
                // case '---': // separator
                //     result = '---';
                //     break;
                // default: // an ExtensionBlockMetadata object
                //     result = this._prepareBlockInfo(serviceName, blockInfo);
                //     break;
                // }
                results.push(result);
            } catch (e) {
                // TODO: more meaningful error reporting
                log.error(`Error processing block: ${e.message}, Block:\n${JSON.stringify(blockInfo)}`);
            }
            return results;
        }, []);
        extensionInfo.menus = extensionInfo.menus || {};
        extensionInfo.menus = this._prepareMenuInfo(serviceName, extensionInfo.menus);
        return extensionInfo;
    }

    /**
     * Prepare extension menus. e.g. setup binding for dynamic menu functions.
     * @param {string} serviceName - the name of the service hosting this extension block
     * @param {Array.<MenuInfo>} menus - the menu defined by the extension.
     * @returns {Array.<MenuInfo>} - a menuInfo object with all preprocessing done.
     * @private
     */
    _prepareMenuInfo(serviceName, menus) {
        const menuNames = Object.getOwnPropertyNames(menus);
        for (let i = 0; i < menuNames.length; i++) {
            const menuName = menuNames[i];
            let menuInfo = menus[menuName];

            // If the menu description is in short form (items only) then normalize it to general form: an object with
            // its items listed in an `items` property.
            if (!menuInfo.items) {
                menuInfo = {
                    items: menuInfo
                };
                menus[menuName] = menuInfo;
            }
            // If `items` is a string, it should be the name of a function in the extension object. Calling the
            // function should return an array of items to populate the menu when it is opened.
            if (typeof menuInfo.items === 'string') {
                const menuItemFunctionName = menuInfo.items;
                const serviceObject = dispatch.services[serviceName];
                // Bind the function here so we can pass a simple item generation function to Scratch Blocks later.
                menuInfo.items = this._getExtensionMenuItems.bind(this, serviceObject, menuItemFunctionName);
            }
        }
        return menus;
    }

    /**
     * Fetch the items for a particular extension menu, providing the target ID for context.
     * @param {object} extensionObject - the extension object providing the menu.
     * @param {string} menuItemFunctionName - the name of the menu function to call.
     * @returns {Array} menu items ready for scratch-blocks.
     * @private
     */
    _getExtensionMenuItems(extensionObject, menuItemFunctionName) {
        // Fetch the items appropriate for the target currently being edited. This assumes that menus only
        // collect items when opened by the user while editing a particular target.
        const editingTarget = this.runtime.getEditingTarget() || this.runtime.getTargetForStage();
        const editingTargetID = editingTarget ? editingTarget.id : null;
        const extensionMessageContext = this.runtime.makeMessageContextForTarget(editingTarget);

        // TODO: Fix this to use dispatch.call when extensions are running in workers.
        const menuFunc = extensionObject[menuItemFunctionName];
        const menuItems = menuFunc.call(extensionObject, editingTargetID).map(
            item => {
                item = maybeFormatMessage(item, extensionMessageContext);
                switch (typeof item) {
                    case 'object':
                        return [
                            maybeFormatMessage(item.text, extensionMessageContext),
                            item.value
                        ];
                    case 'string':
                        return [item, item];
                    default:
                        return item;
                }
            });

        if (!menuItems || menuItems.length < 1) {
            throw new Error(`Extension menu returned no items: ${menuItemFunctionName}`);
        }
        return menuItems;
    }

    /**
     * Apply defaults for optional block fields.
     * @param {string} serviceName - the name of the service hosting this extension block
     * @param {ExtensionBlockMetadata} blockInfo - the block info from the extension
     * @returns {ExtensionBlockMetadata} - a new block info object which has values for all relevant optional fields.
     * @private
     */
    _prepareBlockInfo(serviceName, blockInfo) {
        blockInfo = Object.assign({}, {
            blockType: BlockType.COMMAND,
            terminal: false,
            blockAllThreads: false,
            arguments: {}
        }, blockInfo);
        blockInfo.opcode = blockInfo.opcode && this._sanitizeID(blockInfo.opcode);
        blockInfo.text = blockInfo.text || blockInfo.opcode;

        switch (blockInfo.blockType) {
            case BlockType.EVENT:
                if (blockInfo.func) {
                    log.warn(`Ignoring function "${blockInfo.func}" for event block ${blockInfo.opcode}`);
                }
                break;
            case BlockType.BUTTON:
                if (blockInfo.opcode) {
                    log.warn(`Ignoring opcode "${blockInfo.opcode}" for button with text: ${blockInfo.text}`);
                }
                break;
            default: {
                if (!blockInfo.opcode) {
                    throw new Error('Missing opcode for block');
                }

                const funcName = blockInfo.func ? this._sanitizeID(blockInfo.func) : blockInfo.opcode;

                const getBlockInfo = blockInfo.isDynamic ?
                    args => args && args.mutation && args.mutation.blockInfo :
                    () => blockInfo;
                const callBlockFunc = (() => {
                    if (dispatch._isRemoteService(serviceName)) {
                        return (args, util, realBlockInfo) =>
                            dispatch.call(serviceName, funcName, args, util, realBlockInfo);
                    }

                    // avoid promise latency if we can call direct
                    const serviceObject = dispatch.services[serviceName];
                    if (!serviceObject[funcName]) {
                        // The function might show up later as a dynamic property of the service object
                        log.warn(`Could not find extension block function called ${funcName}`);
                    }
                    return (args, util, realBlockInfo) =>
                        serviceObject[funcName](args, util, realBlockInfo);
                })();

                blockInfo.func = (args, util) => {
                    const realBlockInfo = getBlockInfo(args);
                    // TODO: filter args using the keys of realBlockInfo.arguments? maybe only if sandboxed?
                    return callBlockFunc(args, util, realBlockInfo);
                };
                break;
            }
        }

        return blockInfo;
    }
}

module.exports = ExtensionManager;
