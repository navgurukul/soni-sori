const rgbColor = (args, util) => {
    //return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
    const command = `hexString(${args.RED}, ${args.GREEN}, ${args.BLUE}).c_str()`;
    return command;
}

const quonInitialiseRGB = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    util.runtime.define.add(`Adafruit_NeoPixel Strip_${args.STRIP} = Adafruit_NeoPixel(${args.LED}, ${args.DIGITALPIN}, NEO_GRB + NEO_KHZ800);\n`);
    util.runtime._setup.add(`Strip_${args.STRIP}.begin();\n Strip_${args.STRIP}.show();\n`);
};

const initialiseRGB = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    util.runtime.define.add(`Adafruit_NeoPixel Strip_${args.STRIP} = Adafruit_NeoPixel(${args.LED}, ${args.DIGITALPIN}, NEO_GRB + NEO_KHZ800);\n`);
    util.runtime._setup.add(`Strip_${args.STRIP}.begin();\n Strip_${args.STRIP}.show();\n`);
};

const setPixel = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    let color = args.COLOR;
    //const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    //const command = `Strip_${args.STRIP}.setPixelColor(${args.PIXEL}-1, 1235);\n`;
    color = color.startsWith('hexString') ? color : `"${color.substr(1)}"`; 
    //const command = `Strip_${args.STRIP}.setPixelColor(${args.PIXEL}-1, Strip_${args.STRIP}.Color(${rgb[0]}, ${rgb[1]}, ${rgb[2]}));\n`;

    const command = `Strip_${args.STRIP}.setPixelColor(${args.PIXEL}-1,Strip_${args.STRIP}.ColorString(${color}));\n`;
    util.runtime.codeGenerateHelper(command);
};

const showRGB = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    const command = `Strip_${args.STRIP}.show();\n`;
	util.runtime.codeGenerateHelper(command);

};

const showPattern1 = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    //const color = args.COLOR.substr(1);
    //const rgb = color.match(/.{2}/g).map(ele => parseInt(ele, 16));
    let color = args.COLOR;
    color = color.startsWith('hexString') ? color : `"${color.substr(1)}"`; 
    const command = `Strip_${args.STRIP}.pattern1(${args.PATTERN1}, Strip_${args.STRIP}.ColorString(${color}), ${args.DELAY}*1000);\n`;
	util.runtime.codeGenerateHelper(command);

};

const showPattern2 = (args, util) => {
    util.runtime.include.add(`#include <Adafruit_NeoPixel.h>\n`);
    const command = `Strip_${args.STRIP}.pattern2(${args.PATTERN2}, ${args.DELAY}*1000);\n`;
	util.runtime.codeGenerateHelper(command);

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
