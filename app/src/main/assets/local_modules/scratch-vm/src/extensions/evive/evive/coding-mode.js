const tactileSwitch = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.TACTILE_SWITCH}, INPUT);\n`);
    return `digitalRead(${args.TACTILE_SWITCH})`;
};

const slideSwitch = (args, util) => {
    let pin;
    switch (args.SLIDE_SWITCH){
    case '1':
        if (args.SWITCH_DIRECTION === 'up'){
            pin = 40;
        } else {
            pin = 41;
        }
        break;
    case '2':
        if (args.SWITCH_DIRECTION === 'up') {
            pin = 42;
        } else {
            pin = 43;
        }
        break;
    }
    util.runtime._setup.add(`pinMode(${pin}, INPUT);\n`);
    return `digitalRead(${pin})`;
};

const navigationKey = (args, util) => {
    let isDirection;
    util.runtime.define.add(`Navkey NavKey;\n`);
    switch (args.NAV_DIRECTION){
    case '1':
        isDirection = 'isUp';
        break;
    case '2':
        isDirection = 'isRight';
        break;
    case '3':
        isDirection = 'isDown';
        break;
    case '4':
        isDirection = 'isLeft';
        break;
    }
    console.log(isDirection);
    return `NavKey.${isDirection}()`;
};

const potentiometer = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.POTENTIOMETER}, INPUT);\n`);
    return `analogRead(${args.POTENTIOMETER})`;
};

const touchSensor = (args, util) => {
    util.runtime._setup.add(`touchPins.begin(0x5A);\n`);
    return `touchPins.isTouched(${args.TOUCH_PINS})`;
};

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
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `analogWrite(${args.PIN}, ${args.VALUE});\n`;
	util.runtime.codeGenerateHelper(command);

};

const playTone = (args, util) => {
    util.runtime._setup.add(`pinMode(${args.PIN}, OUTPUT);\n`);
    const command = `tone(${args.PIN},${args.NOTE},${args.BEATS});\ndelay(${args.BEATS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setClockTime = (args, util) => {
    util.runtime._setup.add(`Wire.begin();\n`);
    util.runtime.define.add(`#include <RTC.h>\n`);
    const command = `setTimePCF8563(${args.HRS}, ${args.MINS}, ${args.SECS});\n`;
	util.runtime.codeGenerateHelper(command);

};

const setClockDate = (args, util) => {
    util.runtime._setup.add(`Wire.begin();\n`);
    util.runtime.define.add(`#include <RTC.h>\n`);
    const command = `setDatePCF8563(${args.WEEKDAY}, ${args.DATE}, ${args.MONTH}, ${args.YEAR});\n`;
	util.runtime.codeGenerateHelper(command);

};

const getDataFromClock = (args, util) => {
    util.runtime._setup.add(`Wire.begin();\n`);
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

const voltageSense = (args, util) => {
    util.runtime._setup.add(`ade791x_init();\n`);
    return `ade791x_read_v${args.CHANNEL}()`;
};

const currentSense = (args, util) => {
    util.runtime._setup.add(`ade791x_init();\n`);
    return `ade791x_read_im()`;
};

const readTimer = (args, util) => {
    util.runtime.define.add(`double currentTime1 = 0;\ndouble lastTime1 = 0;\n`);
    util.runtime.define.add(`double getLastTime(){\n\treturn currentTime1 = millis()/1000.0 - lastTime1;\n}\n`);
    return `getLastTime()`;
};

const resetTimer = (args, util) => {
    util.runtime.define.add(`double currentTime1 = 0;\ndouble lastTime1 = 0;\n`);
    const command = `lastTime1 = millis()/1000.0;\n`;
	util.runtime.codeGenerateHelper(command);

};

const cast = (args, util) => {
    if (args.OPERATION === '1'){
        return `((int) ${args.VALUE})`;
    } 
    if (args.OPERATION === '2') {
        return `float(${args.VALUE})`;
    }
};

const map = (args, util) => {
    return `map(${args.VALUE}, ${args.RANGE11}, ${args.RANGE12}, ${args.RANGE21}, ${args.RANGE22})`;
};


module.exports = {
    tactileSwitch,
    slideSwitch,
    navigationKey,
    potentiometer,
    touchSensor,
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    playTone,
    setClockTime,
    setClockDate,
    getDataFromClock,
    voltageSense,
    currentSense,
    readTimer,
    resetTimer,
    cast,
    map
};
