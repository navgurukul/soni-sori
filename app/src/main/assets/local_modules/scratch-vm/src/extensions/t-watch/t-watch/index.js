const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4yLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA5MCAxMDAiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDkwIDEwMDsiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHN0eWxlIHR5cGU9InRleHQvY3NzIj4NCgkuc3Qwe2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0YyRjJGMjt9DQoJLnN0MXtmaWxsOm5vbmU7c3Ryb2tlOiM5OTk5OTk7c3Ryb2tlLXdpZHRoOjIuNTtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0MntmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM5OTk5OTk7fQ0KCS5zdDN7ZmlsbDojRkZGRkZGO30NCgkuc3Q0e2ZvbnQtZmFtaWx5OidSb2JvdG8tTWVkaXVtJzt9DQoJLnN0NXtmb250LXNpemU6MTguNTkyM3B4O30NCjwvc3R5bGU+DQo8ZyBpZD0i5b2i54q2XzFfMV8iPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNNjMuNiwxNS43YzAtMC4yLTEuNS03LjItMS41LTcuMmMtMC4zLTMtMi42LTMuNS0yLjYtMy41SDMwLjVDMjguMyw2LDI4LDcuNywyOCw3LjdsLTEuNSw3LjENCgkJCWMtMC44LDEuNCwwLjQsMS4yLDAuNCwxLjJoMzUuOUM2My44LDE2LjEsNjMuNiwxNS43LDYzLjYsMTUuN3oiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02My42LDE1LjdjMC0wLjItMS41LTcuMi0xLjUtNy4yYy0wLjMtMy0yLjYtMy41LTIuNi0zLjVIMzAuNUMyOC4zLDYsMjgsNy43LDI4LDcuN2wtMS41LDcuMQ0KCQkJYy0wLjgsMS40LDAuNCwxLjIsMC40LDEuMmgzNS45QzYzLjgsMTYuMSw2My42LDE1LjcsNjMuNiwxNS43eiIvPg0KCTwvZz4NCjwvZz4NCjxnIGlkPSLlvaLnirZfMV/mi7fotJ1fMV8iPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNNjIuOSw4NEgyNy4xYy0xLTAuMS0wLjcsMC40LTAuNywwLjRjMCwwLjIsMS41LDcuMiwxLjUsNy4yYzAuMywzLDIuNiwzLjUsMi42LDMuNWgyOC45DQoJCQljMi4zLTEsMi41LTIuNywyLjUtMi43bDEuNS03LjFDNjQuMSw4My44LDYyLjksODQsNjIuOSw4NHoiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02Mi45LDg0SDI3LjFjLTEtMC4xLTAuNywwLjQtMC43LDAuNGMwLDAuMiwxLjUsNy4yLDEuNSw3LjJjMC4zLDMsMi42LDMuNSwyLjYsMy41aDI4LjkNCgkJCWMyLjMtMSwyLjUtMi43LDIuNS0yLjdsMS41LTcuMUM2NC4xLDgzLjgsNjIuOSw4NCw2Mi45LDg0eiIvPg0KCTwvZz4NCjwvZz4NCjxnIGlkPSLlnIbop5Lnn6nlvaJfMV8xXyI+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik02OC44LDE1LjNIMjEuMmMtNS41LDAtOS45LDQuNC05LjksOS45djQ5LjRjMCw1LjUsNC40LDkuOSw5LjksOS45aDQ3LjZjNS41LDAsOS45LTQuNCw5LjktOS45VjI1LjINCgkJCUM3OC43LDE5LjcsNzQuMywxNS4zLDY4LjgsMTUuM3oiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02OC44LDE1LjNIMjEuMmMtNS41LDAtOS45LDQuNC05LjksOS45djQ5LjRjMCw1LjUsNC40LDkuOSw5LjksOS45aDQ3LjZjNS41LDAsOS45LTQuNCw5LjktOS45VjI1LjINCgkJCUM3OC43LDE5LjcsNzQuMywxNS4zLDY4LjgsMTUuM3oiLz4NCgk8L2c+DQo8L2c+DQo8ZyBpZD0i5ZyG6KeS55+p5b2iXzFf5ou36LSdXzFfIj4NCgk8Zz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTY1LjYsMjIuNkgyNC40Yy0zLjQsMC02LjEsMi43LTYuMSw2LjF2NDIuNmMwLDMuNCwyLjcsNi4xLDYuMSw2LjFoNDEuMWMzLjQsMCw2LjEtMi43LDYuMS02LjFWMjguNw0KCQkJQzcxLjYsMjUuMyw2OC45LDIyLjYsNjUuNiwyMi42eiIvPg0KCTwvZz4NCjwvZz4NCjx0ZXh0IHRyYW5zZm9ybT0ibWF0cml4KDAuOTk5MiAwIDAgMSAyMS40MTggNTUuMjUyOSkiIGNsYXNzPSJzdDMgc3Q0IHN0NSI+MDk6NTY8L3RleHQ+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4yLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA5MCAxMDAiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDkwIDEwMDsiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHN0eWxlIHR5cGU9InRleHQvY3NzIj4NCgkuc3Qwe2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0YyRjJGMjt9DQoJLnN0MXtmaWxsOm5vbmU7c3Ryb2tlOiM5OTk5OTk7c3Ryb2tlLXdpZHRoOjIuNTtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0MntmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM5OTk5OTk7fQ0KCS5zdDN7ZmlsbDojRkZGRkZGO30NCgkuc3Q0e2ZvbnQtZmFtaWx5OidSb2JvdG8tTWVkaXVtJzt9DQoJLnN0NXtmb250LXNpemU6MTguNTkyM3B4O30NCjwvc3R5bGU+DQo8ZyBpZD0i5b2i54q2XzFfMV8iPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNNjMuNiwxNS43YzAtMC4yLTEuNS03LjItMS41LTcuMmMtMC4zLTMtMi42LTMuNS0yLjYtMy41SDMwLjVDMjguMyw2LDI4LDcuNywyOCw3LjdsLTEuNSw3LjENCgkJCWMtMC44LDEuNCwwLjQsMS4yLDAuNCwxLjJoMzUuOUM2My44LDE2LjEsNjMuNiwxNS43LDYzLjYsMTUuN3oiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02My42LDE1LjdjMC0wLjItMS41LTcuMi0xLjUtNy4yYy0wLjMtMy0yLjYtMy41LTIuNi0zLjVIMzAuNUMyOC4zLDYsMjgsNy43LDI4LDcuN2wtMS41LDcuMQ0KCQkJYy0wLjgsMS40LDAuNCwxLjIsMC40LDEuMmgzNS45QzYzLjgsMTYuMSw2My42LDE1LjcsNjMuNiwxNS43eiIvPg0KCTwvZz4NCjwvZz4NCjxnIGlkPSLlvaLnirZfMV/mi7fotJ1fMV8iPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNNjIuOSw4NEgyNy4xYy0xLTAuMS0wLjcsMC40LTAuNywwLjRjMCwwLjIsMS41LDcuMiwxLjUsNy4yYzAuMywzLDIuNiwzLjUsMi42LDMuNWgyOC45DQoJCQljMi4zLTEsMi41LTIuNywyLjUtMi43bDEuNS03LjFDNjQuMSw4My44LDYyLjksODQsNjIuOSw4NHoiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02Mi45LDg0SDI3LjFjLTEtMC4xLTAuNywwLjQtMC43LDAuNGMwLDAuMiwxLjUsNy4yLDEuNSw3LjJjMC4zLDMsMi42LDMuNSwyLjYsMy41aDI4LjkNCgkJCWMyLjMtMSwyLjUtMi43LDIuNS0yLjdsMS41LTcuMUM2NC4xLDgzLjgsNjIuOSw4NCw2Mi45LDg0eiIvPg0KCTwvZz4NCjwvZz4NCjxnIGlkPSLlnIbop5Lnn6nlvaJfMV8xXyI+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik02OC44LDE1LjNIMjEuMmMtNS41LDAtOS45LDQuNC05LjksOS45djQ5LjRjMCw1LjUsNC40LDkuOSw5LjksOS45aDQ3LjZjNS41LDAsOS45LTQuNCw5LjktOS45VjI1LjINCgkJCUM3OC43LDE5LjcsNzQuMywxNS4zLDY4LjgsMTUuM3oiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik02OC44LDE1LjNIMjEuMmMtNS41LDAtOS45LDQuNC05LjksOS45djQ5LjRjMCw1LjUsNC40LDkuOSw5LjksOS45aDQ3LjZjNS41LDAsOS45LTQuNCw5LjktOS45VjI1LjINCgkJCUM3OC43LDE5LjcsNzQuMywxNS4zLDY4LjgsMTUuM3oiLz4NCgk8L2c+DQo8L2c+DQo8ZyBpZD0i5ZyG6KeS55+p5b2iXzFf5ou36LSdXzFfIj4NCgk8Zz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTY1LjYsMjIuNkgyNC40Yy0zLjQsMC02LjEsMi43LTYuMSw2LjF2NDIuNmMwLDMuNCwyLjcsNi4xLDYuMSw2LjFoNDEuMWMzLjQsMCw2LjEtMi43LDYuMS02LjFWMjguNw0KCQkJQzcxLjYsMjUuMyw2OC45LDIyLjYsNjUuNiwyMi42eiIvPg0KCTwvZz4NCjwvZz4NCjx0ZXh0IHRyYW5zZm9ybT0ibWF0cml4KDAuOTk5MiAwIDAgMSAyMS40MTggNTUuMjUyOSkiIGNsYXNzPSJzdDMgc3Q0IHN0NSI+MDk6NTY8L3RleHQ+DQo8L3N2Zz4NCg==';


class tWatch {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'tWatch';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const tWatchState = sourceTarget.getCustomState(tWatch.STATE_KEY);
            if (tWatchState) {
                newTarget.setCustomState(tWatch.STATE_KEY, Clone.simple(tWatchState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'tWatch',
            name: formatMessage({
                id: 'tWatch.tWatch',
                default: 'T-Watch',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5372e5',
            colourSecondary: '#4657ca',
            colourTertiary: '#3a4ca5',
            blocks: [
                {
                    opcode: 'tWatchStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'tWatch.tWatchStartUp',
                        default: 'when T-Watch starts up',
                        description: 'tWatch Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'tWatch.digitalRead',
                        default: 'read status of digital pin [PIN]',
                        description: 'Read digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '25'
                        }
                    }
                },
                {
                    opcode: 'analogRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatch.analogRead',
                        default: 'read analog pin [PIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '33'
                        }
                    }
                },
                {
                    opcode: 'digitalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.digitalWrite',
                        default: 'set digital pin [PIN] output as [MODE]',
                        description: 'Write digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '25'
                        },
                        MODE: {
                            type: ArgumentType.STRING,
                            menu: 'digitalModes',
                            defaultValue: 'true'
                        }

                    }
                },
                '---',
                {
                    opcode: 'setPWM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Write PWM Value of pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '25'
                        },
                        VALUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: '255',
                        }
                    }
                },
                '---',
                {
                    opcode: 'userButton',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'tWatch.userButton',
                        default: 'User Button Pressed?',
                        description: 'Check state of push button on IO36'
                    })
                },
                {
                    opcode: 'setClockTime',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.setClockTime',
                        default: 'set clock to [HRS] hours, [MINS] minutes, [SECS] seconds',
                        description: 'Set clock time'
                    }),
                    arguments: {
                        HRS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'hrs',
                            defaultValue: '10'
                        },
                        MINS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'mins',
                            defaultValue: '30'

                        },
                        SECS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'secs',
                            defaultValue: '45'
                        }
                    }
                },
                {
                    opcode: 'setClockDate',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.setClockDate',
                        default: 'set date to [DATE] / [MONTH] / [YEAR] and weekday [WEEKDAY]',
                        description: 'Set date'
                    }),
                    arguments: {
                        DATE: {
                            type: ArgumentType.NUMBER,
                            // menu: 'date',
                            defaultValue: '1'
                        },
                        MONTH: {
                            type: ArgumentType.STRING,
                            menu: 'month',
                            defaultValue: '1'
                        },
                        YEAR: {
                            type: ArgumentType.NUMBER,
                            // menu: 'year',
                            menu: 'year',
                            defaultValue: '19'
                        },
                        WEEKDAY: {
                            type: ArgumentType.STRING,
                            menu: 'weekday',
                            defaultValue: '3'
                        }
                    }
                },
                {
                    opcode: 'getDataFromClock',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatch.getDataFromClock',
                        default: 'get [TIME] from clock',
                        description: 'Get data from clock'
                    }),
                    arguments: {
                        TIME: {
                            type: ArgumentType.STRING,
                            menu: 'clock',
                            defaultValue: '11'
                        }
                    }
                },
                {
                    opcode: 'playTone',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.playTone',
                        default: 'play tone on [PIN] of note [NOTE] & beat [BEATS]',
                        description: 'Play a tone'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '25'
                        },
                        NOTE: {
                            type: ArgumentType.NUMBER,
                            menu: 'notes',
                            defaultValue: '65'
                        },
                        BEATS: {
                            type: ArgumentType.NUMBER,
                            menu: 'beatsList',
                            defaultValue: '500'
                        }
                    }
                },
                {
                    opcode: 'map',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatch.map',
                        default: 'map [VALUE] from [RANGE11] ~ [RANGE12] to [RANGE21] ~ [RANGE22]',
                        description: 'map'
                    }),
                    arguments: {
                        VALUE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '50'
                        },
                        RANGE11: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        RANGE12: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '255'
                        },
                        RANGE21: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        RANGE22: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1023'
                        },
                    }
                },
                {
                    opcode: 'getMacAddr',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatch.getMacAddr',
                        default: 'get bluetooth Mac Address',
                        description: 'Gives Bluetooth MAc Address'
                    })
                },
                {
                    message: formatMessage({
                        id: 'tWatch.blockSeparatorMessage1',
                        default: 'Deep Sleep',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'deepSleepTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.deepSleepTimer',
                        default: 'wake up from deep sleep mode in [TIME] seconds',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        TIME: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '2'
                        },
                    }
                },
                {
                    opcode: 'deepSleepExternalSource',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.deepSleepExternalSource',
                        default: 'wake up from deep sleep mode when [PIN] is [STATE]',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'rtcPins',
                            defaultValue: '36'
                        },
                        STATE: {
                            type: ArgumentType.STRING,
                            menu: 'digitalModes',
                            defaultValue: 'true'
                        }
                    }
                },
                {
                    opcode: 'deepSleepTouchPins',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.deepSleepTouchPins',
                        default: 'wake up from deep sleep mode when [TOUCH_PIN] is greater than [THRESHOLD]',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        TOUCH_PIN: {
                            type: ArgumentType.STRING,
                            menu: 'touchPinsMenu',
                            defaultValue: 'T0'
                        },
                        THRESHOLD: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '40'
                        }
                    }
                },
                // {
                //     opcode: 'deepSleepTouchSource',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'tWatch.deepSleepTimer',
                //         default: 'activate from deep sleep mode when [PIN] is [STATE]',
                //         description: 'Deep  Sleep mode on T-watch'
                //     }),
                //     arguments: {
                //        PIN:{
                //         type: ArgumentType.NUMBER,
                //         menu: 'rtcPins',
                //         defaultValue: '36'
                //        },
                //        STATE:{
                //         type: ArgumentType.STRING,
                //         menu: 'digitalModes',
                //         defaultValue: 'true'
                //        }
                //     }
                // },
                /*,
                '---',
                {                                       ///////////////
                    opcode: 'fillscreen',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillscreen',
                        default: 'fill screen with [COLOR] color',
                        description: 'Fill screen'
                    }),
                    arguments: {
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'setCursor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.setCursor',
                        default: 'set cursor at [X_AXIS], [Y_AXIS]',
                        description: 'Set cursor'
                    }),
                    arguments: {
                        X_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        }
                    }
                },
                {
                    opcode: 'setTextColorSize',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.setTextColorSize',
                        default: 'set text color to [TEXT_COLOR] with [BG_COLOR] background & size to [SIZE]',
                        description: 'Set pwm pin'
                    }),
                    arguments: {
                        TEXT_COLOR: {
                            type: ArgumentType.COLOR
                        },
                        BG_COLOR: {
                            type: ArgumentType.COLOR
                        },
                        SIZE: {
                            type: ArgumentType.NUMBER,
                            menu: 'textSize',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'write',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.write',
                        default: 'write [TEXT]',
                        description: 'Set text size'
                    }),
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
        id: "pictoBlox.staticText4",
        default: 'Hello World!',
        description: "PictoBlox static text for TEXT type block argument"
    })
                        }
                    }
                },
                {
                    opcode: 'drawLine',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.drawLine',
                        default: 'draw line from [X1_AXIS], [Y1_AXIS] to [X2_AXIS], [Y2_AXIS] of [COLOR] color',
                        description: 'Set text size'
                    }),
                    arguments: {
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '150'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '118'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'displayMatrix3',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.displayMatrix3',
                        default: 'display [MATRIX] of size [SIZE] px at position [XPOSITION], [YPOSITION],  color [COLOR] & background [COLOR2]',
                        description: 'display pattern on dot matrix'
                    }),
                    arguments: {
                        MATRIX: {
                            type: ArgumentType.MATRIX3, 
                            defaultValue: '00000001111110000000000001111111111000000000111111111111000000011111111111111000000111001111001110000011110011110011110000111111111111111100001111111111111111000011111111111111110000111111111111111100001111011111101111000001111000000111100000011111000011111000000011111111111100000000011111111110000000000001111110000000'
                        },
                        SIZE: {
                            type: ArgumentType.NUMBER,
                            menu: 'size',
                            defaultValue: '3'
                        },
                        XPOSITION: {
                            type: ArgumentType.MATHSLIDER159,
                            defaultValue: '0'
                        },
                        YPOSITION: {
                            type: ArgumentType.MATHSLIDER127,
                            defaultValue: '0'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR,
                        },
                        COLOR2: {
                            type: ArgumentType.COLOR,
                        },
                    }
                },
                '---',
                {
                    opcode: 'fillDrawRect',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillDrawRect',
                        default: '[OPTION] rectangle from [X1_AXIS], [Y1_AXIS] of width [X2_AXIS] & height [Y2_AXIS] & [COLOR] color',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '140'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '108'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawRoundRect',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillDrawRoundRect',
                        default: '[OPTION] round rect form [X1_AXIS],[Y1_AXIS] of width [X2_AXIS] height [Y2_AXIS] radius [RADIUS] color [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '140'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '108'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '5'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawCircle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillDrawCircle',
                        default: '[OPTION] circle from center [X1_AXIS] [Y1_AXIS] radius [RADIUS] colour[COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '80'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '64'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '30'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawEllipse',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillDrawEllipse',
                        default: '[OPTION] ellipse from center [X1_AXIS] [Y1_AXIS] X length [X2_AXIS] Y length [Y2_AXIS] colour [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '80'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '64'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '50'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '30'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawTriangle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatch.fillDrawTriangle',
                        default: '[OPTION] triangle : Point 1 [X1_AXIS] [Y1_AXIS] Point 2 [X2_AXIS] [Y2_AXIS] Point 3 [X3_AXIS] [Y3_AXIS] colour [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '150'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '64'
                        },
                        X3_AXIS: {
                            type: ArgumentType.MATHSLIDER159,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y3_AXIS: {
                            type: ArgumentType.MATHSLIDER127,
                            // menu: 'yAxis',
                            defaultValue: '118'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                }*/             ///////////////////////////////////
            ],
            menus: {
                analogPins: [
                    '32', '33', '34', '35', '36', '34', '25', '26'
                ],

                digitalModes: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.digitalModes.option1',
                            default: 'HIGH',
                            description: 'Menu'
                        }), value: 'true'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.digitalModes.option2',
                            default: 'LOW',
                            description: 'Menu'
                        }), value: 'false'
                    }
                ],

                digitalPins: [
                    '2', '4', '5', '12', '13', '14', '15', '16', '17', '18', '19', '21', '22', '23', '25', '26', '27'
                ],

                rtcPins: ['4', '2', '15', '36', '39', '34', '35', '32', '33', '25', '26', '27', '14', '12', '13'],
                touchPinsMenu: ['T0', 'T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9'],
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
                weekday: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option1',
                            default: 'Mon',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option2',
                            default: 'Tue',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option3',
                            default: 'Wed',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option4',
                            default: 'Thu',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option5',
                            default: 'Fri',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option6',
                            default: 'Sat',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option7',
                            default: 'Sun',
                            description: 'Menu'
                        }), value: '1'
                    }
                ],
                month: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option1',
                            default: 'Jan',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option2',
                            default: 'Feb',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option3',
                            default: 'Mar',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option4',
                            default: 'Apr',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option5',
                            default: 'May',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option6',
                            default: 'Jun',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option7',
                            default: 'Jul',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option8',
                            default: 'Aug',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option9',
                            default: 'Sep',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option10',
                            default: 'Oct',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option11',
                            default: 'Nov',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option12',
                            default: 'Dec',
                            description: 'Menu'
                        }), value: '12'
                    }
                ],
                clock: [{
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option1',
                        default: 'Hour',
                        description: 'Menu'
                    }), value: '11'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option2',
                        default: 'Minute',
                        description: 'Menu'
                    }), value: '12'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option3',
                        default: 'Second',
                        description: 'Menu'
                    }), value: '13'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option4',
                        default: 'Day',
                        description: 'Menu'
                    }), value: '21'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option5',
                        default: 'Month',
                        description: 'Menu'
                    }), value: '22'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option6',
                        default: 'Year',
                        description: 'Menu'
                    }), value: '23'
                },
                {
                    text: formatMessage({
                        id: 'extension.menu.clockFormattedMessages.option7',
                        default: 'Weekday',
                        description: 'Menu'
                    }), value: '24'
                }],
                voltageChannel: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option1',
                            default: 'red',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option3',
                            default: 'blue',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                year: [
                    { text: '2015', value: '15' },
                    { text: '2016', value: '16' },
                    { text: '2017', value: '17' },
                    { text: '2018', value: '18' },
                    { text: '2019', value: '19' },
                    { text: '2020', value: '20' },
                    { text: '2021', value: '21' },
                    { text: '2022', value: '22' },
                    { text: '2023', value: '23' },
                    { text: '2024', value: '24' },
                    { text: '2025', value: '25' },
                    { text: '2026', value: '26' },
                    { text: '2027', value: '27' },
                    { text: '2028', value: '28' },
                    { text: '2029', value: '29' },
                    { text: '2030', value: '30' },
                    { text: '2031', value: '31' },
                    { text: '2032', value: '32' },
                    { text: '2033', value: '33' },
                    { text: '2034', value: '34' },
                    { text: '2035', value: '35' },
                    { text: '2036', value: '36' },
                    { text: '2037', value: '37' },
                    { text: '2038', value: '38' },
                    { text: '2039', value: '39' },
                    { text: '2040', value: '40' },
                    { text: '2041', value: '41' },
                    { text: '2042', value: '42' },
                    { text: '2043', value: '43' },
                    { text: '2044', value: '44' },
                    { text: '2045', value: '45' },
                    { text: '2046', value: '46' },
                    { text: '2047', value: '47' },
                    { text: '2048', value: '48' },
                    { text: '2049', value: '49' }
                ],
                notes: [
                    { text: 'C2', value: '65' },
                    { text: 'D2', value: '73' },
                    { text: 'E2', value: '82' },
                    { text: 'F2', value: '87' },
                    { text: 'G2', value: '98' },
                    { text: 'A2', value: '110' },
                    { text: 'B2', value: '123' },
                    { text: 'C3', value: '131' },
                    { text: 'D3', value: '147' },
                    { text: 'E3', value: '165' },
                    { text: 'F3', value: '175' },
                    { text: 'G3', value: '196' },
                    { text: 'A3', value: '220' },
                    { text: 'B3', value: '247' },
                    { text: 'C4', value: '262' },
                    { text: 'D4', value: '294' },
                    { text: 'E4', value: '330' },
                    { text: 'F4', value: '349' },
                    { text: 'G4', value: '392' },
                    { text: 'A4', value: '440' },
                    { text: 'B4', value: '494' },
                    { text: 'C5', value: '523' },
                    { text: 'D5', value: '587' },
                    { text: 'E5', value: '659' },
                    { text: 'F5', value: '698' },
                    { text: 'G5', value: '784' },
                    { text: 'A5', value: '880' },
                    { text: 'B5', value: '988' },
                    { text: 'C6', value: '1047' },
                    { text: 'D6', value: '1175' },
                    { text: 'E6', value: '1319' },
                    { text: 'F6', value: '1397' },
                    { text: 'G6', value: '1568' },
                    { text: 'A6', value: '1760' },
                    { text: 'B6', value: '1976' },
                    { text: 'C7', value: '2093' },
                    { text: 'D7', value: '2349' },
                    { text: 'E7', value: '2637' },
                    { text: 'F7', value: '2794' },
                    { text: 'G7', value: '3136' },
                    { text: 'A7', value: '3520' },
                    { text: 'B7', value: '3951' },
                    { text: 'C8', value: '4186' },
                    { text: 'D8', value: '4699' }
                ],
                beatsList: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option1',
                            default: 'Half',
                            description: 'Menu'
                        }), value: '500'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option2',
                            default: 'Quarter',
                            description: 'Menu'
                        }), value: '250'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option3',
                            default: 'Eighth',
                            description: 'Menu'
                        }), value: '125'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option4',
                            default: 'Whole',
                            description: 'Menu'
                        }), value: '1000'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option5',
                            default: 'Double',
                            description: 'Menu'
                        }), value: '2000'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option6',
                            default: 'Zero',
                            description: 'Menu'
                        }), value: '0'
                    }
                ]
                /*,
                textSize: ['1', '2', '3', '4', '5',
                    {text: 'digital', value: '6'},
                    {text: 'ask MM', value: '7'}
                ],
                options: [
                    {text: 'Fill', value: '1'},
                    {text: 'Draw', value: '2'}
                ],
                size: [
                    '1', '2', '3', '4', '5', '6', '7', '8'
                ]*/
            }
        };
    }

    TWatchStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_TWatchStartUp');
            return;
        }
    }

    digitalRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.digitalRead(args, util, this);
        }
        return RealtimeMode.digitalRead(args, util, this);
    }

    analogRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.analogRead(args, util, this);
        }
        return RealtimeMode.analogRead(args, util, this);
    }

    digitalWrite(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.digitalWrite(args, util, this);
        }
        return RealtimeMode.digitalWrite(args, util, this);
    }

    setPWM(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setPWM(args, util, this);
        }
        return RealtimeMode.setPWM(args, util, this);
    }

    userButton(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.userButton(args, util, this);
        }
        return RealtimeMode.userButton(args, util, this);
    }

    setClockTime(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setClockTime(args, util, this);
        }
        return RealtimeMode.setClockTime(args, util, this);
    }

    setClockDate(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setClockDate(args, util, this);
        }
        return RealtimeMode.setClockDate(args, util, this);
    }

    playTone(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playTone(args, util, this);
        }
        return RealtimeMode.playTone(args, util, this);
    }

    getDataFromClock(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getDataFromClock(args, util, this);
        }
        return RealtimeMode.getDataFromClock(args, util, this);
    }

    map(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.map(args, util, this);
        }
        return RealtimeMode.map(args, util, this);
    }

    getMacAddr(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getMacAddr(args, util, this);
        }
        return RealtimeMode.getMacAddr(args, util, this);
    }

    deepSleepTimer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepTimer(args, util, this);
        }
        return RealtimeMode.deepSleepTimer(args, util, this);
    }

    deepSleepExternalSource(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepExternalSource(args, util, this);
        }
        return RealtimeMode.deepSleepExternalSource(args, util, this);
    }

    deepSleepTouchPins(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepTouchPins(args, util, this);
        }
        return RealtimeMode.deepSleepTouchPins(args, util, this);
    }
    // playTone (args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.playTone(args, util, this);
    //     }
    //     return RealtimeMode.playTone(args, util, this);
    // }

    // readTimer (args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.readTimer(args, util, this);
    //     }
    //     return RealtimeMode.readTimer(args, util, this);
    // }

    // resetTimer (args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.resetTimer(args, util, this);
    //     }
    //     return RealtimeMode.resetTimer(args, util, this);
    // }

    // cast (args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.cast(args, util, this);
    //     }
    //     return RealtimeMode.cast(args, util, this);
    // }
}

module.exports = tWatch;
