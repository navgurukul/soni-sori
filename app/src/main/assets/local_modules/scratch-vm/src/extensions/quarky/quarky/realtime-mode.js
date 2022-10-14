const digitalRead = (args, util, parent) => {
    if(parent.runtime.firmwareVer == 1)
    {
        const dataBuffer = [args.PIN, 0];
        return new Promise(resolve => {
            new Promise(resolve => {
                util.runtime.serialWrite(dataBuffer, 30, 1);
                parent._isSerialRead = resolve;
            })
            .then((net) => {
            resolve(net);  
            console.log(Date.now());  
            });
        });
    }
    else
    {
        if (parent.runtime.isVMPreStoreDataAvailable()) {
            if (args.PIN === '18') {
                return parent.runtime.getVMPreStoreData().digital[0];
            }
            else if (args.PIN === '19') {
                return parent.runtime.getVMPreStoreData().digital[1];
            }
            else if (args.PIN === '26') {
                return parent.runtime.getVMPreStoreData().digital[2];
            }
        }
        return false;
    }

};



const analogRead = (args, util, parent) => {
    if(parent.runtime.firmwareVer == 1)
    {
        const dataBuffer = [args.PIN, 0];
        return new Promise(resolve => {
            util.runtime.serialWrite(dataBuffer, 31, 1);
            parent._isSerialRead = resolve;
        });
    }
    else
    {
        if (parent.runtime.isVMPreStoreDataAvailable()) {
            if (args.PIN === '33') {
                return parent.runtime.getVMPreStoreData().analog1;
            }
            else if (args.PIN === '32') {
                return parent.runtime.getVMPreStoreData().analog2;
            }
            else if (args.PIN === '39') {
                return parent.runtime.getVMPreStoreData().analog3;
            }
        }
        return false;
    }
};

const digitalWrite = (args, util) => {
    const dataBuffer = [args.PIN, args.STATE];
    util.runtime.writeToPeripheral(dataBuffer, 30, 2);
    return util.setSendDelay();
};

const setPWM = (args, util) => {
    const arg = [args.PIN, args.VALUE];
    util.runtime.writeToPeripheral(arg, 32, 2);
    return util.setSendDelay();
};

const bluetoothIndicator = (args, util, parent) => {
    const dataBuffer = [args.STATE, 0];
    util.runtime.writeToPeripheral(dataBuffer, 164, 2);
    return util.setSendDelay();
};

const map = (args, util) => {
    let val = ((args.VALUE - args.RANGE11) * (args.RANGE22 - args.RANGE21)) / ((args.RANGE12 - args.RANGE11) + args.RANGE21);
    return val.toFixed(2);
};

const deepSleepTimer = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};

const deepSleepExternalSource = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};

const deepSleepTouchPins = (args, util, parent) => {
    console.log("Deep Sleep Timer");
};

const enableDeepSleep = (args, util, parent) => {
    console.log("Deep Sleep Enabled");
};
// const pushButtons = (args, util,parent) => {
//     const dataBuffer = [args.TACTILE_SW,0];
//     return new Promise(resolve => {
//        util.runtime.writeToPeripheral(dataBuffer, 135, 1);
//        parent._isSerialRead = resolve;
//    });
// };

// const touchRead = (args, util,parent) => {
//     const dataBuffer = [args.TOUCHPIN,0];
//     return new Promise(resolve => {
//        util.runtime.writeToPeripheral(dataBuffer, 132, 1);
//        parent._isSerialRead = resolve;
//    });
// };

// const getMacAddr = (args, util,parent) => {
//     return new Promise(resolve => {
//        util.runtime.writeToPeripheral(0, 131, 1);
//        parent._isSerialRead = resolve;
//    });
// };

// const playTone = (args, util) => {
//     const arg = [args.PIN, args.NOTE, args.BEATS];
//     util.runtime.writeToPeripheral(arg, 34, 2);
//     return util.setSendDelay();
// };

const cast = (args, util) => {
    if (args.OPERATION === '1') {
        return Math.ceil(args.VALUE);
    }
    if (args.OPERATION === '2') {
        return args.VALUE;
    }
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
    //getMacAddr,
    //playTone,
    //pushButtons,
    //touchRead
};
