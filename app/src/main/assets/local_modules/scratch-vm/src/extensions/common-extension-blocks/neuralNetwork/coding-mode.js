const startTraining = (args, util) => {
    util.runtime.include.add(`#include <neuralNetwork.h>\n`);
    util.runtime.define.add(`neuralNetwork bot(PatternCountNN, InputNodesNN, HiddenNodesNN, OutputNodesNN);\n`);
    const command = `bot.trainNN();\n`;
	util.runtime.codeGenerateHelper(command);
};

const setNodes = (args, util) => {
    util.runtime.define.add(`const int InputNodesNN = ${args.INPUT};\n`);
    util.runtime.define.add(`const int HiddenNodesNN = ${args.HIDDEN};\n`);
    util.runtime.define.add(`const int OutputNodesNN = ${args.OUTPUT};\n`);
};

const setPatternCount = (args, util) => {
    util.runtime.define.add(`const int PatternCountNN = ${args.DATA};\n`);
};

const defineParameter = (args, util) => {
    let command = `bot.defineParameterNN(${args.PARAMETER}, ${args.DATA});\n`;
    util.runtime.codeGenerateHelper(command);
};

const setInputPattern = (args, util) => {
    const command = `bot.setInputPatternNN(${args.PATTERNID}, ${args.DATA1}, ${args.DATA2}, ${args.DATA3}, ${args.DATA4});\n`;
	util.runtime.codeGenerateHelper(command);
};

const setTargetPattern = (args, util) => {
    const command = `bot.setTargetPatternNN(${args.PATTERNID}, ${args.DATA1}, ${args.DATA2}, ${args.DATA3}, ${args.DATA4});\n`;
	util.runtime.codeGenerateHelper(command);
};

const calculateOutput = (args, util) => {
    const command = `bot.calculateOutputNN(${args.DATA1}, ${args.DATA2}, ${args.DATA3}, ${args.DATA4});\n`;
	util.runtime.codeGenerateHelper(command);
};

const getOutput = (args, util) => {
    return `bot.getOutputNN(${args.OUTPUT})`;
};

module.exports = {
    defineParameter,
    startTraining,
    setTargetPattern,
    setInputPattern,
    calculateOutput,
    getOutput,
    setNodes,
    setPatternCount
};