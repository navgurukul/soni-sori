const getBoardId = require('../../../util/board-config.js').getBoardId;

const setBluetoothName = (args, util) => {
    const arg = args.BLUETOOTH_NAME.split('');
    util.runtime.writeToPeripheral(arg.map(ele => ele.charCodeAt(0)), 104, 2);
    return util.setSendDelay();
};

const enableLEDControl = (args, util) => {
    util.runtime.writeToPeripheral([], 62, 2);
    return util.setSendDelay();
};

const terminalCheck = (args, util, parent) => {
    const arg = args.DATA.split('');
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(arg.map(ele => ele.charCodeAt(0)), 64, 1);
        parent._isSerialRead = resolve;
    });
};

const terminalRead = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral([], 65, 1);
        parent._isSerialRead = resolve;
    });
};

const terminalWrite = (args, util) => {
    const arg = args.DATA.split('');
    util.runtime.writeToPeripheral(arg.map(ele => ele.charCodeAt(0)), 66, 2);
    return util.setSendDelay();
};

const getGamepadOne = (args, util, parent) => {
    const dataBuffer = [args.GAMEPAD_BUTTON];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 63, 1);
        parent._isSerialRead = resolve;
    });
};

const getGamepadTwo = (args, util, parent) => {
    const arg = [args.JOYSTICK_BUTTON];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(arg, 67, 1);
        parent._isSerialRead = resolve;
    });
};

const enablePinStateMonitor = (args, util) => {
    util.runtime.writeToPeripheral([], 72, 2);
    return util.setSendDelay();
};

const dabbleeviveMotorControl = (args, util) => {
    const dataBuffer = [args.MOTOR_NO];
    util.runtime.writeToPeripheral(dataBuffer,121, 2);
    return util.setSendDelay();
}

const customDabbleMotorControl = (args, util) => {
    const dataBuffer = [args.MOTOR_NO];
    util.runtime.writeToPeripheral(dataBuffer,121, 2);
    return util.setSendDelay();
}

const espDabbleMotorControl = (args, util) => {
    const dataBuffer = [args.MOTOR_NO, args.PWM, args.DIR1, args.DIR2];
    util.runtime.writeToPeripheral(dataBuffer,121, 2);
    return util.setSendDelay();
}

const dabbleMotorControl = (args, util) => {
    const dataBuffer = [args.MOTOR_NO, args.PWM, args.DIR1, args.DIR2];
    util.runtime.writeToPeripheral(dataBuffer,121, 2);
    return util.setSendDelay();
}

const dabbleServoControl = (args, util) => {
    const dataBuffer = [args.SERVO_NO,args.SERVO_PIN];
    util.runtime.writeToPeripheral(dataBuffer,122, 2);
    return util.setSendDelay();
}

const tactileSwitch = (args, util, parent) => {
    const dataBuffer = [args.TACTILE_SWITCH];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 68, 1);
        parent._isSerialRead = resolve;
    });
};

const slideSwitch = (args, util, parent) => {
    const dataBuffer = [args.SLIDE_SWITCH, args.SWITCH_DIRECTION];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 69, 1);
        parent._isSerialRead = resolve;
    });
};

const potentiometer = (args, util, parent) => {
    const dataBuffer = [args.POTENTIOMETER];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 70, 1);
        parent._isSerialRead = resolve;
    });
};

const readDabbleSensor = (args, util, parent) => {
    const dataBuffer = [args.SENSOR];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 71, 1);
        parent._isSerialRead = resolve;
    });
};

const dabbleRefresh = (args, util, parent) => {
    util.runtime.writeToPeripheral([], 62, 2);
    return util.setSendDelay();
};

const getColorValue = (args, util, parent) => {
    const dataBuffer = [args.COLOR_VALUE, args.ROW - 1, args.COLUMN - 1];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 80, 1);
        parent._isSerialRead = resolve;
    });
};

const colorDetectorSettings = (args, util, parent) => {
    util.runtime.writeToPeripheral([args.GRID_VAL, args.CALC_MOD, args.SCHEME_VAL], 81, 2);
     return util.setSendDelay();
};

const cameraSettings = (args, util) => {
    util.runtime.writeToPeripheral([args.FLASH_MODE, args.QUALITY, args.ZOOM], 82, 2);
    return util.setSendDelay();
};

const rotateCamera = (args, util) => {
    util.runtime.writeToPeripheral([args.SIDE], 83, 2);
    return util.setSendDelay();
};

const cameraAction = (args, util) => {
    util.runtime.writeToPeripheral([args.CAMERA_ACTION], 84, 2);
    return util.setSendDelay();
};

const createDataLoggerFile = (args, util) => {
    const arg = args.FILENAME.split('');
    const dataBuffer = [args.FILETYPE,arg.map(ele => ele.charCodeAt(0))];
    util.runtime.writeToPeripheral(dataBuffer,114, 2);
    return util.setSendDelay();
}

const sendDatatoDataLogger = (args, util) => {
    const arg = args.COLUMN_NAME.split('');
    const bytes = new Uint8Array(new Float32Array([args.DATA]).buffer);
    console.log(bytes);
    const dataBuffer = [...bytes,arg.map(ele => ele.charCodeAt(0))];
    util.runtime.writeToPeripheral(dataBuffer,115, 2);
    return util.setSendDelay();
}

const stopDataLogger = (args, util) => {
    util.runtime.writeToPeripheral([], 116, 2);
    return util.setSendDelay();
}

const sendNotification = (args, util) => {
    const arg = args.TITLE.concat('#', args.NOTIFICATION_MESSAGE);
    const arg1 = arg.split('');
    util.runtime.writeToPeripheral(arg1.map(ele => ele.charCodeAt(0)),117, 2);
    return util.setSendDelay();
}

const clearNotification = (args, util) => {
    util.runtime.writeToPeripheral([],118, 2);
    return util.setSendDelay();
}

const playMusic = (args, util) => {
    const arg = args.FILENAME.split('');
    const dataBuffer = [args.MUSIC_TASK,arg.map(ele => ele.charCodeAt(0))];
    util.runtime.writeToPeripheral(dataBuffer,119, 2);
    return util.setSendDelay();
}

const stopMusic = (args, util) => {
    util.runtime.writeToPeripheral([],120, 2);
    return util.setSendDelay();
}

module.exports = {
    enableLEDControl,
    terminalCheck,
    terminalRead,
    terminalWrite,
    getGamepadOne,
    getGamepadTwo,
    enablePinStateMonitor,
    tactileSwitch,
    slideSwitch,
    potentiometer,
    readDabbleSensor,
    dabbleRefresh,
    getColorValue,
    colorDetectorSettings,
    cameraSettings,
    rotateCamera,
    cameraAction,
    createDataLoggerFile,
    sendDatatoDataLogger,
    stopDataLogger,
    sendNotification,
    clearNotification,
    playMusic,
    stopMusic,
    dabbleMotorControl,
    dabbleServoControl,
    setBluetoothName,
    customDabbleMotorControl,
    espDabbleMotorControl
}