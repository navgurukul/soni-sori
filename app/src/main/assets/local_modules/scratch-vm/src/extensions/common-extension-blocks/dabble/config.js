const servoChannel = {
    evive: '44',
    arduinoUno: '11',
    arduinoMega: '13',
    arduinoNano: '10',
    esp32: '14',
    tWatch: '25',
    quon: '5',
    tecBits:'3',
    quarky:'22'
};

const baudRate = {
    evive: '115200',
    arduinoUno: '9600',
    arduinoMega: '9600',
    arduinoNano: '9600',
    tecBits:'9600'
};

const bleName = {
    esp32: 'ESP32BLE',
    tWatch: 'TWatchBLE',
    quon: 'QuonBLE',
    quarky:'Quarky BLE'
};

const dir1Pin = {
    arduinoUno: '2',
    arduinoMega: '2',
    arduinoNano: '2',
    esp32: '2',
    tWatch: '25',
    quon: '3',
    tecBits:'4'
};

const dir2Pin = {
    arduinoUno: '4',
    arduinoMega: '3',
    arduinoNano: '4',
    esp32: '4',
    tWatch: '26',
    quon: '4',
    tecBits:'5'
};

const pwmPin = {
    arduinoUno: '3',
    arduinoMega: '4',
    arduinoNano: '3',
    esp32: '5',
    tWatch: '5',
    quon: '3',
    tecBits:'3',
    quarky:'18'
};

const configSetter = function (blockName, board, menuName) {
    return [blockName][board][menuName];
}

module.exports = {
    configSetter,
    servoChannel,
    baudRate,
    bleName,
    dir1Pin,
    dir2Pin,
    pwmPin
};