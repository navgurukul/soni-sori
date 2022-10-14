const initialiseDisplay = (args, util) => {
    const dataBuffer = [args.RESET,args.ENABLE,args.PIND4,args.PIND5,args.PIND6,args.PIND7];
    util.runtime.writeToPeripheral(dataBuffer, 124, 2);
    return util.setDelay(1); 
};

const initialiseI2CDisplay = (args, util) => {
    const intAdd = parseInt(args.I2C_ADD);
    const dataBuffer = [intAdd];
    util.runtime.writeToPeripheral(dataBuffer, 125, 2);
    return util.setDelay(1); 
};

const setCursor = (args, util) => {
    const dataBuffer = [args.COLUMN,args.ROW];
    util.runtime.writeToPeripheral(dataBuffer, 126, 2);
    return util.setDelay(1); 
};

const write = (args, util) => {
    const arg = args.TEXT.toString().split('');
    util.runtime.writeToPeripheral(arg.map(ele => ele.charCodeAt(0)), 127, 2);
    return util.setDelay(1);
    
};

const clearDisplay = (args, util) => {
    util.runtime.writeToPeripheral([], 128, 2);
    return util.setDelay(1);
};

const setMode = (args, util) => {
    const dataBuffer = [args.MODE];
    util.runtime.writeToPeripheral(dataBuffer, 129, 2);
    return util.setDelay(1);
};

const initializeTM1637Display = (args, util) => {
    
};

const showNumberTM1637Display = (args, util) => {
    
};

const clearTM1637Display = (args, util) => {
    
};

const initializeDotMatrixDisplay = (args, util) => {
    
};

const displayMatrix = (args, util) => {
    
};

const setLED = (args, util) => {
    
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