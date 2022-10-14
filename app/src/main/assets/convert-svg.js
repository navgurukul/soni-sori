const fs = require("fs");
var command = process.argv
var filePath = command[2]
var svgText = fs.readFileSync(filePath,'utf-8')

if (svgText.search('__webpack_public_path__')>-1) {
    svgURL = svgText.split('__webpack_public_path__')[1].split('"')[1]
    if (svgURL.search("static")>-1) {
        fs.renameSync(svgURL,filePath)
        // fs.unlinkSync(svgURL)
        console.log("PASS : ", filePath)
    } else {
        console.log("FAIL", filePath)
    }
}