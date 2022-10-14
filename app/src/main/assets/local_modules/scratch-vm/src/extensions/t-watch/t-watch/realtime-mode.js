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

const userButton = (args, util, parent) => {
    let pin = 36;
    const dataBuffer = [pin, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
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

const map = (args, util) => {
    return eval((args.VALUE - args.RANGE11) * (args.RANGE22 - args.RANGE21) / (args.RANGE12 - args.RANGE11) + args.RANGE21);
};

const getMacAddr = (args, util,parent) => {
    return new Promise(resolve => {
       util.runtime.writeToPeripheral(0, 131, 1);
       parent._isSerialRead = resolve;
   });
};

const playTone = (args, util) => {
    const arg = [args.PIN, args.NOTE, args.BEATS];
    util.runtime.writeToPeripheral(arg, 34, 2);
    return util.setSendDelay();
};

const deepSleepTimer = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};

const deepSleepExternalSource = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};

const deepSleepTouchPins = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};
////////////////////////////
/*const fillscreen = (args, util, parent) => {
};

const setCursor = (args, util, parent) => {
};

const setTextColorSize = (args, util, parent) => {
};

const write = (args, util, parent) => {
};

const drawLine = (args, util, parent) => {
};

const fillDrawRect = (args, util, parent) => {
};

const fillDrawRoundRect = (args, util, parent) => {
};

const fillDrawCircle = (args, util, parent) => {
};

const fillDrawEllipse = (args, util, parent) => {
};

const fillDrawTriangle = (args, util, parent) => {
};

const displayMatrix3 = (args, util, parent) => {
};*/

module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    setClockTime,
    setClockDate,
    getDataFromClock,
    userButton,
    map,
    getMacAddr,
    playTone,
    deepSleepTimer,
    deepSleepExternalSource,
    deepSleepTouchPins
    /*,
    fillscreen,
    setCursor,
    setTextColorSize,
    write,
    drawLine,
    fillDrawRect,
    fillDrawRoundRect,
    fillDrawCircle,
    fillDrawEllipse,
    fillDrawTriangle,
    displayMatrix3*/
};
