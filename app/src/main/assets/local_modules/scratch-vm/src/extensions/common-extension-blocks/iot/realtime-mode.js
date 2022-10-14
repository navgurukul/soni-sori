const nets = require('nets');

const serverTimeoutMs = 10000;

let AIO_ServerHost = "https://io.adafruit.com";
let TS_ServerHost = "https://api.thingspeak.com";
let IFTTT_ServerHost = "https://maker.ifttt.com";

let AIO_Username = null;
let AIO_Key = null;

let TS_Channel = null;
let TS_WriteAPI = null;
let TS_ReadAPI = null;

let IFTTT_Webhook_Key = null;

let field1 = -100;
let field2 = -100;
let field3 = -100;
let field4 = -100;
let field5 = -100;
let field6 = -100;
let field7 = -100;
let field8 = -100;

let red = 0;
let green = 0;
let blue = 0;

let API_RequestBody = "";
let API_Body = "";
let API_Error = "";
let API_ResponseCoode = "";

const connectToWifi = (args, util, parent) => {
};

const connectToThingspeak = (args, util, parent) => {
    TS_Channel = args.CHANNEL;
    TS_ReadAPI = args.READAPI;
    TS_WriteAPI = args.WRITEAPI;
};

const sendDataToCloud = (args, util, parent) => {

    // Build up URL
    let path = `${TS_ServerHost}/update?api_key=`;
    path += `${TS_WriteAPI}&field1=`;
    path += `${args.DATA}`;

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                resolve('Error');
                return 'Error';
            }

            if (res.statusCode === 200) {
                resolve('Done');
                return 'Done';
            }
        });
    });

    let delay = args.TIME*1000;
    
    return util.setDelay(delay);
};

const sendMultipleDataToCloud = (args, util, parent) => {
    // Build up URL
    let path = `${TS_ServerHost}/update?api_key=`;
    path += `${TS_WriteAPI}`;
    
    switch(args.NUMBER_OF_DATA) {
        case '1':
            path += `&field1=${args.DATA1}`;
            break;
        case '2':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            break;
        case '3':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            break;
        case '4':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            path += `&field4=${args.DATA4}`;
            break;
        case '5':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            path += `&field4=${args.DATA4}`;
            path += `&field5=${args.DATA5}`;
            break;
        case '6':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            path += `&field4=${args.DATA4}`;
            path += `&field5=${args.DATA5}`;
            path += `&field6=${args.DATA6}`;
            break;
        case '7':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            path += `&field4=${args.DATA4}`;
            path += `&field5=${args.DATA5}`;
            path += `&field6=${args.DATA6}`;
            path += `&field7=${args.DATA7}`;
            break;
        case '8':
            path += `&field1=${args.DATA1}`;
            path += `&field2=${args.DATA2}`;
            path += `&field3=${args.DATA3}`;
            path += `&field4=${args.DATA4}`;
            path += `&field5=${args.DATA5}`;
            path += `&field6=${args.DATA6}`;
            path += `&field7=${args.DATA7}`;
            path += `&field8=${args.DATA8}`;
            break;
        default:
            return "Number of data out of range";
    }
       
    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                resolve('Error');
                return 'Error';
            }

            if (res.statusCode === 200) {
                resolve('Done');
                return 'Done';
            }
        });
    });

    let delay = args.TIME*1000;
    
    return util.setDelay(delay);
};

const getDataFromThingspeak = (args, util, parent) => {
    // Build up URL
    let path = `${TS_ServerHost}/channels/`;
    path += `${TS_Channel}/feeds.json?api_key=`;
    path += `${TS_ReadAPI}&results=1`;

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                log.warn(`error fetching translate result! ${res}`);
                resolve('');
                return '';
            }
            let stringBody = new TextDecoder("utf-8").decode(body.buffer);
            let jsonBody = JSON.parse(stringBody);

            if (jsonBody.hasOwnProperty('feeds')) {
                if (jsonBody.feeds[0].hasOwnProperty('field1')) {
                    field1 = jsonBody.feeds[0].field1;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field2')) {
                    field2 = jsonBody.feeds[0].field2;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field3')) {
                    field3 = jsonBody.feeds[0].field3;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field4')) {
                    field4 = jsonBody.feeds[0].field4;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field5')) {
                    field5 = jsonBody.feeds[0].field5;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field6')) {
                    field6 = jsonBody.feeds[0].field6;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field7')) {
                    field7 = jsonBody.feeds[0].field7;
                }
                if (jsonBody.feeds[0].hasOwnProperty('field8')) {
                    field8 = jsonBody.feeds[0].field8;
                }
            }

            console.log(jsonBody);
            resolve('');
            return '';
        });
    });

    return translatePromise;
};

const getDataFromField = (args, util, parent) => {
    switch (args.FIELD) {
        case '1':
            return field1;
        case '2':
            return field2;
        case '3':
            return field3;
        case '4':
            return field4;
        case '5':
            return field5;
        case '6':
            return field6;
        case '7':
            return field7;
        case '8':
            return field8;
        default:
            return "Error";
    }    
};

const connectToAdafruitIO = (args, util, parent) => {
    AIO_Username = args.USERNAME;
    AIO_Username = AIO_Username.replace(`"`, "");
    AIO_Username = AIO_Username.replace(`"`, "");
    AIO_Key = args.KEY;
    AIO_Key = AIO_Key.replace(`"`, "");
    AIO_Key = AIO_Key.replace(`"`, "");
};

const createFeedAdafruitIO = (args, util, parent) => {

    if (args.OPTION === '0') {

        // Build up URL
        let path = `${AIO_ServerHost}/api/v2/`;
        path += `${AIO_Username}/feeds?X-AIO-Key=`;
        path += `${AIO_Key}`;

        let requestBody = `{"name": "${args.FEED}"}`;

        // Perform HTTP request
        const translatePromise = new Promise(resolve => {
            nets({
                body: requestBody,
                url: path,
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                timeout: serverTimeoutMs,
            }, (err, res, body) => {
                if (err) {
                    resolve('Error');
                    return 'Error';
                }

                let stringBody = new TextDecoder("utf-8").decode(body.buffer);
                let jsonBody = JSON.parse(stringBody);
                console.log(jsonBody);

                if (jsonBody.status === 'online') {
                    resolve('Feed created');
                    return 'Feed created';
                }

                if (jsonBody.status === 'bad_request') {
                    resolve('Bad request');
                    return 'Bad request';
                }

            });
        });

        return translatePromise;
    }

    if (args.OPTION === '1') {
        var lowerCaseFeed = args.FEED;
        lowerCaseFeed = lowerCaseFeed.toLowerCase();
        lowerCaseFeed = lowerCaseFeed.replace(" ", "-");

        // Build up URL
        let path = `${AIO_ServerHost}/api/v2/`;
        path += `${AIO_Username}/feeds/`;
        path += `${lowerCaseFeed}?X-AIO-Key=`;
        path += `${AIO_Key}`;

        // Perform HTTP request
        const translatePromise = new Promise(resolve => {
            nets({
                url: path,
                method: "DELETE",
                timeout: serverTimeoutMs,
            }, (err, res, body) => {
                if (err) {
                    resolve('Error');
                    return 'Error';
                }

                let stringBody = new TextDecoder("utf-8").decode(body.buffer);
                let jsonBody = JSON.parse(stringBody);
                console.log(jsonBody);

                if (jsonBody.hasOwnProperty('error')) {
                    resolve(jsonBody.error);
                    return jsonBody.error;
                }

                if (jsonBody.status === 'online') {
                    resolve('Feed deleted');
                    return 'Feed deleted';
                }

                resolve('Error');
                return 'Error';

            });
        });

        return translatePromise;
    }
};

const sendDataAdafruitIONumber = (args, util, parent) => {

    var lowerCaseFeed = args.FEED;
    lowerCaseFeed = lowerCaseFeed.toLowerCase();
    lowerCaseFeed = lowerCaseFeed.replace(" ", "-");

    // Build up URL
    let path = `${AIO_ServerHost}/api/v2/`;
    path += `${AIO_Username}/feeds/`;
    path += `${lowerCaseFeed}/data.json?X-AIO-Key=`;
    path += `${AIO_Key}`;

    let requestBody = `{"value": "${args.DATA}"}`;

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            body: requestBody,
            url: path,
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                resolve('Error');
                return 'Error';
            }

            if (res.statusCode === 200) {
                resolve('Done');
                return 'Done';
            }
        });
    });

    return translatePromise;
};

const sendDataAdafruitIOString = (args, util, parent) => {
    var lowerCaseFeed = args.FEED;
    lowerCaseFeed = lowerCaseFeed.toLowerCase();
    lowerCaseFeed = lowerCaseFeed.replace(" ", "-");

    // Build up URL
    let path = `${AIO_ServerHost}/api/v2/`;
    path += `${AIO_Username}/feeds/`;
    path += `${lowerCaseFeed}/data.json?X-AIO-Key=`;
    path += `${AIO_Key}`;

    let requestBody = `{"value": "${args.DATA}"}`;

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            body: requestBody,
            url: path,
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                resolve('Error');
                return 'Error';
            }

            if (res.statusCode === 200) {
                resolve('Done');
                return 'Done';
            }
        });
    });

    return translatePromise;
};

const getDataAdafruitIO = (args, util, parent) => {

    var lowerCaseFeed = args.FEED;
    lowerCaseFeed = lowerCaseFeed.toLowerCase();
    lowerCaseFeed = lowerCaseFeed.replace(" ", "-");

    // Build up URL
    let path = `${AIO_ServerHost}/api/v2/`;
    path += `${AIO_Username}/feeds/`;
    path += `${lowerCaseFeed}/data/last?X-AIO-Key=`;
    path += `${AIO_Key}`;

    console.log(path);

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                log.warn(`error fetching translate result! ${res}`);
                resolve('');
                return '';
            }
            let stringBody = new TextDecoder("utf-8").decode(body.buffer);
            let jsonBody = JSON.parse(stringBody);
            console.log(jsonBody);
            resolve(jsonBody.value);
            return jsonBody.value;
        });
    });
    return translatePromise;
};

const getColorAdafruitIO = (args, util, parent) => {

    var lowerCaseFeed = args.FEED;
    lowerCaseFeed = lowerCaseFeed.toLowerCase();
    lowerCaseFeed = lowerCaseFeed.replace(" ", "-");

    // Build up URL
    let path = `${AIO_ServerHost}/api/v2/`;
    path += `${AIO_Username}/feeds/`;
    path += `${lowerCaseFeed}/data/last?X-AIO-Key=`;
    path += `${AIO_Key}`;

    // Perform HTTP request
    const translatePromise = new Promise(resolve => {
        nets({
            url: path,
            method: "GET",
            timeout: serverTimeoutMs,
        }, (err, res, body) => {
            if (err) {
                log.warn(`error fetching translate result! ${res}`);
                resolve('');
                return '';
            }
            let stringBody = new TextDecoder("utf-8").decode(body.buffer);
            let jsonBody = JSON.parse(stringBody);
            console.log(jsonBody);
            let rgbValue = jsonBody.value;
            if(rgbValue.length === 7){
                let rHex = "0x" + rgbValue.substring(1,3);
                let gHex = "0x" + rgbValue.substring(3,5);
                let bHex = "0x" + rgbValue.substring(5,7);
                red = parseInt(rHex);
                green = parseInt(gHex);
                blue = parseInt(bHex);
                resolve(jsonBody.value);
                return jsonBody.value;
            }
            else{
                console.log("Color code incorrect")
                resolve("Error");
                return "Error";
            }
        });
    });
    return translatePromise;
};

const getRGB = (args, util, parent) => {
    switch(args.COLOR){
        case '1':
            return red;
        case '2':
            return green;
        case '3':
            return blue;
    }
};

const makeAPIRequest = (args, util, parent) => {
    
    const translatePromise = new Promise(resolve => {
        API_RequestBody = "";
        API_Error = "";
        API_ResponseCoode = "";
        if(args.REQUEST === '1'){
            if(args.BODYOPTION === '1'){
                nets({
                    body: API_Body,
                    url: args.URL,
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
            else if(args.BODYOPTION === '2'){
                nets({
                    url: args.URL,
                    method: "GET",
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
        }
        else if(args.REQUEST === '2'){
            if(args.BODYOPTION === '1'){
                nets({
                    body: API_Body,
                    url: args.URL,
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
            else if(args.BODYOPTION === '2'){
                nets({
                    url: args.URL,
                    method: "POST",
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
        }
        else if(args.REQUEST === '3'){
            if(args.BODYOPTION === '1'){
                nets({
                    body: API_Body,
                    url: args.URL,
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
            else if(args.BODYOPTION === '2'){
                nets({
                    url: args.URL,
                    method: "DELETE",
                    timeout: serverTimeoutMs,
                }, (err, res, body) => {
                    API_ResponseCoode = res.statusCode;
                    API_RequestBody = new TextDecoder("utf-8").decode(body.buffer);
                    console.log(API_ResponseCoode);
                    console.log(API_RequestBody);
                    if (res.statusCode === 200) {
                        resolve('Done');
                        return 'Done';
                    }
                    if (res.statusCode !== 200) {
                        let jsonBody = JSON.parse(API_RequestBody);
                        API_Error = jsonBody.error;
                        console.log(API_Error);
                        resolve('Error');
                        return 'Error';
                    }
                    resolve('');
                    return '';
                });
            }
        }
    });

    return translatePromise;
};

const setAPIBody = (args, util, parent) => {
    API_Body = args.BODY;
};

const requestAPICode = (args, util, parent) => {
    return API_ResponseCoode
};

const getResponse = (args, util, parent) => {
    if(args.RESPONSE === '1'){
        return API_RequestBody;
    }
    else {
        return API_Error;
    }
};

const getAPIJson = (args, util, parent) => {
    let jsonBody = JSON.parse(API_RequestBody);
    if (jsonBody.hasOwnProperty(args.JSON1)) {
        if(!args.JSON2){
            return JSON.stringify(jsonBody[args.JSON1]);
        }
        else {
            if (jsonBody[args.JSON1].hasOwnProperty(args.JSON2)) {
                if(!args.JSON3){
                    return JSON.stringify(jsonBody[args.JSON1][args.JSON2]);
                }
                else {
                    if (jsonBody[args.JSON1][args.JSON2].hasOwnProperty(args.JSON3)) {
                        return JSON.stringify(jsonBody[args.JSON1][args.JSON2][args.JSON3]);
                    }
                }
            }
        }
    }
    return "Property not found";
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
    sendDataAdafruitIOString,
    getDataAdafruitIO,
    getColorAdafruitIO,
    getRGB,
    makeAPIRequest,
    setAPIBody,
    requestAPICode,
    getResponse,
    getAPIJson
};
