const buttonPressed = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.readPushButton(${args.BUTTON})`;
    return command;
}

const getIRValue = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.getIRValue(${args.SENSOR})`;
    return command;
}

const setIRThreshold = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.setIRThreshold(${args.SENSOR},${args.VALUE});\n`;
    util.runtime.codeGenerateHelper(command);
}

const getIRSensorState = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = args.SENSOR ===37?`(Quarky.isSensorActive(IR1)&&Quarky.isSensorActive(IR2))`:`Quarky.isSensorActive(${args.SENSOR})`;
    return command;
}

const getTouchSensorState = (args, util) => {
    return `Quarky.isPinTouched(TP_${args.TOUCHPIN}.pin)`;
};

const readUltrasonic = (args, util) => {
    return `getDistance(trig_${args.SENSOR}, echo_${args.SENSOR})`;
};

const defineUltrasonic = (args, util) => {
    util.runtime.define.add(`float getDistance(int trig,int echo){\n\tpinMode(trig,OUTPUT);\n\tdigitalWrite(trig,LOW);\n\tdelayMicroseconds(2);\n\tdigitalWrite(trig,HIGH);\n\tdelayMicroseconds(10);\n\tdigitalWrite(trig,LOW);\n\tpinMode(echo, INPUT);\n\treturn pulseIn(echo, HIGH)/58.0;\n}\n`);
    util.runtime.define.add(`int trig_${args.SENSOR} = ${args.TRIG_PIN};\n`);
    util.runtime.define.add(`int echo_${args.SENSOR} = ${args.ECHO_PIN};\n`);
};

const readAnalogSensor = (args, util) => {

    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `analogRead(${args.PIN})`;
};

const readDigitalSensor = (args, util) => {

    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `digitalRead(${args.PIN})`;

};
module.exports = {
    buttonPressed,
    getIRValue,
    setIRThreshold,
    getIRSensorState,
    readUltrasonic,
    readAnalogSensor,
    readDigitalSensor,
    getTouchSensorState,
    defineUltrasonic  
};
