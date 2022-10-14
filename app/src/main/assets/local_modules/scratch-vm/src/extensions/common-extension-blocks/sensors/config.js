const trigPin = {
    evive: '2',
    arduinoUno: '2',
    arduinoNano: '2',
    arduinoMega: '2',
    esp32: '2',
    tWatch: '25',
    quarky: '18',
    tecBits: '3'
};

const echoPin = {
    evive: '3',
    arduinoUno: '3',
    arduinoNano: '3',
    arduinoMega: '3',
    esp32: '4',
    tWatch: '26',
    quarky: '19',
    tecBits: '4'

};

const digitalSensorPin = {
    evive: '2',
    arduinoUno: '2',
    arduinoNano: '2',
    arduinoMega: '2',
    esp32: '2',
    tWatch: '25',
    quon: 'PORTC',
    quarky: '18',
    tecBits: '3'
};

const analogPins = {
    evive: 'A0',
    arduinoUno: 'A0',
    arduinoNano: 'A0',
    arduinoMega: 'A0',
    esp32: '32',
    tWatch: '33',
    quon: 'PORTA',
    quarky: '33',
    tecBits: '7'
};

const configSetter = function (blockName, board, menuName) {
    return [blockName][board][menuName];
}

module.exports = {
    configSetter,
    echoPin,
    trigPin,
    digitalSensorPin,
    analogPins
};