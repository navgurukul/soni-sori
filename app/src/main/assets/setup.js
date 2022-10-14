const process = require('process')
const { execSync } = require("child_process");
const fs = require('fs');

var command = process.argv
var exPath = command[2]
var mainPath = command[3]
var ifDev = command[4] ? '--save-dev ' : ''

process.chdir('local_modules/'+exPath)

execSync('npm init -y')

jsonC = require('./local_modules/'+exPath+'/'+'package.json')
jsonC.main = mainPath;

fs.truncate('./local_modules/'+exPath+'/'+'package.json', 0, function() {
    fs.writeFileSync('package.json', JSON.stringify(jsonC, null, 4))
    process.chdir('../..')
    console.log('Executing: npm install ' + ifDev + 'file:./local_modules/'+exPath)
    execSync('npm install ' + ifDev + 'file:./local_modules/'+exPath)
})
