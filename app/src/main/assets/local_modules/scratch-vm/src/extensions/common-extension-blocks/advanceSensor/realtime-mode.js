const initializeFingerprintSensor = (args, util, parent) => {
};

const isSensorConnected = (args, util, parent) => {
};

const getFingerprintImage = (args, util, parent) => {
};

const convertImageToTemplateSlot = (args, util, parent) => {
};

const createFingerprintModel = (args, util, parent) => {
};

const fingerprintModel = (args, util, parent) => {
};

const fingerprintModelOperation2 = (args, util, parent) => {
};

const getTemperatureReading = (args, util, parent) => {
};

const initialiseRFID = (args, util, parent) => {
    const dataBuffer = [args.SSPIN, args.RESETPIN];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 49, 1);
        parent._isSerialRead = resolve;
    });
};

const setMasterRFID = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral([], 51, 1);
        parent._isSerialRead = resolve;
    });
};

const isMasterRFID = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral([], 52, 1);
        parent._isSerialRead = resolve;
    });
};

const getFromRFID = (args, util, parent) => {
    const dataBuffer = [args.RFIDCHOICE];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 53, 1);
        parent._isSerialRead = resolve;
    });
};

/*const writeEEPROM = (args, util, parent) => {
    const dataBuffer = [args.SLOT, args.DATA];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 54, 1);
        parent._isSerialRead = resolve;
    });
};

const readEEPROM = (args, util, parent) => {
    const dataBuffer = [args.SLOT];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 55, 1);
        parent._isSerialRead = resolve;
    });
};

const getEEPROMSlot = (args, util, parent) => {
    const dataBuffer = [args.DATA];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 56, 1);
        parent._isSerialRead = resolve;
    });
};*/

const initialiseKeypad = (args, util, parent) => {
    const dataBuffer = [args.RPIN1, args.RPIN2, args.RPIN3, args.RPIN4, args.CPIN1, args.CPIN2, args.CPIN3, args.CPIN4];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 57, 1);
        parent._isSerialRead = resolve;
    });
};

const checkKeyPressed = (args, util, parent) => {
    const dataBuffer = [args.KEY];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 58, 1);
        parent._isSerialRead = resolve;
    });
};

const getKeyPressed = (args, util, parent) => {
    return new Promise(resolve => {
        util.runtime.writeToPeripheral([], 59, 1);
        parent._isSerialRead = resolve;
    });
};

module.exports = {
    initializeFingerprintSensor,
    isSensorConnected,
    getFingerprintImage,
    convertImageToTemplateSlot,
    createFingerprintModel,
    fingerprintModel,
    fingerprintModelOperation2,
    getTemperatureReading,
    initialiseRFID,
    setMasterRFID,
    isMasterRFID,
    getFromRFID,
    initialiseKeypad,
    checkKeyPressed,
    getKeyPressed
    //writeEEPROM,
    //readEEPROM,
    //getEEPROMSlot
};
