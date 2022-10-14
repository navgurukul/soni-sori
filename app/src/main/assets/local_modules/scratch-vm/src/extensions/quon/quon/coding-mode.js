const digitalRead = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);  
    util.runtime._setup.add(`Quon.begin();\n`);
    util.runtime._setup.add(`Quon.pinMode(${args.PIN}, INPUT);\n`);
    return `Quon.digitalRead(${args.PIN})`;
};

const analogRead = (args, util) => {
    util.runtime._setup.add(`Quon.pinMode(${args.ANALOG_PIN}, INPUT);\n`);
    return `Quon.analogRead(${args.ANALOG_PIN})`;
};

const digitalWrite = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    util.runtime._setup.add(`Quon.pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `Quon.digitalWrite(${args.PIN}, ${args.MODE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setPWM = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`);  
    const command = `Quon.analogWrite(${args.PIN}, ${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const tactileSwitch = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`);  
    return `Quon.isTactileSwitchPressed()`
};

const slideSwitch = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = (args.SWITCH_DIRECTION === '1' ? `Quon.isSlideSwitchUp()` : `Quon.isSlideSwitchDown()`); 
    return command;
};

const navigationKey = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    return `Quon.getNavKeyDir(${args.NAV_DIRECTION})`;
};

const potentiometer = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    return `Quon.getPotValue()`;
};

// const readTouchPin = (args, util) => {
//     util.runtime.include.add(`#include <quon.h>\n`);
//     util.runtime._setup.add(`Quon.begin();\n`); 
//     return `Quon.getTouchValue(${args.TOUCHSENSORPIN})`;
// };

// const readMPUPin = (args, util) => {
//     util.runtime.include.add(`#include <quon.h>\n`);
//     util.runtime.include.add(`#include <MPU6050.h>\n`);
//     util.runtime.define.add('MPU6050lib mpu;\n');
//     util.runtime.define.add('float gyroBias[3] = {0, 0, 0}, accelBias[3] = {0, 0, 0};\n');
//     util.runtime._setup.add(`Quon.begin();\n`); 
//     util.runtime._setup.add(`mpu.calibrateMPU6050(gyroBias, accelBias);\n`); 
//     util.runtime._setup.add(`mpu.initMPU6050();\n`);
//     return `mpu.getMPURawData(${args.MPUAXIS})`;
// };

const map = (args, util) => {
    return `map(${args.VALUE}, ${args.RANGE11}, ${args.RANGE12}, ${args.RANGE21}, ${args.RANGE22})`;
};

const cast = (args, util) => {
    if (args.OPERATION === '1'){
        return `((int) ${args.VALUE})`;
    } 
    if (args.OPERATION === '2') {
        return `float(${args.VALUE})`;
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
    cast
};
