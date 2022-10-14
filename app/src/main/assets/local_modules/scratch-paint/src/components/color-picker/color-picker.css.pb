exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n/* Popover styles */\n\n.Popover-body {\n    background: white;\n    border: 1px solid #ddd;\n    padding: 4px;\n    border-radius: 4px;\n    padding: 4px;\n    -webkit-box-shadow: 0px 0px 8px 1px rgba(0, 0, 0, .3);\n            box-shadow: 0px 0px 8px 1px rgba(0, 0, 0, .3);\n}\n\n.Popover-tipShape {\n    fill: white;\n    stroke: #ddd;\n}\n\n.color-picker_clickable_3WU-R {\n    cursor: pointer;\n}\n\n.color-picker_swatch-row_1bfn0 {\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-orient: horizontal;\n    -webkit-box-direction: normal;\n    -webkit-flex-direction: row;\n        -ms-flex-direction: row;\n            flex-direction: row;\n    -webkit-box-pack: justify;\n    -webkit-justify-content: space-between;\n        -ms-flex-pack: justify;\n            justify-content: space-between;\n}\n\n.color-picker_row-header_1F-KU {\n    font-family: \"Helvetica Neue\", Helvetica, sans-serif;\n    font-size: 0.65rem;\n    color: #575E75;\n    margin: 8px;\n}\n\n[dir=\"ltr\"] .color-picker_label-readout_2yr4Y {\n    margin-left: 10px;\n}\n\n[dir=\"rtl\"] .color-picker_label-readout_2yr4Y {\n    margin-right: 10px;\n}\n\n.color-picker_label-name_3Ohhm {\n    font-weight: bold;\n}\n\n.color-picker_divider_1rrhm {\n    border-top: 1px solid #ddd;\n    margin: 8px;\n}\n\n.color-picker_swap-button_1IHVI {\n    margin-left: 8px;\n    margin-right: 8px;\n}\n\n.color-picker_swatches_1IxyF {\n    margin: 8px;\n}\n\n.color-picker_swatch_v12lU {\n    width: 1.5rem;\n    height: 1.5rem;\n    border: 1px solid #ddd;\n    border-radius: 4px;\n    -webkit-box-sizing: content-box;\n            box-sizing: content-box;\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-align: center;\n    -webkit-align-items: center;\n        -ms-flex-align: center;\n            align-items: center;\n}\n\n.color-picker_large-swatch-icon_1jo3W {\n    width: 1.75rem;\n    margin: auto;\n}\n\n.color-picker_large-swatch_VnybH {\n    width: 2rem;\n    height: 2rem;\n}\n\n.color-picker_active-swatch_cOpcH {\n    border: 1px solid #4C97FF;\n    -webkit-box-shadow: 0px 0px 0px 3px hsla(215, 100%, 65%, 0.2);\n            box-shadow: 0px 0px 0px 3px hsla(215, 100%, 65%, 0.2);\n}\n\n.color-picker_swatch-icon_sM_0v {\n    width: 1.5rem;\n    height: 1.5rem;\n}\n\n.color-picker_inactive-gradient_1IOdw {\n    -webkit-filter: saturate(0%);\n            filter: saturate(0%);\n}\n\n.color-picker_gradient-picker-row_2lMgC {\n    -webkit-box-align: center;\n    -webkit-align-items: center;\n        -ms-flex-align: center;\n            align-items: center;\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-orient: horizontal;\n    -webkit-box-direction: normal;\n    -webkit-flex-direction: row;\n        -ms-flex-direction: row;\n            flex-direction: row;\n    -webkit-box-pack: center;\n    -webkit-justify-content: center;\n        -ms-flex-pack: center;\n            justify-content: center;\n    margin: 8px;\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n}\n\n[dir=\"ltr\"] .color-picker_gradient-picker-row_2lMgC > img + img {\n    margin-left: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .color-picker_gradient-picker-row_2lMgC > img + img {\n    margin-right: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .color-picker_gradient-swatches-row_2u1cR {\n    -webkit-box-orient: horizontal;\n    -webkit-box-direction: reverse;\n    -webkit-flex-direction: row-reverse;\n        -ms-flex-direction: row-reverse;\n            flex-direction: row-reverse;\n}\n", ""]);

// exports
exports.locals = {
	"clickable": "color-picker_clickable_3WU-R",
	"swatch-row": "color-picker_swatch-row_1bfn0",
	"swatchRow": "color-picker_swatch-row_1bfn0",
	"row-header": "color-picker_row-header_1F-KU",
	"rowHeader": "color-picker_row-header_1F-KU",
	"label-readout": "color-picker_label-readout_2yr4Y",
	"labelReadout": "color-picker_label-readout_2yr4Y",
	"label-name": "color-picker_label-name_3Ohhm",
	"labelName": "color-picker_label-name_3Ohhm",
	"divider": "color-picker_divider_1rrhm",
	"swap-button": "color-picker_swap-button_1IHVI",
	"swapButton": "color-picker_swap-button_1IHVI",
	"swatches": "color-picker_swatches_1IxyF",
	"swatch": "color-picker_swatch_v12lU",
	"large-swatch-icon": "color-picker_large-swatch-icon_1jo3W",
	"largeSwatchIcon": "color-picker_large-swatch-icon_1jo3W",
	"large-swatch": "color-picker_large-swatch_VnybH",
	"largeSwatch": "color-picker_large-swatch_VnybH",
	"active-swatch": "color-picker_active-swatch_cOpcH",
	"activeSwatch": "color-picker_active-swatch_cOpcH",
	"swatch-icon": "color-picker_swatch-icon_sM_0v",
	"swatchIcon": "color-picker_swatch-icon_sM_0v",
	"inactive-gradient": "color-picker_inactive-gradient_1IOdw",
	"inactiveGradient": "color-picker_inactive-gradient_1IOdw",
	"gradient-picker-row": "color-picker_gradient-picker-row_2lMgC",
	"gradientPickerRow": "color-picker_gradient-picker-row_2lMgC",
	"gradient-swatches-row": "color-picker_gradient-swatches-row_2u1cR",
	"gradientSwatchesRow": "color-picker_gradient-swatches-row_2u1cR"
};