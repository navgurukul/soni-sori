const initialise2 = (args, util, parent) => {
    const dataBuffer = [args.BASE, args.LINK1, args.LINK2, args.ROT, args.GRIPPER];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 90, 1);
        parent._isSerialRead = resolve;
    });
};

const setTrim = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.BASE), ...util.short2array(args.LINK1), ...util.short2array(args.LINK2), ...util.short2array(args.ROT), ...util.short2array(args.GRIPPER)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 103, 1);
        parent._isSerialRead = resolve;
    });
};

const initialiseGripper = (args, util, parent) => {
    const dataBuffer = [args.OPEN, args.CLOSE];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 91, 1);
        parent._isSerialRead = resolve;
    });
};

const gotoXYZ = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.XPOS), ...util.short2array(args.YPOS), ...util.short2array(args.ZPOS), ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 92, 1);
        parent._isSerialRead = resolve;
    });
};

const gotoXYZinLine = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.XPOS), ...util.short2array(args.YPOS), ...util.short2array(args.ZPOS), ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 93, 1);
        parent._isSerialRead = resolve;
    });
};

const moveInCircle = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.XPOS), ...util.short2array(args.ZPOS), ...util.short2array(args.YPOS), ...util.short2array(args.RADIUS), ...util.short2array(args.SEC), args.DIRECTION];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 94, 1);
        parent._isSerialRead = resolve;
    });
};

const moveInArc = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.XPOS), ...util.short2array(args.ZPOS), ...util.short2array(args.YPOS), ...util.short2array(args.RADIUS), ...util.short2array(args.START), ...util.short2array(args.END), ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 102, 1);
        parent._isSerialRead = resolve;
    });
};

const goToInOneAxis = (args, util, parent) => {
    const dataBuffer = [args.AXIS, ...util.short2array(args.POSITION), ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 95, 1);
        parent._isSerialRead = resolve;
    });
};

const moveByInOneAxis = (args, util, parent) => {
    const dataBuffer = [args.AXIS, ...util.short2array(args.POSITION), ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 96, 1);
        parent._isSerialRead = resolve;
    });
};

const setServoAngleTo = (args, util, parent) => {
    const dataBuffer = [args.SERVO, args.ANGLE, ...util.short2array(args.SEC)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 97, 1);
        parent._isSerialRead = resolve;
    });
};

const controlGripper = (args, util, parent) => {
    const dataBuffer = [args.STATE];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 98, 1);
        parent._isSerialRead = resolve;
    });
};

const getServoAngle = (args, util, parent) => {
    const dataBuffer = [args.SERVO];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 99, 1);
        parent._isSerialRead = resolve;
    });
};

const getCurrentPosition = (args, util, parent) => {
    const dataBuffer = [args.POSITION];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 100, 1);
        parent._isSerialRead = resolve;
    });
};

const setOffset = (args, util, parent) => {
    const dataBuffer = [...util.short2array(args.LENGTH), ...util.short2array(args.ZOFFSET)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 101, 1);
        parent._isSerialRead = resolve;
    });
};

const uploadFirmware = (args, util) => {
};

module.exports = {
    uploadFirmware,
    initialise2,
    gotoXYZ,
    gotoXYZinLine,
    setServoAngleTo,
    goToInOneAxis,
    moveByInOneAxis,
    getServoAngle,
    getCurrentPosition,
    initialiseGripper,
    controlGripper,
    moveInCircle,
    moveInArc,
    setOffset,
    setTrim
};
