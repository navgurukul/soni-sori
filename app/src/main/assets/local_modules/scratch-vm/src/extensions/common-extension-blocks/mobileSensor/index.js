const ArgumentType = require("../../../extension-support/argument-type");
const BlockType = require("../../../extension-support/block-type");
const formatMessage = require("format-message");
const Runtime = require("../../../engine/runtime");
const Video = require("../../../io/video");
const StageLayering = require("../../../engine/stage-layering");

const quarkyRobot = require("../../quarky/quarkyRobot");

// import MobilePlatformController from "mobile";

// var isIos = false;

const blockIconURI =
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwIDAgNDAgNDAiPjxkZWZzPjxjbGlwUGF0aCBpZD0iYSI+PHJlY3QgeD0iLTY3MCIgeT0iNzAiIHdpZHRoPSI2MDAiIGhlaWdodD0iMzcxIiBzdHlsZT0iZmlsbDpub25lIi8+PC9jbGlwUGF0aD48Y2xpcFBhdGggaWQ9ImIiPjxyZWN0IHg9Ii0yMy44MiIgeT0iNC4yMSIgd2lkdGg9IjU3LjY4IiBoZWlnaHQ9IjQzLjU3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMjEuNDUgMjUuMikgcm90YXRlKC03Ny4zNSkiIHN0eWxlPSJmaWxsOm5vbmUiLz48L2NsaXBQYXRoPjxjbGlwUGF0aCBpZD0iYyI+PHJlY3QgeD0iLTcuNjgiIHk9IjAuMTYiIHdpZHRoPSI1Ny42OCIgaGVpZ2h0PSI0My41NyIgc3R5bGU9ImZpbGw6bm9uZSIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJmIj48cmVjdCB4PSItMTguNSIgeT0iLTMuNDgiIHdpZHRoPSI1Ny42OCIgaGVpZ2h0PSI0My41NyIgc3R5bGU9ImZpbGw6bm9uZSIvPjwvY2xpcFBhdGg+PC9kZWZzPjx0aXRsZT5BcnRib2FyZCA3MTwvdGl0bGU+PGcgc3R5bGU9Im9wYWNpdHk6MC4xIj48ZyBzdHlsZT0iY2xpcC1wYXRoOnVybCgjYSkiPjxyZWN0IHg9Ii0xNjAuNzYiIHk9Ii0xMDYuNjciIHdpZHRoPSI0MjkuNjIiIGhlaWdodD0iMjA1LjgzIiBzdHlsZT0iZmlsbDpub25lIi8+PGNpcmNsZSBjeD0iNDEuOTIiIGN5PSI1OS43MyIgcj0iMjAiIHN0eWxlPSJmaWxsOiM1NGQyZWYiLz48L2c+PC9nPjxjaXJjbGUgY3g9Ii0zNzEiIGN5PSIyNzQiIHI9IjM2NyIgc3R5bGU9ImZpbGw6bm9uZTtzdHJva2U6IzAwYTc5ZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6NTBweDtvcGFjaXR5OjAuMSIvPjxwYXRoIGQ9Ik0xMC41OSwzNS44NEgyMC43NWEyLjU4LDIuNTgsMCwwLDAsMi41OS0yLjU5VjYuODVhMi41OCwyLjU4LDAsMCwwLTIuNTktMi41OEgxMC41OUEyLjU5LDIuNTksMCwwLDAsOCw2Ljg1VjMzLjI0QTIuNiwyLjYsMCwwLDAsMTAuNTksMzUuODRaIiBzdHlsZT0iZmlsbDojNDE0MDQyIi8+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2IpIj48Y2lyY2xlIGN4PSIxNS42MSIgY3k9IjM0LjA2IiByPSIxLjE5IiBzdHlsZT0iZmlsbDojNmQ2ZTcxIi8+PC9nPjxnIHN0eWxlPSJjbGlwLXBhdGg6dXJsKCNjKSI+PHBhdGggZD0iTTcuOSwxMy4zOWguMTVWMTFINy45YS4yNi4yNiwwLDAsMC0uMjYuMjZ2MS44M0EuMjYuMjYsMCwwLDAsNy45LDEzLjM5WiIgc3R5bGU9ImZpbGw6IzZkNmU3MSIvPjwvZz48ZyBzdHlsZT0iY2xpcC1wYXRoOnVybCgjYykiPjxwYXRoIGQ9Ik03LjksMTYuMzFoLjE1VjE0SDcuOWEuMjYuMjYsMCwwLDAtLjI2LjI1djEuODRBLjI1LjI1LDAsMCwwLDcuOSwxNi4zMVoiIHN0eWxlPSJmaWxsOiM2ZDZlNzEiLz48L2c+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2MpIj48cGF0aCBkPSJNMTQuMjIsNi4zOUgxN2EuMi4yLDAsMCwwLC4yMS0uMmgwQS4yMS4yMSwwLDAsMCwxNyw2aC0yLjhhLjIxLjIxLDAsMCwwLS4yMS4yMWgwQS4yLjIsMCwwLDAsMTQuMjIsNi4zOVoiIHN0eWxlPSJmaWxsOiMyMzFmMjAiLz48L2c+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2YpIj48cmVjdCB4PSI4Ljc1IiB5PSI4LjAyIiB3aWR0aD0iMTQuMDEiIGhlaWdodD0iMjQuMjIiIHN0eWxlPSJmaWxsOiNmMWYyZjI7b3BhY2l0eTowLjkzOTk5OTk5NzYxNTgxNDtpc29sYXRpb246aXNvbGF0ZSIvPjwvZz48cG9seWxpbmUgcG9pbnRzPSIxNC40MiAyMi40NyAxNi4yNiAyMi40NyAxOC44MiAxNC4yNCAyMC44IDI3LjAyIDI0LjIyIDE3LjkzIDI1LjkzIDIyLjQ3IDI4LjEyIDIyLjQ3IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZTojZmZkMjAwO3N0cm9rZS1taXRlcmxpbWl0OjEwIi8+PGNpcmNsZSBjeD0iMjkuNDciIGN5PSIyMi4zMyIgcj0iMS45MiIgc3R5bGU9ImZpbGw6I2ZmZDIwMCIvPjxwYXRoIGQ9Ik0zMi45MywxOC42M2MtLjYtLjYtMS41My4zNC0uOTMuOTNhNC4yOSw0LjI5LDAsMCwxLDAsNi4xNGMtLjYxLjU5LjMyLDEuNTIuOTMuOTNhNS41Myw1LjUzLDAsMCwwLDAtOFoiIHN0eWxlPSJmaWxsOiNmZmQyMDAiLz48cGF0aCBkPSJNMzQuNjgsMTYuNTdjLS41OC0uNjMtMS41MS4zMS0uOTMuOTNhNy42OSw3LjY5LDAsMCwxLDIuMDYsNWMwLDEuNzItLjUsNC4wNy0yLDUuMTUtLjY4LjUsMCwxLjY0LjY3LDEuMTQsMS44My0xLjM0LDIuNjMtMy45MSwyLjYzLTYuMUE5LDksMCwwLDAsMzQuNjgsMTYuNTdaIiBzdHlsZT0iZmlsbDojZmZkMjAwIi8+PC9zdmc+";

const menuIconURI =
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwIDAgNDAgNDAiPjxkZWZzPjxjbGlwUGF0aCBpZD0iYSI+PHJlY3QgeD0iLTY3MCIgeT0iNzAiIHdpZHRoPSI2MDAiIGhlaWdodD0iMzcxIiBzdHlsZT0iZmlsbDpub25lIi8+PC9jbGlwUGF0aD48Y2xpcFBhdGggaWQ9ImIiPjxyZWN0IHg9Ii0yMy44MiIgeT0iNC4yMSIgd2lkdGg9IjU3LjY4IiBoZWlnaHQ9IjQzLjU3IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMjEuNDUgMjUuMikgcm90YXRlKC03Ny4zNSkiIHN0eWxlPSJmaWxsOm5vbmUiLz48L2NsaXBQYXRoPjxjbGlwUGF0aCBpZD0iYyI+PHJlY3QgeD0iLTcuNjgiIHk9IjAuMTYiIHdpZHRoPSI1Ny42OCIgaGVpZ2h0PSI0My41NyIgc3R5bGU9ImZpbGw6bm9uZSIvPjwvY2xpcFBhdGg+PGNsaXBQYXRoIGlkPSJmIj48cmVjdCB4PSItMTguNSIgeT0iLTMuNDgiIHdpZHRoPSI1Ny42OCIgaGVpZ2h0PSI0My41NyIgc3R5bGU9ImZpbGw6bm9uZSIvPjwvY2xpcFBhdGg+PC9kZWZzPjx0aXRsZT5BcnRib2FyZCA3MTwvdGl0bGU+PGcgc3R5bGU9Im9wYWNpdHk6MC4xIj48ZyBzdHlsZT0iY2xpcC1wYXRoOnVybCgjYSkiPjxyZWN0IHg9Ii0xNjAuNzYiIHk9Ii0xMDYuNjciIHdpZHRoPSI0MjkuNjIiIGhlaWdodD0iMjA1LjgzIiBzdHlsZT0iZmlsbDpub25lIi8+PGNpcmNsZSBjeD0iNDEuOTIiIGN5PSI1OS43MyIgcj0iMjAiIHN0eWxlPSJmaWxsOiM1NGQyZWYiLz48L2c+PC9nPjxjaXJjbGUgY3g9Ii0zNzEiIGN5PSIyNzQiIHI9IjM2NyIgc3R5bGU9ImZpbGw6bm9uZTtzdHJva2U6IzAwYTc5ZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6NTBweDtvcGFjaXR5OjAuMSIvPjxwYXRoIGQ9Ik0xMC41OSwzNS44NEgyMC43NWEyLjU4LDIuNTgsMCwwLDAsMi41OS0yLjU5VjYuODVhMi41OCwyLjU4LDAsMCwwLTIuNTktMi41OEgxMC41OUEyLjU5LDIuNTksMCwwLDAsOCw2Ljg1VjMzLjI0QTIuNiwyLjYsMCwwLDAsMTAuNTksMzUuODRaIiBzdHlsZT0iZmlsbDojNDE0MDQyIi8+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2IpIj48Y2lyY2xlIGN4PSIxNS42MSIgY3k9IjM0LjA2IiByPSIxLjE5IiBzdHlsZT0iZmlsbDojNmQ2ZTcxIi8+PC9nPjxnIHN0eWxlPSJjbGlwLXBhdGg6dXJsKCNjKSI+PHBhdGggZD0iTTcuOSwxMy4zOWguMTVWMTFINy45YS4yNi4yNiwwLDAsMC0uMjYuMjZ2MS44M0EuMjYuMjYsMCwwLDAsNy45LDEzLjM5WiIgc3R5bGU9ImZpbGw6IzZkNmU3MSIvPjwvZz48ZyBzdHlsZT0iY2xpcC1wYXRoOnVybCgjYykiPjxwYXRoIGQ9Ik03LjksMTYuMzFoLjE1VjE0SDcuOWEuMjYuMjYsMCwwLDAtLjI2LjI1djEuODRBLjI1LjI1LDAsMCwwLDcuOSwxNi4zMVoiIHN0eWxlPSJmaWxsOiM2ZDZlNzEiLz48L2c+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2MpIj48cGF0aCBkPSJNMTQuMjIsNi4zOUgxN2EuMi4yLDAsMCwwLC4yMS0uMmgwQS4yMS4yMSwwLDAsMCwxNyw2aC0yLjhhLjIxLjIxLDAsMCwwLS4yMS4yMWgwQS4yLjIsMCwwLDAsMTQuMjIsNi4zOVoiIHN0eWxlPSJmaWxsOiMyMzFmMjAiLz48L2c+PGcgc3R5bGU9ImNsaXAtcGF0aDp1cmwoI2YpIj48cmVjdCB4PSI4Ljc1IiB5PSI4LjAyIiB3aWR0aD0iMTQuMDEiIGhlaWdodD0iMjQuMjIiIHN0eWxlPSJmaWxsOiNmMWYyZjI7b3BhY2l0eTowLjkzOTk5OTk5NzYxNTgxNDtpc29sYXRpb246aXNvbGF0ZSIvPjwvZz48cG9seWxpbmUgcG9pbnRzPSIxNC40MiAyMi40NyAxNi4yNiAyMi40NyAxOC44MiAxNC4yNCAyMC44IDI3LjAyIDI0LjIyIDE3LjkzIDI1LjkzIDIyLjQ3IDI4LjEyIDIyLjQ3IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZTojZmZkMjAwO3N0cm9rZS1taXRlcmxpbWl0OjEwIi8+PGNpcmNsZSBjeD0iMjkuNDciIGN5PSIyMi4zMyIgcj0iMS45MiIgc3R5bGU9ImZpbGw6I2ZmZDIwMCIvPjxwYXRoIGQ9Ik0zMi45MywxOC42M2MtLjYtLjYtMS41My4zNC0uOTMuOTNhNC4yOSw0LjI5LDAsMCwxLDAsNi4xNGMtLjYxLjU5LjMyLDEuNTIuOTMuOTNhNS41Myw1LjUzLDAsMCwwLDAtOFoiIHN0eWxlPSJmaWxsOiNmZmQyMDAiLz48cGF0aCBkPSJNMzQuNjgsMTYuNTdjLS41OC0uNjMtMS41MS4zMS0uOTMuOTNhNy42OSw3LjY5LDAsMCwxLDIuMDYsNWMwLDEuNzItLjUsNC4wNy0yLDUuMTUtLjY4LjUsMCwxLjY0LjY3LDEuMTQsMS44My0xLjM0LDIuNjMtMy45MSwyLjYzLTYuMUE5LDksMCwwLDAsMzQuNjgsMTYuNTdaIiBzdHlsZT0iZmlsbDojZmZkMjAwIi8+PC9zdmc+";

var quarkyRobotInstance;

var property_accelerometer = "";
var property_gyroscope = "";

var x_acce_value_temp = 0,
    y_acce_value_temp = 0,
    z_acce_value_temp = 0;

var accelerometerSensor;
var gyroscopeSensor;
var linearAccelerometerSensor;
var absoluteOrientationSensor;

var x_acce_value = 0,
    y_acce_value = 0,
    z_acce_value = 0;

var x_acce_grav_value_temp = 0,
    y_acce_grav_value_temp = 0,
    y_acce_grav_value_temp = 0;

var x_acce_grav_value = 0,
    y_acce_grav_value = 0,
    z_acce_grav_value = 0;

var x_gyro_value = 0,
    y_gyro_value = 0,
    z_gyro_value = 0;

var x_gyro_value_temp = 0,
    y_gyro_value_temp = 0,
    z_gyro_value_temp = 0;

var alpha_value = 0,
    beta_value = 0,
    gamma_value = 0;

var webkit_compass = 0;

var x_absolute_value = 0,
    y_absolute_value = 0,
    z_absolute_value = 0;
// alpha	the direction the device is facing according to the compass
//          rotation around the z-axis [0, 360]

// beta	    the angle in degrees the device is tilted front-to-back
//          rotation around the x-axis [90, -90]

// gamma	the angle in degrees the device is tilted left-to-right
//          rotation around the y-axis [180, -180]

var screen_direction = "";
var phone_head_direction = "";

var tilt_status = "";

var level = 0,
    charging = 0,
    charge_time = 0,
    time_left = 0;

var value_accelerometer = 0,
    value_gyroscope = 0;

var distance_proximity = 0;

var latitude = 0;
var longitude = 0;

// for rotate robot
static_alpha = null;

var difference_start;

var done_rotating = true;

var rotateRobotArgs;
var rotateRobotUtil;

// run robot for certain meter
var velocity = 0;
var distance = 0,
    total_distance = 0;

var interval_motion,
    interval_cnt = 0;
seconds_cnt = 0;

var z_acce_value_distance;

var runRobotMeterArgs;
var runRobotMeterUtil;

var is_robot_reach_meter = true;

// until done
static_alpha_until_done = null;

var rotateRobotUntilDoneArgs;
var rotateRobotUntilDoneUtil;

var first_time_rotate = 0;

var until_done_rotating = true;

// run sinusoidal shape
static_alpha_sinusoidal = null;
first_static_alpha_sinusoidal = null;

var runSinusoidalShapeArgs;
var runSinusoidalShapeUtil;

var number_static_alpha_sinusoidal = 0;

var moment_sinusoidal;

// direction turning
var direction_turning = false;

function iOS() {
    return [
        "iPad Simulator",
        "iPhone Simulator",
        "iPod Simulator",
        "iPad",
        "iPhone",
        "iPod",
        "iOS",
        "MacIntel",
    ].includes(navigator.platform);
}

var userAgent = window.navigator.userAgent.toLowerCase(),
    safari = /safari/.test(userAgent),
    ios = /iphone|ipod|ipad/.test(userAgent);
console.log("---Mobile Sensor iOS()", iOS());

// Acceleration
window.addEventListener("devicemotion", handleMotion);

function handleMotion(eventData) {
    try {
        // Acceleration
        x_acce_value_temp = eventData.acceleration.x;
        y_acce_value_temp = eventData.acceleration.y;
        z_acce_value_temp = eventData.acceleration.z;
        interval_motion = eventData.interval; // 16

        x_acce_value = 0;
        y_acce_value = 0;
        z_acce_value = 0;

        if (x_acce_value_temp <= -0.1 || x_acce_value_temp >= 0.1) {
            x_acce_value = x_acce_value_temp;
        }
        if (y_acce_value_temp <= -0.1 || y_acce_value_temp >= 0.1) {
            y_acce_value = y_acce_value_temp;
        }
        if (z_acce_value_temp <= -0.1 || z_acce_value_temp >= 0.1) {
            z_acce_value = z_acce_value_temp;
        }

        // run robot for certain meter

        // run_robot_meter();

        // console.log(`LA: x : ${x_acce_value} | y : ${y_acce_value} | z : ${z_acce_value}`)

        ////////////////////////////////////////////////////////////////

        // Gravity Acceleration
        x_acce_grav_value_temp = eventData.accelerationIncludingGravity.x;
        y_acce_grav_value_temp = eventData.accelerationIncludingGravity.y;
        z_acce_grav_value_temp = eventData.accelerationIncludingGravity.z;

        x_acce_grav_value = 0;
        y_acce_grav_value = 0;
        z_acce_grav_value = 0;

        if (x_acce_grav_value_temp <= -0.1 || x_acce_grav_value_temp >= 0.1) {
            x_acce_grav_value = x_acce_grav_value_temp;
        }
        if (y_acce_grav_value_temp <= -0.1 || y_acce_grav_value_temp >= 0.1) {
            y_acce_grav_value = y_acce_grav_value_temp;
        }
        if (z_acce_grav_value_temp <= -0.1 || z_acce_grav_value_temp >= 0.1) {
            z_acce_grav_value = z_acce_grav_value_temp;
        }

        // console.log(`GA: x : ${x_acce_grav_value} | y : ${y_acce_grav_value} | z : ${z_acce_grav_value}`)

        ////////////////////////////////////////////////////////////////

        // Gyroscope
        x_gyro_value_temp = eventData.rotationRate.alpha;
        y_gyro_value_temp = eventData.rotationRate.beta;
        z_gyro_value_temp = eventData.rotationRate.gamma;

        x_gyro_value = 0;
        y_gyro_value = 0;
        z_gyro_value = 0;

        if (x_gyro_value_temp <= -0.1 || x_gyro_value_temp >= 0.1) {
            x_gyro_value = x_gyro_value_temp;
        }
        if (y_gyro_value_temp <= -0.1 || y_gyro_value_temp >= 0.1) {
            y_gyro_value = y_gyro_value_temp;
        }
        if (z_gyro_value_temp <= -0.1 || z_gyro_value_temp >= 0.1) {
            z_gyro_value = z_gyro_value_temp;
        }
    } catch (error) {
        x_acce_value_temp = eventData.acceleration.x;
        y_acce_value_temp = eventData.acceleration.y;
        z_acce_value_temp = eventData.acceleration.z;

        x_gyro_value_temp = eventData.rotationRate.alpha;
        y_gyro_value_temp = eventData.rotationRate.beta;
        z_gyro_value_temp = eventData.rotationRate.gamma;

        x_acce_grav_value_temp = eventData.accelerationIncludingGravity.x;
        y_acce_grav_value_temp = eventData.accelerationIncludingGravity.y;
        z_acce_grav_value_temp = eventData.accelerationIncludingGravity.z;
    }
    // console.log(`GYRO: x : ${x_gyro_value} | y : ${y_gyro_value} | z : ${z_gyro_value}`)

    // console.log("-----------------------------------------------------------------")

    // property_accelerometer = `X: ${x_value} m/s² | Y: ${y_value} m/s² | Z: ${z_value} m/s²`;
}

// try {
//     MobilePlatformController.getInstance((instance) => {
//         isIos = instance.isIOS;
//     });
// } catch (e) {
//     console.log(e);
// }

if (!iOS()) {
    window.addEventListener("deviceorientationabsolute", handleOrientation);
} else {
    window.addEventListener("deviceorientation", handleOrientation);
}

function handleOrientation(eventData) {
    try {
        // value_gyroscope = parseInt(eventData[prop]);
        // (Math.round(eventData[prop] * 100) / 100).toFixed(1)

        alpha_value = Math.floor(eventData.alpha);
        beta_value = Math.floor(eventData.beta);
        gamma_value = Math.floor(eventData.gamma);

        screen_heading(); // Screen pointing
        compass_heading(); // Heading of compass
        rotateRobotFunction(); // robot rotation

        var gamma_possitive = Math.abs(gamma_value);

        if (gamma_possitive >= 45) {
            tilt_status = "UP";

            // if (beta_value <= -15) {
            //     tilt_status +=
            //         " & " + String(gamma_value < 0 ? "LEFT" : "RIGHT");
            // }
            // if (beta_value >= 15) {
            //     tilt_status +=
            //         " & " + String(gamma_value < 0 ? "RIGHT" : "LEFT");
            // }
        }

        if (gamma_possitive < 45) {
            tilt_status = "DOWN";

            // if (beta_value <= -15) {
            //     tilt_status +=
            //         " & " + String(gamma_value < 0 ? "LEFT" : "RIGHT");
            // }
            // if (beta_value >= 15) {
            //     tilt_status +=
            //         " & " + String(gamma_value < 0 ? "RIGHT" : "LEFT");
            // }
        }

        if (beta_value <= -15) {
            tilt_status = String(gamma_value < 0 ? "LEFT" : "RIGHT");
            // tilt_status +=
            //     " & " + String(gamma_possitive >= 45 ? "UP" : "DOWN");
        }
        if (beta_value >= 15) {
            tilt_status = String(gamma_value < 0 ? "RIGHT" : "LEFT");
            // tilt_status +=
            //     " & " + String(gamma_possitive >= 45 ? "UP" : "DOWN");
        }

        if (iOS()) {
            webkit_compass = eventData["webkitCompassHeading"];
        }
    } catch (err) {
        alpha_value = eventData.alpha;
        beta_value = eventData.beta;
        gamma_value = eventData.gamma;
    }

    property_gyroscope = `Alpha: ${alpha_value}° | Beta: ${beta_value}° | Gamma: ${gamma_value}°`;
}

function round(number, precision) {
    let factor = 10 ** precision;
    return Math.round(number * factor) / factor;
}

// Screen heading
function screen_heading() {
    // Convert degrees to radians
    const alpha_rad = alpha_value * (Math.PI / 180);
    const beta_rad = beta_value * (Math.PI / 180);
    const gamma_rad = gamma_value * (Math.PI / 180);

    // Calculate equation components
    const cA = Math.cos(alpha_rad);
    const sA = Math.sin(alpha_rad);
    const cB = Math.cos(beta_rad);
    const sB = Math.sin(beta_rad);
    const cG = Math.cos(gamma_rad);
    const sG = Math.sin(gamma_rad);

    // Calculate A, B, C rotation components
    const rA = -cA * sG - sA * sB * cG;
    const rB = -sA * sG + cA * sB * cG;
    const rC = -cB * cG;

    // Calculate compass screen_direction
    screen_direction = Math.atan(rA / rB);

    // Convert from half unit circle to whole unit circle
    if (rB < 0) {
        screen_direction += Math.PI;
    } else if (rA < 0) {
        screen_direction += 2 * Math.PI;
    }

    // Convert radians to degrees
    screen_direction *= 180 / Math.PI;

    screen_direction = [
        "North",
        "North East",
        "East",
        "South East",
        "South",
        "South West",
        "West",
        "North West",
    ][Math.floor(((screen_direction + 22.5) % 360) / 45)];

    // return screen_direction;
}

// compass heading

function compass_heading() {
    var alpha_head = alpha_value;

    if (alpha_head <= 22 || alpha_head >= 338) {
        phone_head_direction = "South";
    } else if (alpha_head >= 23 && alpha_head <= 67) {
        phone_head_direction = "South East";
    } else if (alpha_head >= 68 && alpha_head <= 112) {
        phone_head_direction = "East";
    } else if (alpha_head >= 113 && alpha_head <= 157) {
        phone_head_direction = "North East";
    } else if (alpha_head >= 158 && alpha_head <= 202) {
        phone_head_direction = "North";
    } else if (alpha_head >= 203 && alpha_head <= 247) {
        phone_head_direction = "North West";
    } else if (alpha_head >= 248 && alpha_head <= 292) {
        phone_head_direction = "West";
    } else if (alpha_head >= 293 && alpha_head <= 337) {
        phone_head_direction = "South West";
    }
}

// Time
function secondsToHMS(secs) {
    if (secs == "Infinity") {
        return "Infinity";
    }
    var hours = Math.floor(secs / (60 * 60));

    var divisor_for_minutes = secs % (60 * 60);
    var minutes = Math.floor(divisor_for_minutes / 60);

    var divisor_for_seconds = divisor_for_minutes % 60;
    var seconds = Math.ceil(divisor_for_seconds);

    return `${hours}:${minutes}:${seconds}`;
}

// Proximity
// window.addEventListener("deviceproximity", handleProximity);

// function handleProximity(eventData) {
//     console.log(`-----------IN PROXIMITY-----------`);
//     if ("ondeviceproximity" in window) {
//         distance_proximity = (Math.round(eventData.value * 100) / 100).toFixed(
//             1
//         );
//         console.log(`-----------${distance_proximity}-----------`);
//     }
//     else{
//         console.log("-----------deviceproximity not supported-----------");
//     }
// }

// Location
var errorHandler = function (errorObj) {
    // console.log("Not able to access location");
    latitude = 0;
    longitude = 0;
    console.log(errorObj);
};

function currentPosition(position) {
    latitude = position.coords.latitude;
    longitude = position.coords.longitude;
}

// function geolocation() {
//     //GEO Location. Requires https
//     // var output = document.getElementById("output");

//     navigator.geolocation.getCurrentPosition(success_location, error_location);

//     if (!navigator.geolocation) {
//         // output.innerHTML = "Geolocation is not supported by your browser";
//         return "Geolocation is not supported by your browser";
//     }

//     function success_location(position) {
//         var latitude = position.coords.latitude;
//         var longitude = position.coords.longitude;

//         return "Latitude is " + latitude + "°Longitude is " + longitude + "°";
//     }

//     function error_location() {
//         return "Unable to retrieve your location";
//     }
//     return "Locating… ";
// }

// update alpha
function alpha_update() {
    // phone face back - camera in left
    var local_alpha = alpha_value;
    if (iOS()) {
        local_alpha = 360 - webkit_compass;
    }
    if (gamma_value > 0) {
        if (alpha_value >= 180) {
            local_alpha = alpha_value - 180;
        } else {
            local_alpha = alpha_value + 180;
        }
    } else {
        local_alpha = alpha_value;
    }

    return local_alpha;
}

// rotate robot
function check_difference(static_alpha, moment) {
    // phone face back - camera in left

    var local_alpha = alpha_value;
    var difference;

    if (gamma_value > 0) {
        if (local_alpha >= 180) {
            difference = static_alpha - (local_alpha - 180);
        } else {
            difference = static_alpha - (local_alpha + 180);
        }
    } else {
        difference = static_alpha - local_alpha;
    }

    if (moment == "R") {
        if (difference >= 0) {
            return difference;
        } else {
            return difference + 360;
        }
    } else {
        if (difference <= 0) {
            return Math.abs(difference);
        } else {
            return Math.abs(difference - 360);
        }
    }
}

function rotateRobotFunction() {
    if (static_alpha == null) {
        // console.log("IN")
        static_alpha = alpha_value;
    }
    if (!done_rotating) {
        difference_start = check_difference(
            static_alpha,
            rotateRobotArgs.MOMENT
        );

        if (
            difference_start >= parseInt(rotateRobotArgs.ANGLE) - 3 &&
            difference_start < parseInt(rotateRobotArgs.ANGLE) + 10
        ) {
            done_rotating = true;
            quarkyRobotInstance.stopRobot(
                (args = { SPEED: rotateRobotArgs.SPEED }),
                (util = rotateRobotUtil)
            );
        } else {
            if (rotateRobotArgs.MOMENT == "L") {
                // As the functionality is reversed
                quarkyRobotInstance.runRobot(
                    (args = { DIRECTION: "3", SPEED: rotateRobotArgs.SPEED }),
                    (util = rotateRobotUtil)
                );
            } else {
                quarkyRobotInstance.runRobot(
                    (args = { DIRECTION: "4", SPEED: rotateRobotArgs.SPEED }),
                    (util = rotateRobotUtil)
                );
            }
            // runMotor(args, util, this);
        }
    }
}

function rotateRobotUntilDoneFunction() {
    // if (static_alpha_until_done == null) {
    //     // console.log("IN")

    //     console.log("---FUNC: first IF---");
    //     static_alpha_until_done = alpha_value;
    // }
    // console.log(`---FUNC: in start---`);

    difference_start = check_difference(
        static_alpha_until_done,
        rotateRobotUntilDoneArgs.MOMENT
    );
    // console.log(
    //     `---FUNC: static_alpha: ${static_alpha_until_done} | alpha: ${alpha_value} | diff: ${difference_start}---`
    // );

    // console.log(
    //     `---FUNC: rotateRobotUntilDoneArgs.ANGLE: ${rotateRobotUntilDoneArgs.ANGLE} ---`
    // );

    // console.log(
    //     `---FUNC: diff >= angle-3: ${difference_start >= parseInt(rotateRobotUntilDoneArgs.ANGLE) - 3} ---`
    // );
    // console.log(
    //     `---FUNC: diff < angle+10: ${difference_start < parseInt(rotateRobotUntilDoneArgs.ANGLE) + 10} ---`
    // );

    if (
        difference_start >= parseInt(rotateRobotUntilDoneArgs.ANGLE) - 3 &&
        difference_start < parseInt(rotateRobotUntilDoneArgs.ANGLE) + 10
    ) {
        until_done_rotating = true;

        // console.log("---FUNC: done rotating---");

        quarkyRobotInstance.stopRobot(
            (args = { SPEED: rotateRobotUntilDoneArgs.SPEED }),
            (util = rotateRobotUntilDoneUtil)
        );
    }
    // console.log("---FUNC: last line---");
}

// function runSinusoidalShapeFunction() {
//     difference_start = check_difference(
//         static_alpha_sinusoidal,
//         moment_sinusoidal
//     );

//     if (
//         difference_start == number_static_alpha_sinusoidal * 60 &&
//         number_static_alpha_sinusoidal != 0
//     ) {
//         if (moment_sinusoidal == "L") {
//             moment_sinusoidal = "R";
//             quarkyRobotInstance.runMotor(
//                 (args = {
//                     MOTOR: "L",
//                     SPEED: runSinusoidalShapeArgs.SPEED,
//                     DIRECTION: "1",
//                 }),
//                 (util = runSinusoidalShapeUtil)
//             );

//             quarkyRobotInstance.runMotor(
//                 (args = {
//                     MOTOR: "R",
//                     SPEED: runSinusoidalShapeArgs.SPEED - 20,
//                     DIRECTION: "1",
//                 }),
//                 (util = runSinusoidalShapeUtil)
//             );
//         } else {
//             moment_sinusoidal = "L";
//             quarkyRobotInstance.runMotor(
//                 (args = {
//                     MOTOR: "L",
//                     SPEED: runSinusoidalShapeArgs.SPEED - 20,
//                     DIRECTION: "1",
//                 }),
//                 (util = runSinusoidalShapeUtil)
//             );

//             quarkyRobotInstance.runMotor(
//                 (args = {
//                     MOTOR: "R",
//                     SPEED: runSinusoidalShapeArgs.SPEED,
//                     DIRECTION: "1",
//                 }),
//                 (util = runSinusoidalShapeUtil)
//             );
//         }
//         static_alpha_sinusoidal = alpha_value;

//         sleep(100);
//     }

//     if (
//         first_static_alpha_sinusoidal == alpha_value &&
//         static_alpha_sinusoidal != alpha_value
//     ) {
//         number_static_alpha_sinusoidal++;
//     }

//     console.log(
//         `FUNC: static_al: ${static_alpha_sinusoidal} | alpha: ${alpha_value} | diff: ${difference_start} | mom: ${moment_sinusoidal} | num: ${number_static_alpha_sinusoidal}`
//     );
// }

function cameraLocationFunction() {
    // accelerometerSensor.onreading = () => {
    //     x_acce_grav_value = round(accelerometerSensor.x, 3);
    // };
    // accelerometerSensor.onerror = (event) => {
    //     x_acce_grav_value = 0;
    // };

    if (x_acce_grav_value >= 0) {
        return "left";
    } else {
        return "right";
    }

    // if (x_acce_grav_value != 0) {
    //     recursive_cnt = 0;
    //     if (x_acce_grav_value >= 0) {
    //         return "left";
    //     } else {
    //         return "right";
    //     }
    // } else {
    //     if (recursive_cnt <= 3) {
    //         recursive_cnt++;
    //         cameraLocationFunction();
    //     } else {
    //         recursive_cnt = 0;
    //     }
    // }
}

function sleep(milliseconds) {
    var start = new Date().getTime();
    for (var i = 0; i < 1e7; i++) {
        if (new Date().getTime() - start > milliseconds) {
            break;
        }
    }
}

// run robot certain meter
// function run_robot_meter() {
//     if (is_robot_reach_meter == false) {
//         interval_cnt++;

//         if (
//             interval_cnt %
//                 parseInt(
//                     (1000 / interval_motion) *
//                         ((runRobotMeterArgs.METER * 100) / 13.07)
//                 ) ==
//             0
//         ) {
//             // motor speed 13.07 cm/s
//             // 13.07 cm -> 1 sec
//             // 100   cm -> ?
//             //
//             // interval: 16 ms
//             // 16   ms -> 1 sample
//             // 1000 ms -> ?

//             quarkyRobotInstance.stopRobot(
//                 (args = { SPEED: runRobotMeterArgs.SPEED }),
//                 (util = runRobotMeterUtil)
//             );

//             interval_cnt = 0;

//             is_robot_reach_meter = true;
//         }
//     }
// }

class mobileSensor {
    // constructor(runtime) {
    //     this.runtime = runtime;
    //     this._onTargetCreated = this._onTargetCreated.bind(this);
    //     this.runtime.on("targetWasCreated", this._onTargetCreated);
    //     this.runtime.on("serialRead", this._serialRead.bind(this));
    //     this._isSerialRead = () => {};
    // }

    // static get STATE_KEY() {
    //     return "mobileSensor";
    // }

    // _onTargetCreated(newTarget, sourceTarget) {
    //     if (sourceTarget) {
    //         const mobileSensorState = sourceTarget.getCustomState(
    //             mobileSensor.STATE_KEY
    //         );
    //         if (mobileSensorState) {
    //             newTarget.setCustomState(
    //                 mobileSensor.STATE_KEY,
    //                 Clone.simple(mobileSensorState)
    //             );
    //         }
    //     }
    // }

    // _serialRead(data) {
    //     this._isSerialRead(data);
    // }
    constructor(runtime) {
        quarkyRobotInstance = new quarkyRobot(runtime);

        // linearAccelerometerSensor = new LinearAccelerationSensor({
        //     frequency: 30,
        // });
        // linearAccelerometerSensor.start();
        // linearAccelerometerSensor.onreading = () => {
        //     x_acce_value = round(linearAccelerometerSensor.x, 3);
        // };

        // accelerometerSensor = new Accelerometer({ frequency: 30 });
        // accelerometerSensor.start();
        // accelerometerSensor.onreading = () => {
        //     x_acce_grav_value = round(accelerometerSensor.x, 3);
        // };

        // gyroscopeSensor = new Gyroscope({ frequency: 30 });
        // gyroscopeSensor.start();
        // gyroscopeSensor.onreading = () => {
        //     x_gyro_value = round(gyroscopeSensor.x, 3);
        // };

        // absoluteOrientationSensor = new AbsoluteOrientationSensor({
        //     frequency: 30,
        // });
        // absoluteOrientationSensor.start();
        // absoluteOrientationSensor.onreading = () => {
        //     x_absolute_value = round(
        //         absoluteOrientationSensor.quaternion[0],
        //         3
        //     );
        // };
    }

    getInfo() {
        return {
            id: "mobileSensor",
            name: "Mobile Sensors",
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: "#5CB1D6",
            colourSecondary: "#47A8D1",
            colourTertiary: "#2E8EB8",
            blocks: [
                {
                    opcode: "getPhoneTilt",
                    text: formatMessage({
                        id: "mobileSensor.getPhoneTilt",
                        default: "is phone tilted [TILT]?",
                        description: "Get Phone Tilt status",
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        TILT: {
                            type: ArgumentType.NUMBER,
                            menu: "phoneTilt",
                            defaultValue: "1",
                        },
                    },
                },
                {
                    opcode: "phoneCameraLocation",
                    text: formatMessage({
                        id: "mobileSensor.phoneCameraLocation",
                        default: "is phone camera on [CAMERALOCATION]?",
                        description: "phone camera location",
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        CAMERALOCATION: {
                            type: ArgumentType.NUMBER,
                            menu: "cameraLocation",
                            defaultValue: "1",
                        },
                    },
                },

                "---",
                // {
                //     opcode: "rotateRobot",
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: "mobileSensor.rotateRobot",
                //         default:
                //             "rotate [MOMENT] [ANGLE] degrees at [SPEED] % speed",
                //         description: "Rotate robot",
                //     }),
                //     arguments: {
                //         MOMENT: {
                //             type: ArgumentType.NUMBER,
                //             menu: "moment",
                //             defaultValue: "L",
                //         },
                //         ANGLE: {
                //             type: ArgumentType.MATHSLIDER180,
                //             defaultValue: "90",
                //         },
                //         SPEED: {
                //             type: ArgumentType.MATHSLIDER100,
                //             defaultValue: "65",
                //         },
                //     },
                // },
                {
                    opcode: "rotateRobotUntilDone",
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: "mobileSensor.rotateRobotUntilDone",
                        default:
                            "rotate [MOMENT] [ANGLE] degrees at [SPEED] % speed",
                        description: "Rotate robot",
                    }),
                    arguments: {
                        MOMENT: {
                            type: ArgumentType.NUMBER,
                            menu: "moment",
                            defaultValue: "L",
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: "90",
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: "65",
                        },
                    },
                },

                // {
                //     opcode: "rotateRobotUntilDoneSlow",
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: "mobileSensor.rotateRobotUntilDoneSlow",
                //         default:
                //             "rotate [MOMENT] [ANGLE] degrees at [SPEED] % speed until done slow",
                //         description: "Rotate robot",
                //     }),
                //     arguments: {
                //         MOMENT: {
                //             type: ArgumentType.NUMBER,
                //             menu: "moment",
                //             defaultValue: "L",
                //         },
                //         ANGLE: {
                //             type: ArgumentType.MATHSLIDER180,
                //             defaultValue: "90",
                //         },
                //         SPEED: {
                //             type: ArgumentType.MATHSLIDER100,
                //             defaultValue: "65",
                //         },
                //     },
                // },

                // {
                //     opcode: "runSinusoidalShape",
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: "mobileSensor.runSinusoidalShape",
                //         default: "sinusoidal rotation at [SPEED] % speed",
                //         description: "Run robot in sinusoidal shape",
                //     }),
                //     arguments: {
                //         DIRECTION: {
                //             type: ArgumentType.NUMBER,
                //             menu: "robotDirection",
                //             defaultValue: "1",
                //         },
                //         SPEED: {
                //             type: ArgumentType.MATHSLIDER100,
                //             defaultValue: "65",
                //         },
                //     },
                // },

                {
                    opcode: "rotateDirection",
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: "mobileSensor.rotateDirection",
                        default: "rotate in [NEWS] at [SPEED] % speed",
                        description: "Run robot in sinusoidal shape",
                    }),
                    arguments: {
                        NEWS: {
                            type: ArgumentType.NUMBER,
                            menu: "news",
                            defaultValue: "1",
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: "70",
                        },
                    },
                },
                // {
                //     opcode: "runRobotMeter",
                //     blockType: BlockType.COMMAND,
                //     text: formatMessage({
                //         id: "mobileSensor.runRobotMeter",
                //         default: "run robot [DIRECTION] for [METER] meters",
                //         description: "Rotate robot",
                //     }),
                //     arguments: {
                //         DIRECTION: {
                //             type: ArgumentType.NUMBER,
                //             menu: "robotDirection",
                //             defaultValue: "1",
                //         },

                //         METER: {
                //             type: ArgumentType.NUMBER,
                //             menu: "meter",
                //             defaultValue: "1",
                //         },

                //         SPEED: {
                //             type: ArgumentType.MATHSLIDER100,
                //             defaultValue: "100",
                //         },
                //     },
                // },

                "---",

                {
                    opcode: "getAccelerometerReading",
                    text: formatMessage({
                        id: "mobileSensor.getAccelerometerReading",
                        default: "linear acceleration in [DIR] direction",
                        description: "Get Accelerometer Reading",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        DIR: {
                            type: ArgumentType.NUMBER,
                            menu: "directionValue",
                            defaultValue: "1",
                        },
                    },
                },

                {
                    opcode: "getAccelerometerGravityReading",
                    text: formatMessage({
                        id: "mobileSensor.getAccelerometerGravityReading",
                        default:
                            "acceleration with gravity in [DIR_GRAVITY] direction",
                        description: "Get Accelerometer Gravity Reading",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        DIR_GRAVITY: {
                            type: ArgumentType.NUMBER,
                            menu: "directionValue",
                            defaultValue: "1",
                        },
                    },
                },

                {
                    opcode: "getGyroscopeReading",
                    text: formatMessage({
                        id: "mobileSensor.getGyroscopeReading",
                        default: "gyroscope [GYROSCOPE] value",
                        description: "Get Gyroscope Reading",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        GYROSCOPE: {
                            type: ArgumentType.NUMBER,
                            menu: "directionValue",
                            defaultValue: "1",
                        },
                    },
                },

                {
                    opcode: "getOrientationReading",
                    text: formatMessage({
                        id: "mobileSensor.getOrientationReading",
                        default: "orientation [MOTION] value",
                        description: "Get Gyroscope Reading",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        MOTION: {
                            type: ArgumentType.NUMBER,
                            menu: "motionValue",
                            defaultValue: "1",
                        },
                    },
                },

                // {
                //     opcode: "getAbsoluteOrientationReading",
                //     text: formatMessage({
                //         id: "mobileSensor.getAbsoluteOrientationReading",
                //         default: "absolute orientation [DIR] value",
                //         description: "Get Gyroscope Reading",
                //     }),
                //     blockType: BlockType.REPORTER,
                //     arguments: {
                //         DIR: {
                //             type: ArgumentType.NUMBER,
                //             menu: "directionValue",
                //             defaultValue: "1",
                //         },
                //     },
                // },

                // {
                //     message: "screen direction",
                // },

                // {
                //     opcode: "getScreenHeading",
                //     text: formatMessage({
                //         id: "mobileSensor.getScreenHeading",
                //         default: "is screen's direction [NEWS]",
                //         description: "Get Screen Heading",
                //     }),
                //     blockType: BlockType.BOOLEAN,
                //     arguments: {
                //         NEWS: {
                //             type: ArgumentType.NUMBER,
                //             menu: "newsValue",
                //             defaultValue: "1",
                //         },
                //     },
                // },
                // {
                //     message: "Phone Tilt",
                // },

                // {
                //     message: "Battery Status",
                // },

                {
                    opcode: "getBatteryStatus",
                    text: formatMessage({
                        id: "mobileSensor.getBatteryStatus",
                        default: "battery status [BATTERY]",
                        description: "Get Battery status",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        BATTERY: {
                            type: ArgumentType.NUMBER,
                            menu: "batteryStatus",
                            defaultValue: "1",
                        },
                    },
                },

                {
                    opcode: "getPhoneHeading",
                    text: formatMessage({
                        id: "mobileSensor.getPhoneHeading",
                        default: "is phone's head in [NEWS]",
                        description: "Get Screen Heading",
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        NEWS: {
                            type: ArgumentType.NUMBER,
                            menu: "newsValue",
                            defaultValue: "1",
                        },
                    },
                },

                // {
                //     opcode: "getPhoneHeading",
                //     text: formatMessage({
                //         id: "mobileSensor.getPhoneHeading",
                //         default: "is phone's head in [NEWS]",
                //         description: "Get Screen Heading",
                //     }),
                //     blockType: BlockType.BOOLEAN,
                //     arguments: {
                //         NEWS: {
                //             type: ArgumentType.NUMBER,
                //             menu: "newsValue",
                //             defaultValue: "1",
                //         },
                //     },
                // },

                // {
                //     opcode: "getProximityReading",
                //     text: formatMessage({
                //         id: "mobileSensor.getProximityReading",
                //         default: "object distance",
                //         description: "Get Proximity Reading",
                //     }),
                //     blockType: BlockType.REPORTER,
                // },

                {
                    opcode: "getLocation",
                    text: formatMessage({
                        id: "mobileSensor.getLocation",
                        default: "current location [LOCATION]",
                        description: "Get Current Location",
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        LOCATION: {
                            type: ArgumentType.NUMBER,
                            menu: "currentLocation",
                            defaultValue: "1",
                        },
                    },
                },
            ],
            menus: {
                directionValue: {
                    items: [
                        { text: "x", value: "1" },
                        { text: "y", value: "2" },
                        { text: "z", value: "3" },
                    ],
                },
                motionValue: {
                    items: [
                        { text: "yaw", value: "1" },
                        { text: "pitch", value: "2" },
                        { text: "roll", value: "3" },
                    ],
                },
                newsValue: {
                    items: [
                        { text: "north", value: "1" },
                        { text: "north east", value: "2" },
                        { text: "east", value: "3" },
                        { text: "south east", value: "4" },
                        { text: "south", value: "5" },
                        { text: "south west", value: "6" },
                        { text: "west", value: "7" },
                        { text: "north west", value: "8" },
                    ],
                },
                news: {
                    items: [
                        { text: "north", value: "1" },
                        { text: "east", value: "2" },
                        { text: "west", value: "3" },
                        { text: "south", value: "4" },
                    ],
                },
                phoneTilt: {
                    items: [
                        { text: "up", value: "1" },
                        { text: "down", value: "2" },
                        { text: "left", value: "3" },
                        { text: "right", value: "4" },
                    ],
                },
                batteryStatus: {
                    items: [
                        { text: "level", value: "1" },
                        { text: "charging", value: "2" },
                        { text: "time remaining", value: "3" },
                    ],
                },

                moment: {
                    items: [
                        { text: "left", value: "L" },
                        { text: "right", value: "R" },
                    ],
                },

                meter: {
                    acceptReporters: true,
                    items: ["1", "2", "3", "4", "5", "6", "7"],
                },
                robotDirection: {
                    items: [
                        { text: "forward", value: "1" },
                        { text: "backward", value: "2" },
                    ],
                },

                cameraLocation: {
                    items: [
                        { text: "left", value: "1" },
                        { text: "right", value: "2" },
                    ],
                },
                currentLocation: {
                    items: [
                        { text: "latitude", value: "1" },
                        { text: "longitude", value: "2" },
                    ],
                },
            },
        };
    }

    // getAccelerometerReading(args, util) {
    //     // Value in : m/s^2

    //     linearAccelerometerSensor.onreading = () => {
    //         x_acce_value = round(linearAccelerometerSensor.x, 3);
    //         y_acce_value = round(linearAccelerometerSensor.y, 3);
    //         z_acce_value = round(linearAccelerometerSensor.z, 3);
    //     };
    //     linearAccelerometerSensor.onerror = (event) => {
    //         x_acce_value = 0;
    //         y_acce_value = 0;
    //         z_acce_value = 0;
    //     };

    //     if (x_acce_value != 0 || y_acce_value != 0 || z_acce_value != 0) {
    //         recursive_cnt = 0;
    //         if (args.DIR == "1") {
    //             return x_acce_value;
    //         }
    //         if (args.DIR == "2") {
    //             return y_acce_value;
    //         }
    //         if (args.DIR == "3") {
    //             return z_acce_value;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }
    getAccelerometerReading(args, util) {
        // Value in : m/s^2
        if (args.DIR == "1") {
            return x_acce_value;
        }
        if (args.DIR == "2") {
            return y_acce_value;
        }
        if (args.DIR == "3") {
            return z_acce_value;
        }
    }

    // getAccelerometerGravityReading(args, util) {
    //     // Value in : m/s^2

    //     accelerometerSensor.onreading = () => {
    //         x_acce_grav_value = round(accelerometerSensor.x, 3);
    //         y_acce_grav_value = round(accelerometerSensor.y, 3);
    //         z_acce_grav_value = round(accelerometerSensor.z, 3);
    //     };
    //     accelerometerSensor.onerror = (event) => {
    //         x_acce_grav_value = 0;
    //         y_acce_grav_value = 0;
    //         z_acce_grav_value = 0;
    //     };
    //     if (
    //         x_acce_grav_value != 0 ||
    //         y_acce_grav_value != 0 ||
    //         z_acce_grav_value != 0
    //     ) {
    //         recursive_cnt = 0;
    //         if (args.DIR_GRAVITY == "1") {
    //             return x_acce_grav_value;
    //         }
    //         if (args.DIR_GRAVITY == "2") {
    //             return y_acce_grav_value;
    //         }
    //         if (args.DIR_GRAVITY == "3") {
    //             return z_acce_grav_value;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }

    getAccelerometerGravityReading(args, util) {
        // Value in : m/s^2

        if (args.DIR_GRAVITY == "1") {
            return x_acce_grav_value;
        }
        if (args.DIR_GRAVITY == "2") {
            return y_acce_grav_value;
        }
        if (args.DIR_GRAVITY == "3") {
            return z_acce_grav_value;
        }
    }

    // getGyroscopeReading(args, util) {
    //     // Value in : degree/sec

    //     gyroscopeSensor.onreading = () => {
    //         x_gyro_value = round(gyroscopeSensor.x, 3);
    //         y_gyro_value = round(gyroscopeSensor.y, 3);
    //         z_gyro_value = round(gyroscopeSensor.z, 3);
    //     };
    //     gyroscopeSensor.onerror = (event) => {
    //         x_gyro_value = 0;
    //         y_gyro_value = 0;
    //         z_gyro_value = 0;
    //     };

    //     if (x_gyro_value != 0 || y_gyro_value != 0 || z_gyro_value != 0) {
    //         recursive_cnt = 0;
    //         if (args.GYROSCOPE == "1") {
    //             return x_gyro_value;
    //         }
    //         if (args.GYROSCOPE == "2") {
    //             return y_gyro_value;
    //         }
    //         if (args.GYROSCOPE == "3") {
    //             return z_gyro_value;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }
    getGyroscopeReading(args, util) {
        // Value in : degree/sec
        if (args.GYROSCOPE == "1") {
            return x_gyro_value;
        }
        if (args.GYROSCOPE == "2") {
            return y_gyro_value;
        }
        if (args.GYROSCOPE == "3") {
            return z_gyro_value;
        }
    }

    // getOrientationReading(args, util) {
    //     // value in degree
    //     if (alpha_value != 0 || beta_value != 0 || gamma_value != 0) {
    //         recursive_cnt = 0;
    //         if (args.MOTION == "1") {
    //             return alpha_value;
    //         }
    //         if (args.MOTION == "2") {
    //             return beta_value;
    //         }
    //         if (args.MOTION == "3") {
    //             return gamma_value;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }

    getOrientationReading(args, util) {
        // value in degree

        if (args.MOTION == "1") {
            return alpha_value;
        }
        if (args.MOTION == "2") {
            return beta_value;
        }
        if (args.MOTION == "3") {
            return gamma_value;
        }
    }

    // getAbsoluteOrientationReading(args, util) {
    //     absoluteOrientationSensor.onreading = () => {
    //         x_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[0],
    //             3
    //         );
    //         y_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[1],
    //             3
    //         );
    //         z_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[2],
    //             3
    //         );
    //     };
    //     absoluteOrientationSensor.onerror = (event) => {
    //         x_absolute_value = 0;
    //         y_absolute_value = 0;
    //         z_absolute_value = 0;
    //     };

    //     if (
    //         x_absolute_value != 0 ||
    //         y_absolute_value != 0 ||
    //         z_absolute_value != 0
    //     ) {
    //         recursive_cnt = 0;
    //         if (args.DIR == "1") {
    //             return x_absolute_value;
    //         }
    //         if (args.DIR == "2") {
    //             return y_absolute_value;
    //         }
    //         if (args.DIR == "3") {
    //             return z_absolute_value;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }

    getScreenHeading(args, util) {
        if (
            (args.NEWS == "1" && screen_direction == "North") ||
            (args.NEWS == "2" && screen_direction == "North East") ||
            (args.NEWS == "3" && screen_direction == "East") ||
            (args.NEWS == "4" && screen_direction == "South East") ||
            (args.NEWS == "5" && screen_direction == "South") ||
            (args.NEWS == "6" && screen_direction == "South West") ||
            (args.NEWS == "7" && screen_direction == "West") ||
            (args.NEWS == "8" && screen_direction == "North West")
        ) {
            return true;
        } else {
            return false;
        }
    }

    getPhoneTilt(args, util) {
        if (
            (args.TILT == "1" && tilt_status.includes("UP")) ||
            (args.TILT == "2" && tilt_status.includes("DOWN")) ||
            (args.TILT == "3" && tilt_status.includes("LEFT")) ||
            (args.TILT == "4" && tilt_status.includes("RIGHT"))
        ) {
            return true;
        } else {
            return false;
        }
    }

    // phoneCameraLocation(args, util) {
    //     // accelerometerSensor = new Accelerometer({ frequency: 30 });
    //     // accelerometerSensor.start();

    //     accelerometerSensor.onreading = () => {
    //         x_acce_grav_value = round(accelerometerSensor.x, 3);
    //     };
    //     accelerometerSensor.onerror = (event) => {
    //         x_acce_grav_value = 0;
    //     };

    //     if (x_acce_grav_value != 0) {
    //         recursive_cnt = 0;
    //         if (
    //             (x_acce_grav_value >= 0 && args.CAMERALOCATION == "1") ||
    //             (x_acce_grav_value <= 0 && args.CAMERALOCATION == "2")
    //         ) {
    //             return true;
    //         } else {
    //             return false;
    //         }
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }

    phoneCameraLocation(args, util) {
        // accelerometerSensor = new Accelerometer({ frequency: 30 });
        // accelerometerSensor.start();
        first_time_rotate = 0;
        direction_turning = false;

        var phone_camera_left_right = cameraLocationFunction();
        if (
            (phone_camera_left_right == "left" && args.CAMERALOCATION == "1") ||
            (phone_camera_left_right == "right" && args.CAMERALOCATION == "2")
        ) {
            return true;
        } else {
            return false;
        }
    }

    getBatteryStatus(args, util) {
        first_time_rotate == 0;

        try {
            navigator.getBattery().then((battery) => {
                level = parseInt(battery.level * 100); // 0-1 range
                charging = battery.charging;
                // charge_time = secondsToHMS(battery.chargingTime);
                time_left = secondsToHMS(battery.dischargingTime);
            });

            // var responseText = `Level: ${level}% | Charging: ${charging} | Time Remaining: ${time_left}`;

            // if (args.BATTERY == "1") {
            //     if (level != 0) {
            //         // recursive_cnt = 0;
            //         return level;
            //     } else {
            //         util.yield();
            //     }
            // }
            if (args.BATTERY == "1") {
                return level;
            }

            if (args.BATTERY == "2") {
                return charging;
            }

            if (args.BATTERY == "3") {
                return time_left;
            }
        } catch (error) {
            console.log(error);
        }
    }

    getPhoneHeading(args, util) {
        if (
            (args.NEWS == "1" && phone_head_direction == "North") ||
            (args.NEWS == "2" && phone_head_direction == "North East") ||
            (args.NEWS == "3" && phone_head_direction == "East") ||
            (args.NEWS == "4" && phone_head_direction == "South East") ||
            (args.NEWS == "5" && phone_head_direction == "South") ||
            (args.NEWS == "6" && phone_head_direction == "South West") ||
            (args.NEWS == "7" && phone_head_direction == "West") ||
            (args.NEWS == "8" && phone_head_direction == "North West")
        ) {
            return true;
        } else {
            return false;
        }
    }

    // getProximityReading(args, util) {
    //     return distance_proximity + " cm";
    // }

    getLocation(args, util) {
        // permission_location = navigator.permissions.query({
        //     name: "geolocation",
        // });

        if (navigator && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                currentPosition,
                errorHandler
            );

            if (args.LOCATION == "1") {
                return latitude;
            }
            if (args.LOCATION == "2") {
                return longitude;
            }
        } else {
            return 0;
        }
        // navigator.geolocation.getCurrentPosition(currentPosition, errorHandler);

        return 0;
    }

    rotateRobot(args, util) {
        static_alpha = null;
        done_rotating = false;

        rotateRobotArgs = args;

        rotateRobotUtil = util;
        // runtime_getCode = this.runtime.getCode;

        // class_this = this
        // console.log(`----${difference_start}----`)
        // static_alpha = null;
    }

    rotateRobotUntilDone(args, util) {
        var phone_camera_left_right = cameraLocationFunction();
        if (phone_camera_left_right == "right") {
            return "kindly place phone properly"
        }
        if (first_time_rotate == 0) {
            static_alpha_until_done = alpha_value;
            until_done_rotating = false;
            first_time_rotate++;
            // console.log(
            //     `MAIN: in first if static_alpha: ${static_alpha_until_done} | done_rotating: ${done_rotating}`
            // );

            rotateRobotUntilDoneArgs = args;

            rotateRobotUntilDoneUtil = util;

            if (rotateRobotUntilDoneArgs.MOMENT == "L") {
                // As the functionality is reversed
                quarkyRobotInstance.runRobot(
                    (args = {
                        DIRECTION: "3",
                        SPEED: rotateRobotUntilDoneArgs.SPEED,
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
            } else {
                quarkyRobotInstance.runRobot(
                    (args = {
                        DIRECTION: "4",
                        SPEED: rotateRobotUntilDoneArgs.SPEED,
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
            }
        }

        // console.log("---MAIN: Before last return---");

        if (!until_done_rotating) {
            // console.log(
            //     `---MAIN: if(!done_rotating) done_rotating: ${until_done_rotating}---`
            // );

            rotateRobotUntilDoneFunction();
            // return false;
            sleep(10);
            util.yield(); // Important
        } else {
            first_time_rotate = 0;
            until_done_rotating = true;

            quarkyRobotInstance.stopRobot(
                (args = { SPEED: rotateRobotUntilDoneArgs.SPEED }),
                (util = rotateRobotUntilDoneUtil)
            );

            // console.log(
            //     `---MAIN: else done_rotating: ${until_done_rotating}---`
            // );

            return true;
        }
    }

    rotateRobotUntilDoneSlow(args, util) {
        if (first_time_rotate == 0) {
            static_alpha_until_done = alpha_value;
            until_done_rotating = false;
            first_time_rotate++;

            rotateRobotUntilDoneArgs = args;

            rotateRobotUntilDoneUtil = util;

            quarkyRobotInstance.stopRobot(
                (args = { SPEED: rotateRobotUntilDoneArgs.SPEED }),
                (util = rotateRobotUntilDoneUtil)
            );

            if (rotateRobotUntilDoneArgs.MOMENT == "L") {
                // As the functionality is reversed
                quarkyRobotInstance.runMotor(
                    (args = {
                        MOTOR: "R",
                        SPEED: rotateRobotUntilDoneArgs.SPEED,
                        DIRECTION: "1",
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
                quarkyRobotInstance.runMotor(
                    (args = {
                        MOTOR: "L",
                        SPEED: rotateRobotUntilDoneArgs.SPEED - 20,
                        DIRECTION: "1",
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
            } else {
                quarkyRobotInstance.runMotor(
                    (args = {
                        MOTOR: "L",
                        SPEED: rotateRobotUntilDoneArgs.SPEED,
                        DIRECTION: "1",
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
                quarkyRobotInstance.runMotor(
                    (args = {
                        MOTOR: "R",
                        SPEED: rotateRobotUntilDoneArgs.SPEED - 20,
                        DIRECTION: "1",
                    }),
                    (util = rotateRobotUntilDoneUtil)
                );
            }
        }
        if (!until_done_rotating) {
            rotateRobotUntilDoneFunction();

            sleep(10);
            util.yield(); // Important
        } else {
            first_time_rotate = 0;
            until_done_rotating = true;

            quarkyRobotInstance.stopRobot(
                (args = { SPEED: rotateRobotUntilDoneArgs.SPEED }),
                (util = rotateRobotUntilDoneUtil)
            );

            return true;
        }
    }

    // rotateDirection(args, util) {
    //     absoluteOrientationSensor.onreading = () => {
    //         x_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[0],
    //             3
    //         );
    //         y_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[1],
    //             3
    //         );
    //         z_absolute_value = round(
    //             absoluteOrientationSensor.quaternion[2],
    //             3
    //         );
    //     };
    //     absoluteOrientationSensor.onerror = (event) => {
    //         x_absolute_value = 0;
    //         y_absolute_value = 0;
    //         z_absolute_value = 0;
    //     };

    //     if (
    //         x_absolute_value != 0 ||
    //         y_absolute_value != 0 ||
    //         z_absolute_value != 0
    //     ) {
    //         recursive_cnt = 0;

    //         // North x: 0.45
    //         if (
    //             args.NEWS == "1" &&
    //             !(x_absolute_value <= 0.5 && x_absolute_value >= 0.4)
    //         ) {
    //             if (
    //                 0.45 - x_absolute_value > 0 &&
    //                 0.45 - x_absolute_value <= 1
    //             ) {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "3",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             } else {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "4",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             }
    //         }
    //         // East x: 0
    //         else if (
    //             args.NEWS == "2" &&
    //             !(x_absolute_value <= 0.1 && x_absolute_value >= -0.1)
    //         ) {
    //             if (x_absolute_value > 0) {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "4",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             } else {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "3",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             }
    //         }
    //         // West x: 0.68
    //         else if (
    //             args.NEWS == "3" &&
    //             !(x_absolute_value <= -0.68 || x_absolute_value >= 0.67)
    //         ) {
    //             if (x_absolute_value < 0) {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "4",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             } else {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "3",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             }
    //         }
    //         // South x: -0.45
    //         else if (
    //             args.NEWS == "4" &&
    //             !(x_absolute_value < -0.4 && x_absolute_value > -0.5)
    //         ) {
    //             if (Math.abs(x_absolute_value) < 0.45) {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "4",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             } else {
    //                 quarkyRobotInstance.runRobot(
    //                     (args = {
    //                         DIRECTION: "3",
    //                         SPEED: "70",
    //                     }),
    //                     (util = util)
    //                 );
    //             }
    //         } else {
    //             quarkyRobotInstance.stopRobot(
    //                 (args = { SPEED: "70" }),
    //                 (util = util)
    //             );
    //             return true;
    //         }
    //         console.log(
    //             `---MAIN: dir:${args.NEWS} | x_absolute: ${x_absolute_value}`
    //         );
    //         util.yield();
    //         sleep(10);
    //     } else {
    //         if (recursive_cnt <= 3) {
    //             recursive_cnt++;
    //             util.yield();
    //         } else {
    //             recursive_cnt = 0;
    //         }
    //     }
    // }

    rotateDirection(args, util) {

        var phone_camera_left_right = cameraLocationFunction();
        if (phone_camera_left_right == "right") {
            return "kindly place phone properly"
        }
        
        // console.log(`Hello strat: ${args.NEWS} | alpha: ${alpha_value}`)

        var local_alpha = 360 - alpha_update();
        // North alpha: 270
        if (args.NEWS == "1" && !(local_alpha < 280 && local_alpha > 260)) {
            if (direction_turning == false) {
                if (local_alpha < 270 && local_alpha > 90) {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "4",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                } else {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "3",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                }
                direction_turning = true;
            }
        }

        // East alpha: 0
        else if (args.NEWS == "2" && !(local_alpha > 350 || local_alpha < 10)) {
            if (direction_turning == false) {
                if (local_alpha > 180) {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "4",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                } else {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "3",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                }
                direction_turning = true;
            }
        }

        // West alpha: 180
        else if (
            args.NEWS == "3" &&
            !(local_alpha < 190 && local_alpha > 170)
        ) {
            if (direction_turning == false) {
                if (local_alpha < 180) {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "4",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                } else {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "3",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                }
                direction_turning = true;
            }
        }

        // South alpha: 90
        else if (args.NEWS == "4" && !(local_alpha < 100 && local_alpha > 80)) {
            if (direction_turning == false) {
                if (local_alpha < 270 && local_alpha > 90) {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "3",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                } else {
                    quarkyRobotInstance.runRobot(
                        (args = {
                            DIRECTION: "4",
                            SPEED: args.SPEED,
                        }),
                        (util = util)
                    );
                }
                direction_turning = true;
            }
        } else {
            quarkyRobotInstance.stopRobot(
                (args = { SPEED: args.SPEED }),
                (util = util)
            );
            // sleep(10);
            direction_turning = false;
            return true;
        }

        // console.log(`---MAIN: dir:${args.NEWS} `);
        util.yield();
        sleep(10);
    }

    runRobotMeter(args, util) {
        // velocity = 0;
        // distance = 0;
        // total_distance = 0;

        // interval_cnt = 0;
        // is_robot_reach_meter = false;

        runRobotMeterArgs = args;
        runRobotMeterUtil = util;

        quarkyRobotInstance.runRobotTimed(
            (args = {
                DIRECTION: runRobotMeterArgs.DIRECTION,
                SPEED: 100,
                TIME: runRobotMeterArgs.METER * (100 / 13.07),
            }),
            (util = runRobotMeterUtil)
        );
    }
}

module.exports = mobileSensor;
