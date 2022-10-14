const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM2NjY2NjY7fQ0KCS5zdDF7ZmlsbDojRDNGM0Y4O30NCgkuc3Qye2ZpbGw6I0ZENUM2Rjt9DQoJLnN0M3tmaWxsOiNGOUVFODA7fQ0KCS5zdDR7ZmlsbDojNTRERERCO30NCgkuc3Q1e2ZpbGw6IzQ3NDYzRTt9DQoJLnN0NntkaXNwbGF5Om5vbmU7ZmlsbDojNDc0NjNFO30NCjwvc3R5bGU+DQo8Zz4NCgk8cmVjdCB4PSIxOS4zIiB5PSIxNy43IiBjbGFzcz0ic3QwIiB3aWR0aD0iMS41IiBoZWlnaHQ9IjMuNiIvPg0KCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0yMS40LDYuOGMtMC4yLTAuMS0wLjMtMC4yLTAuNS0wLjNjMCwwLDAsMCwwLDBjMC4xLTAuMSwwLjItMC4yLDAuMy0wLjNjMC4yLTAuMSwwLjItMC40LDAtMC42DQoJCWMtMC4yLTAuMi0wLjQtMC4yLTAuNiwwYy0wLjIsMC4yLTAuNSwwLjQtMC42LDAuN2MtMC4xLDAuMi0wLjEsMC41LDAsMC43YzAuMSwwLjEsMC4zLDAuMiwwLjQsMC4yYzAuMSwwLjEsMC4yLDAuMSwwLjMsMC4yDQoJCWMwLDAsMCwwLDAsMGMtMC4xLDAuMS0wLjEsMC4xLTAuMiwwLjJjLTAuMSwwLjEtMC4yLDAuMi0wLjMsMC40Yy0wLjEsMC4yLTAuMiwwLjQsMCwwLjZjMC4yLDAuMSwwLjQsMC4yLDAuNiwwDQoJCWMwLjItMC4yLDAuNC0wLjQsMC42LTAuN2MwLjEtMC4xLDAuMi0wLjMsMC4yLTAuNUMyMS43LDcuMSwyMS42LDYuOSwyMS40LDYuOHoiLz4NCgk8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMjUuMSwxOC4ySDE0LjljLTEuNywwLTMuMi0xLjQtMy4yLTMuMnYtMy43YzAtMS43LDEuNC0zLjIsMy4yLTMuMmgxMC4xYzEuNywwLDMuMiwxLjQsMy4yLDMuMlYxNQ0KCQlDMjguMiwxNi44LDI2LjgsMTguMiwyNS4xLDE4LjJ6Ii8+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTEwLjYsMTUuNUwxMC42LDE1LjVjLTAuNiwwLTEuMS0wLjUtMS4xLTEuMXYtMi4xYzAtMC42LDAuNS0xLjEsMS4xLTEuMWgwYzAuNiwwLDEuMSwwLjUsMS4xLDEuMXYyLjENCgkJQzExLjgsMTUsMTEuMywxNS41LDEwLjYsMTUuNXoiLz4NCgk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjkuNCwxNS40TDI5LjQsMTUuNGMtMC42LDAtMS4xLTAuNS0xLjEtMS4xdi0yLjFjMC0wLjYsMC41LTEuMSwxLjEtMS4xaDBjMC42LDAsMS4xLDAuNSwxLjEsMS4xdjIuMQ0KCQlDMzAuNSwxNC44LDMwLDE1LjQsMjkuNCwxNS40eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0yMy42LDEzLjhoLTcuMmMtMS4xLDAtMi0wLjktMi0ydi0wLjNjMC0xLjEsMC45LTIsMi0yaDcuMmMxLjEsMCwyLDAuOSwyLDJ2MC4zDQoJCUMyNS42LDEyLjksMjQuNywxMy44LDIzLjYsMTMuOHoiLz4NCgk8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMjIuNywzMi4yaC01LjRjLTIuOCwwLTUtMi4zLTUtNXYtNS42YzAtMC41LDAuNC0xLDEtMWgxMy40YzAuNiwwLDEsMC41LDEsMXY1LjYNCgkJQzI3LjcsMjkuOSwyNS41LDMyLjIsMjIuNywzMi4yeiIvPg0KCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yMi41LDE4LjJoLTUuMWMtMC4zLDAtMC42LTAuMy0wLjYtMC42di0wLjljMC0wLjMsMC4zLTAuNiwwLjYtMC42aDUuMWMwLjMsMCwwLjYsMC4zLDAuNiwwLjZ2MC45DQoJCUMyMy4xLDE3LjksMjIuOSwxOC4yLDIyLjUsMTguMnoiLz4NCgk8Y2lyY2xlIGNsYXNzPSJzdDMiIGN4PSIyMCIgY3k9IjI1LjMiIHI9IjEuOSIvPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0xMywzNC43aC0xLjRjLTAuOCwwLTEuNS0wLjctMS41LTEuNXYtNS40YzAtMC44LDAuNy0xLjUsMS41LTEuNUgxM2MwLjgsMCwxLjUsMC43LDEuNSwxLjV2NS40DQoJCUMxNC40LDM0LDEzLjgsMzQuNywxMywzNC43eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yOC40LDM0LjdIMjdjLTAuOCwwLTEuNS0wLjctMS41LTEuNXYtNS40YzAtMC44LDAuNy0xLjUsMS41LTEuNWgxLjRjMC44LDAsMS41LDAuNywxLjUsMS41djUuNA0KCQlDMjkuOSwzNCwyOS4yLDM0LjcsMjguNCwzNC43eiIvPg0KCTxlbGxpcHNlIGNsYXNzPSJzdDUiIGN4PSIxNyIgY3k9IjExLjgiIHJ4PSIwLjkiIHJ5PSIxLjUiLz4NCgk8ZWxsaXBzZSBjbGFzcz0ic3Q1IiBjeD0iMjMuMSIgY3k9IjExLjgiIHJ4PSIwLjkiIHJ5PSIxLjUiLz4NCgk8cGF0aCBjbGFzcz0ic3Q2IiBkPSJNMjAuMiwxMy4zSDIwYy0wLjQsMC0wLjctMC4zLTAuNy0wLjd2LTAuM2MwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMS4yYzAuMSwwLDAuMiwwLjEsMC4yLDAuMnYwLjMNCgkJQzIwLjksMTMsMjAuNiwxMy4zLDIwLjIsMTMuM3oiLz4NCgk8Y2lyY2xlIGNsYXNzPSJzdDIiIGN4PSIyMC45IiBjeT0iNC42IiByPSIxLjMiLz4NCjwvZz4NCjwvc3ZnPg0K';
// eslint-disable-next-line max-len
const menuIconURI = blockIconURI;

class quarkyDebug {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkyDebug';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkyDebugState = sourceTarget.getCustomState(quarkyDebug.STATE_KEY);
            if (quarkyDebugState) {
                newTarget.setCustomState(quarkyDebug.STATE_KEY, Clone.simple(quarkyDebugState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkyDebug',
            name: formatMessage({
                id: 'quarkyDebug.Debug',
                default: 'Quarky Debug',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5fbc2e',
            colourSecondary: '#41a80b',
            colourTertiary: '#3d9907',
            blocks: [
                {
                    opcode: 'getTouchValue',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarkyDebug.getTouchValue',
                        default: 'get touch pin [PIN] value',
                        description: 'read Touch pin value'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'touchPinNo',
                            defaultValue: '15'
                        }
                    }
                },
                {
                    opcode: 'setTouchThreshold',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDebug.setTouchThreshold',
                        default: 'set threshold for pin [PIN] to [VALUE]',
                        description: 'set touch pin threshold'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.STRING,
                            menu: 'touchPins',
                            defaultValue: 'T1'
                        },
                        VALUE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '20'
                        },
                    }
                },
                {
                    opcode: 'rawPbvalue',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarkyDebug.rawPbvalue',
                        default: 'get analog push button value',
                        description: 'read analog push button values'
                    }),
                },
                {
                    opcode: 'resetQuarky',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDebug.resetQuarky',
                        default: 'factory reset',
                        description: 'factory reset Quarky'
                    }),
                },
                {
                    opcode: 'setPbThreshold',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDebug.setPbThreshold',
                        default: 'set range for [PBUTTON]button from [LOWER] to [UPPER]',
                        description: 'read Touch pin value'
                    }),
                    arguments: {
                        PBUTTON: {
                            type: ArgumentType.NUMBER,
                            menu: 'buttonMenu',
                            defaultValue: '1'
                        },
                        LOWER: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '550'
                        },
                        UPPER: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1200'
                        }

                    }
                },
                {
                    opcode: 'motorDirSet',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDebug.motorDirSet',
                        default: 'reverse [MOTOR] motor directions',
                        description: 'read Touch pin value'
                    }),
                    arguments: {
                        MOTOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'buttonMenu',
                            defaultValue: '1'
                        },
                    }
                },
            ],
            menus: {
                mathOperation: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.mathOperation.option1',
                            default: 'integer',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.mathOperation.option2',
                            default: 'float',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                touchPins:[
                    'T1','T2','T3','T4','T5'
                    // { text: 'T1', value: '15' },
                    // { text: 'T2', value: '13' },
                    // { text: 'T3', value: '12' },
                    // { text: 'T4', value: '14' },
                    // { text: 'T5', value: '27' }
                ],
                touchPinNo:[
                    { text: 'T1', value: '15' },
                    { text: 'T2', value: '13' },
                    { text: 'T3', value: '12' },
                    { text: 'T4', value: '14' },
                    { text: 'T5', value: '27' }
                ],
                buttonMenu:[
                    { text: 'Left',  value: '1' },
                    { text: 'Right', value: '2' }
                ],
                // motorMenu:[
                //     { text: 'Motor 1',  value: '1' },
                //     { text: 'Motor 2', value: '2' }
                // ],

            }
        };
    }

    setTouchThreshold(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setTouchThreshold(args, util, this);
        }
        return RealtimeMode.setTouchThreshold(args, util, this);
    }

    getTouchValue(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getTouchValue(args, util, this);
        }
        return RealtimeMode.getTouchValue(args, util, this);
    }

    rawPbvalue(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rawPbvalue(args, util, this);
        }
        return RealtimeMode.rawPbvalue(args, util, this);
    }

    resetQuarky(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.resetQuarky(args, util, this);
        }
        return RealtimeMode.resetQuarky(args, util, this);
    }

    setPbThreshold(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setPbThreshold(args, util, this);
        }
        return RealtimeMode.setPbThreshold(args, util, this);
    }

    motorDirSet(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.motorDirSet(args, util, this);
        }
        return RealtimeMode.motorDirSet(args, util, this);
    }
}

module.exports = quarkyDebug;
