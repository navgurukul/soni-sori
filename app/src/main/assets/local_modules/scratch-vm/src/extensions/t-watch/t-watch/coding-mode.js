const digitalRead = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `digitalRead(${args.PIN})`;
};

const analogRead = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, INPUT);\n`);
    return `analogRead(${args.PIN})`;
};

const digitalWrite = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `digitalWrite(${args.PIN}, ${args.MODE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setPWM = (args, util) => {
    util.runtime.include.add(`#include <esp32PWMUtilities.h>\n`);
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `analogWrite(${args.PIN}, ${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const userButton = (args, util) => {
    util.runtime._setup.add(`pinMode(36, INPUT);\n`);
    return `!(digitalRead(36))`;
};

const setClockTime = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime.define.add(`#include <RTC.h>\n`);
    const command = `setTimePCF8563(${args.HRS}, ${args.MINS}, ${args.SECS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setClockDate = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime.define.add(`#include <RTC.h>\n`);
    const command = `setDatePCF8563(${args.WEEKDAY}, ${args.DATE}, ${args.MONTH}, ${args.YEAR});\n`;
	util.runtime.codeGenerateHelper(command);
};

const getDataFromClock = (args, util) => {
    util.runtime.include.add(`#include <TTGO.h>\n`);
    util.runtime.define.add(`TTGOClass *ttgo;\n`);
    util.runtime._setup.add(`ttgo = TTGOClass::getWatch();\nttgo->begin();\nttgo->openBL();\n`);
    util.runtime.define.add(`#include <RTC.h>\n`);
    let command = ``;
    if (args.TIME === '11'){
        command = `getHour()`;
    } 
    if (args.TIME === '12') {
        command = `getMinute()`;
    }
    if (args.TIME === '13') {
        command = `getSecond()`;
    }
    if (args.TIME === '21') {
        command = `getDay()`;
    }
    if (args.TIME === '22') {
        command = `getMonth()`;
    }
    if (args.TIME === '23') {
        command = `getYear()`;
    }
    if (args.TIME === '24') {
        command = `getWeekday()`;
    }

    return command;
};

const playTone = (args, util) => {
    util.runtime.include.add(`#include <Tone32.h>\n`);
    util.runtime.define.add(`#define BUZZER_PIN ${args.PIN}\n`);
    util.runtime.define.add(`#define BUZZER_CHANNEL 0\n`);
    const command = `tone(BUZZER_PIN, ${args.NOTE}, ${args.BEATS}, BUZZER_CHANNEL);\n`;
	util.runtime.codeGenerateHelper(command);
};

const map = (args, util) => {
    return `map(${args.VALUE}, ${args.RANGE11}, ${args.RANGE12}, ${args.RANGE21}, ${args.RANGE22})`;
};

const getMacAddr = (args, util) => {
    return `esp32ble.getMacAddress()`;
};

const deepSleepTimer = (args, util) => {
    util.runtime._setup.add(`esp_sleep_enable_timer_wakeup(${args.TIME} * 1000000);\n`);
    const command = ` esp_deep_sleep_start();\n`;
	util.runtime.codeGenerateHelper(command);
};

const deepSleepExternalSource = (args, util) => {
    util.runtime._setup.add(`esp_sleep_enable_ext0_wakeup(GPIO_NUM_${args.PIN},${args.STATE}); \n`);
    const command = ` esp_deep_sleep_start();\n`;
	util.runtime.codeGenerateHelper(command);
};

const deepSleepTouchPins = (args, util) => {
    util.runtime._setup.add(`touchAttachInterrupt(${args.TOUCH_PIN}, NULL, ${args.THRESHOLD});\n`);
    util.runtime._setup.add(`esp_sleep_enable_touchpad_wakeup();\n`);
    const command = ` esp_deep_sleep_start();\n`;
	util.runtime.codeGenerateHelper(command);
};

/////////////////////////////

/*const fillscreen = (args, util) => {
    util.runtime.include.add(`#include <TFT_eSPI.h>\n`);
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.fillScreen(${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setCursor = (args, util) => {
   // util.runtime.include.add(`#include <TFT_eSPI.h>`);
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.setCursor(${args.X_AXIS}, ${args.Y_AXIS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setTextColorSize = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.setTextColor(${util.color565(args.TEXT_COLOR)}, ${util.color565(args.BG_COLOR)});\nlcd.setTextSize(${args.SIZE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const write = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.print(${args.TEXT});\n`;
	util.runtime.codeGenerateHelper(command);

};

const drawLine = (args, util, parent) => {
    console.log(util);
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.drawLine(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRect = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Rect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawRoundRect = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}RoundRect(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawCircle = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Circle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.RADIUS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawEllipse = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Ellipse(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const fillDrawTriangle = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.${(args.OPTION === '1' ? 'fill' : 'draw')}Triangle(${args.X1_AXIS}, ${args.Y1_AXIS}, ${args.X2_AXIS}, ${args.Y2_AXIS}, ${args.X3_AXIS}, ${args.Y3_AXIS}, ${util.color565(args.COLOR)});\n`;
	util.runtime.codeGenerateHelper(command);

};

const displayMatrix3 = (args, util, parent) => {
    util.runtime.define.add(`TFT_eSPI lcd = TFT_eSPI();\n`);
    util.runtime._setup.add(`lcd.init(INITR_BLACKTAB);\nlcd.setRotation(1);\n`);
    const command = `lcd.drawMatrix(${args.SIZE}, ${args.XPOSITION}, ${args.YPOSITION}, ${util.color565(args.COLOR)}, ${util.color565(args.COLOR2)}, "${args.MATRIX}");\n`;
	util.runtime.codeGenerateHelper(command);

}*/


module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    userButton, 
    setClockTime,
    setClockDate,
    getDataFromClock,
    playTone,
    map,
    getMacAddr,
    deepSleepTimer,
    deepSleepExternalSource,
    deepSleepTouchPins
    /*,
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
    displayMatrix3*/
};
