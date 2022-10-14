const rgbColor = (args, util, parent) => {
    return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
}

const initialiseRGB = (args, util, parent) => {
    const dataBuffer = [args.STRIP, args.LED, args.DIGITALPIN];
    util.runtime.writeToPeripheral(dataBuffer, 73, 2);
    return util.setSendDelay();
};

const quonInitialiseRGB = (args, util, parent) => {
    const dataBuffer = [args.STRIP, args.LED, args.DIGITALPIN];
    util.runtime.writeToPeripheral(dataBuffer, 73, 2);
    return util.setSendDelay();
};

const setPixel = (args, util, parent) => {
    const color = args.COLOR.substr(1);
    const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    const dataBuffer = [args.STRIP, args.PIXEL-1, rgb[0], rgb[1], rgb[2]];
    util.runtime.writeToPeripheral(dataBuffer, 74, 2);
    return util.setSendDelay();
};

const showRGB = (args, util, parent) => {
    const dataBuffer = [args.STRIP];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 75, 1);
        parent._isSerialRead = resolve;
    });
};

const showPattern1 = (args, util, parent) => {
    const color = args.COLOR.substr(1);
    const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    const dataBuffer = [args.PATTERN1, args.STRIP, rgb[0], rgb[1], rgb[2], ...util.short2array(args.DELAY*1000)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 76, 1);
        parent._isSerialRead = resolve;
    });
};

const showPattern2 = (args, util, parent) => {
    const dataBuffer = [args.PATTERN2, args.STRIP, ...util.short2array(args.DELAY*1000)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 77, 1);
        parent._isSerialRead = resolve;
    });
};


module.exports = {
    quonInitialiseRGB,
    initialiseRGB,
    setPixel,
    showRGB,
    showPattern1,
    showPattern2,
    rgbColor
};