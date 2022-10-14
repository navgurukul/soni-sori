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
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBiYXNlUHJvZmlsZT0idGlueSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiDQoJIHg9IjBweCIgeT0iMHB4IiB2aWV3Qm94PSIwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPGcgaWQ9IlhNTElEXzEyNTU1XyI+DQoJPGcgaWQ9IlhNTElEXzEyODk4XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjkwMF8iIGZpbGw9IiNFMEIwMDAiIGQ9Ik0xNC44LDM1LjV2LTYuNGMwLTIuNy0yLjItNC45LTQuOS00LjljLTIuNywwLTQuOSwyLjItNC45LDQuOXY2LjRIMTQuOHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk5XyIgZmlsbD0iI0ZGRDUwMCIgZD0iTTE0LjEsMzUuNXYtNi40YzAtMi43LTIuMi00LjktNC45LTQuOWMtMi43LDAtNC45LDIuMi00LjksNC45djYuNEgxNC4xeiIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMTI4OTVfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk3XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTUuOCwyOC4xYy0wLjMsMC42LTEsMC44LTEuNiwwLjVsMCwwYy0wLjYtMC4zLTAuOC0xLTAuNS0xLjZMMTUuMiw2LjENCgkJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkw1LjgsMjguMXoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk2XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTEwLjYsMzAuMmMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDIwLDguMQ0KCQkJYzAuMy0wLjYsMS0wLjgsMS42LTAuNWwwLDBjMC42LDAuMywwLjgsMSwwLjUsMS42TDEwLjYsMzAuMnoiLz4NCgk8L2c+DQoJPHBhdGggaWQ9IlhNTElEXzEyODk0XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTYsMjguMmMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDE1LjQsNi4yDQoJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkw2LDI4LjJ6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyODkzXyIgZmlsbD0iIzJFMkYyRSIgZD0iTTEwLjcsMzAuMWMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDIwLjEsOC4xDQoJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkwxMC43LDMwLjF6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyODkxXyIgZmlsbD0iIzQ1NDY0NSIgZD0iTTQuOSwyOC40Yy0wLjMsMC42LTEsMC44LTEuNiwwLjVsMCwwYy0wLjYtMC4zLTAuOC0xLTAuNS0xLjZMMTQuMyw2LjMNCgkJYzAuMy0wLjYsMS0wLjgsMS42LTAuNWwwLDBjMC42LDAuMywwLjgsMSwwLjUsMS42TDQuOSwyOC40eiIvPg0KCTxwYXRoIGlkPSJYTUxJRF8xMjg5MF8iIGZpbGw9IiM0NTQ2NDUiIGQ9Ik05LjYsMzAuM2MtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDE5LDguMg0KCQljMC4zLTAuNiwxLTAuOCwxLjYtMC41bDAsMGMwLjYsMC4zLDAuOCwxLDAuNSwxLjZMOS42LDMwLjN6Ii8+DQoJPGcgaWQ9IlhNTElEXzEyODQ4XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjg1MF8iIGZpbGw9IiNFMEIwMDAiIGQ9Ik0xMiwzNS41di02LjRjMC0yLjctMi4yLTQuOS00LjktNC45Yy0yLjcsMC00LjksMi4yLTQuOSw0Ljl2Ni40SDEyeiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMTI4NDlfIiBmaWxsPSIjRkZENTAwIiBkPSJNMTEuMSwzNS41di02LjRjMC0yLjctMi4yLTQuOS00LjktNC45Yy0yLjcsMC00LjksMi4yLTQuOSw0Ljl2Ni40SDExLjF6Ii8+DQoJPC9nPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjcyOV8iIHg9IjAiIHk9IjMzLjUiIGZpbGw9IiMyRTJGMkUiIHdpZHRoPSIxNi4xIiBoZWlnaHQ9IjIiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI3MzVfIiB4PSIxOS4zIiB5PSI3IiBmaWxsPSIjNDU0NjQ1IiB3aWR0aD0iMTUuOSIgaGVpZ2h0PSIzLjYiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MzJfIiB4PSIzMi45IiB5PSIxNC43IiBmaWxsPSIjQ0M4MTAwIiB3aWR0aD0iNS45IiBoZWlnaHQ9IjEuMyIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODMyXyIgZmlsbD0iI0ZGRDUwMCIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjQuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjkyOF8iIHg9IjM0LjEiIHk9IjEwLjYiIGZpbGw9IiMyRTJGMkUiIHdpZHRoPSIzLjciIGhlaWdodD0iNS4xIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzEyOTI2XyIgeD0iMzMuNSIgeT0iMTAuNiIgZmlsbD0iIzQ1NDY0NSIgd2lkdGg9IjMuNyIgaGVpZ2h0PSI1LjEiLz4NCgk8ZyBpZD0iWE1MSURfMTI4MzFfIj4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTI4MzRfIiBmaWxsPSIjNEQ0RDREIiBjeD0iNiIgY3k9IjI4LjciIHI9IjIuNCIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8xMjgzM18iIGZpbGw9IiMyRTJGMkUiIGN4PSI2IiBjeT0iMjguNyIgcj0iMS44Ii8+DQoJPC9nPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODg2XyIgZmlsbD0iI0ZGRDUwMCIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjMuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODUzXyIgZmlsbD0iIzRENEQ0RCIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjIuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODUyXyIgZmlsbD0iIzJFMkYyRSIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjEuOCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODI2XyIgZmlsbD0iIzRENEQ0RCIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjIuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODI1XyIgZmlsbD0iIzJFMkYyRSIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjEuOCIvPg0KCTxnIGlkPSJYTUxJRF8xMjU2Nl8iPg0KCQk8cG9seWdvbiBpZD0iWE1MSURfMTI5MTVfIiBmaWxsPSIjMkUyRjJFIiBwb2ludHM9IjM1LjMsMjEuNSAzNC41LDE5LjkgMzQuNSwxNi44IDMzLjEsMTYuOCAzMy40LDIyIDM0LDIyIAkJIi8+DQoJCTxwb2x5Z29uIGlkPSJYTUxJRF8xMjkxMl8iIGZpbGw9IiMyRTJGMkUiIHBvaW50cz0iMzcuNSwxNi44IDM3LjUsMTkuOSAzNy40LDIyIDM3LjksMjIgMzguOSwyMC4xIDM4LjksMjAuMSAzOC45LDE2LjggCQkiLz4NCgkJPHBvbHlnb24gaWQ9IlhNTElEXzEyOTA4XyIgZmlsbD0iIzRFNEM0RiIgcG9pbnRzPSIzNC43LDIxLjUgMzMuOSwxOS45IDMzLjksMTYuOCAzMi41LDE2LjggMzIuNSwyMC4xIDMyLjUsMjAuMSAzMy40LDIyIAkJIi8+DQoJCTxwb2x5Z29uIGlkPSJYTUxJRF8xMjkwNV8iIGZpbGw9IiM0RTRDNEYiIHBvaW50cz0iMzYuOSwxNi44IDM2LjksMTkuOSAzNi4xLDIxLjUgMzcuNCwyMiAzOC4zLDIwLjEgMzguMywyMC4xIDM4LjMsMTYuOCAJCSIvPg0KCTwvZz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MjJfIiB4PSIzNi4xIiB5PSIxNC43IiBmaWxsPSIjQ0M4MTAwIiB3aWR0aD0iMyIgaGVpZ2h0PSIxLjMiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MTlfIiB4PSIzMi4zIiB5PSIxNC43IiBmaWxsPSIjRkZCMzAwIiB3aWR0aD0iNS40IiBoZWlnaHQ9IjEuMyIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjg0N18iIHg9IjMxLjQiIHk9IjE1LjciIGZpbGw9IiNGRkUwMDEiIHdpZHRoPSI4LjYiIGhlaWdodD0iMS45Ii8+DQoJPHJlY3QgaWQ9IlhNTElEXzEyNTUxXyIgeD0iMzgiIHk9IjE1LjciIGZpbGw9IiNFQUM4MDAiIHdpZHRoPSIyIiBoZWlnaHQ9IjEuOSIvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBiYXNlUHJvZmlsZT0idGlueSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiDQoJIHg9IjBweCIgeT0iMHB4IiB2aWV3Qm94PSIwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPGcgaWQ9IlhNTElEXzEyNTU1XyI+DQoJPGcgaWQ9IlhNTElEXzEyODk4XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjkwMF8iIGZpbGw9IiNFMEIwMDAiIGQ9Ik0xNC44LDM1LjV2LTYuNGMwLTIuNy0yLjItNC45LTQuOS00LjljLTIuNywwLTQuOSwyLjItNC45LDQuOXY2LjRIMTQuOHoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk5XyIgZmlsbD0iI0ZGRDUwMCIgZD0iTTE0LjEsMzUuNXYtNi40YzAtMi43LTIuMi00LjktNC45LTQuOWMtMi43LDAtNC45LDIuMi00LjksNC45djYuNEgxNC4xeiIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMTI4OTVfIj4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk3XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTUuOCwyOC4xYy0wLjMsMC42LTEsMC44LTEuNiwwLjVsMCwwYy0wLjYtMC4zLTAuOC0xLTAuNS0xLjZMMTUuMiw2LjENCgkJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkw1LjgsMjguMXoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEyODk2XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTEwLjYsMzAuMmMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDIwLDguMQ0KCQkJYzAuMy0wLjYsMS0wLjgsMS42LTAuNWwwLDBjMC42LDAuMywwLjgsMSwwLjUsMS42TDEwLjYsMzAuMnoiLz4NCgk8L2c+DQoJPHBhdGggaWQ9IlhNTElEXzEyODk0XyIgZmlsbD0iIzJFMkYyRSIgZD0iTTYsMjguMmMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDE1LjQsNi4yDQoJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkw2LDI4LjJ6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyODkzXyIgZmlsbD0iIzJFMkYyRSIgZD0iTTEwLjcsMzAuMWMtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDIwLjEsOC4xDQoJCWMwLjMtMC42LDEtMC44LDEuNi0wLjVsMCwwYzAuNiwwLjMsMC44LDEsMC41LDEuNkwxMC43LDMwLjF6Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzEyODkxXyIgZmlsbD0iIzQ1NDY0NSIgZD0iTTQuOSwyOC40Yy0wLjMsMC42LTEsMC44LTEuNiwwLjVsMCwwYy0wLjYtMC4zLTAuOC0xLTAuNS0xLjZMMTQuMyw2LjMNCgkJYzAuMy0wLjYsMS0wLjgsMS42LTAuNWwwLDBjMC42LDAuMywwLjgsMSwwLjUsMS42TDQuOSwyOC40eiIvPg0KCTxwYXRoIGlkPSJYTUxJRF8xMjg5MF8iIGZpbGw9IiM0NTQ2NDUiIGQ9Ik05LjYsMzAuM2MtMC4zLDAuNi0xLDAuOC0xLjYsMC41bDAsMGMtMC42LTAuMy0wLjgtMS0wLjUtMS42TDE5LDguMg0KCQljMC4zLTAuNiwxLTAuOCwxLjYtMC41bDAsMGMwLjYsMC4zLDAuOCwxLDAuNSwxLjZMOS42LDMwLjN6Ii8+DQoJPGcgaWQ9IlhNTElEXzEyODQ4XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMjg1MF8iIGZpbGw9IiNFMEIwMDAiIGQ9Ik0xMiwzNS41di02LjRjMC0yLjctMi4yLTQuOS00LjktNC45Yy0yLjcsMC00LjksMi4yLTQuOSw0Ljl2Ni40SDEyeiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMTI4NDlfIiBmaWxsPSIjRkZENTAwIiBkPSJNMTEuMSwzNS41di02LjRjMC0yLjctMi4yLTQuOS00LjktNC45Yy0yLjcsMC00LjksMi4yLTQuOSw0Ljl2Ni40SDExLjF6Ii8+DQoJPC9nPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjcyOV8iIHg9IjAiIHk9IjMzLjUiIGZpbGw9IiMyRTJGMkUiIHdpZHRoPSIxNi4xIiBoZWlnaHQ9IjIiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI3MzVfIiB4PSIxOS4zIiB5PSI3IiBmaWxsPSIjNDU0NjQ1IiB3aWR0aD0iMTUuOSIgaGVpZ2h0PSIzLjYiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MzJfIiB4PSIzMi45IiB5PSIxNC43IiBmaWxsPSIjQ0M4MTAwIiB3aWR0aD0iNS45IiBoZWlnaHQ9IjEuMyIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODMyXyIgZmlsbD0iI0ZGRDUwMCIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjQuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjkyOF8iIHg9IjM0LjEiIHk9IjEwLjYiIGZpbGw9IiMyRTJGMkUiIHdpZHRoPSIzLjciIGhlaWdodD0iNS4xIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzEyOTI2XyIgeD0iMzMuNSIgeT0iMTAuNiIgZmlsbD0iIzQ1NDY0NSIgd2lkdGg9IjMuNyIgaGVpZ2h0PSI1LjEiLz4NCgk8ZyBpZD0iWE1MSURfMTI4MzFfIj4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTI4MzRfIiBmaWxsPSIjNEQ0RDREIiBjeD0iNiIgY3k9IjI4LjciIHI9IjIuNCIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8xMjgzM18iIGZpbGw9IiMyRTJGMkUiIGN4PSI2IiBjeT0iMjguNyIgcj0iMS44Ii8+DQoJPC9nPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODg2XyIgZmlsbD0iI0ZGRDUwMCIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjMuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODUzXyIgZmlsbD0iIzRENEQ0RCIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjIuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODUyXyIgZmlsbD0iIzJFMkYyRSIgY3g9IjM1LjYiIGN5PSI4LjgiIHI9IjEuOCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODI2XyIgZmlsbD0iIzRENEQ0RCIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjIuNCIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzEyODI1XyIgZmlsbD0iIzJFMkYyRSIgY3g9IjE3LjYiIGN5PSI4LjgiIHI9IjEuOCIvPg0KCTxnIGlkPSJYTUxJRF8xMjU2Nl8iPg0KCQk8cG9seWdvbiBpZD0iWE1MSURfMTI5MTVfIiBmaWxsPSIjMkUyRjJFIiBwb2ludHM9IjM1LjMsMjEuNSAzNC41LDE5LjkgMzQuNSwxNi44IDMzLjEsMTYuOCAzMy40LDIyIDM0LDIyIAkJIi8+DQoJCTxwb2x5Z29uIGlkPSJYTUxJRF8xMjkxMl8iIGZpbGw9IiMyRTJGMkUiIHBvaW50cz0iMzcuNSwxNi44IDM3LjUsMTkuOSAzNy40LDIyIDM3LjksMjIgMzguOSwyMC4xIDM4LjksMjAuMSAzOC45LDE2LjggCQkiLz4NCgkJPHBvbHlnb24gaWQ9IlhNTElEXzEyOTA4XyIgZmlsbD0iIzRFNEM0RiIgcG9pbnRzPSIzNC43LDIxLjUgMzMuOSwxOS45IDMzLjksMTYuOCAzMi41LDE2LjggMzIuNSwyMC4xIDMyLjUsMjAuMSAzMy40LDIyIAkJIi8+DQoJCTxwb2x5Z29uIGlkPSJYTUxJRF8xMjkwNV8iIGZpbGw9IiM0RTRDNEYiIHBvaW50cz0iMzYuOSwxNi44IDM2LjksMTkuOSAzNi4xLDIxLjUgMzcuNCwyMiAzOC4zLDIwLjEgMzguMywyMC4xIDM4LjMsMTYuOCAJCSIvPg0KCTwvZz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MjJfIiB4PSIzNi4xIiB5PSIxNC43IiBmaWxsPSIjQ0M4MTAwIiB3aWR0aD0iMyIgaGVpZ2h0PSIxLjMiLz4NCgk8cmVjdCBpZD0iWE1MSURfMTI5MTlfIiB4PSIzMi4zIiB5PSIxNC43IiBmaWxsPSIjRkZCMzAwIiB3aWR0aD0iNS40IiBoZWlnaHQ9IjEuMyIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xMjg0N18iIHg9IjMxLjQiIHk9IjE1LjciIGZpbGw9IiNGRkUwMDEiIHdpZHRoPSI4LjYiIGhlaWdodD0iMS45Ii8+DQoJPHJlY3QgaWQ9IlhNTElEXzEyNTUxXyIgeD0iMzgiIHk9IjE1LjciIGZpbGw9IiNFQUM4MDAiIHdpZHRoPSIyIiBoZWlnaHQ9IjEuOSIvPg0KPC9nPg0KPC9zdmc+DQo=';


class roboticArm {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
        this.extensionName = 'roboticArm';
        BoardConfig.registerBoardSpecificExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS, this.getDefaultInfo());
        BoardConfig.registerBoardSpecificExtraExtensionList(this.extensionName, this.runtime, this.EXTENSION_SPECIFIC_BLOCKS);
    }

    static get STATE_KEY() {
        return 'roboticArm';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const roboticArmState = sourceTarget.getCustomState(roboticArm.STATE_KEY);
            if (roboticArmState) {
                newTarget.setCustomState(roboticArm.STATE_KEY, Clone.simple(roboticArmState));
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

    getDefaultInfo(extensionId) {
        return {
            id: 'roboticArm',
            name: formatMessage({
                id: 'roboticArm.roboticArm',
                default: 'Robotic Arm (4-axis)',
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
                        id: 'roboticArm.uploadFirmware',
                        default: 'upload stage mode firmware',
                        description: 'upload firmware'
                    }),
                },
                '---',
                {
                    opcode: 'initialise2',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.initialise2',
                        default: 'connect servos - base [BASE] link 1 [LINK1] link 2 [LINK2] rotational [ROT] gripper [GRIPPER]',
                        description: 'initialise Robotic Arm'
                    }),
                    arguments: {
                        BASE: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        },
                        LINK1: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        LINK2: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '4'
                        },
                        ROT: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '5'
                        },
                        GRIPPER: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '6'
                        }
                    }
                },
                {
                    opcode: 'setTrim',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.setTrim',
                        default: 'calibrate angles - base [BASE] link 1 [LINK1] link 2 [LINK2] rotational [ROT] gripper [GRIPPER]',
                        description: 'Calibrate Robotic Arm'
                    }),
                    arguments: {
                        BASE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        LINK1: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        LINK2: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        ROT: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        GRIPPER: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'initialiseGripper',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.initialiseGripper',
                        default: 'set gripper open angle to [OPEN] & close angle to [CLOSE]',
                        description: 'initialise Robotic Arm Gripper'
                    }),
                    arguments: {
                        OPEN: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '30'
                        },
                        CLOSE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        }
                    }
                },
                {
                    opcode: 'setOffset',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.setOffset',
                        default: 'set offset along length [LENGTH] & Z [ZOFFSET]',
                        description: 'set offset'
                    }),
                    arguments: {
                        LENGTH: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        ZOFFSET: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        }
                    }
                },
                '---',
                {
                    opcode: 'gotoXYZ',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.gotoXYZ',
                        default: 'go to X [XPOS] Y [YPOS] Z[ZPOS] in [SEC] ms',
                        description: 'Go to XYZ position'
                    }),
                    arguments: {
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '200'
                        },
                        ZPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '150'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1000'
                        }
                    }
                },
                {
                    opcode: 'gotoXYZinLine',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.gotoXYZinLine',
                        default: 'go to X [XPOS] Y [YPOS] Z[ZPOS] in [SEC] ms in line',
                        description: 'Go to XYZ position'
                    }),
                    arguments: {
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '200'
                        },
                        ZPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '150'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1000'
                        }
                    }
                },
                {
                    opcode: 'moveInCircle',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.moveInCircle',
                        default: 'move in [DIRECTION] circle of center X [XPOS] Z [ZPOS], radius [RADIUS] & along Y [YPOS] in [SEC] ms',
                        description: 'Go to XYZ position'
                    }),
                    arguments: {
                        DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'circleDirection',
                            defaultValue: '1'
                        },
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '200'
                        },
                        ZPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '150'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '50'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '4000'
                        }
                    }
                },
                {
                    opcode: 'moveInArc',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.moveInArc',
                        default: 'move in arc with center X [XPOS], Z [ZPOS], radius [RADIUS], start angle [START], end angle [END], along Y [YPOS] in [SEC] ms',
                        description: 'Arc '
                    }),
                    arguments: {
                        XPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        YPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '200'
                        },
                        ZPOS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '150'
                        },
                        RADIUS: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '50'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '4000'
                        },
                        START: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '90'
                        },
                        END: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '270'
                        }
                    }
                },
                '---',
                {
                    opcode: 'goToInOneAxis',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.goToInOneAxis',
                        default: 'go to [POSITION] in [AXIS] in [SEC] ms',
                        description: 'Go to XYZ position'
                    }),
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '150'
                        },
                        AXIS: {
                            type: ArgumentType.NUMBER,
                            menu: 'carPosition',
                            defaultValue: '1'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1000'
                        }
                    }
                },
                {
                    opcode: 'moveByInOneAxis',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.moveByInOneAxis',
                        default: 'move [POSITION] in [AXIS] in [SEC] ms',
                        description: 'Go to XYZ position'
                    }),
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '20'
                        },
                        AXIS: {
                            type: ArgumentType.NUMBER,
                            menu: 'carPosition',
                            defaultValue: '1'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1000'
                        }
                    }
                },
                {
                    opcode: 'setServoAngleTo',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.setServoAngleTo',
                        default: 'set [SERVO] servo angle to [ANGLE] in [SEC] ms',
                        description: 'Set servo angle'
                    }),
                    arguments: {
                        SERVO: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoPosition',
                            defaultValue: '0'
                        },
                        ANGLE: {
                            type: ArgumentType.MATHSLIDER180,
                            defaultValue: '90'
                        },
                        SEC: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1000'
                        }
                    }
                },
                {
                    opcode: 'controlGripper',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'roboticArm.controlGripper',
                        default: '[STATE] gripper',
                        description: 'Set servo angle'
                    }),
                    arguments: {
                        STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'gripperState',
                            defaultValue: '0'
                        }
                    }
                },
                '---',
                {
                    opcode: 'getServoAngle',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'roboticArm.getServoAngle',
                        default: 'get current [SERVO] servo angle',
                        description: 'get servo angle'
                    }),
                    arguments: {
                        SERVO: {
                            type: ArgumentType.NUMBER,
                            menu: 'servoPosition',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getCurrentPosition',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'roboticArm.getCurrentPostion',
                        default: 'get current postion in [POSITION]',
                        description: 'get servo angle'
                    }),
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'carPosition',
                            defaultValue: '1'
                        }
                    }
                },
            ],
            menus: {
                analogPins: this.ANALOG_PINS,
                digitalPins: this.DIGITAL_PINS,
                pwmPins: this.PWM_PINS,
                servoPosition: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.servoPosition.option1',
                            default: 'base',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.servoPosition.option2',
                            default: 'link 1',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.servoPosition.option3',
                            default: 'link 2',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.servoPosition.option4',
                            default: 'rotational',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.servoPosition.option5',
                            default: 'gripper',
                            description: 'Menu'
                        }), value: '4'
                    }
                ],
                carPosition: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.carPosition.option1',
                            default: 'X axis',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.carPosition.option2',
                            default: 'Y axis',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.carPosition.option3',
                            default: 'Z axis',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                gripperState: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.gripperState.option1',
                            default: 'open',
                            description: 'Menu'
                        }), value: '0'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.roboticArm.gripperState.option2',
                            default: 'close',
                            description: 'Menu'
                        }), value: '1'
                    },
                ],
                circleDirection: [
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
                    },
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

    initialise(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialise(args, util, this);
        }
        return RealtimeMode.initialise(args, util, this);
    }

    initialise2(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialise2(args, util, this);
        }
        return RealtimeMode.initialise2(args, util, this);
    }

    gotoXYZ(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.gotoXYZ(args, util, this);
        }
        return RealtimeMode.gotoXYZ(args, util, this);
    }

    gotoXYZinLine(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.gotoXYZinLine(args, util, this);
        }
        return RealtimeMode.gotoXYZinLine(args, util, this);
    }

    setServoAngleTo(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setServoAngleTo(args, util, this);
        }
        return RealtimeMode.setServoAngleTo(args, util, this);
    }

    goToInOneAxis(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.goToInOneAxis(args, util, this);
        }
        return RealtimeMode.goToInOneAxis(args, util, this);
    }

    moveByInOneAxis(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveByInOneAxis(args, util, this);
        }
        return RealtimeMode.moveByInOneAxis(args, util, this);
    }

    getServoAngle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getServoAngle(args, util, this);
        }
        return RealtimeMode.getServoAngle(args, util, this);
    }

    getCurrentPosition(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getCurrentPosition(args, util, this);
        }
        return RealtimeMode.getCurrentPosition(args, util, this);
    }

    initialiseGripper(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.initialiseGripper(args, util, this);
        }
        return RealtimeMode.initialiseGripper(args, util, this);
    }

    controlGripper(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.controlGripper(args, util, this);
        }
        return RealtimeMode.controlGripper(args, util, this);
    }

    moveInCircle(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveInCircle(args, util, this);
        }
        return RealtimeMode.moveInCircle(args, util, this);
    }

    moveInArc(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.moveInArc(args, util, this);
        }
        return RealtimeMode.moveInArc(args, util, this);
    }

    uploadFirmware(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.uploadFirmware(args, util, this);
        }
        return RealtimeMode.uploadFirmware(args, util, this);
    }

    setOffset(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setOffset(args, util, this);
        }
        return RealtimeMode.setOffset(args, util, this);
    }

    setTrim(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setTrim(args, util, this);
        }
        return RealtimeMode.setTrim(args, util, this);
    }

}

module.exports = roboticArm;
