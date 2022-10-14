let patternCount = null;
let inputNodes = null;
let outputNodes = null;
let hiddenNodes = null;
let learningRate = 0.3;
let momentum = 0.9;
let initialWeightMax = 0.5;
let success = 0.0004;
let epoch = 10000;

let i = 0;
let j = 0;
let p = 0;
let q = 0;
let r = 0;

var input = [];
let target = [];

let reportEvery100;
let randomizedIndex = Array(100).fill(0);
let trainingCycle;
let rando;
let error;
let accum;

let hidden = Array(10).fill(0); 
let output = Array(4).fill(0);
let hiddenWeights = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
let outputWeights = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
let hiddenDelta = Array(10).fill(0);
let outputDelta = Array(4).fill(0); 
let changeHiddenWeights = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];
let changeOutputWeights = [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]];

const setPatternCount = (args, util) => {
    patternCount = parseInt(args.DATA);
};

const setNodes = (args, util) => {
    inputNodes = parseInt(args.INPUT);
    hiddenNodes = parseInt(args.HIDDEN);
    outputNodes = parseInt(args.OUTPUT);
};

const defineParameter = (args, util) => {
    switch(args.PARAMETER) {
        case "4":
            epoch = parseInt(args.DATA);
            break;
        case "5":
            learningRate = parseFloat(args.DATA);
            break;
        case "6":
            momentum = parseFloat(args.DATA);
            break;
        case "7":
            initialWeightMax = parseFloat(args.DATA);
            break;
        case "8":
            success = parseFloat(args.DATA);
            break;
    }
};

const setInputPattern = (args, util) => {
    let patternID = parseInt(args.PATTERNID) - 1;
    input[patternID] = [parseFloat(args.DATA1), parseFloat(args.DATA2), parseFloat(args.DATA3), parseFloat(args.DATA4), parseFloat(args.DATA5), parseFloat(args.DATA6), parseFloat(args.DATA7), parseFloat(args.DATA8)];
    console.log(input);
};

const setTargetPattern = (args, util) => {
    let patternID = parseInt(args.PATTERNID) - 1;
    target[patternID] = [parseFloat(args.DATA1), parseFloat(args.DATA2), parseFloat(args.DATA3), parseFloat(args.DATA4), parseFloat(args.DATA5), parseFloat(args.DATA6), parseFloat(args.DATA7), parseFloat(args.DATA8)];
    console.log(target);
};

const startTraining = (args, util) => {

    reportEvery100 = 1;
    for(p = 0; p < patternCount; p++){
        randomizedIndex[p] = p;
    }

    //Initialise hiddenWeights and changeHiddenWeights
    for(i = 0; i < hiddenNodes; i++){
        for(j = 0; j < inputNodes; j++){
            changeHiddenWeights[j][i] = 0;
            rando = Math.random();
            hiddenWeights[j][i] = 2*(rando - 0.5)*initialWeightMax;
        }
    }

    //Initialise outputWeights and changeOutputWeights
    for(i = 0; i < outputNodes; i++){
        for(j = 0; j < hiddenNodes; j++){
            changeOutputWeights[j][i] = 0;
            rando = Math.random();
            outputWeights[j][i] = 2*(rando - 0.5)*initialWeightMax;
        }
    }

    //Begin training
    for(trainingCycle = 1; trainingCycle < epoch; trainingCycle++){
        //Randomise order of training patterns
        for(p = 0; p < patternCount; p++){
            q = Math.floor(Math.random() * patternCount);
            r = randomizedIndex[p];
            randomizedIndex[p] = randomizedIndex[q]
            randomizedIndex[q] = r;
        }
        error = 0;

        //Cycle through each training pattern in the randomized order
        for(q = 0; q < patternCount; q++){
            p = randomizedIndex[q];
            //Compute hidden layer activations
            for(i = 0; i < hiddenNodes; i++){
                accum = hiddenWeights[inputNodes][i];
                for(j = 0; j < inputNodes; j++){
                    accum = accum + input[p][j] * hiddenWeights[j][i];
                }
                hidden[i] = 1/(1 + Math.exp(-accum));
            }

            //Compute output layer activations and calculate errors
            for(i=0; i < outputNodes; i++){
                accum = outputWeights[hiddenNodes][i];
                for(j = 0; j < hiddenNodes; j++){
                    accum = accum + hidden[j] * outputWeights[j][i];
                }
                output[i] = 1/(1 + Math.exp(-accum));
                outputDelta[i] = (target[p][i] - output[i]) * output[i] * (1 - output[i]);
                error = error + 0.5 * (target[p][i] - output[i]) * (target[p][i] - output[i]);
            }

            //Backpropagate errors to hidden layer
            for(i = 0; i < hiddenNodes; i++){
                accum = 0;
                for(j = 0; j < outputNodes; j++){
                    accum = accum + outputWeights[i][j] * outputDelta[j];
                }
                hiddenDelta[i] = accum * hidden[i] * (1 - hidden[i]);
            }

            //Update Inner-->Hidden Weights
            for(i = 0; i < hiddenNodes; i++){
                changeHiddenWeights[inputNodes][i] = learningRate * hiddenDelta[i] + momentum * changeHiddenWeights[inputNodes][i];
                hiddenWeights[inputNodes][i] += changeHiddenWeights[inputNodes][i];
                for(j = 0; j < inputNodes; j++){
                    changeHiddenWeights[j][i] = learningRate * input[p][j] * hiddenDelta[i] + momentum * changeHiddenWeights[j][i];
                    hiddenWeights[j][i] += changeHiddenWeights[j][i];
                }
            }

            //Update Hidden-->Output Weights
            for(i = 0; i < outputNodes; i++){
                changeOutputWeights[hiddenNodes][i] = learningRate * outputDelta[i] + momentum * changeOutputWeights[hiddenNodes][i];
                outputWeights[hiddenNodes][i] += changeOutputWeights[hiddenNodes][i];
                for(j = 0; j < hiddenNodes; j++){
                    changeOutputWeights[j][i] = learningRate * input[p][j] * outputDelta[i] + momentum * changeOutputWeights[j][i];
                    outputWeights[j][i] += changeOutputWeights[j][i];
                }
            }
        }

        //Every 100 cycles send data to console for display
        reportEvery100 = reportEvery100 - 1;
        if(reportEvery100 === 0){
            console.log("TrainingCycle: " + trainingCycle);
            console.log("Error: " + error);
            if (trainingCycle === 1){
                reportEvery100 = 99;
            }
            else{
                reportEvery100 = 100;
            }
        }

        //If error rate is less than pre-determined threshold then end
        if(error < success){
            break;
        }
    }

    console.log("TrainingCycle: " + trainingCycle);
    console.log("Error: " + error);
    console.log("Training Set Solved!");
    console.log("Error: " + error);

};

const calculateOutput = (args, util) => {
    let testInput = [parseFloat(args.DATA1), parseFloat(args.DATA2), parseFloat(args.DATA3), parseFloat(args.DATA4)];

    console.log(testInput);

    //Compute hidden layer activations
    for(i=0; i < hiddenNodes; i++){
        accum = hiddenWeights[inputNodes][i];
        for(j=0; j < inputNodes; j++){
            accum += testInput[j] * hiddenWeights[j][i];
        }
        hidden[i] = 1/(1 + Math.exp(-accum));
    }

    console.log(hidden);

    //Compute output layer activations and calculate errors
    for(i=0; i < outputNodes; i++){
        accum = outputWeights[hiddenNodes][i];
        for(j=0; j < hiddenNodes; j++){
            accum += hidden[j] * outputWeights[j][i];
        }
        output[i] = 1/(1 + Math.exp(-accum));
    }

    console.log(output);
};

const getOutput = (args, util) => {
    return output[args.OUTPUT - 1];
};

module.exports = {
    defineParameter,
    startTraining,
    setTargetPattern,
    setInputPattern,
    calculateOutput,
    getOutput,
    setNodes,
    setPatternCount
};
