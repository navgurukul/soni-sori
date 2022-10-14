const initializeFingerprintSensor = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_Fingerprint.h>\n`);
    util.runtime.define.add(`SoftwareSerial mySerial(${args.RXPIN}, ${args.TXPIN});\n`);
    util.runtime.define.add(`Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial);\n`);
    util.runtime._setup.add(`finger.begin(57600);\n`);
};

const isSensorConnected = (args, util) => {
    return `finger.verifyPassword()`;
};

const getFingerprintImage = (args, util) => {
    return `finger.getImage()`;
};

const convertImageToTemplateSlot = (args, util) => {
    return `finger.image2Tz(${args.SLOT})`;
};

const createFingerprintModel = (args, util) => {
    if (args.OPERATION1 === '1'){
        return `finger.createModel()`;
    } 
    if (args.OPERATION1 === '2') {
        return `finger.getModel()`;
    }
    if (args.OPERATION1 === '3') {
        return `finger.templateCount`;
    }
    if (args.OPERATION1 === '4') {
        return `finger.fingerID`;
    }
    if (args.OPERATION1 === '5') {
        return `finger.confidence`;
    }
    if (args.OPERATION1 === '6') {
        return `finger.fingerFastSearch()`;
    }
};

const fingerprintModel = (args, util) => {
    if (args.OPERATION === '1'){
        return `finger.storeModel(${args.SLOTID})`;
    } 
    if (args.OPERATION === '2') {
        return `finger.loadModel(${args.SLOTID})`;
    }
    if (args.OPERATION === '3') {
        return `finger.deleteModel(${args.SLOTID})`;
    }
};

const fingerprintModelOperation2 = (args, util) => {
    let command = ``;
    if (args.OPERATION === '1'){
        command = `finger.getTemplateCount();\n`;
    } 
    if (args.OPERATION === '3') {
        command = `finger.emptyDatabase();\n`;
    }
    util.runtime.codeGenerateHelper(command);
};

const getTemperatureReading = (args, util) => {
    util.runtime.include.add(`#include <OneWire.h>\n`);
    util.runtime.include.add(`#include <DallasTemperature.h>\n`);
    util.runtime.define.add(`OneWire oneWire(${args.PIN});\n`);
    util.runtime.define.add(`DallasTemperature tempSensor(&oneWire);\n`);
    util.runtime._setup.add(`tempSensor.begin();\n`);
    util.runtime._loop.add(`tempSensor.requestTemperatures();\n`);
    return `tempSensor.getTempCByIndex(0)`;
};

const initialiseRFID = (args, util) => {
    util.runtime._setup.add(`SPI.begin();\nrfid.PCD_Init();\n`);
    util.runtime.define.add(`MFRC522 rfid(${args.SSPIN}, ${args.RESETPIN});\n`);
    util.runtime.include.add(`#include <MFRC522.h>\n`);
};

const setMasterRFID = (args, util) => {
    const command = `rfid.setRFIDMasterTag();\n`;
	util.runtime.codeGenerateHelper(command);

};

const isMasterRFID = (args, util) => {
    return `rfid.isMasterTag()`;
};

const getFromRFID = (args, util) => {
    const func = (args.RFIDCHOICE === '1' ? 'getMasterRFIDtag()' : 'getRFIDTag()');
    return `rfid.${func}`;
};

const initialiseKeypad = (args, util) => {
    util.runtime.include.add(`#include <Keypad.h>\n`);
    util.runtime.define.add(`char keyvalues[16] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '#', 'A', 'B', 'C', 'D'};\n`);
    util.runtime.define.add(`char keymap[4][4] = {{'1', '2', '3', 'A'},  {'4', '5', '6', 'B'}, {'7', '8', '9', 'C'},  {'*', '0', '#', 'D'}};\n`);
    util.runtime.define.add(`byte rowPins[4] = {${args.RPIN1}, ${args.RPIN2}, ${args.RPIN3}, ${args.RPIN4}};\n`);
    util.runtime.define.add(`byte colPins[4] = {${args.CPIN1}, ${args.CPIN2}, ${args.CPIN3}, ${args.CPIN4}};\n`);
    util.runtime.define.add(`Keypad myKeypad = Keypad(makeKeymap(keymap), rowPins, colPins, 4, 4);\n`);
};

const checkKeyPressed = (args, util) => {
    return `((char)myKeypad.getKey() == keyvalues[${args.KEY}])`;
};

const getKeyPressed = (args, util) => {
    return `(char)myKeypad.getKey()`;
};

/*const writeEEPROM = (args, util) => {
    util.runtime.include.add(`#include <EEPROM.h>\n`);
    const command = `EEPROM.writeEEPROMString(${args.SLOT}, ${args.DATA});\ndelay(10);\n`;
	util.runtime.codeGenerateHelper(command);

};

const readEEPROM = (args, util) => {
    util.runtime.include.add(`#include <EEPROM.h>\n`);
    return `EEPROM.readEEPROMString(${args.SLOT})`;
};

const getEEPROMSlot = (args, util) => {
    util.runtime.include.add(`#include <EEPROM.h>\n`);
    return `EEPROM.getSlot(${args.DATA})`;
};*/

const getIMUSensorData = (args, util) => {
    util.runtime.include.add(`#include <MPU6050.h>\n`);
    util.runtime.define.add(`MPU6050 IMUSensor;\n`);
    util.runtime._setup.add(`IMUSensor.init();\n`);
    util.runtime._loop.add(`IMUSensor.readDataFromSensor(${args.WEIGHT});\n`);
    return `IMUSensor.getData(${args.CHOICE})`;
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
    getKeyPressed,
    //writeEEPROM,
    //readEEPROM,
    //getEEPROMSlot,
    getIMUSensorData
};
