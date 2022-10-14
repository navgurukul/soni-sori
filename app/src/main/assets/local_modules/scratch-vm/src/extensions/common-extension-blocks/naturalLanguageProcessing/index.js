const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require('format-message');

var natural = require('natural');
var csv = require('fast-csv');
var Analyzer = require('natural').SentimentAnalyzer;
var stemmer = require('natural').PorterStemmer;
var analyzer = new Analyzer("English", stemmer, "afinn");
var tokenizer = new natural.WordTokenizer();
var classifier = new natural.BayesClassifier();

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PHRpdGxlPkFydGJvYXJkIDY1PC90aXRsZT48cmVjdCB4PSIyMS44MyIgeT0iOS4xOSIgd2lkdGg9IjE3LjE3IiBoZWlnaHQ9IjE4LjQxIiByeD0iMS41NyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNjAuODMgMzYuNzkpIHJvdGF0ZSgtMTgwKSIgc3R5bGU9ImZpbGw6IzFiOGRjYiIvPjxwYXRoIGQ9Ik0yNi41NSwyNy41bC0zLjk0LDRhLjQ2LjQ2LDAsMCwxLS43OC0uMzJWMjMuMTVhLjQ2LjQ2LDAsMCwxLC43OC0uMzJsMy45NCw0QS40NS40NSwwLDAsMSwyNi41NSwyNy41WiIgc3R5bGU9ImZpbGw6IzFiOGRjYiIvPjxyZWN0IHg9IjIxLjgzIiB5PSI4LjM3IiB3aWR0aD0iMTcuMTciIGhlaWdodD0iMTguNDEiIHJ4PSIxLjU3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSg2MC44MyAzNS4xNCkgcm90YXRlKC0xODApIiBzdHlsZT0iZmlsbDojMWNhZWU0Ii8+PHBhdGggZD0iTTI2LjU1LDI2LjY3bC0zLjk0LDRhLjQ2LjQ2LDAsMCwxLS43OC0uMzJ2LThhLjQ1LjQ1LDAsMCwxLC43OC0uMzJsMy45NCw0QS40NS40NSwwLDAsMSwyNi41NSwyNi42N1oiIHN0eWxlPSJmaWxsOiMxY2FlZTQiLz48cmVjdCB4PSIxIiB5PSI5LjE4IiB3aWR0aD0iMTcuMTciIGhlaWdodD0iMTguNDEiIHJ4PSIxLjU3IiBzdHlsZT0iZmlsbDojZTY0OThjIi8+PHBhdGggZD0iTTEzLjQ1LDI3LjQ5bDMuOTQsNGEuNDYuNDYsMCwwLDAsLjc4LS4zMlYyMy4xNGEuNDYuNDYsMCwwLDAtLjc4LS4zMmwtMy45NCw0QS40NS40NSwwLDAsMCwxMy40NSwyNy40OVoiIHN0eWxlPSJmaWxsOiNlNjQ5OGMiLz48cmVjdCB4PSIxIiB5PSI4LjM1IiB3aWR0aD0iMTcuMTciIGhlaWdodD0iMTguNDEiIHJ4PSIxLjU3IiBzdHlsZT0iZmlsbDojZWI1OTljIi8+PHBhdGggZD0iTTEzLjQ1LDI2LjY2bDMuOTQsNGEuNDYuNDYsMCwwLDAsLjc4LS4zMlYyMi4zMWEuNDYuNDYsMCwwLDAtLjc4LS4zMmwtMy45NCw0QS40NS40NSwwLDAsMCwxMy40NSwyNi42NloiIHN0eWxlPSJmaWxsOiNlYjU5OWMiLz48cmVjdCB4PSIyNC44MiIgeT0iMTYuNzEiIHdpZHRoPSIxLjI0IiBoZWlnaHQ9IjQuNDUiIHN0eWxlPSJmaWxsOiNmZmYwMDAiLz48cmVjdCB4PSIyNC44MiIgeT0iMTQuNDIiIHdpZHRoPSIxLjI0IiBoZWlnaHQ9IjEuMjQiIHN0eWxlPSJmaWxsOiNmZmYwMDAiLz48cmVjdCB4PSIzNC43NyIgeT0iMTYuNzEiIHdpZHRoPSIxLjI0IiBoZWlnaHQ9IjQuNDUiIHN0eWxlPSJmaWxsOiNmZmYwMDAiLz48cmVjdCB4PSIzNC43NyIgeT0iMTQuNDIiIHdpZHRoPSIxLjI0IiBoZWlnaHQ9IjEuMjQiIHN0eWxlPSJmaWxsOiNmZmYwMDAiLz48cmVjdCB4PSIyNy4zNCIgeT0iMTMuOTYiIHdpZHRoPSIxLjM4IiBoZWlnaHQ9IjcuMiIgc3R5bGU9ImZpbGw6I2ZmZjAwMCIvPjxyZWN0IHg9IjMxLjk1IiB5PSIxMy45NiIgd2lkdGg9IjEuMzgiIGhlaWdodD0iNy4yIiBzdHlsZT0iZmlsbDojZmZmMDAwIi8+PHJlY3QgeD0iMjcuMzQiIHk9IjEzLjk2IiB3aWR0aD0iNS45OSIgaGVpZ2h0PSIxLjQ0IiBzdHlsZT0iZmlsbDojZmZmMDAwIi8+PHJlY3QgeD0iMjcuMzQiIHk9IjE2LjcxIiB3aWR0aD0iNS45OSIgaGVpZ2h0PSIxLjQ0IiBzdHlsZT0iZmlsbDojZmZmMDAwIi8+PHBhdGggZD0iTTEzLDE0Ljc2SDExLjU0bDAsMy4zOUgxM2ExLjcsMS43LDAsMSwwLDAtMy4zOVoiIHN0eWxlPSJmaWxsOm5vbmU7c3Ryb2tlOiNmZmYwMDA7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjAuODk5OTk5OTc2MTU4MTQycHgiLz48cGF0aCBkPSJNMTMsMTguMTVIMTEuNTRsMCwzLjM5SDEzYTEuNywxLjcsMCwxLDAsMC0zLjM5WiIgc3R5bGU9ImZpbGw6bm9uZTtzdHJva2U6I2ZmZjAwMDtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MC44OTk5OTk5NzYxNTgxNDJweCIvPjxyZWN0IHg9IjUuMjMiIHk9IjE0Ljc2IiB3aWR0aD0iMy45OCIgaGVpZ2h0PSIxMC45MiIgcng9IjEuNTciIHN0eWxlPSJmaWxsOm5vbmU7c3Ryb2tlOiNmZmYwMDA7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjAuODk5OTk5OTc2MTU4MTQycHgiLz48cmVjdCB4PSI1LjIzIiB5PSIxOC4wMSIgd2lkdGg9IjMuNzgiIGhlaWdodD0iMS4wOCIgc3R5bGU9ImZpbGw6I2ZjZWUyMSIvPjxyZWN0IHg9IjMuNzIiIHk9IjIyLjAyIiB3aWR0aD0iNi45NCIgaGVpZ2h0PSI0LjMzIiBzdHlsZT0iZmlsbDojZWI1OTljIi8+PC9zdmc+';

const menuIconURI = blockIconURI;

class naturalLanguageProcessing {
    constructor(runtime) {
        this.runtime = runtime;
        this.extensionName = 'Natural Language Processing';
    }

    getInfo() {
        return {
            id: 'naturalLanguageProcessing',
            name: 'Natural Language Processing',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    message: 'Text Classifier'
                },
                {
                    opcode: 'addDocument',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.addDocument',
                        default: 'add [TEXT] as [LABEL]',
                        description: 'set text in label'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText2",
                                default: 'your text',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        },
                        LABEL: {
                            type: ArgumentType.STRING,
                            defaultValue: 'class'
                        }
                    }
                },
                {
                    opcode: 'loadFromFile',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.loadFromFile',
                        default: 'load data from file [FILE]',
                        description: 'load data from file'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        FILE: {
                            type: ArgumentType.STRING,
                            defaultValue: 'choose .csv file'
                        }
                    }
                },
                "---",
                {
                    opcode: 'train',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.train',
                        default: 'train text classifier',
                        description: 'train classifier'
                    }),
                    blockType: BlockType.COMMAND,
                },
                {
                    opcode: 'resetClassifier',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.resetClassifier',
                        default: 'reset text classifier',
                        description: 'set text in label'
                    }),
                },
                "---",
                {
                    opcode: 'analyseText',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.analyseText',
                        default: 'get class of [TEXT]',
                        description: 'train classifier'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText2",
                                default: 'your text',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        }
                    }
                },
                {
                    message: 'Sentiments Analyser'
                },
                {
                    opcode: 'sentimentsAnalysis',
                    text: formatMessage({
                        id: 'naturalLanguageProcessing.sentimentsAnalysis',
                        default: 'get sentiment of [TEXT]',
                        description: 'get sentiment'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText3",
                                default: 'I am very Happy',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        }
                    }
                },
                // {
                //     message: 'Word Analysis'
                // },
                // {
                //     opcode: 'analyseSentense',
                //     text: formatMessage({
                //         id: 'naturalLanguageProcessing.analyseSentense',
                //         default: 'get [OPTION] in [TEXT]',
                //         description: 'analyse sentense'
                //     }),
                //     blockType: BlockType.COMMAND,
                //     arguments: {
                //         TEXT: {
                //             type: ArgumentType.STRING,
                //             defaultValue: 'I am very Happy'
                //         },
                //         OPTION: {
                //             type: ArgumentType.STRING,
                //             menu: 'option',
                //             defaultValue: '1'
                //         }
                //     }
                // },
            ],
            menus: {
                option: {
                    items: [
                        { text: 'nouns', value: '1' },
                        { text: 'verbs', value: '2' },
                        { text: 'adjectives', value: '3' },
                        { text: 'adverbs', value: '4' }
                    ],
                },
                expression: {
                    acceptReporters: true,
                    items: [
                        { text: 'anrgy', value: '1' },
                        { text: 'disgusted', value: '2' },
                        { text: 'fear', value: '3' },
                        { text: 'happy', value: '4' },
                        { text: 'neutral', value: '5' },
                        { text: 'sad', value: '6' },
                        { text: 'surprised', value: '7' }
                    ],
                },
            }
        };
    }

    addDocument(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        classifier.addDocument(args.TEXT, args.LABEL);
        return "Data added";
    }

    train(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const translatePromise = new Promise(resolve => {
            classifier.train();
            resolve("Classifier trained");
            return "Classifier trained";
        });
        return translatePromise;
    }

    analyseText(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const translatePromise = new Promise(resolve => {
            resolve(classifier.classify(args.TEXT));
            return classifier.classify(args.TEXT);
        }).catch(err => {
            return 'Classifier not trained';
        });
        return translatePromise;
    }

    resetClassifier(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        classifier = new natural.BayesClassifier();
        return "Done";
    }

    loadFromFile(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const translatePromise = new Promise(resolve => {
            var dataArr = [];
            var filePath = args.FILE;
            filePath = filePath.replace(/\\/g, '/');
            console.log(filePath);
            csv.parseFile(filePath, { headers: false })
                .on("data", data => {
                    dataArr.push(data);
                })
                .on("end", () => {
                    console.log(dataArr.length);
                    for (i = 0; i < dataArr.length; i++) {
                        if (dataArr[i][0] !== "" && dataArr[i][0] !== "") {
                            console.log(dataArr[i][0])
                            classifier.addDocument(dataArr[i][0], dataArr[i][1]);
                        }
                    }
                    resolve("File Loaded");
                    return "File Loaded";
                })
        });
        return translatePromise;
    }

    sentimentsAnalysis(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        var sentiment = analyzer.getSentiment(tokenizer.tokenize(args.TEXT));
        if (sentiment < -0.3) {
            return "Negative";
        }
        else if (sentiment < 0.3) {
            return "Neutral";
        }
        else {
            return "Positive";
        }
    }
}

module.exports = naturalLanguageProcessing;