const digitalRead = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `digitalRead(${args.PIN})`;
};

const analogRead = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `analogRead(${args.PIN})`;
};

const digitalWrite = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `digitalWrite(${args.PIN}, ${args.MODE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setPWM = (args, util) => {
    util.runtime.include.add(`#include <esp32PWMUtilities.h>\n`);
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `analogWrite(${args.PIN}, ${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const touchRead = (args, util) => {
    return `touchRead(${args.TOUCHPIN})`;
};

const hallRead = (args, util) => {
    return `hallRead()`;
};


const getMacAddr = (args, util) => {
    return `esp32ble.getMacAddress()`;
};

const map = (args, util) => {
    return `map(${args.VALUE}, ${args.RANGE11}, ${args.RANGE12}, ${args.RANGE21}, ${args.RANGE22})`;
};

module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM ,
    touchRead,
    hallRead,
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
