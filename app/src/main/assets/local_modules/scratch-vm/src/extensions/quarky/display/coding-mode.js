const offLED = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.clearRGBPixel(${args.XPOS}-1,${args.YPOS}-1);\n`;
    util.runtime.codeGenerateHelper(command);
}

const setLED = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    let color = args.COLOR;
    if (color.startsWith('ColorString')){
        const command = `Quarky.setRGBPixel(${args.XPOS}-1, ${args.YPOS}-1, ${args.COLOR},${args.BRIGHTNESS});\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else{
        const _define = `uint32_t ColorStringNum(char* rgbString) { return uint32_t(strtoul(rgbString,NULL,16));}\n`;
        util.runtime.define.add(_define);
        const command = `Quarky.setRGBPixel(${args.XPOS} - 1, ${args.YPOS} - 1, ColorStringNum("${color.substr(1)}"), ${args.BRIGHTNESS});\n`;
        util.runtime.codeGenerateHelper(command);
    }
}

const clearScreen = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.clearAll();\n`;
    util.runtime.codeGenerateHelper(command);
}

const rgbColor = (args, util) => {
    const _define = `uint32_t ColorString(uint8_t r, uint8_t g, uint8_t b) {\n  String color = Quarky.hexString(r, g, b);\n  char rgbString[7];\n  strcpy(rgbString, color.c_str());\n  return uint32_t(strtoul(rgbString,NULL,16));\n}\n`;
    util.runtime.define.add(_define);
    const command = `ColorString(${args.RED}, ${args.GREEN}, ${args.BLUE})`;
    return command;
}

const matrixPattern = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.setMatrixPattern(${args.PATTERN});\n`;
    util.runtime.codeGenerateHelper(command);
}

const colourMatrix = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.displayMatrix("${args.MATRIX}");\n`;
    util.runtime.codeGenerateHelper(command);
}

const displayText = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    let color = args.COLOR;
    if (color.startsWith('ColorString')){
        const command = `Quarky.writeScrollingText(${args.TEXT}, ${args.SPEED}*100, ${args.COLOR});\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else{
        const _define = `uint32_t ColorStringNum(char* rgbString) { return uint32_t(strtoul(rgbString,NULL,16));}\n`;
        util.runtime.define.add(_define);
        const command = `Quarky.writeScrollingText(${args.TEXT}, ${args.SPEED}*100, ColorStringNum("${color.substr(1)}"));\n`;
        util.runtime.codeGenerateHelper(command);
    }
}

const displayChar = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    let color = args.COLOR;
    if (color.startsWith('ColorString')){
        const command = `Quarky.displayText(${args.TEXT},${args.COLOR},0);\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else{
        const _define = `uint32_t ColorStringNum(char* rgbString) { return uint32_t(strtoul(rgbString,NULL,16));}\n`;
        util.runtime.define.add(_define);
        const command = `Quarky.displayText(${args.CHAR}, ColorStringNum("${color.substr(1)}"),0);\n`;
        util.runtime.codeGenerateHelper(command);
    }
}

const displayEmotion = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.drawEmotion(${args.EMOTION});\n`;
    util.runtime.codeGenerateHelper(command);
}

const showAnimation = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime.include.add(`#include "animations.h"\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `showAnimation(${args.ANIMATION});\n`;
    util.runtime.codeGenerateHelper(command);
}

const setBrightness = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime.include.add(`#include "animations.h"\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.setBrightness(${args.BRIGHTNESS});\n`;
    util.runtime.codeGenerateHelper(command);
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
