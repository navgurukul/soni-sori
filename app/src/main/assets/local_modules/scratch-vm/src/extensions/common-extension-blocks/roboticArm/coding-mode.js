const initialise2 = (args, util) => {
    util.runtime.include.add(`#include<RoboticArm.h>\n`);
    util.runtime.define.add(`RoboticArm roboticArm1;\n`);
    util.runtime._setup.add(`roboticArm1.init(${args.BASE}, ${args.LINK1}, ${args.LINK2}, ${args.ROT}, ${args.GRIPPER});\n`);
};

const setTrim = (args, util) => {
    const command = `roboticArm1.setTrims(${args.BASE}, ${args.LINK1}, ${args.LINK2}, ${args.ROT}, ${args.GRIPPER});\n`;
	util.runtime.codeGenerateHelper(command);

};

const gotoXYZ = (args, util) => {
    const command = `roboticArm1.moveToXYZ(${args.XPOS}, ${args.YPOS}, ${args.ZPOS}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const gotoXYZinLine = (args, util) => {
    const command = `roboticArm1.moveInLineToXYZ(${args.XPOS}, ${args.YPOS}, ${args.ZPOS}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setServoAngleTo = (args, util) => {
    const command = `roboticArm1.setServoAngleTo(${args.SERVO}, ${args.ANGLE}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const goToInOneAxis = (args, util) => {
    const command = `roboticArm1.moveInOneDirection(${args.AXIS}, ${args.POSITION}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const moveByInOneAxis = (args, util) => {
    const command = `roboticArm1.moveByInOneDirection(${args.AXIS}, ${args.POSITION}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const getCurrentPosition = (args, util) => {
    return `roboticArm1.getCurrentPosition(${args.POSITION})`;
};

const getServoAngle = (args, util) => {
    return `roboticArm1.getCurrentServoAngle(${args.SERVO})`;
};

const initialiseGripper = (args, util) => {
    const command = `roboticArm1.initialiseGripper(${args.OPEN}, ${args.CLOSE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const controlGripper = (args, util) => {
    const command = (args.STATE === '0' ? `roboticArm1.openGripper();\n` : `roboticArm1.closeGripper();\n`);
	util.runtime.codeGenerateHelper(command);

};

const moveInCircle = (args, util) => {
    const command = (args.DIRECTION === '1' ? `roboticArm1.moveInCircle(${args.XPOS}, ${args.ZPOS}, ${args.YPOS}, ${args.RADIUS}, ${args.SEC});\n` : `roboticArm1.moveInCircleAntiClockWise(${args.XPOS}, ${args.ZPOS}, ${args.YPOS}, ${args.RADIUS}, ${args.SEC});\n`);
	util.runtime.codeGenerateHelper(command);

};

const moveInArc = (args, util) => {
    const command = `roboticArm1.moveInArc(${args.XPOS}, ${args.ZPOS}, ${args.YPOS}, ${args.RADIUS}, ${args.START}, ${args.END}, ${args.SEC});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setOffset = (args, util) => {
    const command = `roboticArm1.setOffset(${args.LENGTH}, ${args.ZOFFSET});\n`;
	util.runtime.codeGenerateHelper(command);

};

const uploadFirmware = (args, util) => {
    util.runtime.include.add(`#include <RoboticArmFirmware.h>\n`);
    util.runtime._setup.add(`RoboticArmFirmwareSetup();\n`);
    util.runtime._loop.add(`RoboticArmFirmwareLoop();\n`);
};

module.exports = {
    initialise2,
    setTrim,
    gotoXYZ,
    gotoXYZinLine,
    setServoAngleTo,
    goToInOneAxis,
    moveByInOneAxis,
    getServoAngle,
    getCurrentPosition,
    initialiseGripper,
    controlGripper,
    moveInCircle,
    moveInArc,
    uploadFirmware,
    setOffset
};