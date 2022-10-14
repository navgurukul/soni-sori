const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require('format-message');
const Runtime = require('../../../engine/runtime');
const automl = require("@tensorflow/tfjs-automl");
const StageLayering = require('../../../engine/stage-layering');

const Video = require('../../../io/video');

const { ModelLoadingType } = require('../../../util/board-config');

objectDetected = [];

let MakerAttributes = [];
for (let i = 0; i < 40; i++) {
    MakerAttributes[i] = {
        color4f: [Math.random(), Math.random(), Math.random(), 0.7],
        diameter: 3
    };
}

let options = { score: 0.8, iou: 0.5, topk: 50 };

let drawOnStage = true;

let isStage = false;
let stageWidth = 480;
let stageHeight = 360;

let netModel2;

const modelURL = "https://raw.githubusercontent.com/pankaj13461/leftRightOD/master/Model6/model.json";

const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyMy4wLjUsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGNzkzMUU7c3Ryb2tlOiNGRkZGRkY7c3Ryb2tlLXdpZHRoOjEuNTtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0MXtmaWxsOiNGMkYyRjI7fQ0KCS5zdDJ7ZmlsbDojRkZGRkZGO30NCjwvc3R5bGU+DQo8Zz4NCgk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNMzIuOCwzNC41SDcuMmMtMSwwLTEuNy0wLjgtMS43LTEuN1Y3LjNjMC0xLDAuOC0xLjgsMS44LTEuOGgyNS41YzEsMCwxLjcsMC44LDEuNywxLjd2MjUuNQ0KCQlDMzQuNSwzMy43LDMzLjcsMzQuNSwzMi44LDM0LjV6Ii8+DQoJPGc+DQoJCTxnPg0KCQkJPHBhdGggY2xhc3M9InN0MSIgZD0iTTEzLjQsMTkuM2MtMi45LDAtNS4yLTIuMy01LjItNS4yczIuMy01LjIsNS4yLTUuMnM1LjIsMi4zLDUuMiw1LjJTMTYuMywxOS4zLDEzLjQsMTkuM3ogTTEzLjQsMTANCgkJCQljLTIuMywwLTQuMiwxLjktNC4yLDQuMnMxLjksNC4yLDQuMiw0LjJzNC4yLTEuOSw0LjItNC4yUzE1LjcsMTAsMTMuNCwxMHoiLz4NCgkJPC9nPg0KCQk8Zz4NCgkJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0zMS4yLDMwLjdIMjAuNWMtMC4yLDAtMC40LTAuMS0wLjUtMC4zYy0wLjEtMC4yLTAuMS0wLjQsMC0wLjZsNS40LTguNmMwLjItMC4zLDAuOC0wLjMsMSwwbDUuNCw4LjYNCgkJCQljMC4xLDAuMiwwLjEsMC40LDAsMC42QzMxLjYsMzAuNSwzMS40LDMwLjcsMzEuMiwzMC43eiBNMjAuOSwyOS44aDkuOWwtNC45LTcuOUwyMC45LDI5Ljh6Ii8+DQoJCTwvZz4NCgkJPGc+DQoJCQk8Y2lyY2xlIGNsYXNzPSJzdDEiIGN4PSIxMy43IiBjeT0iMjMuNiIgcj0iMi42Ii8+DQoJCQk8Y2lyY2xlIGNsYXNzPSJzdDEiIGN4PSIxMS42IiBjeT0iMjcuMSIgcj0iMi42Ii8+DQoJCQk8Y2lyY2xlIGNsYXNzPSJzdDEiIGN4PSIxNiIgY3k9IjI3LjEiIHI9IjIuNiIvPg0KCQkJPGc+DQoJCQkJPHBhdGggY2xhc3M9InN0MSIgZD0iTTEzLjcsMzEuMWMtMC40LDAtMC43LTAuMy0wLjctMC43di0yLjhjMC0wLjQsMC4zLTAuNywwLjctMC43czAuNywwLjMsMC43LDAuN3YyLjgNCgkJCQkJQzE0LjQsMzAuOCwxNC4xLDMxLjEsMTMuNywzMS4xeiIvPg0KCQkJPC9nPg0KCQk8L2c+DQoJCTxnPg0KCQkJPHBhdGggY2xhc3M9InN0MSIgZD0iTTMxLjUsMTIuM2wtNS4xLTIuOWMtMC4xLTAuMS0wLjIsMC0wLjIsMC4xdjUuN2MwLDAuMSwwLjEsMC4yLDAuMiwwLjFsNS4xLTIuOQ0KCQkJCUMzMS42LDEyLjUsMzEuNiwxMi4zLDMxLjUsMTIuM3oiLz4NCgkJCTxnPg0KCQkJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik0yNy45LDEyLjNjMCwwLjMtMC4yLDAuNi0wLjYsMC42aC0zLjJjLTAuMSwwLTAuMSwwLjEtMC4xLDAuMXY0LjFjMCwwLjMtMC4yLDAuNi0wLjYsMC42DQoJCQkJCWMtMC4zLDAtMC42LTAuMi0wLjYtMC42di00LjhjMC0wLjMsMC4zLTAuNiwwLjYtMC42aDMuOUMyNy42LDExLjgsMjcuOSwxMiwyNy45LDEyLjN6Ii8+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQo8L2c+DQo8L3N2Zz4NCg==';

const menuIconURI = blockIconURI;

class customObjectDetection {
    constructor(runtime) {
        this.runtime = runtime;
        let self = this;
        this.modelLoaded = false;

        // To identify the MODEL_FILES_LOCATED from multiple listeners in this class
        this.modelLoadingId = "_" + ModelLoadingType.customObjectDetection;
        this.runtime.emitModelLoading(ModelLoadingType.customObjectDetection, this.modelLoadingId);

        this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
            console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
            if (isAbort) {
                return self.runtime.emit('MODEL_LOADING_FINISHED', false);
            }
            var netModel = new Promise(resolve => {
                automl.loadObjectDetection(`${modelPath}/model.json`).then(function (net) {
                    netModel2 = net;
                    self.runtime.renderer.requestSnapshot(data => {
                        let image = document.createElement('img');
                        image.onload = function () {
                            netModel2.detect(image, options).then(function (output) {
                                isStage = true;
                                stageWidth = image.width;
                                stageHeight = image.height;
                                objectDetected = output;
                                self.modelLoaded = true;
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < objectDetected.length; i++) {
                                        self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                                            objectDetected[i].box.width, objectDetected[i].box.height,
                                            stageWidth, stageHeight, i);
                                    }
                                }
                                runtime.emit('MODEL_LOADING_FINISHED', true);
                                resolve('Done');
                                return 'Done';
                            })
                        };
                        image.setAttribute("src", data);
                    });
                }).catch(err => {
                    this.runtime.emit('MODEL_LOADING_FINISHED', false);
                })
            });
        });

        this.globalVideoState = 'off';
        this.runtime.ioDevices.video.disableVideo();

        this._canvas = document.querySelector('canvas');
        this._penSkinId = this.runtime.renderer.createPenSkin();
        const penDrawableId = this.runtime.renderer.createDrawable(StageLayering.IMAGE_LAYER);
        this.runtime.renderer.updateDrawableProperties(penDrawableId, { skinId: this._penSkinId });

    }

    static get DIMENSIONS() {
        return [224, 224];
    }

    getInfo() {
        return {
            id: 'customObjectDetection',
            name: 'Custom Object Detection',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage1',
                        default: 'Setting',
                        description: 'Settings'
                    })
                },
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'customObjectDetection.toggleStageVideoFeed',
                        default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] % transparency',
                        description: 'toggle video feed on stage'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        VIDEO_STATE: {
                            type: ArgumentType.STRING,
                            menu: 'videoState',
                            defaultValue: 'on'
                        },
                        TRANSPARENCY: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'drawBoundingBox',
                    text: formatMessage({
                        id: 'customObjectDetection.drawBoundingBox',
                        default: '[OPTION] bounding box',
                        description: 'Draw bounding box'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'drawBox',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'setThreshold',
                    text: formatMessage({
                        id: 'customObjectDetection.setThreshold',
                        default: 'set detection threshold to [THRESHOLD]',
                        description: 'set threshold'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        THRESHOLD: {
                            type: ArgumentType.NUMBER,
                            menu: 'threshold',
                            defaultValue: '0.8'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage2',
                        default: 'Analyse Images',
                        description: 'Analyse Feed'
                    })
                },
                {
                    opcode: 'analyseImage',
                    text: formatMessage({
                        id: 'customObjectDetection.analyseImage',
                        default: 'analyse image from [FEED]',
                        description: 'Analyse Image'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        FEED: {
                            type: ArgumentType.NUMBER,
                            menu: 'feed',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage3',
                        default: 'General Object Detection',
                        description: 'Object Detection'
                    })
                },
                {
                    opcode: 'getObjectCount',
                    text: formatMessage({
                        id: 'customObjectDetection.getObjectCount',
                        default: '# of objects detected',
                        description: 'Get # of people'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'getDetails',
                    text: formatMessage({
                        id: 'customObjectDetection.getDetails',
                        default: '[OPTION] of object [OBJECT]',
                        description: 'X Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.NUMBER,
                            menu: 'personNumbers',
                            defaultValue: '1'
                        },
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'objectOption',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getLabel',
                    text: formatMessage({
                        id: 'customObjectDetection.getLabel',
                        default: 'class - [OBJECT]',
                        description: 'Object Label'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objects',
                            defaultValue: 'Go'
                        },
                    }
                },
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage4',
                        default: 'Traffic Signals',
                        description: 'Traffic Signs'
                    })
                },
                {
                    opcode: 'isDetected1',
                    text: formatMessage({
                        id: 'customObjectDetection.isDetected1',
                        default: 'is signal [OBJECT] detected?',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect1',
                            defaultValue: 'Go'
                        },
                    }
                },
                {
                    opcode: 'getLocationDetected1',
                    text: formatMessage({
                        id: 'customObjectDetection.getLocationDetected1',
                        default: 'get [OPTION] of signal [OBJECT]',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect1',
                            defaultValue: 'Go'
                        },
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'objectOption2',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage5',
                        default: 'Numbers',
                        description: 'Numbers'
                    })
                },
                {
                    opcode: 'isDetected2',
                    text: formatMessage({
                        id: 'customObjectDetection.isDetected2',
                        default: 'is number [OBJECT] detected?',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect2',
                            defaultValue: '0'
                        },
                    }
                },
                {
                    opcode: 'getLocationDetected2',
                    text: formatMessage({
                        id: 'customObjectDetection.getLocationDetected2',
                        default: 'get [OPTION] of number [OBJECT]',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect2',
                            defaultValue: '0'
                        },
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'objectOption2',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'customObjectDetection.blockSeparatorMessage6',
                        default: 'Other Objects',
                        description: 'Other Objects'
                    })
                },
                {
                    opcode: 'isDetected3',
                    text: formatMessage({
                        id: 'customObjectDetection.isDetected3',
                        default: 'is [OBJECT] detected?',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect3',
                            defaultValue: 'Pizza'
                        },
                    }
                },
                {
                    opcode: 'getLocationDetected3',
                    text: formatMessage({
                        id: 'customObjectDetection.getLocationDetected3',
                        default: 'get [OPTION] of [OBJECT]',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objectsSelect3',
                            defaultValue: 'Pizza'
                        },
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'objectOption2',
                            defaultValue: '1'
                        }
                    }
                },
            ],
            menus: {
                videoState: [
                    { text: 'off', value: 'off' },
                    { text: 'on', value: 'on' },
                    { text: 'on flipped', value: 'onFlipped' }
                ],
                personNumbers: {
                    acceptReporters: true,
                    items: [
                        { text: '1', value: '1' },
                        { text: '2', value: '2' },
                        { text: '3', value: '3' },
                        { text: '4', value: '4' },
                        { text: '5', value: '5' },
                        { text: '6', value: '6' },
                        { text: '7', value: '7' },
                        { text: '8', value: '8' },
                        { text: '9', value: '9' },
                        { text: '10', value: '10' },
                        { text: 'closest', value: '0' }
                    ]
                },
                threshold: {
                    acceptReporters: true,
                    items: ['0.95', '0.9', '0.85', '0.8', '0.75', '0.7', '0.6', '0.5', '0.4', '0.3']
                },
                objects: [
                    { text: 'Go', value: 'Go' },
                    { text: 'Stop', value: 'Stop' },
                    { text: 'Go Straight', value: 'Go Straight' },
                    { text: 'Turn Left', value: 'Turn Left' },
                    { text: 'Turn Right', value: 'Turn Right' },
                    { text: 'U Turn', value: 'U Turn' },
                    { text: 'Pedestrian Crossing', value: 'Pedestrian Crossing' },
                    { text: 'Cross Road', value: 'Cross Road' },
                    { text: 'Spades', value: 'Spades' },
                    { text: 'Diamonds', value: 'Diamonds' },
                    { text: 'Clubs', value: 'Clubs' },
                    { text: 'Hearts', value: 'Hearts' },
                    { text: 'Pizza Slice', value: 'Pizza Slice' },
                    { text: 'Pizza', value: 'Pizza' },
                    { text: 'Tree', value: 'Tree' },
                    { text: 'Home', value: 'Home' },
                    { text: 'Hospital', value: 'Hospital' },
                    { text: 'Bomb', value: 'Bomb' },
                    { text: 'Hazel', value: 'Hazel' },
                    { text: 'John', value: 'John' },
                    { text: '0', value: '0' },
                    { text: '1', value: '1' },
                    { text: '2', value: '2' },
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' },
                    { text: '6', value: '6' },
                    { text: '7', value: '7' },
                    { text: '8', value: '8' },
                    { text: '9', value: '9' },
                ],
                objectsSelect1: {
                    acceptReporters: true,
                    items: [
                        { text: 'Go', value: 'Go' },
                        { text: 'Stop', value: 'Stop' },
                        { text: 'Go Straight', value: 'Go Straight' },
                        { text: 'Turn Left', value: 'Turn Left' },
                        { text: 'Turn Right', value: 'Turn Right' },
                        { text: 'U Turn', value: 'U Turn' },
                        { text: 'Pedestrian Crossing', value: 'Pedestrian Crossing' },
                        { text: 'Cross Road', value: 'Cross Road' },
                    ]
                },
                objectsSelect2: {
                    acceptReporters: true,
                    items: [
                        { text: '0', value: '0' },
                        { text: '1', value: '1' },
                        { text: '2', value: '2' },
                        { text: '3', value: '3' },
                        { text: '4', value: '4' },
                        { text: '5', value: '5' },
                        { text: '6', value: '6' },
                        { text: '7', value: '7' },
                        { text: '8', value: '8' },
                        { text: '9', value: '9' },
                    ]
                },
                objectsSelect3: {
                    acceptReporters: true,
                    items: [
                        { text: 'Spades', value: 'Spades' },
                        { text: 'Diamonds', value: 'Diamonds' },
                        { text: 'Clubs', value: 'Clubs' },
                        { text: 'Hearts', value: 'Hearts' },
                        { text: 'Pizza Slice', value: 'Pizza Slice' },
                        { text: 'Pizza', value: 'Pizza' },
                        { text: 'Tree', value: 'Tree' },
                        { text: 'Home', value: 'Home' },
                        { text: 'Hospital', value: 'Hospital' },
                        { text: 'Bomb', value: 'Bomb' },
                        { text: 'Hazel', value: 'Hazel' },
                        { text: 'John', value: 'John' },
                    ]
                },
                objectOption: [
                    { text: 'class', value: '0' },
                    { text: 'x position', value: '1' },
                    { text: 'y position', value: '2' },
                    { text: 'width', value: '3' },
                    { text: 'height', value: '4' },
                    { text: 'confidence', value: '5' }
                ],
                objectOption2: [
                    { text: 'x position', value: '1' },
                    { text: 'y position', value: '2' },
                    { text: 'width', value: '3' },
                    { text: 'height', value: '4' },
                    { text: 'confidence', value: '5' }
                ],
                feed: {
                    items: [
                        { text: 'camera', value: '1' },
                        { text: 'stage', value: '2' },
                    ]
                },
                drawBox: [
                    { text: 'show', value: '1' },
                    { text: 'hide', value: '2' }
                ],
            }
        };
    }

    toggleStageVideoFeed(args, util) {
        const state = args.VIDEO_STATE;
        this.globalVideoState = args.VIDEO_STATE;

        this.runtime.ioDevices.video.setPreviewGhost(args.TRANSPARENCY);

        if (state === 'off') {
            this.runtime.ioDevices.video.disableVideo();
        } else {
            this.runtime.ioDevices.video.enableVideo();
            // Mirror if state is ON. Do not mirror if state is ON_FLIPPED.
            this.runtime.ioDevices.video.mirror = state === 'on';
        }
    }

    analyseImage(args, util) {
        let self = this;
        if (this.modelLoaded) {
            if (args.FEED === '1') {
                const translatePromise = new Promise(resolve => {
                    var frame = this.runtime.ioDevices.video.getFrame({
                        format: Video.FORMAT_IMAGE_DATA,
                        dimensions: customObjectDetection.DIMENSIONS
                    });
                    netModel2.detect(frame, options).then(function (output) {
                        objectDetected = output;
                        console.log(objectDetected);
                        if (drawOnStage) {
                            self._clearMark();
                            for (let i = 0; i < objectDetected.length; i++) {
                                self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                                    objectDetected[i].box.width, objectDetected[i].box.height,
                                    customObjectDetection.DIMENSIONS[0], customObjectDetection.DIMENSIONS[1], i);
                            }
                        }
                        resolve('Done');
                        return 'Done';
                    }).catch(err => {
                        objectDetected = [];
                        resolve('Camera not ready!');
                        return 'Camera not ready!';
                    });
                });
                return translatePromise;
            }
            else if (args.FEED === '2') {
                return new Promise(resolve => {
                    this.runtime.renderer.requestSnapshot(data => {
                        let image = document.createElement('img');
                        image.onload = function () {
                            netModel2.detect(image, options).then(function (output) {
                                isStage = true;
                                stageWidth = image.width;
                                stageHeight = image.height;
                                objectDetected = output;
                                console.log(objectDetected);
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < objectDetected.length; i++) {
                                        self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                                            objectDetected[i].box.width, objectDetected[i].box.height,
                                            stageWidth, stageHeight, i);
                                    }
                                }
                                resolve('Done');
                                return 'Done';
                            })
                        };
                        image.setAttribute("src", data);
                    });
                });
            }
        }
        else {
            this.modelLoadingId = "_" + ModelLoadingType.customObjectDetection + "_1";
            this.runtime.emitModelLoading(ModelLoadingType.customObjectDetection, this.modelLoadingId);
            return new Promise((resolve, reject) => {
                this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
                    if (isAbort) {
                        reject();
                        return self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    }
                    automl.loadObjectDetection(`${modelPath}/model.json`).then((net) => {
                        netModel2 = net;
                        self.runtime.emit('MODEL_LOADING_FINISHED', true);
                        self.modelLoaded = true;
                        if (args.FEED === '1') {
                            const frame = this.runtime.ioDevices.video.getFrame({
                                format: Video.FORMAT_IMAGE_DATA,
                                dimensions: customObjectDetection.DIMENSIONS
                            });
                            netModel2.detect(frame, options).then(function (output) {
                                objectDetected = output;
                                console.log(objectDetected);
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < objectDetected.length; i++) {
                                        self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                                            objectDetected[i].box.width, objectDetected[i].box.height,
                                            customObjectDetection.DIMENSIONS[0], customObjectDetection.DIMENSIONS[1], i);
                                    }
                                }
                                resolve('Done');
                                return 'Done';
                            }).catch(err => {
                                objectDetected = [];
                                resolve('Camera not ready!');
                                return 'Camera not ready!';
                            });
                        }
                        else if (args.FEED === '2') {
                            self.runtime.renderer.requestSnapshot(data => {
                                let image = document.createElement('img');
                                image.onload = function () {
                                    netModel2.detect(image, options).then(function (output) {
                                        isStage = true;
                                        stageWidth = image.width;
                                        stageHeight = image.height;
                                        objectDetected = output;
                                        console.log(objectDetected);
                                        if (drawOnStage) {
                                            self._clearMark();
                                            for (let i = 0; i < objectDetected.length; i++) {
                                                self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                                                    objectDetected[i].box.width, objectDetected[i].box.height,
                                                    stageWidth, stageHeight, i);
                                            }
                                        }
                                        resolve('Done');
                                        return 'Done';
                                    })
                                };
                                image.setAttribute("src", data);
                            });
                        }
                    }).catch(err => {
                        this.runtime.emit('MODEL_LOADING_FINISHED', false);
                        resolve('');
                    });
                });
            });
        }
    }

    _drawMark(x1, y1, w, h, width, height, num) {
        let widthScale = 480 / width;
        let heightScale = 360 / height;

        x1 = x1 * widthScale - width / 2 * widthScale;
        let x2 = x1 + w * widthScale;
        let x3 = x2;
        let x4 = x1;
        y1 = height / 2 * heightScale - y1 * heightScale;
        let y2 = y1;
        let y3 = y1 - h * heightScale;
        let y4 = y3;

        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x1, y1, x2, y2);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x2, y2, x3, y3);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x4, y4, x3, y3);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x4, y4, x1, y1);
    }

    _clearMark() {
        this.runtime.renderer.penClear(this._penSkinId);
    }

    getObjectCount(args, util) {
        return objectDetected.length;
    }

    getDetails(args, util) {
        if (args.OBJECT === "0") {
            let closestObject = -1;
            let closestObjectWidth = 0;
            for (let i = 0; i < objectDetected.length; i++) {
                if (objectDetected[i].box.width > closestObjectWidth) {
                    closestObjectWidth = objectDetected[i].box.width;
                    closestObject = i;
                }
            }
            if (closestObject === -1) {
                return "NULL";
            }
            else {
                if (args.OPTION === "0") {
                    return objectDetected[closestObject].label;
                }
                else if (args.OPTION === "1") {
                    if (!isStage) {
                        let XPos = objectDetected[closestObject].box.left + objectDetected[closestObject].box.width / 2;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                    else {
                        let XPos = 480 * (objectDetected[closestObject].box.left + objectDetected[closestObject].box.width / 2) / stageWidth;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "2") {
                    if (!isStage) {
                        let YPos = objectDetected[closestObject].box.top + objectDetected[closestObject].box.height / 2;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                    else {
                        let YPos = 360 * (objectDetected[closestObject].box.top + objectDetected[closestObject].box.height / 2) / stageHeight;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "3") {
                    if (!isStage) {
                        let Width = objectDetected[closestObject].box.width;
                        return Width.toFixed(1);
                    }
                    else {
                        let Width = 480 * (objectDetected[closestObject].box.width) / stageWidth;
                        return Width.toFixed(1);
                    }
                }
                else if (args.OPTION === "4") {
                    if (!isStage) {
                        let Height = objectDetected[closestObject].box.height;
                        return Height.toFixed(1);
                    }
                    else {
                        let Height = 360 * (objectDetected[closestObject].box.height) / stageHeight;
                        return Height.toFixed(1);
                    }
                }
                else if (args.OPTION === "5") {
                    let Confidence = (objectDetected[closestObject].score);
                    return Confidence.toFixed(2);
                }
            }
        }
        else {
            if (objectDetected[parseInt(args.OBJECT, 10) - 1] && objectDetected[parseInt(args.OBJECT, 10) - 1].score > 0.3) {
                if (args.OPTION === "0") {
                    return objectDetected[parseInt(args.OBJECT, 10) - 1].label;
                }
                else if (args.OPTION === "1") {
                    if (!isStage) {
                        let XPos = objectDetected[parseInt(args.OBJECT, 10) - 1].box.left + objectDetected[parseInt(args.OBJECT, 10) - 1].box.width / 2;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                    else {
                        let XPos = 480 * (objectDetected[parseInt(args.OBJECT, 10) - 1].box.left + objectDetected[parseInt(args.OBJECT, 10) - 1].box.width / 2) / stageWidth;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "2") {
                    if (!isStage) {
                        let YPos = objectDetected[parseInt(args.OBJECT, 10) - 1].box.top + objectDetected[parseInt(args.OBJECT, 10) - 1].box.height / 2;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                    else {
                        let YPos = 360 * (objectDetected[parseInt(args.OBJECT, 10) - 1].box.top + objectDetected[parseInt(args.OBJECT, 10) - 1].box.height / 2) / stageHeight;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "3") {
                    if (!isStage) {
                        let Width = objectDetected[parseInt(args.OBJECT, 10) - 1].box.width;
                        return Width.toFixed(1);
                    }
                    else {
                        let Width = 480 * (objectDetected[parseInt(args.OBJECT, 10) - 1].box.width) / stageWidth;
                        return Width.toFixed(1);
                    }
                }
                else if (args.OPTION === "4") {
                    if (!isStage) {
                        let Height = objectDetected[parseInt(args.OBJECT, 10) - 1].box.height;
                        return Height.toFixed(1);
                    }
                    else {
                        let Height = 360 * (objectDetected[parseInt(args.OBJECT, 10) - 1].box.height) / stageHeight;
                        return Height.toFixed(1);
                    }
                }
                else if (args.OPTION === "5") {
                    let Confidence = (objectDetected[parseInt(args.OBJECT, 10) - 1].score);
                    return Confidence.toFixed(2);
                }
            }
        }
        return "NULL";
    }

    drawBoundingBox(args, util) {
        let self = this;
        if (args.OPTION === "1") {
            drawOnStage = true;
            self._clearMark();
            for (let i = 0; i < objectDetected.length; i++) {
                self._drawMark(objectDetected[i].box.left, objectDetected[i].box.top,
                    objectDetected[i].box.width, objectDetected[i].box.height,
                    stageWidth, stageHeight, i);
            }
        }
        else {
            drawOnStage = false;
            this._clearMark();
        }
    }

    setThreshold(args, util) {
        options.score = parseFloat(args.THRESHOLD);
    }

    getLabel(args, util) {
        return args.OBJECT;
    }

    isDetected1(args, util) {
        let isObjectDetected = false;
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].label.trim() === args.OBJECT.trim()) {
                isObjectDetected = true;
            }
        }
        return isObjectDetected;
    }

    isDetected2(args, util) {
        let isObjectDetected = false;
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].label.trim() === args.OBJECT.trim()) {
                isObjectDetected = true;
            }
        }
        return isObjectDetected;
    }

    isDetected3(args, util) {
        let isObjectDetected = false;
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].label.trim() === args.OBJECT.trim()) {
                isObjectDetected = true;
            }
        }
        return isObjectDetected;
    }

    getLocationDetected1(args, util) {
        return this.getLocationDetected(args);
    }

    getLocationDetected2(args, util) {
        return this.getLocationDetected(args);
    }

    getLocationDetected3(args, util) {
        return this.getLocationDetected(args);
    }

    getLocationDetected(args) {
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].label.trim() === args.OBJECT.trim()) {
                if (args.OPTION === "1") {
                    if (!isStage) {
                        let XPos = objectDetected[i].box.left + objectDetected[i].box.width / 2;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                    else {
                        let XPos = 480 * (objectDetected[i].box.left + objectDetected[i].box.width / 2) / stageWidth;
                        XPos = XPos - 240;
                        return XPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "2") {
                    if (!isStage) {
                        let YPos = objectDetected[i].box.top + objectDetected[i].box.height / 2;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                    else {
                        let YPos = 360 * (objectDetected[i].box.top + objectDetected[i].box.height / 2) / stageHeight;
                        YPos = 180 - YPos;
                        return YPos.toFixed(1);
                    }
                }
                else if (args.OPTION === "3") {
                    if (!isStage) {
                        let Width = objectDetected[i].box.width;
                        return Width.toFixed(1);
                    }
                    else {
                        let Width = 480 * (objectDetected[i].box.width) / stageWidth;
                        return Width.toFixed(1);
                    }
                }
                else if (args.OPTION === "4") {
                    if (!isStage) {
                        let Height = objectDetected[i].box.height;
                        return Height.toFixed(1);
                    }
                    else {
                        let Height = 360 * (objectDetected[i].box.height) / stageHeight;
                        return Height.toFixed(1);
                    }
                }
                else if (args.OPTION === "5") {
                    let Confidence = (objectDetected[i].score);
                    return Confidence.toFixed(2);
                }
            }
        }
        return 'NULL';
    }


}

module.exports = customObjectDetection;
