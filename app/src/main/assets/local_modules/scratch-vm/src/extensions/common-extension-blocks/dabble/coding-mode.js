let baudrate = 115200;
const getBoardId = require('../../../util/board-config.js').getBoardId;

const checkDefaultCodeforevive = (util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime._setup.add(`Dabble.begin(${baudrate});\n`);
        util.runtime.include.add(`#include <Dabble.h>\n`);
        util.runtime._loop.add(`Dabble.processInput();\n`);
    }
};

const setBaudRate = (args, util) => {
    baudrate = args.BAUDRATE;
    util.runtime.include.add(`#include <Dabble.h>\n`);
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "tecBits") {
        util.runtime.include.add(`#include <Servo.h>\n`);
    }
    util.runtime._setup.add(`Dabble.begin(${baudrate});\n`);
    util.runtime._loop.add(`Dabble.processInput();\n`);
};

const setBluetoothName = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quarky")
    {
        util.runtime._setup.add(`Dabble.begin(${args.BLUETOOTH_NAME});\n`);
        util.runtime.include.add(`#include <DabbleESP32.h>\n`);
        util.runtime._loop.add(`Dabble.processInput();\n`);
    }
    else if (boardSelected === "quon")
    {
        util.runtime._setup.add(`Dabble.begin(${args.BLUETOOTH_NAME});\n`);
        util.runtime.include.add(`#include <quonDabble.h>\n`);
        util.runtime._loop.add(`Dabble.processInput();\n`);
    }
};

const enableLEDControl = (args, util) => {
    checkDefaultCodeforevive(util);
};

const terminalCheck = (args, util) => {
    checkDefaultCodeforevive(util);
    return `Terminal.compareString(${args.DATA})`;
};

const terminalRead = (args, util) => {
	checkDefaultCodeforevive(util);
    return `Terminal.readNumber()`;
};

const terminalWrite = (args, util) => {
    checkDefaultCodeforevive(util);
    const command = `Terminal.println(${args.DATA});\n`;
	util.runtime.codeGenerateHelper(command);
};

const getGamepadOne = (args, util) => {
    checkDefaultCodeforevive(util);
    return `GamePad.isPressed(${args.GAMEPAD_BUTTON})`;
};

const getGamepadTwo = (args, util) => {
    checkDefaultCodeforevive(util);
    return `GamePad.getJoystickData(${args.JOYSTICK_BUTTON})`;
};

const enablePinStateMonitor = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._loop.add(`Dabble.processInput();\nPinMonitor.sendDigitalData();\nPinMonitor.sendAnalogData();\ndelayMicroseconds(20);\n`);
};

const customDabbleMotorControl = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime.define.add(`motorControls Control;\n`);
    util.runtime._loop.add(`Control.runMotor${args.MOTOR_NO}();\n`);
};

const espDabbleMotorControl = (args, util) => {
    util.runtime._loop.add(`Controls.runMotor${args.MOTOR_NO}(${args.PWM}, ${args.DIR1}, ${args.DIR2});\n`);
};

const dabbleMotorControl = (args, util) => {
    util.runtime.define.add(`motorControls Control;\n`);
    util.runtime._loop.add(`Control.runMotor${args.MOTOR_NO}(${args.DIR1}, ${args.DIR2}, ${args.PWM});\n`);
};

const dabbleServoControl = (args, util) => {
    checkDefaultCodeforevive(util);
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive"|| boardSelected === "tecBits") {
        util.runtime.define.add(`motorControls Control;\n`);
        util.runtime.define.add(`Servo dabbleServo${args.SERVO_NO};\n`);
        util.runtime._setup.add(`dabbleServo${args.SERVO_NO}.attach(${args.SERVO_PIN});\n`);
        util.runtime._loop.add(`dabbleServo${args.SERVO_NO}.write(Control.getangle_Servo${args.SERVO_NO}());\n`);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quarky"){
        util.runtime._loop.add(`Controls.runServo${args.SERVO_NO}(${args.SERVO_PIN});\n`);
    }
    else if(boardSelected === "quon" )
    {
        util.runtime.include.add(`#include <quon.h>\n`); 
        util.runtime.define.add(`quonServo Servo${args.SERVO_PIN};\n`);
        util.runtime._loop.add(`Servo${args.SERVO_PIN}.write(Controls.getServo${args.SERVO_NO}Angle());\n`);        
    }
};

const tactileSwitch = (args, util) => {
    checkDefaultCodeforevive(util);
    return `Inputs.getTactileSwitchStatus(${args.TACTILE_SWITCH})`;
};

const slideSwitch = (args, util) => {
    checkDefaultCodeforevive(util);
    return `Inputs.getSlideSwitchStatus(${args.SLIDE_SWITCH}, ${args.SWITCH_DIRECTION})`;
};

const potentiometer = (args, util) => {
    checkDefaultCodeforevive(util);
    return `Inputs.getPotValue(${args.POTENTIOMETER})`;

};

const readDabbleSensor = (args, util) => {
    checkDefaultCodeforevive(util);
    return `Sensor.getSensorData(${args.SENSOR})`;

};

const dabbleRefresh = (args, util) => {
    checkDefaultCodeforevive(util);
    const command = `Dabble.processInput();\n`;
	util.runtime.codeGenerateHelper(command);
};

const getColorValue = (args, util) => {
    checkDefaultCodeforevive(util);
    return `ColorDetector.getColorValue(${args.COLOR_VALUE}, ${args.ROW} - 1, ${args.COLUMN} - 1)`;
};

const colorDetectorSettings = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // let boardSelected = getBoardId(util.runtime.boardSelected);
    // if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive") {
    //     util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // }
    const command = `ColorDetector.sendSettings(${args.GRID_VAL}, ${args.CALC_MOD}, ${args.SCHEME_VAL});\n`;
	util.runtime.codeGenerateHelper(command);
};

const cameraSettings = (args, util) => {
    checkDefaultCodeforevive(util);
    //let boardSelected = getBoardId(util.runtime.boardSelected);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive") {
    //     util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // }
    const command = `Camera.cameraConfig(${args.FLASH_MODE},${args.QUALITY},${args.ZOOM});\n`;
	util.runtime.codeGenerateHelper(command);
};

const rotateCamera = (args, util) => {
    checkDefaultCodeforevive(util);
    //let boardSelected = getBoardId(util.runtime.boardSelected);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive") {
    //     util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // }
    const command = `Camera.flipTo(${args.SIDE});\n`;
	util.runtime.codeGenerateHelper(command);
};

const cameraAction = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    //let boardSelected = getBoardId(util.runtime.boardSelected);
    // if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive") {
    //     util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    // }
    const command = `Camera.cameraAction(${args.CAMERA_ACTION});\n`;
	util.runtime.codeGenerateHelper(command);
};

const createDataLoggerFile = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    const command = `DataLogger.fileConfig(${args.FILETYPE},${args.FILENAME});\n`;
	util.runtime.codeGenerateHelper(command);
};

const sendDatatoDataLogger = (args, util) => {
    checkDefaultCodeforevive(util);
    const command = `DataLogger.send(${args.COLUMN_NAME},${args.DATA});\n`;
	util.runtime.codeGenerateHelper(command);
};

const stopDataLogger = (args, util) => {
    checkDefaultCodeforevive(util);
    const command = `DataLogger.stop();\n`;
	util.runtime.codeGenerateHelper(command);
};

const sendNotification = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    const command = `Notification.setTitle(${args.TITLE});\nNotification.notifyPhone(${args.NOTIFICATION_MESSAGE});\n`;
	util.runtime.codeGenerateHelper(command);
};

const clearNotification = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    const command = `Notification.clear();\n`;
	util.runtime.codeGenerateHelper(command);
};

const playMusic = (args, util) => {
    checkDefaultCodeforevive(util);
    util.runtime._setup.add(`Dabble.waitForAppConnection();\n`);
    const command = `Music.playMusic(${args.MUSIC_TASK},${args.FILENAME});\n`;
	util.runtime.codeGenerateHelper(command);
};

const stopMusic = (args, util) => {
    checkDefaultCodeforevive(util);
    const command = `Music.stop();\n`;
	util.runtime.codeGenerateHelper(command);
};

// const waitForAppConnection = (args, util) => {
//     checkDefaultCodeforevive(util);
//     const command = `Dabble.waitForAppConnection();\n`;
// 	util.runtime.codeGenerateHelper(command);
// };

const baudrateOscilloscope = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime.include.add(`#include <OscilloscopeModule.h>\n`);
        util.runtime._setup.add(`Oscilloscope.begin(3,${args.OSC_BAUDRATE});\n`);
    }
    else if (boardSelected === "arduinoUno" || boardSelected === "arduinoNano") {
        util.runtime.include.add(`#include <OscilloscopeModule.h>\n`);
        util.runtime._setup.add(`Serial.begin(${args.OSC_BAUDRATE});\n`);
        util.runtime._setup.add(`Oscilloscope.begin(Serial);\n`);
    }
};

const baudrateOscilloscopeMega = (args, util) => {
    util.runtime.include.add(`#include <OscilloscopeModule.h>\n`);
    util.runtime._setup.add(`Oscilloscope.begin(${args.SERIAL},${args.OSC_BAUDRATE});\n`);
};

const sendDataToOscilloscope = (args, util) => {
    const command = `Oscilloscope.sendDataChannel(${args.CHANNEL},${args.OSCILLOSCOPE_DATA});\n`;
	util.runtime.codeGenerateHelper(command);
};

module.exports = {
    setBaudRate,
    dabbleRefresh,
    enableLEDControl,
    terminalCheck,
    terminalRead,
    terminalWrite,
    getGamepadOne,
    getGamepadTwo,
    enablePinStateMonitor,
    dabbleMotorControl,
    dabbleServoControl,
    tactileSwitch,
    slideSwitch,
    potentiometer,
    readDabbleSensor,
    cameraSettings,
    rotateCamera,
    cameraAction,
    colorDetectorSettings,
    getColorValue,
    createDataLoggerFile,
    sendDatatoDataLogger,
    stopDataLogger,
    sendNotification,
    clearNotification,
    playMusic,
    stopMusic,
    baudrateOscilloscope,
    sendDataToOscilloscope,
    setBluetoothName,
    //waitForAppConnection,
    customDabbleMotorControl,
    espDabbleMotorControl,
    baudrateOscilloscopeMega
};
