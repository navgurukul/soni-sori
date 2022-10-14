const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCAzMiAzMiIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgMzIgMzI7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNFQ0VDRUM7fQ0KCS5zdDF7ZmlsbDojNkQ2RDZEO30NCgkuc3Qye2ZpbGw6IzFDNzVCQztzdHJva2U6IzFCNDE5NTtzdHJva2Utd2lkdGg6MC41O3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0M3tmaWxsOm5vbmU7c3Ryb2tlOiNGRkZGRkY7c3Ryb2tlLXdpZHRoOjAuODA4O3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0NHtmaWxsOiMwREI1Qjc7fQ0KCS5zdDV7ZmlsbDojM0EzQTNBO30NCgkuc3Q2e2ZpbGw6I0I1MDAwMDt9DQoJLnN0N3tmaWxsOiM0MDQwNDA7fQ0KCS5zdDh7ZmlsbDojMzczNzM3O30NCgkuc3Q5e2ZpbGw6IzhDOEM4Qzt9DQoJLnN0MTB7ZmlsbDojNzc3Nzc3O30NCgkuc3QxMXtmaWxsOiNGQzAwMDA7fQ0KCS5zdDEye2ZpbGw6IzAwQUExMDt9DQoJLnN0MTN7ZmlsbDojRkZBRDM2O3N0cm9rZTojRkZBRDM2O3N0cm9rZS13aWR0aDowLjgwODtzdHJva2UtbGluZWpvaW46cm91bmQ7fQ0KCS5zdDE0e2ZpbGw6I0ZGNDA0MDtzdHJva2U6I0ZGNDA0MDtzdHJva2Utd2lkdGg6MC44MDg7c3Ryb2tlLWxpbmVqb2luOnJvdW5kO30NCgkuc3QxNXtmaWxsOiNEOUQ5RDk7fQ0KCS5zdDE2e2ZpbGw6IzM4MzgzODt9DQo8L3N0eWxlPg0KPGcgaWQ9IlhNTElEXzE4MjQ1XyI+DQoJPHBhdGggY2xhc3M9InN0MCIgZD0iTTcuMSwyOS40Yy0xLjMsMC0yLjMtMS0yLjMtMi4zVjQuOWMwLjEtMS40LDEuMS0yLjMsMi4zLTIuM0gyNWMxLjMsMCwyLjMsMSwyLjMsMi4zdjIyLjMNCgkJYy0wLjEsMS4zLTEuMSwyLjItMi40LDIuMkg3LjF6Ii8+DQoJPHBhdGggY2xhc3M9InN0MSIgZD0iTTI1LDNjMSwwLDEuOSwwLjgsMS45LDEuOXYyMi4zYy0wLjEsMS0wLjksMS44LTIsMS44SDcuMWMtMSwwLTEuOS0wLjgtMS45LTEuOVY0LjlDNS4zLDMuOCw2LjEsMyw3LjEsM0gyNQ0KCQkgTTI1LDIuMkg3LjFjLTEuNCwwLTIuNiwxLjEtMi43LDIuNmMwLDAsMCwwLDAsMC4xdjIyLjJjMCwxLjUsMS4yLDIuNywyLjcsMi43aDE3LjhjMS41LDAsMi43LTEuMSwyLjgtMi41di0wLjFWNC45DQoJCUMyNy43LDMuNCwyNi41LDIuMiwyNSwyLjJMMjUsMi4yeiIvPg0KPC9nPg0KPHJlY3QgaWQ9IlhNTElEXzE4MjM4XyIgeD0iNi4yIiB5PSIxNC4xIiBjbGFzcz0ic3QyIiB3aWR0aD0iMTkuNiIgaGVpZ2h0PSIxMS4zIi8+DQo8cGF0aCBpZD0iWE1MSURfMTgxOTJfIiBjbGFzcz0ic3QzIiBkPSJNMjQsMjIuNiIvPg0KPGNpcmNsZSBpZD0iWE1MSURfNjM4NF8iIGNsYXNzPSJzdDQiIGN4PSIxOC43IiBjeT0iMjcuNSIgcj0iMS4yIi8+DQo8Y2lyY2xlIGlkPSJYTUxJRF82MzA5XyIgY2xhc3M9InN0NSIgY3g9IjIxLjUiIGN5PSIyNy41IiByPSIxLjIiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzYyMzhfIiBjbGFzcz0ic3Q2IiBjeD0iMjQuMiIgY3k9IjI3LjUiIHI9IjEuMiIvPg0KPHJlY3QgaWQ9IlhNTElEXzI5MjVfIiB4PSIxMS42IiB5PSIxNSIgY2xhc3M9InN0NyIgd2lkdGg9IjQuNSIgaGVpZ2h0PSIwLjgiLz4NCjxyZWN0IGlkPSJYTUxJRF8yNjIxXyIgeD0iMTYuOCIgeT0iMTUiIGNsYXNzPSJzdDciIHdpZHRoPSI2IiBoZWlnaHQ9IjAuOCIvPg0KPHJlY3QgaWQ9IlhNTElEXzI3OThfIiB4PSIxNy4yIiB5PSIyMy44IiBjbGFzcz0ic3Q3IiB3aWR0aD0iNy40IiBoZWlnaHQ9IjAuOCIvPg0KPHJlY3QgaWQ9IlhNTElEXzI1NDFfIiB4PSIxMC45IiB5PSIyMy44IiBjbGFzcz0ic3Q3IiB3aWR0aD0iNiIgaGVpZ2h0PSIwLjgiLz4NCjxwYXRoIGlkPSJYTUxJRF8xMzkzXyIgY2xhc3M9InN0OCIgZD0iTTcuMyw0LjdoNi45YzAuMywwLDAuNSwwLjIsMC41LDAuNXY0LjljMCwwLjMtMC4yLDAuNS0wLjUsMC41SDcuM2MtMC4zLDAtMC41LTAuMi0wLjUtMC41DQoJVjUuMkM2LjgsNSw3LDQuNyw3LjMsNC43eiIvPg0KPHBhdGggaWQ9IlhNTElEXzEzNjVfIiBjbGFzcz0ic3Q5IiBkPSJNMTMuOSwyNi41Ii8+DQo8cGF0aCBpZD0iWE1MSURfMTM1MV8iIGNsYXNzPSJzdDkiIGQ9Ik0xMC4xLDExLjkiLz4NCjxwYXRoIGlkPSJYTUxJRF8xMzQ1XyIgY2xhc3M9InN0OSIgZD0iTTguOCwxMS45Ii8+DQo8Y2lyY2xlIGlkPSJYTUxJRF8xMzQzXyIgY2xhc3M9InN0MTAiIGN4PSIxOC42IiBjeT0iNi4zIiByPSIxIi8+DQo8Y2lyY2xlIGlkPSJYTUxJRF8xMzE3XyIgY2xhc3M9InN0MTEiIGN4PSIyMy43IiBjeT0iNi4zIiByPSIwLjkiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzEzMTNfIiBjbGFzcz0ic3QxMCIgY3g9IjE4LjYiIGN5PSI5LjUiIHI9IjEiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzEyODdfIiBjbGFzcz0ic3QxMiIgY3g9IjIzLjciIGN5PSI5LjUiIHI9IjAuOSIvPg0KPGNpcmNsZSBpZD0iWE1MSURfMTM1NV8iIGNsYXNzPSJzdDEzIiBjeD0iMTAuMyIgY3k9IjEyLjEiIHI9IjAuNSIvPg0KPGNpcmNsZSBpZD0iWE1MSURfMTM0OV8iIGNsYXNzPSJzdDE0IiBjeD0iNy44IiBjeT0iMTIuMSIgcj0iMC41Ii8+DQo8cmVjdCBpZD0iWE1MSURfMzY2XyIgeD0iNy41IiB5PSIxNSIgY2xhc3M9InN0NyIgd2lkdGg9IjMuNCIgaGVpZ2h0PSIyLjMiLz4NCjxyZWN0IGlkPSJYTUxJRF8xMzk3XyIgeD0iMTYiIHk9IjE2LjUiIGNsYXNzPSJzdDE1IiB3aWR0aD0iOC41IiBoZWlnaHQ9IjYuNiIvPg0KPHJlY3QgaWQ9IlhNTElEXzJfIiB4PSI3LjUiIHk9IjIyLjMiIGNsYXNzPSJzdDciIHdpZHRoPSIzLjkiIGhlaWdodD0iMS4zIi8+DQo8cmVjdCBpZD0iWE1MSURfNF8iIHg9IjcuNSIgeT0iMTguNiIgY2xhc3M9InN0NyIgd2lkdGg9IjUuMiIgaGVpZ2h0PSIyLjYiLz4NCjxyZWN0IHg9IjIwLjkiIHk9IjUuNiIgY2xhc3M9InN0MTYiIHdpZHRoPSIwLjYiIGhlaWdodD0iMS41Ii8+DQo8cmVjdCB4PSIyMC45IiB5PSI4LjgiIGNsYXNzPSJzdDE2IiB3aWR0aD0iMC42IiBoZWlnaHQ9IjEuNSIvPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCAzMiAzMiIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgMzIgMzI7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiNFQ0VDRUM7fQ0KCS5zdDF7ZmlsbDojNkQ2RDZEO30NCgkuc3Qye2ZpbGw6IzFDNzVCQztzdHJva2U6IzFCNDE5NTtzdHJva2Utd2lkdGg6MC41O3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0M3tmaWxsOm5vbmU7c3Ryb2tlOiNGRkZGRkY7c3Ryb2tlLXdpZHRoOjAuODA4O3N0cm9rZS1saW5lam9pbjpyb3VuZDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0NHtmaWxsOiMwREI1Qjc7fQ0KCS5zdDV7ZmlsbDojM0EzQTNBO30NCgkuc3Q2e2ZpbGw6I0I1MDAwMDt9DQoJLnN0N3tmaWxsOiM0MDQwNDA7fQ0KCS5zdDh7ZmlsbDojMzczNzM3O30NCgkuc3Q5e2ZpbGw6IzhDOEM4Qzt9DQoJLnN0MTB7ZmlsbDojNzc3Nzc3O30NCgkuc3QxMXtmaWxsOiNGQzAwMDA7fQ0KCS5zdDEye2ZpbGw6IzAwQUExMDt9DQoJLnN0MTN7ZmlsbDojRkZBRDM2O3N0cm9rZTojRkZBRDM2O3N0cm9rZS13aWR0aDowLjgwODtzdHJva2UtbGluZWpvaW46cm91bmQ7fQ0KCS5zdDE0e2ZpbGw6I0ZGNDA0MDtzdHJva2U6I0ZGNDA0MDtzdHJva2Utd2lkdGg6MC44MDg7c3Ryb2tlLWxpbmVqb2luOnJvdW5kO30NCgkuc3QxNXtmaWxsOiNEOUQ5RDk7fQ0KCS5zdDE2e2ZpbGw6IzM4MzgzODt9DQo8L3N0eWxlPg0KPGcgaWQ9IlhNTElEXzE4MjQ1XyI+DQoJPHBhdGggY2xhc3M9InN0MCIgZD0iTTcuMSwyOS40Yy0xLjMsMC0yLjMtMS0yLjMtMi4zVjQuOWMwLjEtMS40LDEuMS0yLjMsMi4zLTIuM0gyNWMxLjMsMCwyLjMsMSwyLjMsMi4zdjIyLjMNCgkJYy0wLjEsMS4zLTEuMSwyLjItMi40LDIuMkg3LjF6Ii8+DQoJPHBhdGggY2xhc3M9InN0MSIgZD0iTTI1LDNjMSwwLDEuOSwwLjgsMS45LDEuOXYyMi4zYy0wLjEsMS0wLjksMS44LTIsMS44SDcuMWMtMSwwLTEuOS0wLjgtMS45LTEuOVY0LjlDNS4zLDMuOCw2LjEsMyw3LjEsM0gyNQ0KCQkgTTI1LDIuMkg3LjFjLTEuNCwwLTIuNiwxLjEtMi43LDIuNmMwLDAsMCwwLDAsMC4xdjIyLjJjMCwxLjUsMS4yLDIuNywyLjcsMi43aDE3LjhjMS41LDAsMi43LTEuMSwyLjgtMi41di0wLjFWNC45DQoJCUMyNy43LDMuNCwyNi41LDIuMiwyNSwyLjJMMjUsMi4yeiIvPg0KPC9nPg0KPHJlY3QgaWQ9IlhNTElEXzE4MjM4XyIgeD0iNi4yIiB5PSIxNC4xIiBjbGFzcz0ic3QyIiB3aWR0aD0iMTkuNiIgaGVpZ2h0PSIxMS4zIi8+DQo8cGF0aCBpZD0iWE1MSURfMTgxOTJfIiBjbGFzcz0ic3QzIiBkPSJNMjQsMjIuNiIvPg0KPGNpcmNsZSBpZD0iWE1MSURfNjM4NF8iIGNsYXNzPSJzdDQiIGN4PSIxOC43IiBjeT0iMjcuNSIgcj0iMS4yIi8+DQo8Y2lyY2xlIGlkPSJYTUxJRF82MzA5XyIgY2xhc3M9InN0NSIgY3g9IjIxLjUiIGN5PSIyNy41IiByPSIxLjIiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzYyMzhfIiBjbGFzcz0ic3Q2IiBjeD0iMjQuMiIgY3k9IjI3LjUiIHI9IjEuMiIvPg0KPHJlY3QgaWQ9IlhNTElEXzI5MjVfIiB4PSIxMS42IiB5PSIxNSIgY2xhc3M9InN0NyIgd2lkdGg9IjQuNSIgaGVpZ2h0PSIwLjgiLz4NCjxyZWN0IGlkPSJYTUxJRF8yNjIxXyIgeD0iMTYuOCIgeT0iMTUiIGNsYXNzPSJzdDciIHdpZHRoPSI2IiBoZWlnaHQ9IjAuOCIvPg0KPHJlY3QgaWQ9IlhNTElEXzI3OThfIiB4PSIxNy4yIiB5PSIyMy44IiBjbGFzcz0ic3Q3IiB3aWR0aD0iNy40IiBoZWlnaHQ9IjAuOCIvPg0KPHJlY3QgaWQ9IlhNTElEXzI1NDFfIiB4PSIxMC45IiB5PSIyMy44IiBjbGFzcz0ic3Q3IiB3aWR0aD0iNiIgaGVpZ2h0PSIwLjgiLz4NCjxwYXRoIGlkPSJYTUxJRF8xMzkzXyIgY2xhc3M9InN0OCIgZD0iTTcuMyw0LjdoNi45YzAuMywwLDAuNSwwLjIsMC41LDAuNXY0LjljMCwwLjMtMC4yLDAuNS0wLjUsMC41SDcuM2MtMC4zLDAtMC41LTAuMi0wLjUtMC41DQoJVjUuMkM2LjgsNSw3LDQuNyw3LjMsNC43eiIvPg0KPHBhdGggaWQ9IlhNTElEXzEzNjVfIiBjbGFzcz0ic3Q5IiBkPSJNMTMuOSwyNi41Ii8+DQo8cGF0aCBpZD0iWE1MSURfMTM1MV8iIGNsYXNzPSJzdDkiIGQ9Ik0xMC4xLDExLjkiLz4NCjxwYXRoIGlkPSJYTUxJRF8xMzQ1XyIgY2xhc3M9InN0OSIgZD0iTTguOCwxMS45Ii8+DQo8Y2lyY2xlIGlkPSJYTUxJRF8xMzQzXyIgY2xhc3M9InN0MTAiIGN4PSIxOC42IiBjeT0iNi4zIiByPSIxIi8+DQo8Y2lyY2xlIGlkPSJYTUxJRF8xMzE3XyIgY2xhc3M9InN0MTEiIGN4PSIyMy43IiBjeT0iNi4zIiByPSIwLjkiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzEzMTNfIiBjbGFzcz0ic3QxMCIgY3g9IjE4LjYiIGN5PSI5LjUiIHI9IjEiLz4NCjxjaXJjbGUgaWQ9IlhNTElEXzEyODdfIiBjbGFzcz0ic3QxMiIgY3g9IjIzLjciIGN5PSI5LjUiIHI9IjAuOSIvPg0KPGNpcmNsZSBpZD0iWE1MSURfMTM1NV8iIGNsYXNzPSJzdDEzIiBjeD0iMTAuMyIgY3k9IjEyLjEiIHI9IjAuNSIvPg0KPGNpcmNsZSBpZD0iWE1MSURfMTM0OV8iIGNsYXNzPSJzdDE0IiBjeD0iNy44IiBjeT0iMTIuMSIgcj0iMC41Ii8+DQo8cmVjdCBpZD0iWE1MSURfMzY2XyIgeD0iNy41IiB5PSIxNSIgY2xhc3M9InN0NyIgd2lkdGg9IjMuNCIgaGVpZ2h0PSIyLjMiLz4NCjxyZWN0IGlkPSJYTUxJRF8xMzk3XyIgeD0iMTYiIHk9IjE2LjUiIGNsYXNzPSJzdDE1IiB3aWR0aD0iOC41IiBoZWlnaHQ9IjYuNiIvPg0KPHJlY3QgaWQ9IlhNTElEXzJfIiB4PSI3LjUiIHk9IjIyLjMiIGNsYXNzPSJzdDciIHdpZHRoPSIzLjkiIGhlaWdodD0iMS4zIi8+DQo8cmVjdCBpZD0iWE1MSURfNF8iIHg9IjcuNSIgeT0iMTguNiIgY2xhc3M9InN0NyIgd2lkdGg9IjUuMiIgaGVpZ2h0PSIyLjYiLz4NCjxyZWN0IHg9IjIwLjkiIHk9IjUuNiIgY2xhc3M9InN0MTYiIHdpZHRoPSIwLjYiIGhlaWdodD0iMS41Ii8+DQo8cmVjdCB4PSIyMC45IiB5PSI4LjgiIGNsYXNzPSJzdDE2IiB3aWR0aD0iMC42IiBoZWlnaHQ9IjEuNSIvPg0KPC9zdmc+DQo=';


class evive {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'evive';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const eviveState = sourceTarget.getCustomState(evive.STATE_KEY);
            if (eviveState) {
                newTarget.setCustomState(evive.STATE_KEY, Clone.simple(eviveState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'evive',
            name: formatMessage({
                id: 'evive.evive',
                default: 'evive',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#1c75bc',
            colourSecondary: '#026295',
            colourTertiary: '#005388',
            blocks: [
                {
                    opcode: 'eviveStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'evive.eviveStartUp',
                        default: 'when evive starts up',
                        description: 'Evive Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'tactileSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'evive.tactileSwitch',
                        default: 'tactile switch [TACTILE_SWITCH] pressed?',
                        description: 'Read tactile switch'
                    }),
                    arguments: {
                        TACTILE_SWITCH: {
                            type: ArgumentType.NUMBER,
                            menu: 'tactileSwitch',
                            defaultValue: '38'
                        }
                    }
                },
                {
                    opcode: 'slideSwitch',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'evive.slideSwitch',
                        default: 'slide switch [SLIDE_SWITCH] is in state [SWITCH_DIRECTION]?',
                        description: 'Read slide switch'
                    }),
                    arguments: {
                        SLIDE_SWITCH: {
                            type: ArgumentType.NUMBER,
                            menu: 'slideSwitch',
                            defaultValue: '1'
                        },
                        SWITCH_DIRECTION: {
                            type: ArgumentType.STRING,
                            menu: 'slideSwitchDir',
                            defaultValue: 'up'
                        }
                    }
                },
                {
                    opcode: 'navigationKey',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'evive.navigationKey',
                        default: 'navigation key is in state [NAV_DIRECTION]?',
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
                        id: 'evive.potentiometer',
                        default: 'potentiometer [POTENTIOMETER] reading',
                        description: 'Read potentiometer'
                    }),
                    arguments: {
                        POTENTIOMETER: {
                            type: ArgumentType.NUMBER,
                            menu: 'potentiometer',
                            defaultValue: '9'
                        }
                    }
                },
                {
                    opcode: 'touchSensor',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'evive.touchSensor',
                        default: 'channel [TOUCH_PINS] touched?',
                        description: 'Read touch sensor'
                    }),
                    arguments: {
                        TOUCH_PINS: {
                            type: ArgumentType.NUMBER,
                            menu: 'touchChannel',
                            defaultValue: '1'
                        }
                    }
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'evive.digitalRead',
                        default: 'read state of digital pin [PIN]',
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
                        id: 'evive.analogRead',
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
                        id: 'evive.digitalWrite',
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
                        id: 'evive.setPWM',
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
                            defaultValue: '100'
                        }
                    }
                },
                '---',
                {
                    opcode: 'playTone',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'evive.playTone',
                        default: 'play tone on [PIN] of note [NOTE] & beat [BEATS]',
                        description: 'Play a tone'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'tonePins',
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
                    opcode: 'setClockTime',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'evive.setClockTime',
                        default: 'set clock to [HRS] hours, [MINS] minutes, [SECS] seconds',
                        description: 'Set clock time'
                    }),
                    arguments: {
                        HRS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'hrs',
                            defaultValue: '10'
                        },
                        MINS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'mins',
                            defaultValue: '30'

                        },
                        SECS: {
                            type: ArgumentType.NUMBER,
                            // menu: 'secs',
                            defaultValue: '45'
                        }
                    }
                },
                {
                    opcode: 'setClockDate',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'evive.setClockDate',
                        default: 'set date to [DATE] / [MONTH] / [YEAR] and weekday [WEEKDAY]',
                        description: 'Set date'
                    }),
                    arguments: {
                        DATE: {
                            type: ArgumentType.NUMBER,
                            // menu: 'date',
                            defaultValue: '1'
                        },
                        MONTH: {
                            type: ArgumentType.STRING,
                            menu: 'month',
                            defaultValue: '1'
                        },
                        YEAR: {
                            type: ArgumentType.NUMBER,
                            // menu: 'year',
                            menu: 'year',
                            defaultValue: '19'
                        },
                        WEEKDAY: {
                            type: ArgumentType.STRING,
                            menu: 'weekday',
                            defaultValue: '3'
                        }
                    }
                },
                {
                    opcode: 'getDataFromClock',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.getDataFromClock',
                        default: 'get [TIME] from clock',
                        description: 'Get data from clock'
                    }),
                    arguments: {
                        TIME: {
                            type: ArgumentType.STRING,
                            menu: 'clock',
                            defaultValue: '11'
                        }
                    }
                },
                '---',
                {
                    opcode: 'voltageSense',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.voltageSense',
                        default: 'get voltage reading from [CHANNEL] channel',
                        description: 'Sense Voltage'
                    }),
                    arguments: {
                        CHANNEL: {
                            type: ArgumentType.NUMBER,
                            menu: 'voltageChannel',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'currentSense',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.currentSense',
                        default: 'get current reading from blue channel',
                        description: 'Sense Current'
                    })
                },
                '---',
                {
                    opcode: 'readTimer',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.readTimer',
                        default: 'get timer value',
                        description: 'Reports timer value'
                    })
                },
                {
                    opcode: 'resetTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'evive.resetTimer',
                        default: 'reset timer',
                        description: 'Resets timer'
                    })
                },
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'evive.cast',
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
                tactileSwitch: [
                    { text: '1', value: '38' },
                    { text: '2', value: '39' }
                ],
                slideSwitch: ['1', '2'],
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
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option2',
                            default: 'right',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.navKeyFormattedMessages.option3',
                            default: 'down',
                            description: 'Menu'
                        }), value: '3'
                    }
                ],
                potentiometer: [
                    { text: '1', value: '9' },
                    { text: '2', value: '10' }
                ],
                touchChannel: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                digitalPins: [
                    '2', '3', '4', '5', '6', '7', '8', '9',
                    '10', '11', '12', '13', '22', '23', '24', '25', '26', '27'
                ],
                analogPins: [
                    // {text: 'VSS_Voltage_Sense', value: '6'},
                    // {text: 'VVR_Voltage_Sense', value: '7'},
                    { text: 'A0', value: '0' },
                    { text: 'A1', value: '1' },
                    { text: 'A2', value: '2' },
                    { text: 'A3', value: '3' },
                    { text: 'A4', value: '4' },
                    { text: 'A5', value: '5' },
                    { text: 'A12', value: '12' },
                    { text: 'A13', value: '13' },
                    { text: 'A14', value: '14' },
                    { text: 'A15', value: '15' }
                ],
                pwmPins: [
                    { text: '13', value: '13' },
                    { text: '2', value: '2' },
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
                    { text: '44', value: '44' },
                    { text: '45', value: '45' }
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
                    {
                        text: formatMessage({
                            id: 'extension.menu.tonePins.option1',
                            default: 'evive Buzzer',
                            description: 'Menu'
                        }), value: '46'
                    },
                    { text: '2', value: '2' },
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
                    { text: '13', value: '13' }
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
                weekday: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option1',
                            default: 'Mon',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option2',
                            default: 'Tue',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option3',
                            default: 'Wed',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option4',
                            default: 'Thu',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option5',
                            default: 'Fri',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option6',
                            default: 'Sat',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.weekdayFormattedMessages.option7',
                            default: 'Sun',
                            description: 'Menu'
                        }), value: '1'
                    }
                ],
                month: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option1',
                            default: 'Jan',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option2',
                            default: 'Feb',
                            description: 'Menu'
                        }), value: '2'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option3',
                            default: 'Mar',
                            description: 'Menu'
                        }), value: '3'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option4',
                            default: 'Apr',
                            description: 'Menu'
                        }), value: '4'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option5',
                            default: 'May',
                            description: 'Menu'
                        }), value: '5'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option6',
                            default: 'Jun',
                            description: 'Menu'
                        }), value: '6'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option7',
                            default: 'Jul',
                            description: 'Menu'
                        }), value: '7'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option8',
                            default: 'Aug',
                            description: 'Menu'
                        }), value: '8'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option9',
                            default: 'Sep',
                            description: 'Menu'
                        }), value: '9'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option10',
                            default: 'Oct',
                            description: 'Menu'
                        }), value: '10'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option11',
                            default: 'Nov',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.monthFormattedMessages.option12',
                            default: 'Dec',
                            description: 'Menu'
                        }), value: '12'
                    }
                ],
                clock: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option1',
                            default: 'Hour',
                            description: 'Menu'
                        }), value: '11'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option2',
                            default: 'Minute',
                            description: 'Menu'
                        }), value: '12'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option3',
                            default: 'Second',
                            description: 'Menu'
                        }), value: '13'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option4',
                            default: 'Day',
                            description: 'Menu'
                        }), value: '21'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option5',
                            default: 'Month',
                            description: 'Menu'
                        }), value: '22'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option6',
                            default: 'Year',
                            description: 'Menu'
                        }), value: '23'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.clockFormattedMessages.option7',
                            default: 'Weekday',
                            description: 'Menu'
                        }), value: '24'
                    }
                ],
                voltageChannel: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option1',
                            default: 'red',
                            description: 'Menu'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.colorFormattedMessages.option3',
                            default: 'blue',
                            description: 'Menu'
                        }), value: '2'
                    }
                ],
                year: [
                    { text: '2015', value: '15' },
                    { text: '2016', value: '16' },
                    { text: '2017', value: '17' },
                    { text: '2018', value: '18' },
                    { text: '2019', value: '19' },
                    { text: '2020', value: '20' },
                    { text: '2021', value: '21' },
                    { text: '2022', value: '22' },
                    { text: '2023', value: '23' },
                    { text: '2024', value: '24' },
                    { text: '2025', value: '25' },
                    { text: '2026', value: '26' },
                    { text: '2027', value: '27' },
                    { text: '2028', value: '28' },
                    { text: '2029', value: '29' },
                    { text: '2030', value: '30' },
                    { text: '2031', value: '31' },
                    { text: '2032', value: '32' },
                    { text: '2033', value: '33' },
                    { text: '2034', value: '34' },
                    { text: '2035', value: '35' },
                    { text: '2036', value: '36' },
                    { text: '2037', value: '37' },
                    { text: '2038', value: '38' },
                    { text: '2039', value: '39' },
                    { text: '2040', value: '40' },
                    { text: '2041', value: '41' },
                    { text: '2042', value: '42' },
                    { text: '2043', value: '43' },
                    { text: '2044', value: '44' },
                    { text: '2045', value: '45' },
                    { text: '2046', value: '46' },
                    { text: '2047', value: '47' },
                    { text: '2048', value: '48' },
                    { text: '2049', value: '49' }
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

    eviveStartUp() {
        if (this.runtime.getCode) {
            console.log('hardware_eviveStartUp');
            return;
        }
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

    touchSensor(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.touchSensor(args, util, this);
        }
        return RealtimeMode.touchSensor(args, util, this);
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

    setClockTime(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setClockTime(args, util, this);
        }
        return RealtimeMode.setClockTime(args, util, this);
    }

    setClockDate(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setClockDate(args, util, this);
        }
        return RealtimeMode.setClockDate(args, util, this);
    }

    getDataFromClock(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getDataFromClock(args, util, this);
        }
        return RealtimeMode.getDataFromClock(args, util, this);
    }

    voltageSense(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.voltageSense(args, util, this);
        }
        return RealtimeMode.voltageSense(args, util, this);
    }

    currentSense(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.currentSense(args, util, this);
        }
        return RealtimeMode.currentSense(args, util, this);
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

module.exports = evive;
