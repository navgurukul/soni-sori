const getBoardId = require('../../../util/board-config.js').getBoardId;

const connectToWifi = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        util.runtime.include.add(`#include <ESP8266.h>\n`);
        util.runtime.define.add(`#define SSID        ${args.WIFI}\n#define PASSWORD    ${args.PASSWORD}\nESP8266 wifi(Serial3, 115200);\n`);
        util.runtime._setup.add(`wifi.setOprToStation();\n`);
        util.runtime._setup.add(`wifi.connectWiFi(SSID, PASSWORD);\n`);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        util.runtime.include.add(`#include <WiFi.h>\n`);
        util.runtime.include.add(`#include <ESP32WiFi.h>\n`);
        util.runtime.define.add(`ESP32WiFi wifiClient;\n`);
        util.runtime.define.add(`#define SSID        ${args.WIFI}\n#define PASSWORD    ${args.PASSWORD}\n`);
        util.runtime._setup.add(`WiFi.begin(SSID, PASSWORD);\n`);
        util.runtime._setup.add(`while (WiFi.status() != WL_CONNECTED){\ndelay(500);\n}\npinMode(2,OUTPUT);\ndigitalWrite(2,HIGH);\n`);
    }
};

const getWeatherData = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        const command = `wifi.getWeatherData(KEY, ${args.LATITUDE}, ${args.LONGITUDE});\n`;
        util.runtime.codeGenerateHelper(command);
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        const command = `wifiClient.getWeatherData(KEY, ${args.LATITUDE}, ${args.LONGITUDE});\n`;
        util.runtime.codeGenerateHelper(command);
    }
};

const setAPI = (args, util, parent) => {
    util.runtime.define.add(`#define KEY ${args.API}\n`);
};

const getWeather = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueString(1)`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(1)`;
    }
};

const getTime = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueString(${args.WEATHERLISTS})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
};

const getCity = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueString(4)`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(4)`;
    }
};

const getWeatherValueTemp = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
};

const getCoord = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
};

const getPressure = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueFloat(12)`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(12)`;
    }
};

const getHumidity = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueFloat(5)`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(5)`;
    }
};

const getWind = (args, util, parent) => {
    let boardSelected = getBoardId(util.runtime.boardSelected);
    if (boardSelected === "evive") {
        return `wifi.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
    else if (boardSelected === "esp32" || boardSelected === "tWatch" || boardSelected === "quon" || boardSelected === "quarky") {
        return `wifiClient.getWeatherValueFloat(${args.WEATHERLIST})`;
    }
};

module.exports = {
    connectToWifi,
    getWeatherData,
    getWeather,
    setAPI,
    getWeatherValueTemp,
    getCity,
    getCoord,
    getPressure,
    getHumidity,
    getWind,
    getTime
};