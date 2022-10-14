const Cast = require('../../../util/cast');
const constants = require('../../../engine/scratch-blocks-constants');

let robotOreintationMode = 1;

const runMotor = (args, util) => {
    const arg = args.MOTOR.split('');
    //const charData = [arg.map(ele => ele.charCodeAt(0))];
    const dataBuffer = [...arg.map(ele => ele.charCodeAt(0)), args.DIRECTION, args.SPEED];
    util.runtime.writeToPeripheral(dataBuffer, 9, 2);
    return util.setSendDelay();
};

const stopMotor = (args, util) => {
    const arg = args.MOTOR.split('');
    const charData = [arg.map(ele => ele.charCodeAt(0))];
    const dataBuffer = [charData, 4, 0];
    util.runtime.writeToPeripheral(dataBuffer, 9, 2);
    return util.setSendDelay();
};

const moveServo = (args, util) => {
    const arg = [args.SERVO_CHANNEL, args.ANGLE];
    util.runtime.writeToPeripheral(arg, 33, 2);
    return util.setSendDelay();
};

const runRobot = (args, util,parent) => {
    const arg = [args.DIRECTION, args.SPEED];
    util.runtime.writeToPeripheral(arg, 147, 2);
    return util.setSendDelay();
};

const runRobotTimed = (args, util, parent) => {
    const bytes = new Uint8Array(new Float32Array([args.TIME]).buffer)
    const arg = [args.DIRECTION, args.SPEED, ...bytes];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(arg, 155, 1);
        parent._isSerialRead = resolve;
    });
};

// const turnRobot = (args, util) => {
//     //const arg = [args.DIRECTION, args.SPEED];
//     let delay = Math.floor(args.ANGLE * (2.721 - 0.02967 * args.SPEED + 0.00013939 * args.SPEED * args.SPEED) * 5.555);
//     const arg = [args.DIRECTION, args.SPEED, ...util.short2array(delay)];
//     console.log(arg);
//     util.runtime.writeToPeripheral(arg, 144, 2);
//     return util.setSendDelay();
//     // const arg = [args.SERVO_CHANNEL, args.ANGLE];
//     // util.runtime.writeToPeripheral(arg, 33, 2);
//     // return util.setSendDelay();
// };

const stopRobot = (args, util,parent) => {
    // const arg = [args.SERVO_CHANNEL, args.ANGLE];
    util.runtime.writeToPeripheral(0, 145, 2);
    return util.setSendDelay();
};

const setLineFollowerParameter = (args, util) => {
    //console.log("setLineFollowerParameter");
    const arg = [args.FORWARD, args.TURNING1, args.TURNING2];
    util.runtime.writeToPeripheral(arg, 146, 2);
    return util.setSendDelay();
};

const doLineFollowing = (args, util, parent) => {
    if (parent.runtime.getVMPreStoreData().irL_digital && parent.runtime.getVMPreStoreData().irR_digital) {
        // set calledDoLineFollowingSerialRead tp false for response true because startBranch will terminate this function
        util.startBranch(1, false);
    }
    else {
        util.runtime.writeToPeripheral(0, 151, 2);
        return util.setDelay(80);
    }
};

const robotOreintation = (args, util, parent) => {
    const arg = [args.DIRECTION];
    util.runtime.writeToPeripheral(arg, 159, 2);
    return util.setSendDelay();
};


module.exports = {
    runMotor,
    stopMotor,
    moveServo,
   // turnRobot,
    stopRobot,
    setLineFollowerParameter,
    doLineFollowing,
    runRobot,
    runRobotTimed,
    robotOreintation
};
