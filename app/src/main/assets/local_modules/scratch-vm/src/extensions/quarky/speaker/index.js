const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PHRpdGxlPkFydGJvYXJkIDc5PC90aXRsZT48cGF0aCBkPSJNMTguNDUsNC44Niw0Ljk0LDEzLjkyVjI2LjZsMTMuNTEsOS4wNmEzLjY5LDMuNjksMCwwLDAsNS43NC0zLjA2VjcuOTJBMy42OSwzLjY5LDAsMCwwLDE4LjQ1LDQuODZaIiBzdHlsZT0iZmlsbDojZjRkNjViIi8+PHBhdGggZD0iTTIuNzIsMTMuMkExLjc2LDEuNzYsMCwwLDAsMS4yLDE1LjEyVjI1LjRhMS43NSwxLjc1LDAsMCwwLDEuNTIsMS45MWgxLjRWMTMuMloiIHN0eWxlPSJmaWxsOiNmOWM3M2QiLz48cGF0aCBkPSJNNC42NCwxMy4ySDQuMTJWMjcuMzFoLjUyQTEuNzYsMS43NiwwLDAsMCw2LjE2LDI1LjRWMTUuMTJBMS43NiwxLjc2LDAsMCwwLDQuNjQsMTMuMloiIHN0eWxlPSJmaWxsOiNmOWM3M2QiLz48cGF0aCBkPSJNMzAuNSwxMi4xYy0xLjItMS4xOC0zLC42Ni0xLjg0LDEuODRhOC40Nyw4LjQ3LDAsMCwxLS4wNSwxMi4xMmMtMS4yLDEuMTcuNjQsMywxLjg0LDEuODRhMTEsMTEsMCwwLDAsLjA1LTE1LjhaIiBzdHlsZT0iZmlsbDojYjVmN2Y3Ii8+PHBhdGggZD0iTTM0LDhjLTEuMTUtMS4yMy0zLC42Mi0xLjg1LDEuODVhMTUuMSwxNS4xLDAsMCwxLDQuMDgsOS44M2MuMDgsMy40MS0xLDguMDYtMy45MSwxMC4xOS0xLjM0LDEsMCwzLjI0LDEuMzIsMi4yNSwzLjYxLTIuNjQsNS4yLTcuNzMsNS4yLTEyQTE3Ljk0LDE3Ljk0LDAsMCwwLDM0LDhaIiBzdHlsZT0iZmlsbDojYjVmN2Y3Ii8+PC9zdmc+';
// eslint-disable-next-line max-len
const menuIconURI = blockIconURI;

class quarkySpeaker {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkySpeaker';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkySpeakerState = sourceTarget.getCustomState(quarkySpeaker.STATE_KEY);
            if (quarkySpeakerState) {
                newTarget.setCustomState(quarkySpeaker.STATE_KEY, Clone.simple(quarkySpeakerState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkySpeaker',
            name: formatMessage({
                id: 'quarkySpeaker.speaker',
                default: 'Speaker',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#CF63CF',
            colourSecondary: '#C94FC9',
            colourTertiary: '#BD42BD',
            blocks: [
                {
                    opcode: 'playSound',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkySpeaker.playSound',
                        default: 'play sound [SOUND]',
                        description: 'play sound on speaker'
                    }),
                    arguments: {
                        SOUND: {
                            type: ArgumentType.STRING,
                            menu: 'soundFiles',
                            defaultValue: 'QuarkyIntro'
                        }
                    }
                },
                {
                    opcode: 'playSoundUntilSound',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkySpeaker.playSoundUntilSound',
                        default: 'play sound [SOUND] until done',
                        description: 'play sound on speaker '
                    }),
                    arguments: {
                        SOUND: {
                            type: ArgumentType.STRING,
                            menu: 'soundFiles',
                            defaultValue: 'QuarkyIntro'
                        }
                    }
                },
                {
                    opcode: 'playNote',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkySpeaker.playNote',
                        default: 'play tone of note [NOTE] with duartion [NOTE_DURATION]',
                        description: 'play tones on speaker'
                    }),
                    arguments: {
                        NOTE: {
                            type: ArgumentType.STRING,
                            menu: 'toneNotes',
                            defaultValue: 'C4'
                        },

                        NOTE_DURATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'noteDuration',
                            defaultValue: '8'
                        },

                    }
                },
                // {
                //     opcode: 'setNoteTempo',
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: 'quarkySpeaker.setNoteTempo',
                //         default: 'set Tempo [TEMPO]',
                //         description: 'set tone tempo'
                //     }),
                //     arguments: {
                //         TEMPO: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'tempo',
                //             defaultValue: 94
                //         },
                //     }
                // },
                {
                    opcode: 'stopSound',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkySpeaker.stopSound',
                        default: 'stop sound',
                        description: 'Stop speaker'
                    }),
                },

            ],
            menus: {
                soundFiles: [
                    {text:'Hi, i am Quarky',value:'QuarkyIntro'},
                    {text:'turning left',      value:'turningLeft'},
                    {text:'turning right',     value:'turningRight'},
                    {text:'go straight',      value:'goStraight'},
                    {text:'u turn',     value:'UTurnDetected'},
                    {text:'one detected',      value:'oneDetected'},
                    {text:'two detected',      value:'twoDetected'},
                    {text:'three detected',    value:'threeDetected'},
                    {text:'four detected',     value:'fourDetected'},
                    {text:'five detected',     value:'fiveDetected'},
                    {text:'quarky boot up',      value:'QuarkyBootUp'}
                ],
                toneNotes:  [
                    'C4','D4','E4','F4','G4','A4','B4',
                    'C5','D5','E5','F5','G5','A5','B5',
                    'C6','D6','E6','F6','G6','A6','B6',
                    'C7','D7','E7','F7','G7','A7','B7'
                ],
                noteDuration:
                [
                    {text:'Whole',        value:'1'},
                    {text:'Half',         value:'2'},
                    {text:'Quarter',      value:'4'},
                    {text:'Eighth',       value:'8'},
                    {text:'Sixteenth',    value:'16'}
                ],
                // tempo:
                // [
                //     {text:'prestissimo',  value:220},
                //     {text:'presto',       value:184},
                //     {text:'allegro',      value:144},
                //     {text:'moderato',     value:114},
                //     {text:'andante',      value:94},
                //     {text:'adagio',       value:71},
                //     {text:'lento',        value:50},
                //     {text:'grave',        value:30}
                // ]
            }
        };
    }

    playSound(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playSound(args, util, this);
        }
        return RealtimeMode.playSound(args, util, this);
    }

    playSoundUntilSound(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playSoundUntilSound(args, util, this);
        }
        return RealtimeMode.playSoundUntilSound(args, util, this);
    }

    playNote(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playNote(args, util, this);
        }
        return RealtimeMode.playNote(args, util, this);
    }

    // setNoteTempo(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.setNoteTempo(args, util, this);
    //     }
    //     return RealtimeMode.setNoteTempo(args, util, this);
    // }

    stopSound(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.stopSound(args, util, this);
        }
        return RealtimeMode.stopSound(args, util, this);
    }
}

module.exports = quarkySpeaker;
