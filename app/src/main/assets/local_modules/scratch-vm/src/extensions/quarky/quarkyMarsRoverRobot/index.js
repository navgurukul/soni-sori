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

class quarkyMarsRoverRobot {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkyMarsRoverRobot';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkyMarsRoverRobotState = sourceTarget.getCustomState(quarkyMarsRoverRobot.STATE_KEY);
            if (quarkyMarsRoverRobotState) {
                newTarget.setCustomState(quarkyMarsRoverRobot.STATE_KEY, Clone.simple(quarkyMarsRoverRobotState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkyMarsRoverRobot',
            name: formatMessage({
                id: 'quarkyMarsRoverRobot.Robot',
                default: 'Mars Rover',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5fbc2e',
            colourSecondary: '#41a80b',
            colourTertiary: '#3d9907',
            blocks: [
                {
                    opcode: 'intializeRobot',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyMarsRoverRobot.intializeRobot',
                        default: 'intialize mars rover',
                        description: 'Intialize Mars Rover'
                    })
                },
                {
                    opcode: 'setServoOffset',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyMarsRoverRobot.setServoOffset',
                        default: 'set offset to head [HEAD], FL [FL], FR [FR], BL [BL], BR [BR]',
                        description: 'Set Servo Position'
                    }),
                    arguments: {
                        HEAD: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FL: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FR: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        BL: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        BR: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                    }
                },
                "---",
                {
                    opcode: 'setServoPosition',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyMarsRoverRobot.setServoPosition',
                        default: 'set [POSITION] to [ANGLE]',
                        description: 'Set Servo Position'
                    }),
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoPosition',
                            defaultValue: '1'
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '40'
                        }
                    }
                },
                {
                    opcode: 'setServoAngle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyMarsRoverRobot.setServoAngle',
                        default: 'set servos to FL [FL], FR [FR], BL [BL], BR [BR]',
                        description: 'Set Servo Position'
                    }),
                    arguments: {
                        FL: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        FR: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        BL: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        BR: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                    }
                },
                {
                    opcode: 'setHeadAngle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyMarsRoverRobot.setHeadAngle',
                        default: 'set head servo to [ANGLE]',
                        description: 'Set Servo Head Position'
                    }),
                    arguments: {
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                    }
                },
            ],
            menus: {
                motor: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.motor.option1',
                            default: 'left',
                            description: 'Menu'
                        }), value: 'L'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.motor.option2',
                            default: 'right',
                            description: 'Menu'
                        }), value: 'R'
                    },
                ],
                robotDirection: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option1',
                            default: 'forward',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option2',
                            default: 'backward',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option3',
                            default: 'left',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option4',
                            default: 'right',
                            description: 'Menu'
                        }), value: '4'
                    },
                ],
                servoPosition: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.servoPosition.option1',
                            default: 'inside',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.servoPosition.option2',
                            default: 'left',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.servoPosition.option3',
                            default: 'right',
                            description: 'Menu'
                        }), value: '3'
                    },
                ],
            }
        };
    }
    intializeRobot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.intializeRobot(args, util, this);
        }
        return RealtimeMode.intializeRobot(args, util, this);
    }

    setServoOffset(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServoOffset(args, util, this);
        }
        return RealtimeMode.setServoOffset(args, util, this);
    }

    setServoPosition(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServoPosition(args, util, this);
        }
        return RealtimeMode.setServoPosition(args, util, this);
    }

    setServoAngle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServoAngle(args, util, this);
        }
        return RealtimeMode.setServoAngle(args, util, this);
    }

    setHeadAngle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setHeadAngle(args, util, this);
        }
        return RealtimeMode.setHeadAngle(args, util, this);
    }

}

module.exports = quarkyMarsRoverRobot;