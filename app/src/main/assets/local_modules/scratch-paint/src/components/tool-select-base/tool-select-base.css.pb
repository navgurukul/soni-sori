exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n.tool-select-base_mod-tool-select_8BmWU {\n    display: inline-block;\n    margin: .25rem;\n    border: none;\n    border-radius: .25rem;\n    outline: none;\n    background: none;\n    padding: .25rem;\n    font-size: 0.85rem;\n    -webkit-transition: 0.2s;\n    -o-transition: 0.2s;\n    transition: 0.2s;\n}\n\n.tool-select-base_mod-tool-select_8BmWU.tool-select-base_is-selected_2pt0e {\n    background-color: #4C97FF;\n}\n\n.tool-select-base_mod-tool-select_8BmWU:focus {\n    outline: none;\n}\n\nimg.tool-select-base_tool-select-icon_2pPoi {\n    width: 2rem;\n    height: 2rem;\n    -webkit-box-flex: 1;\n    -webkit-flex-grow: 1;\n        -ms-flex-positive: 1;\n            flex-grow: 1;\n    vertical-align: middle;\n}\n\n.tool-select-base_mod-tool-select_8BmWU.tool-select-base_is-selected_2pt0e .tool-select-base_tool-select-icon_2pPoi {\n    /* Make the tool icons white while selected by making them black and inverting */\n    -webkit-filter: brightness(0) invert(1);\n            filter: brightness(0) invert(1);\n}\n\n@media only screen and (max-width: 1249px) {\n    .tool-select-base_mod-tool-select_8BmWU {\n        margin: 0;\n    }\n}\n", ""]);

// exports
exports.locals = {
	"mod-tool-select": "tool-select-base_mod-tool-select_8BmWU",
	"modToolSelect": "tool-select-base_mod-tool-select_8BmWU",
	"is-selected": "tool-select-base_is-selected_2pt0e",
	"isSelected": "tool-select-base_is-selected_2pt0e",
	"tool-select-icon": "tool-select-base_tool-select-icon_2pPoi",
	"toolSelectIcon": "tool-select-base_tool-select-icon_2pPoi"
};