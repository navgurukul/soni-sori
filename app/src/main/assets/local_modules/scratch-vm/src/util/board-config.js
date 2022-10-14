const keyMirror = require('keymirror');

const boards = {
    None: 'None',
    Evive: 'evive',
    ArduinoUno: 'Arduino Uno',
    ArduinoMega: 'Arduino Mega',
    ArduinoNano: 'Arduino Nano',
    EviveJunior: 'junior',
    ESP32: 'ESP32',
    Quon: 'Quon',
    TWatch: 'T-Watch',
    Quarky: 'Quarky',
    MicroBit: 'micro:bit',
    TecBits: 'TecBits'
};

const boardMap = {
    'None': 'none',
    'evive': 'evive',
    'Arduino Uno': 'arduinoUno',
    'Arduino Nano': 'arduinoNano',
    'Arduino Mega': 'arduinoMega',
    'ESP32': 'esp32',
    'Quon': 'quon',
    'T-Watch': 'tWatch',
    'Quarky': 'quarky',
    'micro:bit': 'microbit',
    'TecBits': 'tecBits'
}

function getBoardId(board) {
    for (let boardKey in boardMap) {
        if (boardKey === board)
            return boardMap[boardKey];
    }
}

function registerBoardSpecificExtraExtensionList(extensionName, runtime, EXTENSION_SPECIFIC_BLOCKS) {
    let boardSpecificExtraExtensionList = {};
    for (let boardIndex in boardMap) {
        let boardValue = boardMap[boardIndex];
        if (runtime.commonExtensionAmongBoards.hasOwnProperty(boardIndex) && !runtime.commonExtensionAmongBoards[boardIndex].includes(extensionName)) continue;
        let specificBlockDataList = EXTENSION_SPECIFIC_BLOCKS(boardValue);
        boardSpecificExtraExtensionList[boardValue] = [];
        for (let blockDataIndex in specificBlockDataList) {
            let blockData = specificBlockDataList[blockDataIndex];
            let blocks = blockData.getBlocks();
            for (let blockIndex in blocks) {
                if (!!blocks[blockIndex]['opcode'])
                    boardSpecificExtraExtensionList[boardValue].push(extensionName + "_" + blocks[blockIndex]['opcode']);
            }
        }
    }
    runtime.megerBoardSpecificExtraBlocksFromCommonExtensionDict(boardSpecificExtraExtensionList);
}

function registerBoardSpecificExtensionList(extensionName, runtime, EXTENSION_SPECIFIC_BLOCKS, info) {
    let boardSpecificExtensionList = {};
    for (let boardIndex in boardMap) {
        let boardValue = boardMap[boardIndex];
        if (runtime.commonExtensionAmongBoards.hasOwnProperty(boardIndex) && !runtime.commonExtensionAmongBoards[boardIndex].includes(extensionName)) continue;
        let specificBlockDataList = EXTENSION_SPECIFIC_BLOCKS(boardValue);
        boardSpecificExtensionList[boardValue] = [];
        for (let blockDataIndex in specificBlockDataList) {
            let blockData = specificBlockDataList[blockDataIndex];
            let blocks = blockData.getBlocks();
            for (let blockIndex in blocks) {
                if (!!blocks[blockIndex]['opcode'])
                    boardSpecificExtensionList[boardValue].push(extensionName + "_" + blocks[blockIndex]['opcode']);
            }
        }
    }
    for (let boardIndex in boardSpecificExtensionList) {
        for (let blockIndex in info.blocks) {
            if (!!info.blocks[blockIndex]['opcode'])
                boardSpecificExtensionList[boardIndex].push(extensionName + "_" + info.blocks[blockIndex]['opcode']);
        }
    }
    runtime.megerBoardSpecificBlocksFromCommonExtensionDict(boardSpecificExtensionList);
}

function insertExtensionSpecificBlocks(blocks, specificBlockDataList) {
    for (let blockDataIndex in specificBlockDataList) {
        let blockData = specificBlockDataList[blockDataIndex];
        if (blockData.index == -1) {
            if (typeof blockData.getBlocks() === "object") {
                let extraBlocks = blockData.getBlocks();
                for (let blockIndex in extraBlocks) {
                    let block = extraBlocks[blockIndex];
                    blocks.push(block);
                }
            }
            else { blocks.push(blockData.getBlocks()); }
            continue;
        }
        let extraBlocks = blockData.getBlocks();
        for (let blockIndex in extraBlocks) {
            let block = extraBlocks[extraBlocks.length - blockIndex - 1];
            blocks.splice(blockData.index, 0, block);
        }
    }
    return blocks;
}

const ModelLoadingType = keyMirror({
    faceDetection: null,
    poseDetection: null,
    objectDetection: null,
    customObjectDetection: null
});

export {
    boards as default,
    boardMap,
    getBoardId,
    registerBoardSpecificExtraExtensionList,
    registerBoardSpecificExtensionList,
    insertExtensionSpecificBlocks,
    ModelLoadingType
};