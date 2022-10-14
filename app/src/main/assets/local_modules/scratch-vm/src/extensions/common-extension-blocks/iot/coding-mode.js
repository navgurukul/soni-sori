const getBoardId = require('../../../util/board-config.js').getBoardId;

const connectToWifi = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime.include.add(`#include <ESP8266.h>\n`);
        util.runtime.define.add(`#define SSID        ${args.WIFI}\n#define PASSWORD    ${args.PASSWORD}\nESP8266 wifi(Serial3, 115200);\n`);
        util.runtime._setup.add(`wifi.setOprToStation();\n`);
        util.runtime._setup.add(`wifi.connectWiFi(SSID, PASSWORD);\n`);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon"|| boardSelected === "quarky") {
        util.runtime.include.add(`#include <WiFi.h>\n`);
        util.runtime.define.add(`#define SSID        ${args.WIFI}\n#define PASSWORD    ${args.PASSWORD}\n`);
        util.runtime._setup.add(`WiFi.begin(SSID, PASSWORD);\n`);
        util.runtime._setup.add(`while (WiFi.status() != WL_CONNECTED){\ndelay(500);\n}\npinMode(2,OUTPUT);\ndigitalWrite(2,HIGH);\n`);
    }
};

const connectToAdafruitIO = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime.define.add(`#define X_AIO_KEY   ${args.KEY}\n#define USERNAME    ${args.USERNAME}\n\n#define HOST_NAME   "io.adafruit.com"\n#define HOST_PORT   (80)\n`);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        util.runtime.include.add(`#include <ESP32AdaIO.h>\n`);
        util.runtime.define.add(`ESP32AdaIO esp32AdaIO;\n`);
        util.runtime.define.add(`#define X_AIO_KEY   ${args.KEY}\n#define USERNAME    ${args.USERNAME}\n`);    
    }
};

const createFeedAdafruitIO = (args, util, parent) => {
    let command = ``;
    var lowerCaseFeed = args.FEED;
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        if (args.OPTION === '0'){
            command = `wifi.addAdafruitIO_Feed(X_AIO_KEY, USERNAME, ${lowerCaseFeed}, 200, HOST_NAME, HOST_PORT);\n`;
        } 
        if (args.OPTION === '1') {
            command = `wifi.deleteAdafruitIO_Feed(X_AIO_KEY, USERNAME, ${lowerCaseFeed}, 200, HOST_NAME, HOST_PORT);\n`;
        }
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        if (args.OPTION === '0'){
            command = `esp32AdaIO.addAdafruitIO_Feed(X_AIO_KEY, USERNAME, ${lowerCaseFeed});\n`;
        } 
        if (args.OPTION === '1') {
            command = `esp32AdaIO.deleteAdafruitIO_Feed(X_AIO_KEY, USERNAME, ${lowerCaseFeed});\n`;
        }
        util.runtime.codeGenerateHelper(command);
    }
};

const sendDataAdafruitIONumber = (args, util, parent) => {
    let command = ``;
    var lowerCaseFeed = args.FEED;
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        if (args.TYPE === '0'){
            command = `wifi.sendAdafruitIO_NUMBER(${args.DATA}, X_AIO_KEY, USERNAME, ${lowerCaseFeed}, 200, HOST_NAME, HOST_PORT);\n`;
        } 
        if (args.TYPE === '1') {
            command = `wifi.sendAdafruitIO_STRING(${args.DATA}, X_AIO_KEY, USERNAME, ${lowerCaseFeed}, 200, HOST_NAME, HOST_PORT);\n`;
        }
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        if (args.TYPE === '0'){
            command = `esp32AdaIO.sendAdafruitIO_NUMBER(${args.DATA}, X_AIO_KEY, USERNAME, ${lowerCaseFeed});\n`;
        } 
        if (args.TYPE === '1') {
            command = `esp32AdaIO.sendAdafruitIO_STRING(${args.DATA}, X_AIO_KEY, USERNAME, ${lowerCaseFeed});\n`;
        }
        util.runtime.codeGenerateHelper(command);
    }
};

const getDataAdafruitIO = (args, util, parent) => {
    let command = ``;
    var lowerCaseFeed = args.FEED;
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        if (args.TYPE === '0'){
            command = `wifi.getDataAdafruitIO_NUMBER(X_AIO_KEY, USERNAME, ${lowerCaseFeed}, HOST_NAME, HOST_PORT)`;
        } 
        if (args.TYPE === '1') {
            command = `wifi.getDataAdafruitIO_STRING(X_AIO_KEY, USERNAME, ${lowerCaseFeed}, HOST_NAME, HOST_PORT)`;
        }
        return command;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        if (args.TYPE === '0'){
            command = `esp32AdaIO.getDataAdafruitIO_NUMBER(X_AIO_KEY, USERNAME, ${lowerCaseFeed})`;
        } 
        if (args.TYPE === '1') {
            command = `esp32AdaIO.getDataAdafruitIO_STRING(X_AIO_KEY, USERNAME, ${lowerCaseFeed})`;
        }
        return command;
    }
};

const getColorAdafruitIO = (args, util, parent) => {
    var lowerCaseFeed = args.FEED;
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        let command = `wifi.getDataAdafruitIO_COLOR(X_AIO_KEY, USERNAME, ${lowerCaseFeed}, HOST_NAME, HOST_PORT);\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        let command = `esp32AdaIO.getDataAdafruitIO_COLOR(X_AIO_KEY, USERNAME, ${lowerCaseFeed});\n`;
        util.runtime.codeGenerateHelper(command);
    }
};

const getRGB = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getDataAdafruitIO_COLORVALUE(${args.COLOR})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `esp32AdaIO.getDataAdafruitIO_COLORVALUE(${args.COLOR})`;
    }
};

const connectToThingspeak = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime.define.add(`#define HOST_NAME "api.thingspeak.com"\nlong CHANNEL = ${args.CHANNEL};\n#define TSKEYWRITE ${args.WRITEAPI}\n#define TSKEYREAD ${args.READAPI}\n#define HOST_PORT (80)\n`);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        util.runtime.include.add(`#include <ESP32WiFi.h>\n`);
        util.runtime.define.add(`ESP32WiFi wifiClient;\n`);
        util.runtime.define.add(`long CHANNEL = ${args.CHANNEL};\n#define TSKEYWRITE ${args.WRITEAPI}\n#define TSKEYREAD ${args.READAPI}\n`);
    }
};

const sendDataToCloud = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const command = `wifi.sendString1(1, ${args.DATA}, 0, 0, 0, 0, 0, 0, 0, TSKEYWRITE, ${args.TIME}, HOST_NAME, HOST_PORT);\n`;
	    util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        const command = `wifiClient.updateChannelField(TSKEYWRITE, 1 , ${args.DATA});\n delay(${args.TIME}*1000);\n`;
	    util.runtime.codeGenerateHelper(command);
    }
};

const sendMultipleDataToCloud = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const command = `wifi.sendString1(${args.NUMBER_OF_DATA}, ${args.DATA1}, ${args.DATA2}, ${args.DATA3}, ${args.DATA4}, ${args.DATA5}, ${args.DATA6}, ${args.DATA7}, ${args.DATA8}, TSKEYWRITE, ${args.TIME}, HOST_NAME, HOST_PORT);`;
	    util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        const command = `wifiClient.updateChannel(TSKEYWRITE, ${args.NUMBER_OF_DATA},${args.DATA1},${args.DATA2},${args.DATA3},${args.DATA4},${args.DATA5},${args.DATA6},${args.DATA7},${args.DATA8});\n delay(${args.TIME}*1000);\n`;
	    util.runtime.codeGenerateHelper(command);
    }
};

const getDataFromThingspeak = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const command = `wifi.getDataFromChannel(CHANNEL, TSKEYREAD, HOST_NAME, HOST_PORT);`;
	    util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        const command = `wifiClient.getDataFromChannel(TSKEYREAD,CHANNEL);\n`;
	    util.runtime.codeGenerateHelper(command);
    }
};

const getDataFromField = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getFeildValue(${args.FIELD})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getFieldData(${args.FIELD})`;
    }
};

module.exports = {
    connectToWifi,
    connectToThingspeak,
    sendDataToCloud,
    sendMultipleDataToCloud,
    getDataFromThingspeak,
    getDataFromField,
    connectToAdafruitIO,
    createFeedAdafruitIO,
    sendDataAdafruitIONumber,
    getDataAdafruitIO,
    getColorAdafruitIO,
    getRGB
};