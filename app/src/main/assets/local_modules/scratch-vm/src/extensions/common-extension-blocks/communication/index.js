const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;
const Config = require('./config.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGRkZGRkY7c3Ryb2tlOiM2MDMxMTE7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF8xMDg1MV8iIGNsYXNzPSJzdDAiIGQ9Ik0yNi44LDEzLjJMMTcsMTQuOGMtMC42LDAuMS0xLjEsMC43LTEuMSwxLjNjMCwwLjYsMC41LDEuMiwxLjEsMS4zbDkuOCwxLjZsMCwzLjYNCgljMCwxLjIsMC44LDEuNiwxLjgsMC45bDguMi02YzEtMC43LDEtMS45LDAtMi42bC04LjMtNi4yYy0xLTAuNy0xLjctMC4zLTEuNywwLjlMMjYuOCwxMy4yeiIvPg0KPHBhdGggaWQ9IlhNTElEXzEwODUwXyIgY2xhc3M9InN0MCIgZD0iTTEzLjIsMjYuOGw5LjgtMS42YzAuNi0wLjEsMS4xLTAuNywxLjEtMS4zYzAtMC42LTAuNS0xLjItMS4xLTEuM2wtOS44LTEuNmwwLTMuNg0KCWMwLTEuMi0wLjgtMS42LTEuOC0wLjlsLTguMiw2Yy0xLDAuNy0xLDEuOSwwLDIuNmw4LjMsNi4yYzEsMC43LDEuNywwLjMsMS43LTAuOUwxMy4yLDI2Ljh6Ii8+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM3RjQ2MUI7c3Ryb2tlOiM2MDMxMTE7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF8xMDg1MV8iIGNsYXNzPSJzdDAiIGQ9Ik0yNi44LDEzLjJMMTcsMTQuOGMtMC42LDAuMS0xLjEsMC43LTEuMSwxLjNjMCwwLjYsMC41LDEuMiwxLjEsMS4zbDkuOCwxLjZsMCwzLjYNCgljMCwxLjIsMC44LDEuNiwxLjgsMC45bDguMi02YzEtMC43LDEtMS45LDAtMi42bC04LjMtNi4yYy0xLTAuNy0xLjctMC4zLTEuNywwLjlMMjYuOCwxMy4yeiIvPg0KPHBhdGggaWQ9IlhNTElEXzEwODUwXyIgY2xhc3M9InN0MCIgZD0iTTEzLjIsMjYuOGw5LjgtMS42YzAuNi0wLjEsMS4xLTAuNywxLjEtMS4zYzAtMC42LTAuNS0xLjItMS4xLTEuM2wtOS44LTEuNmwwLTMuNg0KCWMwLTEuMi0wLjgtMS42LTEuOC0wLjlsLTguMiw2Yy0xLDAuNy0xLDEuOSwwLDIuNmw4LjMsNi4yYzEsMC43LDEuNywwLjMsMS43LTAuOUwxMy4yLDI2Ljh6Ii8+DQo8L3N2Zz4NCg==';

class communication {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'communication';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'communication';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const communicationState = sourceTarget.getCustomState(communication.STATE_KEY);
            if (communicationState) {
                newTarget.setCustomState(communication.STATE_KEY, Clone.simple(communicationState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    get SERIALPINS_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'ESP32': {
                return Config.setSerialPin.esp32;
            }
            case 'T-Watch': {
                return Config.setSerialPin.tWatch;
            }
            case 'Quon': {
                return Config.setSerialPin.quon;
            }
            case 'Quarky': {
                return Config.setSerialPin.quarky;
            }
        }
        return null;
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [
            {
                index: 1,
                allowedBoards: ['esp32', 'tWatch', 'quon', 'quarky'],
                getBlocks: () => [
                    {
                        opcode: 'setSerialPin',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'communication.setSerialPin',
                            default: 'set [SERIALTX] as TX and [SERIALRX] as RX for Serial[SERIALPORT]',
                            description: 'uart communication'
                        }),
                        arguments: {
                            SERIALRX: {
                                type: ArgumentType.NUMBER,
                                menu: 'serialPins',
                                defaultValue: '18'
                            },
                            SERIALTX: {
                                type: ArgumentType.NUMBER,
                                menu: 'serialPins',
                                defaultValue: '19'
                            },
                            SERIALPORT: {
                                type: ArgumentType.NUMBER,
                                menu: 'serialMenu',
                                defaultValue: '1'
                            }
                        }
                    }
                ]
            },
            {
                index: -1,
                allowedBoards: ['esp32', 'tWatch', 'quon', 'quarky'],
                getBlocks: (extensionId) => [
                    {
                        message: formatMessage({
                            id: 'communication.blockSeparatorMessage1',
                            default: 'Bluetooth (U)',
                            description: 'Blocks separator message'
                        }),

                    },
                    {
                        opcode: 'configureBluetooth',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'communication.configureBluetooth',
                            default: 'configure [BT_TYPE] with name [BLUETOOTH_NAME]',
                            description: 'Set Baud rate of the Serial'
                        }),
                        arguments: {
                            BLUETOOTH_NAME: {
                                type: ArgumentType.STRING,
                                defaultValue: 'MyEsp32'
                            },
                            BT_TYPE: {
                                type: ArgumentType.NUMBER,
                                menu: 'bluetoothType',
                                defaultValue: '1'
                            }
                        }
                    },
                    {
                        opcode: 'bluetoothAvailable',
                        blockType: BlockType.BOOLEAN,
                        text: formatMessage({
                            id: 'communication.bluetoothAvailable',
                            default: 'is data available on Bluetooth?',
                            description: 'Get number of available bytes on a Serial'
                        })
                    },
                    {
                        opcode: 'readBluetoothData',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'communication.readBluetoothData',
                            default: 'get bytes from Bluetooth',
                            description: 'Recieve bytes from bluetooth'
                        })
                    },
                    {
                        opcode: 'writeToBluetooth',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'communication.writeToBluetooth',
                            default: 'send [DATA] on Bluetooth',
                            description: 'transmit data from bluetooth'
                        }),
                        arguments: {
                            DATA: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Hello World'
                            }
                        }
                    },
                ]
            },
        ];
        let boardCommonExtensionList = [];
        let commonExtensionList = commonExtensions();
        for (let commonExtensionIndex in commonExtensionList) {
            let commonExtension = commonExtensionList[commonExtensionIndex];
            if (commonExtension.allowedBoards.includes(board)) {
                boardCommonExtensionList.push(commonExtension);
            }
        }
        return boardCommonExtensionList;
    }

    get SERIAL_PORT() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.serialPort.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.serialPort.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.serialPort.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.serialPort.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.serialPort.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.serialPort.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.serialPort.quon;
            }
            case 'TecBits': {
                return ExtensionMenu.serialPort.tecBits;
            }
            case 'Quarky': {
                return ExtensionMenu.serialPort.quarky;
            }
        }
        return ['0'];
    }

    get SERIAL_PINS() {
        switch (this.runtime.boardSelected) {
            case 'ESP32': {
                return ExtensionMenu.serialPins.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.serialPins.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.serialPins.quon;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'communication',
            name: formatMessage({
                id: 'communication.communication',
                default: 'Communication (U)',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#7f461b',
            colourSecondary: '#753d16',
            colourTertiary: '#603111',
            blocks: [
                {
                    opcode: 'setBaudRate',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'communication.setBaudRate',
                        default: 'set serial [SERIAL] baud rate to [BAUDRATE]',
                        description: 'Set Baud rate of the Serial'
                    }),
                    arguments: {
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        },
                        BAUDRATE: {
                            type: ArgumentType.STRING,
                            menu: 'baudRate',
                            defaultValue: '115200'
                        }
                    }
                },
                {
                    opcode: 'serialAvailable',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'communication.serialAvailable',
                        default: 'bytes available on serial [SERIAL]?',
                        description: 'Get number of available bytes on a Serial'
                    }),
                    arguments: {
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'serialRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'communication.serialRead',
                        default: 'read bytes on serial [SERIAL]',
                        description: 'Read bytes on a Serial'
                    }),
                    arguments: {
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getNumber',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'communication.getNumber',
                        default: 'get a number from serial [SERIAL]',
                        description: 'Read numeric value from received serial data'
                    }),
                    arguments: {
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'readString',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'communication.readString',
                        default: 'read bytes as a string from serial [SERIAL]',
                        description: 'Read numeric value from received serial data'
                    }),
                    arguments: {
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'writeToSerial',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'communication.writeToSerial',
                        default: 'write [DATA] on serial [SERIAL]',
                        description: 'Write text on Serial'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Hello World'
                        },
                        SERIAL: {
                            type: ArgumentType.STRING,
                            menu: 'serial',
                            defaultValue: '0'
                        }
                    }
                }
            ],
            menus: {
                serial: this.SERIAL_PORT,
                serialPins: this.SERIAL_PINS,
                baudRate: ['9600', '19200', '38400', '57600', '74880', '115200', '250000'],
                bluetoothType: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.bluetoothType.option1',
                            default: 'BT Classic',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.bluetoothType.option2',
                            default: 'BLE',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                serialMenu: ['1', '2']
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    setSerialPin(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setSerialPin(args, util, this);
        }
        return RealtimeMode.setSerialPin(args, util, this);
    }

    setBaudRate(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setBaudRate(args, util, this);
        }
        return RealtimeMode.setBaudRate(args, util, this);
    }

    serialRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.serialRead(args, util, this);
        }
        return RealtimeMode.serialRead(args, util, this);
    }

    getNumber(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getNumber(args, util, this);
        }
        return RealtimeMode.getNumber(args, util, this);
    }

    readString(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readString(args, util, this);
        }
        return RealtimeMode.readString(args, util, this);
    }

    serialAvailable(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.serialAvailable(args, util, this);
        }
        return RealtimeMode.serialAvailable(args, util, this);
    }

    writeToSerial(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.writeToSerial(args, util, this);
        }
        return RealtimeMode.writeToSerial(args, util, this);
    }

    configureBluetooth(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.configureBluetooth(args, util, this);
        }
        return RealtimeMode.configureBluetooth(args, util, this);
    }

    readBluetoothData(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readBluetoothData(args, util, this);
        }
        return RealtimeMode.readBluetoothData(args, util, this);
    }

    writeToBluetooth(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.writeToBluetooth(args, util, this);
        }
        return RealtimeMode.writeToBluetooth(args, util, this);
    }

    bluetoothAvailable(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.bluetoothAvailable(args, util, this);
        }
        return RealtimeMode.bluetoothAvailable(args, util, this);
    }

}

module.exports = communication;
