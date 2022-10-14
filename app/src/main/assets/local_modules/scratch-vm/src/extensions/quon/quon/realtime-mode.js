
const digitalRead = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
        parent._isSerialRead = resolve;
    });
};

const analogRead = (args, util, parent) => {
    const dataBuffer = [args.ANALOG_PIN, 0];
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

const tactileSwitch = (args, util, parent) => {
    const dataBuffer = [0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 101, 1);
        parent._isSerialRead = resolve;
    });
};

const slideSwitch = (args, util, parent) => {
    let direction;
    if (args.SWITCH_DIRECTION === 'up'){
        direction = 1;
    } else {
        direction = 2;
    }
    const dataBuffer = [direction, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 102, 1);
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
    const dataBuffer = [32, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer,31, 1);
        parent._isSerialRead = resolve;
    });
};

// const readTouchPin = (args, util,parent) => {
//     const dataBuffer = [args.TOUCHSENSORPIN, 0];
//     return new Promise(resolve => {
//         util.runtime.writeToPeripheral(dataBuffer, 115, 1);
//         parent._isSerialRead = resolve;
//     });
// };

// const readMPUPin = (args, util, parent) => {
//     const dataBuffer = [args.MPUAXIS, 0];
//     return new Promise(resolve => {
//         util.runtime.writeToPeripheral(dataBuffer, 114, 1);
//         parent._isSerialRead = resolve;
//     });
// };



const map = (args, util) => {
    return eval((args.VALUE - args.RANGE11) * (args.RANGE22 - args.RANGE21) / (args.RANGE12 - args.RANGE11) + args.RANGE21);
};

const cast = (args, util) => {
    if (args.OPERATION === '1'){
        return Math.ceil(args.VALUE);
    } 
    if (args.OPERATION === '2') {
        return args.VALUE;
    }
};


module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    tactileSwitch,
    slideSwitch,
    navigationKey,
    potentiometer,
    //readTouchPin,
    //readMPUPin,
    map,
    cast/*,
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
