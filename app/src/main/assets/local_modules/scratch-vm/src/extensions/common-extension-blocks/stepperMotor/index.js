const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBiYXNlUHJvZmlsZT0idGlueSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiDQoJIHg9IjBweCIgeT0iMHB4IiB2aWV3Qm94PSIwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggaWQ9Ik1vdG9ySG91c2luZyIgZmlsbD0iIzRENEQ0RCIgZD0iTTEwLjEsMTguMXYxNC4zbDIuMiwxLjFoMTUuNGwyLjItMS4xVjE4LjFIMTAuMXoiLz4NCjxwYXRoIGlkPSJNb3RvckhvdXNpbmdTaGFkZSIgZmlsbD0iIzMzMzMzMyIgZD0iTTEwLjEsMjIuMnY3LjJsMi4yLDEuMXYtNy4yTDEwLjEsMjIuMnogTTI5LjksMjIuMWwtMi4yLDEuMXY3LjJsMi4yLTEuMVYyMi4xeiIvPg0KPHBhdGggaWQ9IkNhcFRvcCIgZmlsbD0iI0NDQ0NDQyIgZD0iTTkuOSw4LjlsMi40LTEuMmgxNS41bDIuNCwxLjJ2Ny44bC0yLjQsMS4ySDEyLjJsLTIuNC0xLjJWOC45eiIvPg0KPHBhdGggaWQ9IkNhcFRvcFNoYWRlIiBmaWxsPSIjQ0NDQ0NDIiBkPSJNMTIuMyw3LjhsLTIuMiwxLjF2Ny43bDIuMiwxLjFoMTUuM2wyLjItMS4xVjguOWwtMi4yLTEuMUgxMi4zeiIvPg0KPHBhdGggaWQ9IkNhcFRvcEdyb3ZlIiBmaWxsPSIjRjJGMkYyIiBzdHJva2U9IiNCM0IzQjMiIHN0cm9rZS13aWR0aD0iMC4xMzI3IiBkPSJNMjUuNiwxM2MwLDEuNS0yLjUsMi44LTUuNiwyLjhzLTUuNi0xLjMtNS42LTIuOA0KCXMyLjUtMi44LDUuNi0yLjhTMjUuNiwxMS41LDI1LjYsMTN6Ii8+DQo8cGF0aCBpZD0icGF0aDMxNzciIGZpbGw9IiMzMzMzMzMiIGQ9Ik0xMi44LDljMCwwLjItMC4zLDAuMy0wLjYsMC4zYy0wLjMsMC0wLjYtMC4xLTAuNi0wLjNjMC0wLjIsMC4zLTAuMywwLjYtMC4zDQoJQzEyLjYsOC43LDEyLjgsOC44LDEyLjgsOXoiLz4NCjxwYXRoIGlkPSJwYXRoMzE3N18xXyIgZmlsbD0iIzMzMzMzMyIgZD0iTTI4LjQsOWMwLDAuMi0wLjMsMC4zLTAuNiwwLjNjLTAuMywwLTAuNi0wLjEtMC42LTAuM2MwLTAuMiwwLjMtMC4zLDAuNi0wLjMNCglDMjguMSw4LjcsMjguNCw4LjgsMjguNCw5eiIvPg0KPHBhdGggaWQ9InBhdGgzMTc3XzJfIiBmaWxsPSIjMzMzMzMzIiBkPSJNMjguNCwxNi43YzAsMC4yLTAuMywwLjMtMC42LDAuM2MtMC4zLDAtMC42LTAuMS0wLjYtMC4zYzAtMC4yLDAuMy0wLjMsMC42LTAuMw0KCUMyOC4xLDE2LjQsMjguNCwxNi42LDI4LjQsMTYuN3oiLz4NCjxwYXRoIGlkPSJwYXRoMzE3N18zXyIgZmlsbD0iIzMzMzMzMyIgZD0iTTEyLjgsMTYuN2MwLDAuMi0wLjMsMC4zLTAuNiwwLjNjLTAuMywwLTAuNi0wLjEtMC42LTAuM2MwLTAuMiwwLjMtMC4zLDAuNi0wLjMNCglDMTIuNiwxNi40LDEyLjgsMTYuNiwxMi44LDE2Ljd6Ii8+DQo8cGF0aCBpZD0iQ2FwVG9wQmV2ZWwiIGZpbGw9IiM5OTk5OTkiIGQ9Ik0yNS4yLDEyLjdjMCwxLjQtMi4zLDIuNi01LjIsMi42cy01LjItMS4yLTUuMi0yLjZzMi4zLTIuNiw1LjItMi42UzI1LjIsMTEuMywyNS4yLDEyLjd6Ig0KCS8+DQo8cGF0aCBpZD0iQ2FwVG9wU2hhZnRIb2xlIiBmaWxsPSIjMzMzMzMzIiBkPSJNMjEuNiwxMi43YzAsMC41LTAuNywwLjgtMS42LDAuOHMtMS42LTAuNC0xLjYtMC44czAuNy0wLjgsMS42LTAuOA0KCVMyMS42LDEyLjMsMjEuNiwxMi43eiIvPg0KPHBhdGggaWQ9IkNhcCIgZmlsbD0iI0IzQjNCMyIgZD0iTTkuOSwxNi42djcuMmwyLjQsMS4ybDAuNS0wLjF2LTIuM2gxNC41djIuM2wwLjUsMC4xbDIuNC0xLjJ2LTcuMmwtMi40LDEuMkgxMi4yTDkuOSwxNi42eg0KCSBNOS45LDI1LjR2Ny4ybDIuNCwxLjJoMTUuNWwyLjQtMS4ydi03LjJsLTIuNCwxLjJMMjcuMiwyN3YxLjVIMTIuOFYyN2wtMC41LTAuNEw5LjksMjUuNHoiLz4NCjxwYXRoIGlkPSJDYXBTaGFkZSIgZmlsbD0iIzk5OTk5OSIgZD0iTTEyLjIsMjVsLTIuNC0xLjJ2LTcuMmwyLjQsMS4yVjI1eiBNMjcuOCwyNWwyLjQtMS4ydi03LjJsLTIuNCwxLjJWMjV6IE0yNy44LDMzLjdsMi40LTEuMg0KCXYtNy4ybC0yLjQsMS4yVjMzLjd6IE0xMi4yLDMzLjdsLTIuNC0xLjJ2LTcuMmwyLjQsMS4yVjMzLjd6Ii8+DQo8cGF0aCBpZD0iU2hhZnQiIGZpbGw9IiM2NjY2NjYiIGQ9Ik0yMS4yLDQuOHY4YzAsMC4zLTAuNSwwLjYtMS4yLDAuNnMtMS4yLTAuMy0xLjItMC42di04YzAtMC4zLDAuNS0wLjYsMS4yLTAuNlMyMS4yLDQuNCwyMS4yLDQuOA0KCXoiLz4NCjxwYXRoIGlkPSJTaGFmdFRvcCIgZmlsbD0iI0NDQ0NDQyIgZD0iTTIxLDQuNUMyMSw0LjgsMjAuNSw1LDIwLDVzLTEtMC4yLTEtMC41UzE5LjUsNCwyMCw0UzIxLDQuMiwyMSw0LjV6Ii8+DQo8cGF0aCBpZD0iU2hhZnRUb3BIb2xlIiBmaWxsPSIjNjY2NjY2IiBkPSJNMjAuNSw0LjVjMCwwLjEtMC4yLDAuMi0wLjUsMC4ycy0wLjUtMC4xLTAuNS0wLjJzMC4yLTAuMiwwLjUtMC4yUzIwLjUsNC40LDIwLjUsNC41eiIvPg0KPGcgaWQ9IlhNTElEXzFfIj4NCgk8cGF0aCBpZD0iQ2FwQ29ubmVjdG9yVG9wIiBmaWxsPSIjRTZFNkU2IiBkPSJNMTMuMywyOS4yaDEzLjVWMzFIMTMuM1YyOS4yeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3IiIGZpbGw9IiM5OTk5OTkiIGQ9Ik0xMy4zLDMxaDEzLjV2NEgxMy4zVjMxeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nVG9wIiBmaWxsPSIjMzMzMzMzIiBkPSJNMTQuMSwyOS42aDExLjlWMzFIMTQuMVYyOS42eiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nIiBmaWxsPSIjMzMzMzMzIiBkPSJNMTQuMSwzMWgxMS45djMuMkgxNC4xVjMxeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nSW5zZXQiIGZpbGw9IiM0RDRENEQiIGQ9Ik0xNC41LDMxaDExLjF2Mi40SDE0LjVWMzF6Ii8+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjAiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAsLTc2LjQ5OTk5OCkiPg0KCQk8cGF0aCBpZD0iUGluQmFzZSIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNMTUuNSwxMDcuNXYxLjgiLz4NCgkJPHBhdGggaWQ9IlBpbkhpbGl0ZSIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjRkZGRjAwIiBzdHJva2Utd2lkdGg9IjAuMTQyOCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNMTUuNSwxMDcuNXYxLjgiLz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjEiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDguOTk5OTk5OCwwKSI+DQoJCTxnIGlkPSJYTUxJRF8xMjU2MF8iIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAsLTc2LjQ5OTk5OCkiPg0KCQkJPHBhdGggaWQ9IlBpbkJhc2VfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzgwODAwMCIgc3Ryb2tlLXdpZHRoPSIwLjcxMzkiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTguMywxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTguMywxMDcuNXYxLjgiLz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iQ2FwQ29ubmVjdG9yUGluMiIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTgsMCkiPg0KCQk8ZyBpZD0iWE1MSURfMTI1NTlfIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLC03Ni40OTk5OTgpIj4NCgkJCTxwYXRoIGlkPSJQaW5CYXNlXzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiM4MDgwMDAiIHN0cm9rZS13aWR0aD0iMC43MTM5IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0xLjEsMTA3LjV2MS44Ii8+DQoJCQk8cGF0aCBpZD0iUGluSGlsaXRlXzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGMDAiIHN0cm9rZS13aWR0aD0iMC4xNDI4IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0xLjEsMTA3LjV2MS44Ii8+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjMiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDI3LDApIj4NCgkJPGcgaWQ9IlhNTElEXzEyNTU4XyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCwtNzYuNDk5OTk4KSI+DQoJCQk8cGF0aCBpZD0iUGluQmFzZV8zXyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNLTYuMSwxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfM18iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTS02LjEsMTA3LjV2MS44Ii8+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDM2LDApIj4NCgkJPGcgaWQ9IlhNTElEXzEyNTU3XyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCwtNzYuNDk5OTk4KSI+DQoJCQk8cGF0aCBpZD0iUGluQmFzZV80XyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNLTEzLjMsMTA3LjV2MS44Ii8+DQoJCQk8cGF0aCBpZD0iUGluSGlsaXRlXzRfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGMDAiIHN0cm9rZS13aWR0aD0iMC4xNDI4IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0tMTMuMywxMDcuNXYxLjgiLz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iQ2FwQ29ubmVjdG9yUGluNSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNDUsMCkiPg0KCQk8ZyBpZD0iWE1MSURfMTI1NTZfIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLC03Ni40OTk5OTgpIj4NCgkJCTxwYXRoIGlkPSJQaW5CYXNlXzVfIiBmaWxsPSJub25lIiBzdHJva2U9IiM4MDgwMDAiIHN0cm9rZS13aWR0aD0iMC43MTM5IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0tMjAuNSwxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfNV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTS0yMC41LDEwNy41djEuOCIvPg0KCQk8L2c+DQoJPC9nPg0KCTxnIGlkPSJKU1RDb25uZWN0b3JGIj4NCgkJPHBhdGggaWQ9IkhvdXNpbmdUb3AiIGZpbGw9IiNFM0UyREIiIGQ9Ik0xNC41LDMwLjRoMTEuMXYyLjJIMTQuNVYzMC40eiIvPg0KCQk8cGF0aCBpZD0iSG91c2luZyIgZmlsbD0iI0M4QzRCNyIgZD0iTTE0LjUsMzEuOGgxMS4xdjJIMTQuNVYzMS44eiIvPg0KCQk8cGF0aCBpZD0iSG91c2luZ1Nsb3RzIiBmaWxsPSIjNkM1RDUzIiBkPSJNMjMuOCwzMi4xaDEuNXYxLjVoLTEuNVYzMi4xeiBNMjAuMiwzMi4xaDEuNXYxLjVoLTEuNVYzMi4xeiBNMTguNCwzMi4xaDEuNXYxLjVoLTEuNQ0KCQkJVjMyLjF6IE0yMiwzMi4xaDEuNXYxLjVIMjJWMzIuMXogTTE0LjcsMzIuMWgxLjV2MS41aC0xLjVWMzIuMXogTTE2LjUsMzIuMUgxOHYxLjVoLTEuNVYzMi4xeiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBiYXNlUHJvZmlsZT0idGlueSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiDQoJIHg9IjBweCIgeT0iMHB4IiB2aWV3Qm94PSIwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggaWQ9Ik1vdG9ySG91c2luZyIgZmlsbD0iIzRENEQ0RCIgZD0iTTEwLjEsMTguMXYxNC4zbDIuMiwxLjFoMTUuNGwyLjItMS4xVjE4LjFIMTAuMXoiLz4NCjxwYXRoIGlkPSJNb3RvckhvdXNpbmdTaGFkZSIgZmlsbD0iIzMzMzMzMyIgZD0iTTEwLjEsMjIuMnY3LjJsMi4yLDEuMXYtNy4yTDEwLjEsMjIuMnogTTI5LjksMjIuMWwtMi4yLDEuMXY3LjJsMi4yLTEuMVYyMi4xeiIvPg0KPHBhdGggaWQ9IkNhcFRvcCIgZmlsbD0iI0NDQ0NDQyIgZD0iTTkuOSw4LjlsMi40LTEuMmgxNS41bDIuNCwxLjJ2Ny44bC0yLjQsMS4ySDEyLjJsLTIuNC0xLjJWOC45eiIvPg0KPHBhdGggaWQ9IkNhcFRvcFNoYWRlIiBmaWxsPSIjQ0NDQ0NDIiBkPSJNMTIuMyw3LjhsLTIuMiwxLjF2Ny43bDIuMiwxLjFoMTUuM2wyLjItMS4xVjguOWwtMi4yLTEuMUgxMi4zeiIvPg0KPHBhdGggaWQ9IkNhcFRvcEdyb3ZlIiBmaWxsPSIjRjJGMkYyIiBzdHJva2U9IiNCM0IzQjMiIHN0cm9rZS13aWR0aD0iMC4xMzI3IiBkPSJNMjUuNiwxM2MwLDEuNS0yLjUsMi44LTUuNiwyLjhzLTUuNi0xLjMtNS42LTIuOA0KCXMyLjUtMi44LDUuNi0yLjhTMjUuNiwxMS41LDI1LjYsMTN6Ii8+DQo8cGF0aCBpZD0icGF0aDMxNzciIGZpbGw9IiMzMzMzMzMiIGQ9Ik0xMi44LDljMCwwLjItMC4zLDAuMy0wLjYsMC4zYy0wLjMsMC0wLjYtMC4xLTAuNi0wLjNjMC0wLjIsMC4zLTAuMywwLjYtMC4zDQoJQzEyLjYsOC43LDEyLjgsOC44LDEyLjgsOXoiLz4NCjxwYXRoIGlkPSJwYXRoMzE3N18xXyIgZmlsbD0iIzMzMzMzMyIgZD0iTTI4LjQsOWMwLDAuMi0wLjMsMC4zLTAuNiwwLjNjLTAuMywwLTAuNi0wLjEtMC42LTAuM2MwLTAuMiwwLjMtMC4zLDAuNi0wLjMNCglDMjguMSw4LjcsMjguNCw4LjgsMjguNCw5eiIvPg0KPHBhdGggaWQ9InBhdGgzMTc3XzJfIiBmaWxsPSIjMzMzMzMzIiBkPSJNMjguNCwxNi43YzAsMC4yLTAuMywwLjMtMC42LDAuM2MtMC4zLDAtMC42LTAuMS0wLjYtMC4zYzAtMC4yLDAuMy0wLjMsMC42LTAuMw0KCUMyOC4xLDE2LjQsMjguNCwxNi42LDI4LjQsMTYuN3oiLz4NCjxwYXRoIGlkPSJwYXRoMzE3N18zXyIgZmlsbD0iIzMzMzMzMyIgZD0iTTEyLjgsMTYuN2MwLDAuMi0wLjMsMC4zLTAuNiwwLjNjLTAuMywwLTAuNi0wLjEtMC42LTAuM2MwLTAuMiwwLjMtMC4zLDAuNi0wLjMNCglDMTIuNiwxNi40LDEyLjgsMTYuNiwxMi44LDE2Ljd6Ii8+DQo8cGF0aCBpZD0iQ2FwVG9wQmV2ZWwiIGZpbGw9IiM5OTk5OTkiIGQ9Ik0yNS4yLDEyLjdjMCwxLjQtMi4zLDIuNi01LjIsMi42cy01LjItMS4yLTUuMi0yLjZzMi4zLTIuNiw1LjItMi42UzI1LjIsMTEuMywyNS4yLDEyLjd6Ig0KCS8+DQo8cGF0aCBpZD0iQ2FwVG9wU2hhZnRIb2xlIiBmaWxsPSIjMzMzMzMzIiBkPSJNMjEuNiwxMi43YzAsMC41LTAuNywwLjgtMS42LDAuOHMtMS42LTAuNC0xLjYtMC44czAuNy0wLjgsMS42LTAuOA0KCVMyMS42LDEyLjMsMjEuNiwxMi43eiIvPg0KPHBhdGggaWQ9IkNhcCIgZmlsbD0iI0IzQjNCMyIgZD0iTTkuOSwxNi42djcuMmwyLjQsMS4ybDAuNS0wLjF2LTIuM2gxNC41djIuM2wwLjUsMC4xbDIuNC0xLjJ2LTcuMmwtMi40LDEuMkgxMi4yTDkuOSwxNi42eg0KCSBNOS45LDI1LjR2Ny4ybDIuNCwxLjJoMTUuNWwyLjQtMS4ydi03LjJsLTIuNCwxLjJMMjcuMiwyN3YxLjVIMTIuOFYyN2wtMC41LTAuNEw5LjksMjUuNHoiLz4NCjxwYXRoIGlkPSJDYXBTaGFkZSIgZmlsbD0iIzk5OTk5OSIgZD0iTTEyLjIsMjVsLTIuNC0xLjJ2LTcuMmwyLjQsMS4yVjI1eiBNMjcuOCwyNWwyLjQtMS4ydi03LjJsLTIuNCwxLjJWMjV6IE0yNy44LDMzLjdsMi40LTEuMg0KCXYtNy4ybC0yLjQsMS4yVjMzLjd6IE0xMi4yLDMzLjdsLTIuNC0xLjJ2LTcuMmwyLjQsMS4yVjMzLjd6Ii8+DQo8cGF0aCBpZD0iU2hhZnQiIGZpbGw9IiM2NjY2NjYiIGQ9Ik0yMS4yLDQuOHY4YzAsMC4zLTAuNSwwLjYtMS4yLDAuNnMtMS4yLTAuMy0xLjItMC42di04YzAtMC4zLDAuNS0wLjYsMS4yLTAuNlMyMS4yLDQuNCwyMS4yLDQuOA0KCXoiLz4NCjxwYXRoIGlkPSJTaGFmdFRvcCIgZmlsbD0iI0NDQ0NDQyIgZD0iTTIxLDQuNUMyMSw0LjgsMjAuNSw1LDIwLDVzLTEtMC4yLTEtMC41UzE5LjUsNCwyMCw0UzIxLDQuMiwyMSw0LjV6Ii8+DQo8cGF0aCBpZD0iU2hhZnRUb3BIb2xlIiBmaWxsPSIjNjY2NjY2IiBkPSJNMjAuNSw0LjVjMCwwLjEtMC4yLDAuMi0wLjUsMC4ycy0wLjUtMC4xLTAuNS0wLjJzMC4yLTAuMiwwLjUtMC4yUzIwLjUsNC40LDIwLjUsNC41eiIvPg0KPGcgaWQ9IlhNTElEXzFfIj4NCgk8cGF0aCBpZD0iQ2FwQ29ubmVjdG9yVG9wIiBmaWxsPSIjRTZFNkU2IiBkPSJNMTMuMywyOS4yaDEzLjVWMzFIMTMuM1YyOS4yeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3IiIGZpbGw9IiM5OTk5OTkiIGQ9Ik0xMy4zLDMxaDEzLjV2NEgxMy4zVjMxeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nVG9wIiBmaWxsPSIjMzMzMzMzIiBkPSJNMTQuMSwyOS42aDExLjlWMzFIMTQuMVYyOS42eiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nIiBmaWxsPSIjMzMzMzMzIiBkPSJNMTQuMSwzMWgxMS45djMuMkgxNC4xVjMxeiIvPg0KCTxwYXRoIGlkPSJDYXBDb25uZWN0b3JIb3VzaW5nSW5zZXQiIGZpbGw9IiM0RDRENEQiIGQ9Ik0xNC41LDMxaDExLjF2Mi40SDE0LjVWMzF6Ii8+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjAiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAsLTc2LjQ5OTk5OCkiPg0KCQk8cGF0aCBpZD0iUGluQmFzZSIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNMTUuNSwxMDcuNXYxLjgiLz4NCgkJPHBhdGggaWQ9IlBpbkhpbGl0ZSIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjRkZGRjAwIiBzdHJva2Utd2lkdGg9IjAuMTQyOCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNMTUuNSwxMDcuNXYxLjgiLz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjEiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDguOTk5OTk5OCwwKSI+DQoJCTxnIGlkPSJYTUxJRF8xMjU2MF8iIHRyYW5zZm9ybT0idHJhbnNsYXRlKDAsLTc2LjQ5OTk5OCkiPg0KCQkJPHBhdGggaWQ9IlBpbkJhc2VfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzgwODAwMCIgc3Ryb2tlLXdpZHRoPSIwLjcxMzkiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTguMywxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTguMywxMDcuNXYxLjgiLz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iQ2FwQ29ubmVjdG9yUGluMiIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTgsMCkiPg0KCQk8ZyBpZD0iWE1MSURfMTI1NTlfIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLC03Ni40OTk5OTgpIj4NCgkJCTxwYXRoIGlkPSJQaW5CYXNlXzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiM4MDgwMDAiIHN0cm9rZS13aWR0aD0iMC43MTM5IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0xLjEsMTA3LjV2MS44Ii8+DQoJCQk8cGF0aCBpZD0iUGluSGlsaXRlXzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGMDAiIHN0cm9rZS13aWR0aD0iMC4xNDI4IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0xLjEsMTA3LjV2MS44Ii8+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjMiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDI3LDApIj4NCgkJPGcgaWQ9IlhNTElEXzEyNTU4XyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCwtNzYuNDk5OTk4KSI+DQoJCQk8cGF0aCBpZD0iUGluQmFzZV8zXyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNLTYuMSwxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfM18iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTS02LjEsMTA3LjV2MS44Ii8+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IkNhcENvbm5lY3RvclBpbjQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDM2LDApIj4NCgkJPGcgaWQ9IlhNTElEXzEyNTU3XyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCwtNzYuNDk5OTk4KSI+DQoJCQk8cGF0aCBpZD0iUGluQmFzZV80XyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjODA4MDAwIiBzdHJva2Utd2lkdGg9IjAuNzEzOSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBkPSJNLTEzLjMsMTA3LjV2MS44Ii8+DQoJCQk8cGF0aCBpZD0iUGluSGlsaXRlXzRfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGMDAiIHN0cm9rZS13aWR0aD0iMC4xNDI4IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0tMTMuMywxMDcuNXYxLjgiLz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iQ2FwQ29ubmVjdG9yUGluNSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNDUsMCkiPg0KCQk8ZyBpZD0iWE1MSURfMTI1NTZfIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLC03Ni40OTk5OTgpIj4NCgkJCTxwYXRoIGlkPSJQaW5CYXNlXzVfIiBmaWxsPSJub25lIiBzdHJva2U9IiM4MDgwMDAiIHN0cm9rZS13aWR0aD0iMC43MTM5IiBzdHJva2UtbGluZWNhcD0icm91bmQiIGQ9Ik0tMjAuNSwxMDcuNXYxLjgiLz4NCgkJCTxwYXRoIGlkPSJQaW5IaWxpdGVfNV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkYwMCIgc3Ryb2tlLXdpZHRoPSIwLjE0MjgiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTS0yMC41LDEwNy41djEuOCIvPg0KCQk8L2c+DQoJPC9nPg0KCTxnIGlkPSJKU1RDb25uZWN0b3JGIj4NCgkJPHBhdGggaWQ9IkhvdXNpbmdUb3AiIGZpbGw9IiNFM0UyREIiIGQ9Ik0xNC41LDMwLjRoMTEuMXYyLjJIMTQuNVYzMC40eiIvPg0KCQk8cGF0aCBpZD0iSG91c2luZyIgZmlsbD0iI0M4QzRCNyIgZD0iTTE0LjUsMzEuOGgxMS4xdjJIMTQuNVYzMS44eiIvPg0KCQk8cGF0aCBpZD0iSG91c2luZ1Nsb3RzIiBmaWxsPSIjNkM1RDUzIiBkPSJNMjMuOCwzMi4xaDEuNXYxLjVoLTEuNVYzMi4xeiBNMjAuMiwzMi4xaDEuNXYxLjVoLTEuNVYzMi4xeiBNMTguNCwzMi4xaDEuNXYxLjVoLTEuNQ0KCQkJVjMyLjF6IE0yMiwzMi4xaDEuNXYxLjVIMjJWMzIuMXogTTE0LjcsMzIuMWgxLjV2MS41aC0xLjVWMzIuMXogTTE2LjUsMzIuMUgxOHYxLjVoLTEuNVYzMi4xeiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';


class stepperMotor {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'stepperMotor';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const stepperMotorState = sourceTarget.getCustomState(stepperMotor.STATE_KEY);
            if (stepperMotorState) {
                newTarget.setCustomState(stepperMotor.STATE_KEY, Clone.simple(stepperMotorState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    get ANALOG_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.analogPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.analogPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.analogPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.analogPins.evive;
            }
        }
        return ['0'];
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
        }
        return ['0'];
    }

    get PWM_PINS() {
        switch (this.runtime.boardSelected) {
            case 'Arduino Nano': {
                return ExtensionMenu.pwmPins.arduinoNano;
            }
            case 'Arduino Mega': {
                return ExtensionMenu.pwmPins.arduinoMega;
            }
            case 'Arduino Uno': {
                return ExtensionMenu.pwmPins.arduinoUno;
            }
            case 'evive': {
                return ExtensionMenu.pwmPins.evive;
            }
        }
        return ['0'];
    }

    getInfo() {
        return {
            id: 'stepperMotor',
            name: formatMessage({
                id: 'stepperMotor.stepperMotor',
                default: 'Stepper Motor (U)',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5fbc2e',
            colourSecondary: '#41a80b',
            colourTertiary: '#3d9907',
            blocks: [
                {
                    message: formatMessage({
                        id: 'stepperMotor.blockSeparatorMessage1',
                        default: 'Stepper Motor using A4988 Driver',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initialiseMultiStepperA4988',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.initialiseMultiStepperA4988',
                        default: 'initialize stepper [STEPPER] at DIR [DIR] & STEP [STEP]',
                        description: 'Initialise Multi Stepper Motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        DIR: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        },
                        STEP: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        }
                    }
                },
                {
                    opcode: 'setMultiStepperA4988',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.setMultiStepperA4988',
                        default: 'set [OPERATION] of stepper [STEPPER] to [POSITION]',
                        description: 'operation'
                    }),
                    arguments: {
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperOptions1',
                            defaultValue: '1'
                        },
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        }
                    }
                },
                '---',
                {
                    opcode: 'moveMultiStepperA4988',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.moveMultiStepperA4988',
                        default: 'move stepper [STEPPER] [DIRECTION] by [QUANTITY]',
                        description: 'move stepper motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperDirection',
                            defaultValue: '1'
                        },
                        QUANTITY: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '90'
                        }
                    }
                },
                {
                    opcode: 'moveMultiStepperA4988Position',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.moveMultiStepperA4988Position',
                        default: 'move stepper [STEPPER] to [POSITION] position',
                        description: 'move stepper motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'runMultiStepperA4988',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.runMultiStepperA4988',
                        default: '[OPERATION] stepper [STEPPER]',
                        description: 'run stepper motor'
                    }),
                    arguments: {
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperOptions3',
                            defaultValue: '1'
                        },
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        }
                    }
                },
                '---',
                {
                    opcode: 'getMultiStepperA4988',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'stepperMotor.getMultiStepperA4988',
                        default: 'get [OPERATION] of stepper [STEPPER]',
                        description: 'get stepper motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperOptions2',
                            defaultValue: '1'
                        },
                    }
                },
                {
                    message: formatMessage({
                        id: 'stepperMotor.blockSeparatorMessage2',
                        default: '28BYJ-48 Stepper Motor',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'initialiseStepper',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.initialiseStepper',
                        default: 'initialise stepper [STEPPER] at IN1 [IN1] IN2 [IN2] IN3 [IN3] IN4 [IN4]',
                        description: 'Initialise Stepper Motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        IN1: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        },
                        IN2: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        IN3: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '4'
                        },
                        IN4: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        }
                    }
                },
                {
                    opcode: 'moveStepper',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'stepperMotor.moveStepper',
                        default: 'move stepper [STEPPER] [DIRECTION] by [QUANTITY] [CHOICE] & [RPM] RPM',
                        description: 'move stepper motor'
                    }),
                    arguments: {
                        STEPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChannels',
                            defaultValue: '1'
                        },
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperDirection',
                            defaultValue: '1'
                        },
                        QUANTITY: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '90'
                        },
                        CHOICE: {
                            type: ArgumentType.NUMBER,
                            menu: 'stepperChoice',
                            defaultValue: '1'
                        },
                        RPM: {
                            type: ArgumentType.MATHSLIDER200,
                            defaultValue: '10'
                        }
                    }
                }
            ],
            menus: {
                analogPins: this.ANALOG_PINS,
                digitalPins: this.DIGITAL_PINS,
                pwmPins: this.PWM_PINS,
                stepperChannels: [
                    '1', '2', '3', '4', '5'
                ],
                stepperDirection: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option3',
                            default: 'clockwise',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.commonFormattedMessages.option4',
                            default: 'anti-clockwise',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                stepperChoice: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.stepperMotor.stepperChoice.option1',
                            default: 'degrees',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.stepperMotor.stepperChoice.option2',
                            default: 'steps',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                stepperEnable: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.stepperMotor.stepperEnable.option1',
                            default: 'enable',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.stepperMotor.stepperEnable.option2',
                            default: 'disable',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                stepperOptions1: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option1',
                            default: 'max speed',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option2',
                            default: 'speed',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option3',
                            default: 'acceleration',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                stepperOptions2: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option1',
                            default: 'max speed',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option2',
                            default: 'speed',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option4',
                            default: 'distance from target position',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option5',
                            default: 'target position',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option6',
                            default: 'current position',
                            description: 'Menu'
                        }), value: '5'
                    }
                ],
                stepperOptions3: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option7',
                            default: 'run to target position',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option8',
                            default: 'run at target speed',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.speedFormattedMessages.option9',
                            default: 'stop',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
            }
        };
    }

    initialiseStepper(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseStepper(args, util, this);
        }
        return RealtimeMode.initialiseStepper(args, util, this);
    }

    moveStepper(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveStepper(args, util, this);
        }
        return RealtimeMode.moveStepper(args, util, this);
    }

    initialiseMultiStepperA4988(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseMultiStepperA4988(args, util, this);
        }
        return RealtimeMode.initialiseMultiStepperA4988(args, util, this);
    }

    moveMultiStepperA4988(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveMultiStepperA4988(args, util, this);
        }
        return RealtimeMode.moveMultiStepperA4988(args, util, this);
    }

    moveMultiStepperA4988Position(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveMultiStepperA4988Position(args, util, this);
        }
        return RealtimeMode.moveMultiStepperA4988Position(args, util, this);
    }

    runMultiStepperA4988(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.runMultiStepperA4988(args, util, this);
        }
        return RealtimeMode.runMultiStepperA4988(args, util, this);
    }

    setMultiStepperA4988(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setMultiStepperA4988(args, util, this);
        }
        return RealtimeMode.setMultiStepperA4988(args, util, this);
    }

    getMultiStepperA4988(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getMultiStepperA4988(args, util, this);
        }
        return RealtimeMode.getMultiStepperA4988(args, util, this);
    }

}

module.exports = stepperMotor;
