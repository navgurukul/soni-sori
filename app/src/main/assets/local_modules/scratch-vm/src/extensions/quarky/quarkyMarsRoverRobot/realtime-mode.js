const Cast = require('../../../util/cast');
const constants = require('../../../engine/scratch-blocks-constants');

let robotOreintationMode = 1;

let fixedDelayUSB = 10;
let fixedDelayBLE = 150;

const setServoOffset = (args, util,parent) => {
    const arg = [args.HEAD, args.FL, args.FR, args.BL, args.BR,];
    util.runtime.writeToPeripheral(arg, 173, 2);
    util.setSendDelay();
};

const intializeRobot = (args, util,parent) => {
    util.runtime.writeToPeripheral([], 168, 2);
    util.setSendDelay();
};

const setServoPosition = (args, util,parent) => {
    const arg = [args.POSITION, args.ANGLE];
    util.runtime.writeToPeripheral(arg, 169, 2);
    util.setSendDelay();
};

const setServoAngle = (args, util,parent) => {
    const arg = [args.FL, args.FR, args.BL, args.BR,];
    util.runtime.writeToPeripheral(arg, 172, 2);
    util.setSendDelay();
};

const setHeadAngle = (args, util,parent) => {
    const arg = [args.ANGLE];
    util.runtime.writeToPeripheral(arg, 171, 2);
    util.setSendDelay();
};

module.exports = {
    intializeRobot,
    setServoOffset,
    setServoPosition,
    setServoAngle,
    setHeadAngle,
};