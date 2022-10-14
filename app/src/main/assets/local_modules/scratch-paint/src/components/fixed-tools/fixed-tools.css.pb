exports = module.exports = require("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.id, "/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* DO NOT EDIT\n@todo This file is copied from GUI and should be pulled out into a shared library.\nSee https://github.com/LLK/scratch-paint/issues/13 */\n\n/* ACTUALLY, THIS IS EDITED ;)\nTHIS WAS CHANGED ON 10/25/2017 BY @mewtaylor TO ADD A VARIABLE FOR THE SMALLEST\nGRID UNITS.\n\nALSO EDITED ON 11/13/2017 TO ADD IN CONTANTS FOR LAYOUT FROM `layout-contents.js`*/\n\n/* layout contants from `layout-constants.js`, minus 1px */\n\n.fixed-tools_row_192N0 {\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-orient: horizontal;\n    -webkit-box-direction: normal;\n    -webkit-flex-direction: row;\n        -ms-flex-direction: row;\n            flex-direction: row;\n    -webkit-box-align: center;\n    -webkit-align-items: center;\n        -ms-flex-align: center;\n            align-items: center;\n}\n\n.fixed-tools_costume-input_2KLYL {\n    width: 8rem;\n}\n\n[dir=\"ltr\"] .fixed-tools_mod-dashed-border_54CN7 {\n    border-right: 1px dashed #D9D9D9;\n    padding-right: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .fixed-tools_mod-dashed-border_54CN7 {\n    border-left: 1px dashed #D9D9D9;\n    padding-left: calc(2 * .25rem);\n}\n\n.fixed-tools_mod-unselect_3d7b0 {\n    -webkit-user-select: none;\n       -moz-user-select: none;\n        -ms-user-select: none;\n            user-select: none;\n}\n\n.fixed-tools_button-group-button_27c1u {\n    display: inline-block;\n    border: 1px solid #D9D9D9;\n    border-radius: 0;\n    padding: .35rem;\n}\n\n[dir=\"ltr\"] .fixed-tools_button-group-button_27c1u {\n    border-left: none;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button_27c1u {\n    border-right: none;\n}\n\n[dir=\"ltr\"] .fixed-tools_button-group-button_27c1u:last-of-type {\n    border-top-right-radius: 0.25rem;\n    border-bottom-right-radius: 0.25rem;\n}\n\n[dir=\"ltr\"] .fixed-tools_button-group-button_27c1u:first-of-type {\n    border-left: 1px solid #D9D9D9;\n    border-top-left-radius: 0.25rem;\n    border-bottom-left-radius: 0.25rem;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button_27c1u:last-of-type {\n    border-top-left-radius: 0.25rem;\n    border-bottom-left-radius: 0.25rem;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button_27c1u:first-of-type {\n    border-right: 1px solid #D9D9D9;\n    border-top-right-radius: 0.25rem;\n    border-bottom-right-radius: 0.25rem;\n}\n\n[dir=\"ltr\"] .fixed-tools_button-group-button_27c1u.fixed-tools_mod-start-border_4MWrk {\n    border-left: 1px solid #D9D9D9;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button_27c1u.fixed-tools_mod-start-border_4MWrk {\n    border-right: 1px solid #D9D9D9;\n}\n\n[dir=\"ltr\"] .fixed-tools_button-group-button_27c1u.fixed-tools_mod-no-end-border_3vk1m {\n    border-right: none;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button_27c1u.fixed-tools_mod-no-end-border_3vk1m {\n    border-left: none;\n}\n\n.fixed-tools_button-group-button-icon_JkaPk {\n    width: 1.25rem;\n    height: 1.25rem;\n    vertical-align: middle;\n}\n\n[dir=\"rtl\"] .fixed-tools_button-group-button-icon_JkaPk {\n    -webkit-transform: scaleX(-1);\n        -ms-transform: scaleX(-1);\n            transform: scaleX(-1);\n}\n\n.fixed-tools_mod-context-menu_3teHu {\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    -webkit-box-orient: vertical;\n    -webkit-box-direction: normal;\n    -webkit-flex-direction: column;\n        -ms-flex-direction: column;\n            flex-direction: column;\n}\n\n.fixed-tools_mod-top-divider_2Azhw {\n    border-top: 1px solid #D9D9D9;\n}\n\n.fixed-tools_mod-menu-item_32Oly {\n    display: -webkit-box;\n    display: -webkit-flex;\n    display: -ms-flexbox;\n    display: flex;\n    margin: 0 -.25rem;\n    min-width: 6.25rem;\n    padding: calc(3 * .25rem);\n    white-space: nowrap;\n    cursor: pointer;\n    -webkit-transition: 0.1s ease;\n    -o-transition: 0.1s ease;\n    transition: 0.1s ease;\n    -webkit-box-align: center;\n    -webkit-align-items: center;\n        -ms-flex-align: center;\n            align-items: center;\n    font-family: \"Helvetica Neue\", Helvetica, sans-serif;\n}\n\n.fixed-tools_mod-disabled_2yK8h {\n    cursor: auto;\n}\n\n.fixed-tools_mod-menu-item_32Oly:hover {\n    background: hsla(215, 100%, 65%, 0.20);\n}\n\n.fixed-tools_mod-disabled_2yK8h:hover {\n    background-color: transparent;\n}\n\n.fixed-tools_menu-item-icon_d411G {\n    margin-right: calc(2 * .25rem);\n}\n\n[dir=\"rtl\"] .fixed-tools_menu-item-icon_d411G {\n    margin-right: 0;\n    margin-left: calc(2 * .25rem);\n}\n", ""]);

// exports
exports.locals = {
	"row": "fixed-tools_row_192N0",
	"costume-input": "fixed-tools_costume-input_2KLYL",
	"costumeInput": "fixed-tools_costume-input_2KLYL",
	"mod-dashed-border": "fixed-tools_mod-dashed-border_54CN7",
	"modDashedBorder": "fixed-tools_mod-dashed-border_54CN7",
	"mod-unselect": "fixed-tools_mod-unselect_3d7b0",
	"modUnselect": "fixed-tools_mod-unselect_3d7b0",
	"button-group-button": "fixed-tools_button-group-button_27c1u",
	"buttonGroupButton": "fixed-tools_button-group-button_27c1u",
	"mod-start-border": "fixed-tools_mod-start-border_4MWrk",
	"modStartBorder": "fixed-tools_mod-start-border_4MWrk",
	"mod-no-end-border": "fixed-tools_mod-no-end-border_3vk1m",
	"modNoEndBorder": "fixed-tools_mod-no-end-border_3vk1m",
	"button-group-button-icon": "fixed-tools_button-group-button-icon_JkaPk",
	"buttonGroupButtonIcon": "fixed-tools_button-group-button-icon_JkaPk",
	"mod-context-menu": "fixed-tools_mod-context-menu_3teHu",
	"modContextMenu": "fixed-tools_mod-context-menu_3teHu",
	"mod-top-divider": "fixed-tools_mod-top-divider_2Azhw",
	"modTopDivider": "fixed-tools_mod-top-divider_2Azhw",
	"mod-menu-item": "fixed-tools_mod-menu-item_32Oly",
	"modMenuItem": "fixed-tools_mod-menu-item_32Oly",
	"mod-disabled": "fixed-tools_mod-disabled_2yK8h",
	"modDisabled": "fixed-tools_mod-disabled_2yK8h",
	"menu-item-icon": "fixed-tools_menu-item-icon_d411G",
	"menuItemIcon": "fixed-tools_menu-item-icon_d411G"
};