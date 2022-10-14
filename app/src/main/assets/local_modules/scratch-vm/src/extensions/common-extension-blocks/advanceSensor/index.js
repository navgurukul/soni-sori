const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNBNEQ4RkY7fQ0KCS5zdDF7ZmlsbDojRkZGRkZGO30NCgkuc3Qye2ZpbGw6I0QxMTMyRDt9DQoJLnN0M3tvcGFjaXR5OjAuMjtmaWxsOiNGRkZGRkY7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF84MTA0XyI+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDhfIiBjbGFzcz0ic3QwIiBkPSJNMjEuNiw4LjlDMjEuNiw4LjksMjEuNiw4LjksMjEuNiw4LjlMMjEuNiw4LjlMMjEuNiw4LjljMC0wLjktMC43LTEuNi0xLjYtMS42DQoJCWMtMC45LDAtMS42LDAuNy0xLjYsMS42aDB2Ni42aDMuM0wyMS42LDguOUMyMS42LDguOSwyMS42LDguOSwyMS42LDguOXoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODIwNV8iIGNsYXNzPSJzdDEiIGQ9Ik0yMCwzNWMtMy41LDAtNi4zLTIuOC02LjMtNi4zYzAtMiwxLTMuOSwyLjYtNS4xVjguOEMxNi4yLDYuNywxNy45LDUsMjAsNQ0KCQljMi4xLDAsMy44LDEuNywzLjgsMy44djE0LjhjMS42LDEuMiwyLjYsMy4xLDIuNiw1LjFDMjYuMywzMi4yLDIzLjUsMzUsMjAsMzV6IE0yMCwzMy45YzIuOSwwLDUuMy0yLjQsNS4zLTUuMw0KCQljMC0xLjktMS0zLjYtMi42LTQuNVY4LjhjMC0xLjUtMS4yLTIuNy0yLjctMi43Yy0xLjUsMC0yLjcsMS4yLTIuNywyLjd2MTUuM2MtMS42LDAuOS0yLjYsMi43LTIuNiw0LjUNCgkJQzE0LjcsMzEuNiwxNy4xLDMzLjksMjAsMzMuOXoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODIwNF8iIGNsYXNzPSJzdDIiIGQ9Ik0yMS42LDI0Ljh2LTkuNGgtMy4zdjkuNGMtMiwwLjktMy4yLDMuMi0yLjIsNS43YzAuNCwwLjksMS4xLDEuNiwyLDJjMy4xLDEuMyw2LTAuOSw2LTMuOA0KCQlDMjQuMiwyNi45LDIzLjIsMjUuNCwyMS42LDI0Ljh6Ii8+DQoJPGVsbGlwc2UgaWQ9IlhNTElEXzgyMDNfIiBjbGFzcz0ic3QzIiBjeD0iMTcuOCIgY3k9IjI4LjQiIHJ4PSIxIiByeT0iMS40Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDJfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwyMS44aDIuMXYwLjdoLTIuMWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zdi0wLjJDMjAuMywyMS45LDIwLjQsMjEuOCwyMC42LDIxLjh6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDFfIiBjbGFzcz0ic3QxIiBkPSJNMjEuMiwxOS42SDIzdjAuN2gtMS44Yy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC45LDE5LjgsMjEsMTkuNiwyMS4yLDE5LjZ6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDBfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwxNy41aDIuMXYwLjdoLTIuMWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zdi0wLjJDMjAuMywxNy43LDIwLjQsMTcuNSwyMC42LDE3LjV6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDlfIiBjbGFzcz0ic3QxIiBkPSJNMjEuMiwxNS40SDIzdjAuN2gtMS44Yy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC45LDE1LjUsMjEsMTUuNCwyMS4yLDE1LjR6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDhfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwxMy4zaDIuMVYxNGgtMi4xYy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC4zLDEzLjQsMjAuNCwxMy4zLDIwLjYsMTMuM3oiLz4NCgk8cGF0aCBpZD0iWE1MSURfODEwN18iIGNsYXNzPSJzdDEiIGQ9Ik0yMS4yLDExLjJIMjN2MC43aC0xLjhjLTAuMiwwLTAuMy0wLjEtMC4zLTAuM3YtMC4yQzIwLjksMTEuMywyMSwxMS4yLDIxLjIsMTEuMnoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODEwNl8iIGNsYXNzPSJzdDEiIGQ9Ik0yMC42LDloMi4xdjAuN2gtMi4xYy0wLjIsMC0wLjMtMC4xLTAuMy0wLjNWOS4zQzIwLjMsOS4yLDIwLjQsOSwyMC42LDl6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDVfIiBjbGFzcz0ic3QzIiBkPSJNMTkuNCw5LjNjMC0wLjEtMC4xLTAuMi0wLjItMC4yYy0wLjEsMC0wLjIsMC4xLTAuMiwwLjJoMHYxNS40bDAuNC0wLjFMMTkuNCw5LjNMMTkuNCw5LjN6Ig0KCQkvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM3NkM0RjQ7fQ0KCS5zdDF7ZmlsbDojRDExMzJEO30NCgkuc3Qye29wYWNpdHk6MC4yO2ZpbGw6I0ZGRkZGRjt9DQoJLnN0M3tmaWxsOiNBN0E5QUM7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF84MjA4XyIgY2xhc3M9InN0MCIgZD0iTTIyLDYuMkwyMiw2LjJMMjIsNi4yTDIyLDYuMmMwLTEuMS0wLjktMi0yLTJjLTEuMSwwLTIsMC45LTIsMmgwdjguMUgyMkwyMiw2LjINCglDMjIsNi4yLDIyLDYuMiwyMiw2LjJ6Ii8+DQo8cGF0aCBpZD0iWE1MSURfODIwNF8iIGNsYXNzPSJzdDEiIGQ9Ik0yMiwyNS44VjE0LjNIMTh2MTEuNmMtMi41LDEuMS00LDQtMi43LDdjMC41LDEuMSwxLjQsMiwyLjUsMi41YzMuOCwxLjYsNy40LTEuMiw3LjQtNC43DQoJQzI1LjIsMjguNSwyMy45LDI2LjYsMjIsMjUuOHoiLz4NCjxlbGxpcHNlIGlkPSJYTUxJRF84MjAzXyIgY2xhc3M9InN0MiIgY3g9IjE3LjMiIGN5PSIzMC4yIiByeD0iMS4yIiByeT0iMS43Ii8+DQo8cGF0aCBpZD0iWE1MSURfNTBfIiBjbGFzcz0ic3QzIiBkPSJNMjQuNywyNC4zVjYuMWMwLTIuNi0yLjEtNC43LTQuNy00LjdzLTQuNywyLjEtNC43LDQuN3YxOC4yYy0yLDEuNS0zLjIsMy44LTMuMiw2LjMNCgljMCw0LjMsMy41LDcuOCw3LjgsNy44czcuOC0zLjUsNy44LTcuOEMyNy44LDI4LjEsMjYuNiwyNS44LDI0LjcsMjQuM3ogTTIwLDM3LjFjLTMuNiwwLTYuNS0yLjktNi41LTYuNWMwLTIuMywxLjItNC40LDMuMi01LjZWNi4xDQoJYzAtMS44LDEuNS0zLjMsMy4zLTMuM3MzLjMsMS41LDMuMywzLjN2MC4zaC0yLjZjLTAuMiwwLTAuMywwLjItMC4zLDAuM1Y3YzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gyLjZWOWgtMS45DQoJYy0wLjIsMC0wLjMsMC4yLTAuMywwLjN2MC4yYzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gxLjl2MS43aC0yLjZjLTAuMiwwLTAuMywwLjItMC4zLDAuM3YwLjJjMCwwLjIsMC4yLDAuMywwLjMsMC4zaDIuNnYxLjdoLTEuOQ0KCWMtMC4yLDAtMC4zLDAuMi0wLjMsMC4zdjAuMmMwLDAuMiwwLjIsMC4zLDAuMywwLjNoMS45djEuN2gtMi42Yy0wLjIsMC0wLjMsMC4yLTAuMywwLjN2MC4yYzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gyLjZ2MS43aC0xLjkNCgljLTAuMiwwLTAuMywwLjItMC4zLDAuM3YwLjJjMCwwLjIsMC4yLDAuMywwLjMsMC4zaDEuOXYxLjdoLTIuNmMtMC4yLDAtMC4zLDAuMi0wLjMsMC4zdjAuMmMwLDAuMiwwLjIsMC4zLDAuMywwLjNoMi42djINCgljMS45LDEuMiwzLjIsMy4zLDMuMiw1LjZDMjYuNSwzNC4yLDIzLjYsMzcuMSwyMCwzNy4xeiIvPg0KPHBhdGggaWQ9IlhNTElEXzgxMDVfIiBjbGFzcz0ic3QyIiBkPSJNMTkuMiw2LjdjMC0wLjEtMC4xLTAuMi0wLjMtMC4yYy0wLjEsMC0wLjIsMC4xLTAuMywwLjJoMHYxOWwwLjUtMC4xTDE5LjIsNi43TDE5LjIsNi43eiIvPg0KPC9zdmc+DQo=';


class advanceSensor {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'advanceSensor';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'advanceSensor';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const advanceSensorState = sourceTarget.getCustomState(advanceSensor.STATE_KEY);
            if (advanceSensorState) {
                newTarget.setCustomState(advanceSensor.STATE_KEY, Clone.simple(advanceSensorState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        // const commonExtensions = () => [];
        let boardCommonExtensionList = [];
        // let commonExtensionList = commonExtensions();
        // for (let commonExtensionIndex in commonExtensionList) {
        //     let commonExtension = commonExtensionList[commonExtensionIndex];
        //     if (commonExtension.allowedBoards.includes(board)) {
        //         boardCommonExtensionList.push(commonExtension);
        //     }
        // }
        return boardCommonExtensionList;
    }

    get DIGITAL_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.digitalPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.digitalPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.digitalPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.digitalPins.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.digitalPins.esp32;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'advanceSensor',
            name: formatMessage({
                id: 'advanceSensor.advanceSensor',
                default: 'Advance Sensors',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5cb1d6',
            colourSecondary: '#47a8d1',
            colourTertiary: '#2e8eb8',
            blocks: [
                {
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage1',
                        default: 'RFID Sensor',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initialiseRFID',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.initialiseRFID',
                        default: 'initialise RFID at SS Pin [SSPIN] & RST Pin [RESETPIN]',
                        description: 'initialise RFID'
                    }),
                    arguments: {
                        SSPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        },
                        RESETPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        }
                    }
                },
                {
                    opcode: 'setMasterRFID',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.setMasterRFID',
                        default: 'set master RFID tag',
                        description: 'set master RFID tag'
                    })
                },
                {
                    opcode: 'isMasterRFID',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'advanceSensor.isMasterRFID',
                        default: 'is master RFID tag scanned?',
                        description: 'is master RFID tag scanned'
                    })
                },
                {
                    opcode: 'getFromRFID',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getFromRFID',
                        default: 'get [RFIDCHOICE] tag ID',
                        description: 'get RFID tag ID'
                    }),
                    arguments: {
                        RFIDCHOICE: {
                            type: ArgumentType.NUMBER,
                            menu: 'rfidOptions',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage2',
                        default: 'Keypad Sensor',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initialiseKeypad',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.initialiseKeypad',
                        default: 'initialise keypad on row pins [RPIN1][RPIN2][RPIN3][RPIN4] col pins [CPIN1][CPIN2][CPIN3][CPIN4]',
                        description: 'initialise Keypad'
                    }),
                    arguments: {
                        RPIN1: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        },
                        RPIN2: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        RPIN3: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '4'
                        },
                        RPIN4: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        },
                        CPIN1: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        },
                        CPIN2: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '7'
                        },
                        CPIN3: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '8'
                        },
                        CPIN4: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '9'
                        }
                    }
                },
                {
                    opcode: 'checkKeyPressed',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'advanceSensor.checkKeyPressed',
                        default: 'is [KEY] pressed on keypad?',
                        description: 'is key pressed'
                    }),
                    arguments: {
                        KEY: {
                            type: ArgumentType.NUMBER,
                            menu: 'keyAvailable',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'getKeyPressed',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getKeyPressed',
                        default: 'get pressed key on keypad',
                        description: 'get pressed key'
                    })
                },
                {
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage3',
                        default: 'Fingerprint Sensor R307',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initializeFingerprintSensor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.initailiseFingerprintSensor',
                        default: 'initialise fingerprint sensor at RX pin [RXPIN] TX pin [TXPIN]',
                        description: 'Initialise fingerprint sensor'
                    }),
                    arguments: {
                        RXPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '10'
                        },
                        TXPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '11'
                        }
                    }
                },
                {
                    opcode: 'isSensorConnected',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'advanceSensor.isSensorConnected',
                        default: 'is fingerprint sensor connected?',
                        description: 'Is fingerprint sensor connected'
                    })
                },
                {
                    opcode: 'getFingerprintImage',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getFingerprintImage',
                        default: 'scan fingerprint',
                        description: 'Scan fingerprint'
                    })
                },
                {
                    opcode: 'convertImageToTemplateSlot',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.convertImageToTemplateSlot',
                        default: 'convert fingerprint to template slot [SLOT]',
                        description: 'Save to template'
                    }),
                    arguments: {
                        SLOT: {
                            type: ArgumentType.NUMBER,
                            menu: 'tempalateSlot',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'createFingerprintModel',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.createFingerprintModel',
                        default: '[OPERATION1] fingerprint model',
                        description: 'create fingerpritn model'
                    }),
                    arguments: {
                        OPERATION1: {
                            type: ArgumentType.NUMBER,
                            menu: 'fingerprintOperation2',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'fingerprintModel',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.fingerprintModel',
                        default: '[OPERATION] fingerprint model for id [SLOTID]',
                        description: 'operation on fingerpritn model'
                    }),
                    arguments: {
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'fingerprintOperation',
                            defaultValue: '1'
                        },
                        SLOTID: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'fingerprintModelOperation2',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.fingerprintModelOperation2',
                        default: '[OPERATION] fingerprints',
                        description: 'operation on fingerpritn model'
                    }),
                    arguments: {
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'fingerprintOperation3',
                            defaultValue: '1'
                        }
                    }
                },
                /*{ 
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage4',
                        default: 'EEPROM',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'writeEEPROM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'advanceSensor.writeEEPROM',
                        default: 'write [DATA] on EEPROM slot [SLOT]',
                        description: 'write on EEPROM'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Hello World!'
                        },
                        SLOT: {
                            type: ArgumentType.NUMBER,
                            menu: 'EEPROMslot',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'readEEPROM',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.readEEPROM',
                        default: 'read EEPROM slot [SLOT]',
                        description: 'write on EEPROM'
                    }),
                    arguments: {
                        SLOT: {
                            type: ArgumentType.NUMBER,
                            menu: 'EEPROMslot',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'getEEPROMSlot',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getEEPROMSlot',
                        default: 'get [DATA] slot',
                        description: 'get slot on EEPROM'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Hello World!'
                        }
                    }
                },*/
                {
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage5',
                        default: 'IMU Sensor - MPU6050',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'getIMUSensorData',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getIMUSensorData',
                        default: 'get [CHOICE] with weight constant [WEIGHT]',
                        description: 'get IMU Data'
                    }),
                    arguments: {
                        CHOICE: {
                            type: ArgumentType.NUMBER,
                            menu: 'IMUSensor',
                            defaultValue: '1'
                        },
                        WEIGHT: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0.98'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'advanceSensor.blockSeparatorMessage6',
                        default: 'Other Sensors',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'getTemperatureReading',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'advanceSensor.getTemperatureReading',
                        default: 'get temperature from DS18B20 connected to pin [PIN]',
                        description: 'get temperature from sensor'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        }
                    }
                },
            ],
            menus: {
                digitalPins: this.DIGITAL_PINS,
                keypadValues: ['0', '1', '2', '3', '4', '5',
                    '6', '7', '8', '9', '*', '#', 'A', 'B', 'C', 'D'],
                rfidOptions: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.rfidOptions.option1',
                            default: 'master',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.rfidOptions.option2',
                            default: 'current',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                EEPROMslot: [
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14',
                    '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25'
                ],
                keyAvailable: [
                    { text: '0', value: '0' },
                    { text: '1', value: '1' },
                    { text: '2', value: '2' },
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' },
                    { text: '6', value: '6' },
                    { text: '7', value: '7' },
                    { text: '8', value: '8' },
                    { text: '9', value: '9' },
                    { text: '*', value: '10' },
                    { text: '#', value: '11' },
                    { text: 'A', value: '12' },
                    { text: 'B', value: '13' },
                    { text: 'C', value: '14' },
                    { text: 'D', value: '15' }
                ],
                tempalateSlot: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                fingerprintOperation: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation.option1',
                            default: 'store',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation.option2',
                            default: 'load',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation.option3',
                            default: 'delete',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                fingerprintOperation2: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option1',
                            default: 'create new',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option3',
                            default: 'get',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option4',
                            default: 'number of',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option5',
                            default: 'do fast search for',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option6',
                            default: 'get id of matched',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation2.option7',
                            default: 'get confidence of matched',
                            description: 'Menu'
                        }), value: '5'
                    }
                ],
                fingerprintOperation3: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation3.option1',
                            default: 'count',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fingerprintOperation3.option2',
                            default: 'delete all',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                IMUSensor: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option1',
                            default: 'filtered angle in X-axis',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option2',
                            default: 'filtered angle in Y-axis',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option3',
                            default: 'raw accelerometer reading in X-axis',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option4',
                            default: 'raw accelerometer reading in Y-axis',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option5',
                            default: 'raw accelerometer reading in Z-axis',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option6',
                            default: 'raw gyroscope reading in X-axis',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option7',
                            default: 'raw gyroscope reading in Y-axis',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option8',
                            default: 'raw gyroscope reading in Z-axis',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option9',
                            default: 'angle from accelerometer in X-axis',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option10',
                            default: 'angle from accelerometer in Y-axis',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option11',
                            default: 'angle from gyroscope in X-axis',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.IMUSensor.option12',
                            default: 'angle from gyroscope in Y-axis',
                            description: 'Menu'
                        }), value: '12'
                    },
                ]
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    initializeFingerprintSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initializeFingerprintSensor(args, util, this);
        }
        return RealtimeMode.initializeFingerprintSensor(args, util, this);
    }

    isSensorConnected(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.isSensorConnected(args, util, this);
        }
        return RealtimeMode.isSensorConnected(args, util, this);
    }

    getFingerprintImage(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getFingerprintImage(args, util, this);
        }
        return RealtimeMode.getFingerprintImage(args, util, this);
    }

    convertImageToTemplateSlot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.convertImageToTemplateSlot(args, util, this);
        }
        return RealtimeMode.convertImageToTemplateSlot(args, util, this);
    }

    createFingerprintModel(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.createFingerprintModel(args, util, this);
        }
        return RealtimeMode.createFingerprintModel(args, util, this);
    }

    fingerprintModel(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fingerprintModel(args, util, this);
        }
        return RealtimeMode.fingerprintModel(args, util, this);
    }

    fingerprintModelOperation2(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fingerprintModelOperation2(args, util, this);
        }
        return RealtimeMode.fingerprintModelOperation2(args, util, this);
    }

    getTemperatureReading(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getTemperatureReading(args, util, this);
        }
        return RealtimeMode.getTemperatureReading(args, util, this);
    }

    initialiseRFID(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseRFID(args, util, this);
        }
        return RealtimeMode.initialiseRFID(args, util, this);
    }

    setMasterRFID(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setMasterRFID(args, util, this);
        }
        return RealtimeMode.setMasterRFID(args, util, this);
    }

    isMasterRFID(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.isMasterRFID(args, util, this);
        }
        return RealtimeMode.isMasterRFID(args, util, this);
    }

    getFromRFID(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getFromRFID(args, util, this);
        }
        return RealtimeMode.getFromRFID(args, util, this);
    }

    initialiseKeypad(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseKeypad(args, util, this);
        }
        return RealtimeMode.initialiseKeypad(args, util, this);
    }

    checkKeyPressed(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.checkKeyPressed(args, util, this);
        }
        return RealtimeMode.checkKeyPressed(args, util, this);
    }

    getKeyPressed(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getKeyPressed(args, util, this);
        }
        return RealtimeMode.getKeyPressed(args, util, this);
    }

    /*writeEEPROM (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.writeEEPROM(args, util, this);
        }
        return RealtimeMode.writeEEPROM(args, util, this);
    }

    readEEPROM (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readEEPROM(args, util, this);
        }
        return RealtimeMode.readEEPROM(args, util, this);
    }

    getEEPROMSlot (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getEEPROMSlot(args, util, this);
        }
        return RealtimeMode.getEEPROMSlot(args, util, this);
    }*/

    getIMUSensorData(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getIMUSensorData(args, util, this);
        }
        return RealtimeMode.getIMUSensorData(args, util, this);
    }

}

module.exports = advanceSensor;
