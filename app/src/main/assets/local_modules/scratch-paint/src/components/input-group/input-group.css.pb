exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n[dir=\"ltr\"] .input-group_input-group_2GIWd + .input-group_input-group_2GIWd {\n    margin-left: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .input-group_input-group_2GIWd + .input-group_input-group_2GIWd {\n    margin-right: calc(2 * .25rem);\n}\n\n.input-group_disabled_2ud9L {\n    opacity: 0.3;\n    /* Prevent any user actions */\n    pointer-events: none;\n}\n", ""]);

// exports
exports.locals = {
	"input-group": "input-group_input-group_2GIWd",
	"inputGroup": "input-group_input-group_2GIWd",
	"disabled": "input-group_disabled_2ud9L"
};