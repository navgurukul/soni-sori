const initialiseStepper = (args, util) => {
    util.runtime.include.add(`#include <CheapStepper.h>\n`);
    const _define = `CheapStepper stepper${args.STEPPER}(${args.IN1}, ${args.IN2}, ${args.IN3}, ${args.IN4});\n`;
    util.runtime.define.add(_define);
};

const moveStepper = (args, util) => {
    const command1 = (`stepper${args.STEPPER}.setRpm(${args.RPM});\n`);
    command2 = (``);
    if (args.CHOICE === '1'){
        command2 = (args.DIRECTION === '1' ? `stepper${args.STEPPER}.moveDegrees(1, ${args.QUANTITY});\n` : `stepper${args.STEPPER}.moveDegrees(0, ${args.QUANTITY});\n`);
    } else {
        command2 = (args.DIRECTION === '1' ? `stepper${args.STEPPER}.move(1, ${args.QUANTITY});\n` : `stepper${args.STEPPER}.move(0, ${args.QUANTITY});\n`);
    }
    util.runtime.codeGenerateHelper(command1 + command2);
};

const initialiseMultiStepperA4988 = (args, util) => {
    util.runtime.include.add(`#include <AccelStepper.h>\n`);
    util.runtime.define.add(`AccelStepper stepperMultiA4988${args.STEPPER}(1, ${args.STEP}, ${args.DIR});\n`);
};

const moveMultiStepperA4988 = (args, util) => {
    command2 = (``);
    command2 = (args.DIRECTION === '1' ? `stepperMultiA4988${args.STEPPER}.move(${args.QUANTITY});\n` : `stepperA4988${args.STEPPER}.move(- (${args.QUANTITY});\n`);
	util.runtime.codeGenerateHelper(command);

};

const moveMultiStepperA4988Position = (args, util) => {
    command2 = (`stepperMultiA4988${args.STEPPER}.moveTo(${args.POSITION});\n`);
	util.runtime.codeGenerateHelper(command);

};

const runMultiStepperA4988 = (args, util) => {
    command = (``);
    if (args.OPERATION === '1'){
        command = (`stepperMultiA4988${args.STEPPER}.run();\n`);
    } 
    if (args.OPERATION === '2'){
        command = (`stepperMultiA4988${args.STEPPER}.runSpeed();\n`);
    }
    if (args.OPERATION === '3'){
        command = (`stepperMultiA4988${args.STEPPER}.stop();\n`);
    }
	util.runtime.codeGenerateHelper(command);

};

const setMultiStepperA4988 = (args, util) => {
    command = (``);
    if (args.OPERATION === '1'){
        command = (`stepperMultiA4988${args.STEPPER}.setMaxSpeed(${args.POSITION});\n`);
    } 
    if (args.OPERATION === '2'){
        command = (`stepperMultiA4988${args.STEPPER}.setSpeed(${args.POSITION});\n`);
    }
    if (args.OPERATION === '3'){
        command = (`stepperMultiA4988${args.STEPPER}.setAcceleration(${args.POSITION});\n`);
    }
	util.runtime.codeGenerateHelper(command);

};

const getMultiStepperA4988 = (args, util) => {
    command1 = (``);
    if (args.OPERATION === '1'){
        command1 = (`stepperMultiA4988${args.STEPPER}.maxSpeed()`);
    } 
    if (args.OPERATION === '2'){
        command1 = (`stepperMultiA4988${args.STEPPER}.speed()`);
    }
    if (args.OPERATION === '3'){
        command1 = (`stepperMultiA4988${args.STEPPER}.distanceToGo()`);
    }
    if (args.OPERATION === '4'){
        command1 = (`stepperMultiA4988${args.STEPPER}.targetPosition()`);
    }
    if (args.OPERATION === '5'){
        command1 = (`stepperMultiA4988${args.STEPPER}.currentPosition()`);
    }
    return command1;
};

module.exports = {
    initialiseStepper,
    moveStepper,
    initialiseMultiStepperA4988,
    moveMultiStepperA4988,
    moveMultiStepperA4988Position,
    runMultiStepperA4988,
    setMultiStepperA4988,
    getMultiStepperA4988
};