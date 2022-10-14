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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iIzM4MzgzOCIgZD0iTTM0LjMsMzUuNUg1LjdjLTAuNywwLTEuMi0wLjUtMS4yLTEuMlY1LjdDNC41LDUsNSw0LjUsNS43LDQuNWgyOC42YzAuNywwLDEuMiwwLjUsMS4yLDEuMnYyOC42DQoJQzM1LjUsMzUsMzUsMzUuNSwzNC4zLDM1LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxOS41LDI0LDE5LjMsMjQuMiwxOS4xLDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTQuNCwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxNC45LDI0LDE0LjYsMjQuMiwxNC40LDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDI0LjJINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyNCwxMCwyNC4yLDkuNywyNC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMjQuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMjQuMiwyNCwyNCwyNC4yLDIzLjcsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0yOC40LDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzI4LjksMjQsMjguNywyNC4yLDI4LjQsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTkuMywxOS4zLDE5LjUsMTkuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMTkuNWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxOS4zLDE0LjYsMTkuNSwxNC40LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDE5LjVINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxOS4zLDEwLDE5LjUsOS43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE5LjMsMjQsMTkuNSwyMy43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE5LjMsMjguNywxOS41LDI4LjQsMTkuNXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDE0LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTQuNiwxOS4zLDE0LjksMTkuMSwxNC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxNC42LDE0LjYsMTQuOSwxNC40LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDE0LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxNC42LDEwLDE0LjksOS43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE0LjYsMjQsMTQuOSwyMy43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE0LjYsMjguNywxNC45LDI4LjQsMTQuOXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMjguNywxOS4zLDI4LjksMTkuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMjguOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwyOC43LDE0LjYsMjguOSwxNC40LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDI4LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyOC43LDEwLDI4LjksOS43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDI4LjcsMjQsMjguOSwyMy43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDI4LjcsMjguNywyOC45LDI4LjQsMjguOXoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDMzLjZoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMzMuNCwxOS4zLDMzLjYsMTkuMSwzMy42eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwzMy40LDE0LjYsMzMuNiwxNC40LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDMzLjZINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwzMy40LDEwLDMzLjYsOS43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDMzLjQsMjQsMzMuNiwyMy43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDMzLjQsMjguNywzMy42LDI4LjQsMzMuNnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjQsMzMuNCwyNC4yLDMzLjEsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTkuMywzMy40LDE5LjUsMzMuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwxNC42LDMzLjQsMTQuOSwzMy4xLDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTAsMTkuMywxMC4yLDE5LjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xNC40LDEwLjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxMCwxNC42LDEwLjIsMTQuNCwxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTkuNywxMC4ySDYuOWMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxMCwxMCwxMC4yLDkuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDEwLDI0LDEwLjIsMjMuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTI4LjQsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDEwLDI4LjcsMTAuMiwyOC40LDEwLjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMzMuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTAsMzMuNCwxMC4yLDMzLjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0zMy4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjguNywzMy40LDI4LjksMzMuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwzMy40LDMzLjQsMzMuNiwzMy4xLDMzLjZ6Ii8+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iIzM4MzgzOCIgZD0iTTM0LjMsMzUuNUg1LjdjLTAuNywwLTEuMi0wLjUtMS4yLTEuMlY1LjdDNC41LDUsNSw0LjUsNS43LDQuNWgyOC42YzAuNywwLDEuMiwwLjUsMS4yLDEuMnYyOC42DQoJQzM1LjUsMzUsMzUsMzUuNSwzNC4zLDM1LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxOS41LDI0LDE5LjMsMjQuMiwxOS4xLDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTQuNCwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxNC45LDI0LDE0LjYsMjQuMiwxNC40LDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDI0LjJINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyNCwxMCwyNC4yLDkuNywyNC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMjQuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMjQuMiwyNCwyNCwyNC4yLDIzLjcsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0yOC40LDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzI4LjksMjQsMjguNywyNC4yLDI4LjQsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTkuMywxOS4zLDE5LjUsMTkuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMTkuNWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxOS4zLDE0LjYsMTkuNSwxNC40LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDE5LjVINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxOS4zLDEwLDE5LjUsOS43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE5LjMsMjQsMTkuNSwyMy43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE5LjMsMjguNywxOS41LDI4LjQsMTkuNXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDE0LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTQuNiwxOS4zLDE0LjksMTkuMSwxNC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxNC42LDE0LjYsMTQuOSwxNC40LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDE0LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxNC42LDEwLDE0LjksOS43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE0LjYsMjQsMTQuOSwyMy43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE0LjYsMjguNywxNC45LDI4LjQsMTQuOXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMjguNywxOS4zLDI4LjksMTkuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMjguOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwyOC43LDE0LjYsMjguOSwxNC40LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDI4LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyOC43LDEwLDI4LjksOS43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDI4LjcsMjQsMjguOSwyMy43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDI4LjcsMjguNywyOC45LDI4LjQsMjguOXoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDMzLjZoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMzMuNCwxOS4zLDMzLjYsMTkuMSwzMy42eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwzMy40LDE0LjYsMzMuNiwxNC40LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDMzLjZINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwzMy40LDEwLDMzLjYsOS43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDMzLjQsMjQsMzMuNiwyMy43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDMzLjQsMjguNywzMy42LDI4LjQsMzMuNnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjQsMzMuNCwyNC4yLDMzLjEsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTkuMywzMy40LDE5LjUsMzMuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwxNC42LDMzLjQsMTQuOSwzMy4xLDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTAsMTkuMywxMC4yLDE5LjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xNC40LDEwLjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxMCwxNC42LDEwLjIsMTQuNCwxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTkuNywxMC4ySDYuOWMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxMCwxMCwxMC4yLDkuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDEwLDI0LDEwLjIsMjMuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTI4LjQsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDEwLDI4LjcsMTAuMiwyOC40LDEwLjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMzMuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTAsMzMuNCwxMC4yLDMzLjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0zMy4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjguNywzMy40LDI4LjksMzMuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwzMy40LDMzLjQsMzMuNiwzMy4xLDMzLjZ6Ii8+DQo8L3N2Zz4NCg==';

class displayModule {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'displayModule';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'displayModule';
    }


    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const displayModuleState = sourceTarget.getCustomState(displayModule.STATE_KEY);
            if (displayModuleState) {
                newTarget.setCustomState(displayModule.STATE_KEY, Clone.simple(displayModuleState));
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
            case 'TecBits': {
                return ExtensionMenu.digitalPins.tecBits;
            }
        }
        return ['0'];
    }

    getDefaultInfo(extensionId) {
        return {
            id: 'displayModule',
            name: formatMessage({
                id: 'displayModule.displayModule',
                default: 'Display Modules',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#f75252',
            colourSecondary: '#ff3746',
            colourTertiary: '#e52c42',
            blocks: [
                {
                    message: formatMessage({
                        id: 'displayModule.blockSeparatorMessage1',
                        default: 'Initialise LCD Display 16x2',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initialiseDisplay',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.initialiseDisplay',
                        default: 'initialize 16 x 2 display on RST [RESET] EN [ENABLE] D4 [PIND4] D5 [PIND5] D6 [PIND6] D7 [PIND7]',
                        description: 'initialise Display'
                    }),
                    arguments: {
                        RESET: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        ENABLE: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        },
                        PIND4: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        },
                        PIND5: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '9'
                        },
                        PIND6: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '10'
                        },
                        PIND7: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '11'
                        }
                    }
                },
                {
                    opcode: 'initialiseI2CDisplay',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.initialiseI2CDisplay',
                        default: 'initialize 16 x 2 I2C display at address [I2C_ADD]',
                        description: 'initialise Display'
                    }),
                    arguments: {
                        I2C_ADD: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0x27'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'displayModule.blockSeparatorMessage3',
                        default: 'LCD Display 16x2',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'setCursor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.setCursor',
                        default: 'set cursor at column [COLUMN] row [ROW]',
                        description: 'set Cursor'
                    }),
                    arguments: {
                        COLUMN: {
                            type: ArgumentType.NUMBER,
                            menu: 'column',
                            defaultValue: '1'
                        },
                        ROW: {
                            type: ArgumentType.NUMBER,
                            menu: 'row',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'write',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.write',
                        default: 'write [TEXT] display',
                        description: 'write on display',
                    }),
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText1",
                                default: "Hello World",
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        }
                    }
                },
                {
                    opcode: 'clearDisplay',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.clearDisplay',
                        default: 'clear display',
                        description: 'clear display'
                    })
                },
                {
                    opcode: 'setMode',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.setMode',
                        default: 'set [MODE] mode',
                        description: 'set mode'
                    }),
                    arguments: {
                        MODE: {
                            type: ArgumentType.NUMBER,
                            menu: 'options',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'displayModule.blockSeparatorMessage4',
                        default: '4x7 Segment Display (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initializeTM1637Display',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.initializeTM1637Display',
                        default: 'intialize TM1637 4x7 segment display to CLK [CLKPIN] DIO [DIOPIN]',
                        description: 'Intialise TM1637 Display Module'
                    }),
                    arguments: {
                        CLKPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        DIOPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        }
                    }
                },
                {
                    opcode: 'showNumberTM1637Display',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.showNumberTM1637Display',
                        default: 'display [INPUT] of length [LENGTH], position [POSITION], [DOTS] colon & [LEADINGZERO]',
                        description: 'Show Number on TM1637 Display Module'
                    }),
                    arguments: {
                        INPUT: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '50'
                        },
                        LENGTH: {
                            type: ArgumentType.NUMBER,
                            menu: 'TM1637Length',
                            defaultValue: '4'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'TM1637Length',
                            defaultValue: '1'
                        },
                        LEADINGZERO: {
                            type: ArgumentType.NUMBER,
                            menu: 'TM1637LeadingZeroOption',
                            defaultValue: '0'
                        },
                        DOTS: {
                            type: ArgumentType.NUMBER,
                            menu: 'TM1637Colon',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'clearTM1637Display',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.clearTM1637Display',
                        default: 'clear display',
                        description: 'Clear Display'
                    })
                },
                {
                    message: formatMessage({
                        id: 'displayModule.blockSeparatorMessage2',
                        default: '8x8 Dot Matrix Display (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initializeDotMatrixDisplay',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.initializeDotMatrixDisplay',
                        default: 'intialize display to DIN [DINPIN] CS [CSPIN] CLK [CLKPIN]',
                        description: 'Intialise Dot Matrix Display Module'
                    }),
                    arguments: {
                        DINPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        CSPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        },
                        CLKPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        }
                    }
                },
                {
                    opcode: 'displayMatrix',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.displayMatrix',
                        default: 'display [MATRIX]',
                        description: 'display pattern on dot matrix'
                    }),
                    arguments: {
                        MATRIX: {
                            type: ArgumentType.MATRIX2,
                            defaultValue: '0011110001000010101001011000000110100101100110010100001000111100'
                        }
                    }
                },
                {
                    opcode: 'setLED',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'displayModule.setLED',
                        default: 'turn [STATE] LED row [ROW] column [COL]',
                        description: 'Controlling LED'
                    }),
                    arguments: {
                        STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'state',
                            defaultValue: '1'
                        },
                        ROW: {
                            type: ArgumentType.NUMBER,
                            menu: 'rowColumn',
                            defaultValue: '1'
                        },
                        COL: {
                            type: ArgumentType.NUMBER,
                            menu: 'rowColumn',
                            defaultValue: '1'
                        }
                    }
                },
            ],
            menus: {
                digitalPins: this.DIGITAL_PINS,
                column: [
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16'
                ],
                row: [
                    '1', '2'
                ],
                options: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option1',
                            default: 'blink',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option2',
                            default: 'no blink',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option3',
                            default: 'cursor',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option4',
                            default: 'no cursor',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option5',
                            default: 'display',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option6',
                            default: 'no display',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option7',
                            default: 'auto-scroll',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option8',
                            default: 'no auto-scroll',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option9',
                            default: 'scroll display to left',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.options.option10',
                            default: 'scroll display to right',
                            description: 'Menu'
                        }), value: '10'
                    }
                ],
                TM1637Length: [
                    '1', '2', '3', '4'
                ],
                TM1637LeadingZeroOption: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.TM1637LeadingZeroOption.option1',
                            default: 'leading zeros',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.TM1637LeadingZeroOption.option2',
                            default: 'no leading zeros',
                            description: 'Menu'
                        }), value: '0'
                    }
                ],
                TM1637Colon: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option1',
                            default: 'with',
                            description: 'Menu'
                        }), value: '64'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option2',
                            default: 'without',
                            description: 'Menu'
                        }), value: '0'
                    }
                ],
                state: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.state.option1',
                            default: 'ON',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.displayModule.state.option2',
                            default: 'OFF',
                            description: 'Menu'
                        }), value: '0'
                    }
                ],
                rowColumn: [
                    '1', '2', '3', '4', '5', '6', '7', '8'
                ]
            }
        };
    }

    getInfo() {
        let extensionId = this.extensionName.toLowerCase();
        let info = this.getDefaultInfo(extensionId);
        info.blocks = BoardConfig.insertExtensionSpecificBlocks(info.blocks, this.EXTENSION_SPECIFIC_BLOCKS(getBoardId(this.runtime.boardSelected)));
        return info;
    }

    initialiseDisplay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseDisplay(args, util, this);
        }
        return RealtimeMode.initialiseDisplay(args, util, this);
    }

    initialiseI2CDisplay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseI2CDisplay(args, util, this);
        }
        return RealtimeMode.initialiseI2CDisplay(args, util, this);
    }

    setCursor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setCursor(args, util, this);
        }
        return RealtimeMode.setCursor(args, util, this);
    }

    write(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.write(args, util, this);
        }
        return RealtimeMode.write(args, util, this);
    }

    setMode(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setMode(args, util, this);
        }
        return RealtimeMode.setMode(args, util, this);
    }

    clearDisplay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.clearDisplay(args, util, this);
        }
        return RealtimeMode.clearDisplay(args, util, this);
    }

    initializeTM1637Display(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initializeTM1637Display(args, util, this);
        }
        return RealtimeMode.initializeTM1637Display(args, util, this);
    }

    showNumberTM1637Display(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.showNumberTM1637Display(args, util, this);
        }
        return RealtimeMode.showNumberTM1637Display(args, util, this);
    }

    clearTM1637Display(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.clearTM1637Display(args, util, this);
        }
        return RealtimeMode.clearTM1637Display(args, util, this);
    }

    initializeDotMatrixDisplay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initializeDotMatrixDisplay(args, util, this);
        }
        return RealtimeMode.initializeDotMatrixDisplay(args, util, this);
    }

    displayMatrix(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayMatrix(args, util, this);
        }
        return RealtimeMode.displayMatrix(args, util, this);
    }

    setLED(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setLED(args, util, this);
        }
        return RealtimeMode.setLED(args, util, this);
    }

}

module.exports = displayModule;
