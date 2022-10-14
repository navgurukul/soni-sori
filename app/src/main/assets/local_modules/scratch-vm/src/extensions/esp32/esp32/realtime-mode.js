const digitalRead = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    // return new Promise(resolve => {
    //     util.runtime.writeToPeripheral(dataBuffer, 30, 1);
    //     parent._isSerialRead = resolve;
    // });
    return parent.runtime.getVMPreStoreData(args.PIN);
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

const getMacAddr = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(0, 131, 1);
        parent._isSerialRead = resolve;
    });
};

const touchRead = (args, util, parent) => {
    const dataBuffer = [args.TOUCHPIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 132, 1);
        parent._isSerialRead = resolve;
    });
};

const hallRead = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(0, 133, 1);
        parent._isSerialRead = resolve;
    });
};

const map = (args, util) => {
    return eval((args.VALUE - args.RANGE11) * (args.RANGE22 - args.RANGE21) / (args.RANGE12 - args.RANGE11) + args.RANGE21);
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
    touchRead,
    hallRead,
    setPWM,
    getMacAddr,
    map
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
