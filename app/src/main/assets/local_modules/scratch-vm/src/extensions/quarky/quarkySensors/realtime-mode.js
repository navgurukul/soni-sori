const buttonPressed = (args, util, parent) => {
    console.log(`parent.runtime.isVMPreStoreDataAvailable(): ${parent.runtime.isVMPreStoreDataAvailable()}`);
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.BUTTON === '1') {
            return parent.runtime.getVMPreStoreData().buttonL;
        }
        else if (args.BUTTON === '2') {
            return parent.runtime.getVMPreStoreData().buttonR;
        }
        else if (args.BUTTON === '3') {
            return parent.runtime.getVMPreStoreData().buttonB;
        }
    }
    return false;
}

const whenButtonPressed = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.BUTTON === '1') {
            return parent.runtime.getVMPreStoreData().buttonL;
        }
        else if (args.BUTTON === '2') {
            return parent.runtime.getVMPreStoreData().buttonR;
        }
        else if (args.BUTTON === '3') {
            return parent.runtime.getVMPreStoreData().buttonB;
        }
    }
    return false;
}

const getIRValue = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.SENSOR === '35') {
            return parent.runtime.getVMPreStoreData().irL_analog;
        }
        else if (args.SENSOR === '34') {
            return parent.runtime.getVMPreStoreData().irR_analog;
        }
    }
    return false;
}

const setIRThreshold = (args, util, parent) => {
    const arg = [args.SENSOR, ...util.short2array(args.VALUE)];
    console.log(arg);
    util.runtime.writeToPeripheral(arg, 140, 2);
    return util.setSendDelay();
}

const getIRSensorState = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.SENSOR === '35') {
            return parent.runtime.getVMPreStoreData().irL_digital;
        }
        else if (args.SENSOR === '34') {
            return parent.runtime.getVMPreStoreData().irR_digital;
        }
    }
    return false;
}

const getTouchSensorState = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.TOUCHPIN === 'T1') {
            return parent.runtime.getVMPreStoreData().touch[0];
        }
        else if (args.TOUCHPIN === 'T2') {
            return parent.runtime.getVMPreStoreData().touch[1];
        }
        else if (args.TOUCHPIN === 'T3') {
            return parent.runtime.getVMPreStoreData().touch[2];
        }
        else if (args.TOUCHPIN === 'T4') {
            return parent.runtime.getVMPreStoreData().touch[3];
        }
        else if (args.TOUCHPIN === 'T5') {
            return parent.runtime.getVMPreStoreData().touch[4];
        }
    }
    return false;
}

const whenTouchPressed = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.TOUCHPIN === '15') {
            return parent.runtime.getVMPreStoreData().touch[0];
        }
        else if (args.TOUCHPIN === '13') {
            return parent.runtime.getVMPreStoreData().touch[1];
        }
        else if (args.TOUCHPIN === '12') {
            return parent.runtime.getVMPreStoreData().touch[2];
        }
        else if (args.TOUCHPIN === '14') {
            return parent.runtime.getVMPreStoreData().touch[3];
        }
        else if (args.TOUCHPIN === '27') {
            return parent.runtime.getVMPreStoreData().touch[4];
        }
    }
    return false;
}

const readUltrasonic = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.SENSOR === '1') {
            return parent.runtime.getVMPreStoreData().ultrasonic1;
        }
        else if (args.SENSOR === '2') {
            return parent.runtime.getVMPreStoreData().ultrasonic2;
        }
    }
    return false;
}

const defineUltrasonic = (args, util, parent) => {
    const dataBuffer = [args.SENSOR, args.TRIG_PIN, args.ECHO_PIN];
    util.runtime.writeToPeripheral(dataBuffer, 167, 2);
    return util.setSendDelay();
}

const readAnalogSensor = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.PIN === '33') {
            return parent.runtime.getVMPreStoreData().analog1;
        }
        else if (args.PIN === '32') {
            return parent.runtime.getVMPreStoreData().analog2;
        }
        else if (args.PIN === '39') {
            return parent.runtime.getVMPreStoreData().analog3;
        }
    }
    return false;
}

const readDigitalSensor = (args, util, parent) => {
    if (parent.runtime.isVMPreStoreDataAvailable()) {
        if (args.PIN === '18') {
            return parent.runtime.getVMPreStoreData().digital[0];
        }
        else if (args.PIN === '19') {
            return parent.runtime.getVMPreStoreData().digital[1];
        }
        else if (args.PIN === '26') {
            return parent.runtime.getVMPreStoreData().digital[2];
        }
    }
    return false;
}

module.exports = {
    buttonPressed,
    getIRValue,
    setIRThreshold,
    getIRSensorState,
    readUltrasonic,
    readAnalogSensor,
    readDigitalSensor,
    whenButtonPressed,
    getTouchSensorState,
    whenTouchPressed,
    defineUltrasonic
};
