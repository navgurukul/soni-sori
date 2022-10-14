let _TextEncoder;
if (typeof TextEncoder === 'undefined') {
    _TextEncoder = require( "../local_modules/text-encoding/index.js").TextEncoder;
} else {
    /* global TextEncoder */
    _TextEncoder = TextEncoder;
}
const EventEmitter = require( "../../../local_modules/events/events.js");
const JSZip = require( "../local_modules/jszip/lib/index.js");

const Buffer = require( "../../../local_modules/node-libs-browser/local_modules/buffer/index.js").Buffer;
const centralDispatch = require('./dispatch/central-dispatch');
const ExtensionManager = require('./extension-support/extension-manager');
const log = require('./util/log');
const MathUtil = require('./util/math-util');
const Runtime = require('./engine/runtime');
const StringUtil = require('./util/string-util');
const formatMessage = require( "../local_modules/format-message/index.js");

const Variable = require('./engine/variable');
const newBlockIds = require('./util/new-block-ids');

const { loadCostume } = require('./import/load-costume.js');
const { loadSound } = require('./import/load-sound.js');
const { serializeSounds, serializeCostumes } = require('./serialization/serialize-assets');
require( "../local_modules/canvas-toBlob/canvas-toBlob.js");

const RESERVED_NAMES = ['_mouse_', '_stage_', '_edge_', '_myself_', '_random_'];

const CORE_EXTENSIONS = [
    // 'motion',
    // 'looks',
    // 'sound',
    // 'events',
    // 'control',
    // 'sensing',
    // 'operators',
    // 'variables',
    // 'myBlocks'
];

// const tmImage = require('@teachablemachine/image');
// const speechCommands = require('@tensorflow-models/speech-commands');
// const tmPose = require('@teachablemachine/pose')

/**
 * Handles connections between blocks, stage, and extensions.
 * @constructor
 */
class VirtualMachine extends EventEmitter {
    constructor() {
        super();

        /**
         * VM runtime, to store blocks, I/O devices, sprites/targets, etc.
         * @type {!Runtime}
         */
        this.runtime = new Runtime();
        centralDispatch.setService('runtime', this.runtime).catch(e => {
            log.error(`Failed to register runtime service: ${JSON.stringify(e)}`);
        });

        /**
         * The "currently editing"/selected target ID for the VM.
         * Block events from any Blockly workspace are routed to this target.
         * @type {Target}
         */
        this.editingTarget = null;

        /**
         * The currently dragging target, for redirecting IO data.
         * @type {Target}
         */
        this._dragTarget = null;

        // Runtime emits are passed along as VM emits.
        this.runtime.on(Runtime.SCRIPT_GLOW_ON, glowData => {
            this.emit(Runtime.SCRIPT_GLOW_ON, glowData);
        });
        this.runtime.on(Runtime.SCRIPT_GLOW_OFF, glowData => {
            this.emit(Runtime.SCRIPT_GLOW_OFF, glowData);
        });
        this.runtime.on(Runtime.BLOCK_GLOW_ON, glowData => {
            this.emit(Runtime.BLOCK_GLOW_ON, glowData);
        });
        this.runtime.on(Runtime.BLOCK_GLOW_OFF, glowData => {
            this.emit(Runtime.BLOCK_GLOW_OFF, glowData);
        });
        this.runtime.on(Runtime.PROJECT_START, () => {
            this.emit(Runtime.PROJECT_START);
        });
        this.runtime.on(Runtime.PROJECT_RUN_START, () => {
            this.emit(Runtime.PROJECT_RUN_START);
        });
        this.runtime.on(Runtime.PROJECT_RUN_STOP, () => {
            this.emit(Runtime.PROJECT_RUN_STOP);
        });
        this.runtime.on(Runtime.PROJECT_CHANGED, () => {
            this.emit(Runtime.PROJECT_CHANGED);
        });
        this.runtime.on(Runtime.VISUAL_REPORT, visualReport => {
            this.emit(Runtime.VISUAL_REPORT, visualReport);
        });
        this.runtime.on(Runtime.TARGETS_UPDATE, emitProjectChanged => {
            this.emitTargetsUpdate(emitProjectChanged);
        });
        this.runtime.on(Runtime.MONITORS_UPDATE, monitorList => {
            this.emit(Runtime.MONITORS_UPDATE, monitorList);
        });
        this.runtime.on(Runtime.BLOCK_DRAG_UPDATE, (areBlocksOverGui, block) => {
            this.emit(Runtime.BLOCK_DRAG_UPDATE, areBlocksOverGui, block);
        });
        this.runtime.on(Runtime.BLOCK_DRAG_END, (blocks, topBlockId) => {
            this.emit(Runtime.BLOCK_DRAG_END, blocks, topBlockId);
        });
        this.runtime.on(Runtime.EXTENSION_ADDED, categoryInfo => {
            this.emit(Runtime.EXTENSION_ADDED, categoryInfo);
        });
        this.runtime.on(Runtime.EXTENSION_FIELD_ADDED, (fieldName, fieldImplementation) => {
            this.emit(Runtime.EXTENSION_FIELD_ADDED, fieldName, fieldImplementation);
        });
        this.runtime.on(Runtime.BLOCKSINFO_UPDATE, categoryInfo => {
            this.emit(Runtime.BLOCKSINFO_UPDATE, categoryInfo);
        });
        this.runtime.on(Runtime.BLOCKS_NEED_UPDATE, () => {
            this.emitWorkspaceUpdate();
        });
        this.runtime.on(Runtime.TOOLBOX_EXTENSIONS_NEED_UPDATE, () => {
            this.extensionManager.refreshBlocks();
        });
        this.runtime.on(Runtime.PERIPHERAL_LIST_UPDATE, info => {
            this.emit(Runtime.PERIPHERAL_LIST_UPDATE, info);
        });
        this.runtime.on(Runtime.PERIPHERAL_CONNECTED, () =>
            this.emit(Runtime.PERIPHERAL_CONNECTED)
        );
        this.runtime.on(Runtime.PERIPHERAL_REQUEST_ERROR, () =>
            this.emit(Runtime.PERIPHERAL_REQUEST_ERROR)
        );
        this.runtime.on(Runtime.PERIPHERAL_DISCONNECTED, () =>
            this.emit(Runtime.PERIPHERAL_DISCONNECTED)
        );
        this.runtime.on(Runtime.PERIPHERAL_CONNECTION_LOST_ERROR, data =>
            this.emit(Runtime.PERIPHERAL_CONNECTION_LOST_ERROR, data)
        );
        this.runtime.on(Runtime.PERIPHERAL_SCAN_TIMEOUT, () =>
            this.emit(Runtime.PERIPHERAL_SCAN_TIMEOUT)
        );
        this.runtime.on(Runtime.MIC_LISTENING, listening => {
            this.emit(Runtime.MIC_LISTENING, listening);
        });
        this.runtime.on(Runtime.RUNTIME_STARTED, () => {
            this.emit(Runtime.RUNTIME_STARTED);
        });

        this.runtime.on(Runtime.WRITE_TO_PERIPHERAL, data => {
            this.emit(Runtime.WRITE_TO_PERIPHERAL, data);
        });

        this.runtime.on('redoUndo', flag => {
            this.emit('redoUndo', flag);
        });

        this.runtime.on('zoomInOut', flag => {
            this.emit('zoomInOut', flag);
        });

        this.runtime.on(Runtime.HAS_CLOUD_DATA_UPDATE, hasCloudData => {
            this.emit(Runtime.HAS_CLOUD_DATA_UPDATE, hasCloudData);
        });
        this.runtime.on(Runtime.MOBILE_PLATFORM_CALL, (funcName, ...args) => {
            this.emit(Runtime.MOBILE_PLATFORM_CALL, funcName, ...args);
        });
        this.runtime.on(Runtime.FIELD_IMAGE_SELECT, field => {
            this.emit(Runtime.FIELD_IMAGE_SELECT, field);
        });
        this.runtime.on(Runtime.RUNTIME_ERROR, (notUsableBlockList) => {
            this.emit(Runtime.RUNTIME_ERROR, notUsableBlockList);
        });
        this.runtime.on(Runtime.BOARD_UPDATED, (selectedBoard, shouldShowCheckPinsWarning) => {
            this.emit(Runtime.BOARD_UPDATED, selectedBoard, shouldShowCheckPinsWarning);
        });
        this.runtime.on(Runtime.PROJECT_VERSION_ERROR, () => {
            this.emit(Runtime.PROJECT_VERSION_ERROR);
        });
        this.runtime.on(Runtime.PROJECT_MODE_CHANGED, () => {
            this.emit(Runtime.PROJECT_MODE_CHANGED)
        });
        this.runtime.on(Runtime.CAPTURE_AUDIO, timeOut => {
            this.emit(Runtime.CAPTURE_AUDIO, timeOut);
        });
        this.runtime.on(Runtime.CAPTURE_IMAGE, timeOut => {
            this.emit(Runtime.CAPTURE_IMAGE, timeOut);
        });
        this.runtime.on(Runtime.INTERNET_CONNECTION_WARNING, () => {
            this.emit(Runtime.INTERNET_CONNECTION_WARNING);
        });
        this.runtime.on(Runtime.REQUEST_TIMED_OUT, () => {
            this.emit(Runtime.REQUEST_TIMED_OUT)
        });
        this.runtime.on(Runtime.DISCONNECT_PORTS, () => {
            this.emit(Runtime.DISCONNECT_PORTS)
        });
        this.runtime.on('SEND_ANALYTICS', event => {
            this.emit('SEND_ANALYTICS', event);
        });
        this.runtime.on('SERIAL_WRITE', (data, shouldBeParsed) => {
            this.emit('SERIAL_WRITE', data, shouldBeParsed);
        });
        this.runtime.on(Runtime.TEACHABLE_MACHINE_PREDICT, (model, modelType) => {
            this.emit(Runtime.TEACHABLE_MACHINE_PREDICT, model, modelType);
        });
        this.runtime.on(Runtime.TEACHABLE_MACHINE_GET_CLASS, (model, modelType, resolve) => {
            this.emit(Runtime.TEACHABLE_MACHINE_GET_CLASS, model, modelType, resolve);
        });
        this.runtime.on(Runtime.SIGN_IN_REQUIRED_ERROR, (signInRequiredBlockList) => {
            this.emit(Runtime.SIGN_IN_REQUIRED_ERROR, signInRequiredBlockList);
        });
        this.runtime.on(Runtime.INSUFFICIENT_CREDITS, () => {
            this.emit(Runtime.INSUFFICIENT_CREDITS);
        });
        this.runtime.on(Runtime.MODEL_LOADING, (modelType) => {
            this.emit(Runtime.MODEL_LOADING, modelType);
        });
        this.runtime.on(Runtime.MODEL_LOADING_FINISHED, (modelLoadedSuccessfully) => {
            this.emit(Runtime.MODEL_LOADING_FINISHED, modelLoadedSuccessfully);
        });
        // this.runtime.on(Runtime.SCROLL_TO_EXTENSION, () => {
        //     this.emit(Runtime.SCROLL_TO_EXTENSION);
        // });

        // this.runtime.on(Runtime.LOAD_EXTENSION_USING_URL, (extensionURL, extensionId) => {
        //     this.emit(Runtime.LOAD_EXTENSION_USING_URL, extensionURL, extensionId);
        // });

        this.runtime.on(Runtime.MODEL_FILES_LOCATED, (modelData) => {
            this.emit(Runtime.MODEL_FILES_LOCATED, modelData);
        });

        this.runtime.on(Runtime.STORE_PROCEDURE_ON, () => {
            this.emit(Runtime.STORE_PROCEDURE_ON);
        });
        
        this.runtime.on(Runtime.STORE_PROCEDURE_OFF, () => {
            this.emit(Runtime.STORE_PROCEDURE_OFF);
        });

        this.runtime.on(Runtime.FIRMWARE_NOT_UPDATED, () => {
            this.emit(Runtime.FIRMWARE_NOT_UPDATED);
        })

        this.extensionManager = new ExtensionManager(this.runtime);

        // Load core extensions
        for (const id of CORE_EXTENSIONS) {
            this.extensionManager.loadExtensionIdSync(id);
        }

        this.blockListener = this.blockListener.bind(this);
        this.flyoutBlockListener = this.flyoutBlockListener.bind(this);
        this.monitorBlockListener = this.monitorBlockListener.bind(this);
        this.variableListener = this.variableListener.bind(this);
    }

    /*
     * Check if board is from the allowed board list for VM Pre Serial Store Procedure
     */
    checkIfBoardAllowedForStoreProcedure(board) {
        return this.runtime.vmPreStoreProcedureAllowedBoards.includes(board);
    }

    loadTMMOdel(url, modelType, callBack) {
        // const modelURL = url + 'model.json';
        // const metadataURL = url + 'metadata.json';

        // if (modelType === "image") {
        //     tmImage.load(modelURL, metadataURL).then(model => {
        //         if (model._metadata.packageName === "@teachablemachine/image") {
        //             this.extensionManager.loadExtensionURL("teachableMachine", undefined, { teachableMachine: { model: model, url: url, type: modelType } }).then(() => {
        //                 this.runtime.addExtensionIdToLoadedExtension("teachableMachine");
        //                 this.extensionManager.refreshBlocks().then(() => {
        //                     this.emit(Runtime.SCROLL_TO_EXTENSION, "teachableMachine");
        //                 });
        //                 callBack(true);
        //             }).catch(err => {
        //                 callBack(false);
        //             });
        //         } else {
        //             callBack(false);
        //         }
        //     })
        //         .catch(err => {
        //             callBack(false);
        //         })
        // } else if (modelType === "audio") {
        //     let recognizer = speechCommands.create("BROWSER_FFT", undefined, modelURL, metadataURL);
        //     recognizer.ensureModelLoaded().then(() => {
        //         if (recognizer.wordLabels()) {
        //             this.extensionManager.loadExtensionURL("teachableMachine", undefined, { teachableMachine: { model: recognizer, url: url, type: modelType } }).then(() => {
        //                 this.runtime.addExtensionIdToLoadedExtension("teachableMachine");
        //                 this.extensionManager.refreshBlocks().then(() => {
        //                     this.emit(Runtime.SCROLL_TO_EXTENSION, "teachableMachine");
        //                 });
        //                 callBack(true);
        //             });
        //         } else {
        //             callBack(false);
        //         }
        //     }).catch(err => {
        //         callBack(false);
        //     })
        // } else {
        //     tmPose.load(modelURL, metadataURL).then(model => {
        //         if (model._metadata.packageName === "@teachablemachine/pose") {
        //             this.extensionManager.loadExtensionURL("teachableMachine", undefined, { teachableMachine: { model: model, url: url, type: modelType } }).then(() => {
        //                 this.runtime.addExtensionIdToLoadedExtension("teachableMachine");
        //                 this.extensionManager.refreshBlocks().then(() => {
        //                     this.emit(Runtime.SCROLL_TO_EXTENSION, "teachableMachine");
        //                 });
        //                 callBack(true);
        //             }).catch(err => {
        //             })
        //         } else {
        //             callBack(false);
        //         }
        //     }).catch(err => {
        //         callBack(false);
        //     });
        // }
    }

    refreshTMModel() {
        // this.loadTMMOdel(this.runtime.tmModelUrl, this.runtime.tmModelType, () => { });
    }

    /**
     * Start running the VM - do this before anything else.
     */
    start() {
        this.runtime.start();
    }

    /**
     * "Green flag" handler - start all threads starting with a green flag.
     */
    greenFlag() {
        this.runtime.greenFlag();
    }

    /**
     * Set whether the VM is in "turbo mode."
     * When true, loops don't yield to redraw.
     * @param {boolean} turboModeOn Whether turbo mode should be set.
     */
    setTurboMode(turboModeOn) {
        this.runtime.turboMode = !!turboModeOn;
        if (this.runtime.turboMode) {
            this.emit(Runtime.TURBO_MODE_ON);
        } else {
            this.emit(Runtime.TURBO_MODE_OFF);
        }
    }

    /**
     * Set whether the VM is in 2.0 "compatibility mode."
     * When true, ticks go at 2.0 speed (30 TPS).
     * @param {boolean} compatibilityModeOn Whether compatibility mode is set.
     */
    setCompatibilityMode(compatibilityModeOn) {
        this.runtime.setCompatibilityMode(!!compatibilityModeOn);
    }

    /**
     * Stop all threads and running activities.
     */
    stopAll() {
        this.runtime.stopAll();
    }

    /**
     * Clear out current running project data.
     */
    clear() {
        this.runtime.dispose();
        this.editingTarget = null;
        this.emitTargetsUpdate(false /* Don't emit project change */);
    }

    /**
     * Get data for playground. Data comes back in an emitted event.
     */
    getPlaygroundData() {
        const instance = this;
        // Only send back thread data for the current editingTarget.
        const threadData = this.runtime.threads.filter(thread => thread.target === instance.editingTarget);
        // Remove the target key, since it's a circular reference.
        const filteredThreadData = JSON.stringify(threadData, (key, value) => {
            if (key === 'target' || key === 'blockContainer') return;
            return value;
        }, 2);
        this.emit('playgroundData', {
            blocks: this.editingTarget.blocks,
            threads: filteredThreadData
        });
    }

    /**
     * Post I/O data to the virtual devices.
     * @param {?string} device Name of virtual I/O device.
     * @param {object} data Any data object to post to the I/O device.
     */
    postIOData(device, data) {
        if (this.runtime.ioDevices[device]) {
            this.runtime.ioDevices[device].postData(data);
        }
    }

    setVideoProvider(videoProvider) {
        this.runtime.ioDevices.video.setProvider(videoProvider);
    }

    setImageOverlayProvider(imageOverlayProvider) {
        this.runtime.imageOverlay.setProvider(imageOverlayProvider);
    }

    setCloudProvider(cloudProvider) {
        this.runtime.ioDevices.cloud.setProvider(cloudProvider);
    }

    /**
     * Tell the specified extension to scan for a peripheral.
     * @param {string} extensionId - the id of the extension.
     */
    scanForPeripheral(extensionId) {
        // this.runtime.scanForPeripheral(extensionId);
    }

    /**
     * Connect to the extension's specified peripheral.
     * @param {string} extensionId - the id of the extension.
     * @param {number} peripheralId - the id of the peripheral.
     */
    connectPeripheral(extensionId, peripheralId) {
        // this.runtime.connectPeripheral(extensionId, peripheralId);
    }

    /**
     * Disconnect from the extension's connected peripheral.
     * @param {string} extensionId - the id of the extension.
     */
    disconnectPeripheral(extensionId) {
        // this.runtime.disconnectPeripheral(extensionId);
    }

    /**
     * Returns whether the extension has a currently connected peripheral.
     * @param {string} extensionId - the id of the extension.
     * @return {boolean} - whether the extension has a connected peripheral.
     */
    getPeripheralIsConnected(extensionId) {
        // return this.runtime.getPeripheralIsConnected(extensionId);
    }

    /**
     * Load a Scratch project from a .sb, .sb2, .sb3 or json string.
     * @param {string | object} input A json string, object, or ArrayBuffer representing the project to load.
     * @return {!Promise} Promise that resolves after targets are installed.
     */
    loadProject(input) {
        if (typeof input === 'object' && !(input instanceof ArrayBuffer) &&
            !ArrayBuffer.isView(input)) {
            // If the input is an object and not any ArrayBuffer
            // or an ArrayBuffer view (this includes all typed arrays and DataViews)
            // turn the object into a JSON string, because we suspect
            // this is a project.json as an object
            // validate expects a string or buffer as input
            // TODO not sure if we need to check that it also isn't a data view
            input = JSON.stringify(input);
        }

        const validationPromise = new Promise((resolve, reject) => {
            var validate = require( "../local_modules/scratch-parser/index.js"); // The second argument of false below indicates to the validator that the
            // The second argument of false below indicates to the validator that the
            // input should be parsed/validated as an entire project (and not a single sprite)
            validate(input, false, (error, res) => {
                console.log("CODE1 error", error);
                if (error) return reject(error);
                resolve(res);
            });
        })
            .catch(error => {
                const { SB1File, ValidationError } = require( "../local_modules/scratch-sb1-converter/index.js");

                try {
                    const sb1 = new SB1File(input);
                    const json = sb1.json;
                    json.projectVersion = 2;
                    return Promise.resolve([json, sb1.zip]);
                } catch (sb1Error) {
                    if (sb1Error instanceof ValidationError) {
                        // The input does not validate as a Scratch 1 file.
                    } else {
                        // The project appears to be a Scratch 1 file but it
                        // could not be successfully translated into a Scratch 2
                        // project.
                        return Promise.reject(sb1Error);
                    }
                }
                // Throw original error since the input does not appear to be
                // an SB1File.
                return Promise.reject(error);
            });

        // fileVersion represents the version of PictoBlox from where the file was exported.
        let fileVersion;
        return validationPromise
            .then(validatedInput => {
                fileVersion = validatedInput[0].pictobloxVersion;
                return this.deserializeProject(validatedInput[0], validatedInput[1]);
            })
            .then(() => {
                this.runtime.emitProjectLoaded();
                return Promise.resolve(fileVersion);
            })
            .catch(error => {
                // Intentionally rejecting here (want errors to be handled by caller)
                if (error.hasOwnProperty('validationError')) {
                    return Promise.reject({ error: JSON.stringify(error), fileVersion: fileVersion, isValidationError: true });
                }
                return Promise.reject({ error: error, fileVersion: fileVersion, isValidationError: false });
            });
    }

    /**
     * Load a project from the Scratch web site, by ID.
     * @param {string} id - the ID of the project to download, as a string.
     */
    downloadProjectId(id) {
        const storage = this.runtime.storage;
        if (!storage) {
            log.error('No storage module present; cannot load project: ', id);
            return;
        }
        const vm = this;
        const promise = storage.load(storage.AssetType.Project, id);
        promise.then(projectAsset => {
            vm.loadProject(projectAsset.data);
        });
    }

    /**
     * @param {boolean} saveData true if projcet should be saved with data. False, otherwise.
     * @returns {string} Project in a Scratch 3.0 JSON representation.
     */
    saveProjectSb3(saveData) {
        const soundDescs = serializeSounds(this.runtime);
        const costumeDescs = serializeCostumes(this.runtime);
        const projectJson = this.toJSON(saveData);
        let projectImage = '';
        return new Promise(resolve => {
            this.runtime.renderer.requestSnapshot(dataURI => {
                projectImage = dataURI.replace('data:image/png;base64,', '');
                if (projectImage === 'data:,') {
                    projectImage = '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';
                }
                // TODO want to eventually move zip creation out of here, and perhaps
                // into scratch-storage
                const zip = new JSZip();

                // Put everything in a zip file
                zip.file('project.json', projectJson);
                this._addFileDescsToZip(soundDescs.concat(costumeDescs), zip);
                zip.file('ProjectSnap.png', projectImage, { base64: true });
                zip.generateAsync({
                    type: 'uint8array',
                    compression: 'DEFLATE',
                    compressionOptions: {
                        level: 6 // Tradeoff between best speed (1) and best compression (9)
                    }
                })
                    .then(u8 => {
                        resolve(u8);
                    })
                    .catch(err => {
                        console.log(err);
                    });
            });
        });
    }

    /*
     * @type {Array<object>} Array of all costumes and sounds currently in the runtime
     */
    get assets() {
        return this.runtime.targets.reduce((acc, target) => (
            acc
                .concat(target.sprite.sounds.map(sound => sound.asset))
                .concat(target.sprite.costumes.map(costume => costume.asset))
        ), []);
    }

    _addFileDescsToZip(fileDescs, zip) {
        for (let i = 0; i < fileDescs.length; i++) {
            const currFileDesc = fileDescs[i];
            zip.file(currFileDesc.fileName, currFileDesc.fileContent);
        }
    }

    /**
     * Exports a sprite in the sprite3 format.
     * @param {string} targetId ID of the target to export
     * @param {string=} optZipType Optional type that the resulting
     * zip should be outputted in. Options are: base64, binarystring,
     * array, uint8array, arraybuffer, blob, or nodebuffer. Defaults to
     * blob if argument not provided.
     * See https://stuk.github.io/jszip/documentation/api_jszip/generate_async.html#type-option
     * for more information about these options.
     * @return {object} A generated zip of the sprite and its assets in the format
     * specified by optZipType or blob by default.
     */
    exportSprite(targetId, optZipType) {
        const sb3 = require('./serialization/sb3');

        const soundDescs = serializeSounds(this.runtime, targetId);
        const costumeDescs = serializeCostumes(this.runtime, targetId);
        const spriteJson = StringUtil.stringify(sb3.serialize(this.runtime, targetId));

        const zip = new JSZip();
        zip.file('sprite.json', spriteJson);
        this._addFileDescsToZip(soundDescs.concat(costumeDescs), zip);

        return zip.generateAsync({
            type: typeof optZipType === 'string' ? optZipType : 'blob',
            mimeType: 'application/x.scratch.sprite3',
            compression: 'DEFLATE',
            compressionOptions: {
                level: 6
            }
        });
    }

    /**
     * Export project as a Scratch 3.0 JSON representation.
     * @param {boolean} saveData true if projcet should be saved with data. False, otherwise.
     * @return {string} Serialized state of the runtime.
     */
    toJSON(saveData) {
        const sb3 = require('./serialization/sb3');
        return StringUtil.stringify(sb3.serialize(this.runtime, undefined, saveData));
    }

    // TODO do we still need this function? Keeping it here so as not to introduce
    // a breaking change.
    /**
     * Load a project from a Scratch JSON representation.
     * @param {string} json JSON string representing a project.
     * @returns {Promise} Promise that resolves after the project has loaded
     */
    fromJSON(json) {
        log.warning('fromJSON is now just a wrapper around loadProject, please use that function instead.');
        return this.loadProject(json);
    }

    /**
     * Load a project from a Scratch JSON representation.
     * @param {string} projectJSON JSON string representing a project.
     * @param {?JSZip} zip Optional zipped project containing assets to be loaded.
     * @returns {Promise} Promise that resolves after the project has loaded
     */
    deserializeProject(projectJSON, zip) {
        // Clear the current runtime
        this.clear();
        if (projectJSON.boardSelected) {
            this.runtime.setBoard(projectJSON.boardSelected);
            this.emit('BOARD_SELECTED', projectJSON.boardSelected);
        }
        const runtime = this.runtime;
        const deserializePromise = function () {
            const projectVersion = projectJSON.projectVersion;
            if (projectVersion === 2) {
                const sb2 = require('./serialization/sb2');
                return sb2.deserialize(projectJSON, runtime, false, zip);
            }
            if (projectVersion === 3) {
                const sb3 = require('./serialization/sb3');
                return sb3.deserialize(projectJSON, runtime, zip);
            }
            return Promise.reject('Unable to verify Scratch Project version.');
        };
        return deserializePromise()
            .then(({ targets, extensions }) =>
                this.installTargets(targets, extensions, true, projectJSON));
    }

    /**
     * Install `deserialize` results: zero or more targets after the extensions (if any) used by those targets.
     * @param {Array.<Target>} targets - the targets to be installed
     * @param {ImportedExtensionsInfo} extensions - metadata about extensions used by these targets
     * @param {boolean} wholeProject - set to true if installing a whole project, as opposed to a single sprite.
     * @returns {Promise} resolved once targets have been installed
     */
    installTargets(targets, extensions, wholeProject, projectJSON) {
        const extensionPromises = [];

        extensions.extensionIDs.forEach(extensionID => {
            console.log("extensionID", extensionID);
        //     if (extensionID === "teachableMachine" || !this.extensionManager.isExtensionLoaded(extensionID)) {
        //         const teachableMachineModel = { url: projectJSON.teachableMachineModelUrl, type: projectJSON.teachableMachineModelType };
        //         const extensionURL = extensions.extensionURLs.get(extensionID) || extensionID;
        //         if (extensionURL === "teachableMachine") {
        //             let modelURL = teachableMachineModel.url + 'model.json';
        //             let metadataURL = teachableMachineModel.url + 'metadata.json'
        //             if (teachableMachineModel.type == "image") {
        //                 extensionPromises.push(this.extensionManager.loadExtensionURL(extensionURL, null,
        //                     { [extensionURL]: { model: tmImage.load(modelURL, metadataURL), ...teachableMachineModel } }));
        //             } else if (teachableMachineModel.type == "audio") {
        //                 let recognizer = speechCommands.create("BROWSER_FFT", undefined, modelURL, metadataURL);
        //                 extensionPromises.push(this.extensionManager.loadExtensionURL(extensionURL, null,
        //                     { [extensionURL]: { model: recognizer.ensureModelLoaded(), ...teachableMachineModel, recognizer: recognizer } }));
        //             } else {
        //                 extensionPromises.push(this.extensionManager.loadExtensionURL(extensionURL, null,
        //                     { [extensionURL]: { model: tmPose.load(modelURL, metadataURL), ...teachableMachineModel } }));
        //             }
        //         } else if (extensionURL === "faceDetection") {
        //             const { faceComparision } = projectJSON;
        //             extensionPromises.push(this.extensionManager.loadExtensionURL(extensionURL, null, { [extensionURL]: { faceComparision } }));
        //         } else
                    extensionPromises.push(this.extensionManager.loadExtensionURL(extensionURL));
        //     }
        });
        
        targets = targets.filter(target => !!target);

        return Promise.all(extensionPromises).then(() => {
            targets.forEach(target => {
                this.runtime.addTarget(target);
                (/** @type RenderedTarget */ target).updateAllDrawableProperties();
                // Ensure unique sprite name
                if (target.isSprite()) this.renameSprite(target.id, target.getName());
            });
            // Sort the executable targets by layerOrder.
            // Remove layerOrder property after use.
            this.runtime.executableTargets.sort((a, b) => a.layerOrder - b.layerOrder);
            targets.forEach(target => {
                delete target.layerOrder;
            });

            // Select the first target for editing, e.g., the first sprite.
            if (wholeProject && (targets.length > 1)) {
                this.editingTarget = targets[1];
            } else {
                this.editingTarget = targets[0];
            }

            if (!wholeProject) {
                this.editingTarget.fixUpVariableReferences();
            }

            // Update the VM user's knowledge of targets and blocks on the workspace.
            this.emitTargetsUpdate(false /* Don't emit project change */);
            this.emitWorkspaceUpdate();
            this.runtime.setEditingTarget(this.editingTarget);
            this.runtime.ioDevices.cloud.setStage(this.runtime.getTargetForStage());
        });
    }

    /**
     * Add a sprite, this could be .sprite2 or .sprite3. Unpack and validate
     * such a file first.
     * @param {string | object} input A json string, object, or ArrayBuffer representing the project to load.
     * @return {!Promise} Promise that resolves after targets are installed.
     */
    addSprite(input) {
        const errorPrefix = 'Sprite Upload Error:';
        if (typeof input === 'object' && !(input instanceof ArrayBuffer) &&
            !ArrayBuffer.isView(input)) {
            // If the input is an object and not any ArrayBuffer
            // or an ArrayBuffer view (this includes all typed arrays and DataViews)
            // turn the object into a JSON string, because we suspect
            // this is a project.json as an object
            // validate expects a string or buffer as input
            // TODO not sure if we need to check that it also isn't a data view
            input = JSON.stringify(input);
        }

        const validationPromise = new Promise((resolve, reject) => {
            var validate = require( "../local_modules/scratch-parser/index.js");
            // The second argument of true below indicates to the parser/validator
            // that the given input should be treated as a single sprite and not
            // an entire project
            validate(input, true, (error, res) => {
                if (error) return reject(error);
                resolve(res);
            });
        });

        return validationPromise
            .then(validatedInput => {
                const projectVersion = validatedInput[0].projectVersion;
                if (projectVersion === 2) {
                    return this._addSprite2(validatedInput[0], validatedInput[1]);
                }
                if (projectVersion === 3) {
                    return this._addSprite3(validatedInput[0], validatedInput[1]);
                }
                return Promise.reject(`${errorPrefix} Unable to verify sprite version.`);
            })
            .then(() => this.runtime.emitProjectChanged())
            .catch(error => {
                // Intentionally rejecting here (want errors to be handled by caller)
                if (error.hasOwnProperty('validationError')) {
                    return Promise.reject(JSON.stringify(error));
                }
                return Promise.reject(`${errorPrefix} ${error}`);
            });
    }

    /**
     * Add a single sprite from the "Sprite2" (i.e., SB2 sprite) format.
     * @param {object} sprite Object representing 2.0 sprite to be added.
     * @param {?ArrayBuffer} zip Optional zip of assets being referenced by json
     * @returns {Promise} Promise that resolves after the sprite is added
     */
    _addSprite2(sprite, zip) {
        // Validate & parse
        const sb2 = require('./serialization/sb2');
        return sb2.deserialize(sprite, this.runtime, true, zip)
            .then(({ targets, extensions }) =>
                this.installTargets(targets, extensions, false));
    }

    /**
     * Add a single sb3 sprite.
     * @param {object} sprite Object rperesenting 3.0 sprite to be added.
     * @param {?ArrayBuffer} zip Optional zip of assets being referenced by target json
     * @returns {Promise} Promise that resolves after the sprite is added
     */
    _addSprite3(sprite, zip) {
        // Validate & parse
        const sb3 = require('./serialization/sb3');
        return sb3
            .deserialize(sprite, this.runtime, zip, true)
            .then(({ targets, extensions }) => this.installTargets(targets, extensions, false));
    }

    /**
     * Add a costume to the current editing target.
     * @param {string} md5ext - the MD5 and extension of the costume to be loaded.
     * @param {!object} costumeObject Object representing the costume.
     * @property {int} skinId - the ID of the costume's render skin, once installed.
     * @property {number} rotationCenterX - the X component of the costume's origin.
     * @property {number} rotationCenterY - the Y component of the costume's origin.
     * @property {number} [bitmapResolution] - the resolution scale for a bitmap costume.
     * @param {string} optTargetId - the id of the target to add to, if not the editing target.
     * @param {string} optVersion - if this is 2, load costume as sb2, otherwise load costume as sb3.
     * @returns {?Promise} - a promise that resolves when the costume has been added
     */
    addCostume(md5ext, costumeObject, optTargetId, optVersion) {
        const target = optTargetId ? this.runtime.getTargetById(optTargetId) :
            this.editingTarget;
        if (target) {
            return loadCostume(md5ext, costumeObject, this.runtime, optVersion).then(() => {
                target.addCostume(costumeObject);
                target.setCostume(
                    target.getCostumes().length - 1
                );
                this.runtime.emitProjectChanged();
            });
        }
        // If the target cannot be found by id, return a rejected promise
        return Promise.reject();
    }

    /**
     * Add a costume loaded from the library to the current editing target.
     * @param {string} md5ext - the MD5 and extension of the costume to be loaded.
     * @param {!object} costumeObject Object representing the costume.
     * @property {int} skinId - the ID of the costume's render skin, once installed.
     * @property {number} rotationCenterX - the X component of the costume's origin.
     * @property {number} rotationCenterY - the Y component of the costume's origin.
     * @property {number} [bitmapResolution] - the resolution scale for a bitmap costume.
     * @returns {?Promise} - a promise that resolves when the costume has been added
     */
    addCostumeFromLibrary(md5ext, costumeObject) {
        if (!this.editingTarget) return Promise.reject();
        return this.addCostume(md5ext, costumeObject, this.editingTarget.id, 2 /* optVersion */);
    }

    /**
     * Duplicate the costume at the given index. Add it at that index + 1.
     * @param {!int} costumeIndex Index of costume to duplicate
     * @returns {?Promise} - a promise that resolves when the costume has been decoded and added
     */
    duplicateCostume(costumeIndex) {
        const originalCostume = this.editingTarget.getCostumes()[costumeIndex];
        const clone = Object.assign({}, originalCostume);
        const md5ext = `${clone.assetId}.${clone.dataFormat}`;
        return loadCostume(md5ext, clone, this.runtime).then(() => {
            this.editingTarget.addCostume(clone, costumeIndex + 1);
            this.editingTarget.setCostume(costumeIndex + 1);
            this.emitTargetsUpdate();
        });
    }

    /**
     * Duplicate the sound at the given index. Add it at that index + 1.
     * @param {!int} soundIndex Index of sound to duplicate
     * @returns {?Promise} - a promise that resolves when the sound has been decoded and added
     */
    duplicateSound(soundIndex) {
        const originalSound = this.editingTarget.getSounds()[soundIndex];
        const clone = Object.assign({}, originalSound);
        return loadSound(clone, this.runtime, this.editingTarget.sprite.soundBank).then(() => {
            this.editingTarget.addSound(clone, soundIndex + 1);
            this.emitTargetsUpdate();
        });
    }

    /**
     * Rename a costume on the current editing target.
     * @param {int} costumeIndex - the index of the costume to be renamed.
     * @param {string} newName - the desired new name of the costume (will be modified if already in use).
     */
    renameCostume(costumeIndex, newName) {
        this.editingTarget.renameCostume(costumeIndex, newName);
        this.emitTargetsUpdate();
    }

    /**
     * Delete a costume from the current editing target.
     * @param {int} costumeIndex - the index of the costume to be removed.
     * @return {?function} A function to restore the deleted costume, or null,
     * if no costume was deleted.
     */
    deleteCostume(costumeIndex) {
        const deletedCostume = this.editingTarget.deleteCostume(costumeIndex);
        if (deletedCostume) {
            const target = this.editingTarget;
            this.runtime.emitProjectChanged();
            return () => {
                target.addCostume(deletedCostume);
                this.emitTargetsUpdate();
            };
        }
        return null;
    }

    /**
     * Add a sound to the current editing target.
     * @param {!object} soundObject Object representing the costume.
     * @param {string} optTargetId - the id of the target to add to, if not the editing target.
     * @returns {?Promise} - a promise that resolves when the sound has been decoded and added
     */
    addSound(soundObject, optTargetId) {
        const target = optTargetId ? this.runtime.getTargetById(optTargetId) :
            this.editingTarget;
        if (target) {
            return loadSound(soundObject, this.runtime, target.sprite.soundBank).then(() => {
                target.addSound(soundObject);
                this.emitTargetsUpdate();
            });
        }
        // If the target cannot be found by id, return a rejected promise
        return new Promise.reject();
    }

    /**
     * Rename a sound on the current editing target.
     * @param {int} soundIndex - the index of the sound to be renamed.
     * @param {string} newName - the desired new name of the sound (will be modified if already in use).
     */
    renameSound(soundIndex, newName) {
        this.editingTarget.renameSound(soundIndex, newName);
        this.emitTargetsUpdate();
    }

    /**
     * Get a sound buffer from the audio engine.
     * @param {int} soundIndex - the index of the sound to be got.
     * @return {AudioBuffer} the sound's audio buffer.
     */
    getSoundBuffer(soundIndex) {
        const id = this.editingTarget.sprite.sounds[soundIndex].soundId;
        if (id && this.runtime && this.runtime.audioEngine) {
            return this.editingTarget.sprite.soundBank.getSoundPlayer(id).buffer;
        }
        return null;
    }

    /**
     * Update a sound buffer.
     * @param {int} soundIndex - the index of the sound to be updated.
     * @param {AudioBuffer} newBuffer - new audio buffer for the audio engine.
     * @param {ArrayBuffer} soundEncoding - the new (wav) encoded sound to be stored
     */
    updateSoundBuffer(soundIndex, newBuffer, soundEncoding) {
        const sound = this.editingTarget.sprite.sounds[soundIndex];
        const id = sound ? sound.soundId : null;
        if (id && this.runtime && this.runtime.audioEngine) {
            this.editingTarget.sprite.soundBank.getSoundPlayer(id).buffer = newBuffer;
        }
        // Update sound in runtime
        if (soundEncoding) {
            // Now that we updated the sound, the format should also be updated
            // so that the sound can eventually be decoded the right way.
            // Sounds that were formerly 'adpcm', but were updated in sound editor
            // will not get decoded by the audio engine correctly unless the format
            // is updated as below.
            sound.format = '';
            const storage = this.runtime.storage;
            sound.asset = storage.createAsset(
                storage.AssetType.Sound,
                storage.DataFormat.WAV,
                soundEncoding,
                null,
                true // generate md5
            );
            sound.assetId = sound.asset.assetId;
            sound.dataFormat = storage.DataFormat.WAV;
            sound.md5 = `${sound.assetId}.${sound.dataFormat}`;
            sound.sampleCount = newBuffer.length;
            sound.rate = newBuffer.sampleRate;
        }
        // If soundEncoding is null, it's because gui had a problem
        // encoding the updated sound. We don't want to store anything in this
        // case, and gui should have logged an error.

        this.emitTargetsUpdate();
    }

    /**
     * Delete a sound from the current editing target.
     * @param {int} soundIndex - the index of the sound to be removed.
     * @return {?Function} A function to restore the sound that was deleted,
     * or null, if no sound was deleted.
     */
    deleteSound(soundIndex) {
        const target = this.editingTarget;
        const deletedSound = this.editingTarget.deleteSound(soundIndex);
        if (deletedSound) {
            this.runtime.emitProjectChanged();
            const restoreFun = () => {
                target.addSound(deletedSound);
                this.emitTargetsUpdate();
            };
            return restoreFun;
        }
        return null;
    }

    /**
     * Get a string representation of the image from storage.
     * @param {int} costumeIndex - the index of the costume to be got.
     * @return {string} the costume's SVG string if it's SVG,
     *     a dataURI if it's a PNG or JPG, or null if it couldn't be found or decoded.
     */
    getCostume(costumeIndex) {
        const asset = this.editingTarget.getCostumes()[costumeIndex].asset;
        if (!asset || !this.runtime || !this.runtime.storage) return null;
        const format = asset.dataFormat;
        if (format === this.runtime.storage.DataFormat.SVG) {
            return asset.decodeText();
        } else if (format === this.runtime.storage.DataFormat.PNG ||
            format === this.runtime.storage.DataFormat.JPG) {
            return asset.encodeDataURI();
        }
        log.error(`Unhandled format: ${asset.dataFormat}`);
        return null;
    }

    /**
     * Update a costume with the given bitmap
     * @param {!int} costumeIndex - the index of the costume to be updated.
     * @param {!ImageData} bitmap - new bitmap for the renderer.
     * @param {!number} rotationCenterX x of point about which the costume rotates, relative to its upper left corner
     * @param {!number} rotationCenterY y of point about which the costume rotates, relative to its upper left corner
     * @param {!number} bitmapResolution 1 for bitmaps that have 1 pixel per unit of stage,
     *     2 for double-resolution bitmaps
     */
    updateBitmap(costumeIndex, bitmap, rotationCenterX, rotationCenterY, bitmapResolution) {
        const costume = this.editingTarget.getCostumes()[costumeIndex];
        if (!(costume && this.runtime && this.runtime.renderer)) return;

        costume.rotationCenterX = rotationCenterX;
        costume.rotationCenterY = rotationCenterY;

        // If the bitmap originally had a zero width or height, use that value
        const bitmapWidth = bitmap.sourceWidth === 0 ? 0 : bitmap.width;
        const bitmapHeight = bitmap.sourceHeight === 0 ? 0 : bitmap.height;
        // @todo: updateBitmapSkin does not take ImageData
        const canvas = document.createElement('canvas');
        canvas.width = bitmapWidth;
        canvas.height = bitmapHeight;
        const context = canvas.getContext('2d');
        context.putImageData(bitmap, 0, 0);

        // Divide by resolution because the renderer's definition of the rotation center
        // is the rotation center divided by the bitmap resolution
        this.runtime.renderer.updateBitmapSkin(
            costume.skinId,
            canvas,
            bitmapResolution,
            [rotationCenterX / bitmapResolution, rotationCenterY / bitmapResolution]
        );

        // @todo there should be a better way to get from ImageData to a decodable storage format
        canvas.toBlob(blob => {
            const reader = new FileReader();
            reader.addEventListener('loadend', () => {
                const storage = this.runtime.storage;
                costume.dataFormat = storage.DataFormat.PNG;
                costume.bitmapResolution = bitmapResolution;
                costume.size = [bitmapWidth, bitmapHeight];
                costume.asset = storage.createAsset(
                    storage.AssetType.ImageBitmap,
                    costume.dataFormat,
                    Buffer.from(reader.result),
                    null, // id
                    true // generate md5
                );
                costume.assetId = costume.asset.assetId;
                costume.md5 = `${costume.assetId}.${costume.dataFormat}`;
                this.emitTargetsUpdate();
            });
            // Bitmaps with a zero width or height return null for their blob
            if (blob) {
                reader.readAsArrayBuffer(blob);
            }
        });
    }

    /**
     * Update a costume with the given SVG
     * @param {int} costumeIndex - the index of the costume to be updated.
     * @param {string} svg - new SVG for the renderer.
     * @param {number} rotationCenterX x of point about which the costume rotates, relative to its upper left corner
     * @param {number} rotationCenterY y of point about which the costume rotates, relative to its upper left corner
     */
    updateSvg(costumeIndex, svg, rotationCenterX, rotationCenterY) {
        const costume = this.editingTarget.getCostumes()[costumeIndex];
        if (costume && this.runtime && this.runtime.renderer) {
            costume.rotationCenterX = rotationCenterX;
            costume.rotationCenterY = rotationCenterY;
            this.runtime.renderer.updateSVGSkin(costume.skinId, svg, [rotationCenterX, rotationCenterY]);
            costume.size = this.runtime.renderer.getSkinSize(costume.skinId);
        }
        const storage = this.runtime.storage;
        // If we're in here, we've edited an svg in the vector editor,
        // so the dataFormat should be 'svg'
        costume.dataFormat = storage.DataFormat.SVG;
        costume.bitmapResolution = 1;
        costume.asset = storage.createAsset(
            storage.AssetType.ImageVector,
            costume.dataFormat,
            (new _TextEncoder()).encode(svg),
            null,
            true // generate md5
        );
        costume.assetId = costume.asset.assetId;
        costume.md5 = `${costume.assetId}.${costume.dataFormat}`;
        this.emitTargetsUpdate();
    }

    /**
     * Add a backdrop to the stage.
     * @param {string} md5ext - the MD5 and extension of the backdrop to be loaded.
     * @param {!object} backdropObject Object representing the backdrop.
     * @property {int} skinId - the ID of the backdrop's render skin, once installed.
     * @property {number} rotationCenterX - the X component of the backdrop's origin.
     * @property {number} rotationCenterY - the Y component of the backdrop's origin.
     * @property {number} [bitmapResolution] - the resolution scale for a bitmap backdrop.
     * @returns {?Promise} - a promise that resolves when the backdrop has been added
     */
    addBackdrop(md5ext, backdropObject) {
        return loadCostume(md5ext, backdropObject, this.runtime).then(() => {
            const stage = this.runtime.getTargetForStage();
            stage.addCostume(backdropObject);
            stage.setCostume(stage.getCostumes().length - 1);
            this.runtime.emitProjectChanged();
        });
    }
     
    /**
     * Add a backdrop loaded from the library to the current editing target.
     * @param {string} md5ext - the MD5 and extension of the backdrop to be loaded.
     * @param {!object} backdropObject Object representing the backdrop.
     * @property {int} skinId - the ID of the backdrop's render skin, once installed.
     * @property {number} rotationCenterX - the X component of the backdrop's origin.
     * @property {number} rotationCenterY - the Y component of the backdrop's origin.
     * @property {number} [bitmapResolution] - the resolution scale for a bitmap backdrop.
     * @returns {?Promise} - a promise that resolves when the backdrop has been added
     */
    addBackdropFromLibrary(md5ext, backdropObject) {
        if (!this.editingTarget) return Promise.reject();
        return this.addBackdrop(md5ext, backdropObject, this.editingTarget.id);
    }

    /**
     * Rename a sprite.
     * @param {string} targetId ID of a target whose sprite to rename.
     * @param {string} newName New name of the sprite.
     */
    renameSprite(targetId, newName) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            if (!target.isSprite()) {
                throw new Error('Cannot rename non-sprite targets.');
            }
            const sprite = target.sprite;
            if (!sprite) {
                throw new Error('No sprite associated with this target.');
            }
            if (newName && RESERVED_NAMES.indexOf(newName) === -1) {
                const names = this.runtime.targets
                    .filter(runtimeTarget => runtimeTarget.isSprite() && runtimeTarget.id !== target.id)
                    .map(runtimeTarget => runtimeTarget.sprite.name);
                const oldName = sprite.name;
                const newUnusedName = StringUtil.unusedName(newName, names);
                sprite.name = newUnusedName;
                const allTargets = this.runtime.targets;
                for (let i = 0; i < allTargets.length; i++) {
                    const currTarget = allTargets[i];
                    currTarget.blocks.updateAssetName(oldName, newName, 'sprite');
                }

                if (newUnusedName !== oldName) this.emitTargetsUpdate();
            }
        } else {
            throw new Error('No target with the provided id.');
        }
    }

    /**
     * Delete a sprite and all its clones.
     * @param {string} targetId ID of a target whose sprite to delete.
     * @return {Function} Returns a function to restore the sprite that was deleted
     */
    deleteSprite(targetId) {
        const target = this.runtime.getTargetById(targetId);

        if (target) {
            const targetIndexBeforeDelete = this.runtime.targets.map(t => t.id).indexOf(target.id);
            if (!target.isSprite()) {
                throw new Error('Cannot delete non-sprite targets.');
            }
            const sprite = target.sprite;
            if (!sprite) {
                throw new Error('No sprite associated with this target.');
            }
            const spritePromise = this.exportSprite(targetId, 'uint8array');
            const restoreSprite = () => spritePromise.then(spriteBuffer => this.addSprite(spriteBuffer));
            // Remove monitors from the runtime state and remove the
            // target-specific monitored blocks (e.g. local variables)
            target.deleteMonitors();
            const currentEditingTarget = this.editingTarget;
            for (let i = 0; i < sprite.clones.length; i++) {
                const clone = sprite.clones[i];
                this.runtime.stopForTarget(sprite.clones[i]);
                this.runtime.disposeTarget(sprite.clones[i]);
                // Ensure editing target is switched if we are deleting it.
                if (clone === currentEditingTarget) {
                    const nextTargetIndex = Math.min(this.runtime.targets.length - 1, targetIndexBeforeDelete);
                    if (this.runtime.targets.length > 0) {
                        this.setEditingTarget(this.runtime.targets[nextTargetIndex].id);
                    } else {
                        this.editingTarget = null;
                    }
                }
            }
            // Sprite object should be deleted by GC.
            this.emitTargetsUpdate();
            return restoreSprite;
        }

        throw new Error('No target with the provided id.');
    }

    /**
     * Duplicate a sprite.
     * @param {string} targetId ID of a target whose sprite to duplicate.
     * @returns {Promise} Promise that resolves when duplicated target has
     *     been added to the runtime.
     */
    duplicateSprite(targetId) {
        const target = this.runtime.getTargetById(targetId);
        if (!target) {
            throw new Error('No target with the provided id.');
        } else if (!target.isSprite()) {
            throw new Error('Cannot duplicate non-sprite targets.');
        } else if (!target.sprite) {
            throw new Error('No sprite associated with this target.');
        }
        return target.duplicate().then(newTarget => {
            this.runtime.addTarget(newTarget);
            newTarget.goBehindOther(target);
            this.setEditingTarget(newTarget.id);
        });
    }

    /**
     * Set the audio engine for the VM/runtime
     * @param {!AudioEngine} audioEngine The audio engine to attach
     */
    attachAudioEngine(audioEngine) {
        this.runtime.attachAudioEngine(audioEngine);
    }

    /**
     * Set the renderer for the VM/runtime
     * @param {!RenderWebGL} renderer The renderer to attach
     */
    attachRenderer(renderer) {
        this.runtime.attachRenderer(renderer);
    }

    /**
     * @returns {RenderWebGL} The renderer attached to the vm
     */
    get renderer() {
        return this.runtime && this.runtime.renderer;
    }

    /**
     * Set the svg adapter for the VM/runtime, which converts scratch 2 svgs to scratch 3 svgs
     * @param {!SvgRenderer} svgAdapter The adapter to attach
     */
    attachV2SVGAdapter(svgAdapter) {
        this.runtime.attachV2SVGAdapter(svgAdapter);
    }

    /**
     * Set the bitmap adapter for the VM/runtime, which converts scratch 2
     * bitmaps to scratch 3 bitmaps. (Scratch 3 bitmaps are all bitmap resolution 2)
     * @param {!function} bitmapAdapter The adapter to attach
     */
    attachV2BitmapAdapter(bitmapAdapter) {
        this.runtime.attachV2BitmapAdapter(bitmapAdapter);
    }

    /**
     * Set the storage module for the VM/runtime
     * @param {!ScratchStorage} storage The storage module to attach
     */
    attachStorage(storage) {
        this.runtime.attachStorage(storage);
    }

    /**
     * set the current locale and builtin messages for the VM
     * @param {!string} locale       current locale
     * @param {!object} messages     builtin messages map for current locale
     * @returns {Promise} Promise that resolves when all the blocks have been
     *     updated for a new locale (or empty if locale hasn't changed.)
     */
    setLocale(locale, messages) {
        if (locale !== formatMessage.setup().locale) {
            formatMessage.setup({ locale: locale, translations: { [locale]: messages } });
        }
        // return this.extensionManager.refreshBlocks();
    }

    /**
     * get the current locale for the VM
     * @returns {string} the current locale in the VM
     */
    getLocale() {
        return formatMessage.setup().locale;
    }

    /**
     * Handle a Blockly event for the current editing target.
     * @param {!Blockly.Event} e Any Blockly event.
     */
    blockListener(e) {
        if (this.editingTarget) {
            this.editingTarget.blocks.blocklyListen(e);
        }
    }

    /**
     * Handle a Blockly event for the flyout.
     * @param {!Blockly.Event} e Any Blockly event.
     */
    flyoutBlockListener(e) {
        this.runtime.flyoutBlocks.blocklyListen(e);
    }

    /**
     * Handle a Blockly event for the flyout to be passed to the monitor container.
     * @param {!Blockly.Event} e Any Blockly event.
     */
    monitorBlockListener(e) {
        // Filter events by type, since monitor blocks only need to listen to these events.
        // Monitor blocks shouldn't be destroyed when flyout blocks are deleted.
        if (['create', 'change'].indexOf(e.type) !== -1) {
            this.runtime.monitorBlocks.blocklyListen(e);
        }
    }

    /**
     * Handle a Blockly event for the variable map.
     * @param {!Blockly.Event} e Any Blockly event.
     */
    variableListener(e) {
        // Filter events by type, since blocks only needs to listen to these
        // var events.
        if (['var_create', 'var_rename', 'var_delete'].indexOf(e.type) !== -1) {
            this.runtime.getTargetForStage().blocks.blocklyListen(e);
        }
    }

    /**
     * Set an editing target. An editor UI can use this function to switch
     * between editing different targets, sprites, etc.
     * After switching the editing target, the VM may emit updates
     * to the list of targets and any attached workspace blocks
     * (see `emitTargetsUpdate` and `emitWorkspaceUpdate`).
     * @param {string} targetId Id of target to set as editing.
     */
    setEditingTarget(targetId) {
        // Has the target id changed? If not, exit.
        if (this.editingTarget && targetId === this.editingTarget.id) {
            return;
        }
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            this.editingTarget = target;
            // Emit appropriate UI updates.
            this.emitTargetsUpdate(false /* Don't emit project change */);
            this.emitWorkspaceUpdate();
            this.runtime.setEditingTarget(target);
        }
    }

    /**
     * Called when blocks are dragged from one sprite to another. Adds the blocks to the
     * workspace of the given target.
     * @param {!Array<object>} blocks Blocks to add.
     * @param {!string} targetId Id of target to add blocks to.
     * @param {?string} optFromTargetId Optional target id indicating that blocks are being
     * shared from that target. This is needed for resolving any potential variable conflicts.
     * @return {!Promise} Promise that resolves when the extensions and blocks have been added.
     */
    shareBlocksToTarget(blocks, targetId, optFromTargetId) {
        const sb3 = require('./serialization/sb3');

        const copiedBlocks = JSON.parse(JSON.stringify(blocks));
        newBlockIds(copiedBlocks);
        const target = this.runtime.getTargetById(targetId);

        if (optFromTargetId) {
            // If the blocks are being shared from another target,
            // resolve any possible variable conflicts that may arise.
            const fromTarget = this.runtime.getTargetById(optFromTargetId);
            fromTarget.resolveVariableSharingConflictsWithTarget(copiedBlocks, target);
        }

        // Create a unique set of extensionIds that are not yet loaded
        const extensionIDs = new Set(copiedBlocks
            .map(b => sb3.getExtensionIdForOpcode(b.opcode))
            .filter(id => !!id) // Remove ids that do not exist
            .filter(id => !this.extensionManager.isExtensionLoaded(id)) // and remove loaded extensions
        );

        // const extensionPromises = []
        // Create an array promises for extensions to load
        const extensionPromises = Array.from(extensionIDs,
            id => this.extensionManager.loadExtensionURL(id)
        );

        return Promise.all(extensionPromises).then(() => {
            copiedBlocks.forEach(block => {
                target.blocks.createBlock(block);
            });
            target.blocks.updateTargetSpecificBlocks(target.isStage);
        });
    }

    /**
     * Called when costumes are dragged from editing target to another target.
     * Sets the newly added costume as the current costume.
     * @param {!number} costumeIndex Index of the costume of the editing target to share.
     * @param {!string} targetId Id of target to add the costume.
     * @return {Promise} Promise that resolves when the new costume has been loaded.
     */
    shareCostumeToTarget(costumeIndex, targetId) {
        const originalCostume = this.editingTarget.getCostumes()[costumeIndex];
        const clone = Object.assign({}, originalCostume);
        const md5ext = `${clone.assetId}.${clone.dataFormat}`;
        return loadCostume(md5ext, clone, this.runtime).then(() => {
            const target = this.runtime.getTargetById(targetId);
            if (target) {
                target.addCostume(clone);
                target.setCostume(
                    target.getCostumes().length - 1
                );
            }
        });
    }

    /**
     * Called when sounds are dragged from editing target to another target.
     * @param {!number} soundIndex Index of the sound of the editing target to share.
     * @param {!string} targetId Id of target to add the sound.
     * @return {Promise} Promise that resolves when the new sound has been loaded.
     */
    shareSoundToTarget(soundIndex, targetId) {
        const originalSound = this.editingTarget.getSounds()[soundIndex];
        const clone = Object.assign({}, originalSound);
        const target = this.runtime.getTargetById(targetId);
        return loadSound(clone, this.runtime, target.sprite.soundBank).then(() => {
            if (target) {
                target.addSound(clone);
                this.emitTargetsUpdate();
            }
        });
    }

    /**
     * Repopulate the workspace with the blocks of the current editingTarget. This
     * allows us to get around bugs like gui#413.
     */
    refreshWorkspace() {
        if (this.editingTarget) {
            this.emitWorkspaceUpdate();
            this.runtime.setEditingTarget(this.editingTarget);
            this.emitTargetsUpdate(false /* Don't emit project change */);
        }
    }

    /**
     * Emit metadata about available targets.
     * An editor UI could use this to display a list of targets and show
     * the currently editing one.
     * @param {bool} triggerProjectChange If true, also emit a project changed event.
     * Disabled selectively by updates that don't affect project serialization.
     * Defaults to true.
     */
    emitTargetsUpdate(triggerProjectChange) {
        if (typeof triggerProjectChange === 'undefined') triggerProjectChange = true;
        this.emit('targetsUpdate', {
            // [[target id, human readable target name], ...].
            targetList: this.runtime.targets
                .filter(
                    // Don't report clones.
                    target => !target.hasOwnProperty('isOriginal') || target.isOriginal
                ).map(
                    target => target.toJSON()
                ),
            // Currently editing target id.
            editingTarget: this.editingTarget ? this.editingTarget.id : null
        });
        if (triggerProjectChange) {
            this.runtime.emitProjectChanged();
        }
    }

    /**
     * Emit an Blockly/scratch-blocks compatible XML representation
     * of the current editing target's blocks.
     */
    emitWorkspaceUpdate() {
        // Create a list of broadcast message Ids according to the stage variables
        const stageVariables = this.runtime.getTargetForStage().variables;
        let messageIds = [];
        for (const varId in stageVariables) {
            if (stageVariables[varId].type === Variable.BROADCAST_MESSAGE_TYPE) {
                messageIds.push(varId);
            }
        }
        // Go through all blocks on all targets, removing referenced
        // broadcast ids from the list.
        for (let i = 0; i < this.runtime.targets.length; i++) {
            const currTarget = this.runtime.targets[i];
            const currBlocks = currTarget.blocks._blocks;
            for (const blockId in currBlocks) {
                if (currBlocks[blockId].fields.BROADCAST_OPTION) {
                    const id = currBlocks[blockId].fields.BROADCAST_OPTION.id;
                    const index = messageIds.indexOf(id);
                    if (index !== -1) {
                        messageIds = messageIds.slice(0, index)
                            .concat(messageIds.slice(index + 1));
                    }
                }
            }
        }
        // Anything left in messageIds is not referenced by a block, so delete it.
        for (let i = 0; i < messageIds.length; i++) {
            const id = messageIds[i];
            delete this.runtime.getTargetForStage().variables[id];
        }
        const globalVarMap = Object.assign({}, this.runtime.getTargetForStage().variables);
        const localVarMap = this.editingTarget.isStage ?
            Object.create(null) :
            Object.assign({}, this.editingTarget.variables);

        const globalVariables = Object.keys(globalVarMap).map(k => globalVarMap[k]);
        const localVariables = Object.keys(localVarMap).map(k => localVarMap[k]);
        const workspaceComments = Object.keys(this.editingTarget.comments)
            .map(k => this.editingTarget.comments[k])
            .filter(c => c.blockId === null);

        const xmlString = `<xml xmlns="http://www.w3.org/1999/xhtml">
                            <variables>
                                ${globalVariables.map(v => v.toXML()).join()}
                                ${localVariables.map(v => v.toXML(true)).join()}
                            </variables>
                            ${workspaceComments.map(c => c.toXML()).join()}
                            ${this.editingTarget.blocks.toXML(this.editingTarget.comments)}
                        </xml>`;

        this.emit('workspaceUpdate', { xml: xmlString });
    }

    /**
     * Get a target id for a drawable id. Useful for interacting with the renderer
     * @param {int} drawableId The drawable id to request the target id for
     * @returns {?string} The target id, if found. Will also be null if the target found is the stage.
     */
    getTargetIdForDrawableId(drawableId) {
        const target = this.runtime.getTargetByDrawableId(drawableId);
        if (target && target.hasOwnProperty('id') && target.hasOwnProperty('isStage') && !target.isStage) {
            return target.id;
        }
        return null;
    }

    /**
     * Reorder target by index. Return whether a change was made.
     * @param {!string} targetIndex Index of the target.
     * @param {!number} newIndex index that the target should be moved to.
     * @returns {boolean} Whether a target was reordered.
     */
    reorderTarget(targetIndex, newIndex) {
        let targets = this.runtime.targets;
        targetIndex = MathUtil.clamp(targetIndex, 0, targets.length - 1);
        newIndex = MathUtil.clamp(newIndex, 0, targets.length - 1);
        if (targetIndex === newIndex) return false;
        const target = targets[targetIndex];
        targets = targets.slice(0, targetIndex).concat(targets.slice(targetIndex + 1));
        targets.splice(newIndex, 0, target);
        this.runtime.targets = targets;
        this.emitTargetsUpdate();
        return true;
    }

    /**
     * Reorder the costumes of a target if it exists. Return whether it succeeded.
     * @param {!string} targetId ID of the target which owns the costumes.
     * @param {!number} costumeIndex index of the costume to move.
     * @param {!number} newIndex index that the costume should be moved to.
     * @returns {boolean} Whether a costume was reordered.
     */
    reorderCostume(targetId, costumeIndex, newIndex) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            const reorderSuccessful = target.reorderCostume(costumeIndex, newIndex);
            if (reorderSuccessful) {
                this.runtime.emitProjectChanged();
            }
            return reorderSuccessful;
        }
        return false;
    }

    /**
     * Reorder the sounds of a target if it exists. Return whether it occured.
     * @param {!string} targetId ID of the target which owns the sounds.
     * @param {!number} soundIndex index of the sound to move.
     * @param {!number} newIndex index that the sound should be moved to.
     * @returns {boolean} Whether a sound was reordered.
     */
    reorderSound(targetId, soundIndex, newIndex) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            const reorderSuccessful = target.reorderSound(soundIndex, newIndex);
            if (reorderSuccessful) {
                this.runtime.emitProjectChanged();
            }
            return reorderSuccessful;
        }
        return false;
    }

    /**
     * Put a target into a "drag" state, during which its X/Y positions will be unaffected
     * by blocks.
     * @param {string} targetId The id for the target to put into a drag state
     */
    startDrag(targetId) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            this._dragTarget = target;
            target.startDrag();
        }
    }

    /**
     * Remove a target from a drag state, so blocks may begin affecting X/Y position again
     * @param {string} targetId The id for the target to remove from the drag state
     */
    stopDrag(targetId) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            this._dragTarget = null;
            target.stopDrag();
            this.setEditingTarget(target.sprite && target.sprite.clones[0] ?
                target.sprite.clones[0].id : target.id);
        }
    }

    /**
     * Post/edit sprite info for the current editing target or the drag target.
     * @param {object} data An object with sprite info data to set.
     */
    postSpriteInfo(data) {
        if (this._dragTarget) {
            this._dragTarget.postSpriteInfo(data);
        } else {
            this.editingTarget.postSpriteInfo(data);
        }
        // Post sprite info means the gui has changed something about a sprite,
        // either through the sprite info pane fields (e.g. direction, size) or
        // through dragging a sprite on the stage
        // Emit a project changed event.
        this.runtime.emitProjectChanged();
    }

    /**
     * Set a target's variable's value. Return whether it succeeded.
     * @param {!string} targetId ID of the target which owns the variable.
     * @param {!string} variableId ID of the variable to set.
     * @param {!*} value The new value of that variable.
     * @returns {boolean} whether the target and variable were found and updated.
     */
    setVariableValue(targetId, variableId, value) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            const variable = target.lookupVariableById(variableId);
            if (variable) {
                variable.value = value;

                if (variable.isCloud) {
                    this.runtime.ioDevices.cloud.requestUpdateVariable(variable.name, variable.value);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Get a target's variable's value. Return null if the target or variable does not exist.
     * @param {!string} targetId ID of the target which owns the variable.
     * @param {!string} variableId ID of the variable to set.
     * @returns {?*} The value of the variable, or null if it could not be looked up.
     */
    getVariableValue(targetId, variableId) {
        const target = this.runtime.getTargetById(targetId);
        if (target) {
            const variable = target.lookupVariableById(variableId);
            if (variable) {
                return variable.value;
            }
        }
        return null;
    }

    /**
     * Allow VM consumer to configure the ScratchLink socket creator.
     * @param {Function} factory The custom ScratchLink socket factory.
     */
    configureScratchLinkSocketFactory(factory) {
        this.runtime.configureScratchLinkSocketFactory(factory);
    }

    /**
     * Post/edit input value for the current input being focused.
     * @param {object} data An object with input data to set.
     */
    postInputFocusValue(data) {
        this.runtime.setInputValue(data);
    }

}

module.exports = VirtualMachine;
