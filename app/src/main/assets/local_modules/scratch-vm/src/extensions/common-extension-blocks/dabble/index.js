const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const Config = require('./config.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGRkZGRkY7fQ0KCS5zdDF7ZmlsbDojMDA1NUQxO30NCgkuc3Qye2ZpbGw6IzAwQ0VCMDt9DQo8L3N0eWxlPg0KPGcgaWQ9IlhNTElEXzg3XyI+DQoJPGNpcmNsZSBpZD0iWE1MSURfODZfIiBjbGFzcz0ic3QwIiBjeD0iMjAiIGN5PSIyMCIgcj0iMTYiLz4NCgk8ZyBpZD0iWE1MSURfNDBfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzc1XyIgY2xhc3M9InN0MSIgZD0iTTIyLjEsMTcuNnYwLjFoMEMyMi4xLDE3LjcsMjIuMSwxNy43LDIyLjEsMTcuNnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzczXyIgY2xhc3M9InN0MiIgZD0iTTMwLjEsMjdjLTAuMS0wLjItMC4yLTAuNS0wLjItMC44VjIwaDRjMCwxLjUtMC40LDIuOC0xLjEsNEMzMi4yLDI1LjIsMzEuMiwyNi4yLDMwLjEsMjd6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80Nl8iIGNsYXNzPSJzdDIiIGQ9Ik0yNS44LDExLjhjLTIuMiwwLTQuMiwwLjktNS43LDIuM2MwLjgsMC44LDEuNCwxLjcsMS45LDIuOGMwLjEsMC4yLDAuMiwwLjUsMC4yLDAuOA0KCQkJYzAsMCwwLDAuMSwwLDAuMWMwLDAuMiwwLDAuMy0wLjEsMC41YzAuNy0xLjQsMi4xLTIuNCwzLjgtMi40YzIuMywwLDQuMSwxLjksNC4xLDQuMWg0QzMzLjksMTUuNSwzMC4zLDExLjgsMjUuOCwxMS44eg0KCQkJIE0xOC4yLDE4LjRjMCwwLDAtMC4xLTAuMS0wLjFjMC4yLDAuNywwLjksMS4zLDEuNywxLjRDMTkuMSwxOS41LDE4LjUsMTkuMSwxOC4yLDE4LjR6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80NF8iIGNsYXNzPSJzdDEiIGQ9Ik0yMiwyMS44Yy0wLjEtMC4yLTAuMi0wLjQtMC4zLTAuNWMwLDAsMCwwLDAsMGMtMC4zLTAuNC0wLjctMC42LTEuMS0wLjcNCgkJCWMtMC4xLDAtMC4yLDAtMC4zLTAuMWMwLDAsMCwwLDAsMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwaDBjMCwwLDAsMC0wLjEsMGMwLDAsMCwwLDAsMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwDQoJCQljMCwwLDAsMCwwLDBjLTAuOCwwLjEtMS40LDAuNS0xLjcsMS4zYzAsMCwwLDAuMS0wLjEsMC4xYy0wLjEsMC40LTAuMSwwLjksMCwxLjRjMC4xLDAuMywwLjMsMC42LDAuNCwwLjkNCgkJCWMwLjQsMC43LDAuOSwxLjMsMS40LDEuOGMwLjgtMC44LDEuNC0xLjcsMS45LTIuOEMyMi4yLDIyLjcsMjIuMSwyMi4yLDIyLDIxLjh6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80M18iIGNsYXNzPSJzdDEiIGQ9Ik0yMi4xLDE3LjZjMCwwLDAsMC4xLDAsMC4xYzAsMC4yLDAsMC4zLTAuMSwwLjVjMCwwLDAsMC4xLTAuMSwwLjFjLTAuMywwLjctMSwxLjItMS43LDEuMw0KCQkJYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAtMC4xLDBoMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwYzAsMC0wLjEsMC0wLjEsMA0KCQkJYy0wLjctMC4xLTEuNC0wLjYtMS43LTEuMmMwLDAsMC0wLjEtMC4xLTAuMWMtMC43LTEuNC0yLjEtMi40LTMuOC0yLjRjLTIuMywwLTQuMSwxLjktNC4xLDQuMWMwLDIuMywxLjksNC4xLDQuMSw0LjENCgkJCWMxLjcsMCwzLjEtMSwzLjgtMi40Yy0wLjEsMC40LTAuMSwwLjksMCwxLjRjMC4xLDAuMywwLjMsMC42LDAuNCwwLjljMC40LDAuNywwLjksMS4zLDEuNCwxLjhjLTEuNSwxLjQtMy41LDIuMy01LjcsMi4zDQoJCQljLTQuNSwwLTguMi0zLjctOC4yLTguMnMzLjctOC4yLDguMi04LjJjMS4zLDAsMi42LDAuMywzLjcsMC45YzAuNywwLjQsMS40LDAuOSwyLDEuNGMwLjgsMC44LDEuNCwxLjcsMS45LDIuOA0KCQkJQzIyLjEsMTcuMSwyMi4xLDE3LjQsMjIuMSwxNy42eiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfNDJfIiBjbGFzcz0ic3QxIiBkPSJNMjIsMTguMmMtMC4yLDAuNi0wLjgsMS4yLTEuNSwxLjNjLTAuMSwwLTAuMiwwLTAuMywwYzAuOC0wLjEsMS40LTAuNSwxLjctMS4zDQoJCQlDMjIsMTguMywyMiwxOC4zLDIyLDE4LjJ6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80MV8iIGNsYXNzPSJzdDIiIGQ9Ik0zMy45LDIwTDMzLjksMjBjMCwxLjUtMC40LDIuOC0xLjEsNGMtMC43LDEuMi0xLjYsMi4yLTIuOCwyLjljLTEuMywwLjgtMi43LDEuMi00LjMsMS4yDQoJCQljLTIuMiwwLTQuMi0wLjktNS43LTIuM2MtMC42LTAuNS0xLTEuMi0xLjQtMS44Yy0wLjItMC4zLTAuMy0wLjYtMC40LTAuOWMtMC4yLTAuNS0wLjItMSwwLTEuNGMwLjItMC42LDAuOC0xLjIsMS40LTEuMw0KCQkJYzAuMSwwLDAuMiwwLDAuMy0wLjFjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLjEsMGMwLDAsMCwwLDAsMGMwLDAsMCwwLDAuMSwwaDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLjEsMA0KCQkJYzAsMCwwLDAsMCwwYzAsMCwwLjEsMCwwLjEsMGMwLDAsMCwwLDAsMGMwLjMsMCwwLjYsMC4xLDAuOSwwLjNjMC4yLDAuMSwwLjQsMC4zLDAuNSwwLjVjMCwwLDAsMCwwLDBjMC4xLDAuMSwwLjIsMC4yLDAuMiwwLjQNCgkJCWMwLDAuMSwwLDAuMSwwLjEsMC4yYzAuMSwwLjMsMC4zLDAuNiwwLjUsMC44YzAuNiwwLjcsMS40LDEuMiwyLjMsMS40YzAuMywwLjEsMC42LDAuMSwxLDAuMWMwLjMsMCwwLjYsMCwxLTAuMQ0KCQkJYzEuOC0wLjQsMy4xLTIsMy4yLTMuOWMwLDAsMC0wLjEsMC0wLjF2MEgzMy45eiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGRkZGRkY7fQ0KCS5zdDF7ZmlsbDojMDA1NUQxO30NCgkuc3Qye2ZpbGw6IzAwQ0VCMDt9DQo8L3N0eWxlPg0KPGcgaWQ9IlhNTElEXzg3XyI+DQoJPGNpcmNsZSBpZD0iWE1MSURfODZfIiBjbGFzcz0ic3QwIiBjeD0iMjAiIGN5PSIyMCIgcj0iMTYiLz4NCgk8ZyBpZD0iWE1MSURfNDBfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzc1XyIgY2xhc3M9InN0MSIgZD0iTTIyLjEsMTcuNnYwLjFoMEMyMi4xLDE3LjcsMjIuMSwxNy43LDIyLjEsMTcuNnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzczXyIgY2xhc3M9InN0MiIgZD0iTTMwLjEsMjdjLTAuMS0wLjItMC4yLTAuNS0wLjItMC44VjIwaDRjMCwxLjUtMC40LDIuOC0xLjEsNEMzMi4yLDI1LjIsMzEuMiwyNi4yLDMwLjEsMjd6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80Nl8iIGNsYXNzPSJzdDIiIGQ9Ik0yNS44LDExLjhjLTIuMiwwLTQuMiwwLjktNS43LDIuM2MwLjgsMC44LDEuNCwxLjcsMS45LDIuOGMwLjEsMC4yLDAuMiwwLjUsMC4yLDAuOA0KCQkJYzAsMCwwLDAuMSwwLDAuMWMwLDAuMiwwLDAuMy0wLjEsMC41YzAuNy0xLjQsMi4xLTIuNCwzLjgtMi40YzIuMywwLDQuMSwxLjksNC4xLDQuMWg0QzMzLjksMTUuNSwzMC4zLDExLjgsMjUuOCwxMS44eg0KCQkJIE0xOC4yLDE4LjRjMCwwLDAtMC4xLTAuMS0wLjFjMC4yLDAuNywwLjksMS4zLDEuNywxLjRDMTkuMSwxOS41LDE4LjUsMTkuMSwxOC4yLDE4LjR6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80NF8iIGNsYXNzPSJzdDEiIGQ9Ik0yMiwyMS44Yy0wLjEtMC4yLTAuMi0wLjQtMC4zLTAuNWMwLDAsMCwwLDAsMGMtMC4zLTAuNC0wLjctMC42LTEuMS0wLjcNCgkJCWMtMC4xLDAtMC4yLDAtMC4zLTAuMWMwLDAsMCwwLDAsMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwaDBjMCwwLDAsMC0wLjEsMGMwLDAsMCwwLDAsMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwDQoJCQljMCwwLDAsMCwwLDBjLTAuOCwwLjEtMS40LDAuNS0xLjcsMS4zYzAsMCwwLDAuMS0wLjEsMC4xYy0wLjEsMC40LTAuMSwwLjksMCwxLjRjMC4xLDAuMywwLjMsMC42LDAuNCwwLjkNCgkJCWMwLjQsMC43LDAuOSwxLjMsMS40LDEuOGMwLjgtMC44LDEuNC0xLjcsMS45LTIuOEMyMi4yLDIyLjcsMjIuMSwyMi4yLDIyLDIxLjh6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80M18iIGNsYXNzPSJzdDEiIGQ9Ik0yMi4xLDE3LjZjMCwwLDAsMC4xLDAsMC4xYzAsMC4yLDAsMC4zLTAuMSwwLjVjMCwwLDAsMC4xLTAuMSwwLjFjLTAuMywwLjctMSwxLjItMS43LDEuMw0KCQkJYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAsMCwwYzAsMCwwLDAtMC4xLDBoMGMwLDAsMCwwLTAuMSwwYzAsMCwwLDAsMCwwYzAsMC0wLjEsMC0wLjEsMA0KCQkJYy0wLjctMC4xLTEuNC0wLjYtMS43LTEuMmMwLDAsMC0wLjEtMC4xLTAuMWMtMC43LTEuNC0yLjEtMi40LTMuOC0yLjRjLTIuMywwLTQuMSwxLjktNC4xLDQuMWMwLDIuMywxLjksNC4xLDQuMSw0LjENCgkJCWMxLjcsMCwzLjEtMSwzLjgtMi40Yy0wLjEsMC40LTAuMSwwLjksMCwxLjRjMC4xLDAuMywwLjMsMC42LDAuNCwwLjljMC40LDAuNywwLjksMS4zLDEuNCwxLjhjLTEuNSwxLjQtMy41LDIuMy01LjcsMi4zDQoJCQljLTQuNSwwLTguMi0zLjctOC4yLTguMnMzLjctOC4yLDguMi04LjJjMS4zLDAsMi42LDAuMywzLjcsMC45YzAuNywwLjQsMS40LDAuOSwyLDEuNGMwLjgsMC44LDEuNCwxLjcsMS45LDIuOA0KCQkJQzIyLjEsMTcuMSwyMi4xLDE3LjQsMjIuMSwxNy42eiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfNDJfIiBjbGFzcz0ic3QxIiBkPSJNMjIsMTguMmMtMC4yLDAuNi0wLjgsMS4yLTEuNSwxLjNjLTAuMSwwLTAuMiwwLTAuMywwYzAuOC0wLjEsMS40LTAuNSwxLjctMS4zDQoJCQlDMjIsMTguMywyMiwxOC4zLDIyLDE4LjJ6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF80MV8iIGNsYXNzPSJzdDIiIGQ9Ik0zMy45LDIwTDMzLjksMjBjMCwxLjUtMC40LDIuOC0xLjEsNGMtMC43LDEuMi0xLjYsMi4yLTIuOCwyLjljLTEuMywwLjgtMi43LDEuMi00LjMsMS4yDQoJCQljLTIuMiwwLTQuMi0wLjktNS43LTIuM2MtMC42LTAuNS0xLTEuMi0xLjQtMS44Yy0wLjItMC4zLTAuMy0wLjYtMC40LTAuOWMtMC4yLTAuNS0wLjItMSwwLTEuNGMwLjItMC42LDAuOC0xLjIsMS40LTEuMw0KCQkJYzAuMSwwLDAuMiwwLDAuMy0wLjFjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLjEsMGMwLDAsMCwwLDAsMGMwLDAsMCwwLDAuMSwwaDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLDBjMCwwLDAsMCwwLjEsMA0KCQkJYzAsMCwwLDAsMCwwYzAsMCwwLjEsMCwwLjEsMGMwLDAsMCwwLDAsMGMwLjMsMCwwLjYsMC4xLDAuOSwwLjNjMC4yLDAuMSwwLjQsMC4zLDAuNSwwLjVjMCwwLDAsMCwwLDBjMC4xLDAuMSwwLjIsMC4yLDAuMiwwLjQNCgkJCWMwLDAuMSwwLDAuMSwwLjEsMC4yYzAuMSwwLjMsMC4zLDAuNiwwLjUsMC44YzAuNiwwLjcsMS40LDEuMiwyLjMsMS40YzAuMywwLjEsMC42LDAuMSwxLDAuMWMwLjMsMCwwLjYsMCwxLTAuMQ0KCQkJYzEuOC0wLjQsMy4xLTIsMy4yLTMuOWMwLDAsMC0wLjEsMC0wLjF2MEgzMy45eiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';


class dabble {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'dabble';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'dabble';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const dabbleState = sourceTarget.getCustomState(dabble.STATE_KEY);
            if (dabbleState) {
                newTarget.setCustomState(dabble.STATE_KEY, Clone.simple(dabbleState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [
            {
                index: 0,
                allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'setBaudRate',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.setBaudRate',
                            default: 'set Bluetooth baud rate to [BAUDRATE]',
                            description: 'Send data to terminal'
                        }),
                        arguments: {
                            BAUDRATE: {
                                type: ArgumentType.STRING,
                                menu: 'baudRate',
                                defaultValue: this.BLE_DEFAULT_BAUDRATE
                            }
                        }
                    }
                ]
            },
            {
                index: 0,
                allowedBoards: ['esp32', 'tWatch', 'quon', 'quarky'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'setBluetoothName',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.setBluetoothName',
                            default: 'set Bluetooth name to [BLUETOOTH_NAME]',
                            description: 'set name of Bluetooth'
                        }),
                        arguments: {
                            BLUETOOTH_NAME: {
                                type: ArgumentType.STRING,
                                defaultValue: this.BLE_DEFAULT_NAME
                            }
                        }
                    }
                ]
            },
            // {
            //     index: 2,
            //     allowedBoards: ['esp32', 'tWatch','quon','evive','arduinoMega','arduinoNano','arduinoUno'],
            //     getBlocks: (extensionId) => [
            //         {
            //             opcode: 'waitForAppConnection',
            //             blockType: BlockType.COMMAND,
            //             text: formatMessage({
            //                 id: 'dabble.waitForAppConnection',
            //                 default: 'wait for App connection',
            //                 description: 'wait for bluetooth connection'
            //             })
            //         }
            //     ]
            // },
            {
                index: 14,
                allowedBoards: ['evive'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'customDabbleMotorControl',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.customDabbleMotorControl',
                            default: 'enable motor channel [MOTOR_NO]',
                            description: 'Control servo through Dabble'
                        }),
                        arguments: {
                            MOTOR_NO: {
                                type: ArgumentType.NUMBER,
                                menu: 'motorNumber',
                                defaultValue: '1'
                            }
                        }
                    },
                ]
            },
            {
                index: 14,
                allowedBoards: ['arduinoUno', 'arduinoMega', 'arduinoNano', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'dabbleMotorControl',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.dabbleMotorControl',
                            default: 'enable motor[MOTOR_NO] connected to DIR pin [DIR1],[DIR2] & PWM pin [PWM]',
                            description: 'Control servo through Dabble'
                        }),
                        arguments: {
                            MOTOR_NO: {
                                type: ArgumentType.NUMBER,
                                menu: 'motorNumber',
                                defaultValue: '1'
                            },
                            DIR1: {
                                type: ArgumentType.NUMBER,
                                menu: 'digitalPins',
                                defaultValue: this.MOTOR_DIR1_DEFAULT_PIN
                            },
                            DIR2: {
                                type: ArgumentType.NUMBER,
                                menu: 'digitalPins',
                                defaultValue: this.MOTOR_DIR2_DEFAULT_PIN
                            },
                            PWM: {
                                type: ArgumentType.NUMBER,
                                menu: 'pwmPins',
                                defaultValue: this.MOTOR_PWM_DEFAULT_PIN
                            }
                        }
                    },
                ]
            },
            {
                index: 15,
                allowedBoards: ['esp32', 'tWatch', 'quon', 'quarky'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'espDabbleMotorControl',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.espDabbleMotorControl',
                            default: 'enable motor[MOTOR_NO] connected to DIR pin [DIR1],[DIR2] & PWM pin [PWM]',
                            description: 'Control servo through Dabble'
                        }),
                        arguments: {
                            MOTOR_NO: {
                                type: ArgumentType.NUMBER,
                                menu: 'motorNumber',
                                defaultValue: '1'
                            },
                            DIR1: {
                                type: ArgumentType.NUMBER,
                                menu: 'digitalPins',
                                defaultValue: this.MOTOR_DIR1_DEFAULT_PIN
                            },
                            DIR2: {
                                type: ArgumentType.NUMBER,
                                menu: 'digitalPins',
                                defaultValue: this.MOTOR_DIR2_DEFAULT_PIN
                            },
                            PWM: {
                                type: ArgumentType.NUMBER,
                                menu: 'pwmPins',
                                defaultValue: this.MOTOR_PWM_DEFAULT_PIN
                            }
                        }
                    },
                ]
            },
            {
                index: 38,
                allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        message: formatMessage({
                            id: 'dabble.blockSeparatorMessage1',
                            default: 'Oscilloscope (U)',
                            description: 'Blocks separator message'
                        })
                    },
                    {
                        opcode: 'sendDataToOscilloscope',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.sendDataToOscilloscope',
                            default: 'send data [OSCILLOSCOPE_DATA] on channel [CHANNEL]',
                            description: 'change bluetooth for Oscilloscope'
                        }),
                        arguments: {
                            OSCILLOSCOPE_DATA: {
                                type: ArgumentType.NUMBER,
                                defaultValue: 0
                            },
                            CHANNEL: {
                                type: ArgumentType.NUMBER,
                                menu: 'oscilloscopeChannel',
                                defaultValue: 1
                            },
                        }
                    },

                ]
            },
            {
                index: 39,
                allowedBoards: ['evive', 'arduinoUno', 'arduinoNano', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'baudrateOscilloscope',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.baudrateOscilloscope',
                            default: 'set Bluetooth baudrate to [OSC_BAUDRATE] for Oscilloscope',
                            description: 'set bluetooth baudrate for Oscilloscope Module'
                        }),
                        arguments: {
                            OSC_BAUDRATE: {
                                type: ArgumentType.STRING,
                                menu: 'baudRate',
                                defaultValue: this.BLE_DEFAULT_BAUDRATE
                            }
                        }
                    }
                ]
            },
            {
                index: 39,
                allowedBoards: ['arduinoMega'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'baudrateOscilloscopeMega',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'dabble.baudrateOscilloscopeMega',
                            default: 'connect Bluetooth on serial [SERIAL] at baudrate [OSC_BAUDRATE]',
                            description: 'set bluetooth baudrate for Oscilloscope Module'
                        }),
                        arguments: {
                            SERIAL: {
                                type: ArgumentType.NUMBER,
                                menu: 'serialMenu',
                                defaultValue: '3'
                            },
                            OSC_BAUDRATE: {
                                type: ArgumentType.STRING,
                                menu: 'baudRate',
                                defaultValue: this.BLE_DEFAULT_BAUDRATE
                            }
                        }
                    }
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

    get SERVO_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.servoChannel.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.servoChannel.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.servoChannel.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.servoChannel.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.servoChannel.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.servoChannel.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.servoChannel.quon;
            }
            case 'TecBits': {
                return ExtensionMenu.servoChannel.tecBits;
            }
            case 'Quarky': {
                return ExtensionMenu.servoChannel.quarky;
            }
        }
        return ['0'];
    }

    get DIGITAL_PIN() {
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
            case 'T-Watch': {
                return ExtensionMenu.digitalPins.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.digitalPins.quon;
            }
            case 'TecBits': {
                return ExtensionMenu.digitalPins.tecBits;
            }
            case 'Quarky': {
                return ExtensionMenu.digitalPins.quarky;
            }
        }
        return ['0'];
    }

    get PWM_PIN() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.pwmPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.pwmPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.pwmPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.pwmPins.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.pwmPins.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.pwmPins.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.pwmPins.quon;
            }
            case 'TecBits': {
                return ExtensionMenu.pwmPins.tecBits;
            }
            case 'Quarky': {
                return ExtensionMenu.pwmPins.quarky;
            }
        }
        return ['0'];
    }

    get SERVO_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.servoChannel.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.servoChannel.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.servoChannel.arduinoNano;
            }
            case 'evive': {
                return Config.servoChannel.evive;
            }
            case 'ESP32': {
                return Config.servoChannel.esp32;
            }
            case 'T-Watch': {
                return Config.servoChannel.tWatch;
            }
            case 'Quon': {
                return Config.servoChannel.quon;
            }
            case 'TecBits': {
                return Config.servoChannel.tecBits;
            }
            case 'Quarky': {
                return Config.servoChannel.quarky;
            }
        }
        return ['0'];
    }

    get BLE_DEFAULT_BAUDRATE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.baudRate.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.baudRate.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.baudRate.arduinoNano;
            }
            case 'evive': {
                return Config.baudRate.evive;
            }
            case 'TecBits': {
                return Config.baudRate.tecBits;
            }
        }
        return ['0'];
    }

    get BLE_DEFAULT_NAME() {
        switch (this.runtime.boardSelected) {
            case 'ESP32': {
                return Config.bleName.esp32;
            }
            case 'T-Watch': {
                return Config.bleName.tWatch;
            }
            case 'Quon': {
                return Config.bleName.quon;
            }
            case 'Quarky': {
                return Config.bleName.quarky;
            }
        }
        return ['0'];
    }

    get MOTOR_DIR1_DEFAULT_PIN() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.dir1Pin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.dir1Pin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.dir1Pin.arduinoNano;
            }
            case 'ESP32': {
                return Config.dir1Pin.esp32;
            }
            case 'T-Watch': {
                return Config.dir1Pin.tWatch;
            }
            case 'Quon': {
                return Config.dir1Pin.quon;
            }
            case 'TecBits': {
                return Config.dir1Pin.tecBits;
            }
        }
        return ['0'];
    }

    get MOTOR_DIR2_DEFAULT_PIN() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.dir2Pin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.dir2Pin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.dir2Pin.arduinoNano;
            }
            case 'ESP32': {
                return Config.dir2Pin.esp32;
            }
            case 'T-Watch': {
                return Config.dir2Pin.tWatch;
            }
            case 'Quon': {
                return Config.dir2Pin.quon;
            }
            case 'TecBits': {
                return Config.dir2Pin.tecBits;
            }
        }
        return ['0'];
    }

    get MOTOR_PWM_DEFAULT_PIN() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.pwmPin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.pwmPin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.pwmPin.arduinoNano;
            }
            case 'ESP32': {
                return Config.pwmPin.esp32;
            }
            case 'T-Watch': {
                return Config.pwmPin.tWatch;
            }
            case 'Quon': {
                return Config.pwmPin.quon;
            }
            case 'TecBits': {
                return Config.pwmPin.tecBits;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'dabble',
            name: formatMessage({
                id: 'dabble.dabble',
                default: 'Dabble',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#a81a8a',
            colourSecondary: '#870c66',
            colourTertiary: '#680553',
            blocks: [
                {
                    opcode: 'dabbleRefresh',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.dabbleRefresh',
                        default: 'refresh data',
                        description: 'Refresh data'
                    })
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage2',
                        default: 'LED Brightness Control Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'enableLEDControl',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.enableLEDControl',
                        default: 'enable LED control',
                        description: 'Send data to terminal'
                    })
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage3',
                        default: 'Terminal Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'terminalCheck',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'dabble.terminalCheck',
                        default: 'is data from terminal [DATA]',
                        description: 'Whether data is there on the terminal'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: 'hi'
                        }
                    }
                },
                {
                    opcode: 'terminalRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'dabble.terminalRead',
                        default: 'get number from terminal',
                        description: 'Read terminal'
                    })
                },
                {
                    opcode: 'terminalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.terminalWrite',
                        default: 'send [DATA] to terminal',
                        description: 'Send data to terminal'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Hello!'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage4',
                        default: 'Gamepad Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'getGamepadOne',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'dabble.getGamepadOne',
                        default: 'is [GAMEPAD_BUTTON] pressed on gamepad?',
                        description: 'Get gamepad one values'
                    }),
                    arguments: {
                        GAMEPAD_BUTTON: {
                            type: ArgumentType.STRING,
                            menu: 'gamepadControls',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getGamepadTwo',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'dabble.getGamepadTwo',
                        default: 'get [JOYSTICK_BUTTON] from gamepad',
                        description: 'Get gamepad two values'
                    }),
                    arguments: {
                        JOYSTICK_BUTTON: {
                            type: ArgumentType.STRING,
                            menu: 'joystickControls',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage5',
                        default: 'Pin State Monitor Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'enablePinStateMonitor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.enablePinStateMonitor',
                        default: 'enable pin state monitor',
                        description: 'Enable pin state Monitor'
                    })
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage6',
                        default: 'Motor Control Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'dabbleServoControl',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.dabbleServoControl',
                        default: 'enable Servo[SERVO_NO] for servo connected to [SERVO_PIN]',
                        description: 'Control servo through Dabble'
                    }),
                    arguments: {
                        SERVO_NO: {
                            type: ArgumentType.NUMBER,
                            menu: 'motorNumber',
                            defaultValue: '1'
                        },
                        SERVO_PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoPins',
                            defaultValue: this.SERVO_PIN_DEFAULT_VALUE
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage7',
                        default: 'Inputs Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'tactileSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'dabble.tactileSwitch',
                        default: 'is tactile switch [TACTILE_SWITCH] pressed?',
                        description: 'Read tactile switch'
                    }),
                    arguments: {
                        TACTILE_SWITCH: {
                            type: ArgumentType.NUMBER,
                            menu: 'tactileSwitch',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'slideSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'dabble.slideSwitch',
                        default: 'is slide switch [SLIDE_SWITCH] [SWITCH_DIRECTION] ?',
                        description: 'Read slide switch'
                    }),
                    arguments: {
                        SLIDE_SWITCH: {
                            type: ArgumentType.NUMBER,
                            menu: 'slideSwitch',
                            defaultValue: '1'
                        },
                        SWITCH_DIRECTION: {
                            type: ArgumentType.STRING,
                            menu: 'slideSwitchDir',
                            defaultValue: '2'
                        }
                    }
                },
                {
                    opcode: 'potentiometer',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'dabble.potentiometer',
                        default: 'get potentiometer [POTENTIOMETER] value',
                        description: 'Read potentiometer'
                    }),
                    arguments: {
                        POTENTIOMETER: {
                            type: ArgumentType.NUMBER,
                            menu: 'potentiometer',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage8',
                        default: 'Phone Sensors Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'readDabbleSensor',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'dabble.readDabbleSensor',
                        default: 'get [SENSOR] sensors reading',
                        description: 'Read dabble sensors'
                    }),
                    arguments: {
                        SENSOR: {
                            type: ArgumentType.STRING,
                            menu: 'sensors',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage9',
                        default: 'Camera Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'cameraSettings',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.cameraSettings',
                        default: 'set camera flash to [FLASH_MODE], quality to [QUALITY] & zoom to [ZOOM] %',
                        description: 'Set camera settings'
                    }),
                    arguments: {
                        FLASH_MODE: {
                            type: ArgumentType.NUMBER,
                            menu: 'flashMode',
                            defaultValue: '1'
                        },
                        QUALITY: {
                            type: ArgumentType.NUMBER,
                            menu: 'quality',
                            defaultValue: '1'
                        },
                        ZOOM: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'rotateCamera',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.rotateCamera',
                        default: 'rotate camera to [SIDE] side',
                        description: 'Rotate camera'
                    }),
                    arguments: {
                        SIDE: {
                            type: ArgumentType.NUMBER,
                            menu: 'cameraSide',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'cameraAction',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.cameraAction',
                        default: '[CAMERA_ACTION] on camera',
                        description: 'Capture images and record videos'
                    }),
                    arguments: {
                        CAMERA_ACTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'cameraAction',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage10',
                        default: 'Color Detector Module (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'colorDetectorSettings',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.colorDetectorSettings',
                        default: 'set grid size [GRID_VAL], calculation mode [CALC_MOD] & color scheme [SCHEME_VAL]',
                        description: 'Set color detector settings'
                    }),
                    arguments: {
                        GRID_VAL: {
                            type: ArgumentType.NUMBER,
                            menu: 'grid',
                            defaultValue: '1'
                        },
                        CALC_MOD: {
                            type: ArgumentType.NUMBER,
                            menu: 'calcMode',
                            defaultValue: '1'
                        },
                        SCHEME_VAL: {
                            type: ArgumentType.NUMBER,
                            menu: 'scheme',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'getColorValue',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'dabble.getColorValue',
                        default: 'get [COLOR_VALUE] color value for cell row [ROW] col [COLUMN]',
                        description: 'Get color Value'
                    }),
                    arguments: {
                        COLOR_VALUE: {
                            type: ArgumentType.NUMBER,
                            menu: 'colorValue',
                            defaultValue: '1'
                        },
                        ROW: {
                            type: ArgumentType.NUMBER,
                            menu: 'rowValue',
                            defaultValue: '1'
                        },
                        COLUMN: {
                            type: ArgumentType.NUMBER,
                            menu: 'colValue',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage11',
                        default: 'IoT Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'createDataLoggerFile',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.createDataLoggerFile',
                        default: 'create [FILETYPE] named [FILENAME]',
                        description: 'Create a data logging File'
                    }),
                    arguments: {
                        FILETYPE: {
                            type: ArgumentType.NUMBER,
                            menu: 'fileMenu',
                            defaultValue: '1'
                        },
                        FILENAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'fileName'
                        }
                    }
                },
                {
                    opcode: 'sendDatatoDataLogger',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.sendDatatoDataLogger',
                        default: 'log [COLUMN_NAME] with data [DATA]',
                        description: 'Send column data'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        COLUMN_NAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'enter column name'
                        }
                    }
                },
                {
                    opcode: 'stopDataLogger',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.stopDataLogger',
                        default: 'stop data logger',
                        description: 'Close data logger and save File'
                    })
                },
                {
                    opcode: 'sendNotification',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.sendNotification',
                        default: 'send notification titled [TITLE] & message [NOTIFICATION_MESSAGE]',
                        description: 'create notifications in phone'
                    }),
                    arguments: {
                        TITLE: {
                            type: ArgumentType.STRING,
                            defaultValue: 'title'
                        },
                        NOTIFICATION_MESSAGE: {
                            type: ArgumentType.STRING,
                            defaultValue: 'message'
                        }
                    }
                },
                {
                    opcode: 'clearNotification',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.clearNotification',
                        default: 'clear notification',
                        description: 'clear Notification'
                    }),
                },
                {
                    message: formatMessage({
                        id: 'dabble.blockSeparatorMessage12',
                        default: 'Music Module',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'playMusic',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.playMusic',
                        default: '[MUSIC_TASK] music file [FILENAME]',
                        description: 'play music in Dabble'
                    }),
                    arguments: {
                        MUSIC_TASK: {
                            type: ArgumentType.NUMBER,
                            menu: 'musicFunctions',
                            defaultValue: '1'
                        },
                        FILENAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'C4'
                        }
                    }
                },
                {
                    opcode: 'stopMusic',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'dabble.stopMusic',
                        default: 'stop music',
                        description: 'stop all music files'
                    })
                }
            ],
            menus: {
                servoPins: this.SERVO_PINS,
                digitalPins: this.DIGITAL_PIN,
                pwmPins: this.PWM_PIN,
                gamepadControls: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option1',
                            default: 'up',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option2',
                            default: 'left',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option3',
                            default: 'right',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option4',
                            default: 'down',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option5',
                            default: 'triangle',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option6',
                            default: 'circle',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option7',
                            default: 'cross',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option8',
                            default: 'square',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option9',
                            default: 'start',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.gamepadControls.option10',
                            default: 'select',
                            description: 'Menu'
                        }), value: '5'
                    }
                ],
                joystickControls: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.joystickControls.option1',
                            default: 'X value',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.joystickControls.option2',
                            default: 'Y value',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.joystickControls.option3',
                            default: 'angle',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.joystickControls.option4',
                            default: 'radial distance',
                            description: 'Menu'
                        }), value: '1'
                    }
                ],
                tactileSwitch: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                slideSwitch: ['1', '2'],
                slideSwitchDir: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.slideSwitchDir.option1',
                            default: 'left',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.slideSwitchDir.option2',
                            default: 'right',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                potentiometer: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                baudRate: ['9600', '19200', '38400', '57600', '74880', '115200', '250000'],
                sensors: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option1',
                            default: 'accelerometer X',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option2',
                            default: 'accelerometer Y',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option3',
                            default: 'accelerometer Z',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option4',
                            default: 'gyroscope X',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option5',
                            default: 'gyroscope Y',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option6',
                            default: 'gyroscope Z',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option7',
                            default: 'magnetometer X',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option8',
                            default: 'magnetometer Y',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option9',
                            default: 'magnetometer Z',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option10',
                            default: 'proximity',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option11',
                            default: 'temperature',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option12',
                            default: 'sound',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option13',
                            default: 'barometer',
                            description: 'Menu'
                        }), value: '12'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option14',
                            default: 'GPS Longitude',
                            description: 'Menu'
                        }), value: '14'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option15',
                            default: 'GPS Latitude',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.option16',
                            default: 'light',
                            description: 'Menu'
                        }), value: '15'
                    }
                ],
                colorValue: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option1',
                            default: 'red',
                            description: 'Menu'
                        }),
                        value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option2',
                            default: 'green',
                            description: 'Menu'
                        }),
                        value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option3',
                            default: 'blue',
                            description: 'Menu'
                        }),
                        value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option4',
                            default: 'black',
                            description: 'Menu'
                        }),
                        value: '4'
                    }
                ],
                rowValue: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' },
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' }
                ],
                colValue: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' },
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' }
                ],
                grid: [
                    { text: '1x1', value: '1' },
                    { text: '3x3', value: '2' },
                    { text: '5x5', value: '3' }
                ],
                calcMode: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.calcMode.option1',
                            default: 'dominant',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.calcMode.option2',
                            default: 'average',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                scheme: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option1',
                            default: '24bit RGB',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option2',
                            default: '15bit RGB',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option3',
                            default: '3bit RGB',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option4',
                            default: '8bit Grayscale',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option5',
                            default: '4bit Grayscale',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.scheme.option6',
                            default: '1bit Grayscale',
                            description: 'Menu'
                        }), value: '6'
                    }
                ],
                flashMode: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.flashMode.option1',
                            default: 'on',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.flashMode.option2',
                            default: 'auto',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.flashMode.option3',
                            default: 'off',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                quality: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.quality.option1',
                            default: 'high',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.quality.option2',
                            default: 'low',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                cameraSide: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.cameraSide.option1',
                            default: 'rear',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.cameraSide.option2',
                            default: 'front',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                cameraAction: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.cameraAction.option1',
                            default: 'Capture image',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.cameraAction.option2',
                            default: 'Start video',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.cameraAction.option3',
                            default: 'Stop video',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                motorNumber: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                servoNumber: [
                    '1', '2'
                ],
                musicFunctions: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.musicFunctions.option1',
                            default: 'play',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.musicFunctions.option2',
                            default: 'add to queue',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                fileMenu: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.fileMenu.option1',
                            default: 'File',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.fileMenu.option2',
                            default: 'Column',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                oscilloscopeChannel: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                serialMenu: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' },
                    { text: '3', value: '3' },
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

    setBaudRate(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setBaudRate(args, util, this);
        }
        return RealtimeMode.setBaudRate(args, util, this);
    }

    setBluetoothName(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setBluetoothName(args, util, this);
        }
        return RealtimeMode.setBluetoothName(args, util, this);
    }

    waitForAppConnection(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.waitForAppConnection(args, util, this);
        }
        return RealtimeMode.waitForAppConnection(args, util, this);
    }

    dabbleRefresh(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.dabbleRefresh(args, util, this);
        }
        return RealtimeMode.dabbleRefresh(args, util, this);
    }

    enableLEDControl(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.enableLEDControl(args, util, this);
        }
        return RealtimeMode.enableLEDControl(args, util, this);
    }

    terminalCheck(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.terminalCheck(args, util, this);
        }
        return RealtimeMode.terminalCheck(args, util, this);
    }

    terminalRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.terminalRead(args, util, this);
        }
        return RealtimeMode.terminalRead(args, util, this);
    }

    terminalWrite(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.terminalWrite(args, util, this);
        }
        return RealtimeMode.terminalWrite(args, util, this);
    }

    getGamepadOne(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getGamepadOne(args, util, this);
        }
        return RealtimeMode.getGamepadOne(args, util, this);
    }

    getGamepadTwo(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getGamepadTwo(args, util, this);
        }
        return RealtimeMode.getGamepadTwo(args, util, this);
    }

    enablePinStateMonitor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.enablePinStateMonitor(args, util, this);
        }
        return RealtimeMode.enablePinStateMonitor(args, util, this);
    }

    tactileSwitch(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.tactileSwitch(args, util, this);
        }
        return RealtimeMode.tactileSwitch(args, util, this);
    }

    slideSwitch(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.slideSwitch(args, util, this);
        }
        return RealtimeMode.slideSwitch(args, util, this);
    }

    potentiometer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.potentiometer(args, util, this);
        }
        return RealtimeMode.potentiometer(args, util, this);
    }

    readDabbleSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readDabbleSensor(args, util, this);
        }
        return RealtimeMode.readDabbleSensor(args, util, this);
    }

    getColorValue(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getColorValue(args, util, this);
        }
        return RealtimeMode.getColorValue(args, util, this);
    }

    colorDetectorSettings(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.colorDetectorSettings(args, util, this);
        }
        return RealtimeMode.colorDetectorSettings(args, util, this);
    }

    cameraSettings(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.cameraSettings(args, util, this);
        }
        return RealtimeMode.cameraSettings(args, util, this);
    }

    rotateCamera(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rotateCamera(args, util, this);
        }
        return RealtimeMode.rotateCamera(args, util, this);
    }

    cameraAction(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.cameraAction(args, util, this);
        }
        return RealtimeMode.cameraAction(args, util, this);
    }

    customDabbleMotorControl(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.customDabbleMotorControl(args, util, this);
        }
        return RealtimeMode.customDabbleMotorControl(args, util, this);
    }

    espDabbleMotorControl(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.espDabbleMotorControl(args, util, this);
        }
        return RealtimeMode.espDabbleMotorControl(args, util, this);
    }

    dabbleServoControl(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.dabbleServoControl(args, util, this);
        }
        return RealtimeMode.dabbleServoControl(args, util, this);
    }

    dabbleMotorControl(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.dabbleMotorControl(args, util, this);
        }
        return RealtimeMode.dabbleMotorControl(args, util, this);
    }

    createDataLoggerFile(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.createDataLoggerFile(args, util, this);
        }
        return RealtimeMode.createDataLoggerFile(args, util, this);
    }

    sendDatatoDataLogger(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendDatatoDataLogger(args, util, this);
        }
        return RealtimeMode.sendDatatoDataLogger(args, util, this);
    }

    stopDataLogger(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.stopDataLogger(args, util, this);
        }
        return RealtimeMode.stopDataLogger(args, util, this);
    }

    sendNotification(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendNotification(args, util, this);
        }
        return RealtimeMode.sendNotification(args, util, this);
    }

    clearNotification(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.clearNotification(args, util, this);
        }
        return RealtimeMode.clearNotification(args, util, this);
    }

    playMusic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playMusic(args, util, this);
        }
        return RealtimeMode.playMusic(args, util, this);
    }

    stopMusic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.stopMusic(args, util, this);
        }
        return RealtimeMode.stopMusic(args, util, this);
    }

    baudrateOscilloscope(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.baudrateOscilloscope(args, util, this);
        }
        return RealtimeMode.baudrateOscilloscope(args, util, this);
    }

    baudrateOscilloscopeMega(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.baudrateOscilloscopeMega(args, util, this);
        }
        return RealtimeMode.baudrateOscilloscopeMega(args, util, this);
    }

    sendDataToOscilloscope(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendDataToOscilloscope(args, util, this);
        }
        return RealtimeMode.sendDataToOscilloscope(args, util, this);
    }

}

module.exports = dabble;
