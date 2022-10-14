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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNBNEQ4RkY7fQ0KCS5zdDF7ZmlsbDojRkZGRkZGO30NCgkuc3Qye2ZpbGw6I0QxMTMyRDt9DQoJLnN0M3tvcGFjaXR5OjAuMjtmaWxsOiNGRkZGRkY7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF84MTA0XyI+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDhfIiBjbGFzcz0ic3QwIiBkPSJNMjEuNiw4LjlDMjEuNiw4LjksMjEuNiw4LjksMjEuNiw4LjlMMjEuNiw4LjlMMjEuNiw4LjljMC0wLjktMC43LTEuNi0xLjYtMS42DQoJCWMtMC45LDAtMS42LDAuNy0xLjYsMS42aDB2Ni42aDMuM0wyMS42LDguOUMyMS42LDguOSwyMS42LDguOSwyMS42LDguOXoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODIwNV8iIGNsYXNzPSJzdDEiIGQ9Ik0yMCwzNWMtMy41LDAtNi4zLTIuOC02LjMtNi4zYzAtMiwxLTMuOSwyLjYtNS4xVjguOEMxNi4yLDYuNywxNy45LDUsMjAsNQ0KCQljMi4xLDAsMy44LDEuNywzLjgsMy44djE0LjhjMS42LDEuMiwyLjYsMy4xLDIuNiw1LjFDMjYuMywzMi4yLDIzLjUsMzUsMjAsMzV6IE0yMCwzMy45YzIuOSwwLDUuMy0yLjQsNS4zLTUuMw0KCQljMC0xLjktMS0zLjYtMi42LTQuNVY4LjhjMC0xLjUtMS4yLTIuNy0yLjctMi43Yy0xLjUsMC0yLjcsMS4yLTIuNywyLjd2MTUuM2MtMS42LDAuOS0yLjYsMi43LTIuNiw0LjUNCgkJQzE0LjcsMzEuNiwxNy4xLDMzLjksMjAsMzMuOXoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODIwNF8iIGNsYXNzPSJzdDIiIGQ9Ik0yMS42LDI0Ljh2LTkuNGgtMy4zdjkuNGMtMiwwLjktMy4yLDMuMi0yLjIsNS43YzAuNCwwLjksMS4xLDEuNiwyLDJjMy4xLDEuMyw2LTAuOSw2LTMuOA0KCQlDMjQuMiwyNi45LDIzLjIsMjUuNCwyMS42LDI0Ljh6Ii8+DQoJPGVsbGlwc2UgaWQ9IlhNTElEXzgyMDNfIiBjbGFzcz0ic3QzIiBjeD0iMTcuOCIgY3k9IjI4LjQiIHJ4PSIxIiByeT0iMS40Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDJfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwyMS44aDIuMXYwLjdoLTIuMWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zdi0wLjJDMjAuMywyMS45LDIwLjQsMjEuOCwyMC42LDIxLjh6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDFfIiBjbGFzcz0ic3QxIiBkPSJNMjEuMiwxOS42SDIzdjAuN2gtMS44Yy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC45LDE5LjgsMjEsMTkuNiwyMS4yLDE5LjZ6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgyMDBfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwxNy41aDIuMXYwLjdoLTIuMWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zdi0wLjJDMjAuMywxNy43LDIwLjQsMTcuNSwyMC42LDE3LjV6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDlfIiBjbGFzcz0ic3QxIiBkPSJNMjEuMiwxNS40SDIzdjAuN2gtMS44Yy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC45LDE1LjUsMjEsMTUuNCwyMS4yLDE1LjR6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDhfIiBjbGFzcz0ic3QxIiBkPSJNMjAuNiwxMy4zaDIuMVYxNGgtMi4xYy0wLjIsMC0wLjMtMC4xLTAuMy0wLjN2LTAuMkMyMC4zLDEzLjQsMjAuNCwxMy4zLDIwLjYsMTMuM3oiLz4NCgk8cGF0aCBpZD0iWE1MSURfODEwN18iIGNsYXNzPSJzdDEiIGQ9Ik0yMS4yLDExLjJIMjN2MC43aC0xLjhjLTAuMiwwLTAuMy0wLjEtMC4zLTAuM3YtMC4yQzIwLjksMTEuMywyMSwxMS4yLDIxLjIsMTEuMnoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODEwNl8iIGNsYXNzPSJzdDEiIGQ9Ik0yMC42LDloMi4xdjAuN2gtMi4xYy0wLjIsMC0wLjMtMC4xLTAuMy0wLjNWOS4zQzIwLjMsOS4yLDIwLjQsOSwyMC42LDl6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzgxMDVfIiBjbGFzcz0ic3QzIiBkPSJNMTkuNCw5LjNjMC0wLjEtMC4xLTAuMi0wLjItMC4yYy0wLjEsMC0wLjIsMC4xLTAuMiwwLjJoMHYxNS40bDAuNC0wLjFMMTkuNCw5LjNMMTkuNCw5LjN6Ig0KCQkvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM3NkM0RjQ7fQ0KCS5zdDF7ZmlsbDojRDExMzJEO30NCgkuc3Qye29wYWNpdHk6MC4yO2ZpbGw6I0ZGRkZGRjt9DQoJLnN0M3tmaWxsOiNBN0E5QUM7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF84MjA4XyIgY2xhc3M9InN0MCIgZD0iTTIyLDYuMkwyMiw2LjJMMjIsNi4yTDIyLDYuMmMwLTEuMS0wLjktMi0yLTJjLTEuMSwwLTIsMC45LTIsMmgwdjguMUgyMkwyMiw2LjINCglDMjIsNi4yLDIyLDYuMiwyMiw2LjJ6Ii8+DQo8cGF0aCBpZD0iWE1MSURfODIwNF8iIGNsYXNzPSJzdDEiIGQ9Ik0yMiwyNS44VjE0LjNIMTh2MTEuNmMtMi41LDEuMS00LDQtMi43LDdjMC41LDEuMSwxLjQsMiwyLjUsMi41YzMuOCwxLjYsNy40LTEuMiw3LjQtNC43DQoJQzI1LjIsMjguNSwyMy45LDI2LjYsMjIsMjUuOHoiLz4NCjxlbGxpcHNlIGlkPSJYTUxJRF84MjAzXyIgY2xhc3M9InN0MiIgY3g9IjE3LjMiIGN5PSIzMC4yIiByeD0iMS4yIiByeT0iMS43Ii8+DQo8cGF0aCBpZD0iWE1MSURfNTBfIiBjbGFzcz0ic3QzIiBkPSJNMjQuNywyNC4zVjYuMWMwLTIuNi0yLjEtNC43LTQuNy00LjdzLTQuNywyLjEtNC43LDQuN3YxOC4yYy0yLDEuNS0zLjIsMy44LTMuMiw2LjMNCgljMCw0LjMsMy41LDcuOCw3LjgsNy44czcuOC0zLjUsNy44LTcuOEMyNy44LDI4LjEsMjYuNiwyNS44LDI0LjcsMjQuM3ogTTIwLDM3LjFjLTMuNiwwLTYuNS0yLjktNi41LTYuNWMwLTIuMywxLjItNC40LDMuMi01LjZWNi4xDQoJYzAtMS44LDEuNS0zLjMsMy4zLTMuM3MzLjMsMS41LDMuMywzLjN2MC4zaC0yLjZjLTAuMiwwLTAuMywwLjItMC4zLDAuM1Y3YzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gyLjZWOWgtMS45DQoJYy0wLjIsMC0wLjMsMC4yLTAuMywwLjN2MC4yYzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gxLjl2MS43aC0yLjZjLTAuMiwwLTAuMywwLjItMC4zLDAuM3YwLjJjMCwwLjIsMC4yLDAuMywwLjMsMC4zaDIuNnYxLjdoLTEuOQ0KCWMtMC4yLDAtMC4zLDAuMi0wLjMsMC4zdjAuMmMwLDAuMiwwLjIsMC4zLDAuMywwLjNoMS45djEuN2gtMi42Yy0wLjIsMC0wLjMsMC4yLTAuMywwLjN2MC4yYzAsMC4yLDAuMiwwLjMsMC4zLDAuM2gyLjZ2MS43aC0xLjkNCgljLTAuMiwwLTAuMywwLjItMC4zLDAuM3YwLjJjMCwwLjIsMC4yLDAuMywwLjMsMC4zaDEuOXYxLjdoLTIuNmMtMC4yLDAtMC4zLDAuMi0wLjMsMC4zdjAuMmMwLDAuMiwwLjIsMC4zLDAuMywwLjNoMi42djINCgljMS45LDEuMiwzLjIsMy4zLDMuMiw1LjZDMjYuNSwzNC4yLDIzLjYsMzcuMSwyMCwzNy4xeiIvPg0KPHBhdGggaWQ9IlhNTElEXzgxMDVfIiBjbGFzcz0ic3QyIiBkPSJNMTkuMiw2LjdjMC0wLjEtMC4xLTAuMi0wLjMtMC4yYy0wLjEsMC0wLjIsMC4xLTAuMywwLjJoMHYxOWwwLjUtMC4xTDE5LjIsNi43TDE5LjIsNi43eiIvPg0KPC9zdmc+DQo=';


class sensors {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'sensors';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'sensors';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const sensorsState = sourceTarget.getCustomState(sensors.STATE_KEY);
            if (sensorsState) {
                newTarget.setCustomState(sensors.STATE_KEY, Clone.simple(sensorsState));
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
                allowedBoards: ['quon'],
                getBlocks: (extensionId) => [
                    {

                        message: formatMessage({
                            id: 'sensors.blockSeparatorMessage2',
                            default: 'External',
                            description: 'Blocks separator message'
                        })

                    },
                    {
                        opcode: 'quonUltrasonic',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'sensors.quonUltrasonic',
                            default: 'get ultrasonic sensor distance (cm) on [PORT]',
                            description: 'Read Ultrasonic sensor'
                        }),
                        arguments: {
                            PORT: {
                                type: ArgumentType.NUMBER,
                                menu: 'portList',
                                defaultValue: 'PORTC'
                            }
                        }
                    }]
            },
            {
                index: 3,
                allowedBoards: ['quon'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'quonDHTSensor',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'sensors.quonDHTSensor',
                            default: 'get [DHT_SENSOR] from DHT sensor at pin [PIN]',
                            description: 'Read temperature/humidity'
                        }),
                        arguments: {
                            DHT_SENSOR: {
                                type: ArgumentType.NUMBER,
                                menu: 'dhtSensor',
                                defaultValue: '1'
                            },
                            PIN: {
                                type: ArgumentType.NUMBER,
                                menu: 'quonDhtPins',
                                defaultValue: '27'
                            }
                        }
                    }
                ]
            },
            {
                index: 1,
                allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'esp32', 'tWatch', 'quarky', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'readUltrasonic',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'sensors.readUltrasonic',
                            default: 'get ultrasonic sensor distance (cm) | trig [TRIG_PIN], echo [ECHO_PIN]',
                            description: 'Read Ultrasonic sensor'
                        }),
                        arguments: {
                            TRIG_PIN: {
                                type: ArgumentType.STRING,
                                menu: 'digitalPins',
                                defaultValue: this.TRIG_PIN_DEFAULT_VALUE
                            },
                            ECHO_PIN: {
                                type: ArgumentType.STRING,
                                menu: 'digitalPins',
                                defaultValue: this.ECHO_PIN_DEFAULT_VALUE
                            }
                        }
                    }
                ]
            },
            {
                index: 2,
                allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'esp32', 'tWatch', 'quarky', 'tecBits'],
                getBlocks: (extensionId) => [
                    {
                        opcode: 'readDHTSensor',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'sensors.readDHTSensor',
                            default: 'get [DHT_SENSOR] from DHT sensor at pin [PIN]',
                            description: 'Read temperature/humidity'
                        }),
                        arguments: {
                            DHT_SENSOR: {
                                type: ArgumentType.NUMBER,
                                menu: 'dhtSensor',
                                defaultValue: '1'
                            },
                            PIN: {
                                type: ArgumentType.NUMBER,
                                menu: 'digitalPins',
                                defaultValue: this.DIGITAL_SENSOR_PIN_DEFAULT_VALUE
                            }
                        }
                    }
                ]
            },
            {
                index: 4,
                allowedBoards: ['tWatch'],
                getBlocks: (extensionId) => [
                    {
                        message: formatMessage({
                            id: 'sensors.blockSeparatorMessage1',
                            default: 'Step Counter(U)',
                            description: 'Blocks separator message'
                        })
                    },
                    {
                        opcode: 'tWatchStepCount',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            id: 'sensors.tWatchStepCount',
                            default: 'get step count from step counter',
                            description: 'reads step counts from BMA423'
                        }),
                    }]
            },
            {
                index: 5,
                allowedBoards: ['quon'],
                getBlocks: (extensionId) => [
                    {
                        message: formatMessage({
                            id: 'sensors.blockSeparatorMessage3',
                            default: 'On board',
                            description: 'Blocks separator message'
                        })
                    },
                    {
                        opcode: 'readTouchPin',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            opcode: 'readTouchPin',
                            id: 'sensors.readTouchPin',
                            default: 'read touch on pin [TOUCHSENSORPIN]',
                            description: 'Read potentiometer'
                        }),
                        arguments: {
                            TOUCHSENSORPIN: {
                                type: ArgumentType.NUMBER,
                                menu: 'touchPins',
                                defaultValue: '1'
                            }
                        }
                    },
                    {
                        opcode: 'readMPUPin',
                        blockType: BlockType.REPORTER,
                        text: formatMessage({
                            opcode: 'readMPUPin',
                            id: 'quon.readMPUPin',
                            default: 'get [MPUAXIS] data',
                            description: 'Read MPU'
                        }),
                        arguments: {
                            MPUAXIS: {
                                type: ArgumentType.NUMBER,
                                menu: 'axisTable',
                                defaultValue: '1'
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
            case 'Quon': {
                return ExtensionMenu.digitalPins.quon;
            }
            case 'Quarky': {
                return ExtensionMenu.digitalPins.quarky;
            }
            case 'TecBits': {
                return ExtensionMenu.digitalPins.tecBits;
            }
        }
        return ['0'];
    }

    get ANALOG_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.analogPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.analogPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.analogPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.analogPins.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.analogPins.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.analogPins.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.analogPins.quon;
            }
            case 'Quarky': {
                return ExtensionMenu.analogPins.quarky;
            }
            case 'TecBits': {
                return ExtensionMenu.analogPins.tecBits;
            }
        }
        return ['0'];
    }

    get TRIG_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.trigPin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.trigPin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.trigPin.arduinoNano;
            }
            case 'evive': {
                return Config.trigPin.evive;
            }
            case 'ESP32': {
                return Config.trigPin.esp32;
            }
            case 'T-Watch': {
                return Config.trigPin.tWatch;
            }
            case 'Quarky': {
                return Config.trigPin.quarky;
            }
            case 'TecBits': {
                return Config.trigPin.tecBits;
            }
        }
        return ['0'];
    }

    get ECHO_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.echoPin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.echoPin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.echoPin.arduinoNano;
            }
            case 'evive': {
                return Config.echoPin.evive;
            }
            case 'ESP32': {
                return Config.echoPin.esp32;
            }
            case 'T-Watch': {
                return Config.echoPin.tWatch;
            }
            case 'Quarky': {
                return Config.echoPin.quarky;
            }
            case 'TecBits': {
                return Config.echoPin.tecBits;
            }
        }
        return ['0'];
    }

    get ANALOG_SENSOR_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.analogPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.analogPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.analogPins.arduinoNano;
            }
            case 'evive': {
                return Config.analogPins.evive;
            }
            case 'ESP32': {
                return Config.analogPins.esp32;
            }
            case 'T-Watch': {
                return Config.analogPins.tWatch;
            }
            case 'Quon': {
                return Config.analogPins.quon;
            }
            case 'Quarky': {
                return Config.analogPins.quarky;
            }
            case 'TecBits': {
                return Config.analogPins.tecBits;
            }
        }
        return ['0'];
    }

    get DIGITAL_SENSOR_PIN_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.digitalSensorPin.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.digitalSensorPin.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.digitalSensorPin.arduinoNano;
            }
            case 'evive': {
                return Config.digitalSensorPin.evive;
            }
            case 'ESP32': {
                return Config.digitalSensorPin.esp32;
            }
            case 'T-Watch': {
                return Config.digitalSensorPin.tWatch;
            }
            case 'Quon': {
                return Config.digitalSensorPin.quon;
            }
            case 'Quarky': {
                return Config.digitalSensorPin.quarky;
            }
            case 'TecBits': {
                return Config.digitalSensorPin.tecBits;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'sensors',
            name: formatMessage({
                id: 'sensors.sensors',
                default: 'Sensors',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5cb1d6',
            colourSecondary: '#47a8d1',
            colourTertiary: '#2e8eb8',
            blocks: [
                {
                    opcode: 'readAnalogSensor',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'sensors.readAnalogSensor',
                        default: 'read analog sensor [ANALOG_SENSOR] at [PIN]',
                        description: 'Read Analog sensor'
                    }),
                    arguments: {
                        ANALOG_SENSOR: {
                            type: ArgumentType.STRING,
                            menu: 'analogSensors',
                            defaultValue: '1'
                        },
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: this.ANALOG_SENSOR_PIN_DEFAULT_VALUE
                        }
                    }
                },
                {
                    opcode: 'readDigitalSensor',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'sensors.readDigitalSensor',
                        default: 'read digital sensor [DIGITAL_SENSOR] at [PIN]',
                        description: 'Read Keypad value'
                    }),
                    arguments: {
                        DIGITAL_SENSOR: {
                            type: ArgumentType.STRING,
                            menu: 'digitalSensors',
                            defaultValue: '3'
                        },
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: this.DIGITAL_SENSOR_PIN_DEFAULT_VALUE
                        }
                    }
                }
            ],
            menus: {
                digitalPins: this.DIGITAL_PINS,
                analogPins: this.ANALOG_PINS,
                quonDhtPins:
                    [
                        { text: '3', value: '27' },
                        { text: '4', value: '14' },
                        { text: '5', value: '13' },
                        { text: '6', value: '15' },
                        { text: 'PORTA', value: '26' },
                        { text: 'PORTB', value: '4' }
                    ],
                portList: [
                    { text: 'PORTC', value: 'PORTC' },
                    { text: 'PORTD', value: 'PORTD' }
                ],
                touchPins: [
                    { text: 'T1', value: '1' },
                    { text: 'T2', value: '2' },
                    { text: 'T3', value: '3' },
                    { text: 'T4', value: '4' },
                    { text: 'T5', value: '5' },
                    { text: 'T6', value: '6' }
                ],
                axisTable: [
                    { text: 'accelerometer x axis', value: '1' },
                    { text: 'accelerometer y axis', value: '2' },
                    { text: 'accelerometer z axis', value: '3' },
                    { text: 'gyroscope x axis', value: '4' },
                    { text: 'gyroscope y axis', value: '5' },
                    { text: 'gyroscope z axis', value: '6' }
                ],
                analogSensors: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option1',
                            default: 'light / photoresistor',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option2',
                            default: 'soil moisture',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option3',
                            default: 'raindrop',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option4',
                            default: 'sound / microphone',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option5',
                            default: 'gas sensor',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option6',
                            default: 'joystick X',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option7',
                            default: 'joystick Y',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.analogSensors.option8',
                            default: 'generic',
                            description: 'Menu'
                        }), value: '8'
                    }
                ],
                digitalSensors: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option1',
                            default: 'IR (proximity)',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option2',
                            default: 'PIR',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option3',
                            default: 'soil moisture',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option4',
                            default: 'hall effect / magnetic field',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option5',
                            default: 'touch',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.digitalSensors.option6',
                            default: 'Generic',
                            description: 'Menu'
                        }), value: '6'
                    }
                ],
                dhtSensor: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.dhtSensor.option1',
                            default: 'temperature',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensors.dhtSensor.option2',
                            default: 'humidity',
                            description: 'Menu'
                        }), value: '2'
                    }
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

    readTouchPin(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readTouchPin(args, util, this);
        }
        return RealtimeMode.readTouchPin(args, util, this);
    }

    readMPUPin(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readMPUPin(args, util, this);
        }
        return RealtimeMode.readMPUPin(args, util, this);
    }

    quonUltrasonic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.quonUltrasonic(args, util, this);
        }
        return RealtimeMode.quonUltrasonic(args, util, this);
    }

    tWatchStepCount(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.tWatchStepCount(args, util, this);
        }
        return RealtimeMode.tWatchStepCount(args, util, this);
    }

    quonDHTSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.quonDHTSensor(args, util, this);
        }
        return RealtimeMode.quonDHTSensor(args, util, this);
    }

    readUltrasonic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readUltrasonic(args, util, this);
        }
        return RealtimeMode.readUltrasonic(args, util, this);
    }

    readDHTSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readDHTSensor(args, util, this);
        }
        return RealtimeMode.readDHTSensor(args, util, this);
    }

    readAnalogSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readAnalogSensor(args, util, this);
        }
        return RealtimeMode.readAnalogSensor(args, util, this);
    }

    readDigitalSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readDigitalSensor(args, util, this);
        }
        return RealtimeMode.readDigitalSensor(args, util, this);
    }

}

module.exports = sensors;

