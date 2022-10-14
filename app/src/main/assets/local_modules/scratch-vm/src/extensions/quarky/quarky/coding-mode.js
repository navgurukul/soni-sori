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
    const command = `digitalWrite(${args.PIN}, ${args.STATE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setPWM = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `analogWrite(${args.PIN}, ${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const bluetoothIndicator = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.bleIndicator(${args.STATE});\n`;
	util.runtime.codeGenerateHelper(command);

};

// const playTone = (args, util) => {
//     util.runtime.include.add(`#include <Tone32.h>\n`);
//     util.runtime.define.add(`#define BUZZER_PIN ${args.PIN}\n`);
//     util.runtime.define.add(`#define BUZZER_CHANNEL 0\n`);
//     const command = `tone(BUZZER_PIN, ${args.NOTE}, ${args.BEATS}, BUZZER_CHANNEL);\n`;
// 	util.runtime.codeGenerateHelper(command);
// };

const map = (args, util) => {
    return `map(${args.VALUE}, ${args.RANGE11}, ${args.RANGE12}, ${args.RANGE21}, ${args.RANGE22})`;
};

// const getMacAddr = (args, util) => {
//     return `esp32ble.getMacAddress()`;
// };

// const pushButtons = (args, util) => {
//     const func = (args.TACTILE_SW === '1' ? `Quarky.readPushButtonA()` : `Quarky.readPushButtonB()`);
//     return func;
// };

// const touchRead = (args, util) => {
//     return `touchRead(${args.TOUCHPIN})`;
// };

const cast = (args, util) => {
    if (args.OPERATION === '1'){
        return `((int) ${args.VALUE})`;
    } 
    if (args.OPERATION === '2') {
        return `float(${args.VALUE})`;
    }
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

const enableDeepSleep = (args, util) => {
    const command = ` esp_deep_sleep_start();\n`;
	util.runtime.codeGenerateHelper(command);
};

module.exports = {
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    bluetoothIndicator,
    map,
    cast,
    enableDeepSleep,
    deepSleepTimer,
    deepSleepExternalSource,
    deepSleepTouchPins
   // playTone,
   // pushButtons,
   // getMacAddr,
   // touchRead
};
