const getBoardId = require('../../../util/board-config.js').getBoardId;

const initialiseMotor = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "tecBits") {
        util.runtime.include.add(`#include <motor.h>\n`);
        const _define = `Motor Motor${args.MOTOR}(${args.DIRECTION1}, ${args.DIRECTION2}, ${args.PWM});\n`;
        util.runtime.define.add(_define);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch") {
        util.runtime.include.add(`#include <esp32PWMUtilities.h>\n`);
        const _define = `Motors Motor${args.MOTOR};\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(`Motor${args.MOTOR}.attach(${args.PWM},${args.DIRECTION1},${args.DIRECTION2});\n`);
    }
};

const runMotor = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const _define = `Motor Motor${args.MOTOR}(MOTOR${args.MOTOR}_D1, MOTOR${args.MOTOR}_D2, MOTOR${args.MOTOR}_EN);\n`;
        util.runtime.define.add(_define);
        const command = (args.DIRECTION === '1' ? `Motor${args.MOTOR}.moveMotor(2.55*${args.SPEED});\n` : `Motor${args.MOTOR}.moveMotor(-2.55*${args.SPEED});\n`);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "tecBits") {
        const command = (args.DIRECTION === '1' ? `Motor${args.MOTOR}.moveMotor(2.55*${args.SPEED});\n` : `Motor${args.MOTOR}.moveMotor(-2.55*${args.SPEED});\n`);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "quarky") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime._setup.add(`Quon.begin();\n`);
        const command = (args.DIRECTION === '1' ? `Quon.moveMotor${args.MOTOR}(2.55*${args.SPEED});\n` : `Quon.moveMotor${args.MOTOR}(-2.55*${args.SPEED});\n`);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected == 'quarky') {
        util.runtime.include.add(`#include <quonMini.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = (args.DIRECTION === '1' ? `Motor${args.MOTOR}.moveMotor(FORWARD,${args.SPEED});\n` : `Motor${args.MOTOR}.moveMotor(BACKWARD,${args.SPEED});\n`);
        util.runtime.codeGenerateHelper(command);
    }
};

const updateMotorState = (args, util) => {

    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const _define = `Motor Motor${args.MOTOR}(MOTOR${args.MOTOR}_D1, MOTOR${args.MOTOR}_D2, MOTOR${args.MOTOR}_EN);\n`;
        util.runtime.define.add(_define);
        const func = (args.MOTOR_STATE === '4' ? 'freeMotor()' : 'lockMotor()');
        const command = `Motor${args.MOTOR}.${func};\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "tecBits") {
        const func = (args.MOTOR_STATE === '4' ? 'freeMotor()' : 'lockMotor()');
        const command = `Motor${args.MOTOR}.${func};\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "quarky") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime._setup.add(`Quon.begin();\n`);
        const func = (args.MOTOR_STATE === '4' ? `freeMotor${args.MOTOR}()` : `lockMotor${args.MOTOR}()`);
        const command = `Quon.${func};\n`;
        util.runtime.codeGenerateHelper(command);
    }
};

const setServo = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const _define = `Servo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "tecBits") {
        util.runtime.include.add(`#include <Servo.h>\n`);
        const _define = `Servo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch") {
        util.runtime.include.add(`#include <esp32PWMUtilities.h>\n`);
        const _define = `Servo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "quarky") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime._setup.add(`Quon.begin();\n`);
        const _define = `quonServo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "quarky") {
        util.runtime.include.add(`#include <quonMini.h>\n`);
        const _define = `Servo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.codeGenerateHelper(command);
    }
};

const setBLDC = (args, util) => {
    util.runtime.include.add(`#include <Servo.h>\n`);
    const _define = `Servo BLDC_${args.SERVO_CHANNEL};\n`;
    const _setup = `BLDC_${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL}, 1000, 2000);\n`;
    const command = `BLDC_${args.SERVO_CHANNEL}.write(${args.SPEED} * 1.8);\n`;
    util.runtime.define.add(_define);
    util.runtime._setup.add(_setup);
    util.runtime.codeGenerateHelper(command);
};

const setRelay = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive" || boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "tecBits") {
        util.runtime._setup.add(`pinMode(${args.DIGITAL_PIN}, OUTPUT);\n`);
        const write = `digitalWrite(${args.DIGITAL_PIN}, ${args.MODE});\n`;
        util.runtime.codeGenerateHelper(write);
    }
    else if (boardSelected === "quarky") {
        util.runtime._setup.add(`Quon.pinMode(${args.DIGITAL_PIN}, OUTPUT);\n`);
        const write = `Quon.digitalWrite(${args.DIGITAL_PIN}, ${args.MODE});\n`;
        util.runtime.codeGenerateHelper(write);
    }
};

const motorShieldRunMotor = (args, util) => {
    util.runtime.include.add(`#include <AFMotor.h>\n`);
    const _define = `AF_DCMotor motor${args.MOTOR2}(${args.MOTOR2});\n`;
    util.runtime.define.add(_define);
    const command1 = (`motor${args.MOTOR2}.setSpeed(2.55*${args.SPEED});\n`);
    const command2 = (`motor${args.MOTOR2}.run(${args.DIRECTION});\n`);
    util.runtime.codeGenerateHelper(command1 + command2);
};

const tWatchVibrationMotor = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "tWatch") {
        util.runtime._setup.add(`pinMode(33,OUTPUT);`);
        const command = (`digitalWrite(33,${args.VMOTOR_STATE});\n`);
        util.runtime.codeGenerateHelper(command);
    }
};

module.exports = {
    initialiseMotor,
    runMotor,
    updateMotorState,
    setServo,
    setRelay,
    setBLDC,
    motorShieldRunMotor,
    tWatchVibrationMotor
};
