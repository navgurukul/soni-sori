const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPGcgaWQ9IlhNTElEXzc3MDhfIj4NCgk8cGF0aCBpZD0iWE1MSURfODg0NF8iIGZpbGw9IiMwRjczOTEiIHN0cm9rZT0iIzA2NTk2RCIgc3Ryb2tlLXdpZHRoPSIwLjkzNzUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTM0LjYsNy40bDAuNywwLjd2NS40DQoJCWwxLjIsMS4ydjE1LjRsLTEuMiwxLjJWMzJjMCwwLjMtMC4yLDAuNS0wLjUsMC41aDBINS4xYy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVjMCwwLDAsMCwwLDBWNy45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWwwLDBIMzQuNiIvPg0KCTxwYXRoIGlkPSJYTUxJRF84ODQzXyIgZmlsbD0iIzBGNzM5MSIgZD0iTTM0LjYsMTZjMCwwLjQsMC4zLDAuNywwLjcsMC43YzAuNCwwLDAuNy0wLjMsMC43LTAuN2MwLDAsMCwwLDAsMGMwLTAuNC0wLjMtMC43LTAuNy0wLjcNCgkJQzM0LjksMTUuMywzNC42LDE1LjYsMzQuNiwxNkMzNC42LDE2LDM0LjYsMTYsMzQuNiwxNnoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODg0Ml8iIGZpbGw9IiMwRjczOTEiIGQ9Ik0zNC42LDI4LjljMCwwLjQsMC4zLDAuNywwLjcsMC43YzAuNCwwLDAuNy0wLjMsMC43LTAuN2MwLDAsMCwwLDAsMA0KCQljMC0wLjQtMC4zLTAuNy0wLjctMC43QzM0LjksMjguMiwzNC42LDI4LjUsMzQuNiwyOC45QzM0LjYsMjguOSwzNC42LDI4LjksMzQuNiwyOC45eiIvPg0KCQ0KCQk8bGluZSBpZD0iX3gzMF8uMS4wLjg1XzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGRkYiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHgxPSIxMS4zIiB5MT0iMTMuNyIgeDI9IjM0LjMiIHkyPSIxMy43Ii8+DQoJDQoJCTxsaW5lIGlkPSJfeDMwXy4xLjAuODVfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkZGRiIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgeDE9IjExLjMiIHkxPSIyNy42IiB4Mj0iMzQuMyIgeTI9IjI3LjYiLz4NCgk8ZyBpZD0iWE1MSURfODg0MF8iPg0KCQk8ZyBpZD0iX3gzMF8uMS4wLjk2LjAuMF8xXyI+DQoJCQk8cGF0aCBpZD0iX3gzMF8uMS4wLjk2LjAuMC4wXzFfIiBmaWxsPSIjRkZGRkZGIiBkPSJNMjQuOCwxOS4yYzAuNS0wLjUsMC44LTEsMS4yLTEuM2MxLjYtMS40LDMuNC0yLDUuNS0xLjFjMiwwLjgsMywyLjksMi42LDUNCgkJCQljLTAuNCwxLjktMi4zLDMuNC00LjMsMy41Yy0xLjksMC4xLTMuMy0wLjgtNC41LTIuMWMtMC4yLTAuMi0wLjQtMC40LTAuNi0wLjdjLTAuMiwwLjItMC4zLDAuMy0wLjQsMC41DQoJCQkJYy0xLjMsMS42LTIuOCwyLjUtNC45LDIuNGMtMi4xLTAuMS0zLjktMi00LjEtNC4xYy0wLjItMi42LDEuOC00LjgsNC42LTQuOGMxLjgsMCwzLjMsMC45LDQuNCwyLjRDMjQuNiwxOC44LDI0LjcsMTksMjQuOCwxOS4yeg0KCQkJCSIvPg0KCQkJPHBhdGggaWQ9Il94MzBfLjEuMC45Ni4wLjAuMV8xXyIgZmlsbD0iIzBGNzM5MSIgZD0iTTIwLjMsMTcuOGMtMS45LDAtMy4zLDEuMi0zLjQsMi44Yy0wLjEsMS40LDEsMi44LDIuNSwzLjENCgkJCQljMS41LDAuMywyLjctMC4zLDMuNi0xLjRjMS40LTEuNywxLjMtMS40LDAtM0MyMi4zLDE4LjMsMjEuMywxNy44LDIwLjMsMTcuOHoiLz4NCgkJCTxwYXRoIGlkPSJfeDMwXy4xLjAuOTYuMC4wLjJfMV8iIGZpbGw9IiMwRjczOTEiIGQ9Ik0yOS44LDE3LjdjLTAuMiwwLTAuNCwwLTAuNiwwYy0xLjYsMC4zLTIuNiwxLjQtMy40LDIuOA0KCQkJCWMtMC4xLDAuMS0wLjEsMC4zLDAsMC40YzAuNywxLjIsMS41LDIuMiwyLjksMi42YzEuMywwLjQsMi41LDAuMSwzLjQtMC45YzAuOC0wLjksMS0yLDAuNS0zLjFDMzIsMTguNCwzMSwxNy44LDI5LjgsMTcuN3oiLz4NCgkJPC9nPg0KCQk8ZyBpZD0iWE1MSURfODg0MV8iPg0KCQkJPGcgaWQ9Il94MzBfLjEuMC45Ni4wLjFfMV8iPg0KCQkJCTxwYXRoIGlkPSJfeDMwXy4xLjAuOTYuMC4xLjBfMV8iIGZpbGw9IiNGRkZGRkYiIGQ9Ik0yMS43LDIxLjJDMjEuNywyMS4yLDIxLjcsMjEuMiwyMS43LDIxLjJsLTIuOSwwLjFjMCwwLTAuMSwwLTAuMS0wLjF2LTAuOA0KCQkJCQljMCwwLDAtMC4xLDAuMS0wLjFoMi44YzAsMCwwLjEsMCwwLjEsMC4xVjIxLjJ6Ii8+DQoJCQk8L2c+DQoJCQk8ZyBpZD0iX3gzMF8uMS4wLjk2LjAuMl8xXyI+DQoJCQkJPHBhdGggaWQ9Il94MzBfLjEuMC45Ni4wLjIuMF8xXyIgZmlsbD0iI0ZGRkZGRiIgZD0iTTMxLDIwLjNDMzEsMjAuMywzMC45LDIwLjIsMzEsMjAuM2wtMC45LTAuMWMwLDAtMC4xLDAtMC4xLTAuMXYtMC44DQoJCQkJCWMwLDAsMC0wLjEtMC4xLTAuMWgtMC45YzAsMC0wLjEsMC0wLjEsMC4xdjAuOGMwLDAsMCwwLjEtMC4xLDAuMWgtMC44YzAsMC0wLjEsMC0wLjEsMC4xdjAuOGMwLDAsMCwwLjEsMC4xLDAuMWgwLjgNCgkJCQkJYzAsMCwwLjEsMCwwLjEsMC4xdjAuOGMwLDAsMCwwLjEsMC4xLDAuMWgwLjljMCwwLDAuMSwwLDAuMS0wLjF2LTAuOGMwLDAsMC0wLjEsMC4xLTAuMWgwLjhjMCwwLDAuMSwwLDAuMS0wLjFWMjAuM3oiLz4NCgkJCTwvZz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfODgzN18iPg0KCQk8ZyBpZD0iWE1MSURfODgzOF8iPg0KCQkJPGcgaWQ9IlhNTElEXzg4MzlfIj4NCgkJCQk8ZyBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjBfMV8iPg0KCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjAuMF8xXyIgeD0iMTAuMSIgeT0iMjIiIGZpbGw9IiM5OTk5OTkiIHdpZHRoPSIxLjIiIGhlaWdodD0iMC44Ii8+DQoJCQkJPC9nPg0KCQkJPC9nPg0KCQk8L2c+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF84ODM0XyI+DQoJCTxnIGlkPSJYTUxJRF84ODM1XyI+DQoJCQk8ZyBpZD0iWE1MSURfODgzNl8iPg0KCQkJCTxnIGlkPSJfeDMwXy4xLjEyLjAuMC4wLjAuMV8xXyI+DQoJCQkJCTxyZWN0IGlkPSJfeDMwXy4xLjEyLjAuMC4wLjAuMS4wXzFfIiB4PSIxMC4xIiB5PSIyMC40IiBmaWxsPSIjOTk5OTk5IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjAuOCIvPg0KCQkJCTwvZz4NCgkJCTwvZz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfODMwN18iPg0KCQk8ZyBpZD0iWE1MSURfODgzMl8iPg0KCQkJPGcgaWQ9IlhNTElEXzg4MzNfIj4NCgkJCQk8ZyBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjJfMV8iPg0KCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjIuMF8xXyIgeD0iMTAuMSIgeT0iMTguOCIgZmlsbD0iIzk5OTk5OSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIwLjgiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzgzMDRfIj4NCgkJPGcgaWQ9IlhNTElEXzgzMDVfIj4NCgkJCTxnIGlkPSJYTUxJRF84MzA2XyI+DQoJCQkJPGcgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC42XzFfIj4NCgkJCQkJPHJlY3QgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC42LjBfMV8iIHg9IjYuNSIgeT0iMTkuNiIgZmlsbD0iIzk5OTk5OSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIyLjUiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzgwMDNfIj4NCgkJPGcgaWQ9IlhNTElEXzgwMDRfIj4NCgkJCTxnIGlkPSJYTUxJRF84MzAzXyI+DQoJCQkJPGcgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC44XzFfIj4NCgkJCQkJPHJlY3QgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC44LjBfMV8iIHg9IjcuNyIgeT0iMTguNSIgZmlsbD0iIzMwMzAzMCIgd2lkdGg9IjIuNCIgaGVpZ2h0PSI0LjYiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDlfIiB0cmFuc2Zvcm09Im1hdHJpeCgtMi41NDcxMWUtMDYsIC0xLCAxLCAtMi41NDcxMWUtMDYsIDEuOTc2MywgNDMuNjAzMykiPg0KCQk8ZyBpZD0iWE1MSURfNzkxMF8iPg0KCQkJPGcgaWQ9IlhNTElEXzc5MTFfIj4NCgkJCQk8ZyBpZD0iWE1MSURfODAwMl8iPg0KCQkJCQk8ZyBpZD0iX3gzMF8uMS4xNC4wLjAuMC4wLjEuMF8xXyI+DQoJCQkJCQkNCgkJCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xNC4wLjAuMC4wLjEuMC4wXzFfIiB4PSIyNi4yIiB5PSIyLjIiIHRyYW5zZm9ybT0ibWF0cml4KDQuNjQ4MTg0ZS0wMDcgLTEgMSA0LjY0ODE4NGUtMDA3IDI1LjA1OTMgMzIuMTQ4OSkiIGZpbGw9IiMzMDMwMzAiIHdpZHRoPSI0LjciIGhlaWdodD0iMi43Ii8+DQoJCQkJCTwvZz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDhfIj4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4wXzFfIiB4PSIxMC42IiB5PSI5IiBmaWxsPSIjNDA0MDQwIiB3aWR0aD0iMjMiIGhlaWdodD0iMi4zIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8xMF8iIHg9IjExLjIiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzFfIiB4PSIxMy41IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8yXyIgeD0iMTUuOCIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfM18iIHg9IjE4LjEiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzRfIiB4PSIyMC40IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV81XyIgeD0iMjIuOCIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfNl8iIHg9IjI1LjEiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzdfIiB4PSIyNy40IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV84XyIgeD0iMjkuNyIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfOV8iIHg9IjMyIiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJPC9nPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF83OTA3XyIgZmlsbD0iIzM1MzUzNSIgcG9pbnRzPSI4LjUsMjYuOSA0LjQsMjYuOSA0LjQsMjYuOCAzLjEsMjYuOCAzLjEsMzAuOSA0LjQsMzAuOSA0LjQsMzAuOCA0LjQsMzAuOCANCgkJNC40LDMwLjggOC41LDMwLjggCSIvPg0KCTxnIGlkPSJYTUxJRF83ODA0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTA2XyIgeD0iMTkuNyIgeT0iMjkuMSIgZmlsbD0iIzQwNDA0MCIgd2lkdGg9IjE0IiBoZWlnaHQ9IjIuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzkwNV8iIHg9IjIwLjMiIHk9IjI5LjYiIGZpbGw9IiMwQzBDMEMiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF83ODA5XyIgeD0iMjIuNiIgeT0iMjkuNiIgZmlsbD0iIzBDMEMwQyIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDhfIiB4PSIyNC45IiB5PSIyOS42IiBmaWxsPSIjMEMwQzBDIiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwN18iIHg9IjI3LjIiIHk9IjI5LjYiIGZpbGw9IiMwQzBDMEMiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF83ODA2XyIgeD0iMjkuNSIgeT0iMjkuNiIgZmlsbD0iIzBDMEMwQyIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDVfIiB4PSIzMS44IiB5PSIyOS42IiBmaWxsPSIjMEMwQzBDIiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPGcgaWQ9IlhNTElEXzc3MDhfIj4NCgk8cGF0aCBpZD0iWE1MSURfODg0NF8iIGZpbGw9IiMwRjczOTEiIHN0cm9rZT0iIzA2NTk2RCIgc3Ryb2tlLXdpZHRoPSIwLjkzNzUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgZD0iTTM0LjYsNy40bDAuNywwLjd2NS40DQoJCWwxLjIsMS4ydjE1LjRsLTEuMiwxLjJWMzJjMCwwLjMtMC4yLDAuNS0wLjUsMC41aDBINS4xYy0wLjMsMC0wLjUtMC4yLTAuNS0wLjVjMCwwLDAsMCwwLDBWNy45YzAtMC4zLDAuMi0wLjUsMC41LTAuNWwwLDBIMzQuNiIvPg0KCTxwYXRoIGlkPSJYTUxJRF84ODQzXyIgZmlsbD0iIzBGNzM5MSIgZD0iTTM0LjYsMTZjMCwwLjQsMC4zLDAuNywwLjcsMC43YzAuNCwwLDAuNy0wLjMsMC43LTAuN2MwLDAsMCwwLDAsMGMwLTAuNC0wLjMtMC43LTAuNy0wLjcNCgkJQzM0LjksMTUuMywzNC42LDE1LjYsMzQuNiwxNkMzNC42LDE2LDM0LjYsMTYsMzQuNiwxNnoiLz4NCgk8cGF0aCBpZD0iWE1MSURfODg0Ml8iIGZpbGw9IiMwRjczOTEiIGQ9Ik0zNC42LDI4LjljMCwwLjQsMC4zLDAuNywwLjcsMC43YzAuNCwwLDAuNy0wLjMsMC43LTAuN2MwLDAsMCwwLDAsMA0KCQljMC0wLjQtMC4zLTAuNy0wLjctMC43QzM0LjksMjguMiwzNC42LDI4LjUsMzQuNiwyOC45QzM0LjYsMjguOSwzNC42LDI4LjksMzQuNiwyOC45eiIvPg0KCQ0KCQk8bGluZSBpZD0iX3gzMF8uMS4wLjg1XzJfIiBmaWxsPSJub25lIiBzdHJva2U9IiNGRkZGRkYiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHgxPSIxMS4zIiB5MT0iMTMuNyIgeDI9IjM0LjMiIHkyPSIxMy43Ii8+DQoJDQoJCTxsaW5lIGlkPSJfeDMwXy4xLjAuODVfMV8iIGZpbGw9Im5vbmUiIHN0cm9rZT0iI0ZGRkZGRiIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgeDE9IjExLjMiIHkxPSIyNy42IiB4Mj0iMzQuMyIgeTI9IjI3LjYiLz4NCgk8ZyBpZD0iWE1MSURfODg0MF8iPg0KCQk8ZyBpZD0iX3gzMF8uMS4wLjk2LjAuMF8xXyI+DQoJCQk8cGF0aCBpZD0iX3gzMF8uMS4wLjk2LjAuMC4wXzFfIiBmaWxsPSIjRkZGRkZGIiBkPSJNMjQuOCwxOS4yYzAuNS0wLjUsMC44LTEsMS4yLTEuM2MxLjYtMS40LDMuNC0yLDUuNS0xLjFjMiwwLjgsMywyLjksMi42LDUNCgkJCQljLTAuNCwxLjktMi4zLDMuNC00LjMsMy41Yy0xLjksMC4xLTMuMy0wLjgtNC41LTIuMWMtMC4yLTAuMi0wLjQtMC40LTAuNi0wLjdjLTAuMiwwLjItMC4zLDAuMy0wLjQsMC41DQoJCQkJYy0xLjMsMS42LTIuOCwyLjUtNC45LDIuNGMtMi4xLTAuMS0zLjktMi00LjEtNC4xYy0wLjItMi42LDEuOC00LjgsNC42LTQuOGMxLjgsMCwzLjMsMC45LDQuNCwyLjRDMjQuNiwxOC44LDI0LjcsMTksMjQuOCwxOS4yeg0KCQkJCSIvPg0KCQkJPHBhdGggaWQ9Il94MzBfLjEuMC45Ni4wLjAuMV8xXyIgZmlsbD0iIzBGNzM5MSIgZD0iTTIwLjMsMTcuOGMtMS45LDAtMy4zLDEuMi0zLjQsMi44Yy0wLjEsMS40LDEsMi44LDIuNSwzLjENCgkJCQljMS41LDAuMywyLjctMC4zLDMuNi0xLjRjMS40LTEuNywxLjMtMS40LDAtM0MyMi4zLDE4LjMsMjEuMywxNy44LDIwLjMsMTcuOHoiLz4NCgkJCTxwYXRoIGlkPSJfeDMwXy4xLjAuOTYuMC4wLjJfMV8iIGZpbGw9IiMwRjczOTEiIGQ9Ik0yOS44LDE3LjdjLTAuMiwwLTAuNCwwLTAuNiwwYy0xLjYsMC4zLTIuNiwxLjQtMy40LDIuOA0KCQkJCWMtMC4xLDAuMS0wLjEsMC4zLDAsMC40YzAuNywxLjIsMS41LDIuMiwyLjksMi42YzEuMywwLjQsMi41LDAuMSwzLjQtMC45YzAuOC0wLjksMS0yLDAuNS0zLjFDMzIsMTguNCwzMSwxNy44LDI5LjgsMTcuN3oiLz4NCgkJPC9nPg0KCQk8ZyBpZD0iWE1MSURfODg0MV8iPg0KCQkJPGcgaWQ9Il94MzBfLjEuMC45Ni4wLjFfMV8iPg0KCQkJCTxwYXRoIGlkPSJfeDMwXy4xLjAuOTYuMC4xLjBfMV8iIGZpbGw9IiNGRkZGRkYiIGQ9Ik0yMS43LDIxLjJDMjEuNywyMS4yLDIxLjcsMjEuMiwyMS43LDIxLjJsLTIuOSwwLjFjMCwwLTAuMSwwLTAuMS0wLjF2LTAuOA0KCQkJCQljMCwwLDAtMC4xLDAuMS0wLjFoMi44YzAsMCwwLjEsMCwwLjEsMC4xVjIxLjJ6Ii8+DQoJCQk8L2c+DQoJCQk8ZyBpZD0iX3gzMF8uMS4wLjk2LjAuMl8xXyI+DQoJCQkJPHBhdGggaWQ9Il94MzBfLjEuMC45Ni4wLjIuMF8xXyIgZmlsbD0iI0ZGRkZGRiIgZD0iTTMxLDIwLjNDMzEsMjAuMywzMC45LDIwLjIsMzEsMjAuM2wtMC45LTAuMWMwLDAtMC4xLDAtMC4xLTAuMXYtMC44DQoJCQkJCWMwLDAsMC0wLjEtMC4xLTAuMWgtMC45YzAsMC0wLjEsMC0wLjEsMC4xdjAuOGMwLDAsMCwwLjEtMC4xLDAuMWgtMC44YzAsMC0wLjEsMC0wLjEsMC4xdjAuOGMwLDAsMCwwLjEsMC4xLDAuMWgwLjgNCgkJCQkJYzAsMCwwLjEsMCwwLjEsMC4xdjAuOGMwLDAsMCwwLjEsMC4xLDAuMWgwLjljMCwwLDAuMSwwLDAuMS0wLjF2LTAuOGMwLDAsMC0wLjEsMC4xLTAuMWgwLjhjMCwwLDAuMSwwLDAuMS0wLjFWMjAuM3oiLz4NCgkJCTwvZz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfODgzN18iPg0KCQk8ZyBpZD0iWE1MSURfODgzOF8iPg0KCQkJPGcgaWQ9IlhNTElEXzg4MzlfIj4NCgkJCQk8ZyBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjBfMV8iPg0KCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjAuMF8xXyIgeD0iMTAuMSIgeT0iMjIiIGZpbGw9IiM5OTk5OTkiIHdpZHRoPSIxLjIiIGhlaWdodD0iMC44Ii8+DQoJCQkJPC9nPg0KCQkJPC9nPg0KCQk8L2c+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF84ODM0XyI+DQoJCTxnIGlkPSJYTUxJRF84ODM1XyI+DQoJCQk8ZyBpZD0iWE1MSURfODgzNl8iPg0KCQkJCTxnIGlkPSJfeDMwXy4xLjEyLjAuMC4wLjAuMV8xXyI+DQoJCQkJCTxyZWN0IGlkPSJfeDMwXy4xLjEyLjAuMC4wLjAuMS4wXzFfIiB4PSIxMC4xIiB5PSIyMC40IiBmaWxsPSIjOTk5OTk5IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjAuOCIvPg0KCQkJCTwvZz4NCgkJCTwvZz4NCgkJPC9nPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfODMwN18iPg0KCQk8ZyBpZD0iWE1MSURfODgzMl8iPg0KCQkJPGcgaWQ9IlhNTElEXzg4MzNfIj4NCgkJCQk8ZyBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjJfMV8iPg0KCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xMi4wLjAuMC4wLjIuMF8xXyIgeD0iMTAuMSIgeT0iMTguOCIgZmlsbD0iIzk5OTk5OSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIwLjgiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzgzMDRfIj4NCgkJPGcgaWQ9IlhNTElEXzgzMDVfIj4NCgkJCTxnIGlkPSJYTUxJRF84MzA2XyI+DQoJCQkJPGcgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC42XzFfIj4NCgkJCQkJPHJlY3QgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC42LjBfMV8iIHg9IjYuNSIgeT0iMTkuNiIgZmlsbD0iIzk5OTk5OSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIyLjUiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzgwMDNfIj4NCgkJPGcgaWQ9IlhNTElEXzgwMDRfIj4NCgkJCTxnIGlkPSJYTUxJRF84MzAzXyI+DQoJCQkJPGcgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC44XzFfIj4NCgkJCQkJPHJlY3QgaWQ9Il94MzBfLjEuMTIuMC4wLjAuMC44LjBfMV8iIHg9IjcuNyIgeT0iMTguNSIgZmlsbD0iIzMwMzAzMCIgd2lkdGg9IjIuNCIgaGVpZ2h0PSI0LjYiLz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDlfIiB0cmFuc2Zvcm09Im1hdHJpeCgtMi41NDcxMWUtMDYsIC0xLCAxLCAtMi41NDcxMWUtMDYsIDEuOTc2MywgNDMuNjAzMykiPg0KCQk8ZyBpZD0iWE1MSURfNzkxMF8iPg0KCQkJPGcgaWQ9IlhNTElEXzc5MTFfIj4NCgkJCQk8ZyBpZD0iWE1MSURfODAwMl8iPg0KCQkJCQk8ZyBpZD0iX3gzMF8uMS4xNC4wLjAuMC4wLjEuMF8xXyI+DQoJCQkJCQkNCgkJCQkJCQk8cmVjdCBpZD0iX3gzMF8uMS4xNC4wLjAuMC4wLjEuMC4wXzFfIiB4PSIyNi4yIiB5PSIyLjIiIHRyYW5zZm9ybT0ibWF0cml4KDQuNjQ4MTg0ZS0wMDcgLTEgMSA0LjY0ODE4NGUtMDA3IDI1LjA1OTMgMzIuMTQ4OSkiIGZpbGw9IiMzMDMwMzAiIHdpZHRoPSI0LjciIGhlaWdodD0iMi43Ii8+DQoJCQkJCTwvZz4NCgkJCQk8L2c+DQoJCQk8L2c+DQoJCTwvZz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDhfIj4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4wXzFfIiB4PSIxMC42IiB5PSI5IiBmaWxsPSIjNDA0MDQwIiB3aWR0aD0iMjMiIGhlaWdodD0iMi4zIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8xMF8iIHg9IjExLjIiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzFfIiB4PSIxMy41IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8yXyIgeD0iMTUuOCIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfM18iIHg9IjE4LjEiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzRfIiB4PSIyMC40IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV81XyIgeD0iMjIuOCIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfNl8iIHg9IjI1LjEiIHk9IjkuNiIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzdfIiB4PSIyNy40IiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV84XyIgeD0iMjkuNyIgeT0iOS42IiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfOV8iIHg9IjMyIiB5PSI5LjYiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJPC9nPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF83OTA3XyIgZmlsbD0iIzM1MzUzNSIgcG9pbnRzPSI4LjUsMjYuOSA0LjQsMjYuOSA0LjQsMjYuOCAzLjEsMjYuOCAzLjEsMzAuOSA0LjQsMzAuOSA0LjQsMzAuOCA0LjQsMzAuOCANCgkJNC40LDMwLjggOC41LDMwLjggCSIvPg0KCTxnIGlkPSJYTUxJRF83ODA0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTA2XyIgeD0iMTkuNyIgeT0iMjkuMSIgZmlsbD0iIzQwNDA0MCIgd2lkdGg9IjE0IiBoZWlnaHQ9IjIuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzkwNV8iIHg9IjIwLjMiIHk9IjI5LjYiIGZpbGw9IiMwQzBDMEMiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF83ODA5XyIgeD0iMjIuNiIgeT0iMjkuNiIgZmlsbD0iIzBDMEMwQyIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDhfIiB4PSIyNC45IiB5PSIyOS42IiBmaWxsPSIjMEMwQzBDIiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwN18iIHg9IjI3LjIiIHk9IjI5LjYiIGZpbGw9IiMwQzBDMEMiIHN0cm9rZT0iIzIzMUYyMCIgc3Ryb2tlLXdpZHRoPSIwLjMxMjUiIHdpZHRoPSIxLjIiIGhlaWdodD0iMS4yIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF83ODA2XyIgeD0iMjkuNSIgeT0iMjkuNiIgZmlsbD0iIzBDMEMwQyIgc3Ryb2tlPSIjMjMxRjIwIiBzdHJva2Utd2lkdGg9IjAuMzEyNSIgd2lkdGg9IjEuMiIgaGVpZ2h0PSIxLjIiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDVfIiB4PSIzMS44IiB5PSIyOS42IiBmaWxsPSIjMEMwQzBDIiBzdHJva2U9IiMyMzFGMjAiIHN0cm9rZS13aWR0aD0iMC4zMTI1IiB3aWR0aD0iMS4yIiBoZWlnaHQ9IjEuMiIvPg0KCTwvZz4NCjwvZz4NCjwvc3ZnPg0K';

class quon {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quon';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quonState = sourceTarget.getCustomState(quon.STATE_KEY);
            if (quonState) {
                newTarget.setCustomState(quon.STATE_KEY, Clone.simple(quonState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'quon',
            name: formatMessage({
                id: 'quon.quon',
                default: 'quon',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#0fbd8c',
            colourSecondary: '#0da57a',
            colourTertiary: '#0b8e69',
            blocks: [
                {
                    opcode: 'quonStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'quon.quonStartUp',
                        default: 'when quon starts up',
                        description: 'quon Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quon.digitalRead',
                        default: 'read status of digital pin [PIN]',
                        description: 'Read digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        }
                    }
                },
                {
                    opcode: 'analogRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quon.analogRead',
                        default: 'read analog pin [ANALOG_PIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        ANALOG_PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'digitalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quon.digitalWrite',
                        default: 'set digital pin [PIN] output as [MODE]',
                        description: 'Write digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '3'
                        },
                        MODE: {
                            type: ArgumentType.STRING,
                            menu: 'digitalModes',
                            defaultValue: 'true'
                        }

                    }
                },
                '---',
                {
                    opcode: 'setPWM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quon.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Write PWM Value of pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '3'
                        },
                        VALUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: '255',
                        }
                    }
                },
                '---',
                {
                    opcode: 'tactileSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quon.tactileSwitch',
                        default: 'tactile switch pressed?',
                        description: 'Read tactile switch'
                    })
                },
                {
                    opcode: 'slideSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quon.slideSwitch',
                        default: 'is slide switch in [SWITCH_DIRECTION] state?',
                        description: 'Read slide switch'
                    }),
                    arguments: {
                        SWITCH_DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'slideSwitchDir',
                            defaultValue: 'up'
                        }
                    }
                },
                {
                    opcode: 'navigationKey',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quon.navigationKey',
                        default: 'is navigation key in [NAV_DIRECTION] state?',
                        description: 'Read navigation key direction'
                    }),
                    arguments: {
                        NAV_DIRECTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'navKeyDir',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'potentiometer',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quon.potentiometer',
                        default: 'get potentiometer reading',
                        description: 'Read potentiometer'
                    })
                },
                // {
                //     opcode: 'readTouchPin',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         opcode: 'readTouchPin',
                //         id: 'quon.readTouchPin',
                //         default: 'read touch on pin [TOUCHSENSORPIN]',
                //         description: 'Read potentiometer'
                //     }),
                //     arguments: {
                //         TOUCHSENSORPIN: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'touchPins',
                //             defaultValue: '0'
                //         }
                //     }
                // },
                // {
                //     opcode: 'readMPUPin',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         opcode: 'readMPUPin',
                //         id: 'quon.readMPUPin',
                //         default: 'get [MPUAXIS] data',
                //         description: 'Read MPU'
                //     }),
                //     arguments: {
                //         MPUAXIS: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'axisTable',
                //             defaultValue: '1'
                //         }
                //     }
                // },
                {
                    opcode: 'map',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quon.map',
                        default: 'map [VALUE] from [RANGE11] ~ [RANGE12] to [RANGE21] ~ [RANGE22]',
                        description: 'map'
                    }),
                    arguments: {
                        VALUE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '50'
                        },
                        RANGE11: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        RANGE12: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '255'
                        },
                        RANGE21: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '0'
                        },
                        RANGE22: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '1023'
                        },
                    }
                },
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quon.cast',
                        default: 'cast [VALUE] to [OPERATION]',
                        description: 'Casts string value to integer'
                    }),
                    arguments: {
                        VALUE: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '15.5'
                        },
                        OPERATION: {
                            type: ArgumentType.NUMBER,
                            menu: 'mathOperation',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: formatMessage({
                        id: 'quon.blockSeparatorMessage1',
                        default: 'User Defined Function',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'quonUDF',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'quon.quonUDF',
                        default: 'program [NAME]',
                        description: 'quon block for UDFs'
                    }),
                    arguments: {
                        NAME: {
                            type: ArgumentType.STRING,
                            defaultValue: 'UDF'
                        }
                    },
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                }
            ],
            menus: {
                analogPins: [
                    { text: '1', value: '1' },
                    { text: '2', value: '2' }
                ],
                digitalPins: [
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' },
                    { text: '6', value: '6' },
                    { text: '7', value: '7' },
                    { text: '8', value: '8' },
                    { text: '9', value: '9' },
                    { text: '10', value: '10' },
                    { text: '11', value: '11' },
                    { text: '12', value: '12' },
                ],
                pwmPins: [
                    { text: '3', value: '3' },
                    { text: '4', value: '4' },
                    { text: '5', value: '5' },
                    { text: '6', value: '6' }
                ],
                digitalModes: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.digitalModes.option1',
                            default: 'HIGH',
                            description: 'Menu'
                        }), value: 'true'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.digitalModes.option2',
                            default: 'LOW',
                            description: 'Menu'
                        }), value: 'false'
                    }
                ],
                mathOperation: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.mathOperation.option1',
                            default: 'integer',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.mathOperation.option2',
                            default: 'float',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                navKeyDir: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option1',
                            default: 'up',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option4',
                            default: 'left',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option2',
                            default: 'right',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option3',
                            default: 'down',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option5',
                            default: 'pressed',
                            description: 'Menu'
                        }),
                        value: '5'
                    }
                ],
                slideSwitchDir: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option1',
                            default: 'up',
                            description: 'Menu'
                        }),
                        value: "1"
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option3',
                            default: 'down',
                            description: 'Menu'
                        }),
                        value: "2"
                    }
                ],
                // touchPins: [
                //     { text: 'T0', value: '0' },
                //     { text: 'T1', value: '1' },
                //     { text: 'T2', value: '2' },
                //     { text: 'T3', value: '3' },
                //     { text: 'T4', value: '4' },
                //     { text: 'T5', value: '5' }
                // ],

                // axisTable: [
                //     { text: 'accelerometer x axis', value: '1' },
                //     { text: 'accelerometer y axis', value: '2' },
                //     { text: 'accelerometer z axis', value: '3' },
                //     { text: 'gyroscope x axis', value: '4' },
                //     { text: 'gyroscope y axis', value: '5' },
                //     { text: 'gyroscope z axis', value: '6' }
                // ]
            }
        };
    }

    quonStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_quonStartUp');
            return;
        }
    }

    quonUDF() {
        console.log('hardware_quonUDF');
    }

    digitalRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.digitalRead(args, util, this);
        }
        return RealtimeMode.digitalRead(args, util, this);
    }

    analogRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.analogRead(args, util, this);
        }
        return RealtimeMode.analogRead(args, util, this);
    }

    digitalWrite(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.digitalWrite(args, util, this);
        }
        return RealtimeMode.digitalWrite(args, util, this);
    }

    setPWM(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setPWM(args, util, this);
        }
        return RealtimeMode.setPWM(args, util, this);
    }

    tactileSwitch(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.tactileSwitch(args, util, this);
        }
        return RealtimeMode.tactileSwitch(args, util, this);
    }

    slideSwitch(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.slideSwitch(args, util, this);
        }
        return RealtimeMode.slideSwitch(args, util, this);
    }

    navigationKey(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.navigationKey(args, util, this);
        }
        return RealtimeMode.navigationKey(args, util, this);
    }

    potentiometer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.potentiometer(args, util, this);
        }
        return RealtimeMode.potentiometer(args, util, this);
    }

    // readTouchPin(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.readTouchPin(args, util, this);
    //     }
    //     return RealtimeMode.readTouchPin(args, util, this);
    // }

    // readMPUPin(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.readMPUPin(args, util, this);
    //     }
    //     return RealtimeMode.readMPUPin(args, util, this);
    // }

    map(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.map(args, util, this);
        }
        return RealtimeMode.map(args, util, this);
    }

    cast(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.cast(args, util, this);
        }
        return RealtimeMode.cast(args, util, this);
    }

}

module.exports = quon;
