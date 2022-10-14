const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMwRjczOTE7c3Ryb2tlOiMwNjU5NkQ7c3Ryb2tlLXdpZHRoOjAuNzU7fQ0KCS5zdDF7ZmlsbDojQ0NDQ0NDO30NCgkuc3Qye2ZpbGw6IzFBMUExQTt9DQoJLnN0M3tmaWxsOiM4MDgyODU7fQ0KCS5zdDR7ZmlsbDojMjMxRjIwO30NCgkuc3Q1e2ZpbGw6IzQxNDA0Mjt9DQoJLnN0NntmaWxsOiNCQ0JFQzA7fQ0KCS5zdDd7ZmlsbDojNDA0MDQwO30NCgkuc3Q4e2ZpbGw6IzhDODY2Mzt9DQoJLnN0OXtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDt9DQoJLnN0MTB7ZmlsbDojRkZGRkZGO3N0cm9rZTojOUE5MTZDO3N0cm9rZS13aWR0aDowLjI1O30NCgkuc3QxMXtmaWxsOiM5Qjk1N0I7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF82MV8iPg0KCTxyZWN0IGlkPSJYTUxJRF8xXyIgeD0iMS44IiB5PSIxMS4zIiBjbGFzcz0ic3QwIiB3aWR0aD0iMzcuNyIgaGVpZ2h0PSIxNy40Ii8+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzE2XyIgY2xhc3M9InN0MSIgcG9pbnRzPSIyNS4zLDE5IDI1LjMsMTcuNSAyNC44LDE3LjUgMjIuNCwxNy41IDIxLjksMTcuNSAyMS45LDE5IDIyLDE5IDIyLDIxLjEgMjEuOSwyMS4xIA0KCQkyMS45LDIyLjcgMjIuNCwyMi43IDI0LjgsMjIuNyAyNS4zLDIyLjcgMjUuMywyMS4xIDI1LjMsMjEuMSAyNS4zLDE5IAkiLz4NCgkNCgkJPHJlY3QgaWQ9IlhNTElEXzcwNDBfIiB4PSIxMy42IiB5PSIxOCIgdHJhbnNmb3JtPSJtYXRyaXgoLTAuNzA3MSAtMC43MDcxIDAuNzA3MSAtMC43MDcxIDEyLjY4NDUgNDUuNDI5MikiIGNsYXNzPSJzdDIiIHdpZHRoPSI0LjQiIGhlaWdodD0iNC4zIi8+DQoJPGcgaWQ9IlhNTElEXzYwXyI+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNjM3XyIgeD0iMS45IiB5PSIyMy4zIiBjbGFzcz0ic3QzIiB3aWR0aD0iMi4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTYzNl8iIHg9IjciIHk9IjIzLjMiIGNsYXNzPSJzdDMiIHdpZHRoPSIyLjMiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNjM1XyIgeD0iMS45IiB5PSIxNS4yIiBjbGFzcz0ic3QzIiB3aWR0aD0iMi4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTYyOV8iIHg9IjciIHk9IjE1LjIiIGNsYXNzPSJzdDMiIHdpZHRoPSIyLjMiIGhlaWdodD0iMS43Ii8+DQoJPC9nPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjI2XyIgeD0iNy45IiB5PSIxOC40IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjIzXyIgeD0iNy45IiB5PSIxOS4xIiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjIxXyIgeD0iNy45IiB5PSIxOS45IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjE5XyIgeD0iNy45IiB5PSIyMC42IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjE3XyIgeD0iNy45IiB5PSIyMS40IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjEzXyIgeD0iMCIgeT0iMTcuMiIgY2xhc3M9InN0NSIgd2lkdGg9IjcuNyIgaGVpZ2h0PSI1LjgiLz4NCgk8cGF0aCBpZD0iWE1MSURfMTFfIiBjbGFzcz0ic3Q2IiBkPSJNNCwxNy4zQzQsMTcuMyw0LjEsMTcuMyw0LDE3LjNsMC4xLDAuM2MwLDAsMCwwLjEtMC4xLDAuMUgxLjljMCwwLTAuMSwwLTAuMS0wLjF2LTAuMg0KCQljMCwwLDAtMC4xLDAuMS0wLjFINHogTTEuMywxNy44bDAuMywwLjNjMCwwLDAuMSwwLjEsMC4xLDAuMUg0YzAsMCwwLjEsMCwwLjEsMC4xdjAuMmMwLDAsMCwwLjEtMC4xLDAuMUgwLjljMCwwLTAuMSwwLTAuMS0wLjENCgkJdi0wLjJjMCwwLDAtMC4xLDAuMS0wLjFsMC4zLTAuM0MxLjIsMTcuOCwxLjIsMTcuOCwxLjMsMTcuOHogTTcsMTcuOEM3LDE3LjgsNy4xLDE3LjgsNywxNy44bDAuMSw0LjNjMCwwLDAsMC4xLTAuMSwwLjFINi43DQoJCWMwLDAtMC4xLDAtMC4xLTAuMXYtNC4yYzAsMCwwLTAuMSwwLjEtMC4xSDd6IE0xLjIsMjIuM0wwLjksMjJjMCwwLTAuMS0wLjEtMC4xLTAuMXYtMC4yYzAsMCwwLTAuMSwwLjEtMC4xSDRjMCwwLDAuMSwwLDAuMSwwLjENCgkJdjAuMmMwLDAsMCwwLjEtMC4xLDAuMUgxLjdjMCwwLTAuMSwwLTAuMSwwLjFsLTAuMywwLjNDMS4yLDIyLjMsMS4yLDIyLjMsMS4yLDIyLjN6IE0wLjgsMjEuMUwwLjgsMjEuMWMwLTAuMSwwLTAuMSwwLjEtMC4xDQoJCWwzLjItMC4yYzAsMCwwLjEsMCwwLjEtMC4xdi0xLjJjMCwwLDAtMC4xLTAuMS0wLjFsLTMuMi0wLjJjMCwwLTAuMSwwLTAuMS0wLjF2LTAuMWMwLDAsMC0wLjEsMC4xLTAuMWw0LDAuMmMwLDAsMC4xLDAsMC4xLDAuMQ0KCQl2MS41YzAsMCwwLDAuMS0wLjEsMC4xTDAuOCwyMS4xQzAuOSwyMS4xLDAuOCwyMS4xLDAuOCwyMS4xeiBNNCwyMi40QzQsMjIuNCw0LjEsMjIuNCw0LDIyLjRsMC4xLDAuM2MwLDAsMCwwLjEtMC4xLDAuMUgxLjkNCgkJYzAsMC0wLjEsMC0wLjEtMC4xdi0wLjJjMCwwLDAtMC4xLDAuMS0wLjFINHogTTAsMjMuNmg4LjJ2LTJIOHYtM2gwLjJ2LTJIMFYyMy42eiIvPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF8xNTUyXyIgY2xhc3M9InN0NyIgcG9pbnRzPSIzMy41LDIxLjQgMzUsMjEuNCAzNi4zLDIxLjQgMzcuOCwyMS40IDM4LjQsMjAuOCAzOC40LDE5LjMgMzcuOCwxOC44IDM2LjMsMTguOCANCgkJMzUsMTguOCAzMy41LDE4LjggMzIuOSwxOS4zIDMyLjksMjAuOCAJIi8+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzE1NTFfIiBjbGFzcz0ic3Q3IiBwb2ludHM9IjMzLjUsMTcuNiAzNSwxNy42IDM2LjMsMTcuNiAzNy44LDE3LjYgMzguNCwxNy4xIDM4LjQsMTUuNSAzNy44LDE1IDM2LjMsMTUgMzUsMTUgDQoJCTMzLjUsMTUgMzIuOSwxNS41IDMyLjksMTcuMSAJIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzE3XyIgeD0iMzMuNyIgeT0iMTUuOCIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxyZWN0IGlkPSJYTUxJRF8yMl8iIHg9IjM2LjUiIHk9IjE1LjgiIGNsYXNzPSJzdDgiIHdpZHRoPSIxIiBoZWlnaHQ9IjEiLz4NCgk8cmVjdCBpZD0iWE1MSURfMjVfIiB4PSIzMy43IiB5PSIxOS42IiBjbGFzcz0ic3Q4IiB3aWR0aD0iMSIgaGVpZ2h0PSIxIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzI0XyIgeD0iMzYuNSIgeT0iMTkuNiIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF8xNTY1XyIgY2xhc3M9InN0NyIgcG9pbnRzPSIzMy41LDI1LjIgMzUsMjUuMiAzNi4zLDI1LjIgMzcuOCwyNS4yIDM4LjQsMjQuNiAzOC40LDIzLjEgMzcuOCwyMi41IDM2LjMsMjIuNSANCgkJMzUsMjIuNSAzMy41LDIyLjUgMzIuOSwyMy4xIDMyLjksMjQuNiAJIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzI3XyIgeD0iMzMuNyIgeT0iMjMuNCIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxyZWN0IGlkPSJYTUxJRF8yNl8iIHg9IjM2LjUiIHk9IjIzLjQiIGNsYXNzPSJzdDgiIHdpZHRoPSIxIiBoZWlnaHQ9IjEiLz4NCgk8Y2lyY2xlIGlkPSJYTUxJRF8xODAwMF8iIGNsYXNzPSJzdDkiIGN4PSIyMy42IiBjeT0iMjAuMSIgcj0iMS4zIi8+DQoJPGcgaWQ9IlhNTElEXzNfIj4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTgwNTRfIiBjbGFzcz0ic3QxMCIgY3g9IjciIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yXyIgeD0iNi41IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNV8iIGNsYXNzPSJzdDEwIiBjeD0iMTAiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF80XyIgeD0iOS42IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfN18iIGNsYXNzPSJzdDEwIiBjeD0iMTMuMSIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzZfIiB4PSIxMi43IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfOV8iIGNsYXNzPSJzdDEwIiBjeD0iMTYuMiIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzhfIiB4PSIxNS43IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTNfIiBjbGFzcz0ic3QxMCIgY3g9IjE5LjMiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMF8iIHg9IjE4LjgiIHk9IjEyLjkiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8xNV8iIGNsYXNzPSJzdDEwIiBjeD0iMjIuMyIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzE0XyIgeD0iMjEuOSIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzI4XyIgY2xhc3M9InN0MTAiIGN4PSIyNS40IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjNfIiB4PSIyNSIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzMwXyIgY2xhc3M9InN0MTAiIGN4PSIyOC41IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjlfIiB4PSIyOCIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzMyXyIgY2xhc3M9InN0MTAiIGN4PSIzMS41IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMzFfIiB4PSIzMS4xIiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMzRfIiBjbGFzcz0ic3QxMCIgY3g9IjM0LjYiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8zM18iIHg9IjM0LjIiIHk9IjEyLjkiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8zNl8iIGNsYXNzPSJzdDEwIiBjeD0iMzcuNyIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzM1XyIgeD0iMzcuMiIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8zN18iPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF81OV8iIGNsYXNzPSJzdDEwIiBjeD0iNyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzU4XyIgeD0iNi41IiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNTdfIiBjbGFzcz0ic3QxMCIgY3g9IjEwIiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNTZfIiB4PSI5LjYiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF81NV8iIGNsYXNzPSJzdDEwIiBjeD0iMTMuMSIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzU0XyIgeD0iMTIuNyIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzUzXyIgY2xhc3M9InN0MTAiIGN4PSIxNi4yIiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNTJfIiB4PSIxNS43IiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNTFfIiBjbGFzcz0ic3QxMCIgY3g9IjE5LjMiIGN5PSIyNi43IiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF81MF8iIHg9IjE4LjgiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF80OV8iIGNsYXNzPSJzdDEwIiBjeD0iMjIuMyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzQ4XyIgeD0iMjEuOSIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQ3XyIgY2xhc3M9InN0MTAiIGN4PSIyNS40IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDZfIiB4PSIyNSIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQ1XyIgY2xhc3M9InN0MTAiIGN4PSIyOC41IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDRfIiB4PSIyOCIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQzXyIgY2xhc3M9InN0MTAiIGN4PSIzMS41IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDJfIiB4PSIzMS4xIiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNDFfIiBjbGFzcz0ic3QxMCIgY3g9IjM0LjYiIGN5PSIyNi43IiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF80MF8iIHg9IjM0LjIiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8zOV8iIGNsYXNzPSJzdDEwIiBjeD0iMzcuNyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzM4XyIgeD0iMzcuMiIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMwRjczOTE7c3Ryb2tlOiMwNjU5NkQ7c3Ryb2tlLXdpZHRoOjAuNzU7fQ0KCS5zdDF7ZmlsbDojQ0NDQ0NDO30NCgkuc3Qye2ZpbGw6IzFBMUExQTt9DQoJLnN0M3tmaWxsOiM4MDgyODU7fQ0KCS5zdDR7ZmlsbDojMjMxRjIwO30NCgkuc3Q1e2ZpbGw6IzQxNDA0Mjt9DQoJLnN0NntmaWxsOiNCQ0JFQzA7fQ0KCS5zdDd7ZmlsbDojNDA0MDQwO30NCgkuc3Q4e2ZpbGw6IzhDODY2Mzt9DQoJLnN0OXtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDt9DQoJLnN0MTB7ZmlsbDojRkZGRkZGO3N0cm9rZTojOUE5MTZDO3N0cm9rZS13aWR0aDowLjI1O30NCgkuc3QxMXtmaWxsOiM5Qjk1N0I7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF82MV8iPg0KCTxyZWN0IGlkPSJYTUxJRF8xXyIgeD0iMS44IiB5PSIxMS4zIiBjbGFzcz0ic3QwIiB3aWR0aD0iMzcuNyIgaGVpZ2h0PSIxNy40Ii8+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzE2XyIgY2xhc3M9InN0MSIgcG9pbnRzPSIyNS4zLDE5IDI1LjMsMTcuNSAyNC44LDE3LjUgMjIuNCwxNy41IDIxLjksMTcuNSAyMS45LDE5IDIyLDE5IDIyLDIxLjEgMjEuOSwyMS4xIA0KCQkyMS45LDIyLjcgMjIuNCwyMi43IDI0LjgsMjIuNyAyNS4zLDIyLjcgMjUuMywyMS4xIDI1LjMsMjEuMSAyNS4zLDE5IAkiLz4NCgkNCgkJPHJlY3QgaWQ9IlhNTElEXzcwNDBfIiB4PSIxMy42IiB5PSIxOCIgdHJhbnNmb3JtPSJtYXRyaXgoLTAuNzA3MSAtMC43MDcxIDAuNzA3MSAtMC43MDcxIDEyLjY4NDUgNDUuNDI5MikiIGNsYXNzPSJzdDIiIHdpZHRoPSI0LjQiIGhlaWdodD0iNC4zIi8+DQoJPGcgaWQ9IlhNTElEXzYwXyI+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNjM3XyIgeD0iMS45IiB5PSIyMy4zIiBjbGFzcz0ic3QzIiB3aWR0aD0iMi4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTYzNl8iIHg9IjciIHk9IjIzLjMiIGNsYXNzPSJzdDMiIHdpZHRoPSIyLjMiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xNjM1XyIgeD0iMS45IiB5PSIxNS4yIiBjbGFzcz0ic3QzIiB3aWR0aD0iMi4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTYyOV8iIHg9IjciIHk9IjE1LjIiIGNsYXNzPSJzdDMiIHdpZHRoPSIyLjMiIGhlaWdodD0iMS43Ii8+DQoJPC9nPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjI2XyIgeD0iNy45IiB5PSIxOC40IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjIzXyIgeD0iNy45IiB5PSIxOS4xIiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjIxXyIgeD0iNy45IiB5PSIxOS45IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjE5XyIgeD0iNy45IiB5PSIyMC42IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjE3XyIgeD0iNy45IiB5PSIyMS40IiBjbGFzcz0ic3Q0IiB3aWR0aD0iMS42IiBoZWlnaHQ9IjAuNCIvPg0KCTxyZWN0IGlkPSJYTUxJRF8xNjEzXyIgeD0iMCIgeT0iMTcuMiIgY2xhc3M9InN0NSIgd2lkdGg9IjcuNyIgaGVpZ2h0PSI1LjgiLz4NCgk8cGF0aCBpZD0iWE1MSURfMTFfIiBjbGFzcz0ic3Q2IiBkPSJNNCwxNy4zQzQsMTcuMyw0LjEsMTcuMyw0LDE3LjNsMC4xLDAuM2MwLDAsMCwwLjEtMC4xLDAuMUgxLjljMCwwLTAuMSwwLTAuMS0wLjF2LTAuMg0KCQljMCwwLDAtMC4xLDAuMS0wLjFINHogTTEuMywxNy44bDAuMywwLjNjMCwwLDAuMSwwLjEsMC4xLDAuMUg0YzAsMCwwLjEsMCwwLjEsMC4xdjAuMmMwLDAsMCwwLjEtMC4xLDAuMUgwLjljMCwwLTAuMSwwLTAuMS0wLjENCgkJdi0wLjJjMCwwLDAtMC4xLDAuMS0wLjFsMC4zLTAuM0MxLjIsMTcuOCwxLjIsMTcuOCwxLjMsMTcuOHogTTcsMTcuOEM3LDE3LjgsNy4xLDE3LjgsNywxNy44bDAuMSw0LjNjMCwwLDAsMC4xLTAuMSwwLjFINi43DQoJCWMwLDAtMC4xLDAtMC4xLTAuMXYtNC4yYzAsMCwwLTAuMSwwLjEtMC4xSDd6IE0xLjIsMjIuM0wwLjksMjJjMCwwLTAuMS0wLjEtMC4xLTAuMXYtMC4yYzAsMCwwLTAuMSwwLjEtMC4xSDRjMCwwLDAuMSwwLDAuMSwwLjENCgkJdjAuMmMwLDAsMCwwLjEtMC4xLDAuMUgxLjdjMCwwLTAuMSwwLTAuMSwwLjFsLTAuMywwLjNDMS4yLDIyLjMsMS4yLDIyLjMsMS4yLDIyLjN6IE0wLjgsMjEuMUwwLjgsMjEuMWMwLTAuMSwwLTAuMSwwLjEtMC4xDQoJCWwzLjItMC4yYzAsMCwwLjEsMCwwLjEtMC4xdi0xLjJjMCwwLDAtMC4xLTAuMS0wLjFsLTMuMi0wLjJjMCwwLTAuMSwwLTAuMS0wLjF2LTAuMWMwLDAsMC0wLjEsMC4xLTAuMWw0LDAuMmMwLDAsMC4xLDAsMC4xLDAuMQ0KCQl2MS41YzAsMCwwLDAuMS0wLjEsMC4xTDAuOCwyMS4xQzAuOSwyMS4xLDAuOCwyMS4xLDAuOCwyMS4xeiBNNCwyMi40QzQsMjIuNCw0LjEsMjIuNCw0LDIyLjRsMC4xLDAuM2MwLDAsMCwwLjEtMC4xLDAuMUgxLjkNCgkJYzAsMC0wLjEsMC0wLjEtMC4xdi0wLjJjMCwwLDAtMC4xLDAuMS0wLjFINHogTTAsMjMuNmg4LjJ2LTJIOHYtM2gwLjJ2LTJIMFYyMy42eiIvPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF8xNTUyXyIgY2xhc3M9InN0NyIgcG9pbnRzPSIzMy41LDIxLjQgMzUsMjEuNCAzNi4zLDIxLjQgMzcuOCwyMS40IDM4LjQsMjAuOCAzOC40LDE5LjMgMzcuOCwxOC44IDM2LjMsMTguOCANCgkJMzUsMTguOCAzMy41LDE4LjggMzIuOSwxOS4zIDMyLjksMjAuOCAJIi8+DQoJPHBvbHlnb24gaWQ9IlhNTElEXzE1NTFfIiBjbGFzcz0ic3Q3IiBwb2ludHM9IjMzLjUsMTcuNiAzNSwxNy42IDM2LjMsMTcuNiAzNy44LDE3LjYgMzguNCwxNy4xIDM4LjQsMTUuNSAzNy44LDE1IDM2LjMsMTUgMzUsMTUgDQoJCTMzLjUsMTUgMzIuOSwxNS41IDMyLjksMTcuMSAJIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzE3XyIgeD0iMzMuNyIgeT0iMTUuOCIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxyZWN0IGlkPSJYTUxJRF8yMl8iIHg9IjM2LjUiIHk9IjE1LjgiIGNsYXNzPSJzdDgiIHdpZHRoPSIxIiBoZWlnaHQ9IjEiLz4NCgk8cmVjdCBpZD0iWE1MSURfMjVfIiB4PSIzMy43IiB5PSIxOS42IiBjbGFzcz0ic3Q4IiB3aWR0aD0iMSIgaGVpZ2h0PSIxIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzI0XyIgeD0iMzYuNSIgeT0iMTkuNiIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxwb2x5Z29uIGlkPSJYTUxJRF8xNTY1XyIgY2xhc3M9InN0NyIgcG9pbnRzPSIzMy41LDI1LjIgMzUsMjUuMiAzNi4zLDI1LjIgMzcuOCwyNS4yIDM4LjQsMjQuNiAzOC40LDIzLjEgMzcuOCwyMi41IDM2LjMsMjIuNSANCgkJMzUsMjIuNSAzMy41LDIyLjUgMzIuOSwyMy4xIDMyLjksMjQuNiAJIi8+DQoJPHJlY3QgaWQ9IlhNTElEXzI3XyIgeD0iMzMuNyIgeT0iMjMuNCIgY2xhc3M9InN0OCIgd2lkdGg9IjEiIGhlaWdodD0iMSIvPg0KCTxyZWN0IGlkPSJYTUxJRF8yNl8iIHg9IjM2LjUiIHk9IjIzLjQiIGNsYXNzPSJzdDgiIHdpZHRoPSIxIiBoZWlnaHQ9IjEiLz4NCgk8Y2lyY2xlIGlkPSJYTUxJRF8xODAwMF8iIGNsYXNzPSJzdDkiIGN4PSIyMy42IiBjeT0iMjAuMSIgcj0iMS4zIi8+DQoJPGcgaWQ9IlhNTElEXzNfIj4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTgwNTRfIiBjbGFzcz0ic3QxMCIgY3g9IjciIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yXyIgeD0iNi41IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNV8iIGNsYXNzPSJzdDEwIiBjeD0iMTAiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF80XyIgeD0iOS42IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfN18iIGNsYXNzPSJzdDEwIiBjeD0iMTMuMSIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzZfIiB4PSIxMi43IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfOV8iIGNsYXNzPSJzdDEwIiBjeD0iMTYuMiIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzhfIiB4PSIxNS43IiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMTNfIiBjbGFzcz0ic3QxMCIgY3g9IjE5LjMiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMF8iIHg9IjE4LjgiIHk9IjEyLjkiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8xNV8iIGNsYXNzPSJzdDEwIiBjeD0iMjIuMyIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzE0XyIgeD0iMjEuOSIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzI4XyIgY2xhc3M9InN0MTAiIGN4PSIyNS40IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjNfIiB4PSIyNSIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzMwXyIgY2xhc3M9InN0MTAiIGN4PSIyOC41IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjlfIiB4PSIyOCIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzMyXyIgY2xhc3M9InN0MTAiIGN4PSIzMS41IiBjeT0iMTMuMyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMzFfIiB4PSIzMS4xIiB5PSIxMi45IiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfMzRfIiBjbGFzcz0ic3QxMCIgY3g9IjM0LjYiIGN5PSIxMy4zIiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8zM18iIHg9IjM0LjIiIHk9IjEyLjkiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8zNl8iIGNsYXNzPSJzdDEwIiBjeD0iMzcuNyIgY3k9IjEzLjMiIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzM1XyIgeD0iMzcuMiIgeT0iMTIuOSIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KCTxnIGlkPSJYTUxJRF8zN18iPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF81OV8iIGNsYXNzPSJzdDEwIiBjeD0iNyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzU4XyIgeD0iNi41IiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNTdfIiBjbGFzcz0ic3QxMCIgY3g9IjEwIiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNTZfIiB4PSI5LjYiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF81NV8iIGNsYXNzPSJzdDEwIiBjeD0iMTMuMSIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzU0XyIgeD0iMTIuNyIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzUzXyIgY2xhc3M9InN0MTAiIGN4PSIxNi4yIiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNTJfIiB4PSIxNS43IiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNTFfIiBjbGFzcz0ic3QxMCIgY3g9IjE5LjMiIGN5PSIyNi43IiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF81MF8iIHg9IjE4LjgiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF80OV8iIGNsYXNzPSJzdDEwIiBjeD0iMjIuMyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzQ4XyIgeD0iMjEuOSIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQ3XyIgY2xhc3M9InN0MTAiIGN4PSIyNS40IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDZfIiB4PSIyNSIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQ1XyIgY2xhc3M9InN0MTAiIGN4PSIyOC41IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDRfIiB4PSIyOCIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJCTxjaXJjbGUgaWQ9IlhNTElEXzQzXyIgY2xhc3M9InN0MTAiIGN4PSIzMS41IiBjeT0iMjYuNyIgcj0iMSIvPg0KCQk8cmVjdCBpZD0iWE1MSURfNDJfIiB4PSIzMS4xIiB5PSIyNi4yIiBjbGFzcz0ic3QxMSIgd2lkdGg9IjAuOSIgaGVpZ2h0PSIwLjkiLz4NCgkJPGNpcmNsZSBpZD0iWE1MSURfNDFfIiBjbGFzcz0ic3QxMCIgY3g9IjM0LjYiIGN5PSIyNi43IiByPSIxIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF80MF8iIHg9IjM0LjIiIHk9IjI2LjIiIGNsYXNzPSJzdDExIiB3aWR0aD0iMC45IiBoZWlnaHQ9IjAuOSIvPg0KCQk8Y2lyY2xlIGlkPSJYTUxJRF8zOV8iIGNsYXNzPSJzdDEwIiBjeD0iMzcuNyIgY3k9IjI2LjciIHI9IjEiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzM4XyIgeD0iMzcuMiIgeT0iMjYuMiIgY2xhc3M9InN0MTEiIHdpZHRoPSIwLjkiIGhlaWdodD0iMC45Ii8+DQoJPC9nPg0KPC9nPg0KPC9zdmc+DQo=';


class arduinoNano {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'arduinoNano';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const eviveState = sourceTarget.getCustomState(arduinoNano.STATE_KEY);
            if (eviveState) {
                newTarget.setCustomState(arduinoNano.STATE_KEY, Clone.simple(eviveState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'arduinoNano',
            name: formatMessage({
                id: 'arduinoNano.arduinoNano',
                default: 'Arduino Nano',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#0fbd8c',
            colourSecondary: '#0da57a',
            colourTertiary: '#0b8e69',
            blocks: [
                {
                    opcode: 'arduinoNanoStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'arduinoNano.arduinoNanoStartUp',
                        default: 'when Arduino Nano starts up',
                        description: 'Arduino Nano Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'arduinoNano.digitalRead',
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
                        id: 'arduinoNano.analogRead',
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
                        id: 'arduinoNano.digitalWrite',
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
                        id: 'arduinoNano.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Set pwm pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '3'
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
                        id: 'arduinoNano.playTone',
                        default: 'play tone on [PIN] of note [NOTE] & beat [BEATS]',
                        description: 'Play a tone'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '3'
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
                        id: 'arduinoNano.readTimer',
                        default: 'get timer value',
                        description: 'Reports timer value'
                    })
                },
                {
                    opcode: 'resetTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'arduinoNano.resetTimer',
                        default: 'reset timer',
                        description: 'Resets timer'
                    })
                },
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'arduinoNano.cast',
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
                    { text: 'A7', value: '7' }
                ],
                digitalPins: [
                    '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13',
                    { text: 'A0', value: '14' },
                    { text: 'A1', value: '15' },
                    { text: 'A2', value: '16' },
                    { text: 'A3', value: '17' },
                    { text: 'A4', value: '18' },
                    { text: 'A5', value: '19' },
                    { text: 'A6', value: '20' },
                    { text: 'A7', value: '21' }
                ],
                pwmPins: [
                    '3', '5', '6', '9', '10', '11'
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
                    '3', '5', '6', '9', '10', '11'
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

    arduinoNanoStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_arduinoNanoStartUp');
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

module.exports = arduinoNano;
