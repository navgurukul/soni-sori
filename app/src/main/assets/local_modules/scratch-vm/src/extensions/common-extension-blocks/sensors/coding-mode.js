const getBoardId = require('../../../util/board-config.js').getBoardId;

const quonUltrasonic = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`);
    return `Quon.getDistance(${args.PORT})`;
};

const quonDHTSensor = (args, util) => {
    util.runtime._setup.add(`dht_${args.PIN}.begin();\n`);
    util.runtime.define.add(`#define DHTPIN_${args.PIN} ${args.PIN}\n#define DHTTYPE DHT11\nDHT dht_${args.PIN}(DHTPIN_${args.PIN}, DHTTYPE);\n`);
    util.runtime.include.add(`#include <DHT.h>\n`);
    const func = (args.DHT_SENSOR === '2' ? 'readHumidity()' : 'readTemperature()');
    return `dht_${args.PIN}.${func}`;
};

const readUltrasonic = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quarky" || boardSelected === "tecBits") {
        util.runtime.define.add(`float getDistance(int trig,int echo){\n\tpinMode(trig,OUTPUT);\n\tdigitalWrite(trig,LOW);\n\tdelayMicroseconds(2);\n\tdigitalWrite(trig,HIGH);\n\tdelayMicroseconds(10);\n\tdigitalWrite(trig,LOW);\n\tpinMode(echo, INPUT);\n\treturn pulseIn(echo, HIGH)/58.0;\n}\n`);
        return `getDistance(${args.TRIG_PIN}, ${args.ECHO_PIN})`;
    }
};

const readDHTSensor = (args, util) => {
    util.runtime._setup.add(`dht_${args.PIN}.begin();\n`);
    util.runtime.define.add(`#define DHTPIN_${args.PIN} ${args.PIN}\n#define DHTTYPE DHT11\nDHT dht_${args.PIN}(DHTPIN_${args.PIN}, DHTTYPE);\n`);
    util.runtime.include.add(`#include <DHT.h>\n`);
    const func = (args.DHT_SENSOR === '2' ? 'readHumidity()' : 'readTemperature()');
    return `dht_${args.PIN}.${func}`;
};

const readAnalogSensor = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quarky" || boardSelected === "tecBits") {
        util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
        return `analogRead(${args.PIN})`;
    }
    else if (boardSelected === "quon") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime._setup.add(`Quon.begin();\n`);
        util.runtime._setup.add(`Quon.pinMode(${args.PIN}, INPUT);\n`);
        return `Quon.analogRead(${args.PIN})`;
    }
};

const readDigitalSensor = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "arduinoUno" || boardSelected === "arduinoMega" || boardSelected === "arduinoNano" || boardSelected === "evive" || boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quarky" || boardSelected === "tecBits") {
        util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
        return `digitalRead(${args.PIN})`;
    }
    else if (boardSelected === "quon") {
        util.runtime._setup.add(`Quon.pinMode(${args.PIN}, INPUT);\n`);
        return `Quon.digitalRead(${args.PIN})`;
    }
};

const tWatchStepCount = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "tWatch") {
        // util.runtime.define.add(`TTGOClass *ttgo;\n char buf[128];\n
        // bool irq = false;\nint stepCountValue = 0;\n`);
        util.runtime.include.add(`#include <TTGO.h>\n`);
        util.runtime.define.add(`TTGOClass *ttgo;\n`);
        util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
        util.runtime.define.add(`char buf[128];\n
        bool irq = false;\nint stepCountValue = 0;\n`);
        util.runtime.define.add(`TP_Point p;\n`);
        const command = `if (irq) {\nirq = 0;\nbool  rlst;\ndo {  \nrlst =  ttgo->bma->readInterrupt();\n} while (!rlst);\nif (ttgo->bma->isStepCounter()) {\nstepCountValue = ttgo->bma->getCounter();}\n}\n `
        util.runtime.codeGenerateHelper(command);
        return `stepCountValue`;
    }
};

const readTouchPin = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "tWatch") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime._setup.add(`Quon.begin();\n`);
        return `Quon.getTouchValue(${args.TOUCHSENSORPIN})`;
    }
};

const readMPUPin = (args, util) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "quon") {
        util.runtime.include.add(`#include <quon.h>\n`);
        util.runtime.include.add(`#include <MPU6050.h>\n`);
        util.runtime.define.add('MPU6050lib mpu;\n');
        util.runtime.define.add('float gyroBias[3] = {0, 0, 0}, accelBias[3] = {0, 0, 0};\n');
        util.runtime._setup.add(`Quon.begin();\n`);
        util.runtime._setup.add(`mpu.calibrateMPU6050(gyroBias, accelBias);\n`);
        util.runtime._setup.add(`mpu.initMPU6050();\n`);
        return `mpu.getMPURawData(${args.MPUAXIS})`;
    }
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
