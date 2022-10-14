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

class tWatchDisplay {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'tWatchDisplay';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const tWatchDisplayState = sourceTarget.getCustomState(tWatchDisplay.STATE_KEY);
            if (tWatchDisplayState) {
                newTarget.setCustomState(tWatchDisplay.STATE_KEY, Clone.simple(tWatchDisplayState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'tWatchDisplay',
            name: formatMessage({
                id: 'tWatch.display',
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
                    opcode: 'fillscreen',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillscreen',
                        default: 'fill screen with [COLOR] color',
                        description: 'Fill screen'
                    }),
                    arguments: {
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'setScreenRotation',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.setScreenRotation',
                        default: 'set screen rotation to [ROTATION_VALUE]',
                        description: 'Set Screen Rotation'
                    }),
                    arguments: {
                        ROTATION_VALUE: {
                            type: ArgumentType.NUMBER,
                            menu: 'rotation',
                            defaultValue: '0'
                        }
                    }
                },

                {
                    opcode: 'rgbColorDisplay',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatchDisplay.rgbColorDisplay',
                        default: 'set red [RED] green [GREEN] blue [BLUE]',
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
                {
                    message: formatMessage({
                        id: 'tWatchDisplay.blockSeparatorMessage1',
                        default: 'Display Image (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'setImage',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: `tWatchDisplay.setimage`,
                        default: `set [NAME] as [IMAGE] and scale by [SCALER]`,
                        description: `Image Display`
                    }),
                    arguments: {
                        NAME: {
                            type: ArgumentType.IMAGE_MENU
                        },
                        IMAGE: {
                            type: ArgumentType.IMAGE_SELECT_240x240
                        },
                        SCALER: {
                            type: ArgumentType.NUMBER,
                            menu: 'scaler',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'displayImage',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: `tWatchDisplay.displayimage`,
                        default: `display [NAME] at [X_AXIS] , [Y_AXIS]`,
                        description: `Image Display`
                    }),
                    arguments: {
                        NAME: {
                            type: ArgumentType.IMAGE_MENU
                        },
                        X_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            defaultValue: 0
                        },
                        Y_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            defaultValue: 0
                        }
                    }
                },
                {
                    opcode: 'displayImagefromSD',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: `tWatchDisplay.displayImagefromSD`,
                        default: `display [NAME] at [X_AXIS] , [Y_AXIS] from SD card`,
                        description: `Image Display`
                    }),
                    arguments: {
                        NAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'bmp1.bmp'
                        },
                        X_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            defaultValue: 0
                        },
                        Y_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            defaultValue: 0
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'tWatchDisplay.blockSeparatorMessage2',
                        default: 'Display Text',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'setCursor',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.setCursor',
                        default: 'set cursor at [X_AXIS], [Y_AXIS]',
                        description: 'Set cursor'
                    }),
                    arguments: {
                        X_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'setTextColorSize',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.setTextColorSize',
                        default: 'set text color to [TEXT_COLOR] with [BG_COLOR] background & size to [SIZE]',
                        description: 'Set pwm pin'
                    }),
                    arguments: {
                        TEXT_COLOR: {
                            type: ArgumentType.COLOR
                        },
                        BG_COLOR: {
                            type: ArgumentType.COLOR
                        },
                        SIZE: {
                            type: ArgumentType.NUMBER,
                            menu: 'textSize',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'write',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.write',
                        default: 'write [TEXT]',
                        description: 'Set text size'
                    }),
                    arguments: {
                        TEXT: {
                            type: ArgumentType.STRING,
                            defaultValue: formatMessage({
                                id: "pictoBlox.staticText4",
                                default: 'Hello World!',
                                description: "PictoBlox static text for TEXT type block argument"
                            })
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'tWatchDisplay.blockSeparatorMessage3',
                        default: 'Display Shapes',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'drawLine',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.drawLine',
                        default: 'draw line from [X1_AXIS], [Y1_AXIS] to [X2_AXIS], [Y2_AXIS] of [COLOR] color',
                        description: 'Set text size'
                    }),
                    arguments: {
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '0'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '150'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '118'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                /*{
                     opcode: 'displayMatrix3',
                     blockType: BlockType.COMMAND,
                     text: formatMessage({
                         id: 'tWatchDisplay.displayMatrix3',
                         default: 'display [MATRIX] of size [SIZE] px at position [XPOSITION], [YPOSITION],  color [COLOR] & background [COLOR2]',
                         description: 'display pattern on dot matrix'
                     }),
                     arguments: {
                         MATRIX: {
                             type: ArgumentType.MATRIX3,
                             defaultValue: '00000001111110000000000001111111111000000000111111111111000000011111111111111000000111001111001110000011110011110011110000111111111111111100001111111111111111000011111111111111110000111111111111111100001111011111101111000001111000000111100000011111000011111000000011111111111100000000011111111110000000000001111110000000'
                         },
                         SIZE: {
                             type: ArgumentType.NUMBER,
                             menu: 'size',
                             defaultValue: '3'
                         },
                         XPOSITION: {
                             type: ArgumentType.MATHSLIDER240,
                             defaultValue: '0'
                         },
                         YPOSITION: {
                             type: ArgumentType.MATHSLIDER240,
                             defaultValue: '0'
                         },
                         COLOR: {
                             type: ArgumentType.COLOR,
                         },
                         COLOR2: {
                             type: ArgumentType.COLOR,
                         },
                     }
                 },*/
                {
                    opcode: 'fillDrawRect',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillDrawRect',
                        default: '[OPTION] rectangle from [X1_AXIS], [Y1_AXIS] of width [X2_AXIS] & height [Y2_AXIS] & [COLOR] color',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '0'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '140'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '108'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawRoundRect',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillDrawRoundRect',
                        default: '[OPTION] round rect form [X1_AXIS],[Y1_AXIS] of width [X2_AXIS] height [Y2_AXIS] radius [RADIUS] color [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '0'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '10'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '140'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '108'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '5'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawCircle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillDrawCircle',
                        default: '[OPTION] circle from center [X1_AXIS] [Y1_AXIS] radius [RADIUS] colour[COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '0'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '30'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawEllipse',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillDrawEllipse',
                        default: '[OPTION] ellipse from center [X1_AXIS] [Y1_AXIS] X length [X2_AXIS] Y length [Y2_AXIS] colour [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '0'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '64'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '50'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '30'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    opcode: 'fillDrawTriangle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tWatchDisplay.fillDrawTriangle',
                        default: '[OPTION] triangle : Point 1 [X1_AXIS] [Y1_AXIS] Point 2 [X2_AXIS] [Y2_AXIS] Point 3 [X3_AXIS] [Y3_AXIS] colour [COLOR]',
                        description: 'Fill/Draw Rect'
                    }),
                    arguments: {
                        OPTION: {
                            type: ArgumentType.STRING,
                            menu: 'options',
                            defaultValue: '1'
                        },
                        X1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '0'
                        },
                        Y1_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '0'
                        },
                        X2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '150'
                        },
                        Y2_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '64'
                        },
                        X3_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'xAxis',
                            defaultValue: '10'
                        },
                        Y3_AXIS: {
                            type: ArgumentType.MATHSLIDER240,
                            // menu: 'yAxis',
                            defaultValue: '118'
                        },
                        COLOR: {
                            type: ArgumentType.COLOR
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'tWatchDisplay.blockSeparatorMessage4',
                        default: 'Touch Screen',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'getTouchValue',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tWatchDisplay.getTouchValue',
                        default: 'get touch screen [AXIS] coordinate',
                        description: 'check if T-Watch screen is touched'
                    }),
                    arguments: {
                        AXIS: {
                            type: ArgumentType.STRING,
                            defaultValue: 'x',
                            menu: 'axisMenu'
                        }
                    }
                },
                // {
                //     message: formatMessage({
                // id: 'tWatchDisplay.blockSeparatorMessage5',
                // default: 'Touch Screen',
                // description: 'Blocks separator message'
                // })
                // },
                // {
                //     opcode: 'getTouchCoordinates',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         id: 'tWatchDisplay.getTouchCoordinates',
                //         default: 'get [AXIS] touch coordinates',
                //         description: 'Get X and Y Co-ordinates for touch on screen'
                //     }),
                //     arguments: {
                //         AXIS: {
                //             type: ArgumentType.STRING,
                //             menu: 'options',
                //             defaultValue: '1'
                //         }
                //     }
                // }
            ],
            menus: {
                textSize: ['1', '2', '3', '4', '5',
                    { text: 'digital', value: '6' }
                ],
                options: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.esp32.options.option1',
                            default: 'Fill',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.esp32.options.option2',
                            default: 'Draw',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                size: [
                    '1', '2', '3', '4', '5', '6', '7', '8'
                ],
                axisMenu: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.axisMenu.option1',
                            default: 'x-axis',
                            description: 'Menu'
                        }), value: 'x'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.axisMenu.option2',
                            default: 'y-axis',
                            description: 'Menu'
                        }), value: 'y'
                    }
                ],
                rotation: [
                    '0', '1', '2', '3'
                ],
                imageNameMenu: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.imageNameMenu.option1',
                            default: 'image1',
                            description: 'Menu'
                        })
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.imageNameMenu.option2',
                            default: 'image2',
                            description: 'Menu'
                        })
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.imageNameMenu.option3',
                            default: 'image3',
                            description: 'Menu'
                        })
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.imageNameMenu.option4',
                            default: 'image4',
                            description: 'Menu'
                        })
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.tWatch.imageNameMenu.option5',
                            default: 'image5',
                            description: 'Menu'
                        })
                    }
                ],
                scaler: [
                    { text: '0.1x', value: '0.1' },
                    { text: '0.25x', value: '0.25' },
                    { text: '0.5x', value: '0.5' },
                    { text: '0.75x', value: '0.75' },
                    { text: '1x', value: '1' },
                    { text: '1.25x', value: '1.25' },
                    { text: '1.5x', value: '1.5' },
                    { text: '1.75x', value: '1.75' },
                    { text: '2x', value: '2' },
                ]
            }
        };
    }

    setImage(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setImage(args, util, this);
        }
        return RealtimeMode.setImage(args, util, this);
    }

    displayImage(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayImage(args, util, this);
        }
        return RealtimeMode.displayImage(args, util, this);
    }

    displayImagefromSD(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayImagefromSD(args, util, this);
        }
        return RealtimeMode.displayImagefromSD(args, util, this);
    }

    fillscreen(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillscreen(args, util, this);
        }
        return RealtimeMode.fillscreen(args, util, this);
    }

    setCursor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setCursor(args, util, this);
        }
        return RealtimeMode.setCursor(args, util, this);
    }

    setScreenRotation(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setScreenRotation(args, util, this);
        }
        return RealtimeMode.setScreenRotation(args, util, this);
    }

    setTextColorSize(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setTextColorSize(args, util, this);
        }
        return RealtimeMode.setTextColorSize(args, util, this);
    }

    write(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.write(args, util, this);
        }
        return RealtimeMode.write(args, util, this);
    }

    drawLine(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.drawLine(args, util, this);
        }
        return RealtimeMode.drawLine(args, util, this);
    }

    fillDrawRect(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawRect(args, util, this);
        }
        return RealtimeMode.fillDrawRect(args, util, this);
    }

    fillDrawRoundRect(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawRoundRect(args, util, this);
        }
        return RealtimeMode.fillDrawRoundRect(args, util, this);
    }

    fillDrawCircle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawCircle(args, util, this);
        }
        return RealtimeMode.fillDrawCircle(args, util, this);
    }

    fillDrawEllipse(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawEllipse(args, util, this);
        }
        return RealtimeMode.fillDrawEllipse(args, util, this);
    }

    fillDrawTriangle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawTriangle(args, util, this);
        }
        return RealtimeMode.fillDrawTriangle(args, util, this);
    }

    touchScreen(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.touchScreen(args, util, this);
        }
        return RealtimeMode.touchScreen(args, util, this);
    }

    getTouchValue(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getTouchValue(args, util, this);
        }
        return RealtimeMode.getTouchValue(args, util, this);
    }

    /*displayMatrix3 (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayMatrix3(args, util, this);
        }
        return RealtimeMode.displayMatrix3(args, util, this);
    }*/

    rgbColorDisplay(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.rgbColorDisplay(args, util, this);
        }
        return RealtimeMode.rgbColorDisplay(args, util, this);
    }
}

module.exports = tWatchDisplay;
