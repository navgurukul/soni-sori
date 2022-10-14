const Cast = require('../../../util/cast');
const constants = require('../../../engine/scratch-blocks-constants');

const setTouchThreshold = (args, util,parent) => {
    const arg = (args.PIN === 'T1'?1 :args.PIN === 'T2'?2:args.PIN === 'T3'?3:args.PIN === 'T4'?4:5);
    const dataBuffer = [arg, args.VALUE];
    util.runtime.writeToPeripheral(dataBuffer, 160, 2);
    return util.setSendDelay();
};

const getTouchValue = (args, util,parent) => {
    const dataBuffer = [args.PIN];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 161, 1);
        parent._isSerialRead = resolve;
    });
 };

 const rawPbvalue = (args, util,parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(0, 162, 1);
        parent._isSerialRead = resolve;
    });
};

 const resetQuarky = (args, util, parent) => {
    const dataBuffer = [0];
    util.runtime.writeToPeripheral(dataBuffer, 165, 2);
    return util.setSendDelay();
};

 const setPbThreshold = (args, util) => {
    const dataBuffer = [args.PBUTTON, ...util.short2array(args.LOWER),...util.short2array(args.UPPER)];
    util.runtime.writeToPeripheral(dataBuffer, 163, 2);
    return util.setSendDelay();
 };

 const motorDirSet = (args, util) => {
    const dataBuffer = [args.MOTOR];
    util.runtime.writeToPeripheral(dataBuffer, 166, 2);
    return util.setSendDelay();
 };

module.exports = {
    setTouchThreshold,
    getTouchValue,
    rawPbvalue,
    setPbThreshold,
    resetQuarky,
    motorDirSet
};
