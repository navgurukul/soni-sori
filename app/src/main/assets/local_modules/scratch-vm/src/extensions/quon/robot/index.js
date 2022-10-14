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

class quonRobot {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quonRobot';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quonRobotState = sourceTarget.getCustomState(quonRobot.STATE_KEY);
            if (quonRobotState) {
                newTarget.setCustomState(quonRobot.STATE_KEY, Clone.simple(quonRobotState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quonRobot',
            name: formatMessage({
                id: 'quon.robot',
                default: 'Robot',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#f75252',
            colourSecondary: '#ff3746',
            colourTertiary: '#e52c42',
            blocks: [
                {
                    opcode: 'turnRight',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quonRobot.turnRight',
                        default: 'turn right by angle [ANGLE]',
                        description: 'turn robot right at given angle'
                    }),
                    arguments: {
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        }
                    }
                },
                {
                    opcode: 'turnLeft',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quonRobot.turnLeft',
                        default: 'turn left by angle [ANGLE]',
                        description: 'turn robot left at given angle'
                    }),
                    arguments: {
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        }
                    }
                },
                {
                    opcode: 'moveStraight',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quonRobot.moveStraight',
                        default: 'move robot straight [DIRECTION] with speed [SPEED]',
                        description: 'move robot straight in forward and backward direction'
                    }),
                    arguments: {
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'motionDirection',
                            defaultValue: '90'
                        },
                        SPEED: {
                            type: ArgumentType.MATHSLIDER100,
                            defaultValue: '80'
                        },
                    }
                },
                {
                    opcode: 'getAngle',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quonRobot.getAngle',
                        default: 'get [ANGLETYPE] angle',
                        description: 'move robot straight in forward and backward direction'
                    }),
                    arguments: {
                        ANGLETYPE: {
                            type: ArgumentType.NUMBER,
                            menu: 'angleType',
                            defaultValue: '1'
                        }
                    }
                }

            ],
            menus: {
                motionDirection: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection.option1',
                            default: 'forward',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.motorDirection.option2',
                            default: 'reverse',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                angleType: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.quon.angleType.option1',
                            default: 'Yaw',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.quon.angleType.option2',
                            default: 'Pitch',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.quon.angleType.option3',
                            default: 'Roll',
                            description: 'Menu'
                        }), value: '3'
                    },
                ],
            }
        };
    }

    turnRight(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.turnRight(args, util, this);
        }
        return RealtimeMode.turnRight(args, util, this);
    }

    turnLeft(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.turnLeft(args, util, this);
        }
        return RealtimeMode.turnLeft(args, util, this);
    }

    moveStraight(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveStraight(args, util, this);
        }
        return RealtimeMode.moveStraight(args, util, this);
    }

    getAngle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getAngle(args, util, this);
        }
        return RealtimeMode.getAngle(args, util, this);
    }
}

module.exports = quonRobot;
