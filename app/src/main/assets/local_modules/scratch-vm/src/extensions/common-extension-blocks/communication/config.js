const setSerialPin = {
    esp32: {
        SERIALTX: '14',
        SERIALRX: '15',
    },
    tWatch: {
        SERIALTX: '25',
        SERIALRX: '26',
    },
    quon: {
        SERIALTX: '27',
        SERIALRX: '15',       
    },
    quakry: {
        SERIALTX: '18',
        SERIALRX: '19',       
    },
};

const configSetter =  function(blockName, board, menuName) {
    return [blockName][board][menuName];
}

module.exports = {
    configSetter,
    setSerialPin
};