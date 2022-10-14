const fs = require("fs");
const css = require('css');

var command = process.argv

String.prototype.replaceAll = function (search, replacement) {
	var target = this;
	return target.replace(new RegExp(search, "g"), replacement);
};

//taking file path 
var filePath = command[2]

//reading the file 
var cssText = fs.readFileSync(filePath,'utf-8')

//split with module.id 
cssText = cssText.split('module.id')

var neededText = cssText[1].split("=")

text = neededText.slice(0,-1).join("=").split('", ""]);')[0]


startIndex = 0
//finding the proper index where the string starts
while (text[startIndex]!=='"'){
	startIndex+=1
}
text = text.slice(startIndex+1)


dict = neededText.slice(-1)


//parse the string into a object of key value pairs.
dict = JSON.parse(dict[0].replace(";",""))

ndict = {}
for (let key in dict) {
	if (!ndict[dict[key]]) ndict[dict[key]] = key 
}

//Adding new line after every closing bracket
text = text.replaceAll('}','}\n\n')

//replacing \r\n with a new line.
text = text.replace(/(?:\\[rn])+/g, "\n")


//Adding new line after every comment
text = text.replace(/(?:[*][/])+/g, "*/\n")

var ast = css.parse(text);

function addSelector(selector, selectors, addSpace) {
	if (selector[0] === '.'){
		if(selector.substring(1) in ndict)
			selectors.push('.'+ndict[selector.substring(1)]);	
		else
			selectors.push(":global("+selector+")")
	} else {
		selectors.push(selector)
	}	
	if (addSpace) {
		selectors[selectors.length-1]+= ' ';
	}
}

for (var i=0; i<ast.stylesheet.rules.length; i++) {
	let rule = ast.stylesheet.rules[i];
	if (rule.type == 'rule') {
		//Replacing all the values with keys
		for (let m=0; m<rule.selectors.length; m++) {
			let combinedSelector = rule.selectors[m];
			var selectors = []
			var selector = ''
			for (let x=0; x<combinedSelector.length; x++) {
				y = combinedSelector[x]
				if (y=='.') {
					if (selector) { addSelector(selector, selectors, false); selector = ''}
					selector = y;
				} else if (y==':'  && combinedSelector[x+1]==':') {
					if (selector) { addSelector(selector, selectors, false); selector = ''}
					selector = '::';
					x+=1
				} else if (y==':') {
					if (selector) { addSelector(selector, selectors, false); selector = ''}
					selector = ':';
				}
				else if (y==' ') {
					if (selector) { addSelector(selector, selectors, true); selector = ''}
				} else {
					selector += y;
				}
			}
			addSelector(selector, selectors, false)
			combinedSelector = selectors.join('')
			rule.selectors[m] = combinedSelector;
		}
	}
}

newCss = css.stringify(ast)


fs.renameSync(filePath,filePath+".pb")
//output file.
fs.writeFileSync(filePath,newCss)
