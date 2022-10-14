/**
 * Block argument types
 * @enum {string}
 */
const ArgumentType = {
    /**
     * Numeric value with angle picker
     */
    ANGLE: 'angle',

    /**
     * Boolean value with hexagonal placeholder
     */
    BOOLEAN: 'Boolean',

    /**
     * Numeric value with color picker
     */
    COLOR: 'color',

    /**
     * Numeric value with text field
     */
    NUMBER: 'number',

    /**
     * String value with text field
     */
    STRING: 'string',

    /**
     * String value with matrix field
     */
    MATRIX: 'matrix',

    MATRIX2: 'matrix2',

    MATRIX3: 'matrix3',

    MATRIXLEFT: 'matrix4_left',
    MATRIXRIGHT: 'matrix4_right',

    MATRIXCOLOUR: 'matrixColour',

    MATRIXCOLOUR5X7: 'matrixColour5x7',

    MATHSLIDER255: 'slider255',

    MATHSLIDER100: 'slider100',

    MATHSLIDER159: 'slider159',

    MATHSLIDER127: 'slider127',

    MATHSLIDER180: 'slider180',

    MATHSLIDER200: 'slider200',

    MATHSLIDER240: 'slider240',

    MATHSLIDER320: 'slider320',

    MATHSLIDER20: 'slider20',

    IMAGE: 'imageDropdown',

    IMAGE_SELECT: 'image_select',

    IMAGE_SELECT_240x240: 'imageSelect240x240',

    IMAGE_MENU: 'imageMenu',
    /**
     * MIDI note number with note picker (piano) field
     */
    NOTE: 'note'
};

module.exports = ArgumentType;
