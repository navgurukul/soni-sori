// To make compatible with previous versions
const previosExtensionsMapping = {
    'actuators': {
        isCommonExtension: true,
        extensions: [
            'eviveActuators',
            'arduinoUnoActuators',
            'arduinoNanoActuators',
            'tecBitsActuators',
            'arduinoMegaActuators',
            'esp32Actuators',
            'quonActuators',
            'tWatchActuators'
        ]
    },
    'sensors': {
        isCommonExtension: true,
        extensions: [
            'eviveSensors',
            'arduinoUnoSensors',
            'arduinoNanoSensors',
            'arduinoMegaSensors',
            'tecBitsSensors',
            'esp32Sensors',
            'quonSensors',
            'tWatchSensors'
        ]
    },
    'lightning': {
        isCommonExtension: true,
        extensions: [
            'eviveLightning',
            'arduinoMegaLightning',
            'arduinoUnoLightning',
            'arduinoNanoLightning',
            'tecBitsLightning',
            'esp32Lightning',
            'quonLightning',
            'tWatchLightning'
        ]
    },
    'dabble': {
        isCommonExtension: true,
        extensions: [
            'eviveDabble',
            'arduinoMegaDabble',
            'arduinoUnoDabble',
            'arduinoNanoDabble',
            'tecBitsDabble',
            'esp32Dabble',
            'quonDabble',
            'tWatchDabble'
        ]
    },
    'communication': {
        isCommonExtension: true,
        extensions: [
            'eviveCommunication',
            'arduinoMegaCommunication',
            'arduinoUnoCommunication',
            'arduinoNanoCommunication',
            'tecBitsCommunication',
            'esp32Communication',
            'quonCommunication',
            'tWatchCommunication'
        ]
    },
    'iot': {
        isCommonExtension: true,
        extensions: [
            'eviveIot',
            'esp32Iot',
            'quonIot',
            'tWatchIot',
        ]
    },
    'displayModule': {
        isCommonExtension: true,
        extensions: [
            'eviveOtherDisplay',
            'arduinoMegaDisplay',
            'arduinoUnoDisplay',
            'arduinoNanoDisplay',
            'tecBitsDisplay',
        ]
    },
    'advanceSensor': {
        isCommonExtension: true,
        extensions: [
            'eviveAdvanceSensors',
            'arduinoMegaAdvanceSensors',
            'arduinoUnoAdvanceSensors',
            'arduinoNanoAdvanceSensors',
            'tecBitsAdvanceSensors'
        ]
    },
    'aiServices': {
        isCommonExtension: false,
        extensions: [
            'AIServices'
        ]
    }
};

/**
 * Checks if the extensionURL is of the previous versions and returns the extension URL changed into for this version.
 * @param {string} extensionURL - the URL for the extension to load OR the ID of an internal extension.
 * @returns {string} extension URL found for this version for extensionURL argument.
*/
function getExtensionURLOfThisVersion(extensionURL) {
    for (let extension in previosExtensionsMapping) {
        // If extensionURL has may become a common extension.
        if (typeof previosExtensionsMapping[extension].extensions === 'object') {
            if (previosExtensionsMapping[extension].extensions.includes(extensionURL)) {
                return { isCommonExtension: previosExtensionsMapping[extension].isCommonExtension, extension: extension };
            }
        } else if (previosExtensionsMapping[extension].extensions === extension) {
            // If extensionURL of the extension have been changed.
            return { isCommonExtension: previosExtensionsMapping[extension].isCommonExtension, extension: extension };
        }
    }
    return { extension: extensionURL };
}

export {
    previosExtensionsMapping as default,
    getExtensionURLOfThisVersion
};