let btType =  1;
const getBoardId = require('../../../util/board-config.js').getBoardId;

const setSerialPin = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon"|| boardSelected === "quarky") {
        util.runtime.define.add(`#define RX${args.SERIALPORT} ${args.SERIALRX}\n`);
        util.runtime.define.add(`#define TX${args.SERIALPORT} ${args.SERIALTX}\n`);
    }
};

const setBaudRate = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoNano"  || boardSelected === "tecBits") {
        util.runtime._setup.add(`Serial.begin(${args.BAUDRATE});\n`);
    }
    else if (boardSelected === "evive" || boardSelected === "arduinoMega" ){
        util.runtime._setup.add(`Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.begin(${args.BAUDRATE});\n`);
    }

    else if(boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon"|| boardSelected === "quarky")
    {
        util.runtime._setup.add(`Serial${(args.SERIAL === '0' ? `.begin(${args.BAUDRATE})` : `${args.SERIAL}.begin(${args.BAUDRATE},SERIAL_8N1,RX${args.SERIAL},TX${args.SERIAL})`)};\n`);
    }
};

const serialRead = (args, util) => {
    return `Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.read()`;
};

const getNumber = (args, util) => {
    return `Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.parseFloat()`;
};

const readString = (args, util) => {
    return `Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.readString()`;
};

const serialAvailable = (args, util) => {
    return `Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.available()`;
};

const writeToSerial = (args, util) => {
    const command = `Serial${(args.SERIAL === '0' ? '' : args.SERIAL)}.println(${args.DATA});\n`;
	util.runtime.codeGenerateHelper(command);

};

const configureBluetooth = (args, util) => {
    btType = args.BT_TYPE;
    const func = (btType === '1' ? '<BluetoothSerial.h>\n':'<esp32BLEUtilities.h>\n');
    util.runtime.include.add(`#include ${func}\n`);
    const _define = (btType === '1' ? 'BluetoothSerial esp32BT;\n':'Esp32ble esp32BLE;\n');
    util.runtime.define.add(_define);
    const command = (btType === '1' ? `esp32BT.begin(${args.BLUETOOTH_NAME});\n` : `esp32BLE.begin(${args.BLUETOOTH_NAME});\n`);
    util.runtime._setup.add(command);
};

const readBluetoothData = (args, util) => {
    return (btType === '1' ? 'esp32BT.read()': 'esp32BLE.read()');
};

const writeToBluetooth = (args, util) => {
    const command =  (btType === '1' ? `esp32BT.write(${args.DATA});\n` : `esp32BLE.write(${args.DATA});\n`);
	util.runtime.codeGenerateHelper(command);

};

const bluetoothAvailable = (args, util) => {
    return (btType === '1' ? 'esp32BT.available()': 'esp32BLE.available()');
};

module.exports = {
    setSerialPin,
    setBaudRate,
    serialRead,
    getNumber,
    readString,
    serialAvailable,
    writeToSerial,
    configureBluetooth,
    readBluetoothData,
    writeToBluetooth,
    bluetoothAvailable
};
