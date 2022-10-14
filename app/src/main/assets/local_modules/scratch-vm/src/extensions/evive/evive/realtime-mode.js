const tactileSwitch = (args, util, parent) => {
    const dataBuffer = [args.TACTILE_SWITCH, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
        parent._isSerialRead = resolve;
    });
};

const slideSwitch = (args, util, parent) => {
    let pin;
    switch (args.SLIDE_SWITCH){
    case '1':
        if (args.SWITCH_DIRECTION === 'up'){
            pin = 40;
        } else {
            pin = 41;
        }
        break;
    case '2':
        if (args.SWITCH_DIRECTION === 'up') {
            pin = 42;
        } else {
            pin = 43;
        }
        break;
    }
    const dataBuffer = [pin, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
        parent._isSerialRead = resolve;
    });
};

const navigationKey = (args, util, parent) => {
    const dataBuffer = [args.NAV_DIRECTION, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 45, 1);
        parent._isSerialRead = resolve;
    });
};

const potentiometer = (args, util, parent) => {
    const dataBuffer = [args.POTENTIOMETER, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 31, 1);
        parent._isSerialRead = resolve;
    });
};

const touchSensor = (args, util, parent) => {
    const dataBuffer = [args.TOUCH_PINS, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 44, 1);
        parent._isSerialRead = resolve;
    });
};

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

const playTone = (args, util, parent) => {
    const dataBuffer = [args.PIN, ...util.short2array(args.NOTE), ...util.short2array(args.BEATS)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 34, 1);
        parent._isSerialRead = resolve;
    });
};

const setClockTime = (args, util) => {
    const arg = [args.HRS, args.MINS, args.SECS];
    util.runtime.writeToPeripheral(arg, 48, 2);
    return util.setSendDelay();
};

const setClockDate = (args, util) => {
    const arg = [args.DATE, args.MONTH, args.YEAR, args.WEEKDAY];
    util.runtime.writeToPeripheral(arg, 47, 2);
    return util.setSendDelay();
};

const getDataFromClock = (args, util, parent) => {
    const dataBuffer = [args.TIME];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 46, 1);
        parent._isSerialRead = resolve;
    });
};

const voltageSense = (args, util, parent) => {
    const dataBuffer = [args.CHANNEL];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 79, 1);
        parent._isSerialRead = resolve;
    });
};

const currentSense = (args, util, parent) => {
    const dataBuffer = [5];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 78, 1);
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


module.exports = {
    tactileSwitch,
    slideSwitch,
    navigationKey,
    potentiometer,
    touchSensor,
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    playTone,
    setClockTime,
    setClockDate,
    getDataFromClock,
    voltageSense,
    currentSense,
    readTimer,
    resetTimer,
    cast,
    map
};
