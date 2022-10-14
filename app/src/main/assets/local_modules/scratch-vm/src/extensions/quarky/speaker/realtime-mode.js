const playSound = (args, util) => {
    // console.log('playSound');
    const arg = args.SOUND.split('');
    console.log(arg);
    const dataBuffer = [arg.map(ele => ele.charCodeAt(0))];
    util.runtime.writeToPeripheral(dataBuffer, 148, 2);
    return util.setSendDelay();
}

const playSoundUntilSound = (args, util) => {
    //console.log('playSoundUntilSound');
    const arg = args.SOUND.split('');
    const dataBuffer = [arg.map(ele => ele.charCodeAt(0))];
    util.runtime.writeToPeripheral(dataBuffer, 149, 2);
    return util.setSendDelay();
}

const playNote = (args, util) => {
    //console.log('playSoundUntilSound');
    const arg = args.NOTE.split('');
    const arg1 = [arg.map(ele => ele.charCodeAt(0))];
    const dataBuffer = [...arg1,args.NOTE_DURATION];
    util.runtime.writeToPeripheral(dataBuffer, 34, 2);
    return util.setSendDelay();
}

const stopSound = (args, util) => {
    util.runtime.writeToPeripheral(0, 150, 2);
    return util.setSendDelay();
}

// const setNoteTempo = (args, util) => {
//     const dataBuffer = [args.TEMPO,0];
//     console.log("Data Buffer");
//     console.log(dataBuffer);
//     util.runtime.writeToPeripheral(dataBuffer, 157, 2);
//     return util.setSendDelay();
// }

module.exports = {
    playSound,
    playSoundUntilSound,
    playNote,
    //setNoteTempo,
    stopSound
};
