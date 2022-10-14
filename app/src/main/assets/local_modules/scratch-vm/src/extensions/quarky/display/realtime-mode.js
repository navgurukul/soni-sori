const rgbColor = (args, util) => {
    return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
}

const offLED = (args, util) => {
    const arg = [args.XPOS, args.YPOS];
    util.runtime.writeToPeripheral(arg, 137, 2);
    return util.setSendDelay();
}
const clearScreen = (args, util) => {
    util.runtime.writeToPeripheral(0, 138, 2);
    return util.setSendDelay();
}

const setLED = (args, util) => {
    const color = args.COLOR.substr(1);
    const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    const dataBuffer = [args.XPOS, args.YPOS, rgb[1], rgb[0], rgb[2], args.BRIGHTNESS];
    util.runtime.writeToPeripheral(dataBuffer, 136, 2);
    return util.setSendDelay();
}

const matrixPattern = (args, util, parent) => {
    const dataBuffer = [args.PATTERN, ...util.short2array(args.DELAY*1000)];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 142, 1);
        parent._isSerialRead = resolve;
    });
}

const colourMatrix = (args, util, parent) => {
    const arg = args.MATRIX.split('');
    const dataBuffer = [arg.map(ele => ele.charCodeAt(0))];
    console.log(dataBuffer);
    util.runtime.writeToPeripheral(dataBuffer, 143, 2);
    return util.setSendDelay();
   // console.log("Hello");
}

const displayText = (args, util, parent) => {
    const color = args.COLOR.substr(1);
    const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    const arg = args.TEXT.toString().split('');
    dataBuffer = [args.SPEED,rgb[0], rgb[1], rgb[2],arg.map(ele => ele.charCodeAt(0))]
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 156, 1);
        parent._isSerialRead = resolve;
    });
}

const displayChar = (args, util, parent) => {
    const color = args.COLOR.substr(1);
    const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    const arg = args.CHAR.toString().split('');
    dataBuffer = [rgb[0], rgb[1], rgb[2], arg.map(ele => ele.charCodeAt(0))]
    util.runtime.writeToPeripheral(dataBuffer, 158, 2);
    return util.setSendDelay();
}


const displayEmotion = (args, util) => {
    const dataBuffer = [args.EMOTION];
    util.runtime.writeToPeripheral(dataBuffer, 152, 2);
    return util.setSendDelay();

}

const showAnimation = (args, util, parent) => {
    const dataBuffer = [args.ANIMATION];
    return new Promise(resolve => {
        util.runtime.writeToPeripheral(dataBuffer, 153, 1);
        parent._isSerialRead = resolve;
    });
}

const setBrightness= (args, util) => {
    const dataBuffer = [args.BRIGHTNESS];
    util.runtime.writeToPeripheral(dataBuffer, 154, 2);
    return util.setSendDelay();
}
module.exports = {
    offLED,
    clearScreen,
    rgbColor,
    setLED,
    matrixPattern,
    colourMatrix,
    displayText,
    displayChar,
    displayEmotion,
    showAnimation,
    setBrightness
};
