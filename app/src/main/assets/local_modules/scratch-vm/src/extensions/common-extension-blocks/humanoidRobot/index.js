const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;

const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM4MDgyODU7fQ0KCS5zdDF7ZmlsbDojMDBFMjE1O30NCgkuc3Qye2ZpbGw6I0JDQkVDMDt9DQoJLnN0M3tmaWxsOiNGMUYyRjI7fQ0KCS5zdDR7ZmlsbDojRTZFN0U4O30NCgkuc3Q1e2ZpbGw6I0QxRDNENDt9DQoJLnN0NntmaWxsOiMyMzFGMjA7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF8xMjc5OV8iIGNsYXNzPSJzdDAiIGQ9Ik0yNi40LDM1LjNjMCwwLTAuMSwwLTAuMSwwYy0wLjIsMC0wLjMtMC4xLTAuNS0wLjNsLTAuMy0wLjRjLTAuMi0wLjItMC4yLTAuNS0wLjItMC44DQoJYzAtMC4yLDAuMi0wLjQsMC4zLTAuNmwtMS4xLTEuNmMtMC4zLTAuNC0wLjQtMS0wLjItMS40bC0yLjYtMy44aC0zdjMuM2MwLjQsMC4zLDAuNiwwLjgsMC42LDEuM1YzM2MwLjUsMC4xLDAuOCwwLjUsMC44LDF2MC40DQoJYzAsMC40LTAuMywwLjctMC43LDAuN2gtOWMtMC40LDAtMC43LTAuMy0wLjctMC43VjM0YzAtMC42LDAuNS0xLDEtMWgxLjR2LTEuOWMwLTAuNSwwLjItMSwwLjYtMS4zdi0zLjNoLTIuM0M5LjcsMjYuNSw5LDI1LjgsOSwyNQ0KCXYtNC4zbC00LjQtMi41Yy0wLjcsMC41LTEuNSwwLjctMi4zLDAuN2MtMC43LDAtMS40LTAuMi0yLTAuNWMtMC4zLTAuMi0wLjUtMC42LTAuMy0xTDEsMTUuOWMwLjEtMC4yLDAuMi0wLjMsMC40LTAuMw0KCWMwLjEsMCwwLjEsMCwwLjIsMGMwLjEsMCwwLjIsMCwwLjQsMC4xYzAuMSwwLjEsMC4zLDAuMSwwLjQsMC4xYzAuMywwLDAuNi0wLjIsMC44LTAuNGMwLjEtMC4yLDAuMS0wLjQsMC4xLTAuNw0KCWMtMC4xLTAuMi0wLjItMC40LTAuNC0wLjVjLTAuMi0wLjEtMC4zLTAuMi0wLjMtMC40YzAtMC4yLDAtMC40LDAuMS0wLjVsMC45LTEuNWMwLjEtMC4yLDAuMi0wLjMsMC40LTAuM2MwLjEsMCwwLjEsMCwwLjIsMA0KCWMwLjEsMCwwLjIsMCwwLjQsMC4xYzEuNCwwLjgsMi4xLDIuMywyLDMuOEw5LDE2Ljd2LTAuOWMwLTAuNywwLjUtMS40LDEuMi0xLjV2LThjMC0wLjksMC43LTEuNiwxLjYtMS42aDE2LjMNCgljMC45LDAsMS42LDAuNywxLjYsMS42djhjMC43LDAuMSwxLjIsMC43LDEuMiwxLjV2MC45bDIuNy0xLjVjLTAuMS0xLjUsMC42LTMsMi0zLjhjMC4xLTAuMSwwLjItMC4xLDAuNC0wLjFjMC4xLDAsMC4xLDAsMC4yLDANCgljMC4yLDAsMC4zLDAuMiwwLjQsMC4zbDAuOSwxLjVjMC4xLDAuMiwwLjEsMC40LDAuMSwwLjVjMCwwLjItMC4yLDAuMy0wLjMsMC40Yy0wLjIsMC4xLTAuMywwLjMtMC40LDAuNWMtMC4xLDAuMiwwLDAuNSwwLjEsMC43DQoJYzAuMiwwLjMsMC40LDAuNCwwLjgsMC40YzAuMiwwLDAuMywwLDAuNC0wLjFjMC4xLTAuMSwwLjItMC4xLDAuNC0wLjFjMC4xLDAsMC4xLDAsMC4yLDBjMC4yLDAsMC4zLDAuMiwwLjQsMC4zbDAuOSwxLjUNCgljMC4yLDAuMywwLjEsMC44LTAuMywxYy0wLjYsMC40LTEuMywwLjUtMiwwLjVsMCwwYy0wLjgsMC0xLjYtMC4zLTIuMy0wLjdsLTQuNCwyLjVWMjVjMCwwLjgtMC43LDEuNS0xLjUsMS41aC0wLjVsMC4zLDAuNA0KCWMwLjUsMCwxLDAuMywxLjMsMC43bDEuMSwxLjZsMS4xLTAuN2MwLjItMC4xLDAuNC0wLjIsMC42LTAuMmMwLjMsMCwwLjYsMC4yLDAuOCwwLjRsMC4zLDAuNGMwLjIsMC4zLDAuMSwwLjgtMC4yLDFsLTcuNCw1LjINCglDMjYuNiwzNS4zLDI2LjUsMzUuMywyNi40LDM1LjN6Ii8+DQo8ZyBpZD0iWE1MSURfMTI3OThfIj4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NTdfIiBjbGFzcz0ic3QxIiBkPSJNMjksMTVIMTFWNi4zYzAtMC41LDAuNC0wLjksMC45LTAuOWgxNi4zYzAuNSwwLDAuOSwwLjQsMC45LDAuOVYxNXoiLz4NCgk8ZyBpZD0iWE1MSURfMTI3NTNfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNzU2XyIgeD0iMTMuNSIgeT0iMjQuNSIgY2xhc3M9InN0MiIgd2lkdGg9IjQuNSIgaGVpZ2h0PSI5LjkiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzU1XyIgY2xhc3M9InN0MyIgZD0iTTE4LjcsMzQuNGgtNS44di0zLjRjMC0wLjUsMC40LTAuOSwwLjktMC45aDMuOWMwLjUsMCwwLjksMC40LDAuOSwwLjlWMzQuNHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzU0XyIgY2xhc3M9InN0NCIgZD0iTTE5LjEsMzMuN2gtOC40Yy0wLjIsMC0wLjMsMC4xLTAuMywwLjN2MC40aDlWMzRDMTkuNSwzMy44LDE5LjMsMzMuNywxOS4xLDMzLjd6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8xMjc0OV8iPg0KCQkNCgkJCTxyZWN0IGlkPSJYTUxJRF8xMjc1Ml8iIHg9IjI0LjQiIHk9IjIzLjQiIHRyYW5zZm9ybT0ibWF0cml4KDAuODE5MiAtMC41NzM2IDAuNTczNiAwLjgxOTIgLTExLjQ0NzggMjAuMzkyNykiIGNsYXNzPSJzdDIiIHdpZHRoPSI0LjUiIGhlaWdodD0iOS45Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjc1MV8iIGNsYXNzPSJzdDMiIGQ9Ik0zMS44LDMwLjhsLTQuNywzLjNsLTEuOS0yLjhjLTAuMy0wLjQtMC4yLTEsMC4yLTEuM2wzLjItMi4yYzAuNC0wLjMsMS0wLjIsMS4zLDAuMg0KCQkJTDMxLjgsMzAuOHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzUwXyIgY2xhc3M9InN0NCIgZD0iTTMzLDI5bC02LjksNC44Yy0wLjEsMC4xLTAuMiwwLjMtMC4xLDAuNGwwLjMsMC40bDcuNC01LjJsLTAuMy0wLjQNCgkJCUMzMy40LDI4LjksMzMuMiwyOC45LDMzLDI5eiIvPg0KCTwvZz4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NDdfIiBjbGFzcz0ic3Q1IiBkPSJNNC4xLDExLjlsLTAuOSwxLjVjMC44LDAuNCwxLDEuNCwwLjYsMi4ycy0xLjQsMS0yLjIsMC42bC0wLjksMS41YzEuMywwLjcsMi45LDAuNSwzLjktMC40DQoJCWw2LjUsMy43YzAuNSwwLjMsMS4xLDAuMSwxLjQtMC40bDAsMGMwLjMtMC41LDAuMS0xLjEtMC40LTEuNGwtNi41LTMuN0M2LDE0LjEsNS4zLDEyLjcsNC4xLDExLjl6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyNzQ2XyIgY2xhc3M9InN0NSIgZD0iTTM5LjMsMTcuN2wtMC45LTEuNWMtMC44LDAuNC0xLjcsMC4yLTIuMi0wLjZjLTAuNC0wLjgtMC4yLTEuNywwLjYtMi4ybC0wLjktMS41DQoJCWMtMS4zLDAuNy0xLjksMi4yLTEuNiwzLjZsLTYuNSwzLjdjLTAuNSwwLjMtMC43LDAuOS0wLjQsMS40bDAsMGMwLjMsMC41LDAuOSwwLjcsMS40LDAuNGw2LjUtMy43QzM2LjQsMTguMywzOCwxOC41LDM5LjMsMTcuN3oiDQoJCS8+DQoJPGcgaWQ9IlhNTElEXzEyNTcyXyI+DQoJCTxnIGlkPSJYTUxJRF8xMjY1NF8iPg0KCQkJPHBhdGggaWQ9IlhNTElEXzEyNzQ0XyIgY2xhc3M9InN0NiIgZD0iTTE3LjksMTIuOGgtMy42Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJWOWMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMy42DQoJCQkJQzE3LjksOC45LDE4LDguOSwxOCw5djMuNkMxOCwxMi43LDE3LjksMTIuOCwxNy45LDEyLjh6Ii8+DQoJCTwvZz4NCgkJPGcgaWQ9IlhNTElEXzEyNTgxXyI+DQoJCQk8cGF0aCBpZD0iWE1MSURfMTI2NTFfIiBjbGFzcz0ic3Q2IiBkPSJNMjUuNywxMi44aC0zLjZjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMlY5YzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgzLjYNCgkJCQljMC4xLDAsMC4yLDAuMSwwLjIsMC4ydjMuNkMyNS45LDEyLjcsMjUuOCwxMi44LDI1LjcsMTIuOHoiLz4NCgkJPC9nPg0KCTwvZz4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NDhfIiBjbGFzcz0ic3QzIiBkPSJNMjkuNCwyNS44SDEwLjZjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYtOS4yYzAtMC40LDAuNC0wLjgsMC44LTAuOGgxOC45DQoJCWMwLjQsMCwwLjgsMC40LDAuOCwwLjhWMjVDMzAuMiwyNS41LDI5LjksMjUuOCwyOS40LDI1Ljh6Ii8+DQo8L2c+DQo8L3N2Zz4NCg==';

const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM4MDgyODU7fQ0KCS5zdDF7ZmlsbDojMDBFMjE1O30NCgkuc3Qye2ZpbGw6I0JDQkVDMDt9DQoJLnN0M3tmaWxsOiNGMUYyRjI7fQ0KCS5zdDR7ZmlsbDojRTZFN0U4O30NCgkuc3Q1e2ZpbGw6I0QxRDNENDt9DQoJLnN0NntmaWxsOiMyMzFGMjA7fQ0KPC9zdHlsZT4NCjxwYXRoIGlkPSJYTUxJRF8xMjc5OV8iIGNsYXNzPSJzdDAiIGQ9Ik0yNi40LDM1LjNjMCwwLTAuMSwwLTAuMSwwYy0wLjIsMC0wLjMtMC4xLTAuNS0wLjNsLTAuMy0wLjRjLTAuMi0wLjItMC4yLTAuNS0wLjItMC44DQoJYzAtMC4yLDAuMi0wLjQsMC4zLTAuNmwtMS4xLTEuNmMtMC4zLTAuNC0wLjQtMS0wLjItMS40bC0yLjYtMy44aC0zdjMuM2MwLjQsMC4zLDAuNiwwLjgsMC42LDEuM1YzM2MwLjUsMC4xLDAuOCwwLjUsMC44LDF2MC40DQoJYzAsMC40LTAuMywwLjctMC43LDAuN2gtOWMtMC40LDAtMC43LTAuMy0wLjctMC43VjM0YzAtMC42LDAuNS0xLDEtMWgxLjR2LTEuOWMwLTAuNSwwLjItMSwwLjYtMS4zdi0zLjNoLTIuM0M5LjcsMjYuNSw5LDI1LjgsOSwyNQ0KCXYtNC4zbC00LjQtMi41Yy0wLjcsMC41LTEuNSwwLjctMi4zLDAuN2MtMC43LDAtMS40LTAuMi0yLTAuNWMtMC4zLTAuMi0wLjUtMC42LTAuMy0xTDEsMTUuOWMwLjEtMC4yLDAuMi0wLjMsMC40LTAuMw0KCWMwLjEsMCwwLjEsMCwwLjIsMGMwLjEsMCwwLjIsMCwwLjQsMC4xYzAuMSwwLjEsMC4zLDAuMSwwLjQsMC4xYzAuMywwLDAuNi0wLjIsMC44LTAuNGMwLjEtMC4yLDAuMS0wLjQsMC4xLTAuNw0KCWMtMC4xLTAuMi0wLjItMC40LTAuNC0wLjVjLTAuMi0wLjEtMC4zLTAuMi0wLjMtMC40YzAtMC4yLDAtMC40LDAuMS0wLjVsMC45LTEuNWMwLjEtMC4yLDAuMi0wLjMsMC40LTAuM2MwLjEsMCwwLjEsMCwwLjIsMA0KCWMwLjEsMCwwLjIsMCwwLjQsMC4xYzEuNCwwLjgsMi4xLDIuMywyLDMuOEw5LDE2Ljd2LTAuOWMwLTAuNywwLjUtMS40LDEuMi0xLjV2LThjMC0wLjksMC43LTEuNiwxLjYtMS42aDE2LjMNCgljMC45LDAsMS42LDAuNywxLjYsMS42djhjMC43LDAuMSwxLjIsMC43LDEuMiwxLjV2MC45bDIuNy0xLjVjLTAuMS0xLjUsMC42LTMsMi0zLjhjMC4xLTAuMSwwLjItMC4xLDAuNC0wLjFjMC4xLDAsMC4xLDAsMC4yLDANCgljMC4yLDAsMC4zLDAuMiwwLjQsMC4zbDAuOSwxLjVjMC4xLDAuMiwwLjEsMC40LDAuMSwwLjVjMCwwLjItMC4yLDAuMy0wLjMsMC40Yy0wLjIsMC4xLTAuMywwLjMtMC40LDAuNWMtMC4xLDAuMiwwLDAuNSwwLjEsMC43DQoJYzAuMiwwLjMsMC40LDAuNCwwLjgsMC40YzAuMiwwLDAuMywwLDAuNC0wLjFjMC4xLTAuMSwwLjItMC4xLDAuNC0wLjFjMC4xLDAsMC4xLDAsMC4yLDBjMC4yLDAsMC4zLDAuMiwwLjQsMC4zbDAuOSwxLjUNCgljMC4yLDAuMywwLjEsMC44LTAuMywxYy0wLjYsMC40LTEuMywwLjUtMiwwLjVsMCwwYy0wLjgsMC0xLjYtMC4zLTIuMy0wLjdsLTQuNCwyLjVWMjVjMCwwLjgtMC43LDEuNS0xLjUsMS41aC0wLjVsMC4zLDAuNA0KCWMwLjUsMCwxLDAuMywxLjMsMC43bDEuMSwxLjZsMS4xLTAuN2MwLjItMC4xLDAuNC0wLjIsMC42LTAuMmMwLjMsMCwwLjYsMC4yLDAuOCwwLjRsMC4zLDAuNGMwLjIsMC4zLDAuMSwwLjgtMC4yLDFsLTcuNCw1LjINCglDMjYuNiwzNS4zLDI2LjUsMzUuMywyNi40LDM1LjN6Ii8+DQo8ZyBpZD0iWE1MSURfMTI3OThfIj4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NTdfIiBjbGFzcz0ic3QxIiBkPSJNMjksMTVIMTFWNi4zYzAtMC41LDAuNC0wLjksMC45LTAuOWgxNi4zYzAuNSwwLDAuOSwwLjQsMC45LDAuOVYxNXoiLz4NCgk8ZyBpZD0iWE1MSURfMTI3NTNfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNzU2XyIgeD0iMTMuNSIgeT0iMjQuNSIgY2xhc3M9InN0MiIgd2lkdGg9IjQuNSIgaGVpZ2h0PSI5LjkiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzU1XyIgY2xhc3M9InN0MyIgZD0iTTE4LjcsMzQuNGgtNS44di0zLjRjMC0wLjUsMC40LTAuOSwwLjktMC45aDMuOWMwLjUsMCwwLjksMC40LDAuOSwwLjlWMzQuNHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzU0XyIgY2xhc3M9InN0NCIgZD0iTTE5LjEsMzMuN2gtOC40Yy0wLjIsMC0wLjMsMC4xLTAuMywwLjN2MC40aDlWMzRDMTkuNSwzMy44LDE5LjMsMzMuNywxOS4xLDMzLjd6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8xMjc0OV8iPg0KCQkNCgkJCTxyZWN0IGlkPSJYTUxJRF8xMjc1Ml8iIHg9IjI0LjQiIHk9IjIzLjQiIHRyYW5zZm9ybT0ibWF0cml4KDAuODE5MiAtMC41NzM2IDAuNTczNiAwLjgxOTIgLTExLjQ0NzggMjAuMzkyNykiIGNsYXNzPSJzdDIiIHdpZHRoPSI0LjUiIGhlaWdodD0iOS45Ii8+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjc1MV8iIGNsYXNzPSJzdDMiIGQ9Ik0zMS44LDMwLjhsLTQuNywzLjNsLTEuOS0yLjhjLTAuMy0wLjQtMC4yLTEsMC4yLTEuM2wzLjItMi4yYzAuNC0wLjMsMS0wLjIsMS4zLDAuMg0KCQkJTDMxLjgsMzAuOHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyNzUwXyIgY2xhc3M9InN0NCIgZD0iTTMzLDI5bC02LjksNC44Yy0wLjEsMC4xLTAuMiwwLjMtMC4xLDAuNGwwLjMsMC40bDcuNC01LjJsLTAuMy0wLjQNCgkJCUMzMy40LDI4LjksMzMuMiwyOC45LDMzLDI5eiIvPg0KCTwvZz4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NDdfIiBjbGFzcz0ic3Q1IiBkPSJNNC4xLDExLjlsLTAuOSwxLjVjMC44LDAuNCwxLDEuNCwwLjYsMi4ycy0xLjQsMS0yLjIsMC42bC0wLjksMS41YzEuMywwLjcsMi45LDAuNSwzLjktMC40DQoJCWw2LjUsMy43YzAuNSwwLjMsMS4xLDAuMSwxLjQtMC40bDAsMGMwLjMtMC41LDAuMS0xLjEtMC40LTEuNGwtNi41LTMuN0M2LDE0LjEsNS4zLDEyLjcsNC4xLDExLjl6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyNzQ2XyIgY2xhc3M9InN0NSIgZD0iTTM5LjMsMTcuN2wtMC45LTEuNWMtMC44LDAuNC0xLjcsMC4yLTIuMi0wLjZjLTAuNC0wLjgtMC4yLTEuNywwLjYtMi4ybC0wLjktMS41DQoJCWMtMS4zLDAuNy0xLjksMi4yLTEuNiwzLjZsLTYuNSwzLjdjLTAuNSwwLjMtMC43LDAuOS0wLjQsMS40bDAsMGMwLjMsMC41LDAuOSwwLjcsMS40LDAuNGw2LjUtMy43QzM2LjQsMTguMywzOCwxOC41LDM5LjMsMTcuN3oiDQoJCS8+DQoJPGcgaWQ9IlhNTElEXzEyNTcyXyI+DQoJCTxnIGlkPSJYTUxJRF8xMjY1NF8iPg0KCQkJPHBhdGggaWQ9IlhNTElEXzEyNzQ0XyIgY2xhc3M9InN0NiIgZD0iTTE3LjksMTIuOGgtMy42Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJWOWMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMy42DQoJCQkJQzE3LjksOC45LDE4LDguOSwxOCw5djMuNkMxOCwxMi43LDE3LjksMTIuOCwxNy45LDEyLjh6Ii8+DQoJCTwvZz4NCgkJPGcgaWQ9IlhNTElEXzEyNTgxXyI+DQoJCQk8cGF0aCBpZD0iWE1MSURfMTI2NTFfIiBjbGFzcz0ic3Q2IiBkPSJNMjUuNywxMi44aC0zLjZjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMlY5YzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgzLjYNCgkJCQljMC4xLDAsMC4yLDAuMSwwLjIsMC4ydjMuNkMyNS45LDEyLjcsMjUuOCwxMi44LDI1LjcsMTIuOHoiLz4NCgkJPC9nPg0KCTwvZz4NCgk8cGF0aCBpZD0iWE1MSURfMTI3NDhfIiBjbGFzcz0ic3QzIiBkPSJNMjkuNCwyNS44SDEwLjZjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYtOS4yYzAtMC40LDAuNC0wLjgsMC44LTAuOGgxOC45DQoJCWMwLjQsMCwwLjgsMC40LDAuOCwwLjhWMjVDMzAuMiwyNS41LDI5LjksMjUuOCwyOS40LDI1Ljh6Ii8+DQo8L2c+DQo8L3N2Zz4NCg==';


class humanoidRobot {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'humanoidRobot';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'humanoidRobot';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const humanoidRobotState = sourceTarget.getCustomState(humanoidRobot.STATE_KEY);
            if (humanoidRobotState) {
                newTarget.setCustomState(humanoidRobot.STATE_KEY, Clone.simple(humanoidRobotState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        // const commonExtensions = () => [];
        let boardCommonExtensionList = [];
        // let commonExtensionList = commonExtensions();
        // for (let commonExtensionIndex in commonExtensionList) {
        //     let commonExtension = commonExtensionList[commonExtensionIndex];
        //     if (commonExtension.allowedBoards.includes(board)) {
        //         boardCommonExtensionList.push(commonExtension);
        //     }
        // }
        return boardCommonExtensionList;
    }

    get DIGITAL_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.digitalPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.digitalPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.digitalPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.digitalPins.evive;
            }
            case 'ESP32': {
                return ExtensionMenu.digitalPins.esp32;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'humanoidRobot',
            name: formatMessage({
                id: 'humanoidRobot.humanoidRobot',
                default: 'Humanoid Robot',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#0fBD8C',
            colourSecondary: '#0DA57A',
            colourTertiary: '#0B8E69',
            blocks: [
                {
                    opcode: 'uploadFirmware',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.uploadFirmware',
                        default: 'upload stage mode firmware',
                        description: 'upload firmware'
                    }),
                },
                '---',
                {
                    opcode: 'initialiseLeg',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.initialiseLeg',
                        default: 'connect left leg [HIP_L] right leg [HIP_R] left foot [FOOT_L] right foot [FOOT_R]',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        },
                        HIP_R: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        FOOT_L: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '4'
                        },
                        FOOT_R: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        }
                    }
                },
                {
                    opcode: 'initialiseHand',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.initialiseHand',
                        default: 'connect left shoulder [HIP_L] right shoulder [HIP_R] left hand [FOOT_L] right hand [FOOT_R]',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        },
                        HIP_R: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '7'
                        },
                        FOOT_L: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '8'
                        },
                        FOOT_R: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '9'
                        }
                    }
                },
                '---',
                {
                    opcode: 'calibrateLeg',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.calibrateLeg',
                        default: 'calibrate left leg [HIP_L] right leg [HIP_R] left foot [FOOT_L] right foot [FOOT_R]',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        HIP_R: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FOOT_L: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FOOT_R: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'calibrateHand',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.calibrateHand',
                        default: 'calibrate left shoulder [HIP_L] right shoulder [HIP_R] left hand [FOOT_L] right hand [FOOT_R]',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        HIP_R: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FOOT_L: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        FOOT_R: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                '---',
                {
                    opcode: 'moveHumanoidRobot',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.moveHumanoidRobot',
                        default: 'move [ACTION] speed [SPEED] moving-size [SIZE]',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        ACTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'actions',
                            defaultValue: '1'
                        },
                        SPEED: {
                            type: ArgumentType.NUMBER,
                            menu: 'speeds',
                            defaultValue: '1000'
                        },
                        SIZE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '25'
                        }
                    }
                },
                {
                    opcode: 'moveHumanoidRobotHand',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.moveHumanoidRobotHand',
                        default: 'do hand action - [ACTION] with delay [SPEED] ms',
                        description: 'initialise HumanoidRobot Robot'
                    }),
                    arguments: {
                        ACTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'handAction',
                            defaultValue: '0'
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER200,
                            defaultValue: '50'
                        }
                    }
                },
                '---',
                {
                    opcode: 'moveLeg',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.moveLeg',
                        default: 'move left leg [HIP_L] right leg [HIP_R] left foot [FOOT_L] right foot [FOOT_R] in [DURATION] ms',
                        description: 'move leg'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        HIP_R: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        FOOT_L: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        FOOT_R: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        DURATION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '500'
                        }
                    }
                },
                {
                    opcode: 'moveHand',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.moveHand',
                        default: 'move left shoulder [HIP_L] right shoulder [HIP_R] left hand [FOOT_L] right hand [FOOT_R] in [DURATION] ms',
                        description: 'move hand'
                    }),
                    arguments: {
                        HIP_L: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        HIP_R: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        FOOT_L: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        FOOT_R: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        DURATION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '500'
                        }
                    }
                },
                {
                    opcode: 'setServoAngle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.setServoAngle',
                        default: 'set [POSITION] servo angle to [ANGLE] degrees [CALIBRATIONS] calibration',
                        description: 'set servo angle'
                    }),
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoPosition',
                            defaultValue: '0'
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        CALIBRATIONS: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoCalib',
                            defaultValue: '1'
                        }
                    }
                },
                '---',
                {
                    opcode: 'initializeDotMatrixDisplayLeft',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.initializeDotMatrixDisplayLeft',
                        default: 'intialise [SIDE] eye to DIN [DINPIN] CS [CSPIN] CLK [CLKPIN]',
                        description: 'Intialise left Dot Matrix Display Module'
                    }),
                    arguments: {
                        SIDE: {
                            type: ArgumentType.NUMBER,
                            menu: 'side',
                            defaultValue: '1'
                        },
                        DINPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '12'
                        },
                        CSPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '10'
                        },
                        CLKPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '11'
                        }
                    }
                },
                {
                    opcode: 'displayMatrix1',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'humanoidRobot.displayMatrix1',
                        default: 'display left eye [MATRIXL] right eye [MATRIXR]',
                        description: 'display pattern on dot matrix'
                    }),
                    arguments: {
                        MATRIXL: {
                            type: ArgumentType.MATRIXLEFT,
                            defaultValue: '0000000000011000001111000011110000111100001111000001100000000000'
                        },
                        MATRIXR: {
                            type: ArgumentType.MATRIXRIGHT,
                            defaultValue: '0000000000011000001111000011110000111100001111000001100000000000'
                        }
                    }
                },
            ],
            menus: {
                digitalPins: this.DIGITAL_PINS,
                actions: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option1',
                            default: 'home',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option2',
                            default: 'forward',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option3',
                            default: 'backward',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option4',
                            default: 'turn left',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option5',
                            default: 'turn right',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option6',
                            default: 'updown',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option7',
                            default: 'moonwalker left',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option8',
                            default: 'moonwalker right',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option9',
                            default: 'swing',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option10',
                            default: 'crusaito 1',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option11',
                            default: 'crusaito 2',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option12',
                            default: 'flapping 1',
                            description: 'Menu'
                        }), value: '12'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option13',
                            default: 'flapping 2',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option14',
                            default: 'tip toe swing',
                            description: 'Menu'
                        }), value: '14'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option15',
                            default: 'bend left',
                            description: 'Menu'
                        }), value: '15'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option16',
                            default: 'bend right',
                            description: 'Menu'
                        }), value: '16'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option17',
                            default: 'shake leg right',
                            description: 'Menu'
                        }), value: '17'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option18',
                            default: 'shake leg left',
                            description: 'Menu'
                        }), value: '18'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.actions.option19',
                            default: 'jitter',
                            description: 'Menu'
                        }), value: '19'
                    }
                ],
                handAction: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option1',
                            default: 'hands forword',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option2',
                            default: 'hands down',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option3',
                            default: 'hands up',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option4',
                            default: 'hands straight',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option5',
                            default: 'fly',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option6',
                            default: 'left hand HI',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option7',
                            default: 'right hand HI',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option8',
                            default: 'left hand HI FIVE',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option9',
                            default: 'right hand HI FIVE',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option10',
                            default: 'left handshake',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option11',
                            default: 'right handshake',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option12',
                            default: 'dance 1',
                            description: 'Menu'
                        }), value: '12'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option13',
                            default: 'dance 2',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option14',
                            default: 'dance 3',
                            description: 'Menu'
                        }), value: '14'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.handAction.option15',
                            default: 'dance 4',
                            description: 'Menu'
                        }), value: '15'
                    },
                ],
                speeds: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.speeds.option1',
                            default: 'very fast',
                            description: 'Menu'
                        }), value: '500'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.speeds.option2',
                            default: 'fast',
                            description: 'Menu'
                        }), value: '800'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.speeds.option3',
                            default: 'normal',
                            description: 'Menu'
                        }), value: '1000'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.speeds.option4',
                            default: 'slow',
                            description: 'Menu'
                        }), value: '1300'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.speeds.option5',
                            default: 'very slow',
                            description: 'Menu'
                        }), value: '1700'
                    }
                ],
                gestures: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option1',
                            default: 'happy',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option2',
                            default: 'supperhappy',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option3',
                            default: 'sad',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option4',
                            default: 'sleeping',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option5',
                            default: 'fart',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option6',
                            default: 'confused',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option7',
                            default: 'love',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option8',
                            default: 'angry',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option9',
                            default: 'fretful',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option10',
                            default: 'magic',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option11',
                            default: 'wave',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option12',
                            default: 'victory',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.gestures.option13',
                            default: 'fail',
                            description: 'Menu'
                        }), value: '12'
                    }
                ],
                songs: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option1',
                            default: 'connection',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option2',
                            default: 'disconnection',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option3',
                            default: 'OhOoh',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option4',
                            default: 'OhOoh 2',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option5',
                            default: 'cuddly',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option6',
                            default: 'sleeping',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option7',
                            default: 'happy',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option8',
                            default: 'super happy',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option9',
                            default: 'happy short',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option10',
                            default: 'sad',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option11',
                            default: 'confused',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option12',
                            default: 'fart 1',
                            description: 'Menu'
                        }), value: '12'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option13',
                            default: 'fart 2',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.songs.option14',
                            default: 'fart 3',
                            description: 'Menu'
                        }), value: '14'
                    }
                ],
                notes: [
                    { text: 'C2', value: '65' },
                    { text: 'D2', value: '73' },
                    { text: 'E2', value: '82' },
                    { text: 'F2', value: '87' },
                    { text: 'G2', value: '98' },
                    { text: 'A2', value: '110' },
                    { text: 'B2', value: '123' },
                    { text: 'C3', value: '131' },
                    { text: 'D3', value: '147' },
                    { text: 'E3', value: '165' },
                    { text: 'F3', value: '175' },
                    { text: 'G3', value: '196' },
                    { text: 'A3', value: '220' },
                    { text: 'B3', value: '247' },
                    { text: 'C4', value: '262' },
                    { text: 'D4', value: '294' },
                    { text: 'E4', value: '330' },
                    { text: 'F4', value: '349' },
                    { text: 'G4', value: '392' },
                    { text: 'A4', value: '440' },
                    { text: 'B4', value: '494' },
                    { text: 'C5', value: '523' },
                    { text: 'D5', value: '587' },
                    { text: 'E5', value: '659' },
                    { text: 'F5', value: '698' },
                    { text: 'G5', value: '784' },
                    { text: 'A5', value: '880' },
                    { text: 'B5', value: '988' },
                    { text: 'C6', value: '1047' },
                    { text: 'D6', value: '1175' },
                    { text: 'E6', value: '1319' },
                    { text: 'F6', value: '1397' },
                    { text: 'G6', value: '1568' },
                    { text: 'A6', value: '1760' },
                    { text: 'B6', value: '1976' },
                    { text: 'C7', value: '2093' },
                    { text: 'D7', value: '2349' },
                    { text: 'E7', value: '2637' },
                    { text: 'F7', value: '2794' },
                    { text: 'G7', value: '3136' },
                    { text: 'A7', value: '3520' },
                    { text: 'B7', value: '3951' },
                    { text: 'C8', value: '4186' },
                    { text: 'D8', value: '4699' }
                ],
                beatsList: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option1',
                            default: 'Half',
                            description: 'Menu'
                        }), value: '500'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option2',
                            default: 'Quarter',
                            description: 'Menu'
                        }), value: '250'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option3',
                            default: 'Eighth',
                            description: 'Menu'
                        }), value: '125'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option4',
                            default: 'Whole',
                            description: 'Menu'
                        }), value: '1000'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option5',
                            default: 'Double',
                            description: 'Menu'
                        }), value: '2000'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.beatsList.option6',
                            default: 'Zero',
                            description: 'Menu'
                        }), value: '0'
                    }
                ],
                side: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.side.option1',
                            default: 'left',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.side.option2',
                            default: 'right',
                            description: 'Menu'
                        }), value: '2'
                    },
                ],
                servoPosition: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option1',
                            default: 'left leg',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option2',
                            default: 'right leg',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option3',
                            default: 'left foot',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option4',
                            default: 'right foot',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option5',
                            default: 'left shoulder',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option6',
                            default: 'right shoulder',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option7',
                            default: 'left hand',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.humanoidRobots.servoPosition.option8',
                            default: 'right hand',
                            description: 'Menu'
                        }), value: '7'
                    },
                ],
                servoCalib: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option1',
                            default: 'with',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option2',
                            default: 'without',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    uploadFirmware(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.uploadFirmware(args, util, this);
        }
        return RealtimeMode.uploadFirmware(args, util, this);
    }

    initialiseLeg(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseLeg(args, util, this);
        }
        return RealtimeMode.initialiseLeg(args, util, this);
    }

    initialiseHand(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseHand(args, util, this);
        }
        return RealtimeMode.initialiseHand(args, util, this);
    }

    calibrateLeg(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.calibrateLeg(args, util, this);
        }
        return RealtimeMode.calibrateLeg(args, util, this);
    }

    calibrateHand(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.calibrateHand(args, util, this);
        }
        return RealtimeMode.calibrateHand(args, util, this);
    }

    moveHumanoidRobot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveHumanoidRobot(args, util, this);
        }
        return RealtimeMode.moveHumanoidRobot(args, util, this);
    }

    gestureHumanoidRobot(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.gestureHumanoidRobot(args, util, this);
        }
        return RealtimeMode.gestureHumanoidRobot(args, util, this);
    }

    moveLeg(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveLeg(args, util, this);
        }
        return RealtimeMode.moveLeg(args, util, this);
    }

    moveHand(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveHand(args, util, this);
        }
        return RealtimeMode.moveHand(args, util, this);
    }

    initializeDotMatrixDisplayLeft(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initializeDotMatrixDisplayLeft(args, util, this);
        }
        return RealtimeMode.initializeDotMatrixDisplayLeft(args, util, this);
    }

    displayMatrix1(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayMatrix1(args, util, this);
        }
        return RealtimeMode.displayMatrix1(args, util, this);
    }

    setServoAngle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServoAngle(args, util, this);
        }
        return RealtimeMode.setServoAngle(args, util, this);
    }

    moveHumanoidRobotHand(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveHumanoidRobotHand(args, util, this);
        }
        return RealtimeMode.moveHumanoidRobotHand(args, util, this);
    }

}

module.exports = humanoidRobot;
