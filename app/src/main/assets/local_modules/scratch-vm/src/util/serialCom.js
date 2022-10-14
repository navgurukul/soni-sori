const sendPackage = (argList, type, mode) => {
    let bytes = [0xff, 0x55, 0x00, 0x00, mode, type];
    for (let i = 0; i < argList.length; ++i) {
        const val = argList[i];
        //console.log(Array.isArray(val));
        if (Array.isArray(val)) {
            bytes = bytes.concat(val);
        } else {
            bytes.push(val);
        }
    }
    // console.log(bytes);
    bytes[2] = bytes.length - 3;
    return bytes;
};

module.exports = {
    sendPackage
};
