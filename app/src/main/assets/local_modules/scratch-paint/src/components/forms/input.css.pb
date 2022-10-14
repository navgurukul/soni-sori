exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* NOTE:\nEdited to add input-range-small\n*/\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n.input_input-form_K5SYW {\n    height: 2rem;\n    padding: 0 0.75rem;\n\n    font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n    font-size: 0.75rem;\n    font-weight: bold;\n    color: #575e75;\n\n    border-width: 1px;\n    border-style: solid;\n    border-color: #E9EEF2;\n    border-radius: 2rem;\n\n    outline: none;\n    cursor: text;\n    -webkit-transition: 0.25s ease-out;\n    -o-transition: 0.25s ease-out;\n    transition: 0.25s ease-out; /* @todo: standardize with var */\n    -webkit-box-shadow: none;\n            box-shadow: none;\n\n    /*\n        For truncating overflowing text gracefully\n        Min-width is for a bug: https://css-tricks.com/flexbox-truncated-text\n        @todo: move this out into a mixin or a helper component\n    */\n    overflow: hidden;\n    -o-text-overflow: ellipsis;\n       text-overflow: ellipsis;\n    white-space: nowrap;\n    min-width: 0;\n}\n\n.input_input-form_K5SYW:focus {\n    border-color: #4C97FF;\n    -webkit-box-shadow: 0 0 0 .25rem hsla(215, 100%, 65%, 0.20);\n            box-shadow: 0 0 0 .25rem hsla(215, 100%, 65%, 0.20);\n}\n\n.input_input-small_BxP6l {\n    width: 3rem;\n    text-align: center;\n}\n\n.input_input-small-range_300XG {\n    width: 4rem;\n    text-align: center;\n}\n", ""]);

// exports
exports.locals = {
	"input-form": "input_input-form_K5SYW",
	"inputForm": "input_input-form_K5SYW",
	"input-small": "input_input-small_BxP6l",
	"inputSmall": "input_input-small_BxP6l",
	"input-small-range": "input_input-small-range_300XG",
	"inputSmallRange": "input_input-small-range_300XG"
};