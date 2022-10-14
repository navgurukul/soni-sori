let screenRotation = 0;
const setImage  = (args, util) => {
    //#include <TTGO.h>

//
    let name = args.NAME.split(' ').join('_');
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    // ttgo = TTGOClass::getWatch();
   // ttgo->begin();
    //ttgo->openBL();   
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime._setup.add(`ttgo->eTFT->setSwapBytes(1);\n`);
    const [imageData, size] = util.imageToRgb(args.IMAGE, args.SCALER, 240, 240);    util.runtime.include.add(`const uint16_t ${name}[] PROGMEM={${imageData}};\n`);
    util.runtime.vars.add(`int ${name}_width = ${size.width};\n`);
    util.runtime.vars.add(`int ${name}_height = ${size.height};\n`);
    util.runtime._setup.add(`ttgo->eTFT->init(TFT_BLACK);\nttgo->eTFT->setRotation(${screenRotation});\n`);
   // util.runtime._setup.add(`pinMode(TFT_BL,OUTPUT);\ndigitalWrite(TFT_BL,HIGH);\n`);
}

const displayImage = (args, util) => {
    let name = args.NAME.split(' ').join('_');
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime._setup.add(`ttgo->eTFT->setSwapBytes(1);\n`);
    // util.runtime._setup.add(`Wire.begin(21,22);\n`);
    // util.runtime._setup.add(`int ret = axp.begin(Wire);\naxp.setPowerOutPut(AXP202_LDO2,AXP202_ON);\n`);
    const command = `ttgo->eTFT->pushImage(${args.X_AXIS}, ${args.Y_AXIS}, ${name}_width, ${name}_height, ${name});\n`;
    util.runtime.codeGenerateHelper(command);
}

const displayImagefromSD = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.include.add(`#include <SDDrawBitMap.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime.define.add(`TFT_eSPI *tft ;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime._setup.add(`tft = ttgo->eTFT;\ntft->fillScreen(TFT_BLACK);\ntft->setTextDatum(MC_DATUM);\n`);
    util.runtime._setup.add(`checkSDCardInit(ttgo,tft);\n`);
    // util.runtime._setup.add(`Wire.begin(21,22);\n`);
    // util.runtime._setup.add(`int ret = axp.begin(Wire);\naxp.setPowerOutPut(AXP202_LDO2,AXP202_ON);\n`);
    const command = `drawBmp(tft,${args.NAME},${args.X_AXIS}, ${args.Y_AXIS});\n`;
    util.runtime.codeGenerateHelper(command);
}

const fillscreen = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime._setup.add(`ttgo->eTFT->init(TFT_BLACK);\nttgo->eTFT->setRotation(${screenRotation});\n`);
    let color = args.COLOR;
    const command = color.startsWith('ttgo->eTFT->color565') ? `ttgo->eTFT->fillScreen(${color});\n` : `ttgo->eTFT->fillScreen(${util.color565(args.COLOR)});\n`; 
    //console.log(command);
    //const command = `lcd.fillScreen(0);\n`;
	util.runtime.codeGenerateHelper(command);
};

const rgbColorDisplay = (args, util) => {
   // return util.rgbToHex(args.RED, args.GREEN, args.BLUE);
   return `ttgo->eTFT->color565(${args.RED}, ${args.GREEN}, ${args.BLUE})`;
}

const setCursor = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime._setup.add(`ttgo->eTFT->init(TFT_BLACK);\nttgo->eTFT->setRotation(${screenRotation});\n`);
    const command = `ttgo->eTFT->setCursor(${args.X_AXIS}, ${args.Y_AXIS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setScreenRotation = (args, util) => {
    screenRotation = args.ROTATION_VALUE;
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->setRotation(${screenRotation});\n`;
	util.runtime.codeGenerateHelper(command);

};



const setTextColorSize = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->setTextColor(${util.color565(args.TEXT_COLOR)}, ${util.color565(args.BG_COLOR)});\nttgo->eTFT->setTextSize(${args.SIZE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const write = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->print(${args.TEXT});\n`;
	util.runtime.codeGenerateHelper(command);

};

const drawLine = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->drawLine(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRect = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->${(args.OPTION === '1' ? 'fill' : 'draw')}Rect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRoundRect = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->${(args.OPTION === '1' ? 'fill' : 'draw')}RoundRect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawCircle = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->${(args.OPTION === '1' ? 'fill' : 'draw')}Circle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);
};

const fillDrawEllipse = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->${(args.OPTION === '1' ? 'fill' : 'draw')}Ellipse(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawTriangle = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `ttgo->eTFT->${(args.OPTION === '1' ? 'fill' : 'draw')}Triangle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.X3_AXIS}, ${args.Y3_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const getTouchValue = (args, util, parent) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime.define.add(`TP_Point p;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    const command = `p =  ttgo->touch->getPoint();\n`
	util.runtime.codeGenerateHelper(command);

    return `map(p.${args.AXIS},0,239,0,239)`;
};

/*const displayMatrix3 = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(TFT_BLACK);\nlcd.setRotation(0);\n`);
    const command = `lcd.drawMatrix(${args.SIZE}, ${args.XPOSITION}, ${args.YPOSITION}, ${util.color565(args.COLOR)}, ${util.color565(args.COLOR2)}, "${args.MATRIX}");\n`;
	util.runtime.codeGenerateHelper(command);

};*/

module.exports = {
    fillscreen,
    setCursor,
    setScreenRotation,
    setTextColorSize,
    write,
    drawLine,
    fillDrawRect,
    fillDrawRoundRect,
    fillDrawCircle,
    fillDrawEllipse,
    fillDrawTriangle,
    getTouchValue,
    setImage,
    displayImage,
    displayImagefromSD,
    rgbColorDisplay
    //displayMatrix3
};
