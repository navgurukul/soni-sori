const fillscreen = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    util.runtime.writeToPeripheral(util.short2array(COLOR), 14, 2);
    return util.setSendDelay();
};

const setCursor = (args, util, parent) => {
    const arg = [args.X_AXIS, args.Y_AXIS];
    util.runtime.writeToPeripheral(arg, 15, 2);
    return util.setSendDelay();
};

const setTextColorSize = (args, util, parent) => {
    const TEXT_COLOR = util.color565(args.TEXT_COLOR);
    const BG_COLOR = util.color565(args.BG_COLOR);
    const arg = [...util.short2array(TEXT_COLOR), ...util.short2array(BG_COLOR), args.SIZE];
    util.runtime.writeToPeripheral(arg, 16, 2);
    return util.setSendDelay();
};

const write = (args, util, parent) => {
    const arg = args.TEXT.toString().split('');
    util.runtime.writeToPeripheral(arg.map(ele => ele.charCodeAt(0)), 13, 2);
    return util.setSendDelay();
};

const drawLine = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.X2_AXIS, args.Y2_AXIS, ...util.short2array(COLOR)];
    util.runtime.writeToPeripheral(arg, 18, 2);
    return util.setSendDelay();
};

const fillDrawRect = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.X2_AXIS, args.Y2_AXIS, ...util.short2array(COLOR), args.OPTION];
    util.runtime.writeToPeripheral(arg, 21, 2);
    return util.setSendDelay();
};

const fillDrawRoundRect = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.X2_AXIS, args.Y2_AXIS, args.RADIUS, ...util.short2array(COLOR), args.OPTION];
    util.runtime.writeToPeripheral(arg, 22, 2);
    return util.setSendDelay();
};

const fillDrawCircle = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.RADIUS, ...util.short2array(COLOR), args.OPTION];
    util.runtime.writeToPeripheral(arg, 23, 2);
    return util.setSendDelay();
};

const fillDrawEllipse = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.X2_AXIS, args.Y2_AXIS, ...util.short2array(COLOR), args.OPTION];
    util.runtime.writeToPeripheral(arg, 24, 2);
    return util.setSendDelay();
};

const fillDrawTriangle = (args, util, parent) => {
    const COLOR = util.color565(args.COLOR);
    const arg = [args.X1_AXIS, args.Y1_AXIS, args.X2_AXIS, args.Y2_AXIS, args.X3_AXIS, args.Y3_AXIS, ...util.short2array(COLOR), args.OPTION];
    util.runtime.writeToPeripheral(arg, 25, 2);
    return util.setSendDelay();
};

const displayMatrix3 = (args, util, parent) => {
    const arg1String = args.MATRIX.substring(0, 160);
    const arg2String = args.MATRIX.substring(160, 320);
    const arg1 = arg1String.split('');
    const arg2 = arg2String.split('');
    const COLOR1 = util.color565(args.COLOR);
    const COLOR2 = util.color565(args.COLOR2);
    util.runtime.writeToPeripheral([...arg1], 17, 2);
    util.setDelay(100);
    util.runtime.writeToPeripheral([args.SIZE, args.XPOSITION, args.YPOSITION, ...util.short2array(COLOR1), ...util.short2array(COLOR2), ...arg2], 26, 2);
    return util.setDelay(10);
};

const rgbColorDisplay = (args, util, parent) => {
    return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
}

module.exports = {
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
    displayMatrix3,
    rgbColorDisplay
};
