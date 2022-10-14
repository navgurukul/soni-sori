const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzIiIGRhdGEtbmFtZT0iTGF5ZXIgMzIiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMSwuY2xzLTEwLC5jbHMtOXtmaWxsOm5vbmU7fS5jbHMtMntjbGlwLXBhdGg6dXJsKCNjbGlwLXBhdGgpO30uY2xzLTN7b3BhY2l0eTowLjU7fS5jbHMtNHtjbGlwLXBhdGg6dXJsKCNjbGlwLXBhdGgtMik7fS5jbHMtNXtvcGFjaXR5OjAuMDk7fS5jbHMtNntmaWxsOiMxNDNjNWU7fS5jbHMtMTAsLmNscy02e3N0cm9rZTojZmNmZGZmO30uY2xzLTEwLC5jbHMtNiwuY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MC40N3B4O30uY2xzLTd7ZmlsbDojZDI1MDgxO30uY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2U6I2ZmZjt9LmNscy04e2ZpbGw6I2ZjZmRmZjt9LmNscy0xMXtmaWxsOiNmMWFkMWQ7fTwvc3R5bGU+PGNsaXBQYXRoIGlkPSJjbGlwLXBhdGgiPjxyZWN0IGlkPSJfUmVjdGFuZ2xlXyIgZGF0YS1uYW1lPSImbHQ7UmVjdGFuZ2xlJmd0OyIgY2xhc3M9ImNscy0xIiB4PSItMjk1LjY5IiB5PSItMzk0LjMxIiB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3MiIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJjbGlwLXBhdGgtMiI+PHJlY3QgaWQ9Il9SZWN0YW5nbGVfMiIgZGF0YS1uYW1lPSImbHQ7UmVjdGFuZ2xlJmd0OyIgY2xhc3M9ImNscy0xIiB4PSI0Ni44MSIgeT0iLTI0MS4wNSIgd2lkdGg9IjI2My4zMyIgaGVpZ2h0PSIyMTgiLz48L2NsaXBQYXRoPjwvZGVmcz48dGl0bGU+SWNvbl9OZXVyYWwgTmV0d29ya3MtMDI8L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtNiIgZD0iTTYuNTQsMTMuNjhTOC40Nyw3LjA2LDE2Ljc5LDkuMzdhOCw4LDAsMCwxLDkuOTQuMjNzNi44Ni0xLjA5LDkuNCw2LjM5YTcsNywwLDAsMSwyLjU1LDcsNy40LDcuNCwwLDAsMS00LjYsNS4wNiw0LjA2LDQuMDYsMCwwLDEtLjQ3LDEuMzRBNS4wOSw1LjA5LDAsMCwxLDMwLDMxLjY2YTExLjYzLDExLjYzLDAsMCwwLDEsMy45MWgtMy43cy0wLjg1LTIuMzEtNi4zMi01LjdhNy44Nyw3Ljg3LDAsMCwxLTguMTctMi4yMyw5Ljc5LDkuNzksMCwwLDEtMS45LjQsOS45LDkuOSwwLDAsMS01LjIyLS44Nyw5LjQ3LDkuNDcsMCwwLDEtMy4wNy0yLjM4QTcuNDQsNy40NCwwLDAsMSwxLjYsMTgsNy4zOSw3LjM5LDAsMCwxLDYuNTQsMTMuNjhaIi8+PGxpbmUgY2xhc3M9ImNscy03IiB4MT0iNi41NiIgeTE9IjEzLjYxIiB4Mj0iMTAuNjUiIHkyPSIxNi41OSIvPjxsaW5lIGNsYXNzPSJjbHMtOCIgeDE9IjIxLjA2IiB5MT0iMjYuNzIiIHgyPSIyMS4wNiIgeTI9IjI5Ljk0Ii8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iOC44MSIgeTE9IjI0LjU4IiB4Mj0iOC44MSIgeTI9IjI4LjAzIi8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iMzUuOSIgeTE9IjE1LjM1IiB4Mj0iMjUuNjgiIHkyPSIxNS4zNSIvPjxsaW5lIGNsYXNzPSJjbHMtOCIgeDE9IjM4Ljg0IiB5MT0iMjAuMTgiIHgyPSIzMy4zNCIgeTI9IjIwLjQzIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSIxMS45NyAxNy42OCAyNS4wNiAxNS43MSAyMS4wNiAyNS44OCAzMi41IDIwLjE4IDI2LjAyIDE1Ljk5Ii8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSI5LjQ0IDI0LjQzIDIxLjIgMjYuMzEgMTIuNzkgMTcuNzYgOS4wMiAyMy44MyAyLjEzIDI0LjA1Ii8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSIxMy41NiAxNi4zNSAxOC42NSAxMS45MyAyNC40MyAxNC44MSIvPjxsaW5lIGNsYXNzPSJjbHMtNyIgeDE9IjE4LjQ1IiB5MT0iMTEuMTkiIHgyPSIxNi4yOSIgeTI9IjkuMjQiLz48bGluZSBjbGFzcz0iY2xzLTgiIHgxPSIyNS45MyIgeTE9IjE0LjY3IiB4Mj0iMzAuMjMiIHkyPSI5LjkxIi8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iMjcuNTQiIHkxPSIyOS42NiIgeDI9IjI4LjgzIiB5Mj0iMzUuNTciLz48cG9seWxpbmUgY2xhc3M9ImNscy0xMCIgcG9pbnRzPSIyMS43NSAyNS41NCAzMi4yMSAyMCAyOC4wOCAyOS4zOCAyMS4wNiAyNS44OCIvPjxjaXJjbGUgY2xhc3M9ImNscy0xMSIgY3g9IjExLjk3IiBjeT0iMTcuNDciIHI9IjIuNTMiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTEiIGN4PSIyNS4yMyIgY3k9IjE2LjE0IiByPSIyLjUzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMzIuNSIgY3k9IjIwLjE4IiByPSIxLjgzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMjEuMiIgY3k9IjI1Ljg4IiByPSIxLjgzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMjcuNzYiIGN5PSIyOC44MSIgcj0iMS44MyIvPjxjaXJjbGUgY2xhc3M9ImNscy0xMSIgY3g9IjE4LjY1IiBjeT0iMTEuOTMiIHI9IjEuNTYiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTEiIGN4PSI5LjI4IiBjeT0iMjQuMjkiIHI9IjEuODMiLz48L3N2Zz4=';

const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzIiIGRhdGEtbmFtZT0iTGF5ZXIgMzIiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMSwuY2xzLTEwLC5jbHMtOXtmaWxsOm5vbmU7fS5jbHMtMntjbGlwLXBhdGg6dXJsKCNjbGlwLXBhdGgpO30uY2xzLTN7b3BhY2l0eTowLjU7fS5jbHMtNHtjbGlwLXBhdGg6dXJsKCNjbGlwLXBhdGgtMik7fS5jbHMtNXtvcGFjaXR5OjAuMDk7fS5jbHMtNntmaWxsOiMxNDNjNWU7fS5jbHMtMTAsLmNscy02e3N0cm9rZTojZmNmZGZmO30uY2xzLTEwLC5jbHMtNiwuY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MC40N3B4O30uY2xzLTd7ZmlsbDojZDI1MDgxO30uY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2U6I2ZmZjt9LmNscy04e2ZpbGw6I2ZjZmRmZjt9LmNscy0xMXtmaWxsOiNmMWFkMWQ7fTwvc3R5bGU+PGNsaXBQYXRoIGlkPSJjbGlwLXBhdGgiPjxyZWN0IGlkPSJfUmVjdGFuZ2xlXyIgZGF0YS1uYW1lPSImbHQ7UmVjdGFuZ2xlJmd0OyIgY2xhc3M9ImNscy0xIiB4PSItMjk1LjY5IiB5PSItMzk0LjMxIiB3aWR0aD0iNjAwIiBoZWlnaHQ9IjM3MiIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJjbGlwLXBhdGgtMiI+PHJlY3QgaWQ9Il9SZWN0YW5nbGVfMiIgZGF0YS1uYW1lPSImbHQ7UmVjdGFuZ2xlJmd0OyIgY2xhc3M9ImNscy0xIiB4PSI0Ni44MSIgeT0iLTI0MS4wNSIgd2lkdGg9IjI2My4zMyIgaGVpZ2h0PSIyMTgiLz48L2NsaXBQYXRoPjwvZGVmcz48dGl0bGU+SWNvbl9OZXVyYWwgTmV0d29ya3MtMDI8L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtNiIgZD0iTTYuNTQsMTMuNjhTOC40Nyw3LjA2LDE2Ljc5LDkuMzdhOCw4LDAsMCwxLDkuOTQuMjNzNi44Ni0xLjA5LDkuNCw2LjM5YTcsNywwLDAsMSwyLjU1LDcsNy40LDcuNCwwLDAsMS00LjYsNS4wNiw0LjA2LDQuMDYsMCwwLDEtLjQ3LDEuMzRBNS4wOSw1LjA5LDAsMCwxLDMwLDMxLjY2YTExLjYzLDExLjYzLDAsMCwwLDEsMy45MWgtMy43cy0wLjg1LTIuMzEtNi4zMi01LjdhNy44Nyw3Ljg3LDAsMCwxLTguMTctMi4yMyw5Ljc5LDkuNzksMCwwLDEtMS45LjQsOS45LDkuOSwwLDAsMS01LjIyLS44Nyw5LjQ3LDkuNDcsMCwwLDEtMy4wNy0yLjM4QTcuNDQsNy40NCwwLDAsMSwxLjYsMTgsNy4zOSw3LjM5LDAsMCwxLDYuNTQsMTMuNjhaIi8+PGxpbmUgY2xhc3M9ImNscy03IiB4MT0iNi41NiIgeTE9IjEzLjYxIiB4Mj0iMTAuNjUiIHkyPSIxNi41OSIvPjxsaW5lIGNsYXNzPSJjbHMtOCIgeDE9IjIxLjA2IiB5MT0iMjYuNzIiIHgyPSIyMS4wNiIgeTI9IjI5Ljk0Ii8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iOC44MSIgeTE9IjI0LjU4IiB4Mj0iOC44MSIgeTI9IjI4LjAzIi8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iMzUuOSIgeTE9IjE1LjM1IiB4Mj0iMjUuNjgiIHkyPSIxNS4zNSIvPjxsaW5lIGNsYXNzPSJjbHMtOCIgeDE9IjM4Ljg0IiB5MT0iMjAuMTgiIHgyPSIzMy4zNCIgeTI9IjIwLjQzIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSIxMS45NyAxNy42OCAyNS4wNiAxNS43MSAyMS4wNiAyNS44OCAzMi41IDIwLjE4IDI2LjAyIDE1Ljk5Ii8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSI5LjQ0IDI0LjQzIDIxLjIgMjYuMzEgMTIuNzkgMTcuNzYgOS4wMiAyMy44MyAyLjEzIDI0LjA1Ii8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtOSIgcG9pbnRzPSIxMy41NiAxNi4zNSAxOC42NSAxMS45MyAyNC40MyAxNC44MSIvPjxsaW5lIGNsYXNzPSJjbHMtNyIgeDE9IjE4LjQ1IiB5MT0iMTEuMTkiIHgyPSIxNi4yOSIgeTI9IjkuMjQiLz48bGluZSBjbGFzcz0iY2xzLTgiIHgxPSIyNS45MyIgeTE9IjE0LjY3IiB4Mj0iMzAuMjMiIHkyPSI5LjkxIi8+PGxpbmUgY2xhc3M9ImNscy04IiB4MT0iMjcuNTQiIHkxPSIyOS42NiIgeDI9IjI4LjgzIiB5Mj0iMzUuNTciLz48cG9seWxpbmUgY2xhc3M9ImNscy0xMCIgcG9pbnRzPSIyMS43NSAyNS41NCAzMi4yMSAyMCAyOC4wOCAyOS4zOCAyMS4wNiAyNS44OCIvPjxjaXJjbGUgY2xhc3M9ImNscy0xMSIgY3g9IjExLjk3IiBjeT0iMTcuNDciIHI9IjIuNTMiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTEiIGN4PSIyNS4yMyIgY3k9IjE2LjE0IiByPSIyLjUzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMzIuNSIgY3k9IjIwLjE4IiByPSIxLjgzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMjEuMiIgY3k9IjI1Ljg4IiByPSIxLjgzIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTExIiBjeD0iMjcuNzYiIGN5PSIyOC44MSIgcj0iMS44MyIvPjxjaXJjbGUgY2xhc3M9ImNscy0xMSIgY3g9IjE4LjY1IiBjeT0iMTEuOTMiIHI9IjEuNTYiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTEiIGN4PSI5LjI4IiBjeT0iMjQuMjkiIHI9IjEuODMiLz48L3N2Zz4=';

class neuralNetwork {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'neuralNetwork';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'neuralNetwork';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const neuralNetworkState = sourceTarget.getCustomState(neuralNetwork.STATE_KEY);
            if (neuralNetworkState) {
                newTarget.setCustomState(neuralNetwork.STATE_KEY, Clone.simple(neuralNetworkState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        // const commonExtensions = () => [];
        let boardCommonExtensionList = [];
        // let commonExtensionList = commonExtensions();
        // for (let commonExtensionIndex in commonExtensionList) {
        //     let commonExtension = commonExtensionList[commonExtensionIndex];
        //     if (commonExtension.allowedBoards.includes(board)) {
        //         boardCommonExtensionList.push(commonExtension);
        //     }
        // }
        return boardCommonExtensionList;
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'neuralNetwork',
            name: 'Neural Network',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    opcode: 'setPatternCount',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.setPatternCount',
                        default: 'set no. of patterns to [DATA]',
                        description: 'Define Number of patterns'
                    }),
                    arguments: {
                        DATA: {
                            type: ArgumentType.NUMBER,
                            menu: 'inputPattern',
                            defaultValue: '4'
                        }
                    }
                },
                {
                    opcode: 'setNodes',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.setNodes',
                        default: 'set nodes > input [INPUT] | hidden [HIDDEN] | output [OUTPUT]',
                        description: 'Define Number of Nodes'
                    }),
                    arguments: {
                        INPUT: {
                            type: ArgumentType.NUMBER,
                            menu: 'channel',
                            defaultValue: '2'
                        },
                        HIDDEN: {
                            type: ArgumentType.NUMBER,
                            menu: 'hiddenChannel',
                            defaultValue: '2'
                        },
                        OUTPUT: {
                            type: ArgumentType.NUMBER,
                            menu: 'channel',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'defineParameter',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.defineParameter',
                        default: 'set [PARAMETER] to [DATA]',
                        description: 'define parameter'
                    }),
                    arguments: {
                        PARAMETER: {
                            type: ArgumentType.NUMBER,
                            menu: 'parameter',
                            defaultValue: '4'
                        },
                        DATA: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '2000'
                        }
                    }
                },
                "---",
                {
                    opcode: 'setInputPattern',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.setInputPattern',
                        default: 'set input pattern [PATTERNID] to 1 > [DATA1] 2 > [DATA2] 3 > [DATA3] 4 > [DATA4]',
                        description: 'set input pattern'
                    }),
                    arguments: {
                        PATTERNID: {
                            type: ArgumentType.NUMBER,
                            menu: 'inputPattern',
                            defaultValue: '1'
                        },
                        DATA1: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA2: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA3: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA4: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'setTargetPattern',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.setTargetPattern',
                        default: 'set target pattern [PATTERNID] to 1 > [DATA1] 2 > [DATA2] 3 > [DATA3] 4 > [DATA4]',
                        description: 'set target pattern'
                    }),
                    arguments: {
                        PATTERNID: {
                            type: ArgumentType.NUMBER,
                            menu: 'inputPattern',
                            defaultValue: '1'
                        },
                        DATA1: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA2: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA3: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA4: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'startTraining',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.startTraining',
                        default: 'start training',
                        description: 'start training'
                    }),
                },
                "---",
                {
                    opcode: 'calculateOutput',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'neuralNetwork.calculateOutput',
                        default: 'calculate output for 1 > [DATA1] 2 > [DATA2] 3 > [DATA3] 4 > [DATA4]',
                        description: 'calculate output'
                    }),
                    arguments: {
                        DATA1: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA2: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA3: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        DATA4: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getOutput',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'neuralNetwork.getOutput',
                        default: 'get output [OUTPUT]',
                        description: 'calculate output'
                    }),
                    arguments: {
                        OUTPUT: {
                            type: ArgumentType.NUMBER,
                            menu: 'channel',
                            defaultValue: '1'
                        }
                    }
                },
            ],
            menus: {
                parameter: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.neuralNetwork.parameter.option1',
                            default: 'epoch',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.neuralNetwork.parameter.option2',
                            default: 'learning rate',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.neuralNetwork.parameter.option3',
                            default: 'momentum',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.neuralNetwork.parameter.option4',
                            default: 'max initial weight',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.neuralNetwork.parameter.option5',
                            default: 'success',
                            description: 'Menu'
                        }), value: '8'
                    },
                ],
                channel: ['1', '2', '3', '4'],
                hiddenChannel: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
                inputPattern: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20'],
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    setPatternCount(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setPatternCount(args, util, this);
        }
        return RealtimeMode.setPatternCount(args, util, this);
    }

    setNodes(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setNodes(args, util, this);
        }
        return RealtimeMode.setNodes(args, util, this);
    }

    defineParameter(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.defineParameter(args, util, this);
        }
        return RealtimeMode.defineParameter(args, util, this);
    }

    startTraining(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.startTraining(args, util, this);
        }
        return RealtimeMode.startTraining(args, util, this);
    }

    setInputPattern(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setInputPattern(args, util, this);
        }
        return RealtimeMode.setInputPattern(args, util, this);
    }

    setTargetPattern(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setTargetPattern(args, util, this);
        }
        return RealtimeMode.setTargetPattern(args, util, this);
    }

    calculateOutput(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.calculateOutput(args, util, this);
        }
        return RealtimeMode.calculateOutput(args, util, this);
    }

    getOutput(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getOutput(args, util, this);
        }
        return RealtimeMode.getOutput(args, util, this);
    }

}

module.exports = neuralNetwork;
