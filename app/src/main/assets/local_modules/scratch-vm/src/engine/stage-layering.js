class StageLayering {
    static get BACKGROUND_LAYER() {
        return 'background';
    }

    static get VIDEO_LAYER() {
        return 'video';
    }

    static get IMAGE_OVERLAY_LAYER() {
        return 'image_overlay'
    }

    static get PEN_LAYER() {
        return 'pen';
    }

    static get IMAGE_LAYER() {
        return 'image';
    }

    static get SPRITE_LAYER() {
        return 'sprite';
    }

    // Order of layer groups relative to each other,
    static get LAYER_GROUPS() {
        return [
            StageLayering.BACKGROUND_LAYER,
            StageLayering.VIDEO_LAYER,
            StageLayering.IMAGE_OVERLAY_LAYER,
            StageLayering.PEN_LAYER,
            StageLayering.IMAGE_LAYER,
            StageLayering.SPRITE_LAYER
        ];
    }
}

module.exports = StageLayering;
