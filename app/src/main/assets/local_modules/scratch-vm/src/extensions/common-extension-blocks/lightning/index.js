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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMwMTAxMDE7fQ0KCS5zdDF7Y2xpcC1wYXRoOnVybCgjWE1MSURfMl8pO30NCgkuc3Qye2ZpbGw6IzJBQjM0Qjt9DQoJLnN0M3tmaWxsOiNGQ0VFMjM7fQ0KCS5zdDR7ZmlsbDojRUExMThEO30NCgkuc3Q1e2ZpbGw6Izc4MkI5MDt9DQoJLnN0NntmaWxsOiNGNDc0MjE7fQ0KCS5zdDd7ZmlsbDojRUQyMDI1O30NCgkuc3Q4e2ZpbGw6IzM5NENBMDt9DQoJLnN0OXtmaWxsOiMxMDgwNDA7fQ0KCS5zdDEwe2ZpbGw6bm9uZTtzdHJva2U6IzAxMDEwMTtzdHJva2UtbGluZWNhcDpyb3VuZDtzdHJva2UtbGluZWpvaW46cm91bmQ7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KCS5zdDExe2ZpbGw6bm9uZTtzdHJva2U6IzAxMDEwMTtzdHJva2Utd2lkdGg6MC44NTtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQo8L3N0eWxlPg0KPHBhdGggaWQ9IlhNTElEXzI4ODZfIiBjbGFzcz0ic3QwIiBkPSJNMzYuMywyMGMwLDktNy4zLDE2LjMtMTYuMywxNi4zUzMuNywyOSwzLjcsMjBTMTEsMy43LDIwLDMuN1MzNi4zLDExLDM2LjMsMjB6IE0yMCwxMi4xDQoJYy00LjMsMC03LjksMy41LTcuOSw3LjlzMy41LDcuOSw3LjksNy45czcuOS0zLjUsNy45LTcuOVMyNC4zLDEyLjEsMjAsMTIuMXoiLz4NCjxnIGlkPSJYTUxJRF8xMjdfIj4NCgk8ZGVmcz4NCgkJPHBhdGggaWQ9IlhNTElEXzEzMF8iIGQ9Ik0yMCwzNS4xYy04LjMsMC0xNS4xLTYuOC0xNS4xLTE1LjFTMTEuNyw0LjksMjAsNC45UzM1LjEsMTEuNywzNS4xLDIwUzI4LjMsMzUuMSwyMCwzNS4xeiBNMjAsMTAuOQ0KCQkJYy01LDAtOS4xLDQuMS05LjEsOS4xczQuMSw5LjEsOS4xLDkuMXM5LjEtNC4xLDkuMS05LjFTMjUsMTAuOSwyMCwxMC45eiIvPg0KCTwvZGVmcz4NCgk8Y2xpcFBhdGggaWQ9IlhNTElEXzJfIj4NCgkJPHVzZSB4bGluazpocmVmPSIjWE1MSURfMTMwXyIgIHN0eWxlPSJvdmVyZmxvdzp2aXNpYmxlOyIvPg0KCTwvY2xpcFBhdGg+DQoJPGcgaWQ9IlhNTElEXzE0OV8iIGNsYXNzPSJzdDEiPg0KCQk8cGF0aCBpZD0iWE1MSURfMjg4NV8iIGNsYXNzPSJzdDIiIGQ9Ik0yMCwyMFYyLjhIMi44QzIuOCwyLjgsMjAuMSwyMC4xLDIwLDIweiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMjg4NF8iIGNsYXNzPSJzdDMiIGQ9Ik0yMCwyMFYyLjhoMTcuMkMzNy4yLDIuOCwxOS45LDIwLjEsMjAsMjB6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF8yODgzXyIgY2xhc3M9InN0NCIgZD0iTTIwLDIwdjE3LjJoMTcuMkMzNy4yLDM3LjIsMTkuOSwxOS45LDIwLDIweiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMjg4Ml8iIGNsYXNzPSJzdDUiIGQ9Ik0yMCwyMHYxNy4ySDIuOEMyLjgsMzcuMiwyMC4xLDE5LjksMjAsMjB6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF8yODgxXyIgY2xhc3M9InN0NiIgZD0iTTIwLDIwaDE3LjJWMi44QzM3LjIsMi44LDE5LjksMjAuMSwyMCwyMHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzU3OF8iIGNsYXNzPSJzdDciIGQ9Ik0yMCwyMGgxNy4ydjE3LjJDMzcuMiwzNy4yLDE5LjksMTkuOSwyMCwyMHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzU3N18iIGNsYXNzPSJzdDgiIGQ9Ik0yMCwyMEgyLjh2MTcuMkMyLjgsMzcuMiwyMC4xLDE5LjksMjAsMjB6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF81NzZfIiBjbGFzcz0ic3Q5IiBkPSJNMjAsMjBIMi44VjIuOEMyLjgsMi44LDIwLjEsMjAuMSwyMCwyMHoiLz4NCgk8L2c+DQo8L2c+DQo8ZyBpZD0iWE1MSURfOV8iPg0KCTxnIGlkPSJYTUxJRF8xMjZfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzI4OTBfIiBjbGFzcz0ic3QxMCIgZD0iTTIwLDI4LjZjMCwxLjcsMCw1LjIsMCw2LjgiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4ODlfIiBjbGFzcz0ic3QxMCIgZD0iTTIwLDQuNWMwLDEsMCw2LDAsNyIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMjNfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzkzXyIgY2xhc3M9InN0MTAiIGQ9Ik0yNiwyNi4xYzEuMiwxLjIsMy43LDMuNyw0LjgsNC44Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF81NV8iIGNsYXNzPSJzdDEwIiBkPSJNOSw5LjFjMC43LDAuNyw0LjIsNC4yLDQuOSw0LjkiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzI4OTFfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzI5MjZfIiBjbGFzcz0ic3QxMCIgZD0iTTI4LjcsMjAuMWMxLjcsMCw1LDAsNi43LDAiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4OTdfIiBjbGFzcz0ic3QxMCIgZD0iTTQuNCwyMC4xYzEsMCw2LjEsMCw3LjEsMCIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMjkyN18iPg0KCQk8cGF0aCBpZD0iWE1MSURfMjkyOV8iIGNsYXNzPSJzdDEwIiBkPSJNMjYsMTRjMS4yLTEuMiwzLjctMy43LDQuOS00LjkiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI5MjhfIiBjbGFzcz0ic3QxMCIgZD0iTTksMzFjMC43LTAuNyw0LjQtNC40LDUuMS01LjEiLz4NCgk8L2c+DQoJPGNpcmNsZSBpZD0iWE1MSURfMTlfIiBjbGFzcz0ic3QxMSIgY3g9IjIwIiBjeT0iMjAiIHI9IjguNCIvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMwMTAxMDE7fQ0KCS5zdDF7Y2xpcC1wYXRoOnVybCgjWE1MSURfMl8pO30NCgkuc3Qye2ZpbGw6IzJBQjM0Qjt9DQoJLnN0M3tmaWxsOiNGQ0VFMjM7fQ0KCS5zdDR7ZmlsbDojRUExMThEO30NCgkuc3Q1e2ZpbGw6Izc4MkI5MDt9DQoJLnN0NntmaWxsOiNGNDc0MjE7fQ0KCS5zdDd7ZmlsbDojRUQyMDI1O30NCgkuc3Q4e2ZpbGw6IzM5NENBMDt9DQoJLnN0OXtmaWxsOiMxMDgwNDA7fQ0KCS5zdDEwe2ZpbGw6bm9uZTtzdHJva2U6IzAxMDEwMTtzdHJva2Utd2lkdGg6MS4wNzE0O3N0cm9rZS1saW5lY2FwOnJvdW5kO3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0MTF7ZmlsbDpub25lO3N0cm9rZTojMDEwMTAxO3N0cm9rZS13aWR0aDowLjkxMDc7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF8yODg2XyIgY2xhc3M9InN0MCIgZD0iTTM3LjUsMjBjMCw5LjctNy44LDE3LjUtMTcuNSwxNy41UzIuNSwyOS43LDIuNSwyMFMxMC4zLDIuNSwyMCwyLjVTMzcuNSwxMC4zLDM3LjUsMjB6DQoJIE0yMCwxMS42Yy00LjcsMC04LjQsMy44LTguNCw4LjRzMy44LDguNCw4LjQsOC40czguNC0zLjgsOC40LTguNFMyNC43LDExLjYsMjAsMTEuNnoiLz4NCjxnIGlkPSJYTUxJRF8xMjdfIj4NCgk8ZGVmcz4NCgkJPHBhdGggaWQ9IlhNTElEXzEzMF8iIGQ9Ik0yMCwzNi4yYy04LjksMC0xNi4yLTcuMy0xNi4yLTE2LjJTMTEuMSwzLjgsMjAsMy44UzM2LjIsMTEuMSwzNi4yLDIwUzI4LjksMzYuMiwyMCwzNi4yeiBNMjAsMTAuMw0KCQkJYy01LjQsMC05LjcsNC40LTkuNyw5LjdzNC40LDkuNyw5LjcsOS43czkuNy00LjQsOS43LTkuN1MyNS40LDEwLjMsMjAsMTAuM3oiLz4NCgk8L2RlZnM+DQoJPGNsaXBQYXRoIGlkPSJYTUxJRF8yXyI+DQoJCTx1c2UgeGxpbms6aHJlZj0iI1hNTElEXzEzMF8iICBzdHlsZT0ib3ZlcmZsb3c6dmlzaWJsZTsiLz4NCgk8L2NsaXBQYXRoPg0KCTxnIGlkPSJYTUxJRF8xNDlfIiBjbGFzcz0ic3QxIj4NCgkJPHBhdGggaWQ9IlhNTElEXzI4ODVfIiBjbGFzcz0ic3QyIiBkPSJNMjAsMjBWMS42SDEuNkMxLjYsMS42LDIwLjEsMjAuMSwyMCwyMHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4ODRfIiBjbGFzcz0ic3QzIiBkPSJNMjAsMjBWMS42aDE4LjRDMzguNCwxLjYsMTkuOSwyMC4xLDIwLDIweiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMjg4M18iIGNsYXNzPSJzdDQiIGQ9Ik0yMCwyMHYxOC40aDE4LjRDMzguNCwzOC40LDE5LjksMTkuOSwyMCwyMHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4ODJfIiBjbGFzcz0ic3Q1IiBkPSJNMjAsMjB2MTguNEgxLjZDMS42LDM4LjQsMjAuMSwxOS45LDIwLDIweiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMjg4MV8iIGNsYXNzPSJzdDYiIGQ9Ik0yMCwyMGgxOC40VjEuNkMzOC40LDEuNiwxOS45LDIwLjEsMjAsMjB6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF81NzhfIiBjbGFzcz0ic3Q3IiBkPSJNMjAsMjBoMTguNHYxOC40QzM4LjQsMzguNCwxOS45LDE5LjksMjAsMjB6Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF81NzdfIiBjbGFzcz0ic3Q4IiBkPSJNMjAsMjBIMS42djE4LjRDMS42LDM4LjQsMjAuMSwxOS45LDIwLDIweiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfNTc2XyIgY2xhc3M9InN0OSIgZD0iTTIwLDIwSDEuNlYxLjZDMS42LDEuNiwyMC4xLDIwLjEsMjAsMjB6Ii8+DQoJPC9nPg0KPC9nPg0KPGcgaWQ9IlhNTElEXzlfIj4NCgk8ZyBpZD0iWE1MSURfMTI2XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8yODkwXyIgY2xhc3M9InN0MTAiIGQ9Ik0xOS45LDI5LjJjMCwxLjksMCw1LjYsMCw3LjMiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4ODlfIiBjbGFzcz0ic3QxMCIgZD0iTTIwLDMuNGMwLDEuMSwwLDYuNCwwLDcuNSIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMjNfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzkzXyIgY2xhc3M9InN0MTAiIGQ9Ik0yNi40LDI2LjZjMS4zLDEuMywzLjksMy45LDUuMiw1LjIiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzU1XyIgY2xhc3M9InN0MTAiIGQ9Ik04LjIsOC4zYzAuOCwwLjgsNC41LDQuNSw1LjMsNS4zIi8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8yODkxXyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8yOTI2XyIgY2xhc3M9InN0MTAiIGQ9Ik0yOS4zLDIwLjFjMS45LDAsNS40LDAsNy4yLDAiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzI4OTdfIiBjbGFzcz0ic3QxMCIgZD0iTTMuMywyMC4xYzEuMSwwLDYuNSwwLDcuNiwwIi8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8yOTI3XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8yOTI5XyIgY2xhc3M9InN0MTAiIGQ9Ik0yNi40LDEzLjZjMS4zLTEuMyw0LTQsNS4yLTUuMiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMjkyOF8iIGNsYXNzPSJzdDEwIiBkPSJNOC4yLDMxLjhjMC44LTAuOCw0LjctNC43LDUuNS01LjUiLz4NCgk8L2c+DQoJPGNpcmNsZSBpZD0iWE1MSURfMTlfIiBjbGFzcz0ic3QxMSIgY3g9IjIwIiBjeT0iMjAiIHI9IjkiLz4NCjwvZz4NCjwvc3ZnPg0K';


class lightning {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'lightning';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'lightning';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const lightningState = sourceTarget.getCustomState(lightning.STATE_KEY);
            if (lightningState) {
                newTarget.setCustomState(lightning.STATE_KEY, Clone.simple(lightningState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [{
            index: 0,
            allowedBoards: ['quon'],
            getBlocks: (extensionId) => [{
                opcode: 'quonInitialiseRGB',
                blockType: BlockType.COMMAND,
                text: formatMessage({
                    id: 'lightning.quonInitialiseRGB',
                    default: 'initialise RGB strip [STRIP] with [LED] LED pixels on pin [DIGITALPIN]',
                    description: 'Initialise RGB Strip'
                }),
                arguments: {
                    STRIP: {
                        type: ArgumentType.NUMBER,
                        menu: 'Strip',
                        defaultValue: '1'
                    },
                    LED: {
                        type: ArgumentType.NUMBER,
                        defaultValue: '10'
                    },
                    DIGITALPIN: {
                        type: ArgumentType.NUMBER,
                        menu: 'quonLedPins',
                        defaultValue: '3'
                    }
                }
            }]
        },
        {
            index: 1,
            allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'esp32', 'tWatch', 'tecBits', 'quarky'],
            getBlocks: (extensionId) => [
                {
                    opcode: 'initialiseRGB',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'lightning.initialiseRGB',
                        default: 'initialise RGB strip [STRIP] with [LED] LED pixels on pin [DIGITALPIN]',
                        description: 'Initialise RGB Strip'
                    }),
                    arguments: {
                        STRIP: {
                            type: ArgumentType.NUMBER,
                            menu: 'Strip',
                            defaultValue: '1'
                        },
                        LED: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '10'
                        },
                        DIGITALPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: this.DIGITAL_PIN_DEFAULT_VALUE
                        }
                    }
                }
            ]
        }
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
            case 'T-Watch': {
                return ExtensionMenu.digitalPins.tWatch;
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

    get DIGITAL_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.digitalPin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.digitalPin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.digitalPin.arduinoNano;
            }
            case 'evive': {
                return Config.digitalPin.evive;
            }
            case 'ESP32': {
                return Config.digitalPin.esp32;
            }
            case 'T-Watch': {
                return Config.digitalPin.tWatch;
            }
            case 'TecBits': {
                return Config.digitalPin.tecBits;
            }
            case 'Quarky': {
                return Config.digitalPin.quarky;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'lightning',
            name: formatMessage({
                id: 'lightning.lightning',
                default: 'RGB Lighting',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#ff7b70',
            colourSecondary: '#f46767',
            colourTertiary: '#e55757',
            blocks: [
                {
                    opcode: 'setPixel',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'lightning.setPixel',
                        default: 'set RGB strip [STRIP] LED pixel [PIXEL] color [COLOR]',
                        description: 'Set Pixel'
                    }),
                    arguments: {
                        STRIP: {
                            type: ArgumentType.NUMBER,
                            menu: 'Strip',
                            defaultValue: '1'
                        },
                        PIXEL: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'showRGB',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'lightning.showRGB',
                        default: 'show RGB strip [STRIP]',
                        description: 'Show RGB Strip'
                    }),
                    arguments: {
                        STRIP: {
                            type: ArgumentType.NUMBER,
                            menu: 'Strip',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'rgbColor',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'lightning.rgbColor',
                        default: 'set Red [RED] Green [GREEN] Blue [BLUE]',
                        description: 'Set RGB color values'
                    }),
                    arguments: {
                        RED: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        },
                        GREEN: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        },
                        BLUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        }
                    }
                },
                '---',
                {
                    opcode: 'showPattern1',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'lightning.showPattern1',
                        default: '[PATTERN1] RGB strip [STRIP] with color [COLOR] & delay [DELAY] seconds',
                        description: 'Show pattern 1'
                    }),
                    arguments: {
                        PATTERN1: {
                            type: ArgumentType.NUMBER,
                            menu: 'Pattern1',
                            defaultValue: '1'
                        },
                        STRIP: {
                            type: ArgumentType.NUMBER,
                            menu: 'Strip',
                            defaultValue: '1'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        },
                        DELAY: {
                            type: ArgumentType.NUMBER,
                            menu: 'DelayTime',
                            defaultValue: '0.05'
                        }
                    }
                },
                {
                    opcode: 'showPattern2',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'lightning.showPattern2',
                        default: '[PATTERN2] RGB strip [STRIP] with delay [DELAY] seconds',
                        description: 'Show pattern 2'
                    }),
                    arguments: {
                        PATTERN2: {
                            type: ArgumentType.NUMBER,
                            menu: 'Pattern2',
                            defaultValue: '1'
                        },
                        STRIP: {
                            type: ArgumentType.NUMBER,
                            menu: 'Strip',
                            defaultValue: '1'
                        },
                        DELAY: {
                            type: ArgumentType.NUMBER,
                            menu: 'DelayTime',
                            defaultValue: '0.05'
                        }
                    }
                },
            ],
            menus: {
                digitalPins: this.DIGITAL_PINS,
                Strip: [
                    '1', '2', '3', '4', '5'
                ],
                Pattern1: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.lightning.Pattern1.option1',
                            default: 'Color Wipe',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.lightning.Pattern1.option2',
                            default: 'Theater Chase',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                Pattern2: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.lightning.Pattern2.option1',
                            default: 'Rainbow',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.lightning.Pattern2.option2',
                            default: 'Rainbow Cycle',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.lightning.Pattern2.option3',
                            default: 'Theater Chase Rainbow',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                DelayTime: [
                    '0.01', '0.05', '0.1', '0.5', '1'
                ],
                quonLedPins: [
                    { text: '3', value: '27' },
                    { text: '4', value: '14' },
                    { text: '5', value: '13' },
                    { text: '6', value: '15' },
                    { text: 'PORTA', value: '26' },
                    { text: 'PORTB', value: '4' }
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

    initialiseRGB(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseRGB(args, util, this);
        }
        return RealtimeMode.initialiseRGB(args, util, this);
    }

    quonInitialiseRGB(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.quonInitialiseRGB(args, util, this);
        }
        return RealtimeMode.quonInitialiseRGB(args, util, this);
    }

    setPixel(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setPixel(args, util, this);
        }
        return RealtimeMode.setPixel(args, util, this);
    }

    showRGB(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.showRGB(args, util, this);
        }
        return RealtimeMode.showRGB(args, util, this);
    }

    showPattern1(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.showPattern1(args, util, this);
        }
        return RealtimeMode.showPattern1(args, util, this);
    }

    showPattern2(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.showPattern2(args, util, this);
        }
        return RealtimeMode.showPattern2(args, util, this);
    }

    rgbColor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rgbColor(args, util, this);
        }
        return RealtimeMode.rgbColor(args, util, this);
    }

}

module.exports = lightning;
