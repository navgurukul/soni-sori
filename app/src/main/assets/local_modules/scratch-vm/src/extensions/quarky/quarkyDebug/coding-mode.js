const constants = require('../../../engine/scratch-blocks-constants');
const { MATHSLIDER100 } = require('../../../extension-support/argument-type');

const setTouchThreshold = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = ` Quarky.setThreshold(&TP_${args.PIN},${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);
};

const resetQuarky = (args, util) => {
    const command = `EEPROM.write(0,0);\nEEPROM.commit();\n`;
	util.runtime.codeGenerateHelper(command);
};

const getTouchValue = (args, util) => {
    return `touchRead(${args.PIN})`;
};

const rawPbvalue = (args, util) => {
    return `analogRead(36)`;
};

const setPbThreshold = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = args.PBUTTON === '1'?`Quarky.setPbLeftThres(${args.LOWER},${args.UPPER});\n`:`Quarky.setPbRightThres(${args.LOWER},${args.UPPER});\n`;
	util.runtime.codeGenerateHelper(command);
};

const motorDirSet = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Motor${args.MOTOR}.reverseMotorDir();\n`;
	util.runtime.codeGenerateHelper(command);
};

module.exports = {
    setTouchThreshold,
    getTouchValue,
    rawPbvalue,
    setPbThreshold,
    motorDirSet,
    resetQuarky
};
