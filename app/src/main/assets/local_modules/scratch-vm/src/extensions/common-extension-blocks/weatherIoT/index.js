const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const BoardConfig = require('../../../util/board-config.js');
const getBoardId = BoardConfig.getBoardId;
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGOUUzMTU7fQ0KCS5zdDF7ZmlsbDojRjFGMkYyO30NCgkuc3Qye2ZpbGw6I0ZGRkZGRjt9DQoJLnN0M3tkaXNwbGF5Om5vbmU7fQ0KCS5zdDR7ZGlzcGxheTppbmxpbmU7fQ0KCS5zdDV7ZmlsbDojRDdFRkZBO30NCgkuc3Q2e2ZpbGw6I0YyNkU5MTt9DQoJLnN0N3tmaWxsOiNBMjlBQTU7fQ0KCS5zdDh7ZGlzcGxheTppbmxpbmU7ZmlsbDojRUY5MzZGO30NCjwvc3R5bGU+DQo8ZyBpZD0iaWNvbl8xXyI+DQoJPGNpcmNsZSBjbGFzcz0ic3QwIiBjeD0iMjkiIGN5PSIxNSIgcj0iNiIvPg0KCTxnPg0KCQk8Y2lyY2xlIGNsYXNzPSJzdDEiIGN4PSI5LjciIGN5PSIyMy45IiByPSI2LjIiLz4NCgkJPGNpcmNsZSBjbGFzcz0ic3QxIiBjeD0iMTguMyIgY3k9IjE2LjUiIHI9IjguNyIvPg0KCQk8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMzAuNywzMC4xSDkuN2MtMy40LDAtNi4yLTIuOC02LjItNi4ydjBjMC0zLjQsMi44LTYuMiw2LjItNi4yaDIxLjFjMy40LDAsNi4yLDIuOCw2LjIsNi4ydjANCgkJCUMzNywyNy4zLDM0LjIsMzAuMSwzMC43LDMwLjF6Ii8+DQoJPC9nPg0KCTxnPg0KCQk8Y2lyY2xlIGNsYXNzPSJzdDIiIGN4PSI4LjQiIGN5PSIyNS4yIiByPSI2LjIiLz4NCgkJPGNpcmNsZSBjbGFzcz0ic3QyIiBjeD0iMTcuMSIgY3k9IjE3LjciIHI9IjguNyIvPg0KCQk8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMjkuNSwzMS40SDguNGMtMy40LDAtNi4yLTIuOC02LjItNi4ydjBDMi4yLDIxLjcsNSwxOSw4LjQsMTloMjEuMWMzLjQsMCw2LjIsMi44LDYuMiw2LjJ2MA0KCQkJQzM1LjcsMjguNiwzMi45LDMxLjQsMjkuNSwzMS40eiIvPg0KCTwvZz4NCgkNCgkJPGltYWdlIHN0eWxlPSJkaXNwbGF5Om5vbmU7b3ZlcmZsb3c6dmlzaWJsZTsiIHdpZHRoPSI1MTIiIGhlaWdodD0iNTEyIiB4bGluazpocmVmPSJFOlxwbmdcdGhlcm1vbWV0ZXIucG5nIiAgdHJhbnNmb3JtPSJtYXRyaXgoMy4xNDQ5ODllLTAyIDAgMCAzLjE0NDk4OWUtMDIgMjIuMjExNyAxMS4zOTQxKSI+DQoJPC9pbWFnZT4NCgk8ZyBjbGFzcz0ic3QzIj4NCgkJPGcgY2xhc3M9InN0NCI+DQoJCQk8Y2lyY2xlIGNsYXNzPSJzdDUiIGN4PSIyOS43IiBjeT0iMjIuNyIgcj0iMy43Ii8+DQoJCQk8cGF0aCBjbGFzcz0ic3Q1IiBkPSJNMzAuMiwyMS43aC0wLjljLTAuOCwwLTEuNS0wLjctMS41LTEuNVY5LjNjMC0wLjgsMC43LTEuNSwxLjUtMS41aDAuOWMwLjgsMCwxLjUsMC43LDEuNSwxLjV2MTAuOQ0KCQkJCUMzMS43LDIxLDMxLDIxLjcsMzAuMiwyMS43eiIvPg0KCQk8L2c+DQoJCTxnIGNsYXNzPSJzdDQiPg0KCQkJPGNpcmNsZSBjbGFzcz0ic3Q2IiBjeD0iMjkuNyIgY3k9IjIyLjciIHI9IjIuOSIvPg0KCQkJPHBhdGggY2xhc3M9InN0NiIgZD0iTTMwLDIyaC0wLjZjLTAuNSwwLTAuOS0wLjQtMC45LTAuOVY5LjZjMC0wLjUsMC40LTAuOSwwLjktMC45SDMwYzAuNSwwLDAuOSwwLjQsMC45LDAuOXYxMS41DQoJCQkJQzMwLjksMjEuNiwzMC41LDIyLDMwLDIyeiIvPg0KCQkJPHBhdGggY2xhc3M9InN0NyIgZD0iTTMwLjUsMTEuOWgtMS42Yy0wLjIsMC0wLjQtMC4yLTAuNC0wLjRWOS42YzAtMC41LDAuNC0wLjksMC45LTAuOUgzMGMwLjUsMCwwLjksMC40LDAuOSwwLjl2MS45DQoJCQkJQzMwLjksMTEuNywzMC43LDExLjksMzAuNSwxMS45eiIvPg0KCQk8L2c+DQoJPC9nPg0KCTxnIGNsYXNzPSJzdDMiPg0KCQk8cGF0aCBjbGFzcz0ic3Q4IiBkPSJNMzAuNywxMy40aC0wLjRjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMmwwLDBjMC0wLjEsMC4xLTAuMiwwLjItMC4yaDAuNGMwLjEsMCwwLjIsMC4xLDAuMiwwLjJsMCwwDQoJCQlDMzAuOSwxMy4zLDMwLjgsMTMuNCwzMC43LDEzLjR6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDgiIGQ9Ik0zMC43LDE0LjRoLTAuNGMtMC4xLDAtMC4yLTAuMS0wLjItMC4ybDAsMGMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMC40YzAuMSwwLDAuMiwwLjEsMC4yLDAuMmwwLDANCgkJCUMzMC45LDE0LjMsMzAuOCwxNC40LDMwLjcsMTQuNHoiLz4NCgkJPHBhdGggY2xhc3M9InN0OCIgZD0iTTMwLjcsMTUuNGgtMC40Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJsMCwwYzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgwLjRjMC4xLDAsMC4yLDAuMSwwLjIsMC4ybDAsMA0KCQkJQzMwLjksMTUuMywzMC44LDE1LjQsMzAuNywxNS40eiIvPg0KCTwvZz4NCgk8ZyBjbGFzcz0ic3QzIj4NCgkJPHBhdGggY2xhc3M9InN0OCIgZD0iTTMwLjcsMTYuNGgtMC40Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJsMCwwYzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgwLjRjMC4xLDAsMC4yLDAuMSwwLjIsMC4ybDAsMA0KCQkJQzMwLjksMTYuMywzMC44LDE2LjQsMzAuNywxNi40eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3Q4IiBkPSJNMzAuNywxNy40aC0wLjRjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMmwwLDBjMC0wLjEsMC4xLTAuMiwwLjItMC4yaDAuNGMwLjEsMCwwLjIsMC4xLDAuMiwwLjJsMCwwDQoJCQlDMzAuOSwxNy4zLDMwLjgsMTcuNCwzMC43LDE3LjR6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDgiIGQ9Ik0zMC43LDE4LjRoLTAuNGMtMC4xLDAtMC4yLTAuMS0wLjItMC4ybDAsMGMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMC40YzAuMSwwLDAuMiwwLjEsMC4yLDAuMmwwLDANCgkJCUMzMC45LDE4LjMsMzAuOCwxOC40LDMwLjcsMTguNHoiLz4NCgk8L2c+DQo8L2c+DQo8Zz4NCgk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNMjUuNiw4LjJMMjUuNiw4LjJjLTAuMywwLjEtMC43LDAtMC44LTAuM2wtMC42LTEuMmMtMC4xLTAuMywwLTAuNywwLjMtMC44bDAsMGMwLjMtMC4xLDAuNywwLDAuOCwwLjMNCgkJbDAuNiwxLjJDMjYuMSw3LjcsMjUuOSw4LDI1LjYsOC4yeiIvPg0KCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0zMC4xLDcuOGwtMC4xLDBjLTAuMywwLTAuNi0wLjMtMC41LTAuN2wwLjEtMS40YzAtMC4zLDAuMy0wLjYsMC43LTAuNWwwLjEsMGMwLjMsMCwwLjYsMC4zLDAuNSwwLjdsLTAuMSwxLjQNCgkJQzMwLjgsNy42LDMwLjUsNy44LDMwLjEsNy44eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0zNC4zLDkuNEwzNC4zLDkuNGMtMC4yLTAuMi0wLjMtMC42LDAtMC45bDAuOS0xYzAuMi0wLjIsMC42LTAuMywwLjksMHYwYzAuMiwwLjIsMC4zLDAuNiwwLDAuOWwtMC45LDENCgkJQzM0LjksOS42LDM0LjUsOS42LDM0LjMsOS40eiIvPg0KCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0zNi4zLDEzTDM2LjMsMTNjMC0wLjMsMC4yLTAuNiwwLjUtMC42bDEuNC0wLjFjMC4zLDAsMC42LDAuMiwwLjYsMC41bDAsMGMwLDAuMy0wLjIsMC42LTAuNSwwLjZsLTEuNCwwLjENCgkJQzM2LjYsMTMuNiwzNi4zLDEzLjMsMzYuMywxM3oiLz4NCgk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNMzUuOSwxN0wzNS45LDE3YzAuMS0wLjMsMC41LTAuNSwwLjgtMC40bDEuMywwLjVjMC4zLDAuMSwwLjQsMC40LDAuMywwLjdsMCwwYy0wLjEsMC4zLTAuNCwwLjQtMC43LDAuMw0KCQlsLTEuMy0wLjVDMzUuOSwxNy42LDM1LjgsMTcuMywzNS45LDE3eiIvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = `data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4wLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNGOUUzMTU7fQ0KCS5zdDF7ZmlsbDojQjJCN0I3O30NCgkuc3Qye2ZpbGw6I0NDQ0NDQzt9DQoJLnN0M3tkaXNwbGF5Om5vbmU7fQ0KCS5zdDR7ZGlzcGxheTppbmxpbmU7fQ0KCS5zdDV7ZmlsbDojRDdFRkZBO30NCgkuc3Q2e2ZpbGw6I0YyNkU5MTt9DQoJLnN0N3tmaWxsOiNBMjlBQTU7fQ0KCS5zdDh7ZGlzcGxheTppbmxpbmU7ZmlsbDojRUY5MzZGO30NCjwvc3R5bGU+DQo8ZyBpZD0iaWNvbl8xXyI+DQoJPGNpcmNsZSBjbGFzcz0ic3QwIiBjeD0iMjkiIGN5PSIxNSIgcj0iNiIvPg0KCTxnPg0KCQk8Y2lyY2xlIGNsYXNzPSJzdDEiIGN4PSI5LjciIGN5PSIyMy45IiByPSI2LjIiLz4NCgkJPGNpcmNsZSBjbGFzcz0ic3QxIiBjeD0iMTguMyIgY3k9IjE2LjUiIHI9IjguNyIvPg0KCQk8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMzAuNywzMC4xaC0yMWMtMy40LDAtNi4yLTIuOC02LjItNi4ybDAsMGMwLTMuNCwyLjgtNi4yLDYuMi02LjJoMjEuMWMzLjQsMCw2LjIsMi44LDYuMiw2LjJsMCwwDQoJCQlDMzcsMjcuMywzNC4yLDMwLjEsMzAuNywzMC4xeiIvPg0KCTwvZz4NCgk8Zz4NCgkJPGNpcmNsZSBjbGFzcz0ic3QyIiBjeD0iOC40IiBjeT0iMjUuMiIgcj0iNi4yIi8+DQoJCTxjaXJjbGUgY2xhc3M9InN0MiIgY3g9IjE3LjEiIGN5PSIxNy43IiByPSI4LjciLz4NCgkJPHBhdGggY2xhc3M9InN0MiIgZD0iTTI5LjUsMzEuNEg4LjRjLTMuNCwwLTYuMi0yLjgtNi4yLTYuMmwwLDBDMi4yLDIxLjcsNSwxOSw4LjQsMTloMjEuMWMzLjQsMCw2LjIsMi44LDYuMiw2LjJsMCwwDQoJCQlDMzUuNywyOC42LDMyLjksMzEuNCwyOS41LDMxLjR6Ii8+DQoJPC9nPg0KCTxnIGNsYXNzPSJzdDMiPg0KCQk8ZyBjbGFzcz0ic3Q0Ij4NCgkJCTxjaXJjbGUgY2xhc3M9InN0NSIgY3g9IjI5LjciIGN5PSIyMi43IiByPSIzLjciLz4NCgkJCTxwYXRoIGNsYXNzPSJzdDUiIGQ9Ik0zMC4yLDIxLjdoLTAuOWMtMC44LDAtMS41LTAuNy0xLjUtMS41VjkuM2MwLTAuOCwwLjctMS41LDEuNS0xLjVoMC45YzAuOCwwLDEuNSwwLjcsMS41LDEuNXYxMC45DQoJCQkJQzMxLjcsMjEsMzEsMjEuNywzMC4yLDIxLjd6Ii8+DQoJCTwvZz4NCgkJPGcgY2xhc3M9InN0NCI+DQoJCQk8Y2lyY2xlIGNsYXNzPSJzdDYiIGN4PSIyOS43IiBjeT0iMjIuNyIgcj0iMi45Ii8+DQoJCQk8cGF0aCBjbGFzcz0ic3Q2IiBkPSJNMzAsMjJoLTAuNmMtMC41LDAtMC45LTAuNC0wLjktMC45VjkuNmMwLTAuNSwwLjQtMC45LDAuOS0wLjlIMzBjMC41LDAsMC45LDAuNCwwLjksMC45djExLjUNCgkJCQlDMzAuOSwyMS42LDMwLjUsMjIsMzAsMjJ6Ii8+DQoJCQk8cGF0aCBjbGFzcz0ic3Q3IiBkPSJNMzAuNSwxMS45aC0xLjZjLTAuMiwwLTAuNC0wLjItMC40LTAuNFY5LjZjMC0wLjUsMC40LTAuOSwwLjktMC45SDMwYzAuNSwwLDAuOSwwLjQsMC45LDAuOXYxLjkNCgkJCQlDMzAuOSwxMS43LDMwLjcsMTEuOSwzMC41LDExLjl6Ii8+DQoJCTwvZz4NCgk8L2c+DQoJPGcgY2xhc3M9InN0MyI+DQoJCTxwYXRoIGNsYXNzPSJzdDgiIGQ9Ik0zMC43LDEzLjRoLTAuNGMtMC4xLDAtMC4yLTAuMS0wLjItMC4ybDAsMGMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMC40YzAuMSwwLDAuMiwwLjEsMC4yLDAuMmwwLDANCgkJCUMzMC45LDEzLjMsMzAuOCwxMy40LDMwLjcsMTMuNHoiLz4NCgkJPHBhdGggY2xhc3M9InN0OCIgZD0iTTMwLjcsMTQuNGgtMC40Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJsMCwwYzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgwLjRjMC4xLDAsMC4yLDAuMSwwLjIsMC4ybDAsMA0KCQkJQzMwLjksMTQuMywzMC44LDE0LjQsMzAuNywxNC40eiIvPg0KCQk8cGF0aCBjbGFzcz0ic3Q4IiBkPSJNMzAuNywxNS40aC0wLjRjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMmwwLDBjMC0wLjEsMC4xLTAuMiwwLjItMC4yaDAuNGMwLjEsMCwwLjIsMC4xLDAuMiwwLjJsMCwwDQoJCQlDMzAuOSwxNS4zLDMwLjgsMTUuNCwzMC43LDE1LjR6Ii8+DQoJPC9nPg0KCTxnIGNsYXNzPSJzdDMiPg0KCQk8cGF0aCBjbGFzcz0ic3Q4IiBkPSJNMzAuNywxNi40aC0wLjRjLTAuMSwwLTAuMi0wLjEtMC4yLTAuMmwwLDBjMC0wLjEsMC4xLTAuMiwwLjItMC4yaDAuNGMwLjEsMCwwLjIsMC4xLDAuMiwwLjJsMCwwDQoJCQlDMzAuOSwxNi4zLDMwLjgsMTYuNCwzMC43LDE2LjR6Ii8+DQoJCTxwYXRoIGNsYXNzPSJzdDgiIGQ9Ik0zMC43LDE3LjRoLTAuNGMtMC4xLDAtMC4yLTAuMS0wLjItMC4ybDAsMGMwLTAuMSwwLjEtMC4yLDAuMi0wLjJoMC40YzAuMSwwLDAuMiwwLjEsMC4yLDAuMmwwLDANCgkJCUMzMC45LDE3LjMsMzAuOCwxNy40LDMwLjcsMTcuNHoiLz4NCgkJPHBhdGggY2xhc3M9InN0OCIgZD0iTTMwLjcsMTguNGgtMC40Yy0wLjEsMC0wLjItMC4xLTAuMi0wLjJsMCwwYzAtMC4xLDAuMS0wLjIsMC4yLTAuMmgwLjRjMC4xLDAsMC4yLDAuMSwwLjIsMC4ybDAsMA0KCQkJQzMwLjksMTguMywzMC44LDE4LjQsMzAuNywxOC40eiIvPg0KCTwvZz4NCjwvZz4NCjxnPg0KCTxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0yNS42LDguMkwyNS42LDguMmMtMC4zLDAuMS0wLjcsMC0wLjgtMC4zbC0wLjYtMS4yYy0wLjEtMC4zLDAtMC43LDAuMy0wLjhsMCwwYzAuMy0wLjEsMC43LDAsMC44LDAuMw0KCQlsMC42LDEuMkMyNi4xLDcuNywyNS45LDgsMjUuNiw4LjJ6Ii8+DQoJPHBhdGggY2xhc3M9InN0MCIgZD0iTTMwLjEsNy44SDMwYy0wLjMsMC0wLjYtMC4zLTAuNS0wLjdsMC4xLTEuNGMwLTAuMywwLjMtMC42LDAuNy0wLjVoMC4xYzAuMywwLDAuNiwwLjMsMC41LDAuN2wtMC4xLDEuNA0KCQlDMzAuOCw3LjYsMzAuNSw3LjgsMzAuMSw3Ljh6Ii8+DQoJPHBhdGggY2xhc3M9InN0MCIgZD0iTTM0LjMsOS40TDM0LjMsOS40Yy0wLjItMC4yLTAuMy0wLjYsMC0wLjlsMC45LTFjMC4yLTAuMiwwLjYtMC4zLDAuOSwwbDAsMGMwLjIsMC4yLDAuMywwLjYsMCwwLjlsLTAuOSwxDQoJCUMzNC45LDkuNiwzNC41LDkuNiwzNC4zLDkuNHoiLz4NCgk8cGF0aCBjbGFzcz0ic3QwIiBkPSJNMzYuMywxM0wzNi4zLDEzYzAtMC4zLDAuMi0wLjYsMC41LTAuNmwxLjQtMC4xYzAuMywwLDAuNiwwLjIsMC42LDAuNWwwLDBjMCwwLjMtMC4yLDAuNi0wLjUsMC42bC0xLjQsMC4xDQoJCUMzNi42LDEzLjYsMzYuMywxMy4zLDM2LjMsMTN6Ii8+DQoJPHBhdGggY2xhc3M9InN0MCIgZD0iTTM1LjksMTdMMzUuOSwxN2MwLjEtMC4zLDAuNS0wLjUsMC44LTAuNGwxLjMsMC41YzAuMywwLjEsMC40LDAuNCwwLjMsMC43bDAsMGMtMC4xLDAuMy0wLjQsMC40LTAuNywwLjMNCgkJbC0xLjMtMC41QzM1LjksMTcuNiwzNS44LDE3LjMsMzUuOSwxN3oiLz4NCjwvZz4NCjwvc3ZnPg0K`;

class weatherIoT {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'weatherIoT';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'weatherIoT';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const weatherIoTState = sourceTarget.getCustomState(weatherIoT.STATE_KEY);
            if (weatherIoTState) {
                newTarget.setCustomState(weatherIoT.STATE_KEY, Clone.simple(weatherIoTState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    EXTENSION_SPECIFIC_BLOCKS(board) {
        const commonExtensions = () => [
            
        ];
        let boardCommonExtensionList = [];
        let commonExtensionList = commonExtensions();
        for (let commonExtensionIndex in commonExtensionList) {
            let commonExtension = commonExtensionList[commonExtensionIndex];
            if (commonExtension.allowedBoards.includes(board)) {
                boardCommonExtensionList.push(commonExtension);
            }
        }
        return boardCommonExtensionList;
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'weatherIoT',
            name: formatMessage({
                id: 'weatherIoT.weatherIoT',
                default: 'Weather Data',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#f96a32',
            colourSecondary: '#fa4c13',
            colourTertiary: '#f43d0c',
            blocks: [
                {
                    opcode: 'getWeatherData',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'weatherIoT.getWeatherData',
                        default: 'get weather data for lat [LATITUDE] & lon [LONGITUDE]',
                        description: 'Read data from web'
                    }),
                    arguments: {
                        LATITUDE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '23.02'
                        },
                        LONGITUDE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '72.57'
                        }
                    }
                },
                "---",
                {
                    opcode: 'getWeatherValueTemp',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getWeatherValueTemp',
                        default: 'get temperature [WEATHERLIST] data',
                        description: 'Read float value of the weather'
                    }),
                    arguments: {
                        WEATHERLIST: {
                            type: ArgumentType.NUMBER,
                            menu: 'tempWeatherData',
                            defaultValue: '3'
                        }
                    }
                },
                {
                    opcode: 'getPressure',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getPressure',
                        default: 'get pressure (hPa)',
                        description: 'Get Pressure'
                    }),
                },
                {
                    opcode: 'getHumidity',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getHumidity',
                        default: 'get humidity (%)',
                        description: 'Get Humidity'
                    }),
                },
                {
                    opcode: 'getWind',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getWind',
                        default: 'get wind [WEATHERLIST]',
                        description: 'Get wind'
                    }),
                    arguments: {
                        WEATHERLIST: {
                            type: ArgumentType.NUMBER,
                            menu: 'windOption',
                            defaultValue: '7'
                        }
                    }
                },
                {
                    opcode: 'getCoord',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getCoord',
                        default: 'get coordinate [WEATHERLIST]',
                        description: 'Get coordinate'
                    }),
                    arguments: {
                        WEATHERLIST: {
                            type: ArgumentType.NUMBER,
                            menu: 'coordinateList',
                            defaultValue: '1'
                        }
                    }
                },
                "---",
                {
                    opcode: 'getTime',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getTime',
                        default: 'get [WEATHERLIST] time',
                        description: 'Get time'
                    }),
                    arguments: {
                        WEATHERLIST: {
                            type: ArgumentType.NUMBER,
                            menu: 'timeList',
                            defaultValue: '6'
                        }
                    }
                },
                {
                    opcode: 'getWeather',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getWeather',
                        default: 'get weather',
                        description: 'Read float value of the weather'
                    }),
                },
                {
                    opcode: 'getCity',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'weatherIoT.getCity',
                        default: 'get city',
                        description: 'Read float value of the weather'
                    }),
                },
                "---",
                {
                    opcode: 'setAPI',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'weatherIoT.setAPI',
                        default: 'set API to [API]',
                        description: 'set API'
                    }),
                    arguments: {
                        API: {
                            type: ArgumentType.STRING,
                            defaultValue: 'your API'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'weatherIoT.blockSeparatorMessage3',
                        default: 'Connect to Wi-Fi (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'connectToWifi',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'weatherIoT.connectToWifi',
                        default: 'connect to Wifi [WIFI] with password [PASSWORD]',
                        description: 'Connect to Wifi'
                    }),
                    arguments: {
                        WIFI: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Wifi Name'
                        },
                        PASSWORD: {
                            type: ArgumentType.STRING,
                            defaultValue: 'Password'
                        }
                    }
                },
            ],
            menus: {
                tempWeatherData: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option3',
                            default: 'in Celcius',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option5',
                            default: 'Min in Celcius',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option6',
                            default: 'Max in Celcius',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option4',
                            default: 'in Fareinhiet',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option13',
                            default: 'Min in Fareinhiet',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option14',
                            default: 'Max in Fareinhiet',
                            description: 'Menu'
                        }), value: '14'
                    },
                ],
                coordinateList: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option1',
                            default: 'latitude',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option2',
                            default: 'longitude',
                            description: 'Menu'
                        }), value: '2'
                    },
                ],
                windOption: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option10',
                            default: 'speed (m/s)',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.FloatWeatherData.option11',
                            default: 'direction',
                            description: 'Menu'
                        }), value: '8'
                    },
                ],
                timeList: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.StringWeatherData.option5',
                            default: 'data capture',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.StringWeatherData.option6',
                            default: 'sunrise',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weatherIoT.StringWeatherData.option7',
                            default: 'sunset',
                            description: 'Menu'
                        }), value: '7'
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

    connectToWifi(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.connectToWifi(args, util, this);
        }
        return RealtimeMode.connectToWifi(args, util, this);
    }

    getWeatherData(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getWeatherData(args, util, this);
        }
        return RealtimeMode.getWeatherData(args, util, this);
    }

    getWeather(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getWeather(args, util, this);
        }
        return RealtimeMode.getWeather(args, util, this);
    }

    getWeatherValueTemp(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getWeatherValueTemp(args, util, this);
        }
        return RealtimeMode.getWeatherValueTemp(args, util, this);
    }

    setAPI(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setAPI(args, util, this);
        }
        return RealtimeMode.setAPI(args, util, this);
    }

    getCity(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getCity(args, util, this);
        }
        return RealtimeMode.getCity(args, util, this);
    }

    getCoord(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getCoord(args, util, this);
        }
        return RealtimeMode.getCoord(args, util, this);
    }

    getPressure(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getPressure(args, util, this);
        }
        return RealtimeMode.getPressure(args, util, this);
    }

    getHumidity(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getHumidity(args, util, this);
        }
        return RealtimeMode.getHumidity(args, util, this);
    }

    getWind(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getWind(args, util, this);
        }
        return RealtimeMode.getWind(args, util, this);
    }

    getTime(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getTime(args, util, this);
        }
        return RealtimeMode.getTime(args, util, this);
    }
}

module.exports = weatherIoT;
