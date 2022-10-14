const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require( "../../../../local_modules/format-message/index.js");
const Runtime = require('../../../engine/runtime');
const cocoSsd = require( "../../../../local_modules/@tensorflow-models/coco-ssd/dist/index.js");
const Video = require('../../../io/video');
const StageLayering = require('../../../engine/stage-layering');

const { ModelLoadingType } = require('../../../util/board-config');

objectDetected = [];

let isStage = false;
let stageWidth = 480;
let stageHeight = 360;

let netModel2;

let drawOnStage = false;

let MakerAttributes = [];
for (let i = 0; i < 20; i++) {
    MakerAttributes[i] = {
        color4f: [Math.random(), Math.random(), Math.random(), 0.7],
        diameter: 3
    };
}

let minScore = 0.5;
let minNumBoxes = 50;

let baseModel = 'lite_mobilenet_v2';
let baseURL = 'static/models/objectDetection/model.json';

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNmZWYyNzA7fS5jbHMtMntmaWxsOiNmYmIwNDA7fS5jbHMtM3tmaWxsOiMwNDA1MDQ7fS5jbHMtNHtmaWxsOiNmZmY7fS5jbHMtNSwuY2xzLTd7ZmlsbDpub25lO3N0cm9rZTojMWExNTE3O3N0cm9rZS1taXRlcmxpbWl0OjEwO30uY2xzLTV7c3Ryb2tlLXdpZHRoOjAuNXB4O30uY2xzLTZ7ZmlsbDojMWExNTE3O30uY2xzLTd7c3Ryb2tlLXdpZHRoOjAuMjlweDt9LmNscy04e2ZpbGw6I2U4NmFhODt9PC9zdHlsZT48L2RlZnM+PHRpdGxlPk9iamVjdCBEZXRlY3Rpb248L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtMSIgZD0iTTYuNzMsMTYuNzhhOC43OCw4Ljc4LDAsMCwwLTMsMi45NUE5LjM1LDkuMzUsMCwwLDAsMi41LDI1LjA3YzAuMzYsNC4yNiwzLjg5LDYuODYsNC44Niw3LjU3LDMuOCwyLjgsNy45NCwyLjY3LDEyLjc4LDIuNTNDMjYsMzUsMzEuMjgsMzQuODQsMzQuNDIsMzAuODZhMTEuMTksMTEuMTksMCwwLDAsMi4xOS03LjUxLDExLjc4LDExLjc4LDAsMCwwLTQuNjktOC4yOWMwLjg4LTQuOTMuNjItOC43Mi0uOS05LjMtMS4xNi0uNDUtMywxLTMuODgsMS43MWExMC4wNywxMC4wNywwLDAsMC0yLjczLDMuMzcsMTUuMjcsMTUuMjcsMCwwLDAtMTAuNjQuOTRBMTAsMTAsMCwwLDAsMTAuODIsOC40QzkuODgsNy43Myw4LjMzLDYuNjIsNy4yNSw3LjE2Yy0wLjkxLjQ1LTEsMS44My0xLjE3LDMuNDJBMTcuMzMsMTcuMzMsMCwwLDAsNi43MywxNi43OFoiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTIiIGN4PSIxMS44OCIgY3k9IjIzLjI2IiByeD0iMi45NSIgcnk9IjMuMzUiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTIiIGN4PSIyNy4yOCIgY3k9IjIyLjYiIHJ4PSIyLjk1IiByeT0iMy4zNSIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtMyIgY3g9IjEyLjQ0IiBjeT0iMjMuMjIiIHJ4PSIyLjM4IiByeT0iMi45Ii8+PGVsbGlwc2UgY2xhc3M9ImNscy00IiBjeD0iMTEuMjciIGN5PSIyMi42IiByeD0iMC41OCIgcnk9IjAuNzMiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTMiIGN4PSIyNi42NCIgY3k9IjIyLjU2IiByeD0iMi4zOCIgcnk9IjIuOSIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtNCIgY3g9IjI1LjQ2IiBjeT0iMjEuOTUiIHJ4PSIwLjU4IiByeT0iMC43MyIvPjxsaW5lIGNsYXNzPSJjbHMtNSIgeDE9IjMxLjk0IiB5MT0iMjUuNjYiIHgyPSIzNC43NCIgeTI9IjI0LjI5Ii8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iMzIuMDciIHkxPSIyNi45IiB4Mj0iMzQuNjgiIHkyPSIyNi45Ii8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iMzIuMTQiIHkxPSIyOC4zMyIgeDI9IjM0LjI5IiB5Mj0iMjguOTgiLz48bGluZSBjbGFzcz0iY2xzLTUiIHgxPSI4LjEiIHkxPSIyNi42OCIgeDI9IjUuMyIgeTI9IjI1LjMyIi8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iNy45NyIgeTE9IjI3LjkyIiB4Mj0iNS4zNyIgeTI9IjI3LjkyIi8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iNy45MSIgeTE9IjI5LjM1IiB4Mj0iNS43NiIgeTI9IjMwIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMTkuMzIsMjcuNDlsMiwwYTAuMjksMC4yOSwwLDAsMSwuMi40OWwtMSwxYTAuMjksMC4yOSwwLDAsMS0uNDEsMGwtMS0xQTAuMjksMC4yOSwwLDAsMSwxOS4zMiwyNy40OVoiLz48bGluZSBjbGFzcz0iY2xzLTciIHgxPSIyMC4yOSIgeTE9IjMwLjQyIiB4Mj0iMjAuMjkiIHkyPSIyOC40NCIvPjxwYXRoIGNsYXNzPSJjbHMtMiIgZD0iTTguNDIsMTZjMC4zNywwLjA4LjUyLS42NCwxLjU1LTEuMzcsMS4yNC0uODgsMi4wNi0wLjU3LDIuMzItMS4xNiwwLjMzLS43NS0wLjczLTEuODgtMS45My0zLjE2LTEtMS4wOC0xLjU2LTEuNjYtMi0xLjUxLTAuNjUuMjItLjYzLDEuODQtMC42LDMuNjlDNy43OCwxMy43OCw3LjgxLDE1Ljg0LDguNDIsMTZaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMzAuMzYsMTQuMjZjLTAuMzcuMDgtLjUyLTAuNjQtMS41NS0xLjM3LTEuMjQtLjg4LTIuMDYtMC41Ny0yLjMyLTEuMTYtMC4zMy0uNzUuNzMtMS44OCwxLjkzLTMuMTYsMS0xLjA4LDEuNTYtMS42NiwyLTEuNTEsMC42NSwwLjIyLjYzLDEuODQsMC42LDMuNjlDMzEsMTIuMDcsMzEsMTQuMTMsMzAuMzYsMTQuMjZaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMjAuMzUsMTAuMjlsLTEsNC43Ny0xLjExLTQuNjNjMC4zLDAsLjYyLTAuMDgsMS0wLjFTMjAsMTAuMjksMjAuMzUsMTAuMjlaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMjMuNjYsMTAuNjVMMjAuNzIsMTdsMS4xLTYuNjMsMC43OSwwLjA5QzIzLDEwLjUxLDIzLjMzLDEwLjU3LDIzLjY2LDEwLjY1WiIvPjxwYXRoIGNsYXNzPSJjbHMtMiIgZD0iTTE2Ljc0LDEwLjcxbDEuMTYsNy0zLTYuNGMwLjI4LS4xMS41OS0wLjIxLDAuOTEtMC4zMVoiLz48cGF0aCBjbGFzcz0iY2xzLTgiIGQ9Ik0xOS4yLDMxLjY2YTEuNiwxLjYsMCwwLDAsLjI1LDEsMS4yMiwxLjIyLDAsMCwwLC44MS41OSwxLjIsMS4yLDAsMCwwLDEtLjM5LDEuNDIsMS40MiwwLDAsMCwuMjgtMS4yNiwyLDIsMCwwLDEtMS4yMy0xLjE4Ii8+PHBhdGggY2xhc3M9ImNscy03IiBkPSJNMTYuNjEsMzAuNTRhMiwyLDAsMCwwLDIuMTcsMS4yLDIsMiwwLDAsMCwxLjUzLTEuMzgsMiwyLDAsMCwwLC42NiwxLDIuMDgsMi4wOCwwLDAsMCwxLjY0LjM1LDIuNDIsMi40MiwwLDAsMCwxLjQxLS44Ii8+PC9zdmc+';

const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNmZWYyNzA7fS5jbHMtMntmaWxsOiNmYmIwNDA7fS5jbHMtM3tmaWxsOiMwNDA1MDQ7fS5jbHMtNHtmaWxsOiNmZmY7fS5jbHMtNSwuY2xzLTd7ZmlsbDpub25lO3N0cm9rZTojMWExNTE3O3N0cm9rZS1taXRlcmxpbWl0OjEwO30uY2xzLTV7c3Ryb2tlLXdpZHRoOjAuNXB4O30uY2xzLTZ7ZmlsbDojMWExNTE3O30uY2xzLTd7c3Ryb2tlLXdpZHRoOjAuMjlweDt9LmNscy04e2ZpbGw6I2U4NmFhODt9PC9zdHlsZT48L2RlZnM+PHRpdGxlPk9iamVjdCBEZXRlY3Rpb248L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtMSIgZD0iTTYuNzMsMTYuNzhhOC43OCw4Ljc4LDAsMCwwLTMsMi45NUE5LjM1LDkuMzUsMCwwLDAsMi41LDI1LjA3YzAuMzYsNC4yNiwzLjg5LDYuODYsNC44Niw3LjU3LDMuOCwyLjgsNy45NCwyLjY3LDEyLjc4LDIuNTNDMjYsMzUsMzEuMjgsMzQuODQsMzQuNDIsMzAuODZhMTEuMTksMTEuMTksMCwwLDAsMi4xOS03LjUxLDExLjc4LDExLjc4LDAsMCwwLTQuNjktOC4yOWMwLjg4LTQuOTMuNjItOC43Mi0uOS05LjMtMS4xNi0uNDUtMywxLTMuODgsMS43MWExMC4wNywxMC4wNywwLDAsMC0yLjczLDMuMzcsMTUuMjcsMTUuMjcsMCwwLDAtMTAuNjQuOTRBMTAsMTAsMCwwLDAsMTAuODIsOC40QzkuODgsNy43Myw4LjMzLDYuNjIsNy4yNSw3LjE2Yy0wLjkxLjQ1LTEsMS44My0xLjE3LDMuNDJBMTcuMzMsMTcuMzMsMCwwLDAsNi43MywxNi43OFoiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTIiIGN4PSIxMS44OCIgY3k9IjIzLjI2IiByeD0iMi45NSIgcnk9IjMuMzUiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTIiIGN4PSIyNy4yOCIgY3k9IjIyLjYiIHJ4PSIyLjk1IiByeT0iMy4zNSIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtMyIgY3g9IjEyLjQ0IiBjeT0iMjMuMjIiIHJ4PSIyLjM4IiByeT0iMi45Ii8+PGVsbGlwc2UgY2xhc3M9ImNscy00IiBjeD0iMTEuMjciIGN5PSIyMi42IiByeD0iMC41OCIgcnk9IjAuNzMiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTMiIGN4PSIyNi42NCIgY3k9IjIyLjU2IiByeD0iMi4zOCIgcnk9IjIuOSIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtNCIgY3g9IjI1LjQ2IiBjeT0iMjEuOTUiIHJ4PSIwLjU4IiByeT0iMC43MyIvPjxsaW5lIGNsYXNzPSJjbHMtNSIgeDE9IjMxLjk0IiB5MT0iMjUuNjYiIHgyPSIzNC43NCIgeTI9IjI0LjI5Ii8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iMzIuMDciIHkxPSIyNi45IiB4Mj0iMzQuNjgiIHkyPSIyNi45Ii8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iMzIuMTQiIHkxPSIyOC4zMyIgeDI9IjM0LjI5IiB5Mj0iMjguOTgiLz48bGluZSBjbGFzcz0iY2xzLTUiIHgxPSI4LjEiIHkxPSIyNi42OCIgeDI9IjUuMyIgeTI9IjI1LjMyIi8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iNy45NyIgeTE9IjI3LjkyIiB4Mj0iNS4zNyIgeTI9IjI3LjkyIi8+PGxpbmUgY2xhc3M9ImNscy01IiB4MT0iNy45MSIgeTE9IjI5LjM1IiB4Mj0iNS43NiIgeTI9IjMwIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMTkuMzIsMjcuNDlsMiwwYTAuMjksMC4yOSwwLDAsMSwuMi40OWwtMSwxYTAuMjksMC4yOSwwLDAsMS0uNDEsMGwtMS0xQTAuMjksMC4yOSwwLDAsMSwxOS4zMiwyNy40OVoiLz48bGluZSBjbGFzcz0iY2xzLTciIHgxPSIyMC4yOSIgeTE9IjMwLjQyIiB4Mj0iMjAuMjkiIHkyPSIyOC40NCIvPjxwYXRoIGNsYXNzPSJjbHMtMiIgZD0iTTguNDIsMTZjMC4zNywwLjA4LjUyLS42NCwxLjU1LTEuMzcsMS4yNC0uODgsMi4wNi0wLjU3LDIuMzItMS4xNiwwLjMzLS43NS0wLjczLTEuODgtMS45My0zLjE2LTEtMS4wOC0xLjU2LTEuNjYtMi0xLjUxLTAuNjUuMjItLjYzLDEuODQtMC42LDMuNjlDNy43OCwxMy43OCw3LjgxLDE1Ljg0LDguNDIsMTZaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMzAuMzYsMTQuMjZjLTAuMzcuMDgtLjUyLTAuNjQtMS41NS0xLjM3LTEuMjQtLjg4LTIuMDYtMC41Ny0yLjMyLTEuMTYtMC4zMy0uNzUuNzMtMS44OCwxLjkzLTMuMTYsMS0xLjA4LDEuNTYtMS42NiwyLTEuNTEsMC42NSwwLjIyLjYzLDEuODQsMC42LDMuNjlDMzEsMTIuMDcsMzEsMTQuMTMsMzAuMzYsMTQuMjZaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMjAuMzUsMTAuMjlsLTEsNC43Ny0xLjExLTQuNjNjMC4zLDAsLjYyLTAuMDgsMS0wLjFTMjAsMTAuMjksMjAuMzUsMTAuMjlaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMjMuNjYsMTAuNjVMMjAuNzIsMTdsMS4xLTYuNjMsMC43OSwwLjA5QzIzLDEwLjUxLDIzLjMzLDEwLjU3LDIzLjY2LDEwLjY1WiIvPjxwYXRoIGNsYXNzPSJjbHMtMiIgZD0iTTE2Ljc0LDEwLjcxbDEuMTYsNy0zLTYuNGMwLjI4LS4xMS41OS0wLjIxLDAuOTEtMC4zMVoiLz48cGF0aCBjbGFzcz0iY2xzLTgiIGQ9Ik0xOS4yLDMxLjY2YTEuNiwxLjYsMCwwLDAsLjI1LDEsMS4yMiwxLjIyLDAsMCwwLC44MS41OSwxLjIsMS4yLDAsMCwwLDEtLjM5LDEuNDIsMS40MiwwLDAsMCwuMjgtMS4yNiwyLDIsMCwwLDEtMS4yMy0xLjE4Ii8+PHBhdGggY2xhc3M9ImNscy03IiBkPSJNMTYuNjEsMzAuNTRhMiwyLDAsMCwwLDIuMTcsMS4yLDIsMiwwLDAsMCwxLjUzLTEuMzgsMiwyLDAsMCwwLC42NiwxLDIuMDgsMi4wOCwwLDAsMCwxLjY0LjM1LDIuNDIsMi40MiwwLDAsMCwxLjQxLS44Ii8+PC9zdmc+';

class objectDetection {
    constructor(runtime) {
        this.runtime = runtime;
        let self = this;
        this.modelLoaded = false;

        // To identify the MODEL_FILES_LOCATED from multiple listeners in this class
        this.modelLoadingId = "_" + ModelLoadingType.objectDetection;
        this.runtime.emitModelLoading(ModelLoadingType.objectDetection, this.modelLoadingId);

        this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (model, isAbort) => {
            console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
            if (isAbort) {
                console.log("Error Message: isAbort: True");
                return self.runtime.emit('MODEL_LOADING_FINISHED', false);
            }
            var netModel = new Promise(resolve => {
                cocoSsd.load({
                    base: 'lite_mobilenet_v2',
                    modelUrl: `${modelPath}/model.json`
                }).then(function (net) {
                    netModel2 = net;
                    self.runtime.renderer.requestSnapshot(data => {
                        let image = document.createElement('img');
                        image.onload = function () {
                            netModel2.detect(image, minNumBoxes, minScore).then(function (prediction) {
                                isStage = true;
                                stageWidth = image.width;
                                stageHeight = image.height;
                                objectDetected = prediction;
                                runtime.emit('MODEL_LOADING_FINISHED', true);
                                self.modelLoaded = true;
                                resolve('Done');
                                return "Done";
                            })
                        };
                        image.setAttribute("src", data);
                    });
                })
                    .catch(err => {
                        console.log("Error Message:", err.message);
                        this.runtime.emit('MODEL_LOADING_FINISHED', false);
                    });
            });
        });

        this.globalVideoState = 'off';
        this.runtime.ioDevices.video.disableVideo();
        this.extensionName = 'Object Detection';

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
            id: 'objectDetection',
            name: 'Object Detection',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'objectDetection.toggleStageVideoFeed',
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
                        id: 'objectDetection.drawBoundingBox',
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
                            defaultValue: '0.5'
                        }
                    }
                },
                "---",
                {
                    opcode: 'analyseImage',
                    text: formatMessage({
                        id: 'objectDetection.analyseImage',
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
                    opcode: 'getObjectCount',
                    text: formatMessage({
                        id: 'objectDetection.getObjectCount',
                        default: 'get # of objects',
                        description: 'Get # of people'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'getDetails',
                    text: formatMessage({
                        id: 'objectDetection.getDetails',
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
                    opcode: 'isDetected',
                    text: formatMessage({
                        id: 'objectDetection.isDetected',
                        default: 'is [OBJECT] detected?',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objects',
                            defaultValue: 'person'
                        },
                    }
                },
                {
                    opcode: 'getNoDetected',
                    text: formatMessage({
                        id: 'objectDetection.getNoDetected',
                        default: 'get number of [OBJECT] detected?',
                        description: 'is object detected'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.STRING,
                            menu: 'objects',
                            defaultValue: 'person'
                        },
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
                    items: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
                },
                objectOption: [
                    { text: 'class', value: '0' },
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
                threshold: {
                    acceptReporters: true,
                    items: ['0.95', '0.9', '0.85', '0.8', '0.75', '0.7', '0.6', '0.5', '0.4', '0.3']
                },
                objects: [
                    { text: 'person', value: "person" },
                    { text: 'bicycle', value: "bicycle" },
                    { text: 'car', value: "car" },
                    { text: 'motorcycle', value: "motorcycle" },
                    { text: 'airplane', value: "airplane" },
                    { text: 'bus', value: "bus" },
                    { text: 'train', value: "train" },
                    { text: 'truck', value: "truck" },
                    { text: 'boat', value: "boat" },
                    { text: 'traffic light', value: "traffic light" },
                    { text: 'fire hydrant', value: "fire hydrant" },
                    { text: 'stop sign', value: "stop sign" },
                    { text: 'parking meter', value: "parking meter" },
                    { text: 'bench', value: "bench" },
                    { text: 'bird', value: "bird" },
                    { text: 'cat', value: "cat" },
                    { text: 'dog', value: "dog" },
                    { text: 'horse', value: "horse" },
                    { text: 'sheep', value: "sheep" },
                    { text: 'cow', value: "cow" },
                    { text: 'elephant', value: "elephant" },
                    { text: 'bear', value: "bear" },
                    { text: 'zebra', value: "zebra" },
                    { text: 'giraffe', value: "giraffe" },
                    { text: 'backpack', value: "backpack" },
                    { text: 'umbrella', value: "umbrella" },
                    { text: 'handbag', value: "handbag" },
                    { text: 'tie', value: "tie" },
                    { text: 'suitcase', value: "suitcase" },
                    { text: 'frisbee', value: "frisbee" },
                    { text: 'skis', value: "skis" },
                    { text: 'snowboard', value: "snowboard" },
                    { text: 'sports ball', value: "sports ball" },
                    { text: 'kite', value: "kite" },
                    { text: 'baseball bat', value: "baseball bat" },
                    { text: 'baseball glove', value: "baseball glove" },
                    { text: 'skateboard', value: "skateboard" },
                    { text: 'surfboard', value: "surfboard" },
                    { text: 'tennis racket', value: "tennis racket" },
                    { text: 'bottle', value: "bottle" },
                    { text: 'wine glass', value: "wine glass" },
                    { text: 'cup', value: "cup" },
                    { text: 'fork', value: "fork" },
                    { text: 'knife', value: "knife" },
                    { text: 'spoon', value: "spoon" },
                    { text: 'bowl', value: "bowl" },
                    { text: 'banana', value: "banana" },
                    { text: 'apple', value: "apple" },
                    { text: 'sandwich', value: "sandwich" },
                    { text: 'orange', value: "orange" },
                    { text: 'broccoli', value: "broccoli" },
                    { text: 'carrot', value: "carrot" },
                    { text: 'hot dog', value: "hot dog" },
                    { text: 'pizza', value: "pizza" },
                    { text: 'donut', value: "donut" },
                    { text: 'cake', value: "cake" },
                    { text: 'chair', value: "chair" },
                    { text: 'couch', value: "couch" },
                    { text: 'potted plant', value: "potted plant" },
                    { text: 'bed', value: "bed" },
                    { text: 'dining table', value: "dining table" },
                    { text: 'toilet', value: "toilet" },
                    { text: 'tv', value: "tv" },
                    { text: 'laptop', value: "laptop" },
                    { text: 'mouse', value: "mouse" },
                    { text: 'remote', value: "remote" },
                    { text: 'keyboard', value: "keyboard" },
                    { text: 'cell phone', value: "cell phone" },
                    { text: 'microwave', value: "microwave" },
                    { text: 'oven', value: "oven" },
                    { text: 'toaster', value: "toaster" },
                    { text: 'sink', value: "sink" },
                    { text: 'refrigerator', value: "refrigerator" },
                    { text: 'book', value: "book" },
                    { text: 'clock', value: "clock" },
                    { text: 'vase', value: "vase" },
                    { text: 'scissors', value: "scissors" },
                    { text: 'teddy bear', value: "teddy bear" },
                    { text: 'hair drier', value: "hair drier" },
                    { text: 'toothbrush', value: "toothbrush" }
                ]
            }
        };
    }

    toggleStageVideoFeed(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
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
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.modelLoaded) {
            if (args.FEED === '1') {
                const translatePromise = new Promise(resolve => {
                    const frame = this.runtime.ioDevices.video.getFrame({
                        format: Video.FORMAT_IMAGE_DATA,
                        dimensions: objectDetection.DIMENSIONS
                    });
                    netModel2.detect(frame, minNumBoxes, minScore).then(function (prediction) {
                        isStage = false;
                        objectDetected = prediction;
                        if (drawOnStage) {
                            self._clearMark();
                            for (let i = 0; i < objectDetected.length; i++) {
                                self._drawMark(objectDetected[i].bbox[0], objectDetected[i].bbox[1],
                                    objectDetected[i].bbox[2], objectDetected[i].bbox[3],
                                    objectDetection.DIMENSIONS[0], objectDetection.DIMENSIONS[1], i);
                            }
                        }
                        resolve('Done');
                        return 'Done';
                    })
                        .catch(err => {
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
                            netModel2.detect(image, minNumBoxes, minScore).then(function (prediction) {
                                isStage = true;
                                stageWidth = image.width;
                                stageHeight = image.height;
                                objectDetected = prediction;
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < objectDetected.length; i++) {
                                        self._drawMark(objectDetected[i].bbox[0], objectDetected[i].bbox[1],
                                            objectDetected[i].bbox[2], objectDetected[i].bbox[3],
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
            this.modelLoadingId = "_" + ModelLoadingType.objectDetection + "_1";
            this.runtime.emitModelLoading(ModelLoadingType.objectDetection, this.modelLoadingId);
            return new Promise((resolve, reject) => {
                this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
                    if (isAbort) {
                        reject();
                        return self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    }
                    cocoSsd.load({
                        base: 'lite_mobilenet_v2',
                        modelUrl: `${modelPath}/model.json`
                    }).then(function (net) {
                        netModel2 = net;
                        self.runtime.emit('MODEL_LOADING_FINISHED', true);
                        self.modelLoaded = true;
                        if (args.FEED === '1') {
                            const frame = this.runtime.ioDevices.video.getFrame({
                                format: Video.FORMAT_IMAGE_DATA,
                                dimensions: objectDetection.DIMENSIONS
                            });
                            netModel2.detect(frame, minNumBoxes, minScore).then(function (prediction) {
                                isStage = false;
                                objectDetected = prediction;
                                if (drawOnStage) {
                                    self._clearMark();
                                    for (let i = 0; i < objectDetected.length; i++) {
                                        self._drawMark(objectDetected[i].bbox[0], objectDetected[i].bbox[1],
                                            objectDetected[i].bbox[2], objectDetected[i].bbox[3],
                                            objectDetection.DIMENSIONS[0], objectDetection.DIMENSIONS[1], i);
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
                                    netModel2.detect(image, minNumBoxes, minScore).then(function (prediction) {
                                        isStage = true;
                                        stageWidth = image.width;
                                        stageHeight = image.height;
                                        objectDetected = prediction;
                                        if (drawOnStage) {
                                            self._clearMark();
                                            for (let i = 0; i < objectDetected.length; i++) {
                                                self._drawMark(objectDetected[i].bbox[0], objectDetected[i].bbox[1],
                                                    objectDetected[i].bbox[2], objectDetected[i].bbox[3],
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
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (objectDetected[parseInt(args.OBJECT, 10) - 1] && objectDetected[parseInt(args.OBJECT, 10) - 1].score > 0.3) {
            if (args.OPTION === "0") {
                return objectDetected[parseInt(args.OBJECT, 10) - 1].class;
            }
            else if (args.OPTION === "1") {
                if (!isStage) {
                    let XPos = objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[0] + objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[2] / 2;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else {
                    let XPos = 480 * (objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[0] + objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[2] / 2) / stageWidth;
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
            }
            else if (args.OPTION === "2") {
                if (!isStage) {
                    let YPos = objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[1] + objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[3] / 2;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
                else {
                    let YPos = 360 * (objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[1] + objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[3] / 2) / stageHeight;
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.OPTION === "3") {
                if (!isStage) {
                    let Width = objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[2];
                    return Width.toFixed(1);
                }
                else {
                    let Width = 480 * (objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[2]) / stageWidth;
                    return Width.toFixed(1);
                }
            }
            else if (args.OPTION === "4") {
                if (!isStage) {
                    let Height = objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[3];
                    return Height.toFixed(1);
                }
                else {
                    let Height = 360 * (objectDetected[parseInt(args.OBJECT, 10) - 1].bbox[3]) / stageHeight;
                    return Height.toFixed(1);
                }
            }
            else if (args.OPTION === "5") {
                let Confidence = (objectDetected[parseInt(args.OBJECT, 10) - 1].score);
                return Confidence.toFixed(2);
            }
        } else {
            return "NULL";
        }
    }

    drawBoundingBox(args, util) {
        let self = this;
        if (args.OPTION === "1") {
            drawOnStage = true;
            self._clearMark();
            for (let i = 0; i < objectDetected.length; i++) {
                self._drawMark(objectDetected[i].bbox[0], objectDetected[i].bbox[1],
                    objectDetected[i].bbox[2], objectDetected[i].bbox[3],
                    objectDetection.DIMENSIONS[0], objectDetection.DIMENSIONS[1], i);
            }
        }
        else {
            drawOnStage = false;
            this._clearMark();
        }
    }

    setThreshold(args, util) {
        minScore = parseFloat(args.THRESHOLD);
    }

    isDetected(args, util) {
        let isObjectDetected = false;
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].class === args.OBJECT) {
                isObjectDetected = true;
            }
        }
        return isObjectDetected;
    }

    getNoDetected(args, util) {
        let objectCount = 0;
        for (let i = 0; i < objectDetected.length; i++) {
            if (objectDetected[i].class === args.OBJECT) {
                objectCount = objectCount + 1;
            }
        }
        return objectCount;
    }

}

module.exports = objectDetection;