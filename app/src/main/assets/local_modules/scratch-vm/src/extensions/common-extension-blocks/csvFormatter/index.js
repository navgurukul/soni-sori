const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require( "../../../../local_modules/format-message/index.js");
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;
const path = require('path');
const fs = require('fs');
const csv = require( "../../../../local_modules/@fast-csv/format/build/src/index.js");

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0OCA0OCIgd2lkdGg9IjQ4cHgiIGhlaWdodD0iNDhweCI+PHBhdGggZmlsbD0iIzQzYTA0NyIgZD0iTTM3LDQ1SDExYy0xLjY1NywwLTMtMS4zNDMtMy0zVjZjMC0xLjY1NywxLjM0My0zLDMtM2gxOWwxMCwxMHYyOUM0MCw0My42NTcsMzguNjU3LDQ1LDM3LDQ1eiIvPjxwYXRoIGZpbGw9IiNjOGU2YzkiIGQ9Ik00MCAxM0wzMCAxMyAzMCAzeiIvPjxwYXRoIGZpbGw9IiMyZTdkMzIiIGQ9Ik0zMCAxM0w0MCAyMyA0MCAxM3oiLz48cGF0aCBmaWxsPSIjZThmNWU5IiBkPSJNMTkgMjNIMzNWMjVIMTl6TTE5IDI4SDMzVjMwSDE5ek0xOSAzM0gzM1YzNUgxOXpNMTUgMjNIMTdWMjVIMTV6TTE1IDI4SDE3VjMwSDE1ek0xNSAzM0gxN1YzNUgxNXoiLz48L3N2Zz4=';

const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0OCA0OCIgd2lkdGg9IjQ4cHgiIGhlaWdodD0iNDhweCI+PHBhdGggZmlsbD0iIzQzYTA0NyIgZD0iTTM3LDQ1SDExYy0xLjY1NywwLTMtMS4zNDMtMy0zVjZjMC0xLjY1NywxLjM0My0zLDMtM2gxOWwxMCwxMHYyOUM0MCw0My42NTcsMzguNjU3LDQ1LDM3LDQ1eiIvPjxwYXRoIGZpbGw9IiNjOGU2YzkiIGQ9Ik00MCAxM0wzMCAxMyAzMCAzeiIvPjxwYXRoIGZpbGw9IiMyZTdkMzIiIGQ9Ik0zMCAxM0w0MCAyMyA0MCAxM3oiLz48cGF0aCBmaWxsPSIjZThmNWU5IiBkPSJNMTkgMjNIMzNWMjVIMTl6TTE5IDI4SDMzVjMwSDE5ek0xOSAzM0gzM1YzNUgxOXpNMTUgMjNIMTdWMjVIMTV6TTE1IDI4SDE3VjMwSDE1ek0xNSAzM0gxN1YzNUgxNXoiLz48L3N2Zz4=';

class CsvFile {
    static write(filestream, rows, options) {
        return new Promise((res, rej) => {
            csv.writeToStream(filestream, rows, options)
                .on('error', err => rej(err))
                .on('finish', () => res());
        });
    }

    constructor(opts) {
        this.headers = opts.headers;
        this.path = opts.path;
        this.writeOpts = { headers: this.headers, includeEndRowDelimiter: true };
    }

    changePath(fileLocation){
        this.path = fileLocation.path;
    }

    create(rows) {
        return CsvFile.write(fs.createWriteStream(this.path), rows, { ...this.writeOpts });
    }

    append(rows) {
        return CsvFile.write(fs.createWriteStream(this.path, { flags: 'a' }), rows, {
            ...this.writeOpts,
            // dont write the headers when appending
            writeHeaders: false,
        });
    }
}

const csvFile = new CsvFile({
    path: path.resolve(__dirname, 'append.tmp1.csv'),
    // headers to write
    headers: ['Data1', 'Data2', 'Data3', 'Data4', 'Data5', 'Data6', 'Data7', 'Data8', 'Data9','Data10'],
});

var data = { Data1: '',
            Data2: '',
            Data3: '',
            Data4: '',
            Data5: '',
            Data6: '',
            Data7: '',
            Data8: '',
            Data9: '',
            Data10: '',
        };

class csvFormatter { 
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('serialRead', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'csvFormatter';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);

        this.documentsPath = path.join(runtime.projectDefaultPath, 'Pictoblox','DataLogger');
        if (!fs.existsSync(this.documentsPath)) {
            try {
                fs.mkdirSync(this.documentsPath);
            } catch (err) {
            }
        }
    }

    static get STATE_KEY() {
        return 'csvFormatter';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const csvFormatterState = sourceTarget.getCustomState(csvFormatter.STATE_KEY);
            if (csvFormatterState) {
                newTarget.setCustomState(csvFormatter.STATE_KEY, Clone.simple(csvFormatterState));
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
            id: 'csvFormatter',
            name: 'Data Logger (S)',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#0fBD8C',
            colourSecondary: '#0DA57A',
            colourTertiary: '#0B8E69',
            blocks: [
                {
                    opcode: 'nameFile',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'csvFormatter.nameFile',
                        default: 'create file [NAME]',
                        description: 'createFile'
                    }),
                    arguments: {
                        NAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'filename'
                        }
                    }
                },
                {
                    opcode: 'setData',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'csvFormatter.setData',
                        default: 'set [PARAMETER] to [DATA]',
                        description: 'set parameter'
                    }),
                    arguments: {
                        PARAMETER: {
                            type: ArgumentType.NUMBER,
                            menu: 'dataNumber',
                            defaultValue: '1'
                        },
                        DATA: {
                            type: ArgumentType.STRING,
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'saveData',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'csvFormatter.saveData',
                        default: 'save data',
                        description: 'save data'
                    }),
                },
                {
                    opcode: 'getTime',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'csvFormatter.getTime',
                        default: 'get timestamp',
                        description: 'get timestamp'
                    }),
                },
            ],
            menus: {
                dataNumber: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option1',
                            default: 'data 1',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option2',
                            default: 'data 2',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option3',
                            default: 'data 3',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option4',
                            default: 'data 4',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option5',
                            default: 'data 5',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option6',
                            default: 'data 6',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option7',
                            default: 'data 7',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option8',
                            default: 'data 8',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option9',
                            default: 'data 9',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.csvFormatter.parameter.option10',
                            default: 'data 10',
                            description: 'Menu'
                        }), value: '10'
                    },
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

    nameFile(args, util) {
        var extention = ".csv";
        var name = args.NAME;
        var fileName = name.concat(extention);
        csvFile.changePath({
            path: path.resolve(this.documentsPath, fileName).split(':')[1]
        });
    }

    setData(args, util) {
        if (args.PARAMETER === '1'){
            data.Data1 = args.DATA;
        }
        else if (args.PARAMETER === '2'){
            data.Data2 = args.DATA;
        }
        else if (args.PARAMETER === '3'){
            data.Data3 = args.DATA;
        }
        else if (args.PARAMETER === '4'){
            data.Data4 = args.DATA;
        }
        else if (args.PARAMETER === '5'){
            data.Data5 = args.DATA;
        }
        else if (args.PARAMETER === '6'){
            data.Data6 = args.DATA;
        }
        else if (args.PARAMETER === '7'){
            data.Data7 = args.DATA;
        }
        else if (args.PARAMETER === '8'){
            data.Data8 = args.DATA;
        }
        else if (args.PARAMETER === '9'){
            data.Data9 = args.DATA;
        }
        else if (args.PARAMETER === '10'){
            data.Data10 = args.DATA;
        }
        else if (args.PARAMETER === '2'){
            data.data2 = args.DATA;
        }
    }

    saveData(args, util) {
        // console.log(data);
        csvFile.append([data]);
        data = { Data1: '',
            Data2: '',
            Data3: '',
            Data4: '',
            Data5: '',
            Data6: '',
            Data7: '',
            Data8: '',
            Data9: '',
            Data10: '',
        };
        return util.setDelay(10);
    }

    getTime(args, util) {
        const date = new Date();
        var time = '';
        var time = time.concat(date.getFullYear());
        var time = time.concat(date.getMonth() + 1);
        var time = time.concat(date.getDate());
        var time = time.concat(" ");
        var time = time.concat(date.getHours());
        var time = time.concat(":");
        var time = time.concat(date.getMinutes());
        var time = time.concat(":");
        var time = time.concat(date.getSeconds());
        var time = time.concat(".");
        var time = time.concat(date.getMilliseconds());
        return time;
    }

}

module.exports = csvFormatter;
