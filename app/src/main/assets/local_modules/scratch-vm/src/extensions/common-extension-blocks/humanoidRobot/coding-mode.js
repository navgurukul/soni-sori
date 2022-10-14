const initialiseLeg = (args, util) => {
    util.runtime.include.add(`#include <Servo.h>\n#include <EEPROM.h>\n#include <HumanoidRobot.h>\n`);
    util.runtime.define.add(`int HIP_L = ${args.HIP_L};\nint HIP_R = ${args.HIP_R};\nint FOOT_L = ${args.FOOT_L};\nint FOOT_R = ${args.FOOT_R};\nHumanoidRobot humanoidRobot;\n`);
    util.runtime._setup.add(`humanoidRobot.init(HIP_L, HIP_R, FOOT_L, FOOT_R);\n`);
};

const initialiseHand = (args, util) => {
    util.runtime.define.add(`int SHOULDER_L = ${args.HIP_L};\nint SHOULDER_R = ${args.HIP_R};\nint HAND_L = ${args.FOOT_L};\nint HAND_R = ${args.FOOT_R};\n`);
    util.runtime._setup.add(`humanoidRobot.initHand(SHOULDER_L, SHOULDER_R, HAND_L, HAND_R);\n`);
};

const calibrateLeg = (args, util) => {
    const command = `humanoidRobot.setTrims(${args.HIP_L}, ${args.HIP_R}, ${args.FOOT_L}, ${args.FOOT_R});\nhumanoidRobot.saveTrimsOnEEPROM();\n`;
	util.runtime.codeGenerateHelper(command);

};

const calibrateHand = (args, util) => {
    const command = `humanoidRobot.setTrimsHand(${args.HIP_L}, ${args.HIP_R}, ${args.FOOT_L}, ${args.FOOT_R});\nhumanoidRobot.saveTrimsOnEEPROMHand();\n`;
	util.runtime.codeGenerateHelper(command);

};

const moveHumanoidRobot = (args, util) => {
    const command = `humanoidRobot.move(${args.ACTION}, ${args.SPEED}, ${args.SIZE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const moveHumanoidRobotHand = (args, util) => {
    const command = `humanoidRobot.handGesture(${args.ACTION}, ${args.SPEED});\n`;
	util.runtime.codeGenerateHelper(command);

};

const moveLeg = (args, util) => {
    const command = `humanoidRobot.moveLeg(${args.HIP_L}, ${args.HIP_R}, ${args.FOOT_L}, ${args.FOOT_R}, ${args.DURATION});\n`;
	util.runtime.codeGenerateHelper(command);

};

const moveHand = (args, util) => {
    const command = `humanoidRobot.moveHand(${args.HIP_L}, ${args.HIP_R}, ${args.FOOT_L}, ${args.FOOT_R}, ${args.DURATION});\n`;
	util.runtime.codeGenerateHelper(command);

};

const initializeDotMatrixDisplayLeft = (args, util) => {
    util.runtime.include.add(`#include <LedControl1.h>\n#include <binary.h>\n`);
    util.runtime.define.add(`LedControl1 dotMatrixDisplay${args.SIDE};\n`);
    util.runtime._setup.add(`dotMatrixDisplay${args.SIDE}.initLEDDisplay(${args.DINPIN}, ${args.CLKPIN}, ${args.CSPIN}, 1);\ndotMatrixDisplay${args.SIDE}.shutdown(0,false);\ndotMatrixDisplay${args.SIDE}.setIntensity(0,8);\ndotMatrixDisplay${args.SIDE}.clearDisplay(0);\n`);
};

const displayMatrix1 = (args, util) => {
    const command = `dotMatrixDisplay1.drawFromString(0, "${args.MATRIXL}");\ndotMatrixDisplay2.drawFromString(0, "${args.MATRIXR}");\n`;
	util.runtime.codeGenerateHelper(command);

};

const setServoAngle = (args, util) => {
    const command = `humanoidRobot.moveServo(${args.POSITION}, ${args.ANGLE}, ${args.CALIBRATIONS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const uploadFirmware = (args, util) => {
    util.runtime.include.add(`#include <HumanoidRobotFirmware.h>\n`);
    util.runtime._setup.add(`HumanoidRobotFirmwareSetup();\n`);
    util.runtime._loop.add(`HumanoidRobotFirmwareLoop();\n`);
};

module.exports = {
    initialiseLeg,
    initialiseHand,
    calibrateLeg,
    calibrateHand,
    moveHumanoidRobot,
    moveLeg,
    moveHand,
    initializeDotMatrixDisplayLeft,
    displayMatrix1,
    setServoAngle,
    moveHumanoidRobotHand,
    uploadFirmware
};