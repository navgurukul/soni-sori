const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require('format-message');
const Video = require('../../../io/video');
const StageLayering = require('../../../engine/stage-layering'); 
const jsQR = require('jsqr');

qrCode = null;

let isStage = false;
let stageWidth = 480;
let stageHeight = 360;   
let drawOnStage = false;

const blockIconURI = `data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtkaXNwbGF5Om5vbmU7fQ0KCS5zdDF7ZGlzcGxheTppbmxpbmU7fQ0KCS5zdDJ7ZmlsbDojRkZGRkZGO30NCgkuc3Qze2ZpbGw6bm9uZTtzdHJva2U6I0ZGRkZGRjtzdHJva2Utd2lkdGg6MS42O3N0cm9rZS1taXRlcmxpbWl0OjEwO30NCjwvc3R5bGU+DQo8ZyBjbGFzcz0ic3QwIj4NCgk8ZyBjbGFzcz0ic3QxIj4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTIuMiwxMC4ySDEuOGMtMC40LDAtMC43LTAuMy0wLjctMC43VjEuNmMwLTAuNCwwLjMtMC43LDAuNy0wLjdoMC40YzAuNCwwLDAuNywwLjMsMC43LDAuN3Y3LjkNCgkJCUMyLjksOS45LDIuNiwxMC4yLDIuMiwxMC4yeiIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTAuNCwxLjZ2MC40YzAsMC40LTAuMywwLjctMC43LDAuN0gxLjhjLTAuNCwwLTAuNy0wLjMtMC43LTAuN1YxLjZjMC0wLjQsMC4zLTAuNywwLjctMC43aDcuOQ0KCQkJQzEwLjEsMC45LDEwLjQsMS4yLDEwLjQsMS42eiIvPg0KCTwvZz4NCgk8ZyBjbGFzcz0ic3QxIj4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTM3LjgsMTAuMmgwLjRjMC40LDAsMC43LTAuMywwLjctMC43VjEuNmMwLTAuNC0wLjMtMC43LTAuNy0wLjdoLTAuNGMtMC40LDAtMC43LDAuMy0wLjcsMC43djcuOQ0KCQkJQzM3LjEsOS45LDM3LjQsMTAuMiwzNy44LDEwLjJ6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yOS42LDEuNnYwLjRjMCwwLjQsMC4zLDAuNywwLjcsMC43aDcuOWMwLjQsMCwwLjctMC4zLDAuNy0wLjdWMS42YzAtMC40LTAuMy0wLjctMC43LTAuN2gtNy45DQoJCQlDMjkuOSwwLjksMjkuNiwxLjIsMjkuNiwxLjZ6Ii8+DQoJPC9nPg0KPC9nPg0KPGcgY2xhc3M9InN0MCI+DQoJPGcgY2xhc3M9InN0MSI+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yLjIsMjkuOEgxLjhjLTAuNCwwLTAuNywwLjMtMC43LDAuN3Y3LjljMCwwLjQsMC4zLDAuNywwLjcsMC43aDAuNGMwLjQsMCwwLjctMC4zLDAuNy0wLjd2LTcuOQ0KCQkJQzIuOSwzMC4xLDIuNiwyOS44LDIuMiwyOS44eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTAuNCwzOC40di0wLjRjMC0wLjQtMC4zLTAuNy0wLjctMC43SDEuOGMtMC40LDAtMC43LDAuMy0wLjcsMC43djAuNGMwLDAuNCwwLjMsMC43LDAuNywwLjdoNy45DQoJCQlDMTAuMSwzOS4xLDEwLjQsMzguOCwxMC40LDM4LjR6Ii8+DQoJPC9nPg0KCTxnIGNsYXNzPSJzdDEiPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMzcuOCwyOS44aDAuNGMwLjQsMCwwLjcsMC4zLDAuNywwLjd2Ny45YzAsMC40LTAuMywwLjctMC43LDAuN2gtMC40Yy0wLjQsMC0wLjctMC4zLTAuNy0wLjd2LTcuOQ0KCQkJQzM3LjEsMzAuMSwzNy40LDI5LjgsMzcuOCwyOS44eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjkuNiwzOC40di0wLjRjMC0wLjQsMC4zLTAuNywwLjctMC43aDcuOWMwLjQsMCwwLjcsMC4zLDAuNywwLjd2MC40YzAsMC40LTAuMywwLjctMC43LDAuN2gtNy45DQoJCQlDMjkuOSwzOS4xLDI5LjYsMzguOCwyOS42LDM4LjR6Ii8+DQoJPC9nPg0KPC9nPg0KPGc+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0xMy42LDEzLjhINy4zYy0wLjUsMC0xLTAuNC0xLTFWNi41YzAtMC41LDAuNS0xLDEtMWg2LjNjMC42LDAsMSwwLjUsMSwxdjYuM0MxNC42LDEzLjQsMTQuMSwxMy44LDEzLjYsMTMuOA0KCQkJeiIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTEuMSwxMS4zSDkuOGMtMC41LDAtMS0wLjQtMS0xVjljMC0wLjUsMC40LTEsMS0xaDEuM2MwLjUsMCwxLDAuNCwxLDF2MS4zQzEyLjEsMTAuOSwxMS42LDExLjMsMTEuMSwxMS4zeiINCgkJCS8+DQoJPC9nPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QzIiBkPSJNMzIuNywxMy44aC02LjNjLTAuNSwwLTEtMC40LTEtMVY2LjVjMC0wLjUsMC41LTEsMS0xaDYuM2MwLjUsMCwxLDAuNSwxLDF2Ni4zDQoJCQlDMzMuNywxMy40LDMzLjMsMTMuOCwzMi43LDEzLjh6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0zMC4yLDExLjNoLTEuM2MtMC41LDAtMS0wLjQtMS0xVjljMC0wLjUsMC40LTEsMS0xaDEuM2MwLjUsMCwxLDAuNCwxLDF2MS4zQzMxLjIsMTAuOSwzMC44LDExLjMsMzAuMiwxMS4zeg0KCQkJIi8+DQoJPC9nPg0KPC9nPg0KPGc+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0xMy4xLDM0SDYuOGMtMC41LDAtMS0wLjUtMS0xdi02LjNjMC0wLjUsMC41LTEsMS0xaDYuM2MwLjYsMCwxLDAuNSwxLDFWMzNDMTQuMSwzMy41LDEzLjYsMzQsMTMuMSwzNHoiLz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTEwLjYsMzEuNEg5LjNjLTAuNSwwLTEtMC40LTEtMXYtMS4zYzAtMC41LDAuNC0xLDEtMWgxLjNjMC41LDAsMSwwLjQsMSwxdjEuM0MxMS42LDMxLDExLjEsMzEuNCwxMC42LDMxLjR6DQoJCQkiLz4NCgk8L2c+DQoJPGc+DQoJCTxwYXRoIGNsYXNzPSJzdDMiIGQ9Ik0zMi4yLDM0aC02LjNjLTAuNSwwLTEtMC41LTEtMXYtNi4zYzAtMC41LDAuNS0xLDEtMWg2LjNjMC42LDAsMSwwLjUsMSwxVjMzQzMzLjIsMzMuNSwzMi44LDM0LDMyLjIsMzR6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yOS43LDMxLjRoLTEuM2MtMC41LDAtMS0wLjQtMS0xdi0xLjNjMC0wLjUsMC40LTEsMS0xaDEuM2MwLjUsMCwxLDAuNCwxLDF2MS4zQzMwLjcsMzEsMzAuMywzMS40LDI5LjcsMzEuNA0KCQkJeiIvPg0KCTwvZz4NCjwvZz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0xNy41LDE0LjZMMTcuNSwxNC42Yy0wLjUsMC0xLTAuNC0xLTFWNS44YzAtMC41LDAuNC0xLDEtMWgwYzAuNSwwLDEsMC40LDEsMXY3LjgNCglDMTguNSwxNC4xLDE4LjEsMTQuNiwxNy41LDE0LjZ6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjIuOSwxNC42aC01LjVjLTAuNSwwLTAuOS0wLjQtMC45LTAuOXYwYzAtMC41LDAuNC0wLjksMC45LTAuOWg1LjVjMC41LDAsMC45LDAuNCwwLjksMC45djANCglDMjMuOCwxNC4yLDIzLjQsMTQuNiwyMi45LDE0LjZ6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjIuOSwxOC45TDIyLjksMTguOWMtMC41LDAtMC45LTAuNC0wLjktMC45di00LjRjMC0wLjUsMC40LTAuOSwwLjktMC45aDBjMC41LDAsMC45LDAuNCwwLjksMC45djQuNA0KCUMyMy44LDE4LjUsMjMuNCwxOC45LDIyLjksMTguOXoiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yNi44LDE4LjloLTRjLTAuNCwwLTAuOC0wLjMtMC44LTAuOHYtMC4xYzAtMC40LDAuMy0wLjgsMC44LTAuOGg0YzAuNCwwLDAuOCwwLjMsMC44LDAuOHYwLjENCglDMjcuNiwxOC42LDI3LjMsMTguOSwyNi44LDE4Ljl6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMzMuMywxOC45aC0zLjhjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYwYzAtMC40LDAuNC0wLjgsMC44LTAuOGgzLjhjMC40LDAsMC44LDAuNCwwLjgsMC44djANCglDMzQuMSwxOC42LDMzLjgsMTguOSwzMy4zLDE4Ljl6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTkuNCwxOC45aC0yLjdjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYwYzAtMC40LDAuNC0wLjgsMC44LTAuOGgyLjdjMC40LDAsMC44LDAuNCwwLjgsMC44djANCglDMjAuMiwxOC42LDE5LjgsMTguOSwxOS40LDE4Ljl6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTMuOCwxOC45SDcuMWMtMC40LDAtMC44LTAuNC0wLjgtMC44djBjMC0wLjQsMC40LTAuOCwwLjgtMC44aDYuN2MwLjQsMCwwLjgsMC40LDAuOCwwLjh2MA0KCUMxNC42LDE4LjYsMTQuMiwxOC45LDEzLjgsMTguOXoiLz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yMi43LDExLjdoLTIuMmMtMC40LDAtMC44LTAuNC0wLjgtMC44di0wLjFjMC0wLjQsMC40LTAuOCwwLjgtMC44aDIuMmMwLjQsMCwwLjgsMC40LDAuOCwwLjh2MC4xDQoJCUMyMy41LDExLjQsMjMuMSwxMS43LDIyLjcsMTEuN3oiLz4NCgk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjEuOCwxMC45VjUuNmMwLTAuNCwwLjQtMC44LDAuOC0wLjhoMC4xYzAuNCwwLDAuOCwwLjQsMC44LDAuOHY1LjNjMCwwLjQtMC40LDAuOC0wLjgsMC44aC0wLjENCgkJQzIyLjIsMTEuNywyMS44LDExLjQsMjEuOCwxMC45eiIvPg0KPC9nPg0KPGc+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTIyLjEsMzQuN2gtMi4yYy0wLjQsMC0wLjgtMC40LTAuOC0wLjh2LTAuMWMwLTAuNCwwLjQtMC44LDAuOC0wLjhoMi4yYzAuNCwwLDAuOCwwLjQsMC44LDAuOHYwLjENCgkJQzIyLjksMzQuMywyMi42LDM0LjcsMjIuMSwzNC43eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yMS4yLDMzLjl2LThjMC0wLjQsMC40LTAuOCwwLjgtMC44aDAuMWMwLjQsMCwwLjgsMC40LDAuOCwwLjh2OGMwLDAuNC0wLjQsMC44LTAuOCwwLjhIMjINCgkJQzIxLjYsMzQuNywyMS4yLDM0LjMsMjEuMiwzMy45eiIvPg0KPC9nPg0KPGc+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTE2LjksMjguOGwyLjIsMGMwLjQsMCwwLjgsMC40LDAuOCwwLjhsMCwwLjFjMCwwLjQtMC40LDAuOC0wLjgsMC44bC0yLjIsMGMtMC40LDAtMC44LTAuNC0wLjgtMC44bDAtMC4xDQoJCUMxNi4xLDI5LjIsMTYuNSwyOC44LDE2LjksMjguOHoiLz4NCgk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTcuOCwyOS42bDAsNC4xYzAsMC40LTAuNCwwLjgtMC44LDAuOGwtMC4xLDBjLTAuNCwwLTAuOC0wLjQtMC44LTAuOGwwLTQuMWMwLTAuNCwwLjQtMC44LDAuOC0wLjhsMC4xLDANCgkJQzE3LjQsMjguOCwxNy44LDI5LjIsMTcuOCwyOS42eiIvPg0KPC9nPg0KPGc+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTIwLjEsMjEuOUwyMCwyNi41YzAsMC40LTAuNCwwLjgtMC44LDAuOGwtMC4xLDBjLTAuNCwwLTAuOC0wLjQtMC44LTAuOGwwLjEtNC43YzAtMC40LDAuNC0wLjgsMC44LTAuOGwwLjEsMA0KCQlDMTkuNywyMSwyMC4xLDIxLjQsMjAuMSwyMS45eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0xOS4zLDIyLjdIOS44Yy0wLjQsMC0wLjgtMC40LTAuOC0wLjh2LTAuMUM5LDIxLjQsOS4zLDIxLDkuOCwyMWg5LjVjMC40LDAsMC44LDAuNCwwLjgsMC44djAuMQ0KCQlDMjAuMSwyMi4zLDE5LjcsMjIuNywxOS4zLDIyLjd6Ii8+DQoJPHBhdGggY2xhc3M9InN0MiIgZD0iTTMzLjQsMjIuN2gtNy41Yy0wLjQsMC0wLjgtMC40LTAuOC0wLjh2LTAuMWMwLTAuNCwwLjQtMC44LDAuOC0wLjhoNy41YzAuNCwwLDAuOCwwLjQsMC44LDAuOHYwLjENCgkJQzM0LjIsMjIuMywzMy45LDIyLjcsMzMuNCwyMi43eiIvPg0KPC9nPg0KPHBhdGggY2xhc3M9InN0MiIgZD0iTTIyLjksMjIuOEwyMi45LDIyLjhjLTAuNSwwLTAuOS0wLjQtMC45LTAuOXYwYzAtMC41LDAuNC0wLjksMC45LTAuOWgwYzAuNSwwLDAuOSwwLjQsMC45LDAuOXYwDQoJQzIzLjgsMjIuNCwyMy40LDIyLjgsMjIuOSwyMi44eiIvPg0KPHBhdGggY2xhc3M9InN0MiIgZD0iTTYuOSwyMi44TDYuOSwyMi44Yy0wLjUsMC0wLjktMC40LTAuOS0wLjl2MGMwLTAuNSwwLjQtMC45LDAuOS0wLjloMGMwLjUsMCwwLjksMC40LDAuOSwwLjl2MA0KCUM3LjgsMjIuNCw3LjQsMjIuOCw2LjksMjIuOHoiLz4NCjwvc3ZnPg0K`;

const menuIconURI = `data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtkaXNwbGF5Om5vbmU7fQ0KCS5zdDF7ZGlzcGxheTppbmxpbmU7fQ0KCS5zdDJ7ZmlsbDojRkZGRkZGO30NCgkuc3Qze2ZpbGw6bm9uZTtzdHJva2U6IzY2NjY2NjtzdHJva2Utd2lkdGg6MS42O3N0cm9rZS1taXRlcmxpbWl0OjEwO30NCgkuc3Q0e2ZpbGw6IzY2NjY2Njt9DQo8L3N0eWxlPg0KPGcgY2xhc3M9InN0MCI+DQoJPGcgY2xhc3M9InN0MSI+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yLjIsMTAuMkgxLjhjLTAuNCwwLTAuNy0wLjMtMC43LTAuN1YxLjZjMC0wLjQsMC4zLTAuNywwLjctMC43aDAuNGMwLjQsMCwwLjcsMC4zLDAuNywwLjd2Ny45DQoJCQlDMi45LDkuOSwyLjYsMTAuMiwyLjIsMTAuMnoiLz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTEwLjQsMS42djAuNGMwLDAuNC0wLjMsMC43LTAuNywwLjdIMS44Yy0wLjQsMC0wLjctMC4zLTAuNy0wLjdWMS42YzAtMC40LDAuMy0wLjcsMC43LTAuN2g3LjkNCgkJCUMxMC4xLDAuOSwxMC40LDEuMiwxMC40LDEuNnoiLz4NCgk8L2c+DQoJPGcgY2xhc3M9InN0MSI+DQoJCTxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0zNy44LDEwLjJoMC40YzAuNCwwLDAuNy0wLjMsMC43LTAuN1YxLjZjMC0wLjQtMC4zLTAuNy0wLjctMC43aC0wLjRjLTAuNCwwLTAuNywwLjMtMC43LDAuN3Y3LjkNCgkJCUMzNy4xLDkuOSwzNy40LDEwLjIsMzcuOCwxMC4yeiIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjkuNiwxLjZ2MC40YzAsMC40LDAuMywwLjcsMC43LDAuN2g3LjljMC40LDAsMC43LTAuMywwLjctMC43VjEuNmMwLTAuNC0wLjMtMC43LTAuNy0wLjdoLTcuOQ0KCQkJQzI5LjksMC45LDI5LjYsMS4yLDI5LjYsMS42eiIvPg0KCTwvZz4NCjwvZz4NCjxnIGNsYXNzPSJzdDAiPg0KCTxnIGNsYXNzPSJzdDEiPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMi4yLDI5LjhIMS44Yy0wLjQsMC0wLjcsMC4zLTAuNywwLjd2Ny45YzAsMC40LDAuMywwLjcsMC43LDAuN2gwLjRjMC40LDAsMC43LTAuMywwLjctMC43di03LjkNCgkJCUMyLjksMzAuMSwyLjYsMjkuOCwyLjIsMjkuOHoiLz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTEwLjQsMzguNHYtMC40YzAtMC40LTAuMy0wLjctMC43LTAuN0gxLjhjLTAuNCwwLTAuNywwLjMtMC43LDAuN3YwLjRjMCwwLjQsMC4zLDAuNywwLjcsMC43aDcuOQ0KCQkJQzEwLjEsMzkuMSwxMC40LDM4LjgsMTAuNCwzOC40eiIvPg0KCTwvZz4NCgk8ZyBjbGFzcz0ic3QxIj4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTM3LjgsMjkuOGgwLjRjMC40LDAsMC43LDAuMywwLjcsMC43djcuOWMwLDAuNC0wLjMsMC43LTAuNywwLjdoLTAuNGMtMC40LDAtMC43LTAuMy0wLjctMC43di03LjkNCgkJCUMzNy4xLDMwLjEsMzcuNCwyOS44LDM3LjgsMjkuOHoiLz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTI5LjYsMzguNHYtMC40YzAtMC40LDAuMy0wLjcsMC43LTAuN2g3LjljMC40LDAsMC43LDAuMywwLjcsMC43djAuNGMwLDAuNC0wLjMsMC43LTAuNywwLjdoLTcuOQ0KCQkJQzI5LjksMzkuMSwyOS42LDM4LjgsMjkuNiwzOC40eiIvPg0KCTwvZz4NCjwvZz4NCjxnPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QzIiBkPSJNMTMuNiwxMy44SDcuM2MtMC41LDAtMS0wLjQtMS0xVjYuNWMwLTAuNSwwLjUtMSwxLTFoNi4zYzAuNiwwLDEsMC41LDEsMXY2LjNDMTQuNiwxMy40LDE0LjEsMTMuOCwxMy42LDEzLjgNCgkJCXoiLz4NCgkJPHBhdGggY2xhc3M9InN0NCIgZD0iTTExLjEsMTEuM0g5LjhjLTAuNSwwLTEtMC40LTEtMVY5YzAtMC41LDAuNC0xLDEtMWgxLjNjMC41LDAsMSwwLjQsMSwxdjEuM0MxMi4xLDEwLjksMTEuNiwxMS4zLDExLjEsMTEuM3oiDQoJCQkvPg0KCTwvZz4NCgk8Zz4NCgkJPHBhdGggY2xhc3M9InN0MyIgZD0iTTMyLjcsMTMuOGgtNi4zYy0wLjUsMC0xLTAuNC0xLTFWNi41YzAtMC41LDAuNS0xLDEtMWg2LjNjMC41LDAsMSwwLjUsMSwxdjYuMw0KCQkJQzMzLjcsMTMuNCwzMy4zLDEzLjgsMzIuNywxMy44eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMzAuMiwxMS4zaC0xLjNjLTAuNSwwLTEtMC40LTEtMVY5YzAtMC41LDAuNC0xLDEtMWgxLjNjMC41LDAsMSwwLjQsMSwxdjEuM0MzMS4yLDEwLjksMzAuOCwxMS4zLDMwLjIsMTEuM3oNCgkJCSIvPg0KCTwvZz4NCjwvZz4NCjxnPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QzIiBkPSJNMTMuMSwzNEg2LjhjLTAuNSwwLTEtMC41LTEtMXYtNi4zYzAtMC41LDAuNS0xLDEtMWg2LjNjMC42LDAsMSwwLjUsMSwxVjMzQzE0LjEsMzMuNSwxMy42LDM0LDEzLjEsMzR6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMC42LDMxLjRIOS4zYy0wLjUsMC0xLTAuNC0xLTF2LTEuM2MwLTAuNSwwLjQtMSwxLTFoMS4zYzAuNSwwLDEsMC40LDEsMXYxLjNDMTEuNiwzMSwxMS4xLDMxLjQsMTAuNiwzMS40eg0KCQkJIi8+DQoJPC9nPg0KCTxnPg0KCQk8cGF0aCBjbGFzcz0ic3QzIiBkPSJNMzIuMiwzNGgtNi4zYy0wLjUsMC0xLTAuNS0xLTF2LTYuM2MwLTAuNSwwLjUtMSwxLTFoNi4zYzAuNiwwLDEsMC41LDEsMVYzM0MzMy4yLDMzLjUsMzIuOCwzNCwzMi4yLDM0eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMjkuNywzMS40aC0xLjNjLTAuNSwwLTEtMC40LTEtMXYtMS4zYzAtMC41LDAuNC0xLDEtMWgxLjNjMC41LDAsMSwwLjQsMSwxdjEuM0MzMC43LDMxLDMwLjMsMzEuNCwyOS43LDMxLjQNCgkJCXoiLz4NCgk8L2c+DQo8L2c+DQo8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMTcuNSwxNC42TDE3LjUsMTQuNmMtMC41LDAtMS0wLjQtMS0xVjUuOGMwLTAuNSwwLjQtMSwxLTFoMGMwLjUsMCwxLDAuNCwxLDF2Ny44DQoJQzE4LjUsMTQuMSwxOC4xLDE0LjYsMTcuNSwxNC42eiIvPg0KPHBhdGggY2xhc3M9InN0NCIgZD0iTTIyLjksMTQuNmgtNS41Yy0wLjUsMC0wLjktMC40LTAuOS0wLjl2MGMwLTAuNSwwLjQtMC45LDAuOS0wLjloNS41YzAuNSwwLDAuOSwwLjQsMC45LDAuOXYwDQoJQzIzLjgsMTQuMiwyMy40LDE0LjYsMjIuOSwxNC42eiIvPg0KPHBhdGggY2xhc3M9InN0NCIgZD0iTTIyLjksMTguOUwyMi45LDE4LjljLTAuNSwwLTAuOS0wLjQtMC45LTAuOXYtNC40YzAtMC41LDAuNC0wLjksMC45LTAuOWgwYzAuNSwwLDAuOSwwLjQsMC45LDAuOXY0LjQNCglDMjMuOCwxOC41LDIzLjQsMTguOSwyMi45LDE4Ljl6Ii8+DQo8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMjYuOCwxOC45aC00Yy0wLjQsMC0wLjgtMC4zLTAuOC0wLjh2LTAuMWMwLTAuNCwwLjMtMC44LDAuOC0wLjhoNGMwLjQsMCwwLjgsMC4zLDAuOCwwLjh2MC4xDQoJQzI3LjYsMTguNiwyNy4zLDE4LjksMjYuOCwxOC45eiIvPg0KPHBhdGggY2xhc3M9InN0NCIgZD0iTTMzLjMsMTguOWgtMy44Yy0wLjQsMC0wLjgtMC40LTAuOC0wLjh2MGMwLTAuNCwwLjQtMC44LDAuOC0wLjhoMy44YzAuNCwwLDAuOCwwLjQsMC44LDAuOHYwDQoJQzM0LjEsMTguNiwzMy44LDE4LjksMzMuMywxOC45eiIvPg0KPHBhdGggY2xhc3M9InN0NCIgZD0iTTE5LjQsMTguOWgtMi43Yy0wLjQsMC0wLjgtMC40LTAuOC0wLjh2MGMwLTAuNCwwLjQtMC44LDAuOC0wLjhoMi43YzAuNCwwLDAuOCwwLjQsMC44LDAuOHYwDQoJQzIwLjIsMTguNiwxOS44LDE4LjksMTkuNCwxOC45eiIvPg0KPHBhdGggY2xhc3M9InN0NCIgZD0iTTEzLjgsMTguOUg3LjFjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYwYzAtMC40LDAuNC0wLjgsMC44LTAuOGg2LjdjMC40LDAsMC44LDAuNCwwLjgsMC44djANCglDMTQuNiwxOC42LDE0LjIsMTguOSwxMy44LDE4Ljl6Ii8+DQo8Zz4NCgk8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMjIuNywxMS43aC0yLjJjLTAuNCwwLTAuOC0wLjQtMC44LTAuOHYtMC4xYzAtMC40LDAuNC0wLjgsMC44LTAuOGgyLjJjMC40LDAsMC44LDAuNCwwLjgsMC44djAuMQ0KCQlDMjMuNSwxMS40LDIzLjEsMTEuNywyMi43LDExLjd6Ii8+DQoJPHBhdGggY2xhc3M9InN0NCIgZD0iTTIxLjgsMTAuOVY1LjZjMC0wLjQsMC40LTAuOCwwLjgtMC44aDAuMWMwLjQsMCwwLjgsMC40LDAuOCwwLjh2NS4zYzAsMC40LTAuNCwwLjgtMC44LDAuOGgtMC4xDQoJCUMyMi4yLDExLjcsMjEuOCwxMS40LDIxLjgsMTAuOXoiLz4NCjwvZz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yMi4xLDM0LjdoLTIuMmMtMC40LDAtMC44LTAuNC0wLjgtMC44di0wLjFjMC0wLjQsMC40LTAuOCwwLjgtMC44aDIuMmMwLjQsMCwwLjgsMC40LDAuOCwwLjh2MC4xDQoJCUMyMi45LDM0LjMsMjIuNiwzNC43LDIyLjEsMzQuN3oiLz4NCgk8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMjEuMiwzMy45di04YzAtMC40LDAuNC0wLjgsMC44LTAuOGgwLjFjMC40LDAsMC44LDAuNCwwLjgsMC44djhjMCwwLjQtMC40LDAuOC0wLjgsMC44SDIyDQoJCUMyMS42LDM0LjcsMjEuMiwzNC4zLDIxLjIsMzMuOXoiLz4NCjwvZz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNi45LDI4LjhsMi4yLDBjMC40LDAsMC44LDAuNCwwLjgsMC44bDAsMC4xYzAsMC40LTAuNCwwLjgtMC44LDAuOGwtMi4yLDBjLTAuNCwwLTAuOC0wLjQtMC44LTAuOGwwLTAuMQ0KCQlDMTYuMSwyOS4yLDE2LjUsMjguOCwxNi45LDI4Ljh6Ii8+DQoJPHBhdGggY2xhc3M9InN0NCIgZD0iTTE3LjgsMjkuNmwwLDQuMWMwLDAuNC0wLjQsMC44LTAuOCwwLjhsLTAuMSwwYy0wLjQsMC0wLjgtMC40LTAuOC0wLjhsMC00LjFjMC0wLjQsMC40LTAuOCwwLjgtMC44bDAuMSwwDQoJCUMxNy40LDI4LjgsMTcuOCwyOS4yLDE3LjgsMjkuNnoiLz4NCjwvZz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yMC4xLDIxLjlMMjAsMjYuNWMwLDAuNC0wLjQsMC44LTAuOCwwLjhsLTAuMSwwYy0wLjQsMC0wLjgtMC40LTAuOC0wLjhsMC4xLTQuN2MwLTAuNCwwLjQtMC44LDAuOC0wLjhsMC4xLDANCgkJQzE5LjcsMjEsMjAuMSwyMS40LDIwLjEsMjEuOXoiLz4NCgk8cGF0aCBjbGFzcz0ic3Q0IiBkPSJNMTkuMywyMi43SDkuOGMtMC40LDAtMC44LTAuNC0wLjgtMC44di0wLjFDOSwyMS40LDkuMywyMSw5LjgsMjFoOS41YzAuNCwwLDAuOCwwLjQsMC44LDAuOHYwLjENCgkJQzIwLjEsMjIuMywxOS43LDIyLjcsMTkuMywyMi43eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0zMy40LDIyLjdoLTcuNWMtMC40LDAtMC44LTAuNC0wLjgtMC44di0wLjFjMC0wLjQsMC40LTAuOCwwLjgtMC44aDcuNWMwLjQsMCwwLjgsMC40LDAuOCwwLjh2MC4xDQoJCUMzNC4yLDIyLjMsMzMuOSwyMi43LDMzLjQsMjIuN3oiLz4NCjwvZz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yMi45LDIyLjhMMjIuOSwyMi44Yy0wLjUsMC0wLjktMC40LTAuOS0wLjl2MGMwLTAuNSwwLjQtMC45LDAuOS0wLjloMGMwLjUsMCwwLjksMC40LDAuOSwwLjl2MA0KCUMyMy44LDIyLjQsMjMuNCwyMi44LDIyLjksMjIuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik02LjksMjIuOEw2LjksMjIuOGMtMC41LDAtMC45LTAuNC0wLjktMC45djBjMC0wLjUsMC40LTAuOSwwLjktMC45aDBjMC41LDAsMC45LDAuNCwwLjksMC45djANCglDNy44LDIyLjQsNy40LDIyLjgsNi45LDIyLjh6Ii8+DQo8L3N2Zz4NCg== `;

const MakerAttributes = {
    color4f: [0.9, 0.6, 0.2, 0.7],
    diameter: 5
};

class qrCodeScanner {
    constructor(runtime) {
        this.runtime = runtime;
        
        this._data = '';
        this._binaryData = null;

        this._canvas = document.querySelector('canvas');
        this._penSkinId = this.runtime.renderer.createPenSkin();
        const penDrawableId = this.runtime.renderer.createDrawable(StageLayering.IMAGE_LAYER);
        this.runtime.renderer.updateDrawableProperties(penDrawableId, {skinId: this._penSkinId});
    }

    static get DIMENSIONS() {
        return [1280, 720];
    }

    getInfo() {
        return {
            id: 'qrCodeScanner',
            name: 'QR Code',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'qrCodeScanner.toggleStageVideoFeed',
                        default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] % transparency',
                        description: 'toggle video feed on stage'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        VIDEO_STATE: {
                            type: ArgumentType.STRING,
                            menu: 'videoState',
                            defaultValue: 'on'
                        },
                        TRANSPARENCY: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'drawBoundingBox',
                    text: formatMessage({
                        id: 'qrCodeScanner.drawBoundingBox',
                        default: '[OPTION] bounding box',
                        description: 'Draw bounding box'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'drawBox',
                            defaultValue: '1'
                        }
                    }
                },
                "---",
                {
                    opcode: 'analyseImage',
                    text: formatMessage({
                        id: 'qrCodeScanner.analyseImage',
                        default: 'analyse image for QR code from camera',
                        description: 'Analyse Image'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        FEED: {
                            type: ArgumentType.NUMBER,
                            menu: 'feed',
                            defaultValue: '1'
                        }
                    }
                },
                "---",
                {
                    opcode: 'isDetected',
                    text: formatMessage({
                        id: 'qrCodeScanner.isDetected',
                        default: 'is QR code detected?',
                        description: 'Is QR Code detected'
                    }),
                    blockType: BlockType.BOOLEAN,
                },
                {
                    opcode: 'getQRCodeData',
                    text: formatMessage({
                        id: 'qrCodeScanner.getQRCodeData',
                        default: 'get QR code data',
                        description: 'Get QR code data'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'getQRCodeLocation',
                    text: formatMessage({
                        id: 'qrCodeScanner.getQRCodeLocation',
                        default: '[OPTION] of [OBJECT]',
                        description: 'X Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        OBJECT: {
                            type: ArgumentType.NUMBER,
                            menu: 'feature',
                            defaultValue: '0'
                        },
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'objectOption',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'getAngle',
                    text: formatMessage({
                        id: 'qrCodeScanner.getAngle',
                        default: 'get angle',
                        description: 'Get angle'
                    }),
                    blockType: BlockType.REPORTER,
                },
            ],
            menus: {
                videoState: [
                    { text: 'off', value: 'off' },
                    { text: 'on', value: 'on' },
                    { text: 'on flipped', value: 'onFlipped' }
                ],
                feature: [
                    { text: 'center', value: '0' },
                    { text: 'top right corner', value: '1' },
                    { text: 'top left corner', value: '2' },
                    { text: 'bottom right corner', value: '3' },
                    { text: 'bottom left corner', value: '4' },
                    { text: 'top right finder pattern', value: '5' },
                    { text: 'top left finder pattern', value: '6' },
                    { text: 'bottom left finder pattern', value: '7' },
                ],
                objectOption: [
                    { text: 'x position', value: '1' },
                    { text: 'y position', value: '2' }
                ],
                drawBox: [
                    { text: 'show', value: '1' },
                    { text: 'hide', value: '2' }
                ],
                feed: {
                    items: [
                        { text: 'camera', value: '1' },
                        { text: 'stage', value: '2' },
                    ]
                }
            }
        };
    }

    toggleStageVideoFeed(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const state = args.VIDEO_STATE;
        this.globalVideoState = args.VIDEO_STATE;

        this.runtime.ioDevices.video.setPreviewGhost(args.TRANSPARENCY);

        if (state === 'off') {
            this.runtime.ioDevices.video.disableVideo();
        } else {
            this.runtime.ioDevices.video.enableVideo();
            // Mirror if state is ON. Do not mirror if state is ON_FLIPPED.
            this.runtime.ioDevices.video.mirror = state === 'on';
        }
    }

    analyseImage(args, util) {
        // if (!this.runtime.checkSessionExists(this.extensionName)) return;
        const translatePromise = new Promise(resolve => {
            const frame = this.runtime.ioDevices.video.getFrame({
                format: Video.FORMAT_IMAGE_DATA,
                dimensions: qrCodeScanner.DIMENSIONS
            }).data;
            if (frame === null) {
                qrCode = null;
                resolve('Camera not ready!');
                return 'Camera not ready!';
            }
            width = qrCodeScanner.DIMENSIONS[0];
            height = qrCodeScanner.DIMENSIONS[1];
            stageWidth = width;
            stageHeight = height;
            console.log(width)
            console.log(height)
            const code = jsQR(frame, width, height, {
                inversionAttempts: 'dontInvert',
            });
            console.log("Scanned");
            this._clearMark();
            if(code){
                qrCode = code;
                console.log(qrCode)
                if(drawOnStage){
                    this._drawMark(qrCode.location, width, height);
                }
            }
            else{
                qrCode = null;
            }
            resolve('Done');
            return 'Done';
        });
        return translatePromise;
    }

    _drawMark(location, width, height){
        let widthScale = 480 / width;
        let heightScale = 360 / height;

        let topLeftCornerX = location.topLeftCorner.x * widthScale - width / 2 * widthScale;
        let topRightCornerX = location.topRightCorner.x * widthScale - width / 2 * widthScale;
        let bottomRightCornerX = location.bottomRightCorner.x * widthScale - width / 2 * widthScale;
        let bottomLeftCornerX = location.bottomLeftCorner.x * widthScale - width / 2 * widthScale;

        let topLeftCornerY = height / 2 * heightScale - location.topLeftCorner.y * heightScale;
        let topRightCornerY = height / 2 * heightScale - location.topRightCorner.y * heightScale;
        let bottomRightCornerY = height / 2 * heightScale - location.bottomRightCorner.y * heightScale;
        let bottomLeftCornerY = height / 2 * heightScale - location.bottomLeftCorner.y * heightScale;

        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes, topLeftCornerX, topLeftCornerY, topRightCornerX, topRightCornerY);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes, topRightCornerX, topRightCornerY, bottomRightCornerX, bottomRightCornerY);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes, bottomLeftCornerX, bottomLeftCornerY, bottomRightCornerX, bottomRightCornerY);
        this.runtime.renderer.penLine(this._penSkinId, MakerAttributes, bottomLeftCornerX, bottomLeftCornerY, topLeftCornerX, topLeftCornerY);
    }

    _clearMark(){
        this.runtime.renderer.penClear(this._penSkinId);
    }

    isDetected(args, util) {
        console.log(qrCode)
        if(qrCode === null){
            return false;
        }
        return true;
    }

    getQRCodeData(args, util) {
        if(qrCode !== null){
            return qrCode.data;
        }
        return "";
    }

    drawBoundingBox(args, util) {
        if(args.OPTION === "1"){
            drawOnStage = true;
        }
        else{
            drawOnStage = false;
            this._clearMark();
        }
    }

    getQRCodeLocation(args, util) {
        if(qrCode !== null){
            let widthScale = 480 / stageWidth;
            let heightScale = 360 / stageHeight;
            let width = stageWidth;
            let height = stageHeight;

            let topLeftCornerX = qrCode.location.topLeftCorner.x * widthScale - width / 2 * widthScale;
            let topRightCornerX = qrCode.location.topRightCorner.x * widthScale - width / 2 * widthScale;
            let bottomRightCornerX = qrCode.location.bottomRightCorner.x * widthScale - width / 2 * widthScale;
            let bottomLeftCornerX = qrCode.location.bottomLeftCorner.x * widthScale - width / 2 * widthScale;

            let topLeftCornerY = height / 2 * heightScale - qrCode.location.topLeftCorner.y * heightScale;
            let topRightCornerY = height / 2 * heightScale - qrCode.location.topRightCorner.y * heightScale;
            let bottomRightCornerY = height / 2 * heightScale - qrCode.location.bottomRightCorner.y * heightScale;
            let bottomLeftCornerY = height / 2 * heightScale - qrCode.location.bottomLeftCorner.y * heightScale;

            let centerX = (topLeftCornerX + bottomRightCornerX)/2;
            let centerY = (topLeftCornerY + bottomRightCornerY)/2;

            let topRightFinderPatternX = qrCode.location.topRightFinderPattern.x * widthScale - width / 2 * widthScale;
            let bottolLeftFinderPatternX = qrCode.location.bottomLeftFinderPattern.x * widthScale - width / 2 * widthScale;
            let topLeftFinderPatternX = qrCode.location.topLeftFinderPattern.x * widthScale - width / 2 * widthScale;

            let topRightFinderPatternY = height / 2 * heightScale - qrCode.location.topRightFinderPattern.y * heightScale;
            let bottolLeftFinderPatternY = height / 2 * heightScale - qrCode.location.bottomLeftFinderPattern.y * heightScale;
            let topLeftFinderPatternY = height / 2 * heightScale - qrCode.location.topLeftFinderPattern.y * heightScale;

            if(args.OBJECT === "0"){
                if (args.OPTION === '1'){
                    return centerX.toFixed(1);
                }
                else{
                    return centerY.toFixed(1);
                }
            }
            else if(args.OBJECT === "1"){
                if (args.OPTION === '1'){
                    return topRightCornerX.toFixed(1);
                }
                else{
                    return topRightCornerY.toFixed(1);
                }
            }
            else if(args.OBJECT === "2"){
                if (args.OPTION === '1'){
                    return topLeftCornerX.toFixed(1);
                }
                else{
                    return topLeftCornerY.toFixed(1);
                }
            }
            else if(args.OBJECT === "3"){
                if (args.OPTION === '1'){
                    return bottomRightCornerX.toFixed(1);
                }
                else{
                    return bottomRightCornerY.toFixed(1);
                }
            }
            else if(args.OBJECT === "4"){
                if (args.OPTION === '1'){
                    return bottomLeftCornerX.toFixed(1);
                }
                else{
                    return bottomLeftCornerY.toFixed(1);
                }
            }
            else if(args.OBJECT === "5"){
                if (args.OPTION === '1'){
                    return topRightFinderPatternX.toFixed(1);
                }
                else{
                    return topRightFinderPatternY.toFixed(1);
                }
            }
            else if(args.OBJECT === "6"){
                if (args.OPTION === '1'){
                    return topLeftFinderPatternX.toFixed(1);
                }
                else{
                    return topLeftFinderPatternY.toFixed(1);
                }
            }
            else if(args.OBJECT === "7"){
                if (args.OPTION === '1'){
                    return bottolLeftFinderPatternX.toFixed(1);
                }
                else{
                    return bottolLeftFinderPatternY.toFixed(1);
                }
            }
        }
        return "";
    }

    getAngle(args, util) {
        if(qrCode !== null){
                let widthScale = 480 / stageWidth;
                let heightScale = 360 / stageHeight;
                let width = stageWidth;
                let height = stageHeight;
    
                let topRightFinderPatternX = qrCode.location.topRightFinderPattern.x * widthScale - width / 2 * widthScale;
                let bottolLeftFinderPatternX = qrCode.location.bottomLeftFinderPattern.x * widthScale - width / 2 * widthScale;
                let topLeftFinderPatternX = qrCode.location.topLeftFinderPattern.x * widthScale - width / 2 * widthScale;

                let topRightFinderPatternY = height / 2 * heightScale - qrCode.location.topRightFinderPattern.y * heightScale;
                let bottolLeftFinderPatternY = height / 2 * heightScale - qrCode.location.bottomLeftFinderPattern.y * heightScale;
                let topLeftFinderPatternY = height / 2 * heightScale - qrCode.location.topLeftFinderPattern.y * heightScale;

                console.log(bottolLeftFinderPatternX)
                console.log(topLeftFinderPatternX)
                console.log(bottolLeftFinderPatternY)
                console.log(this.runtime.ioDevices.video.mirror)

                let angle = 0;

                if(this.runtime.ioDevices.video.mirror){
                    angle = 90 - Math.atan2((topLeftFinderPatternY - topRightFinderPatternY), (topLeftFinderPatternX - topRightFinderPatternX))*180/Math.PI;
                }
                else{
                    angle = 90 - Math.atan2((topLeftFinderPatternY - bottolLeftFinderPatternY), (topLeftFinderPatternX - bottolLeftFinderPatternX))*180/Math.PI;
                }
                return angle.toFixed(1);
            }
        return "";
    }

}

module.exports = qrCodeScanner;