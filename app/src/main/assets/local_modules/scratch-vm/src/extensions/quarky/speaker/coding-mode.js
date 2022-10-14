const playSound = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    util.runtime._loop.add(`Quarky.AudioRefresh();\n`);
    const command = `Quarky.play("/${args.SOUND}.wav");\n`;
    util.runtime.codeGenerateHelper(command);
}

const playSoundUntilSound = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    util.runtime._loop.add(`Quarky.AudioRefresh();\n`);
    const command = `Quarky.playUntilDone("/${args.SOUND}.wav");\n`;
    util.runtime.codeGenerateHelper(command);
}

const playNote = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
   // util.runtime.include.add(`const char NOTE_${args.NOTE}[] = "${args.NOTE}:d=${args.NOTE_DURATION},o=4,b=${args.TEMPO}:${args.NOTE_DURATION}${args.NOTE}";\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    util.runtime._loop.add(`Quarky.AudioRefresh();\n`);
    const command = `Quarky.playNote("${args.NOTE}",${args.NOTE_DURATION});\n`;
    util.runtime.codeGenerateHelper(command);
}

// const setNoteTempo = (args, util) => {
//     util.runtime.include.add(`#include <quarky.h>\n`);
//    // util.runtime.include.add(`const char NOTE_${args.NOTE}[] = "${args.NOTE}:d=${args.NOTE_DURATION},o=4,b=${args.TEMPO}:${args.NOTE_DURATION}${args.NOTE}";\n`);
//     util.runtime._setup.add(`Quarky.begin();\n`);
//     util.runtime._loop.add(`Quarky.AudioRefresh();\n`);
//     const command = `Quarky.setTempo(${args.TEMPO});\n`;
//     util.runtime.codeGenerateHelper(command);
// }

const stopSound = (args, util) => {
    util.runtime.include.add(`#include <quarky.h>\n`);
    util.runtime._setup.add(`Quarky.begin();\n`);
    const command = `Quarky.stop();\n`;
    util.runtime.codeGenerateHelper(command);
}

module.exports = {
    playSound,
    playSoundUntilSound,
    playNote,
    // setNoteTempo,
    stopSound    
};
