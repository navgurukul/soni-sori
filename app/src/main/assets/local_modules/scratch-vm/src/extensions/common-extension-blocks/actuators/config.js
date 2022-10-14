const initialiseMotor = {
    evive: null,
    arduinoUno: {
        MOTOR: '1',
        DIRECTION1: '2',
        DIRECTION2: '4',
        PWM: '3'
    },
    arduinoNano: {
        MOTOR: '1',
        DIRECTION1: '2',
        DIRECTION2: '4',
        PWM: '3'
    },
    tecBits: {
        MOTOR: '1',
        DIRECTION1: '3',
        DIRECTION2: '5',
        PWM: '6'
    },
    arduinoMega: {
        MOTOR: '1',
        DIRECTION1: '2',
        DIRECTION2: '3',
        PWM: '4'
    },
    esp32: {
        MOTOR: '1',
        DIRECTION1: '2',
        DIRECTION2: '4',
        PWM: '5'
    },
    tWatch: {
        MOTOR: '1',
        DIRECTION1: '25',
        DIRECTION2: '26',
        PWM: '5'
    },
    quon: null,
    quarky: null

};

const servoChannel = {
    evive: '44',
    arduinoUno: '11',
    arduinoMega: '13',
    arduinoNano: '10',
    esp32: '14',
    tWatch: '25',
    quon: '5',
    quarky: '22',
    tecBits: '3'
};

const BLDC = {
    evive: '44',
    arduinoUno: '11',
    arduinoMega: '12',
    arduinoNano: '11',
    tecBits: '3'
};

const configSetter = function (blockName, board, menuName) {
    return [blockName][board][menuName];
}

module.exports = {
    configSetter,
    initialiseMotor,
    servoChannel,
    BLDC
};