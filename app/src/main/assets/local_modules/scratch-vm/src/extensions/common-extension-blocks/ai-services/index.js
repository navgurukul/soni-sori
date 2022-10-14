const Runtime = require('../../../engine/runtime');

const formatMessage = require('format-message');

const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');

// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzQiIGRhdGEtbmFtZT0iTGF5ZXIgMzQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMSwuY2xzLTEwLC5jbHMtNywuY2xzLTgsLmNscy05e2ZpbGw6bm9uZTt9LmNscy0ye2NsaXAtcGF0aDp1cmwoI2NsaXAtcGF0aCk7fS5jbHMtM3tvcGFjaXR5OjAuNTt9LmNscy00e2NsaXAtcGF0aDp1cmwoI2NsaXAtcGF0aC0yKTt9LmNscy01e29wYWNpdHk6MC4wOTt9LmNscy02e2ZpbGw6I2YzZTAyYzt9LmNscy0xMCwuY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2U6I2M1NDM0MjtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9LmNscy03e3N0cm9rZS13aWR0aDoxLjA2cHg7fS5jbHMtOHtzdHJva2Utd2lkdGg6MC44NnB4O30uY2xzLTl7c3Ryb2tlLXdpZHRoOjAuOTRweDt9LmNscy0xMHtzdHJva2Utd2lkdGg6MC45OHB4O308L3N0eWxlPjxjbGlwUGF0aCBpZD0iY2xpcC1wYXRoIj48cmVjdCBpZD0iX1JlY3RhbmdsZV8iIGRhdGEtbmFtZT0iJmx0O1JlY3RhbmdsZSZndDsiIGNsYXNzPSJjbHMtMSIgeD0iLTI5NS42OSIgeT0iLTM5NC4zMSIgd2lkdGg9IjYwMCIgaGVpZ2h0PSIzNzIiLz48L2NsaXBQYXRoPjxjbGlwUGF0aCBpZD0iY2xpcC1wYXRoLTIiPjxyZWN0IGlkPSJfUmVjdGFuZ2xlXzIiIGRhdGEtbmFtZT0iJmx0O1JlY3RhbmdsZSZndDsiIGNsYXNzPSJjbHMtMSIgeD0iNDYuODEiIHk9Ii0yNDEuMDUiIHdpZHRoPSIyNjMuMzMiIGhlaWdodD0iMjE4Ii8+PC9jbGlwUGF0aD48L2RlZnM+PHRpdGxlPkljb25fQUktMDI8L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtNiIgZD0iTTkuNTcsMTQuNDNMNiwyMS4yNGExLjE4LDEuMTgsMCwwLDAsMSwxLjczbDEuMzgsMC4wOWExLjE4LDEuMTgsMCwwLDEsMS4xMSwxLjE5bDAsNS44OGExLjE4LDEuMTgsMCwwLDAsMS4xMiwxLjE5bDQuNjksMC4yNmExLjE4LDEuMTgsMCwwLDEsMS4xMiwxLjE4djQuNDdhMS4xOCwxLjE4LDAsMCwwLDEuMTgsMS4xOGg5Ljg3YTEuMTgsMS4xOCwwLDAsMCwxLjE4LTEuMjlMMjguMDcsMzEuOHMtMC41NC0yLjQ5LDEuMTktNi4yM2MyLTQuNDEsMy40Ni01LjUsNC05LjQzYTEzLjUxLDEzLjUxLDAsMCwwLS44MS02LjgzQTEyLjgsMTIuOCwwLDAsMCwyOS41OCw1YTExLjQ3LDExLjQ3LDAsMCwwLTUuMzYtMy4xNCwxMS4zMiwxMS4zMiwwLDAsMC03LjUzLjg3LDEyLjM5LDEyLjM5LDAsMCwwLTQuOTMsMy45LDExLjgyLDExLjgyLDAsMCwwLTIsNC4yNCw5LjQ4LDkuNDgsMCwwLDAtLjA5LDIuODNBMS4yLDEuMiwwLDAsMSw5LjU3LDE0LjQzWiIvPjxjaXJjbGUgY2xhc3M9ImNscy03IiBjeD0iMTUuOTkiIGN5PSIxMS44OSIgcj0iMi43MSIvPjxjaXJjbGUgY2xhc3M9ImNscy04IiBjeD0iMjYuNDciIGN5PSI3LjAzIiByPSIxLjUiLz48Y2lyY2xlIGNsYXNzPSJjbHMtOSIgY3g9IjIxLjg3IiBjeT0iMjEuMjciIHI9IjEuNTEiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTAiIGN4PSIxNi41MyIgY3k9IjI2LjQ5IiByPSIxLjg3Ii8+PGxpbmUgY2xhc3M9ImNscy03IiB4MT0iMjEuODciIHkxPSIyMi43OCIgeDI9IjIxLjg3IiB5Mj0iMzguNDEiLz48cGF0aCBjbGFzcz0iY2xzLTciIGQ9Ik0xOSwxMS44OWgxLjY4YTEuMTgsMS4xOCwwLDAsMSwxLjE4LDEuMThWMTkuNiIvPjxwYXRoIGNsYXNzPSJjbHMtNyIgZD0iTTI2LjQ3LDguNTNWMTVhMS4xOCwxLjE4LDAsMCwxLTEuMTgsMS4xOEgyMi4zNCIvPjxsaW5lIGNsYXNzPSJjbHMtNyIgeDE9IjkuNTIiIHkxPSIyNi40OSIgeDI9IjE0LjY2IiB5Mj0iMjYuNDkiLz48L3N2Zz4=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMzQiIGRhdGEtbmFtZT0iTGF5ZXIgMzQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMSwuY2xzLTEwLC5jbHMtNywuY2xzLTgsLmNscy05e2ZpbGw6bm9uZTt9LmNscy0ye2NsaXAtcGF0aDp1cmwoI2NsaXAtcGF0aCk7fS5jbHMtM3tvcGFjaXR5OjAuNTt9LmNscy00e2NsaXAtcGF0aDp1cmwoI2NsaXAtcGF0aC0yKTt9LmNscy01e29wYWNpdHk6MC4wOTt9LmNscy02e2ZpbGw6I2YzZTAyYzt9LmNscy0xMCwuY2xzLTcsLmNscy04LC5jbHMtOXtzdHJva2U6I2M1NDM0MjtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9LmNscy03e3N0cm9rZS13aWR0aDoxLjA2cHg7fS5jbHMtOHtzdHJva2Utd2lkdGg6MC44NnB4O30uY2xzLTl7c3Ryb2tlLXdpZHRoOjAuOTRweDt9LmNscy0xMHtzdHJva2Utd2lkdGg6MC45OHB4O308L3N0eWxlPjxjbGlwUGF0aCBpZD0iY2xpcC1wYXRoIj48cmVjdCBpZD0iX1JlY3RhbmdsZV8iIGRhdGEtbmFtZT0iJmx0O1JlY3RhbmdsZSZndDsiIGNsYXNzPSJjbHMtMSIgeD0iLTI5NS42OSIgeT0iLTM5NC4zMSIgd2lkdGg9IjYwMCIgaGVpZ2h0PSIzNzIiLz48L2NsaXBQYXRoPjxjbGlwUGF0aCBpZD0iY2xpcC1wYXRoLTIiPjxyZWN0IGlkPSJfUmVjdGFuZ2xlXzIiIGRhdGEtbmFtZT0iJmx0O1JlY3RhbmdsZSZndDsiIGNsYXNzPSJjbHMtMSIgeD0iNDYuODEiIHk9Ii0yNDEuMDUiIHdpZHRoPSIyNjMuMzMiIGhlaWdodD0iMjE4Ii8+PC9jbGlwUGF0aD48L2RlZnM+PHRpdGxlPkljb25fQUktMDI8L3RpdGxlPjxwYXRoIGNsYXNzPSJjbHMtNiIgZD0iTTkuNTcsMTQuNDNMNiwyMS4yNGExLjE4LDEuMTgsMCwwLDAsMSwxLjczbDEuMzgsMC4wOWExLjE4LDEuMTgsMCwwLDEsMS4xMSwxLjE5bDAsNS44OGExLjE4LDEuMTgsMCwwLDAsMS4xMiwxLjE5bDQuNjksMC4yNmExLjE4LDEuMTgsMCwwLDEsMS4xMiwxLjE4djQuNDdhMS4xOCwxLjE4LDAsMCwwLDEuMTgsMS4xOGg5Ljg3YTEuMTgsMS4xOCwwLDAsMCwxLjE4LTEuMjlMMjguMDcsMzEuOHMtMC41NC0yLjQ5LDEuMTktNi4yM2MyLTQuNDEsMy40Ni01LjUsNC05LjQzYTEzLjUxLDEzLjUxLDAsMCwwLS44MS02LjgzQTEyLjgsMTIuOCwwLDAsMCwyOS41OCw1YTExLjQ3LDExLjQ3LDAsMCwwLTUuMzYtMy4xNCwxMS4zMiwxMS4zMiwwLDAsMC03LjUzLjg3LDEyLjM5LDEyLjM5LDAsMCwwLTQuOTMsMy45LDExLjgyLDExLjgyLDAsMCwwLTIsNC4yNCw5LjQ4LDkuNDgsMCwwLDAtLjA5LDIuODNBMS4yLDEuMiwwLDAsMSw5LjU3LDE0LjQzWiIvPjxjaXJjbGUgY2xhc3M9ImNscy03IiBjeD0iMTUuOTkiIGN5PSIxMS44OSIgcj0iMi43MSIvPjxjaXJjbGUgY2xhc3M9ImNscy04IiBjeD0iMjYuNDciIGN5PSI3LjAzIiByPSIxLjUiLz48Y2lyY2xlIGNsYXNzPSJjbHMtOSIgY3g9IjIxLjg3IiBjeT0iMjEuMjciIHI9IjEuNTEiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMTAiIGN4PSIxNi41MyIgY3k9IjI2LjQ5IiByPSIxLjg3Ii8+PGxpbmUgY2xhc3M9ImNscy03IiB4MT0iMjEuODciIHkxPSIyMi43OCIgeDI9IjIxLjg3IiB5Mj0iMzguNDEiLz48cGF0aCBjbGFzcz0iY2xzLTciIGQ9Ik0xOSwxMS44OWgxLjY4YTEuMTgsMS4xOCwwLDAsMSwxLjE4LDEuMThWMTkuNiIvPjxwYXRoIGNsYXNzPSJjbHMtNyIgZD0iTTI2LjQ3LDguNTNWMTVhMS4xOCwxLjE4LDAsMCwxLTEuMTgsMS4xOEgyMi4zNCIvPjxsaW5lIGNsYXNzPSJjbHMtNyIgeDE9IjkuNTIiIHkxPSIyNi40OSIgeDI9IjE0LjY2IiB5Mj0iMjYuNDkiLz48L3N2Zz4=';

const axios = require('axios');

// const AI_SERVER_ENDPOINT = "https://us-central1-pictobloxdev.cloudfunctions.net/aiServer";
const AI_SERVER_ENDPOINT = "https://asia-east2-pictobloxdev.cloudfunctions.net/aiServerAsiaEast2";

const SPEECH_END_POINT = AI_SERVER_ENDPOINT + "/speech-analyze";
const VISION_END_POINT = AI_SERVER_ENDPOINT + "/vision-analyze";
const VISION_URL_END_POINT = AI_SERVER_ENDPOINT + "/vision-url-analyze";
const TEXT_RECOGNITION_ANALYZE = AI_SERVER_ENDPOINT + "/text-recognition-analyze";
const OCR_ANALYZE = AI_SERVER_ENDPOINT + "/ocr-analyze";
const FACE_ANALYZE = AI_SERVER_ENDPOINT + "/face-analyze";
const CHECK_HANDWRITTEN_TEXT = AI_SERVER_ENDPOINT + '/check-handwritten-text-status';

/**
 * Class for the AI related blocks in Scratch 3.0
 * @param {Runtime} runtime - the runtime instantiating this block package.
 * @constructor
 */

class AIServicesBlocks {
    constructor(runtime) {
        /**
         * The runtime instantiating this block package.
         * @type {Runtime}
         */
        this.runtime = runtime;
        this.runtime.checkInternetConnection();

        /**
         * Last recognized speech
         * @type {String}
         * @private
         */
        this._currentRecognizedSpeech = '';

        /**
         * Last Image analyse result
         * @type {String}
         * @private
         */
        this.imageAnalyseResult = '';
        this.printedTextResult = '';
        this.faceAnalyseResult = '';
        this.handwrittenTextRecognized = [];

        this.extensionName = 'Artificial Intelligence';
    }

    static get STATE_KEY() {
        return 'aiServices';
    }

    /**
     * @returns {object} metadata for this extension and its blocks.
     */
    getInfo() {

        return {
            id: 'aiServices',
            name: formatMessage({
                id: 'aiServices.categoryName',
                default: this.extensionName,
                description: 'AI Services using Google Cloud Platform'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    message: 'Recognition'
                },
                {
                    opcode: 'speech2Text',
                    text: formatMessage({
                        id: 'aiServices.speakAndWaitBlock',
                        default: 'recognize speech for [TIME] s in [LANGUAGE]',
                        description: 'recognize speech from mic'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        TIME: {
                            type: ArgumentType.NUMBER,
                            menu: 'countDown',
                            defaultValue: 2
                        },
                        LANGUAGE: {
                            type: ArgumentType.STRING,
                            menu: 'languages',
                            defaultValue: 'en-US'
                        }
                    },
                },
                "---",
                {
                    opcode: 'analyseImageContentFromCamera',
                    text: formatMessage({
                        id: 'aiServices.analyseImageContentFromCamera',
                        default: 'recognize [OPTION] in image after [TIME] seconds',
                        description: 'analyse image content from web camera'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'recognizeOptionsMenu',
                            defaultValue: 'imageFeatures'
                        },
                        TIME: {
                            type: ArgumentType.NUMBER,
                            menu: 'countDown',
                            defaultValue: '2'
                        }
                    }
                },
                {
                    opcode: 'analyseImageContentFromApplication',
                    text: formatMessage({
                        id: 'aiServices.analyseImageContentFromApplication',
                        default: 'recognize [OPTION] in image from [IMAGE]',
                        description: 'analyse image content from local image'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'recognizeOptionsMenu',
                            defaultValue: 'imageFeatures'
                        },
                        IMAGE: {
                            type: ArgumentType.STRING,
                            menu: 'pictoOptions',
                            defaultValue: 'stage'
                        }
                    }
                },
                {
                    opcode: 'analyseImageContentFromURL',
                    text: formatMessage({
                        id: 'aiServices.analyseImageContentFromURL',
                        default: 'recognize [OPTION] in image from URL [URL]',
                        description: 'analyse image content from URL'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'recognizeOptionsMenu',
                            defaultValue: 'imageFeatures'
                        },
                        URL: {
                            type: ArgumentType.STRING,
                        }
                    }
                },
                "---",
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'aiServices.toggleStageVideoFeed',
                        default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] % transparency',
                        description: 'toggle video feed on stage'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        VIDEO_STATE: {
                            type: ArgumentType.STRING,
                            menu: 'videoState',
                            defaultValue: 'on'
                        },
                        TRANSPARENCY: {
                            type: ArgumentType.MATHSLIDER100,
                            default: 0
                        }
                    }
                },
                {
                    message: 'Speech Recognition'
                },
                {
                    opcode: 'speechResult',
                    text: formatMessage({
                        id: 'aiServices.speechResult',
                        default: 'speech recognition result',
                        description: 'Show Result.'
                    }),
                    blockType: BlockType.REPORTER
                },
                {
                    message: 'Image Features'
                },
                {
                    opcode: 'imageFeatureCount',
                    text: formatMessage({
                        id: 'aiServices.imageFeatureCount',
                        default: 'recognized [OPTION] count',
                        description: 'Show feature count.'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'imageFeatureFirst',
                            defaultValue: 'objects'
                        }
                    }
                },
                {
                    opcode: 'imageResultFirst',
                    text: formatMessage({
                        id: 'aiServices.imageResultFirst',
                        default: 'recognized [OPTION] [INDEX] name',
                        description: 'Show Result.'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'imageFeatureFirst',
                            defaultValue: 'objects'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: 1
                        }
                    }
                },
                {
                    opcode: 'imageResultFirstInfo',
                    text: formatMessage({
                        id: 'aiServices.imageResultFirstInfo',
                        default: 'recognized [OPTION] [INDEX] [INFO]',
                        description: 'Show Result.'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'imageFeatureFirstAnother',
                            defaultValue: 'objects'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: 1
                        },
                        INFO: {
                            type: ArgumentType.STRING,
                            menu: 'imageFeatureFirstInfo',
                            defaultValue: 'xPos'
                        }
                    }
                },
                {
                    opcode: 'imageResultSecond',
                    text: formatMessage({
                        id: 'aiServices.imageResultSecond',
                        default: '[OPTION] recognition result',
                        description: 'Show Result.'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'imageFeatureSecond',
                            defaultValue: 'landmark'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'aiServices.blockSeparatorMessage4',
                        default: 'OCR Recognition',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'handwrittenTextResult',
                    text: formatMessage({
                        id: 'aiServices.handwrittenTextResult',
                        default: 'handwritten text result',
                        description: 'Show handwritten text Result.'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'printedTextRecognize',
                    text: formatMessage({
                        id: 'aiServices.printedTextRecognize',
                        default: 'printed text result',
                        description: 'Show Result.'
                    }),
                    blockType: BlockType.REPORTER
                },
                {
                    message: 'Face Features'
                },
                {
                    opcode: 'faceCount',
                    text: formatMessage({
                        id: 'aiServices.faceCount',
                        default: 'recognized face count',
                        description: 'Number of face found'
                    }),
                    blockType: BlockType.REPORTER
                },
                {
                    opcode: 'faceFeaturesResult',
                    text: formatMessage({
                        id: 'aiServices.faceFeaturesResult',
                        default: 'recognized [OPTION] of face [INDEX]',
                        description: 'Result of age and gender recognition'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'ageGenderOption',
                            defaultValue: 'age'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: 1
                        }
                    }
                },
                {
                    opcode: 'emotionIs',
                    text: formatMessage({
                        id: 'aiServices.emotionIs',
                        default: 'emotion [EMOTION] for face [INDEX]?',
                        description: 'Emotion Score'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        EMOTION: {
                            type: ArgumentType.STRING,
                            menu: 'emotions',
                            defaultValue: 'happiness'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'headPosition',
                    text: formatMessage({
                        id: 'aiServices.headPosition',
                        default: 'head gesture [GESTURE] angle for face [INDEX]',
                        description: 'Head Gesture'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        GESTURE: {
                            type: ArgumentType.STRING,
                            menu: 'headPosition',
                            defaultValue: 'roll'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'headLocation',
                    text: formatMessage({
                        id: 'aiServices.headLocation',
                        default: 'recognized [OPTION] for face [INDEX]',
                        description: 'Head location in image'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'headLocationInfo',
                            defaultValue: 'xPos'
                        },
                        INDEX: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1'
                        }
                    }
                }

            ],
            menus: {
                countDown: {
                    acceptReporters: true,
                    items: ['2', '5', '10', '15', '30']
                },
                pictoOptions: [
                    { text: 'stage', value: 'stage', },
                    { text: 'costume', value: 'costume', },
                    { text: 'backdrop', value: 'backdrop' }
                ],
                recognizeOptionsMenu: [
                    { text: 'image features', value: 'imageFeatures' },
                    { text: 'face details', value: 'faceDetails' },
                    { text: 'handwritten text', value: 'handWrittenText' },
                    { text: 'printed text', value: 'printedText' },
                ],
                imageFeatureFirst: [
                    { text: 'celebrity', value: 'celebrity' },
                    { text: 'brand', value: 'brand' },
                    { text: 'object', value: 'objects' },
                    { text: 'image tag', value: 'imageRecognition' }
                ],
                imageFeatureFirstAnother: [
                    { text: 'celebrity', value: 'celebrity' },
                    { text: 'brand', value: 'brand' },
                    { text: 'object', value: 'objects' }
                ],
                imageFeatureFirstInfo: [
                    { text: 'x position', value: 'xPos' },
                    { text: 'y position', value: 'yPos' },
                    { text: 'width', value: 'width' },
                    { text: 'height', value: 'height' },
                    { text: 'confidence', value: 'confidence' },
                ],
                imageFeatureSecond: [
                    { text: 'landmark', value: 'landmark' },
                    { text: 'image description', value: 'imageDescription' }
                ],
                ageGenderOption: [
                    { text: 'age', value: 'age' },
                    { text: 'gender', value: 'gender' },
                    { text: 'emotion', value: 'emotion' }
                ],
                emotions: [
                    { text: 'happiness', value: 'happiness' },
                    { text: 'anger', value: 'anger' },
                    { text: 'contempt', value: 'contempt' },
                    { text: 'disgust', value: 'disgust' },
                    { text: 'fear', value: 'fear' },
                    { text: 'neutral', value: 'neutral' },
                    { text: 'sadness', value: 'sadness' },
                    { text: 'surprise', value: 'surprise' }
                ],
                headPosition: [
                    { text: 'roll', value: 'roll' },
                    { text: 'yaw', value: 'yaw' },
                    { text: 'pitch', value: 'pitch' }
                ],
                headLocationInfo: [
                    { text: 'x position', value: 'xPos' },
                    { text: 'y position', value: 'yPos' },
                    { text: 'width', value: 'width' },
                    { text: 'height', value: 'height' },

                ],
                videoState: [
                    { text: 'off', value: 'off' },
                    { text: 'on', value: 'on' },
                    { text: 'on flipped', value: 'onFlipped' }
                ],
                languages: [
                    { text: 'Arabic', value: 'ar-AE' },
                    { text: 'Catalan', value: 'ca-ES	' },
                    { text: 'Danish', value: 'da-DK' },
                    { text: 'German', value: 'da-de' },
                    { text: 'English (United Kingdom)', value: 'en-GB' },
                    { text: 'English (United States)', value: 'en-US' },
                    { text: 'Spanish', value: 'es-ES' },
                    { text: 'Finnish', value: 'fi-FI' },
                    { text: 'French', value: 'fr-FR' },
                    { text: 'Gujarati', value: 'gu-IN' },
                    { text: 'Hindi', value: 'hi-IN' },
                    { text: 'Italian', value: 'it-IT' },
                    { text: 'Japanese', value: 'ja-JP' },
                    { text: 'Korean', value: 'ko-KR' },
                    { text: 'Marathi', value: 'mr-IN	' },
                    { text: 'Norwegian', value: 'nb-NO' },
                    { text: 'Dutch', value: 'nl-NL' },
                    { text: 'Polish', value: 'pl-PL' },
                    { text: 'Portuguese', value: 'pt-PT' },
                    { text: 'Russian', value: 'ru-RU' },
                    { text: 'Swedish', value: 'sv-SE' },
                    { text: 'Tamil', value: 'ta-IN' },
                    { text: 'Telugu', value: 'te-IN' },
                    { text: 'Thai', value: 'th-TH' },
                    { text: 'Turkish', value: 'tr-TR' },
                    { text: 'Chinese (Mandarin)', value: 'zh-CN' },
                    { text: 'Chinese (Traditional)', value: 'zh-HK' }
                ]
            },
        }
    }

    b64toBlob(rawData, sliceSize) {
        var block = rawData.split(";");
        var contentType = block[0].split(":")[1];
        var b64Data = block[1].split(",")[1];

        contentType = contentType || '';
        sliceSize = sliceSize || 512;

        var byteCharacters = atob(b64Data);
        var byteArrays = [];

        for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
            var slice = byteCharacters.slice(offset, offset + sliceSize);

            var byteNumbers = new Array(slice.length);
            for (var i = 0; i < slice.length; i++) {
                byteNumbers[i] = slice.charCodeAt(i);
            }

            var byteArray = new Uint8Array(byteNumbers);

            byteArrays.push(byteArray);
        }

        var blob = new Blob(byteArrays, { type: contentType });
        return blob;
    }

    makeHttpRequest(url, body, params, api_key, contentType = 'application/octet-stream') {
        return axios({
            method: 'post',
            url: url,
            params: params,
            headers: {
                'ocp-apim-subscription-key': api_key,
                'content-type': contentType
            },
            data: body
        }).then(resp => {
            return resp;
        });
    }

    handleHttpError() {
        this.runtime.emit('REQUEST_TIMED_OUT');
    }

    checkHandwrittenTextStatus(url) {
        return axios({
            method: 'post',
            url: CHECK_HANDWRITTEN_TEXT,
            headers: {
                'content-type': 'application/json'
            },
            data: { url: url }
        }).then(resp => {
            return resp;
        })
    }

    toggleStageVideoFeed(args, util) {
        const state = args.VIDEO_STATE;
        this.globalVideoState = args.VIDEO_STATE;

        this.runtime.ioDevices.video.setPreviewGhost(args.TRANSPARENCY);

        if (state === 'off') {
            this.runtime.ioDevices.video.disableVideo();
        } else {
            this.runtime.ioDevices.video.enableVideo();
            // Mirror if state is ON. Do not mirror if state is ON_FLIPPED.
            this.runtime.ioDevices.video.mirror = state === 'on';
        }
    }

    storeImageSize(base64) {
        let self = this;
        let img = new Image();
        img.onload = function () {
            self.faceImageSize = {
                width: img.width,
                height: img.height
            }
        };
        img.src = base64;
    }


    /**
     * Convert the Speech into text.
     * @param  {object} args Block arguments
     * @param {object} util Utility object provided by the runtime.
     * @return {Promise} A promise that resolves after playing the sound
     */

    speech2Text(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        util.runtime.emit('CAPTURE_AUDIO', args.TIME > 0 ? (args.TIME < 60 ? args.TIME : 60) : 0);
        return new Promise(resolve => {
            setTimeout(() => {
                if (util.runtime.aiBlockDataFromGUI !== null) {
                    let params = {
                        language: args.LANGUAGE
                    };
                    this.runtime.doAIServerCall({ url: SPEECH_END_POINT, data: this.b64toBlob(util.runtime.aiBlockDataFromGUI), params: params, contentType: "application/octet-stream" }).then(resp => {
                        this._currentRecognizedSpeech = resp.data.DisplayText;
                    }).catch(err => {
                        console.log(err.response);
                        this._currentRecognizedSpeech = "NULL";
                        this.handleHttpError();
                    }).finally(() => {
                        util.runtime.aiBlockDataFromGUI = null;
                        resolve();
                    });
                } else {
                    resolve();
                }
            }, args.TIME * 1000 + 500)
        }).catch(function (err) {
        });
    }

    /**
     * Last recognized speech result.
     * @param  {object} args Block arguments
     * @param {object} util Utility object provided by the runtime.
     * @return {Promise} A promise that resolves after playing the sound
     */
    speechResult(args, util) {
        return this._currentRecognizedSpeech ? this._currentRecognizedSpeech : 'NULL';
    }


    //Image Analysis Blocks
    analyseImageContentFromCamera(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        switch (args.OPTION) {
            case 'imageFeatures':
                util.runtime.emit('CAPTURE_IMAGE', args.TIME);
                return new Promise(resolve => {
                    setTimeout(() => {
                        if (util.runtime.aiBlockDataFromGUI !== null) {
                            let img = this.b64toBlob(util.runtime.aiBlockDataFromGUI);
                            let params = {
                                visualFeatures: ['Brands', 'Description', 'Objects'] + '',
                                details: ['Celebrities', 'Landmarks'] + ''
                            }

                            this.runtime.doAIServerCall({ url: VISION_END_POINT, data: img, params: params, contentType: "application/octet-stream" }).then(resp => {
                                this.imageAnalyseResult = resp.data;
                            }).catch(err => {
                                this.handleHttpError();
                            }).finally(() => {
                                util.runtime.aiBlockDataFromGUI = null;
                                resolve();
                            });
                        } else {
                            resolve();
                        }
                    }, args.TIME * 1000 + 500);
                });
                break;
            case 'handWrittenText':
                util.runtime.emit('CAPTURE_IMAGE', args.TIME);
                return new Promise(resolve => {
                    setTimeout(() => {
                        if (util.runtime.aiBlockDataFromGUI !== null) {
                            this.handwrittenTextRecognized = [];
                            let img = this.b64toBlob(util.runtime.aiBlockDataFromGUI);
                            let params = {
                                mode: "Handwritten"
                            }

                            this.runtime.doAIServerCall({ url: TEXT_RECOGNITION_ANALYZE, data: img, params: params, contentType: "application/octet-stream" }).then(resp => {
                                var stopInterval = false;
                                var timer = setInterval(() => {
                                    this.handwrittenTextRecognized = [];
                                    this.checkHandwrittenTextStatus(resp.data).then(response => {
                                        if (response.data != "") {
                                            let temp = [];
                                            clearInterval(timer);
                                            for (line in response.data.lines) {
                                                temp.push(response.data.lines[line].text);
                                            }
                                            this.handwrittenTextRecognized = temp;
                                        }
                                    }).catch((e) => {
                                    }).finally(() => {
                                        util.runtime.aiBlockDataFromGUI = null;
                                        resolve();
                                    });
                                }, 1000);
                            }).catch(err => {
                                this.handleHttpError();
                                resolve()
                            });
                        } else {
                            resolve();
                        }
                    }, args.TIME * 1000 + 500);
                });
                break;
            case 'printedText':
                util.runtime.emit('CAPTURE_IMAGE', args.TIME);
                return new Promise(resolve => {
                    setTimeout(() => {
                        if (util.runtime.aiBlockDataFromGUI !== null) {
                            let img = this.b64toBlob(util.runtime.aiBlockDataFromGUI);
                            let params = {};
                            this.runtime.doAIServerCall({ url: OCR_ANALYZE, data: img, params: params, contentType: "application/octet-stream" }).then(resp => {
                                this.printedTextResult = resp.data;
                            }).catch(err => {
                                this.handleHttpError();
                            }).finally(() => {
                                util.runtime.aiBlockDataFromGUI = null;
                                resolve();
                            });
                        } else {
                            resolve();
                        }
                    }, args.TIME * 1000 + 500);
                })
                break;
            case 'faceDetails':
                util.runtime.emit('CAPTURE_IMAGE', args.TIME);
                return new Promise(resolve => {
                    setTimeout(() => {
                        if (util.runtime.aiBlockDataFromGUI !== null) {
                            let img = this.b64toBlob(util.runtime.aiBlockDataFromGUI);
                            this.storeImageSize(util.runtime.aiBlockDataFromGUI);
                            let params = {
                                recognitionModel: 'recognition_02',
                                returnFaceId: false,
                                returnFaceLandmarks: false,
                                returnRecognitionModel: false,
                                returnFaceAttributes: ['age', 'gender', 'headPose', 'smile', 'emotion'] + ''
                            }

                            this.runtime.doAIServerCall({ url: FACE_ANALYZE, data: img, params: params, contentType: "application/octet-stream" }).then(resp => {
                                this.faceAnalyseResult = resp.data;
                            }).catch(err => {
                                this.handleHttpError();
                            }).finally(() => {
                                util.runtime.aiBlockDataFromGUI = null;
                                resolve();
                            });
                        } else {
                            resolve();
                        }
                    }, args.TIME * 1000 + 500);
                });
                break;
            default:
                break;
        }
    }

    analyseImageContentFromURL(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (args.URL === "")
            return;

        switch (args.OPTION) {
            case 'imageFeatures':
                return new Promise(resolve => {
                    let params = {
                        visualFeatures: ['Brands', 'Description', 'Objects'] + '',
                        details: ['Celebrities', 'Landmarks'] + ''
                    }

                    this.runtime.doAIServerCall({ url: VISION_END_POINT, data: { "url": args.URL }, params: params, contentType: "application/json" }).then(resp => {
                        this.imageAnalyseResult = resp.data;
                    }).catch(err => {
                        this.handleHttpError();
                    }).finally(() => {
                        resolve();
                    });
                });
                break;
            case 'handWrittenText':
                return new Promise(resolve => {
                    this.handwrittenTextRecognized = [];
                    let params = {
                        mode: "Handwritten"
                    }
                    this.runtime.doAIServerCall({ url: TEXT_RECOGNITION_ANALYZE, data: { "url": args.URL }, params: params, contentType: "application/json" }).then(resp => {
                        var stopInterval = false;
                        var timer = setInterval(() => {
                            this.handwrittenTextRecognized = [];
                            this.checkHandwrittenTextStatus(resp.data).then(response => {
                                if (response.data != "") {
                                    let temp = [];
                                    clearInterval(timer);
                                    for (line in response.data.lines) {
                                        temp.push(response.data.lines[line].text);
                                    }
                                    this.handwrittenTextRecognized = temp;
                                }
                            }).catch((e) => {
                            }).finally(() => {
                                util.runtime.aiBlockDataFromGUI = null;
                                resolve();
                            });;
                            if (stopInterval) {
                                clearInterval(timer);
                            }
                        }, 1000);
                    }).catch(err => {
                        this.handleHttpError();
                        resolve();
                    }).finally(() => {
                    });
                });
                break;
            case 'printedText':
                return new Promise(resolve => {
                    let params = {};
                    this.runtime.doAIServerCall({ url: OCR_ANALYZE, data: { "url": args.URL }, params: params, contentType: "application/json" }).then(resp => {
                        this.printedTextResult = resp.data;
                    }).catch(err => {
                        this.handleHttpError();
                    }).finally(() => {
                        resolve();
                    });
                })
                break;
            case 'faceDetails':
                return new Promise(resolve => {
                    let params = {
                        recognitionModel: 'recognition_02',
                        returnFaceId: false,
                        returnFaceLandmarks: false,
                        returnRecognitionModel: false,
                        returnFaceAttributes: ['age', 'gender', 'headPose', 'smile', 'emotion'] + ''
                    }
                    this.runtime.doAIServerCall({ url: FACE_ANALYZE, data: { "url": args.URL }, params: params, contentType: "application/json" }).then(resp => {
                        this.faceAnalyseResult = resp.data;
                    }).catch(err => {
                        this.handleHttpError();
                    }).finally(() => {
                        resolve();
                    });
                });
                break;
            default:
                break;
        }
    }

    makeImageRequestForApplication(args, util, img, resolve) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        let url = null;
        let params = null;
        let imgBlob = null;
        switch (args.OPTION) {
            case 'imageFeatures':
                imgBlob = this.b64toBlob(img);
                params = {
                    visualFeatures: ['Brands', 'Description', 'Objects'] + '',
                    details: ['Celebrities', 'Landmarks'] + ''
                }
                this.runtime.doAIServerCall({ url: VISION_END_POINT, data: imgBlob, params: params, contentType: "application/octet-stream" }).then(resp => {
                    this.imageAnalyseResult = resp.data;
                }).catch(err => {
                    this.handleHttpError();
                }).finally(() => {
                    resolve();
                });
                break;
            case 'handWrittenText':
                this.handwrittenTextRecognized = [];
                imgBlob = this.b64toBlob(img);
                params = {
                    mode: "Handwritten"
                }

                this.runtime.doAIServerCall({ url: TEXT_RECOGNITION_ANALYZE, data: imgBlob, params: params, contentType: "application/octet-stream" }).then(resp => {
                    var stopInterval = false;
                    var timer = setInterval(() => {
                        this.handwrittenTextRecognized = [];
                        this.checkHandwrittenTextStatus(resp.data).then(response => {
                            if (response.data != "") {
                                let temp = [];
                                clearInterval(timer);
                                for (line in response.data.lines) {
                                    temp.push(response.data.lines[line].text);
                                }
                                this.handwrittenTextRecognized = temp;
                            }
                        }).catch((e) => {
                        }).finally(() => {
                            util.runtime.aiBlockDataFromGUI = null;
                            resolve();
                        });;
                        if (stopInterval) {
                            clearInterval(timer);
                        }
                    }, 1000);
                }).catch(err => {
                    resolve();
                    this.handleHttpError();
                }).finally(() => {
                });
                break;
            case 'printedText':
                imgBlob = this.b64toBlob(img);

                this.runtime.doAIServerCall({ url: OCR_ANALYZE, data: imgBlob, params: params, contentType: "application/octet-stream" }).then(resp => {
                    this.printedTextResult = resp.data;
                }).catch(err => {
                    this.handleHttpError();
                }).finally(() => {
                    util.runtime.aiBlockDataFromGUI = null;
                    resolve();
                });
                break;
            case 'faceDetails':
                this.storeImageSize(img);
                imgBlob = this.b64toBlob(img);
                params = {
                    recognitionModel: 'recognition_02',
                    returnFaceId: false,
                    returnFaceLandmarks: false,
                    returnRecognitionModel: false,
                    returnFaceAttributes: ['age', 'gender', 'headPose', 'smile', 'emotion'] + ''
                }
                this.runtime.doAIServerCall({ url: FACE_ANALYZE, data: imgBlob, params: params, contentType: "application/octet-stream" }).then(resp => {
                    this.faceAnalyseResult = resp.data;
                }).catch(err => {
                    this.handleHttpError();
                }).finally(() => {
                    util.runtime.aiBlockDataFromGUI = null;
                    resolve();
                });
                break;
            default:
                break;
        }
    }

    svgString2Image(svgString, format, callback) {
        format = format ? format : 'png';
        var canvas = document.createElement('canvas');

        var image = new Image();

        image.onload = function () {
            canvas.width = image.width > 50 ? image.width : 50;
            canvas.height = image.height > 50 ? image.height : 50;
            let context = canvas.getContext('2d');
            context.drawImage(image, 0, 0, image.width > 50 ? image.width : 50, image.height > 50 ? image.height : 50);
            var pngData = canvas.toDataURL('image/' + format);
            callback(pngData);
        };

        image.src = svgString;
    }

    analyseImageContentFromApplication(args, util) {
        return new Promise(resolve => {
            var img = '';
            let costume = '';
            let data = '';
            switch (args.IMAGE) {
                case 'stage':
                    this.runtime.renderer.requestSnapshot(data => {
                        this.makeImageRequestForApplication(args, util, data, resolve);
                    })
                    break;
                case 'costume':
                    costume = util.target.getCurrentCostume();
                    data = costume.asset.data.reduce((data, byte) => {
                        return data + String.fromCharCode(byte);
                    }, '');
                    img = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);
                    if (costume.asset.assetType.contentType === "image/png") {
                        this.makeImageRequestForApplication(args, util, img, resolve);
                    } else {
                        this.svgString2Image(img, 'png', (img) => {
                            this.makeImageRequestForApplication(args, util, img, resolve);
                        })
                    }
                    break;
                case 'backdrop':
                    costume = util.runtime.getTargetForStage().getCurrentCostume();
                    data = costume.asset.data.reduce((data, byte) => {
                        return data + String.fromCharCode(byte);
                    }, '');
                    img = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                    if (costume.asset.assetType.contentType === "image/png") {
                        this.makeImageRequestForApplication(args, util, img, resolve);
                    } else {
                        this.svgString2Image(img, 'png', (img) => {
                            //if backdrop is plain 50*50 image (which what comes by default if no backdrop is added)
                            if (img == 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAkElEQVRoQ+2SwQkAMAyEkv2X7g6CUIL9nxDtzpG3R+6YDvmtZEUqIhnoa0liMbYiWJ00rIgkFmMrgtVJw4pIYjG2IlidNKyIJBZjK4LVScOKSGIxtiJYnTSsiCQWYyuC1UnDikhiMbYiWJ00rIgkFmMrgtVJw4pIYjG2IlidNKyIJBZjK4LVScOKSGIx9kyRBxCRADOd5J92AAAAAElFTkSuQmCC')
                                resolve();
                            this.makeImageRequestForApplication(args, util, img, resolve);
                        })
                    }
                    break;
                default:
                    break;
            }
        });

    }


    //Image Content Result Block
    imageFeatureCount(args, util) {
        let count = 0;
        switch (args.OPTION) {
            case 'brand':
                try {
                    count = this.imageAnalyseResult.brands.length;
                    break;
                } catch (e) {
                    return 0;
                }
            case 'celebrity':
                try {
                    let celebArray = {};
                    for (category in this.imageAnalyseResult.categories) {
                        if (this.imageAnalyseResult.categories[category].detail &&
                            this.imageAnalyseResult.categories[category].detail.celebrities) {
                            for (celeb in this.imageAnalyseResult.categories[category].detail.celebrities) {
                                celebArray[this.imageAnalyseResult.categories[category].detail.celebrities[celeb].name] = this.imageAnalyseResult.categories[category].detail.celebrities[celeb];
                            }
                        }
                    }
                    count = Object.keys(celebArray).length
                    break;
                } catch (e) {
                    return 0;
                }
            case 'objects':
                try {
                    count = this.imageAnalyseResult.objects.length;
                    break;
                } catch (e) {
                    return 0;
                }
            case 'imageRecognition':
                try {
                    return this.imageAnalyseResult.categories.length;
                } catch (e) {
                    return 'NULL';
                }
                break;
            default:
                break;
        }

        return count;
    }

    imageResultFirst(args, util) {
        switch (args.OPTION) {
            case 'celebrity':
                try {
                    let celebArray = {};
                    for (category in this.imageAnalyseResult.categories) {
                        if (this.imageAnalyseResult.categories[category].detail &&
                            this.imageAnalyseResult.categories[category].detail.celebrities) {
                            for (celeb in this.imageAnalyseResult.categories[category].detail.celebrities) {
                                celebArray[this.imageAnalyseResult.categories[category].detail.celebrities[celeb].name] = this.imageAnalyseResult.categories[category].detail.celebrities[celeb];
                            }
                        }
                    }
                    let celebData = []
                    for (celeb in celebArray) {
                        celebData.push(celeb)
                    }
                    return args.INDEX <= celebData.length && args.INDEX > 0 ? celebData[args.INDEX - 1] : 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'brand':
                try {
                    return (args.INDEX) <= this.imageAnalyseResult.brands.length && args.INDEX > 0
                        ? this.imageAnalyseResult.brands[args.INDEX - 1].name : 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'objects':
                let dataBuffer = [];
                try {
                    for (object in this.imageAnalyseResult.objects) {
                        dataBuffer.push(this.imageAnalyseResult.objects[object].object);
                    }
                    return args.INDEX <= dataBuffer.length && args.INDEX > 0 ? dataBuffer[args.INDEX - 1] : 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'imageRecognition':
                try {
                    return args.INDEX <= this.imageAnalyseResult.categories.length && args.INDEX > 0 ?
                        this.imageAnalyseResult.categories[args.INDEX - 1].name : 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            default:
                break;
        }
        return result === '' ? 'NULL' : result;
    }

    imageResultFirstInfo(args, util) {
        let dataObject = [];
        switch (args.OPTION) {
            case 'celebrity':
                try {
                    let celebArray = {};
                    for (category in this.imageAnalyseResult.categories) {
                        if (this.imageAnalyseResult.categories[category].detail &&
                            this.imageAnalyseResult.categories[category].detail.celebrities) {
                            for (celeb in this.imageAnalyseResult.categories[category].detail.celebrities) {
                                celebArray[this.imageAnalyseResult.categories[category].detail.celebrities[celeb].name] = this.imageAnalyseResult.categories[category].detail.celebrities[celeb];
                            }
                        }
                    }
                    for (celeb in celebArray) {
                        dataObject.push(celebArray[celeb])
                    }
                    if (!(args.INDEX <= dataObject.length && args.INDEX > 0))
                        return 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'brand':
                try {
                    dataObject = this.imageAnalyseResult.brands;
                    if (!(args.INDEX <= dataObject.length && args.INDEX > 0))
                        return 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'objects':
                try {
                    for (object in this.imageAnalyseResult.objects) {
                        dataObject.push(this.imageAnalyseResult.objects[object]);
                    }
                    if (!(args.INDEX <= dataObject.length && args.INDEX > 0))
                        return 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            default:
                break;
        }
        let target = dataObject[args.INDEX - 1];
        let { position, terms } = target.rectangle ? {
            position: target.rectangle,
            terms: {
                height: 'h',
                width: 'w',
                left: 'x',
                top: 'y'
            }
        } : {
                position: target.faceRectangle,
                terms: {
                    height: 'height',
                    width: 'width',
                    left: 'left',
                    top: 'top'
                }
            };
        switch (args.INFO) {
            case 'xPos':
                return (((position[terms.left] + position[terms.width] / 2) * 480) / this.imageAnalyseResult.metadata.width) - 240;
                break;
            case 'yPos':
                return 180 - ((position[terms.top] + position[terms.height] / 2) * 360) / this.imageAnalyseResult.metadata.height;
                break;
            case 'width':
                return ((position[terms.width] * 480) / this.imageAnalyseResult.metadata.width);
                break;
            case 'height':
                return ((position[terms.height] * 360) / this.imageAnalyseResult.metadata.height);
                break;
            case 'confidence':
                return target.confidence;
                break;
            default:
                break;
        }
    }

    imageResultSecond(args, util) {
        switch (args.OPTION) {
            case 'landmark':
                try {
                    let result = null;
                    let landmarkConfidence = 0;
                    for (category in this.imageAnalyseResult.categories) {
                        if (this.imageAnalyseResult.categories[category].detail &&
                            this.imageAnalyseResult.categories[category].detail.landmarks) {
                            for (landmark in this.imageAnalyseResult.categories[category].detail.landmarks) {
                                if (this.imageAnalyseResult.categories[category].detail.landmarks[landmark].confidence > landmarkConfidence) {
                                    result = this.imageAnalyseResult.categories[category].detail.landmarks[landmark].name;
                                    landmarkConfidence = this.imageAnalyseResult.categories[category].detail.landmarks[landmark].confidence;
                                }
                            }
                        }
                    }
                    return result ? result : 'NULL';
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'imageDescription':
                try {
                    return this.imageAnalyseResult.description.captions[0].text;
                } catch (e) {
                    return 'NULL';
                }
                break;
            default:
                break;
        }
    }


    //OCR Result Blocks
    handwrittenTextResult(args, util) {
        let result = this.handwrittenTextRecognized.join(' ');
        return result === '' ? 'NULL' : result;
    }

    printedTextRecognize(args, util) {
        let text = [];
        for (region in this.printedTextResult.regions) {
            for (line in this.printedTextResult.regions[region].lines) {
                for (word in this.printedTextResult.regions[region].lines[line].words) {
                    text.push(this.printedTextResult.regions[region].lines[line].words[word]['text']);
                }
            }
        }
        return text.join('') === '' ? 'NULL' : text.join(' ');
    }


    //Face Features blocks
    faceCount(args, util) {
        try {
            return this.faceAnalyseResult.length;
        } catch (e) {
            return 'NULL';
        }
    }

    faceFeaturesResult(args, util) {
        if (!(args.INDEX <= this.faceAnalyseResult.length && args.INDEX > 0))
            return 'NULL';

        let result = '';
        switch (args.OPTION) {
            case 'age':
                try {
                    result = this.faceAnalyseResult[args.INDEX - 1].faceAttributes.age;
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'gender':
                try {
                    result = this.faceAnalyseResult[args.INDEX - 1].faceAttributes.gender;
                } catch (e) {
                    return 'NULL';
                }
                break;
            case 'emotion':
                try {
                    let emotions = this.faceAnalyseResult[args.INDEX - 1].faceAttributes.emotion;
                    return Object.keys(emotions).reduce(function (a, b) {
                        return emotions[a] > emotions[b] ? a : b
                    });
                } catch (e) {
                    return 'NULL';
                }
            default:
                break;
        }
        return result === '' ? 'NULL' : result;
    }

    emotionIs(args, util) {
        if (!(args.INDEX <= this.faceAnalyseResult.length && args.INDEX > 0))
            return 'NULL';

        try {
            let emotions = this.faceAnalyseResult[args.INDEX - 1].faceAttributes.emotion;
            return Object.keys(emotions).reduce(function (a, b) {
                return emotions[a] > emotions[b] ? a : b
            }) === args.EMOTION;
        } catch (e) {
            return false
        }
    }

    headPosition(args, util) {
        if (!(args.INDEX <= this.faceAnalyseResult.length && args.INDEX > 0))
            return 'NULL';
        try {
            return this.faceAnalyseResult[args.INDEX - 1].faceAttributes.headPose[args.GESTURE];
        } catch (e) {
            return 'NULL';
        }
    }

    headLocation(args, util) {
        if (!(args.INDEX <= this.faceAnalyseResult.length && args.INDEX > 0))
            return 'NULL';
        try {
            switch (args.OPTION) {
                case 'xPos':
                    return ((this.faceAnalyseResult[args.INDEX - 1].faceRectangle.left + this.faceAnalyseResult[args.INDEX - 1].faceRectangle.width / 2) * 480) / this.faceImageSize.width - 240;
                case 'yPos':
                    return 180 - ((this.faceAnalyseResult[args.INDEX - 1].faceRectangle.top + this.faceAnalyseResult[args.INDEX - 1].faceRectangle.height / 2) * 360) / this.faceImageSize.height;
                case 'height':
                    return ((this.faceAnalyseResult[args.INDEX - 1].faceRectangle.height * 360) / this.faceImageSize.height);
                case 'width':
                    return ((this.faceAnalyseResult[args.INDEX - 1].faceRectangle.width * 480) / this.faceImageSize.width);
                default:
                    break;
            }
        } catch (e) {
            return 'NULL';
        }
    }
}
module.exports = AIServicesBlocks;
