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

class quarkyRobot {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkyRobot';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkyRobotState = sourceTarget.getCustomState(quarkyRobot.STATE_KEY);
            if (quarkyRobotState) {
                newTarget.setCustomState(quarkyRobot.STATE_KEY, Clone.simple(quarkyRobotState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkyRobot',
            name: formatMessage({
                id: 'quarkyRobot.Robot',
                default: 'Robot',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5fbc2e',
            colourSecondary: '#41a80b',
            colourTertiary: '#3d9907',
            blocks: [
                // {
                //     opcode: 'robotConfiguration',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'quarkyRobot.robotConfiguration',
                //         default: 'robot configuration is [CONFIGURATION]',
                //         description: 'indicate robot configuration'
                //     }),
                //     arguments: {
                //         CONFIGURATION: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'robotConfig',
                //             defaultValue: '1'
                //         }
                //     }
                // },
                {
                    opcode: 'runRobot',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.runRobot',
                        default: 'go [DIRECTION] at [SPEED]% speed',
                        description: 'run Robot'
                    }),
                    arguments: {
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'robotDirection',
                            defaultValue: '1'
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '100'
                        }
                    }
                },
                {
                    opcode: 'runRobotTimed',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.runRobotTimed',
                        default: 'go [DIRECTION] at [SPEED]% speed for [TIME] seconds',
                        description: 'run Robot'
                    }),
                    arguments: {
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'robotDirection',
                            defaultValue: '1'
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '100'
                        },
                        TIME: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'stopRobot',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.stopRobot',
                        default: 'stop robot',
                        description: 'stop motor'
                    })
                },
                "---",
                {
                    opcode: 'robotOreintation',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.robotOreintation',
                        default: 'set robot orientation as [DIRECTION]',
                        description: 'run Robot'
                    }),
                    arguments: {
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'robotOreint',
                            defaultValue: '1'
                        },
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarkyRobot.lineFollower',
                        default: 'Line Follower',
                        description: 'Line Follower'
                    })
                },
                {
                    opcode: 'setLineFollowerParameter',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.setLineFollowerParameter',
                        default: 'set parameter F [FORWARD], T1 [TURNING1], T2 [TURNING2]',
                        description: 'set Line follower parameter'
                    }),
                    arguments: {
                        FORWARD: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '35'
                        },
                        TURNING1: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '40'
                        },
                        TURNING2: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '10'
                        },
                    }
                },
                {
                    opcode: 'doLineFollowing',
                    blockType: BlockType.CONDITIONAL,
                    branchCount: 1,
                    isTerminal: false,
                    blockAllThreads: false,
                    text: formatMessage({
                        id: 'quarkyRobot.doLineFollowing',
                        default: 'do line following',
                        description: 'Do line following'
                    })
                },
                {
                    message: formatMessage({
                        id: 'quarkyRobot.motor',
                        default: 'Motor',
                        description: 'Motor'
                    })
                },
                {
                    opcode: 'runMotor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.runMotor',
                        default: 'run [MOTOR] motor [DIRECTION] with [SPEED]% speed',
                        description: 'Run motor'
                    }),
                    arguments: {
                        MOTOR: {
                            type: ArgumentType.STRING,
                            menu: 'motor',
                            defaultValue: 'L'
                        },
                        DIRECTION: {
                            type: ArgumentType.STRING,
                            menu: 'motorDirection',
                            defaultValue: '1'
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '100'
                        }
                    }
                },
                {
                    opcode: 'stopMotor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.stopMotor',
                        default: 'stop [MOTOR] motor',
                        description: 'Lock/Free motor'
                    }),
                    arguments: {
                        MOTOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'motor',
                            defaultValue: 'L'
                        }
                    }
                },
                '---',
                {
                    opcode: 'moveServo',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyRobot.moveServo',
                        default: 'set servo on [SERVO_CHANNEL] to [ANGLE] angle',
                        description: 'Set servo'
                    }),
                    arguments: {
                        SERVO_CHANNEL: {
                            type: ArgumentType.STRING,
                            menu: 'servoChannel',
                            defaultValue: '22'
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '30'
                        }
                    }
                },
            ],
            menus: {
                // robotConfig:[
                //     { text: 'Horizontal', value: '1' },
                //     { text: 'Vertical', value: '2' }
                // ],
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
                robotDirectionTurn: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option3',
                            default: 'left',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotDirection.option4',
                            default: 'right',
                            description: 'Menu'
                        }), value: '2'
                    },
                ],
                motorState: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorState.option1',
                            default: 'free',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorState.option2',
                            default: 'lock',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                motorDirection: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection.option1',
                            default: 'forward',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection.option2',
                            default: 'backward',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                servoChannel: [
                    { text: 'Servo 1', value: '22' },
                    { text: 'Servo 2', value: '23' }
                ],
                angleRobot: {
                    acceptReporters: true,
                    items: [
                        { text: '90', value: '90' },
                        { text: '180', value: '180' },
                        { text: '270', value: '270' },
                        { text: '360', value: '360' }
                    ]
                },
                robotOreint: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotOreint.option1',
                            default: 'horizontal',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.robotOreint.option2',
                            default: 'vertical',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
            }
        };
    }
    runMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.runMotor(args, util, this);
        }
        return RealtimeMode.runMotor(args, util, this);
    }

    stopMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.stopMotor(args, util, this);
        }
        return RealtimeMode.stopMotor(args, util, this);
    }

    moveServo(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveServo(args, util, this);
        }
        return RealtimeMode.moveServo(args, util, this);
    }

    // robotConfig(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.robotConfig(args, util, this);
    //     }
    //     return RealtimeMode.robotConfig(args, util, this);
    // }

    runRobot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.runRobot(args, util, this);
        }
        return RealtimeMode.runRobot(args, util, this);
    }

    // turnRobot(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.turnRobot(args, util, this);
    //     }
    //     return RealtimeMode.turnRobot(args, util, this);
    // }

    stopRobot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.stopRobot(args, util, this);
        }
        return RealtimeMode.stopRobot(args, util, this);
    }

    setLineFollowerParameter(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setLineFollowerParameter(args, util, this);
        }
        return RealtimeMode.setLineFollowerParameter(args, util, this);
    }

    doLineFollowing(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.doLineFollowing(args, util, this);
        }
        return RealtimeMode.doLineFollowing(args, util, this);
    }

    runRobotTimed(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.runRobotTimed(args, util, this);
        }
        return RealtimeMode.runRobotTimed(args, util, this);
    }

    robotOreintation(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.robotOreintation(args, util, this);
        }
        return RealtimeMode.robotOreintation(args, util, this);
    }

}

module.exports = quarkyRobot;
