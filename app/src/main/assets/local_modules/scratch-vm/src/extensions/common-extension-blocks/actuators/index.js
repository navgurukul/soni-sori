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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggaWQ9IlhNTElEXzM2XyIgZmlsbD0iI0ZGRjIwMCIgZD0iTTIwLDguNkMxMy43LDguNiw4LjYsMTMuNyw4LjYsMjBTMTMuNywzMS40LDIwLDMxLjRTMzEuNCwyNi4zLDMxLjQsMjBTMjYuMyw4LjYsMjAsOC42eg0KCSBNMjkuMiwyMy44QzI5LjIsMjMuNywyOS4yLDIzLjcsMjkuMiwyMy44bC0yLjYtMi40Yy0wLjgtMC43LTAuOC0xLjksMC0yLjZsMi42LTIuM2MwLDAsMC4xLTAuMSwwLjEtMC4xYzAuNCwxLjEsMC43LDIuNCwwLjcsMy42DQoJQzI5LjksMjEuMywyOS43LDIyLjYsMjkuMiwyMy44eiBNMjcuOSwxMy45YzAsMC0wLjEsMC0wLjEsMEwyNC40LDE1Yy0xLDAuMy0yLTAuMy0yLjItMS4zbC0wLjctMy40YzAtMC4xLDAtMC4xLTAuMS0wLjINCglDMjQsMTAuNSwyNi4zLDExLjksMjcuOSwxMy45eiBNMTguNiwxMC4yYzAsMCwwLDAuMSwwLDAuMWwtMC43LDMuNGMtMC4yLDEtMS4zLDEuNi0yLjIsMS4zbC0zLjMtMS4xYy0wLjEsMC0wLjEsMC0wLjIsMA0KCUMxMy43LDExLjksMTYsMTAuNSwxOC42LDEwLjJ6IE0xMC43LDE2LjRDMTAuOCwxNi40LDEwLjgsMTYuNCwxMC43LDE2LjRsMi43LDIuNGMwLjgsMC43LDAuOCwxLjksMCwyLjZsLTIuNiwyLjNjMCwwLDAsMC0wLjEsMC4xDQoJYy0wLjUtMS4yLTAuNy0yLjQtMC43LTMuN0MxMC4xLDE4LjcsMTAuMywxNy41LDEwLjcsMTYuNHogTTEyLjIsMjYuMkMxMi4yLDI2LjIsMTIuMiwyNi4yLDEyLjIsMjYuMmwzLjMtMS4xYzEtMC4zLDIsMC4zLDIuMiwxLjMNCglsMC43LDMuNGMwLDAsMCwwLDAsMC4xQzE2LDI5LjUsMTMuOCwyOC4xLDEyLjIsMjYuMnogTTIxLjQsMjkuOEMyMS40LDI5LjgsMjEuNCwyOS44LDIxLjQsMjkuOGwwLjctMy40YzAuMi0xLDEuMy0xLjYsMi4yLTEuMw0KCWwzLjMsMS4xYzAsMCwwLjEsMCwwLjEsMEMyNi4yLDI4LjEsMjQsMjkuNSwyMS40LDI5Ljh6IE0xNy44LDIxLjNMMTcuOCwyMS4zYy0wLjUtMC44LTAuNS0xLjgsMC0yLjZsMCwwYzAuNS0wLjgsMS4zLTEuMywyLjItMS4zaDANCgljMC45LDAsMS44LDAuNSwyLjIsMS4zbDAsMGMwLjUsMC44LDAuNSwxLjgsMCwyLjZsMCwwYy0wLjUsMC44LTEuMywxLjMtMi4yLDEuM2gwQzE5LjEsMjIuNiwxOC4yLDIyLjEsMTcuOCwyMS4zeiIvPg0KPHBhdGggaWQ9IlhNTElEXzM1XyIgZmlsbD0iIzMzMzMzMyIgZD0iTTM1LjYsMjIuMWwtMS4xLDAuNGMtMC4yLDAuMS0wLjQsMC4zLTAuNCwwLjVjLTAuMSwwLjUtMC4yLDEtMC40LDEuNQ0KCWMtMC4xLDAuMiwwLDAuNSwwLjEsMC42bDAuNywwLjhjMC4yLDAuMiwwLjIsMC41LDAuMSwwLjhsLTEuNSwyLjVjLTAuMSwwLjItMC40LDAuNC0wLjcsMC4zbC0xLjEtMC4yYy0wLjIsMC0wLjUsMC0wLjYsMC4yDQoJYy0wLjQsMC40LTAuNywwLjgtMS4xLDEuMWMtMC4yLDAuMi0wLjMsMC40LTAuMiwwLjZsMC4yLDEuMWMwLjEsMC4zLTAuMSwwLjYtMC4zLDAuN2wtMi41LDEuNWMtMC4yLDAuMS0wLjYsMC4xLTAuOC0wLjFsLTAuOC0wLjcNCgljLTAuMi0wLjItMC40LTAuMi0wLjYtMC4xYy0wLjUsMC4yLTEsMC4zLTEuNSwwLjRjLTAuMiwwLTAuNCwwLjItMC41LDAuNGwtMC40LDEuMUMyMiwzNS44LDIxLjcsMzYsMjEuNSwzNmgtMi45DQoJYy0wLjMsMC0wLjUtMC4yLTAuNi0wLjRsLTAuNC0xLjFjLTAuMS0wLjItMC4zLTAuNC0wLjUtMC40Yy0wLjUtMC4xLTEtMC4yLTEuNS0wLjRjLTAuMi0wLjEtMC41LDAtMC42LDAuMUwxNCwzNC41DQoJYy0wLjIsMC4yLTAuNSwwLjItMC44LDAuMWwtMi41LTEuNWMtMC4yLTAuMS0wLjQtMC40LTAuMy0wLjdsMC4yLTEuMWMwLTAuMiwwLTAuNS0wLjItMC42QzEwLDMwLjMsOS43LDMwLDkuMywyOS42DQoJYy0wLjItMC4yLTAuNC0wLjMtMC42LTAuMmwtMS4xLDAuMmMtMC4zLDAuMS0wLjYtMC4xLTAuNy0wLjNsLTEuNS0yLjVjLTAuMS0wLjItMC4xLTAuNiwwLjEtMC44bDAuNy0wLjhjMC4yLTAuMiwwLjItMC40LDAuMS0wLjYNCgljLTAuMi0wLjUtMC4zLTEtMC40LTEuNWMwLTAuMi0wLjItMC40LTAuNC0wLjVsLTEuMS0wLjRDNC4yLDIyLDQsMjEuNyw0LDIxLjV2LTIuOWMwLTAuMywwLjItMC41LDAuNC0wLjZsMS4xLTAuNA0KCWMwLjItMC4xLDAuNC0wLjMsMC40LTAuNWMwLjEtMC41LDAuMi0xLDAuNC0xLjVjMC4xLTAuMiwwLTAuNS0wLjEtMC42TDUuNSwxNGMtMC4yLTAuMi0wLjItMC41LTAuMS0wLjhsMS41LTIuNQ0KCWMwLjEtMC4yLDAuNC0wLjQsMC43LTAuM2wxLjEsMC4yYzAuMiwwLDAuNSwwLDAuNi0wLjJDOS43LDEwLDEwLDkuNywxMC40LDkuM2MwLjItMC4yLDAuMy0wLjQsMC4yLTAuNmwtMC4yLTEuMQ0KCWMtMC4xLTAuMywwLjEtMC42LDAuMy0wLjdsMi41LTEuNWMwLjItMC4xLDAuNi0wLjEsMC44LDAuMWwwLjgsMC43YzAuMiwwLjIsMC40LDAuMiwwLjYsMC4xYzAuNS0wLjIsMS0wLjMsMS41LTAuNA0KCWMwLjIsMCwwLjQtMC4yLDAuNS0wLjRsMC40LTEuMUMxOCw0LjIsMTguMyw0LDE4LjUsNGgyLjljMC4zLDAsMC41LDAuMiwwLjYsMC40bDAuNCwxLjFjMC4xLDAuMiwwLjMsMC40LDAuNSwwLjQNCgljMC41LDAuMSwxLDAuMiwxLjUsMC40YzAuMiwwLjEsMC41LDAsMC42LTAuMUwyNiw1LjVjMC4yLTAuMiwwLjUtMC4yLDAuOC0wLjFsMi41LDEuNWMwLjIsMC4xLDAuNCwwLjQsMC4zLDAuN2wtMC4yLDEuMQ0KCWMwLDAuMiwwLDAuNSwwLjIsMC42YzAuNCwwLjQsMC44LDAuNywxLjEsMS4xYzAuMiwwLjIsMC40LDAuMywwLjYsMC4ybDEuMS0wLjJjMC4zLTAuMSwwLjYsMC4xLDAuNywwLjNsMS41LDIuNQ0KCWMwLjEsMC4yLDAuMSwwLjYtMC4xLDAuOGwtMC43LDAuOGMtMC4yLDAuMi0wLjIsMC40LTAuMSwwLjZjMC4yLDAuNSwwLjMsMSwwLjQsMS41YzAsMC4yLDAuMiwwLjQsMC40LDAuNWwxLjEsMC40DQoJYzAuMywwLjEsMC40LDAuMywwLjQsMC42djIuOUMzNiwyMS43LDM1LjgsMjIsMzUuNiwyMi4xeiBNMjAuMSw5LjNDMTQuMiw5LjMsOS40LDE0LDkuNCwxOS45czQuOCwxMC43LDEwLjcsMTAuNw0KCXMxMC43LTQuOCwxMC43LTEwLjdTMjYsOS4zLDIwLjEsOS4zeiIvPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggaWQ9IlhNTElEXzM2XyIgZmlsbD0iI0ZGRjIwMCIgZD0iTTIwLDguNkMxMy43LDguNiw4LjYsMTMuNyw4LjYsMjBTMTMuNywzMS40LDIwLDMxLjRTMzEuNCwyNi4zLDMxLjQsMjBTMjYuMyw4LjYsMjAsOC42eg0KCSBNMjkuMiwyMy44QzI5LjIsMjMuNywyOS4yLDIzLjcsMjkuMiwyMy44bC0yLjYtMi40Yy0wLjgtMC43LTAuOC0xLjksMC0yLjZsMi42LTIuM2MwLDAsMC4xLTAuMSwwLjEtMC4xYzAuNCwxLjEsMC43LDIuNCwwLjcsMy42DQoJQzI5LjksMjEuMywyOS43LDIyLjYsMjkuMiwyMy44eiBNMjcuOSwxMy45YzAsMC0wLjEsMC0wLjEsMEwyNC40LDE1Yy0xLDAuMy0yLTAuMy0yLjItMS4zbC0wLjctMy40YzAtMC4xLDAtMC4xLTAuMS0wLjINCglDMjQsMTAuNSwyNi4zLDExLjksMjcuOSwxMy45eiBNMTguNiwxMC4yYzAsMCwwLDAuMSwwLDAuMWwtMC43LDMuNGMtMC4yLDEtMS4zLDEuNi0yLjIsMS4zbC0zLjMtMS4xYy0wLjEsMC0wLjEsMC0wLjIsMA0KCUMxMy43LDExLjksMTYsMTAuNSwxOC42LDEwLjJ6IE0xMC43LDE2LjRDMTAuOCwxNi40LDEwLjgsMTYuNCwxMC43LDE2LjRsMi43LDIuNGMwLjgsMC43LDAuOCwxLjksMCwyLjZsLTIuNiwyLjNjMCwwLDAsMC0wLjEsMC4xDQoJYy0wLjUtMS4yLTAuNy0yLjQtMC43LTMuN0MxMC4xLDE4LjcsMTAuMywxNy41LDEwLjcsMTYuNHogTTEyLjIsMjYuMkMxMi4yLDI2LjIsMTIuMiwyNi4yLDEyLjIsMjYuMmwzLjMtMS4xYzEtMC4zLDIsMC4zLDIuMiwxLjMNCglsMC43LDMuNGMwLDAsMCwwLDAsMC4xQzE2LDI5LjUsMTMuOCwyOC4xLDEyLjIsMjYuMnogTTIxLjQsMjkuOEMyMS40LDI5LjgsMjEuNCwyOS44LDIxLjQsMjkuOGwwLjctMy40YzAuMi0xLDEuMy0xLjYsMi4yLTEuMw0KCWwzLjMsMS4xYzAsMCwwLjEsMCwwLjEsMEMyNi4yLDI4LjEsMjQsMjkuNSwyMS40LDI5Ljh6IE0xNy44LDIxLjNMMTcuOCwyMS4zYy0wLjUtMC44LTAuNS0xLjgsMC0yLjZsMCwwYzAuNS0wLjgsMS4zLTEuMywyLjItMS4zaDANCgljMC45LDAsMS44LDAuNSwyLjIsMS4zbDAsMGMwLjUsMC44LDAuNSwxLjgsMCwyLjZsMCwwYy0wLjUsMC44LTEuMywxLjMtMi4yLDEuM2gwQzE5LjEsMjIuNiwxOC4yLDIyLjEsMTcuOCwyMS4zeiIvPg0KPHBhdGggaWQ9IlhNTElEXzM1XyIgZmlsbD0iIzMzMzMzMyIgZD0iTTM1LjYsMjIuMWwtMS4xLDAuNGMtMC4yLDAuMS0wLjQsMC4zLTAuNCwwLjVjLTAuMSwwLjUtMC4yLDEtMC40LDEuNQ0KCWMtMC4xLDAuMiwwLDAuNSwwLjEsMC42bDAuNywwLjhjMC4yLDAuMiwwLjIsMC41LDAuMSwwLjhsLTEuNSwyLjVjLTAuMSwwLjItMC40LDAuNC0wLjcsMC4zbC0xLjEtMC4yYy0wLjIsMC0wLjUsMC0wLjYsMC4yDQoJYy0wLjQsMC40LTAuNywwLjgtMS4xLDEuMWMtMC4yLDAuMi0wLjMsMC40LTAuMiwwLjZsMC4yLDEuMWMwLjEsMC4zLTAuMSwwLjYtMC4zLDAuN2wtMi41LDEuNWMtMC4yLDAuMS0wLjYsMC4xLTAuOC0wLjFsLTAuOC0wLjcNCgljLTAuMi0wLjItMC40LTAuMi0wLjYtMC4xYy0wLjUsMC4yLTEsMC4zLTEuNSwwLjRjLTAuMiwwLTAuNCwwLjItMC41LDAuNGwtMC40LDEuMUMyMiwzNS44LDIxLjcsMzYsMjEuNSwzNmgtMi45DQoJYy0wLjMsMC0wLjUtMC4yLTAuNi0wLjRsLTAuNC0xLjFjLTAuMS0wLjItMC4zLTAuNC0wLjUtMC40Yy0wLjUtMC4xLTEtMC4yLTEuNS0wLjRjLTAuMi0wLjEtMC41LDAtMC42LDAuMUwxNCwzNC41DQoJYy0wLjIsMC4yLTAuNSwwLjItMC44LDAuMWwtMi41LTEuNWMtMC4yLTAuMS0wLjQtMC40LTAuMy0wLjdsMC4yLTEuMWMwLTAuMiwwLTAuNS0wLjItMC42QzEwLDMwLjMsOS43LDMwLDkuMywyOS42DQoJYy0wLjItMC4yLTAuNC0wLjMtMC42LTAuMmwtMS4xLDAuMmMtMC4zLDAuMS0wLjYtMC4xLTAuNy0wLjNsLTEuNS0yLjVjLTAuMS0wLjItMC4xLTAuNiwwLjEtMC44bDAuNy0wLjhjMC4yLTAuMiwwLjItMC40LDAuMS0wLjYNCgljLTAuMi0wLjUtMC4zLTEtMC40LTEuNWMwLTAuMi0wLjItMC40LTAuNC0wLjVsLTEuMS0wLjRDNC4yLDIyLDQsMjEuNyw0LDIxLjV2LTIuOWMwLTAuMywwLjItMC41LDAuNC0wLjZsMS4xLTAuNA0KCWMwLjItMC4xLDAuNC0wLjMsMC40LTAuNWMwLjEtMC41LDAuMi0xLDAuNC0xLjVjMC4xLTAuMiwwLTAuNS0wLjEtMC42TDUuNSwxNGMtMC4yLTAuMi0wLjItMC41LTAuMS0wLjhsMS41LTIuNQ0KCWMwLjEtMC4yLDAuNC0wLjQsMC43LTAuM2wxLjEsMC4yYzAuMiwwLDAuNSwwLDAuNi0wLjJDOS43LDEwLDEwLDkuNywxMC40LDkuM2MwLjItMC4yLDAuMy0wLjQsMC4yLTAuNmwtMC4yLTEuMQ0KCWMtMC4xLTAuMywwLjEtMC42LDAuMy0wLjdsMi41LTEuNWMwLjItMC4xLDAuNi0wLjEsMC44LDAuMWwwLjgsMC43YzAuMiwwLjIsMC40LDAuMiwwLjYsMC4xYzAuNS0wLjIsMS0wLjMsMS41LTAuNA0KCWMwLjIsMCwwLjQtMC4yLDAuNS0wLjRsMC40LTEuMUMxOCw0LjIsMTguMyw0LDE4LjUsNGgyLjljMC4zLDAsMC41LDAuMiwwLjYsMC40bDAuNCwxLjFjMC4xLDAuMiwwLjMsMC40LDAuNSwwLjQNCgljMC41LDAuMSwxLDAuMiwxLjUsMC40YzAuMiwwLjEsMC41LDAsMC42LTAuMUwyNiw1LjVjMC4yLTAuMiwwLjUtMC4yLDAuOC0wLjFsMi41LDEuNWMwLjIsMC4xLDAuNCwwLjQsMC4zLDAuN2wtMC4yLDEuMQ0KCWMwLDAuMiwwLDAuNSwwLjIsMC42YzAuNCwwLjQsMC44LDAuNywxLjEsMS4xYzAuMiwwLjIsMC40LDAuMywwLjYsMC4ybDEuMS0wLjJjMC4zLTAuMSwwLjYsMC4xLDAuNywwLjNsMS41LDIuNQ0KCWMwLjEsMC4yLDAuMSwwLjYtMC4xLDAuOGwtMC43LDAuOGMtMC4yLDAuMi0wLjIsMC40LTAuMSwwLjZjMC4yLDAuNSwwLjMsMSwwLjQsMS41YzAsMC4yLDAuMiwwLjQsMC40LDAuNWwxLjEsMC40DQoJYzAuMywwLjEsMC40LDAuMywwLjQsMC42djIuOUMzNiwyMS43LDM1LjgsMjIsMzUuNiwyMi4xeiBNMjAuMSw5LjNDMTQuMiw5LjMsOS40LDE0LDkuNCwxOS45czQuOCwxMC43LDEwLjcsMTAuNw0KCXMxMC43LTQuOCwxMC43LTEwLjdTMjYsOS4zLDIwLjEsOS4zeiIvPg0KPC9zdmc+DQo=';


class actuators {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'actuators';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'actuators';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const actuatorsState = sourceTarget.getCustomState(actuators.STATE_KEY);
            if (actuatorsState) {
                newTarget.setCustomState(actuators.STATE_KEY, Clone.simple(actuatorsState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [{
            index: 0,
            allowedBoards: ['arduinoUno', 'arduinoMega', 'arduinoNano', 'esp32', 'tWatch', 'tecBits'],
            getBlocks: (extensionId) => [{
                opcode: 'initialiseMotor',
                blockType: BlockType.COMMAND,
                text: formatMessage({
                    id: 'actuators.initialiseMotor',
                    default: 'connect motor [MOTOR] direction 1 [DIRECTION1] direction 2 [DIRECTION2] & PWM [PWM]',
                    description: 'initialise motor'
                }),
                arguments: {
                    MOTOR: {
                        type: ArgumentType.NUMBER,
                        menu: 'motor',
                        defaultValue: Config.initialiseMotor[board]['MOTOR']
                    },
                    DIRECTION1: {
                        type: ArgumentType.NUMBER,
                        menu: 'digitalPins',
                        defaultValue: Config.initialiseMotor[board]['DIRECTION1']
                    },
                    DIRECTION2: {
                        type: ArgumentType.NUMBER,
                        menu: 'digitalPins',
                        defaultValue: Config.initialiseMotor[board]['DIRECTION2']
                    },
                    PWM: {
                        type: ArgumentType.NUMBER,
                        menu: 'pwmPins',
                        defaultValue: Config.initialiseMotor[board]['PWM']
                    }
                }
            }]
        },
        {
            index: 3,
            allowedBoards: ['evive', 'arduinoUno', 'arduinoMega', 'arduinoNano', 'tecBits'],
            getBlocks: (extensionId) => [
                '---',
                {
                    opcode: 'setBLDC',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.setBLDC',
                        default: 'run BLDC motor on [SERVO_CHANNEL] at [SPEED] % speed',
                        description: 'Set BLDC motor'
                    }),
                    arguments: {
                        SERVO_CHANNEL: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoChannel',
                            defaultValue: this.BLDC_DEFAULT_VALUE
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '30'
                        }
                    }
                }]
        },
        {
            index: -1,
            allowedBoards: ['arduinoUno', 'arduinoMega', 'arduinoNano', 'tecBits'],
            getBlocks: (extensionId) => [{
                message: formatMessage({
                    id: 'actuators.blockSeparatorMessage1',
                    default: 'Arduino Motor Driver Shield (U)',
                    description: 'Blocks separator message'
                })
            },
            {
                opcode: 'motorShieldRunMotor',
                blockType: BlockType.COMMAND,
                text: formatMessage({
                    id: 'actuators.motorShieldRunMotor',
                    default: 'run motor [MOTOR2] [DIRECTION] with speed [SPEED]%',
                    description: 'move stepper motor'
                }),
                arguments: {
                    MOTOR2: {
                        type: ArgumentType.NUMBER,
                        menu: 'motor',
                        defaultValue: '1'
                    },
                    DIRECTION: {
                        type: ArgumentType.NUMBER,
                        menu: 'motorDirection2',
                        defaultValue: 'FORWARD'
                    },
                    SPEED: {
                        type: ArgumentType.MATHSLIDER100,
                        defaultValue: '90'
                    }
                }
            },
            ]
        },
        {
            index: 7,
            allowedBoards: ['tWatch'],
            getBlocks: (extensionId) => [
                {
                    message: formatMessage({
                        id: 'actuators.blockSeparatorMessage2',
                        default: 'Vibration motor(U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'tWatchVibrationMotor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.tWatchVibrationMotor',
                        default: 'turn [VMOTOR_STATE] vibration motor',
                        description: 'run vibration motor'
                    }),
                    arguments: {
                        VMOTOR_STATE: {
                            type: ArgumentType.STRING,
                            menu: 'vmotorState',
                            defaultValue: 'true'
                        }
                    }
                }]
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
    };

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

    get PWM_PINS() {
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
            case 'Quarky': {
                return ExtensionMenu.pwmPins.quarky;
            }
            case 'TecBits': {
                return ExtensionMenu.digitalPins.tecBits;
            }
        }
        return ['0'];
    }

    get SERVO_CHANNEL() {
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
            case 'Quarky': {
                return ExtensionMenu.servoChannel.quarky;
            }
            case 'TecBits': {
                return ExtensionMenu.servoChannel.tecBits;
            }
        }
        return ['0'];
    }

    get SERVO_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.servoChannel.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.servoChannel.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.servoChannel.arduinoUno;
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
            case 'Quarky': {
                return Config.servoChannel.quarky;
            }
            case 'TecBits': {
                return Config.servoChannel.tecBits;
            }
        }
        return null;
    }

    get BLDC_DEFAULT_VALUE() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return Config.BLDC.arduinoNano;
            }
            case 'Arduino Mega': {
                return Config.BLDC.arduinoMega;
            }
            case 'Arduino Uno': {
                return Config.BLDC.arduinoUno;
            }
            case 'evive': {
                return Config.BLDC.evive;
            }
            case 'TecBits': {
                return Config.BLDC.tecBits;
            }
        }
        return null;
    }

    get MOTOR() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.motor.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.motor.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.motor.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.motor.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.motor.esp32;
            }
            case 'T-Watch': {
                return ExtensionMenu.motor.tWatch;
            }
            case 'Quon': {
                return ExtensionMenu.motor.quon;
            }
            case 'Quarky': {
                return ExtensionMenu.motor.quarky;
            }
            case 'TecBits': {
                return ExtensionMenu.motor.tecBits;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: extensionId,
            name: formatMessage({
                id: 'actuators.actuators',
                default: 'Actuators',
                description: 'name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5fbc2e',
            colourSecondary: '#41a80b',
            colourTertiary: '#3d9907',
            allowRefresh: true,
            blocks: [
                {
                    opcode: 'runMotor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.runMotor',
                        default: 'run motor [MOTOR] in direction [DIRECTION] with speed [SPEED]%',
                        description: 'Run motor'
                    }),
                    arguments: {
                        MOTOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'motor',
                            defaultValue: '1'
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
                    opcode: 'updateMotorState',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.updateMotorState',
                        default: '[MOTOR_STATE] motor [MOTOR]',
                        description: 'Lock/Free motor'
                    }),
                    arguments: {
                        MOTOR_STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'motorState',
                            defaultValue: '4'
                        },
                        MOTOR: {
                            type: ArgumentType.NUMBER,
                            menu: 'motor',
                            defaultValue: '1'
                        }
                    }
                },
                '---',
                {
                    opcode: 'setServo',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.setServo',
                        default: 'set servo on [SERVO_CHANNEL] to [ANGLE] angle',
                        description: 'Set servo'
                    }),
                    arguments: {
                        SERVO_CHANNEL: {
                            type: ArgumentType.STRING,
                            menu: 'servoChannel',
                            defaultValue: this.SERVO_DEFAULT_VALUE
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '30'
                        }
                    }
                },
                '---',
                {
                    opcode: 'setRelay',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'actuators.setRelay',
                        default: 'set relay at pin [DIGITAL_PIN] to [MODE]',
                        description: 'Set relay'
                    }),
                    arguments: {
                        DIGITAL_PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        MODE: {
                            type: ArgumentType.STRING,
                            menu: 'relayState',
                            defaultValue: 'true'
                        }
                    }
                }
            ],
            menus: {
                motor: this.MOTOR,
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
                            default: 'reverse',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                motorDirection2: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection2.option1',
                            default: 'forward',
                            description: 'Menu'
                        }), value: 'FORWARD'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection2.option2',
                            default: 'reverse',
                            description: 'Menu'
                        }), value: 'BACKWARD'
                    }
                ],
                servoChannel: this.SERVO_CHANNEL,
                digitalPins: this.DIGITAL_PINS,
                pwmPins: this.PWM_PINS,
                relayState: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.relayState.option1',
                            default: 'ON',
                            description: 'Menu'
                        }), value: 'false'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.relayState.option2',
                            default: 'OFF',
                            description: 'Menu'
                        }), value: 'true'
                    }
                ],
                vmotorState: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.relayState.option1',
                            default: 'ON',
                            description: 'Menu'
                        }), value: 'false'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.relayState.option2',
                            default: 'OFF',
                            description: 'Menu'
                        }), value: 'true'
                    }
                ],
                speedmotor: [
                    '0', '25', '50', '75', '100'
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

    initialiseMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseMotor(args, util, this);
        }
        return RealtimeMode.initialiseMotor(args, util, this);
    }

    runMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.runMotor(args, util, this);
        }
        return RealtimeMode.runMotor(args, util, this);
    }

    updateMotorState(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.updateMotorState(args, util, this);
        }
        return RealtimeMode.updateMotorState(args, util, this);
    }

    setServo(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServo(args, util, this);
        }
        return RealtimeMode.setServo(args, util, this);
    }

    setRelay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setRelay(args, util, this);
        }
        return RealtimeMode.setRelay(args, util, this);
    }

    setBLDC(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setBLDC(args, util, this);
        }
        return RealtimeMode.setBLDC(args, util, this);
    }

    motorShieldRunMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.motorShieldRunMotor(args, util, this);
        }
        return RealtimeMode.motorShieldRunMotor(args, util, this);
    }

    tWatchVibrationMotor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.tWatchVibrationMotor(args, util, this);
        }
        return RealtimeMode.tWatchVibrationMotor(args, util, this);
    }
}
module.exports = actuators;
