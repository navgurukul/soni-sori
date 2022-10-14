const StageLayering = require( "../engine/stage-layering.js");
const { tidy } = require( "../../local_modules/@tensorflow/tfjs/dist/tf.esm.js");

class ImageOverlay {
    constructor(runtime) {
        this.runtime = runtime;
        this.provider = null;
        this._skinId = -1;
        this._skin = null;
        this._drawable = -1;
        this._ghost = 0;
        this._forceTransparentView = false;
        this._image = null;
        this._image64 = null;
    }

    static get FORMAT_IMAGE_DATA () {
        return 'image-data';
    }

    static get FORMAT_CANVAS () {
        return 'canvas';
    }

    static get DIMENSIONS () {
        return [480, 360];
    }

    setProvider (provider) {
        this.provider = provider;
    }

    setImage (image64) {
        if (!this.provider) {
            return null;
        } else {
            if (this._image64 !== image64) {
                return this.provider.setImage(image64).then((val) => {
                    this._image64 = image64;
                    this._image = val._image;
                    this._setUpImage();
                }).catch((e) => console.log(e));
            } else {
                return;
            }
        }
    }

    setTransparency (transparency) {
        this._ghost = transparency;

        if (this._drawable !== -1) {
            this.runtime.renderer.updateDrawableProperties(this._drawable, {
                ghost: this._forceTransparentView ? 100 : transparency
            })
        }
    }

    clearImage () {
        this._removeImage();
        if(!this.provider) return null;
        this.provider.clearImage();
    }

    _removeImage () {
        if (this._skin) {
            this._skin.clear();
            this.runtime.renderer.updateDrawableProperties(this._drawable, {visible: false});
        }
        this._renderPreviewImage = null;
        this._image64 = null;
    }

    _setUpImage() {
        const {renderer} = this.runtime;
        if (!renderer) {
            return;
        }

        if (this._skinId === -1 && this._skin === null && this._drawable === -1) {
            this._skinId = renderer.createPenSkin();
            this._skin = renderer._allSkins[this._skinId];
            this._drawable = renderer.createDrawable(StageLayering.IMAGE_OVERLAY_LAYER);
            renderer.updateDrawableProperties(this._drawable, {
                skinId: this._skinId
            });
        }

        renderer.updateDrawableProperties(this._drawable, {
            ghost: this._forceTransparentPreview ? 100 : this._ghost,
            visible: true
        });

        this._renderPreviewImage = () => {
            if (!this._renderPreviewImage) {
                return;
            }

            let canvas = document.createElement('canvas');
            canvas.width = 480;
            canvas.height = 360;
            let context = canvas.getContext('2d');
            context
            let img = new Image();
            img.src = this._image.src;

            let _this = this;
            img.onload = () => {
                context.drawImage(img, 0, 0, 480, 360);
                const xOffset = ImageOverlay.DIMENSIONS[0] / -2;
                const yOffset = ImageOverlay.DIMENSIONS[1] / 2;
                _this._skin.drawStamp(canvas, xOffset, yOffset);
                _this.runtime.requestRedraw();
            }

            img.onerror = function (err) {
                console.log("error is: ", err);
            }
        };

        this._renderPreviewImage();
    }
}

module.exports = ImageOverlay;