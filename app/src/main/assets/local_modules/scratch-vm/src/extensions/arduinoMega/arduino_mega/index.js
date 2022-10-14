const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJaGVpZ2h0PSI0MHB4IiB3aWR0aD0iNDBweCINCgkgdmlld0JveD0iMCAwIDQwIDQwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MCA0MDsiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHN0eWxlIHR5cGU9InRleHQvY3NzIj4NCgkuc3Qwe2ZpbGw6IzBGNzM5MTtzdHJva2U6IzA2NTk2RDtzdHJva2Utd2lkdGg6MC43NTt9DQoJLnN0MXtmaWxsOiNEOEQ4RDY7fQ0KCS5zdDJ7ZmlsbDojMzAzMDMwO30NCgkuc3Qze2ZpbGw6IzMzMzMzMzt9DQoJLnN0NHtmaWxsOiNFNkU2RTY7fQ0KCS5zdDV7ZmlsbDojOTk5OTk5O30NCgkuc3Q2e2ZpbGw6I0IzQjNCMzt9DQoJLnN0N3tmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM4NTI3MjU7fQ0KCS5zdDh7ZmlsbDojRkZGRkZGO30NCgkuc3Q5e2ZpbGw6IzBGNzM5MTt9DQoJLnN0MTB7ZmlsbDojNDA0MDQwO30NCgkuc3QxMXtzdHJva2U6IzIzMUYyMDtzdHJva2Utd2lkdGg6MC4yNTt9DQoJLnN0MTJ7ZmlsbDojMEMwQzBDO3N0cm9rZTojMjMxRjIwO3N0cm9rZS13aWR0aDowLjI1O30NCgkuc3QxM3tmaWxsOiMxNjE2MTY7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF8yXyI+DQoJPHBhdGggaWQ9IlhNTElEXzE3Nzg2XyIgY2xhc3M9InN0MCIgZD0iTTM1LjgsMTEuMWwwLjgsMC44djMuNGwwLjgsMC44djExLjRsLTAuOCwwLjh2MC4xYzAsMC4yLTAuMSwwLjMtMC4zLDAuM0g0DQoJCWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zYzAsMCwwLDAsMCwwVjExLjVjMC0wLjIsMC4xLTAuMywwLjMtMC4zSDM1Ljh6Ii8+DQoJPGcgaWQ9IlhNTElEXzI4XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTU2XyIgeD0iOC40IiB5PSIyMi41IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjAuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzk1NV8iIHg9IjguNCIgeT0iMjEuMSIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIwLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc5NTRfIiB4PSI4LjQiIHk9IjE5LjciIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjMiIGhlaWdodD0iMC43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTUwXyIgeD0iNS43IiB5PSIyMC40IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4xIiBoZWlnaHQ9IjIuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzk0OF8iIHg9IjYuOSIgeT0iMTkuNSIgY2xhc3M9InN0MiIgd2lkdGg9IjEuNyIgaGVpZ2h0PSI0LjEiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzI1XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMDUzXyIgY2xhc3M9InN0MyIgZD0iTTExLjEsMjMuNnYxLjhjMCwwLjEsMC4xLDAuMiwwLjIsMC4yaDIuMmMwLjEsMCwwLjItMC4xLDAuMi0wLjJ2LTAuNHYtMS41bC0wLjUtMC41aC0wLjUNCgkJCWgtMC41aC0wLjVMMTEuMSwyMy42eiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMTA1MF8iIGNsYXNzPSJzdDQiIGQ9Ik0xMy41LDI0LjRjMCwwLjItMC4xLDAuNS0wLjIsMC42Yy0wLjIsMC4zLTAuNSwwLjUtMC45LDAuNWMtMC40LDAtMC43LTAuMi0wLjktMC41DQoJCQljLTAuMS0wLjItMC4yLTAuNC0wLjItMC42YzAtMC42LDAuNS0xLjEsMS4xLTEuMUMxMywyMy4yLDEzLjUsMjMuOCwxMy41LDI0LjR6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8yNF8iPg0KCQk8cGF0aCBpZD0iWE1MSURfMTAzN18iIGNsYXNzPSJzdDMiIGQ9Ik0xNC44LDIzLjZ2MS44YzAsMC4xLDAuMSwwLjIsMC4yLDAuMmgyLjJjMC4xLDAsMC4yLTAuMSwwLjItMC4ydi0wLjR2LTEuNWwtMC41LTAuNWgtMC41DQoJCQloLTAuNWgtMC41TDE0LjgsMjMuNnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEwMzRfIiBjbGFzcz0ic3Q0IiBkPSJNMTcuMiwyNC40YzAsMC4yLTAuMSwwLjUtMC4yLDAuNmMtMC4yLDAuMy0wLjUsMC41LTAuOSwwLjVjLTAuNCwwLTAuNy0wLjItMC45LTAuNQ0KCQkJYy0wLjEtMC4yLTAuMi0wLjQtMC4yLTAuNmMwLTAuNiwwLjUtMS4xLDEuMS0xLjFDMTYuNywyMy4yLDE3LjIsMjMuOCwxNy4yLDI0LjR6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8xMF8iPg0KCQk8cmVjdCBpZD0iWE1MSURfODc4XyIgeD0iNi44IiB5PSIxNC43IiBjbGFzcz0ic3Q1IiB3aWR0aD0iMC45IiBoZWlnaHQ9IjMuOCIvPg0KCQk8cGF0aCBpZD0iWE1MSURfM18iIGNsYXNzPSJzdDYiIGQ9Ik02LjgsMTQuN0g1LjZ2LTAuNGMwLTAuMSwwLTAuMS0wLjEtMC4xSDUuNEg1LjF2MC4ydjAuMkgyLjV2My44aDIuNnYwLjJWMTloMC4zDQoJCQljMCwwLDAuMSwwLDAuMSwwYzAuMSwwLDAuMSwwLDAuMS0wLjF2LTAuNGgxLjJWMTQuN3oiLz4NCgk8L2c+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzMwXyIgY2xhc3M9InN0MSIgcG9pbnRzPSIzNC45LDE2LjggMzQuOSwxNS42IDM0LjUsMTUuNiAzNC41LDE1LjEgMzQuMSwxNS4xIDM0LjEsMTUuNiAzMi4yLDE1LjYgMzIuMiwxNS4xIA0KCQkzMS44LDE1LjEgMzEuOCwxNS42IDMxLjQsMTUuNiAzMS40LDE2LjggMzEuNCwxNi44IDMxLjQsMTguNCAzMS40LDE4LjQgMzEuNCwxOS42IDMxLjgsMTkuNiAzMS44LDIwLjEgMzIuMiwyMC4xIDMyLjIsMTkuNiANCgkJMzIuOCwxOS42IDMyLjgsMjAuMSAzMy40LDIwLjEgMzMuNCwxOS42IDM0LjEsMTkuNiAzNC4xLDIwLjEgMzQuNSwyMC4xIDM0LjUsMTkuNiAzNC45LDE5LjYgMzQuOSwxOC40IDM0LjgsMTguNCAzNC44LDE2LjggCSIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzg2OF8iIGNsYXNzPSJzdDciIGN4PSIzMy4yIiBjeT0iMTcuNiIgcj0iMC45Ii8+DQoJPGcgaWQ9IlhNTElEXzExXyI+DQoJCTxwYXRoIGlkPSJYTUxJRF84MDFfIiBjbGFzcz0ic3Q4IiBkPSJNMjIuNywxOC4yYzAuMy0wLjQsMC42LTAuNywwLjktMC45YzEuMS0xLDIuNC0xLjQsMy44LTAuOGMxLjQsMC42LDIuMSwyLDEuOCwzLjQNCgkJCWMtMC4zLDEuMy0xLjYsMi40LTMsMi40Yy0xLjMsMC0yLjMtMC41LTMuMS0xLjVjLTAuMS0wLjEtMC4yLTAuMy0wLjQtMC41Yy0wLjEsMC4xLTAuMiwwLjItMC4zLDAuM2MtMC45LDEuMS0yLDEuNy0zLjQsMS42DQoJCQljLTEuNC0wLjEtMi43LTEuNC0yLjgtMi44Yy0wLjEtMS44LDEuMy0zLjMsMy4yLTMuM2MxLjMsMCwyLjMsMC42LDMsMS42QzIyLjUsMTcuOSwyMi42LDE4LDIyLjcsMTguMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5OV8iIGNsYXNzPSJzdDkiIGQ9Ik0xOS42LDE3LjJjLTEuMywwLTIuMywwLjgtMi40LDEuOWMtMC4xLDEsMC43LDEuOSwxLjcsMi4yYzEsMC4yLDEuOS0wLjIsMi41LTENCgkJCWMxLTEuMiwwLjktMSwwLTIuMUMyMC45LDE3LjYsMjAuMiwxNy4yLDE5LjYsMTcuMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5MV8iIGNsYXNzPSJzdDkiIGQ9Ik0yNi4xLDE3LjJjLTAuMiwwLTAuMywwLTAuNCwwYy0xLjEsMC4yLTEuOCwxLTIuMywxLjljMCwwLjEsMCwwLjIsMCwwLjMNCgkJCWMwLjUsMC44LDEuMSwxLjUsMiwxLjhjMC45LDAuMywxLjcsMCwyLjQtMC43YzAuNi0wLjYsMC43LTEuNCwwLjMtMi4yQzI3LjcsMTcuNiwyNywxNy4zLDI2LjEsMTcuMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5MF8iIGNsYXNzPSJzdDgiIGQ9Ik0yMC41LDE5LjVDMjAuNSwxOS41LDIwLjUsMTkuNiwyMC41LDE5LjVsLTIsMC4xYzAsMC0wLjEsMC0wLjEtMC4xdi0wLjZjMCwwLDAtMC4xLDAuMS0wLjENCgkJCWgxLjljMCwwLDAuMSwwLDAuMSwwLjFWMTkuNXoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc4OV8iIGNsYXNzPSJzdDgiIGQ9Ik0yNywxOC45QzI3LDE4LjksMjYuOSwxOC45LDI3LDE4LjlsLTAuNi0wLjFjMCwwLTAuMSwwLTAuMS0wLjF2LTAuNmMwLDAsMC0wLjEtMC4xLTAuMWgtMC42DQoJCQljMCwwLTAuMSwwLTAuMSwwLjF2MC42YzAsMCwwLDAuMS0wLjEsMC4xaC0wLjZjMCwwLTAuMSwwLTAuMSwwLjF2MC42YzAsMCwwLDAuMSwwLjEsMC4xaDAuNmMwLDAsMC4xLDAsMC4xLDAuMXYwLjYNCgkJCWMwLDAsMCwwLjEsMC4xLDAuMWgwLjZjMCwwLDAuMSwwLDAuMS0wLjF2LTAuNmMwLDAsMC0wLjEsMC4xLTAuMWgwLjZjMCwwLDAuMSwwLDAuMS0wLjFWMTguOXoiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDhfIj4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4wXzFfIiB4PSIxNy42IiB5PSIxMi4yIiBjbGFzcz0ic3QxMCIgd2lkdGg9IjE3LjQiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8xMF8iIHg9IjE4LjEiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfMV8iIHg9IjE5LjgiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfMl8iIHg9IjIxLjYiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfM18iIHg9IjIzLjMiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfNF8iIHg9IjI1IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzVfIiB4PSIyNi44IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzZfIiB4PSIyOC41IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzdfIiB4PSIzMC4zIiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzhfIiB4PSIzMiIgeT0iMTIuNiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV85XyIgeD0iMzMuOCIgeT0iMTIuNiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF83ODA0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTA2XyIgeD0iMjMuOCIgeT0iMjYuMSIgY2xhc3M9InN0MTAiIHdpZHRoPSIxMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzkwNV8iIHg9IjI0LjIiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwOV8iIHg9IjI2LjEiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwOF8iIHg9IjI4IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDdfIiB4PSIyOS44IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDZfIiB4PSIzMS43IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDVfIiB4PSIzMy41IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzE0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF8yNl8iIHg9IjguNiIgeT0iMTIuMiIgY2xhc3M9InN0MTAiIHdpZHRoPSI3LjYiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yMl8iIHg9IjkuMSIgeT0iMTIuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xN18iIHg9IjExIiB5PSIxMi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzE2XyIgeD0iMTIuOSIgeT0iMTIuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNV8iIHg9IjE0LjciIHk9IjEyLjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMV8iPg0KCQk8cG9seWdvbiBpZD0iWE1MSURfMTNfIiBjbGFzcz0ic3QxMCIgcG9pbnRzPSIxMi43LDI2LjEgMTEuNCwyNi4xIDkuNiwyNi4xIDkuNiwyNy44IDExLjQsMjcuOCAxMi43LDI3LjggMjIuNywyNy44IDIyLjcsMjYuMSAJCQ0KCQkJIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF85XyIgeD0iMTEuOSIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF84XyIgeD0iMTMuOCIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF83XyIgeD0iMTUuNyIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF82XyIgeD0iMTcuNSIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF81XyIgeD0iMTkuNCIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF80XyIgeD0iMjEuMiIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yM18iIHg9IjEwLjEiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCTwvZz4NCgk8cG9seWdvbiBpZD0iWE1MSURfMjlfIiBjbGFzcz0ic3QxMyIgcG9pbnRzPSI3LjksMjQuOCA3LjksMjQuOCA0LjEsMjQuOCA0LjEsMjQuNiAyLjksMjQuNiAyLjksMjcuNiA0LjEsMjcuNiA0LjEsMjcuNCA0LjEsMjcuNCANCgkJNC4xLDI3LjQgNy45LDI3LjQgNy45LDI3LjQgNy45LDI3LjQgNy45LDI3LjQgOCwyNy40IDgsMjQuOCAJIi8+DQo8L2c+DQo8L3N2Zz4NCg==';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJaGVpZ2h0PSI0MHB4IiB3aWR0aD0iNDBweCINCgkgdmlld0JveD0iMCAwIDQwIDQwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MCA0MDsiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHN0eWxlIHR5cGU9InRleHQvY3NzIj4NCgkuc3Qwe2ZpbGw6IzBGNzM5MTtzdHJva2U6IzA2NTk2RDtzdHJva2Utd2lkdGg6MC43NTt9DQoJLnN0MXtmaWxsOiNEOEQ4RDY7fQ0KCS5zdDJ7ZmlsbDojMzAzMDMwO30NCgkuc3Qze2ZpbGw6IzMzMzMzMzt9DQoJLnN0NHtmaWxsOiNFNkU2RTY7fQ0KCS5zdDV7ZmlsbDojOTk5OTk5O30NCgkuc3Q2e2ZpbGw6I0IzQjNCMzt9DQoJLnN0N3tmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM4NTI3MjU7fQ0KCS5zdDh7ZmlsbDojRkZGRkZGO30NCgkuc3Q5e2ZpbGw6IzBGNzM5MTt9DQoJLnN0MTB7ZmlsbDojNDA0MDQwO30NCgkuc3QxMXtzdHJva2U6IzIzMUYyMDtzdHJva2Utd2lkdGg6MC4yNTt9DQoJLnN0MTJ7ZmlsbDojMEMwQzBDO3N0cm9rZTojMjMxRjIwO3N0cm9rZS13aWR0aDowLjI1O30NCgkuc3QxM3tmaWxsOiMxNjE2MTY7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF8yXyI+DQoJPHBhdGggaWQ9IlhNTElEXzE3Nzg2XyIgY2xhc3M9InN0MCIgZD0iTTM1LjgsMTEuMWwwLjgsMC44djMuNGwwLjgsMC44djExLjRsLTAuOCwwLjh2MC4xYzAsMC4yLTAuMSwwLjMtMC4zLDAuM0g0DQoJCWMtMC4yLDAtMC4zLTAuMS0wLjMtMC4zYzAsMCwwLDAsMCwwVjExLjVjMC0wLjIsMC4xLTAuMywwLjMtMC4zSDM1Ljh6Ii8+DQoJPGcgaWQ9IlhNTElEXzI4XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTU2XyIgeD0iOC40IiB5PSIyMi41IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjAuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzk1NV8iIHg9IjguNCIgeT0iMjEuMSIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIwLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc5NTRfIiB4PSI4LjQiIHk9IjE5LjciIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjMiIGhlaWdodD0iMC43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTUwXyIgeD0iNS43IiB5PSIyMC40IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4xIiBoZWlnaHQ9IjIuMiIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzk0OF8iIHg9IjYuOSIgeT0iMTkuNSIgY2xhc3M9InN0MiIgd2lkdGg9IjEuNyIgaGVpZ2h0PSI0LjEiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzI1XyI+DQoJCTxwYXRoIGlkPSJYTUxJRF8xMDUzXyIgY2xhc3M9InN0MyIgZD0iTTExLjEsMjMuNnYxLjhjMCwwLjEsMC4xLDAuMiwwLjIsMC4yaDIuMmMwLjEsMCwwLjItMC4xLDAuMi0wLjJ2LTAuNHYtMS41bC0wLjUtMC41aC0wLjUNCgkJCWgtMC41aC0wLjVMMTEuMSwyMy42eiIvPg0KCQk8cGF0aCBpZD0iWE1MSURfMTA1MF8iIGNsYXNzPSJzdDQiIGQ9Ik0xMy41LDI0LjRjMCwwLjItMC4xLDAuNS0wLjIsMC42Yy0wLjIsMC4zLTAuNSwwLjUtMC45LDAuNWMtMC40LDAtMC43LTAuMi0wLjktMC41DQoJCQljLTAuMS0wLjItMC4yLTAuNC0wLjItMC42YzAtMC42LDAuNS0xLjEsMS4xLTEuMUMxMywyMy4yLDEzLjUsMjMuOCwxMy41LDI0LjR6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8yNF8iPg0KCQk8cGF0aCBpZD0iWE1MSURfMTAzN18iIGNsYXNzPSJzdDMiIGQ9Ik0xNC44LDIzLjZ2MS44YzAsMC4xLDAuMSwwLjIsMC4yLDAuMmgyLjJjMC4xLDAsMC4yLTAuMSwwLjItMC4ydi0wLjR2LTEuNWwtMC41LTAuNWgtMC41DQoJCQloLTAuNWgtMC41TDE0LjgsMjMuNnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzEwMzRfIiBjbGFzcz0ic3Q0IiBkPSJNMTcuMiwyNC40YzAsMC4yLTAuMSwwLjUtMC4yLDAuNmMtMC4yLDAuMy0wLjUsMC41LTAuOSwwLjVjLTAuNCwwLTAuNy0wLjItMC45LTAuNQ0KCQkJYy0wLjEtMC4yLTAuMi0wLjQtMC4yLTAuNmMwLTAuNiwwLjUtMS4xLDEuMS0xLjFDMTYuNywyMy4yLDE3LjIsMjMuOCwxNy4yLDI0LjR6Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8xMF8iPg0KCQk8cmVjdCBpZD0iWE1MSURfODc4XyIgeD0iNi44IiB5PSIxNC43IiBjbGFzcz0ic3Q1IiB3aWR0aD0iMC45IiBoZWlnaHQ9IjMuOCIvPg0KCQk8cGF0aCBpZD0iWE1MSURfM18iIGNsYXNzPSJzdDYiIGQ9Ik02LjgsMTQuN0g1LjZ2LTAuNGMwLTAuMSwwLTAuMS0wLjEtMC4xSDUuNEg1LjF2MC4ydjAuMkgyLjV2My44aDIuNnYwLjJWMTloMC4zDQoJCQljMCwwLDAuMSwwLDAuMSwwYzAuMSwwLDAuMSwwLDAuMS0wLjF2LTAuNGgxLjJWMTQuN3oiLz4NCgk8L2c+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzMwXyIgY2xhc3M9InN0MSIgcG9pbnRzPSIzNC45LDE2LjggMzQuOSwxNS42IDM0LjUsMTUuNiAzNC41LDE1LjEgMzQuMSwxNS4xIDM0LjEsMTUuNiAzMi4yLDE1LjYgMzIuMiwxNS4xIA0KCQkzMS44LDE1LjEgMzEuOCwxNS42IDMxLjQsMTUuNiAzMS40LDE2LjggMzEuNCwxNi44IDMxLjQsMTguNCAzMS40LDE4LjQgMzEuNCwxOS42IDMxLjgsMTkuNiAzMS44LDIwLjEgMzIuMiwyMC4xIDMyLjIsMTkuNiANCgkJMzIuOCwxOS42IDMyLjgsMjAuMSAzMy40LDIwLjEgMzMuNCwxOS42IDM0LjEsMTkuNiAzNC4xLDIwLjEgMzQuNSwyMC4xIDM0LjUsMTkuNiAzNC45LDE5LjYgMzQuOSwxOC40IDM0LjgsMTguNCAzNC44LDE2LjggCSIvPg0KCTxjaXJjbGUgaWQ9IlhNTElEXzg2OF8iIGNsYXNzPSJzdDciIGN4PSIzMy4yIiBjeT0iMTcuNiIgcj0iMC45Ii8+DQoJPGcgaWQ9IlhNTElEXzExXyI+DQoJCTxwYXRoIGlkPSJYTUxJRF84MDFfIiBjbGFzcz0ic3Q4IiBkPSJNMjIuNywxOC4yYzAuMy0wLjQsMC42LTAuNywwLjktMC45YzEuMS0xLDIuNC0xLjQsMy44LTAuOGMxLjQsMC42LDIuMSwyLDEuOCwzLjQNCgkJCWMtMC4zLDEuMy0xLjYsMi40LTMsMi40Yy0xLjMsMC0yLjMtMC41LTMuMS0xLjVjLTAuMS0wLjEtMC4yLTAuMy0wLjQtMC41Yy0wLjEsMC4xLTAuMiwwLjItMC4zLDAuM2MtMC45LDEuMS0yLDEuNy0zLjQsMS42DQoJCQljLTEuNC0wLjEtMi43LTEuNC0yLjgtMi44Yy0wLjEtMS44LDEuMy0zLjMsMy4yLTMuM2MxLjMsMCwyLjMsMC42LDMsMS42QzIyLjUsMTcuOSwyMi42LDE4LDIyLjcsMTguMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5OV8iIGNsYXNzPSJzdDkiIGQ9Ik0xOS42LDE3LjJjLTEuMywwLTIuMywwLjgtMi40LDEuOWMtMC4xLDEsMC43LDEuOSwxLjcsMi4yYzEsMC4yLDEuOS0wLjIsMi41LTENCgkJCWMxLTEuMiwwLjktMSwwLTIuMUMyMC45LDE3LjYsMjAuMiwxNy4yLDE5LjYsMTcuMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5MV8iIGNsYXNzPSJzdDkiIGQ9Ik0yNi4xLDE3LjJjLTAuMiwwLTAuMywwLTAuNCwwYy0xLjEsMC4yLTEuOCwxLTIuMywxLjljMCwwLjEsMCwwLjIsMCwwLjMNCgkJCWMwLjUsMC44LDEuMSwxLjUsMiwxLjhjMC45LDAuMywxLjcsMCwyLjQtMC43YzAuNi0wLjYsMC43LTEuNCwwLjMtMi4yQzI3LjcsMTcuNiwyNywxNy4zLDI2LjEsMTcuMnoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc5MF8iIGNsYXNzPSJzdDgiIGQ9Ik0yMC41LDE5LjVDMjAuNSwxOS41LDIwLjUsMTkuNiwyMC41LDE5LjVsLTIsMC4xYzAsMC0wLjEsMC0wLjEtMC4xdi0wLjZjMCwwLDAtMC4xLDAuMS0wLjENCgkJCWgxLjljMCwwLDAuMSwwLDAuMSwwLjFWMTkuNXoiLz4NCgkJPHBhdGggaWQ9IlhNTElEXzc4OV8iIGNsYXNzPSJzdDgiIGQ9Ik0yNywxOC45QzI3LDE4LjksMjYuOSwxOC45LDI3LDE4LjlsLTAuNi0wLjFjMCwwLTAuMSwwLTAuMS0wLjF2LTAuNmMwLDAsMC0wLjEtMC4xLTAuMWgtMC42DQoJCQljMCwwLTAuMSwwLTAuMSwwLjF2MC42YzAsMCwwLDAuMS0wLjEsMC4xaC0wLjZjMCwwLTAuMSwwLTAuMSwwLjF2MC42YzAsMCwwLDAuMSwwLjEsMC4xaDAuNmMwLDAsMC4xLDAsMC4xLDAuMXYwLjYNCgkJCWMwLDAsMCwwLjEsMC4xLDAuMWgwLjZjMCwwLDAuMSwwLDAuMS0wLjF2LTAuNmMwLDAsMC0wLjEsMC4xLTAuMWgwLjZjMCwwLDAuMSwwLDAuMS0wLjFWMTguOXoiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzc5MDhfIj4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4wXzFfIiB4PSIxNy42IiB5PSIxMi4yIiBjbGFzcz0ic3QxMCIgd2lkdGg9IjE3LjQiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV8xMF8iIHg9IjE4LjEiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfMV8iIHg9IjE5LjgiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfMl8iIHg9IjIxLjYiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfM18iIHg9IjIzLjMiIHk9IjEyLjYiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iX3gzMF8uMS4xNy4wLjFfNF8iIHg9IjI1IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzVfIiB4PSIyNi44IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzZfIiB4PSIyOC41IiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzdfIiB4PSIzMC4zIiB5PSIxMi42IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9Il94MzBfLjEuMTcuMC4xXzhfIiB4PSIzMiIgeT0iMTIuNiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJfeDMwXy4xLjE3LjAuMV85XyIgeD0iMzMuOCIgeT0iMTIuNiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF83ODA0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF83OTA2XyIgeD0iMjMuOCIgeT0iMjYuMSIgY2xhc3M9InN0MTAiIHdpZHRoPSIxMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzkwNV8iIHg9IjI0LjIiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwOV8iIHg9IjI2LjEiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNzgwOF8iIHg9IjI4IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDdfIiB4PSIyOS44IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDZfIiB4PSIzMS43IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzc4MDVfIiB4PSIzMy41IiB5PSIyNi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzE0XyI+DQoJCTxyZWN0IGlkPSJYTUxJRF8yNl8iIHg9IjguNiIgeT0iMTIuMiIgY2xhc3M9InN0MTAiIHdpZHRoPSI3LjYiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yMl8iIHg9IjkuMSIgeT0iMTIuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xN18iIHg9IjExIiB5PSIxMi41IiBjbGFzcz0ic3QxMiIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzE2XyIgeD0iMTIuOSIgeT0iMTIuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNV8iIHg9IjE0LjciIHk9IjEyLjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMV8iPg0KCQk8cG9seWdvbiBpZD0iWE1MSURfMTNfIiBjbGFzcz0ic3QxMCIgcG9pbnRzPSIxMi43LDI2LjEgMTEuNCwyNi4xIDkuNiwyNi4xIDkuNiwyNy44IDExLjQsMjcuOCAxMi43LDI3LjggMjIuNywyNy44IDIyLjcsMjYuMSAJCQ0KCQkJIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF85XyIgeD0iMTEuOSIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF84XyIgeD0iMTMuOCIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF83XyIgeD0iMTUuNyIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF82XyIgeD0iMTcuNSIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF81XyIgeD0iMTkuNCIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF80XyIgeD0iMjEuMiIgeT0iMjYuNSIgY2xhc3M9InN0MTIiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yM18iIHg9IjEwLjEiIHk9IjI2LjUiIGNsYXNzPSJzdDEyIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCTwvZz4NCgk8cG9seWdvbiBpZD0iWE1MSURfMjlfIiBjbGFzcz0ic3QxMyIgcG9pbnRzPSI3LjksMjQuOCA3LjksMjQuOCA0LjEsMjQuOCA0LjEsMjQuNiAyLjksMjQuNiAyLjksMjcuNiA0LjEsMjcuNiA0LjEsMjcuNCA0LjEsMjcuNCANCgkJNC4xLDI3LjQgNy45LDI3LjQgNy45LDI3LjQgNy45LDI3LjQgNy45LDI3LjQgOCwyNy40IDgsMjQuOCAJIi8+DQo8L2c+DQo8L3N2Zz4NCg==';


class arduinoMega {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'arduinoMega';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const eviveState = sourceTarget.getCustomState(arduinoMega.STATE_KEY);
            if (eviveState) {
                newTarget.setCustomState(arduinoMega.STATE_KEY, Clone.simple(eviveState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'arduinoMega',
            name: formatMessage({
                id: 'arduinoMega.arduinoMega',
                default: 'Arduino Mega',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#0fbd8c',
            colourSecondary: '#0da57a',
            colourTertiary: '#0b8e69',
            blocks: [
                {
                    opcode: 'arduinoMegaStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'arduinoMega.arduinoMegaStartUp',
                        default: 'when Arduino Mega starts up',
                        description: 'Arduino Mega Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'arduinoMega.digitalRead',
                        default: 'read status of digital pin [PIN]',
                        description: 'Read digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '13'
                        }
                    }
                },
                {
                    opcode: 'analogRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'arduinoMega.analogRead',
                        default: 'read analog pin [PIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'digitalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'arduinoMega.digitalWrite',
                        default: 'set digital pin [PIN] output as [MODE]',
                        description: 'Write digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '13'
                        },
                        MODE: {
                            type: ArgumentType.STRING,
                            menu: 'digitalModes',
                            defaultValue: 'true'
                        }

                    }
                },
                {
                    opcode: 'setPWM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'arduinoMega.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Set pwm pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '13'
                        },
                        VALUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: '255'
                        }

                    }
                },
                '---',
                {
                    opcode: 'playTone',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'arduinoMega.playTone',
                        default: 'play tone on [PIN] of note [NOTE] & beat [BEATS]',
                        description: 'Play a tone'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '46'
                        },
                        NOTE: {
                            type: ArgumentType.NUMBER,
                            menu: 'notes',
                            defaultValue: '65'
                        },
                        BEATS: {
                            type: ArgumentType.NUMBER,
                            menu: 'beatsList',
                            defaultValue: '500'
                        }
                    }
                },
                '---',
                {
                    opcode: 'readTimer',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'arduinoMega.readTimer',
                        default: 'get timer value',
                        description: 'Reports timer value'
                    })
                },
                {
                    opcode: 'resetTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'arduinoMega.resetTimer',
                        default: 'reset timer',
                        description: 'Resets timer'
                    })
                },
                '---',
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'arduinoMega.cast',
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
                    opcode: 'map',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.map',
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
                }
            ],
            menus: {
                analogPins: [
                    // {text: 'VSS_Voltage_Sense', value: '6'},
                    // {text: 'VVR_Voltage_Sense', value: '7'},
                    { text: 'A0', value: '0' },
                    { text: 'A1', value: '1' },
                    { text: 'A2', value: '2' },
                    { text: 'A3', value: '3' },
                    { text: 'A4', value: '4' },
                    { text: 'A5', value: '5' },
                    { text: 'A6', value: '6' },
                    { text: 'A7', value: '7' },
                    { text: 'A8', value: '8' },
                    { text: 'A9', value: '9' },
                    { text: 'A10', value: '10' },
                    { text: 'A11', value: '11' },
                    { text: 'A12', value: '12' },
                    { text: 'A13', value: '13' },
                    { text: 'A14', value: '14' },
                    { text: 'A15', value: '15' }
                ],
                digitalPins: [
                    '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16',
                    '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30',
                    '31', '32', '33', '34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44',
                    '45', '46', '47', '48', '49', '50', '51', '52', '53',
                    { text: 'A0', value: '54' },
                    { text: 'A1', value: '55' },
                    { text: 'A2', value: '56' },
                    { text: 'A3', value: '57' },
                    { text: 'A4', value: '58' },
                    { text: 'A5', value: '59' },
                    { text: 'A6', value: '60' },
                    { text: 'A7', value: '61' },
                    { text: 'A8', value: '62' },
                    { text: 'A9', value: '63' },
                    { text: 'A10', value: '64' },
                    { text: 'A11', value: '65' },
                    { text: 'A12', value: '66' },
                    { text: 'A13', value: '67' },
                    { text: 'A14', value: '68' },
                    { text: 'A15', value: '69' }
                ],
                pwmPins: [
                    '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '44', '45', '46'
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
                tonePins: [
                    '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '44', '45', '46'
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
                ]
            }
        };
    }

    arduinoMegaStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_arduinoMegaStartUp');
            return;
        }
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

    playTone(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.playTone(args, util, this);
        }
        return RealtimeMode.playTone(args, util, this);
    }

    readTimer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.readTimer(args, util, this);
        }
        return RealtimeMode.readTimer(args, util, this);
    }

    resetTimer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.resetTimer(args, util, this);
        }
        return RealtimeMode.resetTimer(args, util, this);
    }

    cast(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.cast(args, util, this);
        }
        return RealtimeMode.cast(args, util, this);
    }

    map(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.map(args, util, this);
        }
        return RealtimeMode.map(args, util, this);
    }
}

module.exports = arduinoMega;
