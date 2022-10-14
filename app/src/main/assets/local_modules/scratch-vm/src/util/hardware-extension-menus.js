const formatMessage = require('format-message');
const commonForAll = {
    baudRate: ['9600', '19200', '38400', '57600', '74880', '115200', '250000', '500000', '1000000', '2000000']
};

const digitalPins = {
    evive: [
        '2', '3', '4', '5', '6', '7', '8', '9',
        '10', '11', '12', '13', '22', '23', '24', '25', '26', '27',
        { text: 'A0', value: '54' },
        { text: 'A1', value: '55' },
        { text: 'A2', value: '56' },
        { text: 'A3', value: '57' },
        { text: 'A4', value: '58' },
        { text: 'A5', value: '59' },
        { text: 'A12', value: '66' },
        { text: 'A13', value: '67' },
        { text: 'A14', value: '68' },
        { text: 'A15', value: '69' }
    ],
    arduinoUno: [
        '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13',
        { text: 'A0', value: '14' },
        { text: 'A1', value: '15' },
        { text: 'A2', value: '16' },
        { text: 'A3', value: '17' },
        { text: 'A4', value: '18' },
        { text: 'A5', value: '19' }
        //'14','15','16','17','18','19'

    ],
    arduinoNano: [
        '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13',
        { text: 'A0', value: '14' },
        { text: 'A1', value: '15' },
        { text: 'A2', value: '16' },
        { text: 'A3', value: '17' },
        { text: 'A4', value: '18' },
        { text: 'A5', value: '19' },
        { text: 'A6', value: '20' },
        { text: 'A7', value: '21' }
    ],
    tecBits: [
        { text: '1', value: '3' },
        { text: '2', value: '5' },
        { text: '3', value: '6' },
        { text: '4', value: '9' },
        { text: '5', value: '10' },
        { text: '6', value: '11' }
    ],
    arduinoMega: [
        '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16',
        '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30',
        '31', '32', '33', '34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44',
        '45', '46', '47', '48', '49', '50', '51', '52', '53',
        { text: 'A0', value: '54' },
        { text: 'A1', value: '55' },
        { text: 'A2', value: '56' },
        { text: 'A3', value: '57' },
        { text: 'A4', value: '58' },
        { text: 'A5', value: '59' },
        { text: 'A6', value: '60' },
        { text: 'A7', value: '61' },
        { text: 'A8', value: '62' },
        { text: 'A9', value: '63' },
        { text: 'A10', value: '64' },
        { text: 'A11', value: '65' },
        { text: 'A12', value: '66' },
        { text: 'A13', value: '67' },
        { text: 'A14', value: '68' },
        { text: 'A15', value: '69' }

    ],
    esp32: ['2', '4', '5', '12', '13', '14', '15', '16', '17', '18', '19', '21', '22', '23', '25', '26', '27'],
    tWatch: ['2', '4', '5', '12', '13', '14', '15', '16', '17', '18', '19', '21', '22', '23', '25', '26', '27'],
    quon: [
        { text: '3', value: '3' },
        { text: '4', value: '4' },
        { text: '5', value: '5' },
        { text: '6', value: '6' },
        { text: 'PORTC', value: 'PC1' },
        { text: 'PORTD', value: 'PD1' }
    ],
    quarky: [
        { text: 'D1', value: '18' },
        { text: 'D2', value: '19' },
        { text: 'D3', value: '26' }
    ]
};

const analogPins = {
    evive: [
        { text: 'A0', value: '0' },
        { text: 'A1', value: '1' },
        { text: 'A2', value: '2' },
        { text: 'A3', value: '3' },
        { text: 'A4', value: '4' },
        { text: 'A5', value: '5' },
        { text: 'A12', value: '12' },
        { text: 'A13', value: '13' },
        { text: 'A14', value: '14' },
        { text: 'A15', value: '15' }
    ],
    arduinoUno: [
        { text: 'A0', value: '0' },
        { text: 'A1', value: '1' },
        { text: 'A2', value: '2' },
        { text: 'A3', value: '3' },
        { text: 'A4', value: '4' },
        { text: 'A5', value: '5' }
    ],
    arduinoNano: [
        { text: 'A0', value: '0' },
        { text: 'A1', value: '1' },
        { text: 'A2', value: '2' },
        { text: 'A3', value: '3' },
        { text: 'A4', value: '4' },
        { text: 'A5', value: '5' },
        { text: 'A6', value: '6' },
        { text: 'A7', value: '7' }
    ],
    arduinoMega: [
        { text: 'A0', value: '0' },
        { text: 'A1', value: '1' },
        { text: 'A2', value: '2' },
        { text: 'A3', value: '3' },
        { text: 'A4', value: '4' },
        { text: 'A5', value: '5' },
        { text: 'A6', value: '6' },
        { text: 'A7', value: '7' },
        { text: 'A8', value: '8' },
        { text: 'A9', value: '9' },
        { text: 'A10', value: '10' },
        { text: 'A11', value: '11' },
        { text: 'A12', value: '12' },
        { text: 'A13', value: '13' },
        { text: 'A14', value: '14' },
        { text: 'A15', value: '15' }
    ],
    esp32: ['32', '33', '34', '35', '36', '39'],
    tWatch: ['32', '33', '34', '35', '36', '34', '25', '26'],
    quon: [
        { text: '1', value: '1' },
        { text: '2', value: '2' },
        { text: 'PORTA', value: 'PA1' },
        { text: 'PORTB', value: 'PB1' }
    ],
    quarky: [
        { text: 'A1', value: '33' },
        { text: 'A2', value: '32' },
        { text: 'A3', value: '39' }
    ],
    tecBits: [
        { text: '7', value: '0' },
        { text: '8', value: '1' }
    ]
};

const pwmPins = {
    evive: [
        { text: '13', value: '13' },
        { text: '2', value: '2' },
        { text: '3', value: '3' },
        { text: '4', value: '4' },
        { text: '5', value: '5' },
        { text: '6', value: '6' },
        { text: '7', value: '7' },
        { text: '8', value: '8' },
        { text: '9', value: '9' },
        { text: '10', value: '10' },
        { text: '11', value: '11' },
        { text: '12', value: '12' },
        { text: '44', value: '44' },
        { text: '45', value: '45' }
    ],
    arduinoUno: [
        '3', '5', '6', '9', '10', '11'
    ],
    arduinoNano: [
        '3', '5', '6', '9', '10', '11'
    ],
    arduinoMega: [
        '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '44', '45', '46'
    ],
    esp32: digitalPins.esp32,
    tWatch: digitalPins.tWatch,
    quon: [
        { text: '3', value: '3' },
        { text: '4', value: '4' },
        { text: '5', value: '5' },
        { text: '6', value: '6' }
    ],
    quarky: digitalPins.quarky,
    tecBits: digitalPins.tecBits
};

const motor = {
    evive: ['1', '2'],
    arduinoUno: ['1', '2', '3', '4'],
    arduinoNano: ['1', '2', '3', '4'],
    arduinoMega: ['1', '2', '3', '4', '5'],
    esp32: ['1', '2', '3', '4'],
    tWatch: ['1', '2', '3', '4'],
    quon: ['1', '2'],
    quarky: ['1', '2'],
    tecBits: ['1', '2']
};

const serialPort = {
    evive: ['0', '1', '2', '3'],
    arduinoUno: ['0'],
    arduinoNano: ['0'],
    arduinoMega: ['0', '1', '2', '3'],
    esp32: ['0', '1', '2'],
    tWatch: ['0', '1', '2'],
    quon: ['0', '1', '2'],
    quarky: ['0', '1', '2'],
    tecBits: ['0', '1', '2']
};

const servoChannel = {
    evive: [{ text: 'Channel 1', value: '44' },
    { text: 'Channel 2', value: '45' },
        '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13'],
    arduinoUno: pwmPins.arduinoUno,
    arduinoMega: pwmPins.arduinoMega,
    arduinoNano: pwmPins.arduinoNano,
    esp32: pwmPins.esp32,
    tWatch: pwmPins.tWatch,
    quon: [
        { text: 'Channel 1', value: '5' },
        { text: 'Channel 2', value: '18' },
        { text: 'Channel 3', value: '2' },
        { text: 'Channel 4', value: '12' },
        { text: '3', value: '27' },
        { text: '4', value: '14' },
        { text: '5', value: '15' },
        { text: '6', value: '13' }
    ],
    quarky: [
        { text: 'Channel 1', value: '22' },
        { text: 'Channel 2', value: '23' },
        { text: 'D1', value: '18' },
        { text: 'D2', value: '19' },
        { text: 'D3', value: '26' },
    ],
    tecBits: digitalPins.tecBits
};

const serialPins = {
    esp32: digitalPins.esp32,
    tWatch: digitalPins.tWatch,
    quon: [
        { text: '3', value: '27' },
        { text: '4', value: '14' },
        { text: '5', value: '13' },
        { text: '6', value: '15' }
    ],
    quarky: digitalPins.quarky
};

module.exports = {
    commonForAll,
    digitalPins,
    analogPins,
    pwmPins,
    motor,
    servoChannel,
    serialPort,
    serialPins
};
