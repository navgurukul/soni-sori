const displayImage  = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    const arg1 = util.imageToRgb(args.IMAGE, args.WIDTH, args.HEIGHT);
    util.runtime.vars.add(`const uint16_t ${args.NAME}[] PROGMEM={${arg1}};\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.pushImage(${args.X_AXIS}, ${args.Y_AXIS}, ${args.WIDTH}, ${args.HEIGHT},${args.NAME});\n`;
    util.runtime.codeGenerateHelper(command);
}

const enableHeaderBlock  = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    //util.runtime._setup.add(`createHeadContainer(${args.STATUS});\n`); 
   const command = `createHeadContainer(${args.STATUS});\n`;
    util.runtime.codeGenerateHelper(command);
}

const colourMatrix = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`);
    const command = `Quon.displayMatrix("${args.MATRIX}");\n`;
    util.runtime.codeGenerateHelper(command);
}

const fillscreen = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.fillScreen(${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const rgbColor = (args, util) => {
    return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
}

const setCursor = (args, util) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.setCursor(${args.X_AXIS}, ${args.Y_AXIS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setTextColorSize = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.setTextColor(${util.color565(args.TEXT_COLOR)}, ${util.color565(args.BG_COLOR)});\nQuon.setTextSize(${args.SIZE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const write = (args, util, parent) => {
    console.log("coding write: ",args.TEXT.toString());
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.print(${args.TEXT});\n`;
	util.runtime.codeGenerateHelper(command);

};

const drawLine = (args, util, parent) => {
    console.log(util);
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.drawLine(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRect = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.${(args.OPTION === '1' ? 'fill' : 'draw')}Rect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRoundRect = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.${(args.OPTION === '1' ? 'fill' : 'draw')}RoundRect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawCircle = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.${(args.OPTION === '1' ? 'fill' : 'draw')}Circle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawEllipse = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.${(args.OPTION === '1' ? 'fill' : 'draw')}Ellipse(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawTriangle = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.${(args.OPTION === '1' ? 'fill' : 'draw')}Triangle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.X3_AXIS}, ${args.Y3_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const displayMatrix3 = (args, util, parent) => {
    util.runtime.include.add(`#include <quon.h>\n`);
    util.runtime._setup.add(`Quon.begin();\n`); 
    const command = `Quon.drawMatrix(${args.SIZE}, ${args.XPOSITION}, ${args.YPOSITION}, ${util.color565(args.COLOR)}, ${util.color565(args.COLOR2)}, "${args.MATRIX}");\n`;
	util.runtime.codeGenerateHelper(command);

};

module.exports = {
    displayImage,
    enableHeaderBlock,
    colourMatrix,
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
    rgbColor
};
