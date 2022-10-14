const initialiseLeg = (args, util, parent) => {
    const dataBuffer = [args.HIP_L, args.HIP_R, args.FOOT_L, args.FOOT_R];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 90, 1);
        parent._isSerialRead = resolve;
    });
};

const initialiseHand = (args, util, parent) => {
    const dataBuffer = [args.HIP_L, args.HIP_R, args.FOOT_L, args.FOOT_R];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 91, 1);
        parent._isSerialRead = resolve;
    });
};

const calibrateLeg = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.HIP_L), ...util.short2array(args.HIP_R), ...util.short2array(args.FOOT_L), ...util.short2array(args.FOOT_R)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 92, 1);
        parent._isSerialRead = resolve;
    });
};

const calibrateHand = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.HIP_L), ...util.short2array(args.HIP_R), ...util.short2array(args.FOOT_L), ...util.short2array(args.FOOT_R)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 93, 1);
        parent._isSerialRead = resolve;
    });
};

const moveHumanoidRobot = (args, util, parent) => {
    const dataBuffer = [args.ACTION, ...util.short2array(args.SPEED), args.SIZE];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 94, 1);
        parent._isSerialRead = resolve;
    });
};

const moveHumanoidRobotHand = (args, util, parent) => {
    const dataBuffer = [args.ACTION, ...util.short2array(args.SPEED)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 95, 1);
        parent._isSerialRead = resolve;
    });
};

const moveLeg = (args, util, parent) => {
    const dataBuffer = [args.HIP_L, args.HIP_R, args.FOOT_L, args.FOOT_R, ...util.short2array(args.DURATION)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 96, 1);
        parent._isSerialRead = resolve;
    });
};

const moveHand = (args, util, parent) => {
    const dataBuffer = [args.HIP_L, args.HIP_R, args.FOOT_L, args.FOOT_R, ...util.short2array(args.DURATION)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 97, 1);
        parent._isSerialRead = resolve;
    });
};

const initializeDotMatrixDisplayLeft = (args, util, parent) => {
    const dataBuffer = [args.SIDE, args.DINPIN, args.CLKPIN, args.CSPIN];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 99, 1);
        parent._isSerialRead = resolve;
    });
};

const displayMatrix1 = (args, util, parent) => {
    const arg1String = args.MATRIXL.substring(0, 64);
    const arg2String = args.MATRIXR.substring(0, 64);
    const arg1 = arg1String.split('');
    const arg2 = arg2String.split('');
    const dataBuffer = [...arg1, ...arg2];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 100, 1);
        parent._isSerialRead = resolve;
    });
};

const setServoAngle = (args, util, parent) => {
    const dataBuffer = [args.POSITION, args.ANGLE, args.CALIBRATIONS];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 98, 1);
        parent._isSerialRead = resolve;
    });
};

const uploadFirmware = (args, util, parent) => {
};

module.exports = {
    initialiseLeg,
    initialiseHand,
    calibrateLeg,
    calibrateHand,
    moveHumanoidRobot,
    moveLeg,
    moveHand,
    initializeDotMatrixDisplayLeft,
    displayMatrix1,
    setServoAngle,
    moveHumanoidRobotHand,
    uploadFirmware
};
