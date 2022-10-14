const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwIDAgMzAuMzcgNDAuMDIiPjxkZWZzPjxjbGlwUGF0aCBpZD0iYSI+PHBhdGggZD0iTTEyLjg4LDMwLjQ0YzEuMjcuMjcsNC4xNi42Nyw1LjktLjY4YTguNDgsOC40OCwwLDAsMCwyLTIuODgsMTguNiwxOC42LDAsMCwxLTUuMzEuNTUsMTcuMzcsMTcuMzcsMCwwLDEtNS43NC0uNjFTMTEsMzAsMTIuODgsMzAuNDRaIiBzdHlsZT0iZmlsbDpub25lIi8+PC9jbGlwUGF0aD48L2RlZnM+PHBhdGggZD0iTTQuNyw0MEE0LjE0LDQuMTQsMCwwLDEsLjU4LDM1Ljg5VjI3LjI2aC42VjI1Ljg0QTEuOTMsMS45MywwLDAsMSwwLDI0LjA3VjE1LjMxYTEuOTIsMS45MiwwLDAsMSwxLjE4LTEuNzdWMTAuNjZBNC40Miw0LjQyLDAsMCwxLDUuMjksNi4yM0w0LjU3LDQuMjhBMi4yOSwyLjI5LDAsMSwxLDgsMi4yOGw0LjA2LDRoNi4yN2w0LTMuOTVhMi4yOSwyLjI5LDAsMSwxLDMuNDcsMmwtLjczLDJhNC40NSw0LjQ1LDAsMCwxLDQuMDYsNC40MlYxMy42YTEuOTIsMS45MiwwLDAsMSwxLjI0LDEuOHY4Ljc2QTEuOTQsMS45NCwwLDAsMSwyOS4xMywyNnYxLjRoLjU5djguNTlhNC4xNCw0LjE0LDAsMCwxLTQsNC4wOFoiIHN0eWxlPSJmaWxsOiNmZmYiLz48cmVjdCB4PSIyNi45NiIgeT0iMTQuNSIgd2lkdGg9IjIuNDMiIGhlaWdodD0iMTAuMzQiIHJ4PSIwLjcxIiBzdHlsZT0iZmlsbDojOTU1M2EwO3N0cm9rZTojNWU1ZTVlO3N0cm9rZS1taXRlcmxpbWl0OjEwO3N0cm9rZS13aWR0aDowLjI1cHgiLz48cmVjdCB4PSIxLjE2IiB5PSIxNC41IiB3aWR0aD0iMi40MyIgaGVpZ2h0PSIxMC4zNCIgcng9IjAuNzEiIHN0eWxlPSJmaWxsOiM5NTUzYTA7c3Ryb2tlOiM1ZTVlNWU7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjAuMjVweCIvPjxwb2x5bGluZSBwb2ludHM9IjIzLjIxIDguMzkgMjUuMzIgMi42NSAyMy42NyAyLjY1IDE3Ljc5IDguMzkgMjIuNDEgOC4yOCIgc3R5bGU9ImZpbGw6Izk1NTNhMCIvPjxwb2x5bGluZSBwb2ludHM9IjIzLjIxIDguMzkgMjUuMzIgMi42NSAyMy42NyAyLjY1IDE3Ljc5IDguMzkgMjIuNDEgOC4yOCIgc3R5bGU9ImZpbGw6bm9uZTtzdHJva2U6IzVlNWU1ZTtzdHJva2UtbWl0ZXJsaW1pdDoxMDtzdHJva2Utd2lkdGg6MC4yNXB4Ii8+PGNpcmNsZSBjeD0iMjQuNzYiIGN5PSIyLjI5IiByPSIxLjE2IiBzdHlsZT0iZmlsbDojZThlN2U3Ii8+PGNpcmNsZSBjeD0iMjQuNzYiIGN5PSIyLjI5IiByPSIxLjE2IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZTojNWU1ZTVlO3N0cm9rZS1taXRlcmxpbWl0OjEwO3N0cm9rZS13aWR0aDowLjI1cHgiLz48cG9seWxpbmUgcG9pbnRzPSI3LjMzIDguMzkgNS4yMSAyLjY1IDYuODYgMi42NSAxMi43NCA4LjM5IDguMTIgOC4yOCIgc3R5bGU9ImZpbGw6Izk1NTNhMCIvPjxwb2x5bGluZSBwb2ludHM9IjcuMzMgOC4zOSA1LjIxIDIuNjUgNi44NiAyLjY1IDEyLjc0IDguMzkgOC4xMiA4LjI4IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZTojNWU1ZTVlO3N0cm9rZS1taXRlcmxpbWl0OjEwO3N0cm9rZS13aWR0aDowLjI1cHgiLz48Y2lyY2xlIGN4PSI1Ljc4IiBjeT0iMi4yOSIgcj0iMS4xNiIgc3R5bGU9ImZpbGw6I2U4ZTdlNyIvPjxjaXJjbGUgY3g9IjUuNzgiIGN5PSIyLjI5IiByPSIxLjE2IiBzdHlsZT0iZmlsbDpub25lO3N0cm9rZTojNWU1ZTVlO3N0cm9rZS1taXRlcmxpbWl0OjEwO3N0cm9rZS13aWR0aDowLjI1cHgiLz48cmVjdCB4PSIyLjM0IiB5PSI3LjMzIiB3aWR0aD0iMjUuOCIgaGVpZ2h0PSIzMS41MSIgcng9IjIuOTQiIHN0eWxlPSJmaWxsOiNlOGU3ZTciLz48cmVjdCB4PSIyLjM0IiB5PSI3LjMzIiB3aWR0aD0iMjUuOCIgaGVpZ2h0PSIzMS41MSIgcng9IjIuOTQiIHN0eWxlPSJmaWxsOm5vbmU7c3Ryb2tlOiM1ZTVlNWU7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjAuMjVweCIvPjxwYXRoIGQ9Ik0xNi43NywyMi4xOXMuNTctNSwzLjQ2LTUuMzMsMy4zNyw0LjgsMy4xOSw1LjMzQTcuMTEsNy4xMSwwLDAsMCwxNi43NywyMi4xOVoiIHN0eWxlPSJmaWxsOiNmZmYiLz48cGF0aCBkPSJNMTMuNjgsMjIuMTlzLS41Ny01LTMuNDYtNS4zM1M2Ljg1LDIxLjY2LDcsMjIuMTlhNi44Niw2Ljg2LDAsMCwxLDMuMjYtLjgyQTYuODEsNi44MSwwLDAsMSwxMy42OCwyMi4xOVoiIHN0eWxlPSJmaWxsOiNmZmYiLz48Y2lyY2xlIGN4PSIxMC41NiIgY3k9IjE5Ljg2IiByPSIxLjE2Ii8+PGNpcmNsZSBjeD0iMjAuMTgiIGN5PSIxOS44NiIgcj0iMS4xNiIvPjxjaXJjbGUgY3g9IjEwLjIiIGN5PSIxOS4yIiByPSIwLjMxIiBzdHlsZT0iZmlsbDojZmZmIi8+PGNpcmNsZSBjeD0iMTkuOCIgY3k9IjE5LjIiIHI9IjAuMzEiIHN0eWxlPSJmaWxsOiNmZmYiLz48cGF0aCBkPSJNMTIuODgsMzAuNDRjMS4yNy4yNyw0LjE2LjY3LDUuOS0uNjhhOC40OCw4LjQ4LDAsMCwwLDItMi44OCwxOC42LDE4LjYsMCwwLDEtNS4zMS41NSwxNy4zNywxNy4zNywwLDAsMS01Ljc0LS42MVMxMSwzMCwxMi44OCwzMC40NFoiLz48ZyBzdHlsZT0iY2xpcC1wYXRoOnVybCgjYSkiPjxwYXRoIGQ9Ik0yMC43MSwyNS45NGE2LjgyLDYuODIsMCwwLDEtNS4xNywyLjI1Yy0zLjM1LDAtNC43MS0xLjExLTUuOTQtMi40NVoiIHN0eWxlPSJmaWxsOiNmZmYiLz48cGF0aCBkPSJNMTguMzUsMzAuNTZjLS4xMy0uNTMtMS40My0xLjI2LTIuODEtMS4yNi0xLjIzLDAtMy4xNS4zLTMuMTksMS4zOHMzLjc5LDEuNDMsNC44LjlDMTcuOCwzMS4yNSwxOC41MSwzMS4wOSwxOC4zNSwzMC41NloiIHN0eWxlPSJmaWxsOiNkYTFmMjYiLz48L2c+PHBhdGggZD0iTTI4LjczLDI4LjM2djcuNDdhMywzLDAsMCwxLTMsM2gtMjFhMywzLDAsMCwxLTMtM1YyOC4zNkg1LjU5bDMsMy43SDIxLjg0bDMtMy43WiIgc3R5bGU9ImZpbGw6Izk1NTNhMCIvPjxwYXRoIGQ9Ik0yOC43MywyOC4zNnY3LjQ3YTMsMywwLDAsMS0zLDNoLTIxYTMsMywwLDAsMS0zLTNWMjguMzZINS41OWwzLDMuN0gyMS44NGwzLTMuN1oiIHN0eWxlPSJmaWxsOm5vbmU7c3Ryb2tlOiM1ZTVlNWU7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjAuMjVweCIvPjxwYXRoIGQ9Ik0xOCwxNi4xNmE1LjA4LDUuMDgsMCwwLDEsNS4xNy4zMlMyMC43MywxMywxOCwxNi4xNloiLz48cGF0aCBkPSJNMTIuNzQsMTYuMTZhNS4wNiw1LjA2LDAsMCwwLTUuMTYuMzJTMTAsMTMsMTIuNzQsMTYuMTZaIi8+PC9zdmc+';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAyNC4xLjEsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiM5NTUzQTA7c3Ryb2tlOiM1RTVFNUU7c3Ryb2tlLXdpZHRoOjAuMjg5NDtzdHJva2UtbWl0ZXJsaW1pdDoxMDt9DQoJLnN0MXtmaWxsOiM5NTUzQTA7fQ0KCS5zdDJ7ZmlsbDpub25lO3N0cm9rZTojNUU1RTVFO3N0cm9rZS13aWR0aDowLjI4OTQ7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KCS5zdDN7ZmlsbDojRThFN0U3O30NCgkuc3Q0e2ZpbGw6I0ZGRkZGRjt9DQoJLnN0NXtjbGlwLXBhdGg6dXJsKCNTVkdJRF8yXyk7fQ0KCS5zdDZ7ZmlsbDojREExRjI2O30NCgkuc3Q3e3N0cm9rZTojNUU1RTVFO3N0cm9rZS13aWR0aDowLjI1O3N0cm9rZS1taXRlcmxpbWl0OjEwO30NCgkuc3Q4e3N0cm9rZTojNUU1RTVFO3N0cm9rZS13aWR0aDowLjI4OTQ7c3Ryb2tlLW1pdGVybGltaXQ6MTA7fQ0KPC9zdHlsZT4NCjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0zMi44LDE0LjRoMC45YzAuNSwwLDAuOCwwLjQsMC44LDAuOHY5YzAsMC41LTAuNCwwLjgtMC44LDAuOGgtMC45Yy0wLjUsMC0wLjgtMC40LTAuOC0wLjh2LTkNCglDMzIsMTQuNywzMi40LDE0LjQsMzIuOCwxNC40eiIvPg0KPHBhdGggY2xhc3M9InN0MCIgZD0iTTYuMywxNC40aDAuOWMwLjUsMCwwLjgsMC40LDAuOCwwLjh2OUM4LDI0LjYsNy42LDI1LDcuMiwyNUg2LjNjLTAuNSwwLTAuOC0wLjQtMC44LTAuOHYtOQ0KCUM1LjUsMTQuNyw1LjksMTQuNCw2LjMsMTQuNHoiLz4NCjxwb2x5bGluZSBjbGFzcz0ic3QxIiBwb2ludHM9IjI4LjIsOC4xIDMwLjMsMi4yIDI4LjYsMi4yIDIyLjYsOC4xIDI3LjMsNy45ICIvPg0KPHBvbHlsaW5lIGNsYXNzPSJzdDIiIHBvaW50cz0iMjguMiw4LjEgMzAuMywyLjIgMjguNiwyLjIgMjIuNiw4LjEgMjcuMyw3LjkgIi8+DQo8Y2lyY2xlIGNsYXNzPSJzdDMiIGN4PSIyOS44IiBjeT0iMS44IiByPSIxLjIiLz4NCjxjaXJjbGUgY2xhc3M9InN0MiIgY3g9IjI5LjgiIGN5PSIxLjgiIHI9IjEuMiIvPg0KPHBvbHlsaW5lIGNsYXNzPSJzdDEiIHBvaW50cz0iMTEuOCw4LjEgOS43LDIuMiAxMS4zLDIuMiAxNy40LDguMSAxMi42LDcuOSAiLz4NCjxwb2x5bGluZSBjbGFzcz0ic3QyIiBwb2ludHM9IjExLjgsOC4xIDkuNywyLjIgMTEuMywyLjIgMTcuNCw4LjEgMTIuNiw3LjkgIi8+DQo8Y2lyY2xlIGNsYXNzPSJzdDMiIGN4PSIxMC4yIiBjeT0iMS44IiByPSIxLjIiLz4NCjxjaXJjbGUgY2xhc3M9InN0MiIgY3g9IjEwLjIiIGN5PSIxLjgiIHI9IjEuMiIvPg0KPHBhdGggY2xhc3M9InN0MyIgZD0iTTEwLjEsN2gxOS43YzEuOSwwLDMuNCwxLjUsMy40LDMuNFYzNmMwLDEuOS0xLjUsMy40LTMuNCwzLjRIMTAuMWMtMS45LDAtMy40LTEuNS0zLjQtMy40VjEwLjQNCglDNi43LDguNSw4LjIsNywxMC4xLDd6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTAuMSw3aDE5LjdjMS45LDAsMy40LDEuNSwzLjQsMy40VjM2YzAsMS45LTEuNSwzLjQtMy40LDMuNEgxMC4xYy0xLjksMC0zLjQtMS41LTMuNC0zLjRWMTAuNA0KCUM2LjcsOC41LDguMiw3LDEwLjEsN3oiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yMS41LDIyLjNjMCwwLDAuNi01LjIsMy42LTUuNXMzLjUsNC45LDMuMyw1LjVjLTEtMC42LTIuMi0wLjktMy40LTAuOEMyMy44LDIxLjQsMjIuNiwyMS43LDIxLjUsMjIuM3oiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xOC40LDIyLjNjMCwwLTAuNi01LjItMy42LTUuNXMtMy41LDQuOS0zLjMsNS41YzEtMC42LDIuMi0wLjgsMy4zLTAuOEMxNi4xLDIxLjQsMTcuMywyMS43LDE4LjQsMjIuM3oiLz4NCjxjaXJjbGUgY3g9IjE1LjIiIGN5PSIxOS45IiByPSIxLjIiLz4NCjxjaXJjbGUgY3g9IjI1LjEiIGN5PSIxOS45IiByPSIxLjIiLz4NCjxjaXJjbGUgY2xhc3M9InN0NCIgY3g9IjE0LjgiIGN5PSIxOS4yIiByPSIwLjMiLz4NCjxjaXJjbGUgY2xhc3M9InN0NCIgY3g9IjI0LjYiIGN5PSIxOS4yIiByPSIwLjMiLz4NCjxwYXRoIGlkPSJTVkdJRC0yIiBkPSJNMTcuNSwzMC44YzEuMywwLjMsNC4zLDAuNyw2LjEtMC43YzAuOS0wLjgsMS42LTEuOCwyLTNjLTEuOCwwLjUtMy42LDAuNy01LjUsMC42Yy0yLDAuMS00LTAuMS01LjktMC42DQoJQzE0LjMsMjcsMTUuNiwzMC4zLDE3LjUsMzAuOHoiLz4NCjxnPg0KCTxkZWZzPg0KCQk8cGF0aCBpZD0iU1ZHSURfMV8iIGQ9Ik0xNy41LDMwLjhjMS4zLDAuMyw0LjMsMC43LDYuMS0wLjdjMC45LTAuOCwxLjYtMS44LDItM2MtMS44LDAuNS0zLjYsMC43LTUuNSwwLjZjLTIsMC4xLTQtMC4xLTUuOS0wLjYNCgkJCUMxNC4zLDI3LDE1LjYsMzAuMywxNy41LDMwLjh6Ii8+DQoJPC9kZWZzPg0KCTxjbGlwUGF0aCBpZD0iU1ZHSURfMl8iPg0KCQk8dXNlIHhsaW5rOmhyZWY9IiNTVkdJRF8xXyIgIHN0eWxlPSJvdmVyZmxvdzp2aXNpYmxlOyIvPg0KCTwvY2xpcFBhdGg+DQoJPGcgY2xhc3M9InN0NSI+DQoJCTxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0yNS42LDI2LjFjLTEuNCwxLjUtMy4zLDIuMy01LjMsMi4zYy0zLjUsMC00LjktMS4yLTYuMS0yLjVMMjUuNiwyNi4xeiIvPg0KCQk8cGF0aCBjbGFzcz0ic3Q2IiBkPSJNMjMuMiwzMC45Yy0wLjEtMC42LTEuNS0xLjMtMi45LTEuM2MtMS40LDAtMy4zLDAuMy0zLjMsMS40YzAsMS4xLDMuOSwxLjUsNC45LDAuOQ0KCQkJQzIyLjYsMzEuNiwyMy4zLDMxLjQsMjMuMiwzMC45eiIvPg0KCTwvZz4NCjwvZz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yOC4zLDE1LjIiLz4NCjxwYXRoIGNsYXNzPSJzdDciIGQ9Ik0zMi41LDM5LjQiLz4NCjxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik0zMy44LDI4LjZ2Ny43YzAsMS43LTEuNCwzLjEtMy4xLDMuMUg5LjJjLTEuNywwLTMuMS0xLjQtMy4xLTMuMXYtNy43aDRsMy4xLDMuOGgxMy42bDMuMS0zLjhIMzMuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0zMy44LDI4LjZ2Ny43YzAsMS43LTEuNCwzLjEtMy4xLDMuMUg5LjJjLTEuNywwLTMuMS0xLjQtMy4xLTMuMXYtNy43aDRsMy4xLDMuOGgxMy42bDMuMS0zLjhIMzMuOHoiLz4NCjxwYXRoIGQ9Ik0yMi44LDE2LjFjMS43LTAuOSwzLjctMC43LDUuMywwLjNDMjguMSwxNi40LDI1LjYsMTIuNywyMi44LDE2LjF6Ii8+DQo8cGF0aCBkPSJNMTcuNCwxNi4xYy0xLjctMC45LTMuNy0wLjctNS4zLDAuM0MxMi4xLDE2LjQsMTQuNiwxMi43LDE3LjQsMTYuMXoiLz4NCjxwYXRoIGNsYXNzPSJzdDgiIGQ9Ik0yNi4zLDIxLjEiLz4NCjwvc3ZnPg0K';

class quarky {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'quarky';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const quarkyState = sourceTarget.getCustomState(quarky.STATE_KEY);
            if (quarkyState) {
                newTarget.setCustomState(quarky.STATE_KEY, Clone.simple(quarkyState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }


    getInfo() {
        return {
            id: 'quarky',
            name: formatMessage({
                id: 'quarky.quarky',
                default: 'Quarky',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5372e5',
            colourSecondary: '#4657ca',
            colourTertiary: '#3a4ca5',
            blocks: [
                {
                    opcode: 'quarkyStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'quarky.quarkyStartUp',
                        default: 'when Quarky starts up',
                        description: 'quarky Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'quarky.digitalRead',
                        default: 'read state of digital pin [PIN]',
                        description: 'Read digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '18'
                        }
                    }
                },
                {
                    opcode: 'analogRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarky.analogRead',
                        default: 'read analog pin [PIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '33'
                        }
                    }
                },
                {
                    opcode: 'digitalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.digitalWrite',
                        default: 'set digital pin [PIN] output as [STATE]',
                        description: 'Write digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '18'
                        },
                        STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalModes',
                            defaultValue: '1'
                        }

                    }
                },
                {
                    opcode: 'setPWM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Write PWM Value of pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '18'
                        },
                        VALUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: '255',
                        }
                    }
                },
                {
                    opcode: 'bluetoothIndicator',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.bluetoothIndicator',
                        default: '[STATE] bluetooth indicator',
                        description: 'Write PWM Value of pin'
                    }),
                    arguments: {
                        STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'bleMenu',
                            defaultValue: '0'
                        }
                    }
                },
                '---',
                // {
                //     opcode: 'pushButtons',
                //     blockType: BlockType.BOOLEAN,
                //     text: formatMessage({
                //         id: 'quarky.pushButtons',
                //         default: 'is push button [TACTILE_SW] pressed?',
                //         description: 'Read analog pin'
                //     }),
                //     arguments: {
                //         TACTILE_SW: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'tactileMenu',
                //             defaultValue: '1'
                //         }
                //     }
                // },
                // {
                //     opcode: 'touchRead',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         id: 'quarky.touchRead',
                //         default: 'get value of touch pin [TOUCHPIN]',
                //         description: 'Read analog pin'
                //     }),
                //     arguments: {
                //         TOUCHPIN: {
                //             type: ArgumentType.NUMBER,
                //             menu: 'touchPinMenu',
                //             defaultValue: '15'
                //         }
                //     }
                // },
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'quarky.cast',
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
                        id: 'quarky.map',
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
                            defaultValue: '4095'
                        },
                    }
                },
                {
                    message: formatMessage({
                        id: 'quarky.blockSeparatorMessage1',
                        default: 'Deep Sleep (U)',
                        description: 'Blocks separator message'
                    })
                },
                {
                    opcode: 'enableDeepSleep',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.enableDeepSleep',
                        default: 'enable deep sleep',
                        description: 'Deep  Sleep mode on Quarky'
                    }),
                },
                {
                    opcode: 'deepSleepTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.deepSleepTimer',
                        default: 'wake up from deep sleep mode in [TIME] seconds',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        TIME: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '2'
                        },
                    }
                },
                {
                    opcode: 'deepSleepExternalSource',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.deepSleepExternalSource',
                        default: 'wake up from deep sleep mode when [PIN] is [STATE]',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '32'
                        },
                        STATE: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalModes',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    opcode: 'deepSleepTouchPins',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'quarky.deepSleepTouchPins',
                        default: 'wake up from deep sleep mode when [TOUCH_PIN] is greater than [THRESHOLD]',
                        description: 'Deep  Sleep mode on T-watch'
                    }),
                    arguments: {
                        TOUCH_PIN: {
                            type: ArgumentType.STRING,
                            menu: 'touchPinMenu',
                            defaultValue: '15'
                        },
                        THRESHOLD: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '40'
                        }
                    }
                },
                // {
                //     opcode: 'getMacAddr',
                //     blockType: BlockType.REPORTER,
                //     text: formatMessage({
                //         id: 'quarky.getMacAddr',
                //         default: 'get bluetooth Mac Address',
                //         description: 'Gives Bluetooth MAc Address'
                //     })
                // },
            ],
            menus: {
                analogPins: {
                    acceptReporters: true,
                    items: [
                        { text: 'A1', value: '33' },
                        { text: 'A2', value: '32' },
                        { text: 'A3', value: '39' }
                    ]
                },
                digitalModes:
                    [
                        { text: 'HIGH', value: '1' },
                        { text: 'LOW', value: '0' },
                        // acceptReporters: true,
                        // items: [
                        //     {
                        //         text: formatMessage({
                        //             id: 'extension.menu.digitalModes.option1',
                        //             default: 'HIGH',
                        //             description: 'Menu'
                        //         }), value: '1'
                        //     },
                        //     {
                        //         text: formatMessage({
                        //             id: 'extension.menu.digitalModes.option2',
                        //             default: 'LOW',
                        //             description: 'Menu'
                        //         }), value: '0'
                        //     }
                        // ]
                    ],


                digitalPins: {
                    acceptReporters: true,
                    items: [
                        { text: 'D1', value: '18' },
                        { text: 'D2', value: '19' },
                        { text: 'D3', value: '26' },
                        { text: 'T1', value: '15' },
                        { text: 'T2', value: '13' },
                        { text: 'T3', value: '12' },
                        { text: 'T4', value: '14' },
                        { text: 'T5', value: '27' },
                        { text: 'A1', value: '33' },
                        { text: 'A2', value: '32' },
                        { text: 'S1', value: '22' },
                        { text: 'S2', value: '23' },
                    ]
                },

                tactileMenu: [
                    { text: 'A', value: '1' },
                    { text: 'B', value: '2' }
                ],
                bleMenu: [
                    { text: 'disable', value: '1' },
                    { text: 'enable', value: '0' }
                ],
                touchPinMenu: {
                    acceptReporters: true,
                    items: [
                        { text: 'T1', value: '15' },
                        { text: 'T2', value: '13' },
                        { text: 'T3', value: '12' },
                        { text: 'T4', value: '14' },
                        { text: 'T5', value: '27' }
                    ]
                },
                touchPins: [
                    'T1', 'T2', 'T3', 'T4', 'T5'
                ],
                notes: {
                    acceptReporters: true,
                    items: [
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
                    ]
                },
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
                /*,
                textSize: ['1', '2', '3', '4', '5',
                    {text: 'digital', value: '6'},
                    {text: 'ask MM', value: '7'}
                ],
                options: [
                    {text: 'Fill', value: '1'},
                    {text: 'Draw', value: '2'}
                ],
                size: [
                    '1', '2', '3', '4', '5', '6', '7', '8'
                ]*/
            }
        };
    }

    quarkyStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_quarkyStartUp');
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

    bluetoothIndicator(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.bluetoothIndicator(args, util, this);
        }
        return RealtimeMode.bluetoothIndicator(args, util, this);
    }
    // playTone(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.playTone(args, util, this);
    //     }
    //     return RealtimeMode.playTone(args, util, this);
    // }

    // pushButtons(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.pushButtons(args, util, this);
    //     }
    //     return RealtimeMode.pushButtons(args, util, this);
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

    enableDeepSleep(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.enableDeepSleep(args, util, this);
        }
        return RealtimeMode.enableDeepSleep(args, util, this);
    }

    deepSleepTimer(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepTimer(args, util, this);
        }
        return RealtimeMode.deepSleepTimer(args, util, this);
    }

    deepSleepExternalSource(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepExternalSource(args, util, this);
        }
        return RealtimeMode.deepSleepExternalSource(args, util, this);
    }

    deepSleepTouchPins(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.deepSleepTouchPins(args, util, this);
        }
        return RealtimeMode.deepSleepTouchPins(args, util, this);
    }

    // getMacAddr(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.getMacAddr(args, util, this);
    //     }
    //     return RealtimeMode.getMacAddr(args, util, this);
    // }

    // touchRead(args, util) {
    //     if (this.runtime.getCode) {
    //         return CodingMode.touchRead(args, util, this);
    //     }
    //     return RealtimeMode.touchRead(args, util, this);
    // }

}

module.exports = quarky;
