const ArgumentType = require('../../extension-support/argument-type');
const BlockType = require('../../extension-support/block-type');
const log = require('../../util/log');
const formatMessage = require('format-message');
const Cast = require('../../util/cast');


class ImageOverlay {
    constructor (runtime) {
        this.runtime = runtime;
        this._skinId = -1;
    }

    set globalImageState (state) {
        const stage = this.runtime.getTargetForStage();
        console.log(stage);
        if(stage) {
            stage.videoState = state;
        }
        return state;
    }

    getInfo() {
        return {
            id: 'imageOverlay',
            name: 'Image Overlay',
            blocks: [
                {
                    opcode: 'setImage',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'imgOverlay.setImage',
                        default: 'Set Image as [IMAGE]',
                        description: 'Sets the image'
                    }),
                    arguments: {
                        IMAGE: {
                            type: ArgumentType.IMAGE_SELECT
                        }
                    }
                },

                {
                    opcode: 'setTransparency',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'imgOverlay.setTransparency',
                        default: 'Set transparency to [NUMBER]',
                        description: 'Sets the transparency of the image'
                    }),
                    arguments: {
                        NUMBER: {
                            type: ArgumentType.NUMBER,
                            defaultValue: 50
                        }
                    }
                },

                {
                    opcode: "detectEdges",
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'imgOverlay.detectEdge',
                        default: 'Edge detection',
                        description: "Detecting edges in the image"
                    })
                },
                
                {
                    opcode: 'clearImage',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'imgOverlay.clearImage',
                        default: 'Remove Image',
                        descreption: 'Remove the image'
                    })
                }
            ]
        };
    }

    setImage (args) {
        const image64 = args.IMAGE;
        if (!image64) {
            return;
        } else {
            this.runtime.imageOverlay.setImage(image64);
            log.log("Image set");
        }
    }

    setTransparency (args) {
        const transparency = Cast.toNumber(args.NUMBER);
        this.runtime.imageOverlay.setTransparency(transparency);
        log.log(`Transparency adjusted ${transparency}`);
    }

    detectEdges () {
        this.runtime.imageOverlay.detectEdges();
        log.log("Edges detected");
    }

    clearImage () {
        log.log("Image removed")
        this.runtime.imageOverlay.clearImage();
    }
}

module.exports = ImageOverlay;