const constants = require('../../../engine/scratch-blocks-constants');
const { MATHSLIDER100 } = require('../../../extension-support/argument-type');

const runRobot = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.runRobot(${args.DIRECTION}, ${args.SPEED});\n`;
        util.runtime.codeGenerateHelper(command);
};

const runRobotTimed = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.runRobot(${args.DIRECTION}, ${args.SPEED});\ndelay(1000*${args.TIME});\nQuarky.stopRobot();\n`;
        util.runtime.codeGenerateHelper(command);
};

// const turnRobot = (args, util) => {
//         util.runtime.include.add(`#include <quarky.h>\n`);
//         util.runtime._setup.add(`Quarky.begin();\n`);
//         const command = `Quarky.runRobot(${args.DIRECTION} + 2, ${args.SPEED});\n`;
//         let delay = Math.floor(args.ANGLE*(2.721 - 0.02967*args.SPEED + 0.00013939*args.SPEED*args.SPEED)*5.555);
//         const command2 = `delay(${delay});`;
//         const command3 = `Quarky.stopRobot();\n`;
//         util.runtime.codeGenerateHelper(command);
//         util.runtime.codeGenerateHelper(command2);
//         util.runtime.codeGenerateHelper(command3);
// };

const stopRobot = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.stopRobot();\n`;
        util.runtime.codeGenerateHelper(command);
};

const setLineFollowerParameter = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.setLineFollowerParameter(${args.FORWARD}, ${args.TURNING1}, ${args.TURNING2});\n`;
        util.runtime.codeGenerateHelper(command);
};

const doLineFollowing = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);

        const command = `if(!(Quarky.isSensorActive(35) && Quarky.isSensorActive(34))) {\n`;
        util.runtime.codeGenerateHelper(command);

        util.runtime.increaseNestBlockCount();
        const blockID = util.getThisBlock();
        util.thread.pushStack(constants.HARDWARE_CLOSE_BRACE);
        util.startBranch(1, false, blockID);
        const ifBlock = `Quarky.doLineFollowing();\n`;
        util.runtime.codeGenerateHelper(ifBlock);
        return doElseLineFollowing(args, util);
};

const doElseLineFollowing = (args, util) => {
        util.runtime.decreaseNestBlockCount();
        const command = `} else { \n`;
        util.runtime.codeGenerateHelper(command);
};

const runMotor = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = (args.DIRECTION === '1' ? `Motor${args.MOTOR}.moveMotor(FORWARD,${args.SPEED});\n` : `Motor${args.MOTOR}.moveMotor(BACKWARD,${args.SPEED});\n`);
        util.runtime.codeGenerateHelper(command);
};

const stopMotor = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Motor${args.MOTOR}.freeMotor();\n`;
        util.runtime.codeGenerateHelper(command);
};

const robotOreintation = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        util.runtime._setup.add(`Quarky.begin();\n`);
        const command = `Quarky.setOreintation(${args.DIRECTION});\n`;
        util.runtime.codeGenerateHelper(command);
};

const moveServo = (args, util) => {
        util.runtime.include.add(`#include <quarky.h>\n`);
        const _define = `Servo Servo${args.SERVO_CHANNEL};\n`;
        const _setup = `Servo${args.SERVO_CHANNEL}.attach(${args.SERVO_CHANNEL});\n`;
        util.runtime.define.add(_define);
        util.runtime._setup.add(_setup);
        const command = `Servo${args.SERVO_CHANNEL}.write(${args.ANGLE});\n`;
        util.runtime.codeGenerateHelper(command);
};

module.exports = {
        runMotor,
        stopMotor,
        moveServo,
        runRobot,
        stopRobot,
        setLineFollowerParameter,
        doLineFollowing,
        doElseLineFollowing,
       // turnRobot,
        runRobotTimed,
        robotOreintation
};
