const digitalPin = {
    evive: '2',
    arduinoUno: '2',
    arduinoNano: '2',
    arduinoMega: '2',
    esp32: '2',
    tWatch: '25',
    quon: '27',
    tecBits: '3',
    quarky: '18'

};

const configSetter = function (blockName, board, menuName) {
    return [blockName][board][menuName];
}

module.exports = {
    digitalPin,
};
