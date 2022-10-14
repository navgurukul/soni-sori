const setServoOffset = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.setOffSet_n_n_n_n_n(${args.HEAD}, ${args.FL}, ${args.FR}, ${args.BL}, ${args.BR});\n`;
        util.runtime.codeGenerateHelper(command);
};

const intializeRobot = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.initializeMarsRover();\n`;
        util.runtime.codeGenerateHelper(command);
};

const setServoPosition = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        if (args.POSITION === '1'){
                const command = `Quarky.setInAngle_n(${args.ANGLE});\n`;
                util.runtime.codeGenerateHelper(command);
        }
        if (args.POSITION === '2'){
                const command = `Quarky.setLeftTurnAngle_n(${args.ANGLE});\n`;
                util.runtime.codeGenerateHelper(command);
        }
        if (args.POSITION === '3'){
                const command = `Quarky.setRightTurnAngle_n(${args.ANGLE});\n`;
                util.runtime.codeGenerateHelper(command);
        }
};

const setServoAngle = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.setServoAnglesWheels_n_n_n_n(${args.FL}, ${args.FR}, ${args.BL}, ${args.BR});\n`;
        util.runtime.codeGenerateHelper(command);
};

const setHeadAngle = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.setHeadAngle_n(${args.ANGLE});\n`;
        util.runtime.codeGenerateHelper(command);
};

module.exports = {
        intializeRobot,
        setServoOffset,
        setServoPosition,
        setServoAngle,
        setHeadAngle,
};