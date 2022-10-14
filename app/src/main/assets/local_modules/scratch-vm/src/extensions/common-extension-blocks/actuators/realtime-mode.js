const initialiseMotor = (args, util) => {
    const arg = [args.MOTOR, args.DIRECTION1, args.DIRECTION2, args.PWM];
    util.runtime.writeToPeripheral(arg, 26, 2);
    return util.setSendDelay();
};

const runMotor = (args, util) => {
    const arg = [args.MOTOR, args.DIRECTION, Math.round(args.SPEED*2.55)];
    util.runtime.writeToPeripheral(arg, 9, 2);
    return util.setSendDelay();
};

const updateMotorState = (args, util) => {
    const arg = [args.MOTOR, args.MOTOR_STATE, 0];
    util.runtime.writeToPeripheral(arg, 9, 2);
    return util.setSendDelay();
};

const setServo = (args, util) => {
    const arg = [args.SERVO_CHANNEL, args.ANGLE];
    util.runtime.writeToPeripheral(arg, 33, 2);
    return util.setSendDelay();
};

const setRelay = (args, util) => {
    const dataBuffer = [args.DIGITAL_PIN, (args.MODE === 'true')];
    util.runtime.writeToPeripheral(dataBuffer, 30, 2);
    return util.setSendDelay();
};

const setBLDC = (args, util) => {
    const angle = Math.round(args.SPEED*1.8);
    const arg = [args.SERVO_CHANNEL,angle];
    util.runtime.writeToPeripheral(arg, 33, 2);
    return util.setSendDelay();
};

const tWatchVibrationMotor = (args, util) => {
    console.log("Step count T-watch");
};

module.exports = {
    runMotor,
    updateMotorState,
    setServo,
    setRelay,
    initialiseMotor,
    setBLDC,
    tWatchVibrationMotor
};
