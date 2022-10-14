const fillscreen = (args, util) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.fillScreen(${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setCursor = (args, util) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.setCursor(${args.X_AXIS}, ${args.Y_AXIS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setTextColorSize = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.setTextColor(${util.color565(args.TEXT_COLOR)}, ${util.color565(args.BG_COLOR)});\nlcd.setTextSize(${args.SIZE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const write = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.print(${args.TEXT});\n`;
	util.runtime.codeGenerateHelper(command);

};

const drawLine = (args, util, parent) => {
    console.log(util);
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.drawLine(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRect = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Rect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRoundRect = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}RoundRect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawCircle = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Circle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawEllipse = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Ellipse(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawTriangle = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Triangle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.X3_AXIS}, ${args.Y3_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const displayMatrix3 = (args, util, parent) => {
    util.runtime.define.add(`TFT_ST7735 lcd = TFT_ST7735();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.drawMatrix(${args.SIZE}, ${args.XPOSITION}, ${args.YPOSITION}, ${util.color565(args.COLOR)}, ${util.color565(args.COLOR2)}, "${args.MATRIX}");\n`;
	util.runtime.codeGenerateHelper(command);

};

const rgbColorDisplay = (args, util) => {
    // return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
    return `lcd.color565(${args.RED}, ${args.GREEN}, ${args.BLUE})`;
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
