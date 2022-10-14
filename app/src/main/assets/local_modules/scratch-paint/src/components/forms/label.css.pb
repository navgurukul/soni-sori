exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n.label_input-group_cypxi {\n    display: -webkit-inline-box;\n    display: -webkit-inline-flex;\n    display: -ms-inline-flexbox;\n    display: inline-flex;\n    -webkit-box-orient: horizontal;\n    -webkit-box-direction: normal;\n    -webkit-flex-direction: row;\n        -ms-flex-direction: row;\n            flex-direction: row;\n    -webkit-box-align: center;\n    -webkit-align-items: center;\n        -ms-flex-align: center;\n            align-items: center;\n}\n\n.label_input-label_2IYQh, .label_input-label-secondary_3FnB7 {\n    font-size: 0.625rem;\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n    cursor: default;\n}\n\n[dir=\"ltr\"] .label_input-label_2IYQh, [dir=\"ltr\"] .label_input-label-secondary_3FnB7{\n    margin-right: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .label_input-label_2IYQh, [dir=\"ltr\"] .label_input-label-secondary_3FnB7{\n    margin-left: calc(2 * .25rem);\n}\n\n.label_input-label_2IYQh {\n    font-weight: bold;\n}\n\n@media only screen and (max-width: 1249px) {\n    .label_input-group_cypxi {\n        display: -webkit-box;\n        display: -webkit-flex;\n        display: -ms-flexbox;\n        display: flex;\n        -webkit-box-orient: vertical;\n        -webkit-box-direction: normal;\n        -webkit-flex-direction: column;\n            -ms-flex-direction: column;\n                flex-direction: column;\n        -webkit-box-align: start;\n        -webkit-align-items: flex-start;\n            -ms-flex-align: start;\n                align-items: flex-start;\n        margin-top: -1rem; /* To align with the non-labeled inputs */\n    }\n\n    .label_input-label_2IYQh {\n        font-weight: normal;\n        margin-bottom: 0.25rem;\n    }\n}\n", ""]);

// exports
exports.locals = {
	"input-group": "label_input-group_cypxi",
	"inputGroup": "label_input-group_cypxi",
	"input-label": "label_input-label_2IYQh",
	"inputLabel": "label_input-label_2IYQh",
	"input-label-secondary": "label_input-label-secondary_3FnB7",
	"inputLabelSecondary": "label_input-label-secondary_3FnB7"
};