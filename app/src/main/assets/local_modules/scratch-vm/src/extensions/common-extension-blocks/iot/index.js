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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDQwIDQwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA0MCA0MCIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+DQo8ZyBpZD0iTGF5ZXJfMiI+DQo8L2c+DQo8ZyBpZD0iTGF5ZXJfMSI+DQoJPHBhdGggaWQ9IlhNTElEXzQ5XyIgZmlsbD0iI0ZGRkZGRiIgc3Ryb2tlPSIjRjQzRDBDIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIGQ9Ik0zMC42LDE1LjNDMzAuNiwxNS4zLDMwLjYsMTUuMywzMC42LDE1LjMNCgkJYzAtMi45LTIuNC01LjMtNS4zLTUuM2MtMS4zLDAtMi40LDAuNC0zLjQsMS4yYy0xLjMtMS44LTMuNC0zLTUuOC0zYy0zLjksMC03LjEsMy4yLTcuMSw3LjFjMCwwLDAsMCwwLDBjLTMuNywwLjMtNi42LDMuNC02LjYsNy4yDQoJCWwwLDBjMCw0LDMuMyw3LjIsNy4yLDcuMmgyMC41YzQsMCw3LjItMy4zLDcuMi03LjJsMCwwQzM3LjUsMTguNywzNC40LDE1LjUsMzAuNiwxNS4zeiIvPg0KCTxnIGlkPSJQYWdlLTEiIHRyYW5zZm9ybT0icm90YXRlKDkwLCA0LCA1KSI+DQoJCTxnIGlkPSJhcnJvdyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTQuMDAwMDAwLCAtMy4wMDAwMDApIj4NCgkJCTxnIGlkPSJYTUxJRF84Nl8iPg0KCQkJCTxwYXRoIGZpbGw9IiNGOTZBMzIiIGQ9Ik0zMC4yLTcuNmMwLDAuMi0wLjIsMC4zLTAuMywwLjRsLTcsMS4ydjIuOWMwLDAuMi0wLjEsMC4zLTAuMSwwLjNjMCwwLTAuMSwwLTAuMy0wLjJsLTUuNi00LjINCgkJCQkJYy0wLjItMC4xLTAuMy0wLjMtMC4zLTAuNWMwLTAuMiwwLjEtMC4zLDAuMy0wLjVsNS42LTRjMC4yLTAuMSwwLjMtMC4xLDAuMy0wLjFjMCwwLDAuMSwwLjEsMC4xLDAuM3YyLjlsNywxLjINCgkJCQkJQzMwLTcuOSwzMC4yLTcuNywzMC4yLTcuNnoiLz4NCgkJCQk8cGF0aCBmaWxsPSIjRjQzRDBDIiBkPSJNMTcuMS03LjdDMTcuMS03LjcsMTcuMS03LjcsMTcuMS03LjdsNS4zLTMuOHYxLjl2MC44bDAuOCwwLjFsNS44LDFsLTUuOCwxbC0wLjgsMC4xdjAuOHYxLjlMMTcuMS03LjcNCgkJCQkJQzE3LjEtNy42LDE3LjEtNy42LDE3LjEtNy43IE0xNi4xLTcuN2MwLDAuMywwLjIsMC42LDAuNSwwLjlsNS42LDQuMmMwLjcsMC41LDEuMiwwLjIsMS4yLTAuNnYtMi40TDMwLTYuNw0KCQkJCQljMC40LTAuMSwwLjctMC40LDAuNy0wLjljMC0wLjQtMC4zLTAuOC0wLjctMC45bC02LjYtMS4xVi0xMmMwLTAuOC0wLjUtMS4xLTEuMi0wLjZsLTUuNiw0QzE2LjMtOC4zLDE2LjEtOCwxNi4xLTcuN0wxNi4xLTcuN3oNCgkJCQkJIi8+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQo8L2c+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDQwIDQwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA0MCA0MCIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+DQo8ZyBpZD0iTGF5ZXJfMiI+DQo8L2c+DQo8ZyBpZD0iTGF5ZXJfMSI+DQoJPHBhdGggaWQ9IlhNTElEXzQ5XyIgZmlsbD0iI0Y5NkEzMiIgc3Ryb2tlPSIjRjQzRDBDIiBzdHJva2Utd2lkdGg9IjEuMSIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBkPSJNMzEuNywxNC45DQoJCUMzMS43LDE0LjksMzEuNywxNC45LDMxLjcsMTQuOWMwLTMuMi0yLjYtNS44LTUuOC01LjhjLTEuNCwwLTIuNywwLjUtMy43LDEuM2MtMS40LTItMy43LTMuMy02LjQtMy4zYy00LjMsMC03LjgsMy41LTcuOCw3LjgNCgkJYzAsMCwwLDAsMCwwYy00LDAuNC03LjIsMy44LTcuMiw3LjlsMCwwYzAsNC40LDMuNiw4LDgsOGgyMi42YzQuNCwwLDgtMy42LDgtOGwwLDBDMzkuMywxOC42LDM1LjksMTUuMSwzMS43LDE0Ljl6Ii8+DQoJPGcgaWQ9IlhNTElEXzg2XyI+DQoJCTxwYXRoIGZpbGw9IiNGRkZGRkYiIGQ9Ik0xOS42LDI4LjdjLTAuNiwwLTEuMi0wLjUtMS4zLTEuMWwtMS02LjJoLTJjLTAuOCwwLTEuMS0wLjQtMS4yLTAuNmMtMC4xLTAuMi0wLjItMC43LDAuMi0xLjRsNC4yLTUuNg0KCQkJYzAuMy0wLjQsMC44LTAuNywxLjMtMC43YzAuNSwwLDEsMC4zLDEuMywwLjdsNCw1LjZjMC4zLDAuNSwwLjQsMSwwLjIsMS40Yy0wLjIsMC40LTAuNiwwLjYtMS4yLDAuNmgtMmwtMSw2LjINCgkJCUMyMC44LDI4LjIsMjAuMiwyOC43LDE5LjYsMjguN3oiLz4NCgkJPHBhdGggZmlsbD0iI0Y0M0QwQyIgZD0iTTE5LjcsMTMuNmMwLjMsMCwwLjYsMC4yLDAuOSwwLjVsNCw1LjZjMC41LDAuNywwLjIsMS4yLTAuNiwxLjJoLTIuNGwtMS4xLDYuNmMtMC4xLDAuNC0wLjUsMC43LTAuOSwwLjcNCgkJCWMtMC40LDAtMC44LTAuMy0wLjktMC43bC0xLjEtNi42aC0yLjRjLTAuOCwwLTEuMS0wLjUtMC42LTEuMmw0LjItNS42QzE5LDEzLjgsMTkuNCwxMy42LDE5LjcsMTMuNiBNMTkuNywxMi42bDAsMUwxOS43LDEyLjYNCgkJCWMtMC42LDAtMS4yLDAuMy0xLjcsMC45bC00LjIsNS42Yy0wLjUsMC42LTAuNiwxLjMtMC4zLDEuOWMwLjMsMC42LDAuOSwwLjksMS43LDAuOWgxLjZsMSw1LjhjMC4xLDAuOSwxLDEuNiwxLjgsMS42DQoJCQljMC45LDAsMS43LTAuNywxLjgtMS42bDEtNS44SDI0YzAuOCwwLDEuNC0wLjMsMS43LTAuOWMwLjMtMC42LDAuMi0xLjMtMC4zLTEuOWwtNC01LjZDMjAuOSwxMi45LDIwLjMsMTIuNiwxOS43LDEyLjZMMTkuNywxMi42eg0KCQkJIi8+DQoJPC9nPg0KPC9nPg0KPC9zdmc+DQo=';

class iot {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'iot';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'iot';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const iotState = sourceTarget.getCustomState(iot.STATE_KEY);
            if (iotState) {
                newTarget.setCustomState(iot.STATE_KEY, Clone.simple(iotState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [
            {
                index: 2,
                allowedBoards: ['evive','quarky','esp32','tWatch'],
                getBlocks: (extensionId) => [
                    {
                        message: formatMessage({
                            id: 'iot.blockSeparatorMessage1',
                            default: 'Adafruit IO',
                            description: 'Blocks separator message'
                        })
                    },
                    {
                        opcode: 'connectToAdafruitIO',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'iot.connectToAdafruitIO',
                            default: 'connect to Adafruit IO username [USERNAME] & AIO Key [KEY]',
                            description: 'Connect to Adafruit IO'
                        }),
                        arguments: {
                            USERNAME: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Username'
                            },
                            KEY: {
                                type: ArgumentType.STRING,
                                defaultValue: 'AIO Key'
                            }
                        }
                    },
                    {
                        opcode: 'createFeedAdafruitIO',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'iot.createFeedAdafruitIO',
                            default: '[OPTION] a new feed named [FEED]',
                            description: 'Create a new feed'
                        }),
                        arguments: {
                            FEED: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Feed Name'
                            },
                            OPTION: {
                                type: ArgumentType.NUMBER,
                                menu: 'Option',
                                defaultValue: '0'
                            },
                        }
                    },
                    {
                        opcode: 'sendDataAdafruitIONumber',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'iot.sendDataAdafruitIONumber',
                            default: 'send [DATA] to feed [FEED] as [TYPE]',
                            description: 'Send data to Feed'
                        }),
                        arguments: {
                            FEED: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Feed Name'
                            },
                            DATA: {
                                type: ArgumentType.NUMBER,
                                defaultValue: '0'
                            },
                            TYPE: {
                                type: ArgumentType.NUMBER,
                                menu: 'Type',
                                defaultValue: '0'
                            },
                        }
                    },
                    {
                        opcode: 'getDataAdafruitIO',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'iot.getDataAdafruitIO',
                            default: 'get last data from feed [FEED] as [TYPE]',
                            description: 'Get data to Feed'
                        }),
                        arguments: {
                            FEED: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Feed Name'
                            },
                            TYPE: {
                                type: ArgumentType.NUMBER,
                                menu: 'Type',
                                defaultValue: '0'
                            },
                        }
                    },
                    {
                        opcode: 'getColorAdafruitIO',
                        blockType: BlockType.COMMAND,
                        text: formatMessage({
                            id: 'iot.getColorAdafruitIO',
                            default: 'get color from feed [FEED]',
                            description: 'Get color from Feed'
                        }),
                        arguments: {
                            FEED: {
                                type: ArgumentType.STRING,
                                defaultValue: 'Feed Name'
                            }
                        }
                    },
                    {
                        opcode: 'getRGB',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'iot.getRGB',
                            default: 'get [COLOR] value',
                            description: 'Get color from Feed'
                        }),
                        arguments: {
                            COLOR: {
                                type: ArgumentType.NUMBER,
                                menu: 'colorType',
                                defaultValue: '1'
                            },
                        }
                    },
                ]
            },
            // {
            //     index: 2,
            //     allowedBoards: ['esp32', 'tWatch'],
            //     getBlocks: (extensionId) => [
            //         {
            //             message: formatMessage({
            //                 id: 'iot.blockSeparatorMessage2',
            //                 default: 'Adafruit IO (S)',
            //                 description: 'Blocks separator message'
            //             })
            //         },
            //     ]
            // },
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

    getDefaultInfo(extensionId) {
        return {
            id: 'iot',
            name: formatMessage({
                id: 'iot.iot',
                default: 'IoT',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#f96a32',
            colourSecondary: '#fa4c13',
            colourTertiary: '#f43d0c',
            blocks: [
                {
                    message: formatMessage({
                        id: 'iot.blockSeparatorMessage3',
                        default: 'Connect to Wi-Fi (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'connectToWifi',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.connectToWifi',
                        default: 'connect to Wifi [WIFI] with password [PASSWORD]',
                        description: 'Connect to Wifi'
                    }),
                    arguments: {
                        WIFI: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Wifi Name'
                        },
                        PASSWORD: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Password'
                        }
                    }
                },
                // {
                //     opcode: 'connectToAdafruitIO',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'iot.connectToAdafruitIO',
                //         default: 'connect to Adafruit IO username [USERNAME] & AIO Key [KEY]',
                //         description: 'Connect to Adafruit IO'
                //     }),
                //     arguments: {
                //         USERNAME: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'Username'
                //         },
                //         KEY: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'AIO Key'
                //         }
                //     }
                // },
                // {
                //     opcode: 'createFeedAdafruitIO',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'iot.createFeedAdafruitIO',
                //         default: '[OPTION] a new feed named [FEED]',
                //         description: 'Create a new feed'
                //     }),
                //     arguments: {
                //         FEED: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'Feed Name'
                //         },
                //         OPTION: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'Option',
                //             defaultValue: '0'
                //         },
                //     }
                // },
                // {
                //     opcode: 'sendDataAdafruitIONumber',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'iot.sendDataAdafruitIONumber',
                //         default: 'send [DATA] to feed [FEED] as [TYPE]',
                //         description: 'Send data to Feed'
                //     }),
                //     arguments: {
                //         FEED: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'Feed Name'
                //         },
                //         DATA: {
                //             type: ArgumentType.NUMBER,
                //             defaultValue: '0'
                //         },
                //         TYPE: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'Type',
                //             defaultValue: '0'
                //         },
                //     }
                // },
                // {
                //     opcode: 'getDataAdafruitIO',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         id: 'iot.getDataAdafruitIO',
                //         default: 'get last data from feed [FEED] as [TYPE]',
                //         description: 'Get data to Feed'
                //     }),
                //     arguments: {
                //         FEED: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'Feed Name'
                //         },
                //         TYPE: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'Type',
                //             defaultValue: '0'
                //         },
                //     }
                // },
                // {
                //     opcode: 'getColorAdafruitIO',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'iot.getColorAdafruitIO',
                //         default: 'get color from feed [FEED]',
                //         description: 'Get color from Feed'
                //     }),
                //     arguments: {
                //         FEED: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'Feed Name'
                //         }
                //     }
                // },
                // {
                //     opcode: 'getRGB',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         id: 'iot.getRGB',
                //         default: 'get [COLOR] value',
                //         description: 'Get color from Feed'
                //     }),
                //     arguments: {
                //         COLOR: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'colorType',
                //             defaultValue: '1'
                //         },
                //     }
                // },
                {
                    message: formatMessage({
                        id: 'iot.blockSeparatorMessage5',
                        default: 'Thing Speak',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'connectToThingspeak',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.connectToThingspeak',
                        default: 'connect to ThingSpeak channel [CHANNEL] & write API [WRITEAPI] & read API [READAPI]',
                        description: 'Connect to thingspeak'
                    }),
                    arguments: {
                        CHANNEL: {
                            type: ArgumentType.NUMBER,
                            defaultValue: 'Channel ID'
                        },
                        WRITEAPI: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Write API'
                        },
                        READAPI: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Read API'
                        }
                    }
                },
                {
                    opcode: 'sendDataToCloud',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.sendDataToCloud',
                        default: 'send data to cloud [DATA] delay [TIME] sec',
                        description: 'Send data to Cloud'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        TIME: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '20'
                        }
                    }
                },
                {
                    opcode: 'sendMultipleDataToCloud',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.sendMultipleDataToCloud',
                        default: 'send multiple data [NUMBER_OF_DATA] to cloud [DATA1][DATA2][DATA3][DATA4][DATA5][DATA6][DATA7][DATA8] delay [TIME] sec',
                        description: 'Send multiple data to Cloud'
                    }),
                    arguments: {
                        NUMBER_OF_DATA: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '2'
                        },
                        DATA1: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA2: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA3: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA4: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA5: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA6: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA7: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        DATA8: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        },
                        TIME: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '20'
                        }
                    }
                },
                '---',
                {
                    opcode: 'getDataFromThingspeak',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.getDataFromThingspeak',
                        default: 'get data from Thingspeak',
                        description: 'Get data from Thingspeak'
                    })
                },
                {
                    opcode: 'getDataFromField',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'iot.getDataFromField',
                        default: 'read data from [FIELD]',
                        description: 'Read data from field'
                    }),
                    arguments: {
                        FIELD: {
                            type: ArgumentType.NUMBER,
                            menu: 'field',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'iot.blockSeparatorMessage6',
                        default: 'HTTP API Requests (S)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'makeAPIRequest',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.makeAPIRequest',
                        default: 'make [REQUEST] request [URL] [BODYOPTION] body',
                        description: 'API request'
                    }),
                    arguments: {
                        REQUEST: {
                            type: ArgumentType.NUMBER,
                            menu: 'request',
                            defaultValue: '1'
                        },
                        URL: {
                            type: ArgumentType.STRING,
                            defaultValue: 'url'
                        },
                        BODYOPTION: {
                            type: ArgumentType.STRING,
                            menu: 'bodyType',
                            defaultValue: '2'
                        }
                    }
                },
                {
                    opcode: 'setAPIBody',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'iot.setAPIBody',
                        default: 'set body to [BODY]',
                        description: 'set body'
                    }),
                    arguments: {
                        BODY: {
                            type: ArgumentType.STRING,
                            defaultValue: `{"value":"5"}`,
                        }
                    }
                },
                {
                    opcode: 'requestAPICode',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'iot.requestAPICode',
                        default: 'get API response code',
                        description: 'API request code'
                    }),
                },
                {
                    opcode: 'getResponse',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'iot.getResponse',
                        default: 'get [RESPONSE]',
                        description: 'get Response'
                    }),
                    arguments: {
                        RESPONSE: {
                            type: ArgumentType.NUMBER,
                            menu: 'responseType',
                            defaultValue: '1',
                        }
                    }
                },
                {
                    opcode: 'getAPIJson',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'iot.getAPIJson',
                        default: 'get [JSON1] [JSON2] [JSON3] value',
                        description: 'get JSON value'
                    }),
                    arguments: {
                        JSON1: {
                            type: ArgumentType.STRING,
                            defaultValue: 'value',
                        },
                        JSON2: {
                            type: ArgumentType.STRING,
                            defaultValue: '',
                        },
                        JSON3: {
                            type: ArgumentType.STRING,
                            defaultValue: '',
                        }
                    }
                },
            ],
            menus: {
                field: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option1',
                            default: 'Field 1',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option2',
                            default: 'Field 2',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option3',
                            default: 'Field 3',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option4',
                            default: 'Field 4',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option5',
                            default: 'Field 5',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option6',
                            default: 'Field 6',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option7',
                            default: 'Field 7',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.field.option8',
                            default: 'Field 7',
                            description: 'Menu'
                        }), value: '8'
                    }
                ],
                Option: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.Option.option1',
                            default: 'create',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.Option.option2',
                            default: 'delete',
                            description: 'Menu'
                        }), value: '1'
                    }
                ],
                Type: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.Type.option1',
                            default: 'number',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.Type.option2',
                            default: 'string',
                            description: 'Menu'
                        }), value: '1'
                    }
                ],
                colorType: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option1',
                            default: 'red',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option2',
                            default: 'green',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option3',
                            default: 'blue',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                request: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.request.option1',
                            default: 'GET',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.request.option2',
                            default: 'POST',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.request.option3',
                            default: 'DELETE',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                bodyType: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option1',
                            default: 'with',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option2',
                            default: 'without',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                responseType: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.responseType.option1',
                            default: 'body',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.iot.responseType.option2',
                            default: 'error',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    connectToWifi(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.connectToWifi(args, util, this);
        }
        return RealtimeMode.connectToWifi(args, util, this);
    }

    connectToThingspeak(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.connectToThingspeak(args, util, this);
        }
        return RealtimeMode.connectToThingspeak(args, util, this);
    }

    sendDataToCloud(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendDataToCloud(args, util, this);
        }
        return RealtimeMode.sendDataToCloud(args, util, this);
    }

    sendMultipleDataToCloud(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendMultipleDataToCloud(args, util, this);
        }
        return RealtimeMode.sendMultipleDataToCloud(args, util, this);
    }

    getDataFromThingspeak(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getDataFromThingspeak(args, util, this);
        }
        return RealtimeMode.getDataFromThingspeak(args, util, this);
    }

    getDataFromField(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getDataFromField(args, util, this);
        }
        return RealtimeMode.getDataFromField(args, util, this);
    }

    connectToAdafruitIO(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.connectToAdafruitIO(args, util, this);
        }
        return RealtimeMode.connectToAdafruitIO(args, util, this);
    }

    createFeedAdafruitIO(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.createFeedAdafruitIO(args, util, this);
        }
        return RealtimeMode.createFeedAdafruitIO(args, util, this);
    }

    sendDataAdafruitIONumber(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.sendDataAdafruitIONumber(args, util, this);
        }
        return RealtimeMode.sendDataAdafruitIONumber(args, util, this);
    }

    getDataAdafruitIO(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getDataAdafruitIO(args, util, this);
        }
        return RealtimeMode.getDataAdafruitIO(args, util, this);
    }

    getColorAdafruitIO(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getColorAdafruitIO(args, util, this);
        }
        return RealtimeMode.getColorAdafruitIO(args, util, this);
    }

    getRGB(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getRGB(args, util, this);
        }
        return RealtimeMode.getRGB(args, util, this);
    }

    makeAPIRequest(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.makeAPIRequest(args, util, this);
        }
        return RealtimeMode.makeAPIRequest(args, util, this);
    }

    setAPIBody(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setAPIBody(args, util, this);
        }
        return RealtimeMode.setAPIBody(args, util, this);
    }

    requestAPICode(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.requestAPICode(args, util, this);
        }
        return RealtimeMode.requestAPICode(args, util, this);
    }

    getResponse(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getResponse(args, util, this);
        }
        return RealtimeMode.getResponse(args, util, this);
    }

    getAPIJson(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getAPIJson(args, util, this);
        }
        return RealtimeMode.getAPIJson(args, util, this);
    }
}

module.exports = iot;
