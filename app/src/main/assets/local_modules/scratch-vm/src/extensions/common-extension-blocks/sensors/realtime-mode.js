const quonUltrasonic = (args, util, parent) => {
    const dataBuffer = [args.PORT, args.ECHO_PIN];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 36, 1);
        parent._isSerialRead = resolve;
    });
};

const readUltrasonic = (args, util, parent) => {
    const dataBuffer = [args.TRIG_PIN, args.ECHO_PIN];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 36, 1);
        parent._isSerialRead = resolve;
    });
};

const readDHTSensor = (args, util, parent) => {
    const dataBuffer = [args.PIN, args.DHT_SENSOR];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 42, 1);
        parent._isSerialRead = resolve;
    });
};

const quonDHTSensor = (args, util, parent) => {
    const dataBuffer = [args.PIN, args.DHT_SENSOR];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 42, 1);
        parent._isSerialRead = resolve;
    });
};

const readAnalogSensor = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 31, 1);
        parent._isSerialRead = resolve;
    });
};

const readDigitalSensor = (args, util, parent) => {
    const dataBuffer = [args.PIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 30, 1);
        parent._isSerialRead = resolve;
    });
};

const tWatchStepCount = (args, util, parent) => {
    console.log("Step count T-watch");
};

const readTouchPin = (args, util, parent) => {
    const dataBuffer = [args.TOUCHSENSORPIN, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 115, 1);
        parent._isSerialRead = resolve;
    });
};

const readMPUPin = (args, util, parent) => {
    const dataBuffer = [args.MPUAXIS, 0];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 114, 1);
        parent._isSerialRead = resolve;
    });
};

module.exports = {
    quonUltrasonic,
    quonDHTSensor,
    readUltrasonic,
    readDHTSensor,
    readAnalogSensor,
    readDigitalSensor,
    tWatchStepCount,
    readTouchPin,
    readMPUPin
};