const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iIzM4MzgzOCIgZD0iTTM0LjMsMzUuNUg1LjdjLTAuNywwLTEuMi0wLjUtMS4yLTEuMlY1LjdDNC41LDUsNSw0LjUsNS43LDQuNWgyOC42YzAuNywwLDEuMiwwLjUsMS4yLDEuMnYyOC42DQoJQzM1LjUsMzUsMzUsMzUuNSwzNC4zLDM1LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxOS41LDI0LDE5LjMsMjQuMiwxOS4xLDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTQuNCwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxNC45LDI0LDE0LjYsMjQuMiwxNC40LDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDI0LjJINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyNCwxMCwyNC4yLDkuNywyNC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMjQuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMjQuMiwyNCwyNCwyNC4yLDIzLjcsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0yOC40LDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzI4LjksMjQsMjguNywyNC4yLDI4LjQsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTkuMywxOS4zLDE5LjUsMTkuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMTkuNWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxOS4zLDE0LjYsMTkuNSwxNC40LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDE5LjVINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxOS4zLDEwLDE5LjUsOS43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE5LjMsMjQsMTkuNSwyMy43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE5LjMsMjguNywxOS41LDI4LjQsMTkuNXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDE0LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTQuNiwxOS4zLDE0LjksMTkuMSwxNC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxNC42LDE0LjYsMTQuOSwxNC40LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDE0LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxNC42LDEwLDE0LjksOS43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE0LjYsMjQsMTQuOSwyMy43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE0LjYsMjguNywxNC45LDI4LjQsMTQuOXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMjguNywxOS4zLDI4LjksMTkuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMjguOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwyOC43LDE0LjYsMjguOSwxNC40LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDI4LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyOC43LDEwLDI4LjksOS43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDI4LjcsMjQsMjguOSwyMy43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDI4LjcsMjguNywyOC45LDI4LjQsMjguOXoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDMzLjZoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMzMuNCwxOS4zLDMzLjYsMTkuMSwzMy42eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwzMy40LDE0LjYsMzMuNiwxNC40LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDMzLjZINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwzMy40LDEwLDMzLjYsOS43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDMzLjQsMjQsMzMuNiwyMy43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDMzLjQsMjguNywzMy42LDI4LjQsMzMuNnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjQsMzMuNCwyNC4yLDMzLjEsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTkuMywzMy40LDE5LjUsMzMuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwxNC42LDMzLjQsMTQuOSwzMy4xLDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTAsMTkuMywxMC4yLDE5LjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xNC40LDEwLjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxMCwxNC42LDEwLjIsMTQuNCwxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTkuNywxMC4ySDYuOWMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxMCwxMCwxMC4yLDkuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDEwLDI0LDEwLjIsMjMuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTI4LjQsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDEwLDI4LjcsMTAuMiwyOC40LDEwLjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMzMuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTAsMzMuNCwxMC4yLDMzLjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0zMy4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjguNywzMy40LDI4LjksMzMuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwzMy40LDMzLjQsMzMuNiwzMy4xLDMzLjZ6Ii8+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZmlsbD0iIzM4MzgzOCIgZD0iTTM0LjMsMzUuNUg1LjdjLTAuNywwLTEuMi0wLjUtMS4yLTEuMlY1LjdDNC41LDUsNSw0LjUsNS43LDQuNWgyOC42YzAuNywwLDEuMiwwLjUsMS4yLDEuMnYyOC42DQoJQzM1LjUsMzUsMzUsMzUuNSwzNC4zLDM1LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxOS41LDI0LDE5LjMsMjQuMiwxOS4xLDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTQuNCwyNC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMxNC45LDI0LDE0LjYsMjQuMiwxNC40LDI0LjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDI0LjJINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyNCwxMCwyNC4yLDkuNywyNC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMjQuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMjQuMiwyNCwyNCwyNC4yLDIzLjcsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0yOC40LDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzI4LjksMjQsMjguNywyNC4yLDI4LjQsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTkuMywxOS4zLDE5LjUsMTkuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMTkuNWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxOS4zLDE0LjYsMTkuNSwxNC40LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNOS43LDE5LjVINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxOS4zLDEwLDE5LjUsOS43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE5LjMsMjQsMTkuNSwyMy43LDE5LjV6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwxOS41aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE5LjMsMjguNywxOS41LDI4LjQsMTkuNXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDE0LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTQuNiwxOS4zLDE0LjksMTkuMSwxNC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxNC42LDE0LjYsMTQuOSwxNC40LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDE0LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxNC42LDEwLDE0LjksOS43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDE0LjYsMjQsMTQuOSwyMy43LDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwxNC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDE0LjYsMjguNywxNC45LDI4LjQsMTQuOXoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xOS4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMjguNywxOS4zLDI4LjksMTkuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0Y3NTI1MiIgZD0iTTE0LjQsMjguOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwyOC43LDE0LjYsMjguOSwxNC40LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDI4LjlINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwyOC43LDEwLDI4LjksOS43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjMuNywyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDI4LjcsMjQsMjguOSwyMy43LDI4Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjguNCwyOC45aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDI4LjcsMjguNywyOC45LDI4LjQsMjguOXoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0xOS4xLDMzLjZoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMzMuNCwxOS4zLDMzLjYsMTkuMSwzMy42eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTE0LjQsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwzMy40LDE0LjYsMzMuNiwxNC40LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNOS43LDMzLjZINi45Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwzMy40LDEwLDMzLjYsOS43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRjc1MjUyIiBkPSJNMjMuNywzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDMzLjQsMjQsMzMuNiwyMy43LDMzLjZ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMjguNCwzMy42aC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNXYtMi44YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDMzLjQsMjguNywzMy42LDI4LjQsMzMuNnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDI0LjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjQsMzMuNCwyNC4yLDMzLjEsMjQuMnoiLz4NCjxwYXRoIGZpbGw9IiNGNzUyNTIiIGQ9Ik0zMy4xLDE5LjVoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTkuMywzMy40LDE5LjUsMzMuMSwxOS41eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMTQuOWgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwxNC42LDMzLjQsMTQuOSwzMy4xLDE0Ljl6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMTkuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzE5LjUsMTAsMTkuMywxMC4yLDE5LjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0xNC40LDEwLjJoLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTQuOSwxMCwxNC42LDEwLjIsMTQuNCwxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTkuNywxMC4ySDYuOWMtMC4zLDAtMC41LTAuMi0wLjUtMC41VjYuOWMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMTAuMiwxMCwxMCwxMC4yLDkuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTIzLjcsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyNC4yLDEwLDI0LDEwLjIsMjMuNywxMC4yeiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTI4LjQsMTAuMmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVWNi45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWgyLjhjMC4zLDAsMC41LDAuMiwwLjUsMC41djIuOA0KCUMyOC45LDEwLDI4LjcsMTAuMiwyOC40LDEwLjJ6Ii8+DQo8cGF0aCBmaWxsPSIjRUFFQUVBIiBkPSJNMzMuMSwxMC4yaC0yLjhjLTAuMywwLTAuNS0wLjItMC41LTAuNVY2LjljMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMTAsMzMuNCwxMC4yLDMzLjEsMTAuMnoiLz4NCjxwYXRoIGZpbGw9IiNFQUVBRUEiIGQ9Ik0zMy4xLDI4LjloLTIuOGMtMC4zLDAtMC41LTAuMi0wLjUtMC41di0yLjhjMC0wLjMsMC4yLTAuNSwwLjUtMC41aDIuOGMwLjMsMCwwLjUsMC4yLDAuNSwwLjV2Mi44DQoJQzMzLjYsMjguNywzMy40LDI4LjksMzMuMSwyOC45eiIvPg0KPHBhdGggZmlsbD0iI0VBRUFFQSIgZD0iTTMzLjEsMzMuNmgtMi44Yy0wLjMsMC0wLjUtMC4yLTAuNS0wLjV2LTIuOGMwLTAuMywwLjItMC41LDAuNS0wLjVoMi44YzAuMywwLDAuNSwwLjIsMC41LDAuNXYyLjgNCglDMzMuNiwzMy40LDMzLjQsMzMuNiwzMy4xLDMzLjZ6Ii8+DQo8L3N2Zz4NCg==';

class quarkyDisplay {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarkyDisplay';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkyDisplayState = sourceTarget.getCustomState(quarkyDisplay.STATE_KEY);
            if (quarkyDisplayState) {
                newTarget.setCustomState(quarkyDisplay.STATE_KEY, Clone.simple(quarkyDisplayState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quarkyDisplay',
            name: formatMessage({
                id: 'quarky.display',
                default: 'Display',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#f75252',
            colourSecondary: '#ff3746',
            colourTertiary: '#e52c42',
            blocks: [
                {
                    opcode: 'displayEmotion',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.displayEmotion',
                        default: 'display [EMOTION] emotion',
                        description: 'show emotions'
                    }),
                    arguments: {
                        EMOTION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1',
                            menu: 'emotionMenu'
                        }

                    }
                },
                {
                    opcode: 'showAnimation',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.showAnimation',
                        default: 'display [ANIMATION] animation',
                        description: 'show animations'
                    }),
                    arguments: {
                        ANIMATION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1',
                            menu: 'animationMenu'
                        },
                    }
                },
                {
                    opcode: 'matrixPattern',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.matrixPattern',
                        default: 'display [PATTERN] pattern',
                        description: 'Set matrix pattern'
                    }),
                    arguments: {
                        PATTERN: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1',
                            menu: 'patternMenu'
                        },
                        // DELAY: {
                        //     type: ArgumentType.NUMBERS,
                        //     menu: 'DelayTime',
                        //     defaultValue: '0.05'
                        // },
                    }
                },
                "---",
                {
                    opcode: 'displayText',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.displayText',
                        default: 'display [TEXT] with [COLOR] at [SPEED] speed',
                        description: 'Set Matrix Display RGB values'
                    }),
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText5",
                                default: 'Hello!',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        },
                        COLOR: {
                            type: ArgumentType.COLOR,
                        },
                        SPEED: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '2',
                            menu: 'speedMenu'
                        }
                    }
                },
                {
                    opcode: 'displayChar',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.displayChar',
                        default: 'display [CHAR] with [COLOR]',
                        description: 'display characters'
                    }),
                    arguments: {
                        CHAR: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText6",
                                default: 'A',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        },
                        COLOR: {
                            type: ArgumentType.COLOR,
                        }
                    }
                },
                "---",
                {
                    opcode: 'colourMatrix',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.colourMatrix',
                        default: 'display matrix as [MATRIX]',
                        description: 'Set Matrix Display RGB values'
                    }),
                    arguments: {
                        MATRIX: {
                            type: ArgumentType.MATRIXCOLOUR5X7,
                            defaultValue: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'
                        },
                    }
                },
                "---",
                {
                    opcode: 'setLED',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.setLED',
                        default: 'set LED x [XPOS] y [YPOS] to [COLOR] with brightness [BRIGHTNESS]%',
                        description: 'Set single led pixel'
                    }),
                    arguments: {
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            menu: 'xCord',
                            defaultValue: '1'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            menu: 'yCord',
                            defaultValue: '1'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        },
                        BRIGHTNESS: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '100'
                        },
                    }
                },
                {
                    opcode: 'offLED',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.offLED',
                        default: 'turn off LED x [XPOS] y [YPOS]',
                        description: 'Set single led pixel'
                    }),
                    arguments: {
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            menu: 'xCord',
                            defaultValue: '1'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            menu: 'yCord',
                            defaultValue: '1'
                        }
                    }
                },
                "---",
                {
                    opcode: 'clearScreen',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.clearScreen',
                        default: 'clear screen',
                        description: 'Set single led pixel'
                    }),
                },
                {
                    opcode: 'setBrightness',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarkyDisplay.setBrightness',
                        default: 'set display brightness to [BRIGHTNESS]',
                        description: 'Set RGB color values'
                    }),
                    arguments: {
                        BRIGHTNESS: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '50'
                        }
                    }
                },
                "---",
                {
                    opcode: 'rgbColor',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarkyDisplay.rgbColor',
                        default: 'set R [RED] G [GREEN] B [BLUE]',
                        description: 'Set RGB color values'
                    }),
                    arguments: {
                        RED: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        },
                        GREEN: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        },
                        BLUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: Math.floor(Math.random() * 255)
                        }
                    }
                },

            ],
            menus: {
                xCord: {
                    acceptReporters: true,
                    items: ['1', '2', '3', '4', '5', '6', '7']
                },
                yCord: {
                    acceptReporters: true,
                    items: ['1', '2', '3', '4', '5']
                },
                patternMenu:
                    [
                        { text: 'rainbow', value: '1' },
                        { text: 'rainbow strip', value: '2' },
                        { text: 'cloud colors', value: '3' },
                        { text: 'party colors', value: '4' },
                        { text: 'forest colors', value: '5' },
                        { text: 'lava colors', value: '6' },
                        { text: 'ocean colors', value: '7' }
                    ],

                speedMenu:[
                    { text: 'fast', value: '1' },
                    { text: 'medium', value: '2' },
                    { text: 'slow', value: '3' },
                ],
                // DelayTime: {
                //     acceptReporters: true,
                //     items: ['0.01', '0.05', '0.1', '0.5', '1']
                // },
                emotionMenu: [
                    { text: 'happy', value: '1' },
                    { text: 'angry', value: '2' },
                    { text: 'basic', value: '3' },
                    { text: 'crying', value: '4' },
                    { text: 'disco', value: '5' },
                    { text: 'giggle', value: '6' },
                    { text: 'super angry', value: '7' },
                    { text: 'love', value: '8' },
                    { text: 'nerd', value: '9' },
                    { text: 'reject', value: '10' },
                    { text: 'surprise', value: '11' },
                    { text: 'wave', value: '12' },
                    { text: 'thinking', value: '13' },
                ],
                animationMenu: [
                    { text: 'happy', value: '1' },
                    { text: 'nerdy', value: '2' },
                    { text: 'thinking', value: '3' },
                    { text: 'angry', value: '4' },
                    { text: 'contempt', value: '5' },
                    { text: 'blink', value: '6' },
                    { text: 'fear', value: '7' },
                    { text: 'surprise', value: '8' },
                    { text: 'wink', value: '9' },
                    { text: 'wave', value: '10' },
                    { text: 'crying', value: '11' },

                ]
            },
        };
    }

    colourMatrix(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.colourMatrix(args, util, this);
        }
        return RealtimeMode.colourMatrix(args, util, this);
    }

    rgbColor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rgbColor(args, util, this);
        }
        return RealtimeMode.rgbColor(args, util, this);
    }

    setLED(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setLED(args, util, this);
        }
        return RealtimeMode.setLED(args, util, this);
    }

    offLED(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.offLED(args, util, this);
        }
        return RealtimeMode.offLED(args, util, this);
    }

    clearScreen(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.clearScreen(args, util, this);
        }
        return RealtimeMode.clearScreen(args, util, this);
    }

    rgbColor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rgbColor(args, util, this);
        }
        return RealtimeMode.rgbColor(args, util, this);
    }

    matrixPattern(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.matrixPattern(args, util, this);
        }
        return RealtimeMode.matrixPattern(args, util, this);
    }

    displayText(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayText(args, util, this);
        }
        return RealtimeMode.displayText(args, util, this);
    }

    displayChar(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayChar(args, util, this);
        }
        return RealtimeMode.displayChar(args, util, this);
    }


    displayEmotion(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayEmotion(args, util, this);
        }
        return RealtimeMode.displayEmotion(args, util, this);
    }

    showAnimation(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.showAnimation(args, util, this);
        }
        return RealtimeMode.showAnimation(args, util, this);
    }

    setBrightness(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setBrightness(args, util, this);
        }
        return RealtimeMode.setBrightness(args, util, this);
    }
}

module.exports = quarkyDisplay;
