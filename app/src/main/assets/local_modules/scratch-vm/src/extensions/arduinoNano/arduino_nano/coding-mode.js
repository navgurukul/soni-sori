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

const readTimer = (args, util) => {
    util.runtime.define.add(`double currentTime1 = 0;\ndouble lastTime1 = 0;\ndouble getLastTime(){\n\treturn currentTime1 = millis()/1000.0 - lastTime1;\n}\n`);
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
    digitalRead,
    analogRead,
    digitalWrite,
    setPWM,
    playTone,
    readTimer,
    resetTimer,
    cast,
    map
};
