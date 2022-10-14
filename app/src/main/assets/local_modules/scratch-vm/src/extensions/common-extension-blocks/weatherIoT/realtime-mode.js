const nets = require('nets');

const serverTimeoutMs = 10000;
let openweathermapAPI = '29d056d28bf81ce4851606c6675b3288';
let defaultAPI = true;
let currTime = 61;
let firstTime = true;
let jsonBody = null;

let Openweathermap = "https://api.openweathermap.org";

const connectToWifi = (args, util, parent) => {
};

const getWeatherData = (args, util, parent) => {
    
    // Build up URL
    let path = `${Openweathermap}/data/2.5/weather?lat=`;
    path += `${args.LATITUDE}&lon=`;
    path += `${args.LONGITUDE}&appid=`;
    path += `${openweathermapAPI}`;

    if (firstTime){
        firstTime = false;
    }
    else{
        currTime = util.ioQuery('clock', 'projectTimer');
    }

    console.log(currTime)
    if (defaultAPI){
        if (currTime < 30){
            return `Try again after ${Math.ceil(30 - currTime)}s`;
        }
        else{
            util.ioQuery('clock', 'resetProjectTimer');
        }
    }

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                log.warn(`error fetching translate result! ${res}`);
                resolve('Error');
                return 'Error';
            }
            let stringBody = new TextDecoder("utf-8").decode(body.buffer);
            jsonBody = JSON.parse(stringBody);

            console.log(jsonBody);
            resolve('Done');
            return 'Done';
        });
    });

    return translatePromise;
};

const getWeatherValueTemp = (args, util, parent) => {
    if(jsonBody){
        switch (args.WEATHERLIST) {
            case '3':
                if (jsonBody.main.hasOwnProperty('temp')) {
                    tempF = jsonBody.main.temp * 1.8 - 459.67;
                    tempC = (tempF - 32) / 1.8;
                    return tempC.toFixed(2);
                }
                else{
                    return 'Null';
                }
            case '4':
                if (jsonBody.main.hasOwnProperty('temp')) {
                    tempF = jsonBody.main.temp * 1.8 - 459.67;
                    return tempF.toFixed(2);
                }
                else{
                    return 'Null';
                }
            case '10':
                if (jsonBody.main.hasOwnProperty('temp_min')) {
                    tempCMin = jsonBody.main.temp_min - 273.15;
                    return tempCMin.toFixed(2);
                }
                else{
                    return 'Null';
                }
            case '11':
                if (jsonBody.main.hasOwnProperty('temp_max')) {
                    tempCMax = jsonBody.main.temp_max - 273.15;
                    return tempCMax.toFixed(2);
                }
                else{
                    return 'Null';
                }
            case '13':
                if (jsonBody.main.hasOwnProperty('temp_min')) {
                    tempFMin = jsonBody.main.temp_min * 1.8 - 459.67;
                    return tempFMin.toFixed(2);
                }
                else{
                    return 'Null';
                }
            case '14':
                if (jsonBody.main.hasOwnProperty('temp_max')) {
                    tempFMax = jsonBody.main.temp_max * 1.8 - 459.67;
                    return tempFMax.toFixed(2);
                }
                else{
                    return 'Null';
                }
            default:
                return "Error";
        }
    }
    return 'Null';
};

const getWeather = (args, util, parent) => {
    if(jsonBody){
        if (jsonBody.weather[0].main) {
            return jsonBody.weather[0].main;
        }
        else{
            return 'Null';
        }
    }
    return 'Null';
};

const getPressure = (args, util, parent) => {
    if(jsonBody){
        if (jsonBody.main.pressure) {
            return jsonBody.main.pressure;
        }
        else{
            return 'Null';
        }
    }
    return 'Null';
};

const getHumidity = (args, util, parent) => {
    if(jsonBody){
        if (jsonBody.main.humidity) {
            return jsonBody.main.humidity;
        }
        else{
            return 'Null';
        }
    }
    return 'Null';
};

const getCity = (args, util, parent) => {
    if(jsonBody){
        if (jsonBody.name) {
            return jsonBody.name;
        }
        else{
            return 'Null';
        }
    }
    return 'Null';
};

const getCoord = (args, util, parent) => {
    if(jsonBody){
        switch (args.WEATHERLIST) {
            case '1':
                if (jsonBody.coord) {
                    return jsonBody.coord.lat;
                }
                else{
                    return 'Null';
                }
            case '2':
                if (jsonBody.coord) {
                    return jsonBody.coord.lon;
                }
                else{
                    return 'Null';
                }
            default:
                return "Error";
        }
    }
    return 'Null';
};

const getWind = (args, util, parent) => {
    if(jsonBody){
        switch (args.WEATHERLIST) {
            case '7':
                if (jsonBody.wind) {
                    return jsonBody.wind.speed;
                }
                else{
                    return 'Null';
                }
            case '8':
                if (jsonBody.wind) {
                    return jsonBody.wind.deg;
                }
                else{
                    return 'Null';
                }
            default:
                return "Error";
        }
    }
    return 'Null';
};

const getTime = (args, util, parent) => {
    if(jsonBody){
        switch (args.WEATHERLIST) {
            case '5':
                if (jsonBody.hasOwnProperty('dt')) {
                    let unix_timestamp = jsonBody.dt;
                    var date = new Date(unix_timestamp * 1000);
                    return date.toUTCString();
                }
                else{
                    return 'Null';
                }
            case '6':
                if (jsonBody.sys.hasOwnProperty('sunrise')) {
                    let unix_timestamp = jsonBody.sys.sunrise;
                    var date = new Date(unix_timestamp * 1000);
                    return date.toUTCString();
                }
                else{
                    return 'Null';
                }
            case '7':
                if (jsonBody.sys.hasOwnProperty('sunset')) {
                    let unix_timestamp = jsonBody.sys.sunset;
                    var date = new Date(unix_timestamp * 1000);
                    return date.toUTCString();
                }
                else{
                    return 'Null';
                }
            default:
                return "Error";
        }
    }
    return 'Null';
};

const setAPI = (args, util, parent) => {
    let api = args.API;
    if (api.length === 32){
        openweathermapAPI = args.API;
        defaultAPI = false;
        return "API edited"
    }
    else{
        defaultAPI = true;
        return "invalid API"
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
