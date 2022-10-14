const digitalRead = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
        parent._isSerialRead = resolve;
    });
};

const analogRead = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 31, 1);
        parent._isSerialRead = resolve;
    });
};

const digitalWrite = (args, util) => {
    const dataBuffer = [args.PIN, (args.MODE === 'true')];
    util.runtime.writeToPeripheral(dataBuffer, 30, 2);
    return util.setSendDelay();
};

const setPWM = (args, util) => {
    const arg = [args.PIN, args.VALUE];
    util.runtime.writeToPeripheral(arg, 32, 2);
    return util.setSendDelay();
};

const playTone = (args, util) => {
    const dataBuffer = [args.PIN, ...util.short2array(args.NOTE), ...util.short2array(args.BEATS)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 34, 1);
        parent._isSerialRead = resolve;
    });
};

const readTimer = (args, util) => {
    return util.ioQuery('clock', 'projectTimer');
};

const resetTimer = (args, util) => {
    util.ioQuery('clock', 'resetProjectTimer');
};

const cast = (args, util) => {
    if (args.OPERATION === '1'){
        return Math.ceil(args.VALUE);
    } 
    if (args.OPERATION === '2') {
        return args.VALUE;
    }
    
};

const map = (args, util) => {
    return eval((args.VALUE - args.RANGE11) * (args.RANGE22 - args.RANGE21) / (args.RANGE12 - args.RANGE11) + args.RANGE21);
};

const isSensorTouched = (args, util, parent) => {
    const dataBuffer = [args.SENSORPIN,args.STATUS, ...util.short2array(args.SENSORTHRESHOLD)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 131, 1);
        parent._isSerialRead = resolve;
    });
};

module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    playTone,
    readTimer,
    resetTimer,
    cast,
    map,
    isSensorTouched
};
