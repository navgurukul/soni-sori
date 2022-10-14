
const initialiseDisplay = (args, util) => {
    util.runtime.include.add(`#include <LiquidCrystal.h>\n`);
    util.runtime.define.add(`const int resetPin = ${args.RESET};\n`);
    util.runtime.define.add(`const int enablePin = ${args.ENABLE};\n`);
    util.runtime.define.add(`const int d4 = ${args.PIND4};\n`);
    util.runtime.define.add(`const int d5 = ${args.PIND5};\n`);
    util.runtime.define.add(`const int d6 = ${args.PIND6};\n`);
    util.runtime.define.add(`const int d7 = ${args.PIND7};\n`);
    util.runtime.define.add(`LiquidCrystal lcd16x2(resetPin, enablePin, d4, d5, d6, d7);\n`);
    util.runtime._setup.add(`lcd16x2.begin(16, 2);\n`);
};

const initialiseI2CDisplay = (args, util) => {
    util.runtime.include.add(`#include <Wire.h> \n`);
    util.runtime.include.add(`#include <LiquidCrystal_I2C.h> \n`);
    util.runtime.define.add(`LiquidCrystal_I2C lcd16x2(${args.I2C_ADD}, 16, 2);\n`);
    util.runtime._setup.add(`lcd16x2.begin();\n`);
    util.runtime._setup.add(`lcd16x2.backlight();\n`);
};

const setCursor = (args, util) => {
    const command = `lcd16x2.setCursor(${args.COLUMN} - 1, ${args.ROW} - 1);\n`;
	util.runtime.codeGenerateHelper(command);

};

const write = (args, util) => {
    const command = `lcd16x2.print(${args.TEXT});\n`;
	util.runtime.codeGenerateHelper(command);

};

const clearDisplay = (args, util) => {
    const command = `lcd16x2.clear();\n`;
	util.runtime.codeGenerateHelper(command);

};

const setMode = (args, util) => {
    let command = ``;
    if (args.MODE === '1'){
        command = `lcd16x2.blink();\n`;
    } 
    if (args.MODE === '2') {
        command = `lcd16x2.noBlink();\n`;
    }
    if (args.MODE === '3') {
        command = `lcd16x2.cursor();\n`;
    }
    if (args.MODE === '4') {
        command = `lcd16x2.noCursor();\n`;
    }
    if (args.MODE === '5') {
        command = `lcd16x2.display();\n`;
    }
    if (args.MODE === '6') {
        command = `lcd16x2.noDisplay();\n`;
    }
    if (args.MODE === '7') {
        command = `lcd16x2.autoscroll();\n`;
    }
    if (args.MODE === '8') {
        command = `lcd16x2.noAutoscroll();\n`;
    }
    if (args.MODE === '9') {
        command = `lcd16x2.scrollDisplayLeft();\n`;
    }
    if (args.MODE === '10') {
        command = `lcd16x2.scrollDisplayRight();\n`;
    }

	util.runtime.codeGenerateHelper(command);

};

const initializeTM1637Display = (args, util) => {
    util.runtime.include.add(`#include <TM1637Display.h>\n`);
    util.runtime.define.add(`TM1637Display display(${args.CLKPIN}, ${args.DIOPIN});\n`);
    util.runtime._setup.add(`display.setBrightness(0x0f);\n`);
};

const showNumberTM1637Display = (args, util) => {
    const command = `display.showNumberDecEx(${args.INPUT}, ${args.DOTS}, ${args.LEADINGZERO}, ${args.LENGHT}, ${args.POSITION} - 1);\n`;
	util.runtime.codeGenerateHelper(command);

};

const clearTM1637Display = (args, util) => {
    const command = `display.clear();\n`;
	util.runtime.codeGenerateHelper(command);

};

const initializeDotMatrixDisplay = (args, util) => {
    util.runtime.include.add(`#include <LedControl.h>\n`);
    util.runtime.include.add(`#include <binary.h>\n`);
    util.runtime.define.add(`LedControl dotMatrixDisplay = LedControl(${args.DINPIN}, ${args.CLKPIN}, ${args.CSPIN}, 1);\n`);
    util.runtime._setup.add(`dotMatrixDisplay.shutdown(0,false);\n`);
    util.runtime._setup.add(`dotMatrixDisplay.setIntensity(0,8);\n`);
    util.runtime._setup.add(`dotMatrixDisplay.clearDisplay(0);\n`);
};

const displayMatrix = (args, util) => {
    const command = `dotMatrixDisplay.drawFromString(0, "${args.MATRIX}");\n`;
	util.runtime.codeGenerateHelper(command);

};

const setLED = (args, util) => {
    const command = `dotMatrixDisplay.setLed(0, ${args.ROW}-1, ${args.COL}-1, ${args.STATE});\n`;
	util.runtime.codeGenerateHelper(command);

};

module.exports = {
    initialiseDisplay,
    initialiseI2CDisplay,
    setCursor,
    write,
    setMode,
    clearDisplay,
    initializeTM1637Display,
    showNumberTM1637Display,
    clearTM1637Display,
    initializeDotMatrixDisplay,
    displayMatrix,
    setLED
}