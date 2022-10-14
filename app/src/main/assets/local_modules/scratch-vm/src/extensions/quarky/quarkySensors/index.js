const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = `data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGRkMwMDA7fQ0KCS5zdDF7ZmlsbDojRUQ1NzFDO30NCgkuc3Qye2ZpbGw6I0VENDQxQzt9DQoJLnN0M3tmaWxsOiM4OUY0QzQ7fQ0KPC9zdHlsZT4NCjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0xNS40LDM1LjJINS42Yy0wLjksMC0xLjYtMC43LTEuNi0xLjZWNi40YzAtMC45LDAuNy0xLjYsMS42LTEuNmg5LjhjMC45LDAsMS42LDAuNywxLjYsMS42djI3LjENCglDMTcsMzQuNSwxNi4zLDM1LjIsMTUuNCwzNS4yeiIvPg0KPGNpcmNsZSBjbGFzcz0ic3QwIiBjeD0iMTcuOSIgY3k9IjE5LjciIHI9IjUuOSIvPg0KPGNpcmNsZSBjbGFzcz0ic3QxIiBjeD0iMTAuNSIgY3k9IjExLjYiIHI9IjIuOCIvPg0KPGNpcmNsZSBjbGFzcz0ic3QyIiBjeD0iMTAuNSIgY3k9IjI5IiByPSIyLjgiLz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0yNS42LDEzLjJjLTEtMS0yLjUsMC41LTEuNSwxLjVjMi44LDIuOCwyLjgsNy4yLDAsMTBjLTEsMSwwLjUsMi41LDEuNSwxLjVjMS43LTEuNywyLjgtNC4xLDIuOC02LjUNCgkJQzI4LjQsMTcuMywyNy40LDE0LjksMjUuNiwxMy4yeiIvPg0KCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0yOC41LDkuOWMtMC45LTEtMi41LDAuNS0xLjUsMS41YzIsMi4yLDMuMyw1LjEsMy40LDguMWMwLjEsMi44LTAuOCw2LjYtMy4yLDguNGMtMS4xLDAuOCwwLDIuNywxLjEsMS45DQoJCWMzLTIuMiw0LjMtNi40LDQuMy05LjlDMzIuNSwxNi4xLDMxLDEyLjYsMjguNSw5Ljl6Ii8+DQoJPHBhdGggY2xhc3M9InN0MyIgZD0iTTM1LjgsMTNjLTAuOS0yLjQtMi41LTQuMS0zLjktNi4zYy0wLjgtMS4xLTIuNi0wLjEtMS45LDEuMWMxLjIsMS45LDIuOCwzLjQsMy42LDUuNWMwLjgsMi4xLDEuMiw0LjMsMS4xLDYuNg0KCQljLTAuMSw0LjQtMiw4LjUtNS4xLDExLjVjLTEsMSwwLjUsMi41LDEuNSwxLjVjMy40LTMuNCw1LjQtNy44LDUuNy0xMi42QzM3LDE3LjgsMzYuNywxNS4zLDM1LjgsMTN6Ii8+DQo8L2c+DQo8L3N2Zz4NCg==`;
// eslint-disable-next-line max-len
const menuIconURI = blockIconURI;

class quarkySensors {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkySensors';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkySensorsState = sourceTarget.getCustomState(quarkySensors.STATE_KEY);
            if (quarkySensorsState) {
                newTarget.setCustomState(quarkySensors.STATE_KEY, Clone.simple(quarkySensorsState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkySensors',
            name: formatMessage({
                id: 'quarkySensors.sensors',
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
                    message: formatMessage({
                        id: 'quarkySensors.switch',
                        default: 'Button',
                        description: 'Switch separator message'
                    })
                },
                {
                    opcode: 'whenButtonPressed',
                    blockType: BlockType.HAT,
                    text: formatMessage({
                        id: 'quarkySensors.whenButtonPressed',
                        default: 'when button [BUTTON] pressed',
                        description: 'when the selected button is pressed'
                    }),
                    arguments: {
                        BUTTON: {
                            type: ArgumentType.NUMBER,
                            menu: 'tactileMenu',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'buttonPressed',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quarkySensors.buttonPressed',
                        default: 'is button [BUTTON] pressed?',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        BUTTON: {
                            type: ArgumentType.NUMBER,
                            menu: 'tactileMenu',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarkySensors.iRSensor',
                        default: 'Infrared Sensor',
                        description: 'IR Sensor separator message'
                    })
                },
                {
                    opcode: 'getIRSensorState',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quarkySensors.getIRSensorState',
                        default: 'is [SENSOR] IR sensor active?',
                        description: 'set IR sensor threshold'
                    }),
                    arguments: {
                        SENSOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'irMenu',
                            defaultValue: '35'
                        }
                    }
                },
                {
                    opcode: 'getIRValue',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarkySensors.getIRValue',
                        default: 'get value of [SENSOR]',
                        description: 'Read IR sensor'
                    }),
                    arguments: {
                        SENSOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'irThresMenu',
                            defaultValue: '35'
                        }
                    }
                },
                {
                    opcode: 'setIRThreshold',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkySensors.setIRThreshold',
                        default: 'set [SENSOR] IR sensor threshold to [VALUE]',
                        description: 'set IR sensor threshold'
                    }),
                    arguments: {
                        SENSOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'irThresMenu',
                            defaultValue: '35'
                        },
                        VALUE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1200'
                        },
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarkySensors.touch',
                        default: 'Touch Sensor',
                        description: 'Touch Sensor separator message'
                    })
                },
                {
                    opcode: 'whenTouchPressed',
                    blockType: BlockType.HAT,
                    text: formatMessage({
                        id: 'quarkySensors.whenTouchPressed',
                        default: 'when [TOUCHPIN] is touched',
                        description: 'when the touch is pressed'
                    }),
                    arguments: {
                        TOUCHPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'touchPinMenu',
                            defaultValue: '15'
                        }
                    }
                },
                {
                    opcode: 'getTouchSensorState',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quarkySensors.getTouchSensorState',
                        default: 'is [TOUCHPIN] touched?',
                        description: 'is touched'
                    }),
                    arguments: {
                        TOUCHPIN: {
                            type: ArgumentType.STRING,
                            menu: 'touchPins',
                            defaultValue: 'T1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarkySensors.ultrasonic',
                        default: 'Ultrasonic Sensor',
                        description: 'Ultrasonic Sensor separator message'
                    })
                },
                {
                    opcode: 'defineUltrasonic',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'sensors.defineUltrasonic',
                        default: 'connect ultrasonic [SENSOR] to trig [TRIG_PIN], echo [ECHO_PIN]',
                        description: 'Set Ultrasonic sensor'
                    }),
                    arguments: {
                        TRIG_PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPinsUltrasonic',
                            defaultValue: '18'
                        },
                        ECHO_PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPinsUltrasonic',
                            defaultValue: '19'
                        },
                        SENSOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'ultrasonicCount',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'readUltrasonic',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'sensors.readUltrasonic',
                        default: 'get ultrasonic [SENSOR] distance (cm)',
                        description: 'Read Ultrasonic sensor'
                    }),
                    arguments: {
                        SENSOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'ultrasonicCount',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarkySensors.otherSensor',
                        default: 'Other Sensors',
                        description: 'Other Sensor separator message'
                    })
                },
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
                            defaultValue: '33'
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
                            defaultValue: '18'
                        }
                    }
                }
            ],
            menus: {
                tactileMenu: [
                    { text: 'L', value: '1' },
                    { text: 'R', value: '2' },
                    //{ text: 'L and R', value: '3' }
                ],
                irMenu: [
                    { text: 'IR-L', value: '35' },
                    { text: 'IR-R', value: '34' },
                ],
                irThresMenu: [
                    { text: 'IR-L', value: '35' },
                    { text: 'IR-R', value: '34' },
                ],
                touchPins: [
                    'T1','T2','T3','T4','T5'
                ],
                touchPinMenu:[
                    { text: 'T1', value: '15' },
                    { text: 'T2', value: '13' },
                    { text: 'T3', value: '12' },
                    { text: 'T4', value: '14' },
                    { text: 'T5', value: '27' },
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
                            id: 'extension.menu.sensors.digitalSensors.option2',
                            default: 'PIR',
                            description: 'Menu'
                        }), value: '3'
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
                            id: 'extension.menu.sensors.digitalSensors.option6',
                            default: 'generic',
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
                ],
                analogPins: [
                    { text: 'A1', value: '33' },
                    { text: 'A2', value: '32' },
                    { text: 'A3', value: '39' }
                ],
                digitalPins: [
                    { text: 'D1', value: '18' },
                    { text: 'D2', value: '19' },
                    { text: 'D3', value: '26' }
                ],
                digitalPinsUltrasonic: [
                    { text: 'D1', value: '18' },
                    { text: 'D2', value: '19' },
                    { text: 'D3', value: '26' },
                    { text: 'A1', value: '33' },
                    { text: 'A2', value: '32' },
                ],
                ultrasonicCount: ['1', '2'],
            }
        };
    }

    buttonPressed(args, util) {
        console.log(`this.runtime.isVMPreStoreDataAvailable(): ${this.runtime.isVMPreStoreDataAvailable()} | buttonPressed index.js quarkySensor`);
        if (this.runtime.getCode) {
            return CodingMode.buttonPressed(args, util, this);
        }
        return RealtimeMode.buttonPressed(args, util, this);
    }

    whenButtonPressed(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.whenButtonPressed(args, util, this);
        }
        return RealtimeMode.whenButtonPressed(args, util, this);
    }

    setIRThreshold(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setIRThreshold(args, util, this);
        }
        return RealtimeMode.setIRThreshold(args, util, this);
    }

    getIRSensorState(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getIRSensorState(args, util, this);
        }
        return RealtimeMode.getIRSensorState(args, util, this);
    }

    getIRValue(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getIRValue(args, util, this);
        }
        return RealtimeMode.getIRValue(args, util, this);
    }

    whenTouchPressed(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.whenTouchPressed(args, util, this);
        }
        return RealtimeMode.whenTouchPressed(args, util, this);
    }

    getTouchSensorState(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getTouchSensorState(args, util, this);
        }
        return RealtimeMode.getTouchSensorState(args, util, this);
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

    readUltrasonic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readUltrasonic(args, util, this);
        }
        return RealtimeMode.readUltrasonic(args, util, this);
    }

    defineUltrasonic(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.defineUltrasonic(args, util, this);
        }
        return RealtimeMode.defineUltrasonic(args, util, this);
    }

}
module.exports = quarkySensors;