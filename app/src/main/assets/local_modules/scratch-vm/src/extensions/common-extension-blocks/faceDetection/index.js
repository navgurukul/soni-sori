const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require('format-message');
const faceapi = require('face-api.js');
const Runtime = require('../../../engine/runtime');
const Video = require('../../../io/video');
const StageLayering = require('../../../engine/stage-layering');

const { ModelLoadingType } = require('../../../util/board-config');

let faces = [];

let isStage = false;

let drawOnStage = false;

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzMiIGRhdGEtbmFtZT0iTGF5ZXIgMzMiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgdmlld0JveD0iMCAwIDQwIDQwIj48ZGVmcz48c3R5bGU+LmNscy0xe29wYWNpdHk6MC4wOTt9LmNscy0ye2ZpbGw6bm9uZTtzdHJva2U6I2ZmZjtzdHJva2UtbGluZWNhcDpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MS41cHg7fS5jbHMtM3tmaWxsOiNmNmM3OTI7fS5jbHMtNHtmaWxsOiNkZGIzODQ7fS5jbHMtNXtmaWxsOiMyNjIyNjE7fS5jbHMtNntmaWxsOiNmY2M2OTA7fS5jbHMtN3tmaWxsOiNmNmM4OTk7fS5jbHMtOHtmaWxsOiMxYTFhMWE7fS5jbHMtOXtmaWxsOiNmZmY7fS5jbHMtMTB7ZmlsbDojOWJiMWMxO308L3N0eWxlPjwvZGVmcz48dGl0bGU+SWNvbl9GYWNlIERldGVjdGlvbjwvdGl0bGU+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIyNi4wOCA1LjYyIDMyLjgxIDUuNjIgMzIuODEgMTIuODYiLz48cG9seWxpbmUgY2xhc3M9ImNscy0yIiBwb2ludHM9IjI1Ljk5IDI4Ljk1IDMyLjcyIDI4Ljk1IDMyLjcyIDIxLjciLz48cG9seWxpbmUgY2xhc3M9ImNscy0yIiBwb2ludHM9IjEzLjQ2IDUuMTYgNi43MyA1LjE2IDYuNzMgMTIuNCIvPjxwb2x5bGluZSBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTMuNTUgMjguNDkgNi44MiAyOC40OSA2LjgyIDIxLjI1Ii8+PHBhdGggY2xhc3M9ImNscy0zIiBkPSJNMTUuOSwyNi44MXYxLjVsLTEuMjUsMy42MnMxLjg3LDMuNTcsNS40OSwyLjY3LDQuNjUtMy4yNiw0LjY1LTMuMjZsLTEuNC0zLjQ3TDIzLDI3LjE3WiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTE1LjksMjcuMjl2MXMyLjg0LDIuOSw2LjMsMi45MywyLjA2LDAsMi4wNiwwbDAuNDUtLjExLTEuMDktMi43OUwyMy41NywyN1oiLz48cGF0aCBjbGFzcz0iY2xzLTUiIGQ9Ik03Ljg4LDMyLjM1SDMxLjY1QTMuNjUsMy42NSwwLDAsMSwzNS4zLDM2djIuMTZhMCwwLDAsMCwxLDAsMEg0LjIzYTAsMCwwLDAsMSwwLDBWMzZBMy42NSwzLjY1LDAsMCwxLDcuODgsMzIuMzVaIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMjcsMjEuNThzMC4wNywxLjExLjgyLDAuMjcsMi44My00LjU5LjIzLTQuOTRsLTEuNDksMVoiLz48cGF0aCBjbGFzcz0iY2xzLTYiIGQ9Ik0xMi40OSwyMS41OHMtMC4wNywxLjExLS44Mi4yNy0yLjgzLTQuNTktLjIzLTQuOTRsMS40OSwxWiIvPjxwYXRoIGNsYXNzPSJjbHMtNyIgZD0iTTEyLDE3LjI2bDAuNDUsNC42NFMxNSwyOS40NSwxOS44LDI5Ljc0czcuMjQtOCw3LjI0LThsMC40MS0zLjZWMTEuODRMMjUuMTgsMTAuMkgyMC4zOWwtNS42MS4yNi0yLC4zN0wxMS42NiwxMloiLz48cGF0aCBjbGFzcz0iY2xzLTgiIGQ9Ik0xMi4wOSwxOS43NmExNy41OCwxNy41OCwwLDAsMS0uODEtNC45YzAtMi4zNy0xLjctOSwzLjktOS40MiwwLDAsLjkyLTEuNjcsNC44OC0xLjM0czMuNjgtMiwzLjY4LTIsNy4zMywyLjE3LDQuNDMsMTIuNTFhMTEuNjYsMTEuNjYsMCwwLDEtLjc1LDUuMjFzLTAuOTUuMzMtLjMzLTUuMjdjMCwwLC45NS00Ljc2LTMuNDMtMy44N2ExOC4yLDE4LjIsMCwwLDEtNy43Ny0uMDZzLTUuMTUtMS4xNC0zLjY4LDQuODVDMTIuMiwxNS41MiwxMi44NywxOC4zNiwxMi4wOSwxOS43NloiLz48cGF0aCBjbGFzcz0iY2xzLTkiIGQ9Ik0xNS4xOCwyNy42M2w0LjUxLDQuNzJhMTIuNzcsMTIuNzcsMCwwLDAtMi4xNiwycy0yLjIyLS41MS0zLjQ0LTMuNThaIi8+PHBhdGggY2xhc3M9ImNscy05IiBkPSJNMjQuMTgsMjcuNjNsLTQuNDksNC43MmExMi43NywxMi43NywwLDAsMSwyLjE2LDJzMi4yMi0uNTEsMy40NC0zLjU4WiIvPjxwYXRoIGNsYXNzPSJjbHMtMTAiIGQ9Ik0xNC4xNSwzMC44MXMxLjM0LDQuMTQsNS41NywzLjg2YTYuNDUsNi40NSwwLDAsMCw1LjU4LTMuODgsMy42OCwzLjY4LDAsMCwwLDEuNjMuNjUsOC4xMyw4LjEzLDAsMCwxLTcuMjYsNXMtNC43Mi40MS03LjE5LTQuOTJaIi8+PHBvbHlnb24gY2xhc3M9ImNscy01IiBwb2ludHM9IjEyLjQ4IDMxLjQ4IDEyLjk0IDMyLjM2IDcuMiAzMi40MyAxMi40OCAzMS40OCIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtNSIgcG9pbnRzPSIyNi45MyAzMS40NCAyNi40MyAzMi40MyAzMS45NSAzMi4zNyAyNi45MyAzMS40NCIvPjwvc3ZnPg==';

const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzMiIGRhdGEtbmFtZT0iTGF5ZXIgMzMiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgdmlld0JveD0iMCAwIDQwIDQwIj48ZGVmcz48c3R5bGU+LmNscy0xe29wYWNpdHk6MC4wOTt9LmNscy0ye2ZpbGw6bm9uZTtzdHJva2U6I2ZmZjtzdHJva2UtbGluZWNhcDpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MS41cHg7fS5jbHMtM3tmaWxsOiNmNmM3OTI7fS5jbHMtNHtmaWxsOiNkZGIzODQ7fS5jbHMtNXtmaWxsOiMyNjIyNjE7fS5jbHMtNntmaWxsOiNmY2M2OTA7fS5jbHMtN3tmaWxsOiNmNmM4OTk7fS5jbHMtOHtmaWxsOiMxYTFhMWE7fS5jbHMtOXtmaWxsOiNmZmY7fS5jbHMtMTB7ZmlsbDojOWJiMWMxO308L3N0eWxlPjwvZGVmcz48dGl0bGU+SWNvbl9GYWNlIERldGVjdGlvbjwvdGl0bGU+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIyNi4wOCA1LjYyIDMyLjgxIDUuNjIgMzIuODEgMTIuODYiLz48cG9seWxpbmUgY2xhc3M9ImNscy0yIiBwb2ludHM9IjI1Ljk5IDI4Ljk1IDMyLjcyIDI4Ljk1IDMyLjcyIDIxLjciLz48cG9seWxpbmUgY2xhc3M9ImNscy0yIiBwb2ludHM9IjEzLjQ2IDUuMTYgNi43MyA1LjE2IDYuNzMgMTIuNCIvPjxwb2x5bGluZSBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTMuNTUgMjguNDkgNi44MiAyOC40OSA2LjgyIDIxLjI1Ii8+PHBhdGggY2xhc3M9ImNscy0zIiBkPSJNMTUuOSwyNi44MXYxLjVsLTEuMjUsMy42MnMxLjg3LDMuNTcsNS40OSwyLjY3LDQuNjUtMy4yNiw0LjY1LTMuMjZsLTEuNC0zLjQ3TDIzLDI3LjE3WiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTE1LjksMjcuMjl2MXMyLjg0LDIuOSw2LjMsMi45MywyLjA2LDAsMi4wNiwwbDAuNDUtLjExLTEuMDktMi43OUwyMy41NywyN1oiLz48cGF0aCBjbGFzcz0iY2xzLTUiIGQ9Ik03Ljg4LDMyLjM1SDMxLjY1QTMuNjUsMy42NSwwLDAsMSwzNS4zLDM2djIuMTZhMCwwLDAsMCwxLDAsMEg0LjIzYTAsMCwwLDAsMSwwLDBWMzZBMy42NSwzLjY1LDAsMCwxLDcuODgsMzIuMzVaIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMjcsMjEuNThzMC4wNywxLjExLjgyLDAuMjcsMi44My00LjU5LjIzLTQuOTRsLTEuNDksMVoiLz48cGF0aCBjbGFzcz0iY2xzLTYiIGQ9Ik0xMi40OSwyMS41OHMtMC4wNywxLjExLS44Mi4yNy0yLjgzLTQuNTktLjIzLTQuOTRsMS40OSwxWiIvPjxwYXRoIGNsYXNzPSJjbHMtNyIgZD0iTTEyLDE3LjI2bDAuNDUsNC42NFMxNSwyOS40NSwxOS44LDI5Ljc0czcuMjQtOCw3LjI0LThsMC40MS0zLjZWMTEuODRMMjUuMTgsMTAuMkgyMC4zOWwtNS42MS4yNi0yLC4zN0wxMS42NiwxMloiLz48cGF0aCBjbGFzcz0iY2xzLTgiIGQ9Ik0xMi4wOSwxOS43NmExNy41OCwxNy41OCwwLDAsMS0uODEtNC45YzAtMi4zNy0xLjctOSwzLjktOS40MiwwLDAsLjkyLTEuNjcsNC44OC0xLjM0czMuNjgtMiwzLjY4LTIsNy4zMywyLjE3LDQuNDMsMTIuNTFhMTEuNjYsMTEuNjYsMCwwLDEtLjc1LDUuMjFzLTAuOTUuMzMtLjMzLTUuMjdjMCwwLC45NS00Ljc2LTMuNDMtMy44N2ExOC4yLDE4LjIsMCwwLDEtNy43Ny0uMDZzLTUuMTUtMS4xNC0zLjY4LDQuODVDMTIuMiwxNS41MiwxMi44NywxOC4zNiwxMi4wOSwxOS43NloiLz48cGF0aCBjbGFzcz0iY2xzLTkiIGQ9Ik0xNS4xOCwyNy42M2w0LjUxLDQuNzJhMTIuNzcsMTIuNzcsMCwwLDAtMi4xNiwycy0yLjIyLS41MS0zLjQ0LTMuNThaIi8+PHBhdGggY2xhc3M9ImNscy05IiBkPSJNMjQuMTgsMjcuNjNsLTQuNDksNC43MmExMi43NywxMi43NywwLDAsMSwyLjE2LDJzMi4yMi0uNTEsMy40NC0zLjU4WiIvPjxwYXRoIGNsYXNzPSJjbHMtMTAiIGQ9Ik0xNC4xNSwzMC44MXMxLjM0LDQuMTQsNS41NywzLjg2YTYuNDUsNi40NSwwLDAsMCw1LjU4LTMuODgsMy42OCwzLjY4LDAsMCwwLDEuNjMuNjUsOC4xMyw4LjEzLDAsMCwxLTcuMjYsNXMtNC43Mi40MS03LjE5LTQuOTJaIi8+PHBvbHlnb24gY2xhc3M9ImNscy01IiBwb2ludHM9IjEyLjQ4IDMxLjQ4IDEyLjk0IDMyLjM2IDcuMiAzMi40MyAxMi40OCAzMS40OCIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtNSIgcG9pbnRzPSIyNi45MyAzMS40NCAyNi40MyAzMi40MyAzMS45NSAzMi4zNyAyNi45MyAzMS40NCIvPjwvc3ZnPg==';

let MakerAttributes = [];
for (let i = 0; i < 100; i++) {
    MakerAttributes[i] = {
        color4f: [Math.random(), Math.random(), Math.random(), 0.7],
        diameter: 3
    };
}

let faceThreshold = 0.5;

class faceDetection {
    constructor(runtime, faceComparision = []) {
        this.fullFaceDescriptions = []
        for(let i = 0; i < 10; i++)
        {
            this.fullFaceDescriptions.push([]);
        }
        this.faceComparision = [];
        if (faceComparision && faceComparision.length) {
            console.log("faceComparision.length", faceComparision.length);
            const faceKeys = Object.keys(faceComparision);
            console.log("faceKeys", faceKeys);
            // Pre load face data
            for (let faceIndex = 0; faceIndex < faceKeys.length; faceIndex++) {
                this.faceComparision[faceKeys[faceIndex]] =
                    new faceapi.LabeledFaceDescriptors.fromJSON(faceComparision[faceKeys[faceIndex]]);
            }
        }
        this.runtime = runtime;

        this.modelLoaded = false;
        let self = this;

        this.modelLoadingId = "_" + ModelLoadingType.faceDetection;
        this.runtime.emitModelLoading(ModelLoadingType.faceDetection, this.modelLoadingId);

        this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
            console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
            if (isAbort) {
                return self.runtime.emit('MODEL_LOADING_FINISHED', false);
            }
            var netModel = new Promise(resolve => {
                Promise.all([
                    faceapi.loadSsdMobilenetv1Model(`${modelPath}/ssdMobilenetv1ModelWeightsManifest.json`),
                    faceapi.loadFaceLandmarkModel(`${modelPath}/faceLandmark68ModelWeightsManifest.json`),
                    faceapi.loadFaceExpressionModel(`${modelPath}/faceExpressionModelWeightsManifest.json`),
                    faceapi.loadFaceRecognitionModel(`${modelPath}/faceRecognitionModelWeightsManifest.json`)
                ]).then(() => {
                    runtime.renderer.requestSnapshot(data => {
                        let image = document.createElement('img');
                        image.onload = function () {
                            faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(
                                (fullFaceDescriptions) => {
                                    self.modelLoaded = true;
                                    faces = fullFaceDescriptions;
                                    isStage = true;
                                    runtime.emit(Runtime.MODEL_LOADING_FINISHED, true);
                                    resolve('Done');
                                    return 'Done';
                                });
                        };
                        image.setAttribute("src", data);
                    });
                }).catch(err => {
                    console.log("err", err);
                    self.modelLoaded = false;
                    this.runtime.emit(Runtime.MODEL_LOADING_FINISHED, false);
                });

                this.globalVideoState = 'off';
                this.runtime.ioDevices.video.disableVideo();
            });
        });
        this.extensionName = 'Face Detection';

        this._canvas = document.querySelector('canvas');
        this._penSkinId = this.runtime.renderer.createPenSkin();
        const penDrawableId = this.runtime.renderer.createDrawable(StageLayering.IMAGE_LAYER);
        this.runtime.renderer.updateDrawableProperties(penDrawableId, { skinId: this._penSkinId });
    }

    static get DIMENSIONS() {
        return [480, 360];
    }

    getInfo() {
        return {
            id: 'faceDetection',
            name: 'Face Detection',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'faceDetection.toggleStageVideoFeed',
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
                        id: 'faceDetection.drawBoundingBox',
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
                        id: 'faceDetection.setThreshold',
                        default: 'set detection threshold to [THRESHOLD]',
                        description: 'set threshold'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        THRESHOLD: {
                            type: ArgumentType.NUMBER,
                            menu: 'threshold',
                            defaultValue: '0.5'
                        }
                    }
                },
                {
                    message: 'Detection'
                },
                {
                    opcode: 'analyseImage',
                    text: formatMessage({
                        id: 'faceDetection.analyseImage',
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
                "---",
                {
                    opcode: 'getNumberFaces',
                    text: formatMessage({
                        id: 'faceDetection.getNumberFaces',
                        default: 'get # faces',
                        description: 'Get number of faces'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'getOption',
                    text: formatMessage({
                        id: 'faceDetection.getOption',
                        default: 'get expression of face [FACE]',
                        description: 'Get age'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'isExpression',
                    text: formatMessage({
                        id: 'faceDetection.isExpression',
                        default: 'is expression of face [FACE] [EXPRESSION]',
                        description: 'Get expression'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        },
                        EXPRESSION: {
                            type: ArgumentType.NUMBER,
                            menu: 'expression',
                            defaultValue: '4'
                        }
                    }
                },
                "---",
                {
                    opcode: 'boxPosition',
                    text: formatMessage({
                        id: 'faceDetection.boxPosition',
                        default: 'get [POSITION] of face [FACE]',
                        description: 'Get expression'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'facePosition',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'faceLandmarksF',
                    text: formatMessage({
                        id: 'faceDetection.faceLandmarksF',
                        default: 'get [POSITION] of [LANDMARK] of face [FACE]',
                        description: 'Get landmark'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        },
                        LANDMARK: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceLandmarks',
                            defaultValue: '1'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'landmarkPosition',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'landmarks',
                    text: formatMessage({
                        id: 'faceDetection.landmarks',
                        default: 'get [POSITION] of landmark [LANDMARK] of face [FACE]',
                        description: 'Get landmark'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        },
                        LANDMARK: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'landmarkPosition',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: 'Face Recognition: Training'
                },
                {
                    opcode: 'saveImage',
                    text: formatMessage({
                        id: 'faceDetection.saveImage',
                        default: 'add class [FACE] as [NAME] from [FEED]',
                        description: 'Get landmark'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        },
                        NAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Jarvis'
                        },
                        FEED: {
                            type: ArgumentType.NUMBER,
                            menu: 'feed',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'deleteImage',
                    text: formatMessage({
                        id: 'faceDetection.deleteImage',
                        default: 'reset class',
                        description: 'Delete faces'
                    }),
                    blockType: BlockType.COMMAND,
                },
                {
                    message: 'Face Recognition: Testing'
                },
                {
                    opcode: 'doFaceMatching',
                    text: formatMessage({
                        id: 'faceDetection.doFaceMatching',
                        default: 'do face matching on [FEED]',
                        description: 'Do face matching!'
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
                    opcode: 'isClassDetected',
                    text: formatMessage({
                        id: 'faceDetection.isClassDetected',
                        default: 'is [FACE] class detected',
                        description: 'class '
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'getFace',
                    text: formatMessage({
                        id: 'faceDetection.getFace',
                        default: 'get class of face [FACE] detected',
                        description: 'get class of face '
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        FACE: {
                            type: ArgumentType.NUMBER,
                            menu: 'faceNumber',
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
                faceNumber: {
                    acceptReporters: true,
                    items: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
                },
                expression: {
                    acceptReporters: true,
                    items: [
                        { text: 'anrgy', value: '1' },
                        { text: 'disgusted', value: '2' },
                        { text: 'fear', value: '3' },
                        { text: 'happy', value: '4' },
                        { text: 'neutral', value: '5' },
                        { text: 'sad', value: '6' },
                        { text: 'surprised', value: '7' }
                    ],
                },
                facePosition: {
                    items: [
                        { text: 'x position', value: '1' },
                        { text: 'y position', value: '2' },
                        { text: 'width', value: '3' },
                        { text: 'height', value: '4' },
                    ]
                },
                feed: {
                    items: [
                        { text: 'camera', value: '1' },
                        { text: 'stage', value: '2' },
                    ]
                },
                option: {
                    items: [
                        { text: 'age', value: '1' },
                        { text: 'gender', value: '2' },
                        { text: 'expression', value: '3' },
                    ]
                },
                landmarkPosition: {
                    items: [
                        { text: 'x position', value: '1' },
                        { text: 'y position', value: '2' },
                    ]
                },
                faceLandmarks: {
                    items: [
                        { text: 'nose', value: '31' },
                        { text: 'chin', value: '9' },
                        { text: 'mouth', value: '3' },
                        { text: 'left eye', value: '1' },
                        { text: 'right eye', value: '2' },
                        { text: 'left eyebrows', value: '20' },
                        { text: 'right eyebrows', value: '25' },
                    ]
                },
                drawBox: [
                    { text: 'show', value: '1' },
                    { text: 'hide', value: '2' }
                ],
                threshold: {
                    acceptReporters: true,
                    items: ['0.9', '0.8', '0.7', '0.6', '0.5', '0.4', '0.3', '0.2', '0.1']
                }
            }
        };
    }

    _drawMark(x1, y1, x2, y2, x3, y3, x4, y4, width, height, num) {
        let widthScale = 480 / width;
        let heightScale = 360 / height;

        x1 = x1 * widthScale - width / 2 * widthScale;
        x2 = x2 * widthScale - width / 2 * widthScale;
        x3 = x3 * widthScale - width / 2 * widthScale;
        x4 = x4 * widthScale - width / 2 * widthScale;
        y1 = height / 2 * heightScale - y1 * heightScale;
        y2 = height / 2 * heightScale - y2 * heightScale;
        y3 = height / 2 * heightScale - y3 * heightScale;
        y4 = height / 2 * heightScale - y4 * heightScale;

        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x1, y1, x2, y2);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x2, y2, x3, y3);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x4, y4, x3, y3);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes[num], x4, y4, x1, y1);
    }

    _clearMark() {
        this.runtime.renderer.penClear(this._penSkinId);
    }

    toggleStageVideoFeed(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const state = args.VIDEO_STATE;
        this.globalVideoState = args.VIDEO_STATE;

        this.runtime.ioDevices.video.setPreviewGhost(args.TRANSPARENCY);

        if (state === 'off') {
            drawOnStage = false;
            this._clearMark();
            this.runtime.ioDevices.video.disableVideo();
        } else {
            this.runtime.ioDevices.video.enableVideo();
            // Mirror if state is ON. Do not mirror if state is ON_FLIPPED.
            this.runtime.ioDevices.video.mirror = state === 'on';
        }
    }

    analyseImage(args, util) {
        let self = this;
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if(this.globalVideoState)
        {
            if (this.modelLoaded) {
                drawOnStage = true;
                if (args.FEED === '1') {
                    const translatePromise = new Promise(resolve => {
                        var canvas = document.createElement("canvas");
                        canvas.width = 480;
                        canvas.height = 360;
                        var ctx = canvas.getContext("2d");
                        const frame = this.runtime.ioDevices.video.getFrame({
                            format: Video.FORMAT_IMAGE_DATA,
                            dimensions: faceDetection.DIMENSIONS
                        });
                        if (frame === null) {
                            faces = [];
                            resolve('Camera not ready!');
                            return 'Camera not ready!';
                        }
                        ctx.putImageData(frame, 0, 0);
                        let image = document.createElement('img');
                        image.src = canvas.toDataURL("image/png");
    
                        image.onload = function () {
                            faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(function (fullFaceDescriptions) {
                                faces = fullFaceDescriptions;
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < faces.length; i++) {
                                        self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                            faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                            faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                            faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                            faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                    }
                                }
                                isStage = false;
                                resolve('Done');
                                return 'Done';
                            })
                                .catch(err => {
                                    faces = [];
                                    console.log(err);
                                    resolve('Error!');
                                    return 'Error!';
                                });
                        };
                    });
                    return translatePromise;
                }
                else if (args.FEED === '2') {
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let image = document.createElement('img');
                            image.onload = function () {
                                faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(function (fullFaceDescriptions) {
                                    faces = fullFaceDescriptions;
                                    isStage = true;
                                    if (drawOnStage) {
                                        self._clearMark();
                                        for (let i = 0; i < faces.length; i++) {
                                            self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                                faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                                faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                                faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                                faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
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
            } else {
                this.runtime.emit('MODEL_LOADING');
                return (new Promise(resolve => {
                    Promise.all([
                        faceapi.loadSsdMobilenetv1Model('static/models/faceDetection/ssdMobilenetv1ModelWeightsManifest.json'),
                        faceapi.loadFaceLandmarkModel('static/models/faceDetection/faceLandmark68ModelWeightsManifest.json'),
                        faceapi.loadFaceExpressionModel('static/models/faceDetection/faceExpressionModelWeightsManifest.json'),
                        faceapi.loadFaceRecognitionModel('static/models/faceDetection/faceRecognitionModelWeightsManifest.json')
                    ]).then(() => {
                        this.runtime.emit('MODEL_LOADING_FINISHED', true);
                        this.modelLoaded = true;
                        if (args.FEED === '1') {
                            var canvas = document.createElement("canvas");
                            canvas.width = 480;
                            canvas.height = 360;
                            var ctx = canvas.getContext("2d");
                            const frame = this.runtime.ioDevices.video.getFrame({
                                format: Video.FORMAT_IMAGE_DATA,
                                dimensions: faceDetection.DIMENSIONS
                            });
                            if (frame === null) {
                                faces = [];
                                resolve('Camera not ready!');
                                return 'Camera not ready!';
                            }
                            ctx.putImageData(frame, 0, 0);
                            let image = document.createElement('img');
                            image.src = canvas.toDataURL("image/png");
    
                            image.onload = function () {
                                faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(function (fullFaceDescriptions) {
                                    faces = fullFaceDescriptions;
                                    console.log(faces);
                                    isStage = false;
                                    if (drawOnStage) {
                                        self._clearMark();
                                        for (let i = 0; i < faces.length; i++) {
                                            self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                                faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                                faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                                faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                                faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                        }
                                    }
                                    resolve('Done');
                                    return 'Done';
                                }).catch(err => {
                                    faces = [];
                                    resolve('No faces detected');
                                    return 'No faces detected';
                                });
                            };
                        }
                        else if (args.FEED === '2') {
                            this.runtime.renderer.requestSnapshot(data => {
                                let image = document.createElement('img');
                                image.onload = function () {
                                    faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(function (fullFaceDescriptions) {
                                        console.log(faces);
                                        faces = fullFaceDescriptions;
                                        isStage = true;
                                        if (drawOnStage) {
                                            self._clearMark();
                                            for (let i = 0; i < faces.length; i++) {
                                                self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                                    faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                                    faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                                    faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                                    faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
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
                        resolve('NULL');
                    });
                }))
    
            }
        }
        else
        {
            console.log('Camera Not Ready!');
            return 'Camera Not Ready!';
        }

    }

    getNumberFaces(args, util) {
        return faces.length;
    }

    getOption(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            let expressionValue = 0;
            let expression;
            if (faces[parseInt(args.FACE, 10) - 1].expressions.angry > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.angry;
                expression = 'angry';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.disgusted > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.disgusted;
                expression = 'disgusted';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.fearful > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.fearful;
                expression = 'fear';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.happy > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.happy;
                expression = 'happy';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.neutral > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.neutral;
                expression = 'neutral';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.sad > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.sad;
                expression = 'sad';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.surprised > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.surprised;
                expression = 'surprised';
            }
            return expression;
        } else {
            return "NULL";
        }
    }

    isExpression(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            let expressionValue = 0;
            let expression;
            if (faces[parseInt(args.FACE, 10) - 1].expressions.angry > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.angry;
                expression = '1';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.disgusted > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.disgusted;
                expression = '2';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.fearful > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.fearful;
                expression = '3';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.happy > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.happy;
                expression = '4';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.neutral > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.neutral;
                expression = '5';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.sad > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.sad;
                expression = '6';
            }
            if (faces[parseInt(args.FACE, 10) - 1].expressions.surprised > expressionValue) {
                expressionValue = faces[parseInt(args.FACE, 10) - 1].expressions.surprised;
                expression = '7';
            }
            if (args.EXPRESSION === expression) {
                return true
            }
            else {
                return false
            }
        } else {
            return false;
        }
    }

    boxPosition(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            if (args.POSITION === "1") {
                let XPos = 240 * (faces[parseInt(args.FACE, 10) - 1].detection.box.left + faces[parseInt(args.FACE, 10) - 1].detection.box.right) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                XPos = XPos - 240;
                return XPos.toFixed(1);
            }
            else if (args.POSITION === "2") {
                let YPos = 180 * (faces[parseInt(args.FACE, 10) - 1].detection.box.top + faces[parseInt(args.FACE, 10) - 1].detection.box.bottom) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                YPos = 180 - YPos;
                return YPos.toFixed(1);
            }
            else if (args.POSITION === "3") {
                let Width = 480 * (faces[parseInt(args.FACE, 10) - 1].detection.box.width) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                return Width.toFixed(1);
            }
            else if (args.POSITION === "4") {
                let Height = 360 * (faces[parseInt(args.FACE, 10) - 1].detection.box.height) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                return Height.toFixed(1);
            }
        } else {
            return "NULL";
        }
    }

    landmarks(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            if (args.POSITION === "1") {
                let XPos = 480 * (faces[parseInt(args.FACE, 10) - 1].landmarks.positions[parseInt(args.LANDMARK, 10) - 1].x) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                XPos = XPos - 240;
                return XPos.toFixed(1);
            }
            else if (args.POSITION === "2") {
                let YPos = 360 * (faces[parseInt(args.FACE, 10) - 1].landmarks.positions[parseInt(args.LANDMARK, 10) - 1].y) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                YPos = 180 - YPos;
                return YPos.toFixed(1);
            }
        } else {
            return "NULL";
        }
    }

    faceLandmarksF(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            if (args.LANDMARK === "1") {
                if (args.POSITION === "1") {
                    let XPos = 480 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[36].x + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[39].x) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === "2") {
                    let YPos = 360 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[36].y + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[39].y) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.LANDMARK === "2") {
                if (args.POSITION === "1") {
                    let XPos = 480 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[42].x + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[45].x) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === "2") {
                    let YPos = 360 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[42].y + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[45].y) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.LANDMARK === "3") {
                if (args.POSITION === "1") {
                    let XPos = 480 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[48].x + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[54].x) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === "2") {
                    let YPos = 360 * ((faces[parseInt(args.FACE, 10) - 1].landmarks.positions[48].y + faces[parseInt(args.FACE, 10) - 1].landmarks.positions[54].y) / 2) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else {
                if (args.POSITION === "1") {
                    let XPos = 480 * (faces[parseInt(args.FACE, 10) - 1].landmarks.positions[parseInt(args.LANDMARK, 10) - 1].x) / faces[parseInt(args.FACE, 10) - 1].detection.imageWidth;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === "2") {
                    let YPos = 360 * (faces[parseInt(args.FACE, 10) - 1].landmarks.positions[parseInt(args.LANDMARK, 10) - 1].y) / faces[parseInt(args.FACE, 10) - 1].detection.imageHeight;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
        } else {
            return "NULL";
        }
    }

    saveImage(args, util) {
        let self = this;
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (args.FEED === '1') {
            drawOnStage = true;
            const translatePromise = new Promise(resolve => {
                var canvas = document.createElement("canvas");
                canvas.width = 480;
                canvas.height = 360;
                var ctx = canvas.getContext("2d");
                const frame = this.runtime.ioDevices.video.getFrame({
                    format: Video.FORMAT_IMAGE_DATA,
                    dimensions: faceDetection.DIMENSIONS
                });
                if (frame === null) {
                    faces = [];
                    resolve('Camera not ready!');
                    return 'Camera not ready!';
                }
                ctx.putImageData(frame, 0, 0);
                let image = document.createElement('img');
                image.src = canvas.toDataURL("image/png");

                image.onload = function () {
                    faceapi.detectSingleFace(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptor().then(
                        function (fullFaceDescription){
                            if (fullFaceDescription) {
                                faces = [fullFaceDescription];
                                console.log(faces, drawOnStage, self.fullFaceDescriptions);
                                self.fullFaceDescriptions[parseInt(args.FACE, 10) - 1].push(fullFaceDescription.descriptor);
                                console.log("1");
                                self.faceComparision[parseInt(args.FACE, 10) - 1] = new faceapi.LabeledFaceDescriptors(args.NAME, self.fullFaceDescriptions[parseInt(args.FACE, 10) - 1]);
                                console.log("2");
                                // self.drawBoundingBox({OPTION: 1}, util);
                                console.log("3");
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < faces.length; i++) {
                                        self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                            faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                            faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                            faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                            faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                    }
                                }
                                resolve('Done');
                                return 'Done';
                            }
                            else {
                                resolve('No face detected');
                                return 'No face detected';
                            }
                        }).catch(err => {
                            if(err.message == "Cannot read property 'length' of undefined")
                            {
                                resolve('No face detected');
                                return 'No face detected';                                
                            }
                            else{
                                resolve(err.message);
                                return err.message;
                            }
                        });
                };
            });
            return translatePromise;
        }
        else if (args.FEED === '2') {
            return new Promise(resolve => {
                this.runtime.renderer.requestSnapshot(data => {
                    let image = document.createElement('img');
                    image.onload = () => {
                        faceapi.detectSingleFace(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptor().then(
                            function (fullFaceDescription) {
                                if (fullFaceDescription) {
                                    faces = [fullFaceDescription];
                                    console.log(faces, drawOnStage, self.fullFaceDescriptions);
                                    self.fullFaceDescriptions[parseInt(args.FACE, 10) - 1].push(fullFaceDescription.descriptor);
                                    console.log("1");
                                    self.faceComparision[parseInt(args.FACE, 10) - 1] = new faceapi.LabeledFaceDescriptors(args.NAME, self.fullFaceDescriptions[parseInt(args.FACE, 10) - 1]);
                                    console.log("2");
                                    // self.drawBoundingBox({OPTION: 1}, util);
                                    console.log("3");
                                    self._clearMark();
                                    for (let i = 0; i < faces.length; i++) {
                                        self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                            faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                            faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                            faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                            faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                    }
                                    resolve('Done');
                                    return 'Done';
                                }
                                else {
                                    faces = [];
                                    resolve('No face detected');
                                    return 'No face detected';
                                }
                            });
                    };
                    image.setAttribute("src", data);
                });
            });
        }
    };

    doFaceMatching(args, util) {
        let self = this;
        if (args.FEED === '1') {
            const translatePromise = new Promise(resolve => {
                var canvas = document.createElement("canvas");
                canvas.width = 480;
                canvas.height = 360;
                var ctx = canvas.getContext("2d");
                const frame = this.runtime.ioDevices.video.getFrame({
                    format: Video.FORMAT_IMAGE_DATA,
                    dimensions: faceDetection.DIMENSIONS
                });
                if (frame === null) {
                    faces = [];
                    resolve('Camera not ready!');
                    return 'Camera not ready!';
                }
                ctx.putImageData(frame, 0, 0);
                let image = document.createElement('img');
                image.src = canvas.toDataURL("image/png");

                image.onload = () => {
                    faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().then(
                        (fullFaceDescriptions) => {
                            if (fullFaceDescriptions.length) {
                                isStage = false;
                                const faceMatcher = new faceapi.FaceMatcher(self.faceComparision);
                                faces = fullFaceDescriptions;
                                let i = 0;
                                fullFaceDescriptions.forEach(fd => {
                                    const bestMatch = faceMatcher.findBestMatch(fd.descriptor)
                                    faces[i].label = bestMatch.label;
                                    faces[i].distance = bestMatch.distance;
                                    i = i + 1;
                                })
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < faces.length; i++) {
                                        self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                            faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                            faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                            faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                            faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                    }
                                }
                                resolve('Done');
                                return 'Done';
                            }
                            else {
                                faces = [];
                                resolve('No face detected');
                                return 'No face detected';
                            }
                        }).catch(err => {
                            console.log(err);
                            if(err.message === "Cannot read property 'length' of undefined")
                            {
                                faces = [];
                                resolve('No face detected');
                                return 'No face detected';
                            }
                            else if(err.message == "FaceRecognizer.constructor - expected atleast one input")
                            {
                                resolve('Train Model before testing');
                                return 'Train Model before testing';
                            }
                            else
                            {
                                faces = [];
                                resolve(err.message);
                                return err.message;
                            }
                        });
                };
            });
            return translatePromise;
        }
        else if (args.FEED === '2') {
            return new Promise(resolve => {
                this.runtime.renderer.requestSnapshot(data => {
                    let image = document.createElement('img');
                    image.onload = function () {
                        faceapi.detectAllFaces(image, new faceapi.SsdMobilenetv1Options({ minConfidence: faceThreshold })).withFaceLandmarks(false).withFaceDescriptors().withFaceExpressions().then(
                            (fullFaceDescriptions) => {
                                if (fullFaceDescriptions.length) {
                                    isStage = true;
                                    const faceMatcher = new faceapi.FaceMatcher(self.faceComparision);
                                    faces = fullFaceDescriptions;
                                    let i = 0;
                                    fullFaceDescriptions.forEach(fd => {
                                        const bestMatch = faceMatcher.findBestMatch(fd.descriptor)
                                        faces[i].label = bestMatch.label;
                                        faces[i].distance = bestMatch.distance;
                                        i = i + 1;
                                    })
                                    if (drawOnStage) {
                                        self._clearMark();
                                        for (let i = 0; i < faces.length; i++) {
                                            self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                                                faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                                                faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                                                faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                                                faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
                                        }
                                    }
                                    resolve('Done');
                                    return 'Done';
                                }
                                else {
                                    faces = [];
                                    resolve('No Face Detected');
                                    return 'No Face Detected';
                                }
                            })
                    };
                    image.setAttribute("src", data);
                });
            });
        }
    }

    deleteImage(args, util) {
        this.faceComparision = [];
        this.fullFaceDescriptions = []
        for(let i = 0; i < 10; i++)
        {
            this.fullFaceDescriptions.push([]);
        }
        faces = [];
    }

    isClassDetected(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.faceComparision[parseInt(args.FACE, 10) - 1]) {
            let idClass = 0;
            let i = 0;
            faces.forEach(face => {
                i = i + 1;
                if (face.label === this.faceComparision[parseInt(args.FACE, 10) - 1].label) {
                    idClass = i;
                }
            })
            if (idClass === 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    getFace(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (faces[parseInt(args.FACE, 10) - 1]) {
            return faces[parseInt(args.FACE, 10) - 1].label;
        }
        return 'unknown';
    }

    drawBoundingBox(args, util) {
        let self = this;
        if (args.OPTION === "1") {
            drawOnStage = true;
            this._clearMark();
            for (let i = 0; i < faces.length; i++) {
                self._drawMark(faces[i].detection.box.topLeft.x, faces[i].detection.box.topLeft.y,
                    faces[i].detection.box.topRight.x, faces[i].detection.box.topRight.y,
                    faces[i].detection.box.bottomRight.x, faces[i].detection.box.bottomRight.y,
                    faces[i].detection.box.bottomLeft.x, faces[i].detection.box.bottomLeft.y,
                    faces[i].detection.imageDims.width, faces[i].detection.imageDims.height, i);
            }
        }
        else {
            drawOnStage = false;
            this._clearMark();
        }
    }

    toJSON() {
        var json = [];
        const faceKeys = Object.keys(this.faceComparision);
        for (let faceIndex = 0; faceIndex < faceKeys.length; faceIndex++) {
            if (typeof this.faceComparision[faceKeys[faceIndex]].toJSON === 'function')
                json[faceKeys[faceIndex]] = this.faceComparision[faceKeys[faceIndex]].toJSON();
        }
        return json;
    }

    setThreshold(args, util) {
        faceThreshold = parseFloat(args.THRESHOLD);
    }
}

module.exports = faceDetection;
