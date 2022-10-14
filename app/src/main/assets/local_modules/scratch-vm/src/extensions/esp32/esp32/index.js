const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMyRDJEMkQ7fQ0KCS5zdDF7ZmlsbDojRkNCRDE0O30NCgkuc3Qye2ZpbGw6Izk5OTk5OTt9DQoJLnN0M3tmaWxsOiNFMkUyRTI7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF8yXyI+DQoJPHJlY3QgaWQ9IlhNTElEXzEyNjI2XyIgeD0iNi44IiB5PSI0IiBjbGFzcz0ic3QwIiB3aWR0aD0iMjYuMyIgaGVpZ2h0PSIzMiIvPg0KCTxnIGlkPSJYTUxJRF8xMjU2OV8iPg0KCQk8cmVjdCBpZD0iWE1MSURfMTI2MjFfIiB4PSI2LjgiIHk9IjEyLjMiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjciIGhlaWdodD0iMS4zIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMjU4NF8iIHg9IjYuOCIgeT0iMTYuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTgwXyIgeD0iNi44IiB5PSIyMC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS43IiBoZWlnaHQ9IjEuMyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTI1NzNfIiB4PSI2LjgiIHk9IjI0LjQiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjciIGhlaWdodD0iMS4zIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMjU3MV8iIHg9IjYuOCIgeT0iMjguNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTcwXyIgeD0iNi44IiB5PSIzMi40IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS43IiBoZWlnaHQ9IjEuMyIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMTI1NTJfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTY3XyIgeD0iMzEuNSIgeT0iMTIuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTY1XyIgeD0iMzEuNSIgeT0iMTYuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTYzXyIgeD0iMzEuNSIgeT0iMjAuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTYyXyIgeD0iMzEuNSIgeT0iMjQuNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTU0XyIgeD0iMzEuNSIgeT0iMjguNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTUzXyIgeD0iMzEuNSIgeT0iMzIuNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzE1MTZfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTQ4XyIgeD0iMjcuNCIgeT0iMzQuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIxLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzkzOTlfIiB4PSIyMy40IiB5PSIzNC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjk3Nl8iIHg9IjE5LjMiIHk9IjM0LjMiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjMiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yOTc1XyIgeD0iMTUuMyIgeT0iMzQuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIxLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzI5NzRfIiB4PSIxMS4zIiB5PSIzNC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCTwvZz4NCgk8cG9seWdvbiBpZD0iWE1MSURfNTgwXyIgcG9pbnRzPSIzMCw1LjkgMzAsNSAyMyw1IDIzLDEwIDIxLDEwIDIxLDUgMTYsNSAxNiwxMCAxNCwxMCAxNCw1IDksNSA5LDExIDEwLDExIDEwLDYgMTMsNiAxMywxMSAxNywxMSANCgkJMTcsNiAyMCw2IDIwLDExIDI0LDExIDI0LDYgMjcsNiAyNywxMSAyOCwxMSAyOCw2IDI5LDYgMjksMTEgMzAsMTEgMzAsNS45IAkiLz4NCgk8cmVjdCBpZD0iWE1MSURfNTI1XyIgeD0iOC41IiB5PSIxMS43IiBjbGFzcz0ic3QyIiB3aWR0aD0iMjIuOSIgaGVpZ2h0PSIyMi43Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzQzMF8iIGNsYXNzPSJzdDMiIGQ9Ik0yOC45LDIyLjljMCwwLTAuMS0wLjItMC4xLTAuM2MtMC4zLTAuNi0wLjctMS41LTEuMS0yLjFjLTEtMS41LTIuMi0yLjgtMy43LTMuOA0KCQljLTEuMi0wLjktMi42LTEuNi00LjEtMmMtMC4yLDAtMC4zLTAuMS0wLjMtMC4xYzAsMCwwLTAuMSwwLjEtMC4zYzAuMS0wLjMsMC0wLjMsMC41LTAuM2MxLjEtMC4xLDIuNCwwLjEsMy41LDAuNQ0KCQljMi42LDAuOSw0LjYsMyw1LjQsNS42YzAuMiwwLjcsMC4zLDEuMiwwLjMsMi4xYzAsMC41LDAsMC42LDAsMC42YzAsMC0wLjEsMC4xLTAuMywwLjFMMjguOSwyMi45TDI4LjksMjIuOXogTTI2LjUsMjcuN2wtMC40LDANCgkJbDAtMC4yYzAtMC40LTAuMS0wLjktMC4xLTEuMmMtMC4zLTEuOC0xLjItMy42LTIuNC01Yy0xLjYtMS44LTMuOS0zLTYuMy0zLjRjLTAuNi0wLjEtMS0wLjEtMS43LTAuMWwtMC41LDBsMC0wLjcNCgkJYzAtMC40LDAtMC43LDAtMC43YzAsMCwwLjQtMC40LDAuNy0wLjZjMC4yLTAuMiwwLjYtMC40LDAuOC0wLjVsMC4yLTAuMWwwLjIsMGMzLjYsMC4zLDYuOCwyLDkuMSw0LjdjMS40LDEuNiwyLjMsMy42LDIuNyw1LjcNCgkJYzAsMC4yLDAuMSwwLjQsMC4xLDAuNGMwLDAuMS0wLjYsMS4xLTEsMS42bC0wLjEsMC4ybC0wLjMsMEMyNywyNy43LDI2LjcsMjcuNywyNi41LDI3Ljd6IE0xNS4yLDI5LjRjLTAuNi0wLjEtMS4xLTAuNS0xLjItMQ0KCQljLTAuMS0wLjItMC4xLTAuNSwwLTAuN2MwLjEtMC40LDAuNC0wLjgsMC43LTFjMC4zLTAuMSwwLjQtMC4yLDAuNy0wLjJjMC4yLDAsMC4zLDAsMC40LDAuMWMwLjUsMC4xLDAuOCwwLjUsMC45LDAuOQ0KCQljMC4xLDAuNCwwLjEsMC43LTAuMSwxLjFjLTAuMiwwLjQtMC41LDAuNy0wLjksMC44QzE1LjcsMjkuNCwxNS40LDI5LjQsMTUuMiwyOS40eiBNMjAsMzAuN2MtMC40LDAtMC44LTAuMS0xLjEtMC4ybC0wLjItMC4xDQoJCWwtMC40LTAuM2MtMC4yLTAuMi0wLjQtMC4zLTAuNC0wLjNjMCwwLDAtMC4xLDAuMS0wLjJjMC4zLTAuNCwwLjQtMC44LDAuNS0xLjNjMC0wLjMsMC0wLjcsMC0xYy0wLjItMS0xLTEuOS0yLTIuMg0KCQljLTAuMy0wLjEtMC41LTAuMS0wLjktMC4xYy0wLjYsMC0wLjktMC4xLTEuNC0wLjNjLTAuNi0wLjMtMS4xLTAuNy0xLjUtMS4zYy0wLjItMC40LTAuNC0wLjktMC40LTEuM2MwLTAuNCwwLjEtMS4xLDAuMi0xLjQNCgkJYzAuNC0xLDEuMy0xLjcsMi40LTJjMC4zLTAuMSwxLTAuMSwxLjUtMC4xYzIuMSwwLjIsNC4xLDEsNS42LDIuM2MwLjgsMC43LDEuNSwxLjUsMiwyLjRjMC41LDEsMC45LDIsMS4xLDMuMg0KCQljMC4xLDAuNCwwLjEsMS40LDAuMSwxLjhjMCwwLjUtMC4xLDEuMy0wLjIsMS40Yy0wLjEsMC4xLTEsMC40LTEuNCwwLjZsLTAuMiwwLjFsLTAuNi0wLjJjLTAuMy0wLjEtMC42LTAuMi0wLjYtMC4yDQoJCWMwLDAsMC0wLjEsMC0wLjJjMC4xLTAuNCwwLjItMC45LDAuMy0xLjRjMC0wLjMsMC0xLjItMC4xLTEuNWMtMC4yLTEtMC42LTItMS4yLTIuOGMtMS4yLTEuNi0zLTIuNi01LTIuN2MtMC43LTAuMS0wLjksMC0xLDAuMw0KCQljMCwwLjEtMC4xLDAuMSwwLDAuM2MwLDAuMiwwLDAuMywwLjIsMC40YzAuMSwwLjEsMC4yLDAuMSwwLjYsMC4xYzEuOCwwLjEsMy40LDAuOSw0LjQsMi4zYzEuMiwxLjYsMS40LDMuNywwLjUsNS41DQoJCWMtMC4yLDAuNC0wLjIsMC41LTAuMywwLjVDMjAuMywzMC43LDIwLjEsMzAuNywyMCwzMC43eiBNMjAuNSwzMi4zYy0yLjMtMC4xLTQuNC0wLjgtNi4yLTIuMmMtMC40LTAuMy0xLTAuOS0xLjMtMS4yDQoJCWMtMC4zLTAuMy0wLjctMC45LTAuOS0xLjJjLTAuOC0xLjItMS4zLTIuNi0xLjUtNGMtMC4xLTAuNS0wLjEtMS4zLTAuMS0xLjhjMC4xLTIuMiwxLTQuMywyLjUtNmMwLjEtMC4xLDAuMi0wLjIsMC4yLTAuMg0KCQljMCwwLDAuNSwwLjQsMC41LDAuNWMwLDAtMC4xLDAuMS0wLjIsMC4yYy0xLjEsMS4xLTEuOCwyLjctMi4xLDQuM2MtMC4xLDAuNy0wLjIsMS4zLTAuMiwyYzAsMC42LDAuMSwxLDAuMiwxLjUNCgkJYzAuNCwxLjgsMS40LDMuNSwyLjcsNC43YzEuNiwxLjUsMy42LDIuNCw1LjksMi41YzAuNCwwLDEuMywwLDEuNywwYzIuMi0wLjIsNC4yLTEuMSw1LjctMi42bDAuMi0wLjJsMC4zLDAuMmwwLjMsMC4ybC0wLjEsMC4xDQoJCWMtMC4xLDAuMS0wLjIsMC4yLTAuNCwwLjNjLTEuNiwxLjUtMy42LDIuNC01LjgsMi42QzIxLjcsMzIuMywyMC45LDMyLjMsMjAuNSwzMi4zeiIvPg0KPC9nPg0KPC9zdmc+DQo=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsOiMyRDJEMkQ7fQ0KCS5zdDF7ZmlsbDojRkNCRDE0O30NCgkuc3Qye2ZpbGw6Izk5OTk5OTt9DQoJLnN0M3tmaWxsOiNFMkUyRTI7fQ0KPC9zdHlsZT4NCjxnIGlkPSJYTUxJRF8yXyI+DQoJPHJlY3QgaWQ9IlhNTElEXzEyNjI2XyIgeD0iNi44IiB5PSI0IiBjbGFzcz0ic3QwIiB3aWR0aD0iMjYuMyIgaGVpZ2h0PSIzMiIvPg0KCTxnIGlkPSJYTUxJRF8xMjU2OV8iPg0KCQk8cmVjdCBpZD0iWE1MSURfMTI2MjFfIiB4PSI2LjgiIHk9IjEyLjMiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjciIGhlaWdodD0iMS4zIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMjU4NF8iIHg9IjYuOCIgeT0iMTYuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTgwXyIgeD0iNi44IiB5PSIyMC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS43IiBoZWlnaHQ9IjEuMyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMTI1NzNfIiB4PSI2LjgiIHk9IjI0LjQiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjciIGhlaWdodD0iMS4zIi8+DQoJCTxyZWN0IGlkPSJYTUxJRF8xMjU3MV8iIHg9IjYuOCIgeT0iMjguNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTcwXyIgeD0iNi44IiB5PSIzMi40IiBjbGFzcz0ic3QxIiB3aWR0aD0iMS43IiBoZWlnaHQ9IjEuMyIvPg0KCTwvZz4NCgk8ZyBpZD0iWE1MSURfMTI1NTJfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTY3XyIgeD0iMzEuNSIgeT0iMTIuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTY1XyIgeD0iMzEuNSIgeT0iMTYuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTYzXyIgeD0iMzEuNSIgeT0iMjAuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTYyXyIgeD0iMzEuNSIgeT0iMjQuNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTU0XyIgeD0iMzEuNSIgeT0iMjguNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTUzXyIgeD0iMzEuNSIgeT0iMzIuNCIgY2xhc3M9InN0MSIgd2lkdGg9IjEuNyIgaGVpZ2h0PSIxLjMiLz4NCgk8L2c+DQoJPGcgaWQ9IlhNTElEXzE1MTZfIj4NCgkJPHJlY3QgaWQ9IlhNTElEXzEyNTQ4XyIgeD0iMjcuNCIgeT0iMzQuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIxLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzkzOTlfIiB4PSIyMy40IiB5PSIzNC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCQk8cmVjdCBpZD0iWE1MSURfMjk3Nl8iIHg9IjE5LjMiIHk9IjM0LjMiIGNsYXNzPSJzdDEiIHdpZHRoPSIxLjMiIGhlaWdodD0iMS43Ii8+DQoJCTxyZWN0IGlkPSJYTUxJRF8yOTc1XyIgeD0iMTUuMyIgeT0iMzQuMyIgY2xhc3M9InN0MSIgd2lkdGg9IjEuMyIgaGVpZ2h0PSIxLjciLz4NCgkJPHJlY3QgaWQ9IlhNTElEXzI5NzRfIiB4PSIxMS4zIiB5PSIzNC4zIiBjbGFzcz0ic3QxIiB3aWR0aD0iMS4zIiBoZWlnaHQ9IjEuNyIvPg0KCTwvZz4NCgk8cG9seWdvbiBpZD0iWE1MSURfNTgwXyIgcG9pbnRzPSIzMCw1LjkgMzAsNSAyMyw1IDIzLDEwIDIxLDEwIDIxLDUgMTYsNSAxNiwxMCAxNCwxMCAxNCw1IDksNSA5LDExIDEwLDExIDEwLDYgMTMsNiAxMywxMSAxNywxMSANCgkJMTcsNiAyMCw2IDIwLDExIDI0LDExIDI0LDYgMjcsNiAyNywxMSAyOCwxMSAyOCw2IDI5LDYgMjksMTEgMzAsMTEgMzAsNS45IAkiLz4NCgk8cmVjdCBpZD0iWE1MSURfNTI1XyIgeD0iOC41IiB5PSIxMS43IiBjbGFzcz0ic3QyIiB3aWR0aD0iMjIuOSIgaGVpZ2h0PSIyMi43Ii8+DQoJPHBhdGggaWQ9IlhNTElEXzQzMF8iIGNsYXNzPSJzdDMiIGQ9Ik0yOC45LDIyLjljMCwwLTAuMS0wLjItMC4xLTAuM2MtMC4zLTAuNi0wLjctMS41LTEuMS0yLjFjLTEtMS41LTIuMi0yLjgtMy43LTMuOA0KCQljLTEuMi0wLjktMi42LTEuNi00LjEtMmMtMC4yLDAtMC4zLTAuMS0wLjMtMC4xYzAsMCwwLTAuMSwwLjEtMC4zYzAuMS0wLjMsMC0wLjMsMC41LTAuM2MxLjEtMC4xLDIuNCwwLjEsMy41LDAuNQ0KCQljMi42LDAuOSw0LjYsMyw1LjQsNS42YzAuMiwwLjcsMC4zLDEuMiwwLjMsMi4xYzAsMC41LDAsMC42LDAsMC42YzAsMC0wLjEsMC4xLTAuMywwLjFMMjguOSwyMi45TDI4LjksMjIuOXogTTI2LjUsMjcuN2wtMC40LDANCgkJbDAtMC4yYzAtMC40LTAuMS0wLjktMC4xLTEuMmMtMC4zLTEuOC0xLjItMy42LTIuNC01Yy0xLjYtMS44LTMuOS0zLTYuMy0zLjRjLTAuNi0wLjEtMS0wLjEtMS43LTAuMWwtMC41LDBsMC0wLjcNCgkJYzAtMC40LDAtMC43LDAtMC43YzAsMCwwLjQtMC40LDAuNy0wLjZjMC4yLTAuMiwwLjYtMC40LDAuOC0wLjVsMC4yLTAuMWwwLjIsMGMzLjYsMC4zLDYuOCwyLDkuMSw0LjdjMS40LDEuNiwyLjMsMy42LDIuNyw1LjcNCgkJYzAsMC4yLDAuMSwwLjQsMC4xLDAuNGMwLDAuMS0wLjYsMS4xLTEsMS42bC0wLjEsMC4ybC0wLjMsMEMyNywyNy43LDI2LjcsMjcuNywyNi41LDI3Ljd6IE0xNS4yLDI5LjRjLTAuNi0wLjEtMS4xLTAuNS0xLjItMQ0KCQljLTAuMS0wLjItMC4xLTAuNSwwLTAuN2MwLjEtMC40LDAuNC0wLjgsMC43LTFjMC4zLTAuMSwwLjQtMC4yLDAuNy0wLjJjMC4yLDAsMC4zLDAsMC40LDAuMWMwLjUsMC4xLDAuOCwwLjUsMC45LDAuOQ0KCQljMC4xLDAuNCwwLjEsMC43LTAuMSwxLjFjLTAuMiwwLjQtMC41LDAuNy0wLjksMC44QzE1LjcsMjkuNCwxNS40LDI5LjQsMTUuMiwyOS40eiBNMjAsMzAuN2MtMC40LDAtMC44LTAuMS0xLjEtMC4ybC0wLjItMC4xDQoJCWwtMC40LTAuM2MtMC4yLTAuMi0wLjQtMC4zLTAuNC0wLjNjMCwwLDAtMC4xLDAuMS0wLjJjMC4zLTAuNCwwLjQtMC44LDAuNS0xLjNjMC0wLjMsMC0wLjcsMC0xYy0wLjItMS0xLTEuOS0yLTIuMg0KCQljLTAuMy0wLjEtMC41LTAuMS0wLjktMC4xYy0wLjYsMC0wLjktMC4xLTEuNC0wLjNjLTAuNi0wLjMtMS4xLTAuNy0xLjUtMS4zYy0wLjItMC40LTAuNC0wLjktMC40LTEuM2MwLTAuNCwwLjEtMS4xLDAuMi0xLjQNCgkJYzAuNC0xLDEuMy0xLjcsMi40LTJjMC4zLTAuMSwxLTAuMSwxLjUtMC4xYzIuMSwwLjIsNC4xLDEsNS42LDIuM2MwLjgsMC43LDEuNSwxLjUsMiwyLjRjMC41LDEsMC45LDIsMS4xLDMuMg0KCQljMC4xLDAuNCwwLjEsMS40LDAuMSwxLjhjMCwwLjUtMC4xLDEuMy0wLjIsMS40Yy0wLjEsMC4xLTEsMC40LTEuNCwwLjZsLTAuMiwwLjFsLTAuNi0wLjJjLTAuMy0wLjEtMC42LTAuMi0wLjYtMC4yDQoJCWMwLDAsMC0wLjEsMC0wLjJjMC4xLTAuNCwwLjItMC45LDAuMy0xLjRjMC0wLjMsMC0xLjItMC4xLTEuNWMtMC4yLTEtMC42LTItMS4yLTIuOGMtMS4yLTEuNi0zLTIuNi01LTIuN2MtMC43LTAuMS0wLjksMC0xLDAuMw0KCQljMCwwLjEtMC4xLDAuMSwwLDAuM2MwLDAuMiwwLDAuMywwLjIsMC40YzAuMSwwLjEsMC4yLDAuMSwwLjYsMC4xYzEuOCwwLjEsMy40LDAuOSw0LjQsMi4zYzEuMiwxLjYsMS40LDMuNywwLjUsNS41DQoJCWMtMC4yLDAuNC0wLjIsMC41LTAuMywwLjVDMjAuMywzMC43LDIwLjEsMzAuNywyMCwzMC43eiBNMjAuNSwzMi4zYy0yLjMtMC4xLTQuNC0wLjgtNi4yLTIuMmMtMC40LTAuMy0xLTAuOS0xLjMtMS4yDQoJCWMtMC4zLTAuMy0wLjctMC45LTAuOS0xLjJjLTAuOC0xLjItMS4zLTIuNi0xLjUtNGMtMC4xLTAuNS0wLjEtMS4zLTAuMS0xLjhjMC4xLTIuMiwxLTQuMywyLjUtNmMwLjEtMC4xLDAuMi0wLjIsMC4yLTAuMg0KCQljMCwwLDAuNSwwLjQsMC41LDAuNWMwLDAtMC4xLDAuMS0wLjIsMC4yYy0xLjEsMS4xLTEuOCwyLjctMi4xLDQuM2MtMC4xLDAuNy0wLjIsMS4zLTAuMiwyYzAsMC42LDAuMSwxLDAuMiwxLjUNCgkJYzAuNCwxLjgsMS40LDMuNSwyLjcsNC43YzEuNiwxLjUsMy42LDIuNCw1LjksMi41YzAuNCwwLDEuMywwLDEuNywwYzIuMi0wLjIsNC4yLTEuMSw1LjctMi42bDAuMi0wLjJsMC4zLDAuMmwwLjMsMC4ybC0wLjEsMC4xDQoJCWMtMC4xLDAuMS0wLjIsMC4yLTAuNCwwLjNjLTEuNiwxLjUtMy42LDIuNC01LjgsMi42QzIxLjcsMzIuMywyMC45LDMyLjMsMjAuNSwzMi4zeiIvPg0KPC9nPg0KPC9zdmc+DQo=';


class esp32 {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'esp32';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const eviveState = sourceTarget.getCustomState(esp32.STATE_KEY);
            if (eviveState) {
                newTarget.setCustomState(esp32.STATE_KEY, Clone.simple(eviveState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'esp32',
            name: formatMessage({
                id: 'esp32.ESP32',
                default: 'ESP32',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#5372e5',
            colourSecondary: '#4657ca',
            colourTertiary: '#3a4ca5',
            blocks: [
                {
                    opcode: 'esp32StartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'esp32.esp32StartUp',
                        default: 'when ESP32 starts up',
                        description: 'ESP32 Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'esp32.digitalRead',
                        default: 'read status of digital pin [PIN]',
                        description: 'Read digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
                        }
                    }
                },
                {
                    opcode: 'analogRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'esp32.analogRead',
                        default: 'read analog pin [PIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'analogPins',
                            defaultValue: '32'
                        }
                    }
                },
                {
                    opcode: 'digitalWrite',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'esp32.digitalWrite',
                        default: 'set digital pin [PIN] output as [MODE]',
                        description: 'Write digital pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
                            defaultValue: '2'
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
                        id: 'esp32.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Write PWM Value of pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'pwmPins',
                            defaultValue: '14'
                        },
                        VALUE: {
                            type: ArgumentType.MATHSLIDER255,
                            defaultValue: '255',
                        }
                    }
                },
                "---",
                {
                    opcode: 'touchRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'esp32.touchRead',
                        default: 'get value of touch pin [TOUCHPIN]',
                        description: 'Read analog pin'
                    }),
                    arguments: {
                        TOUCHPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'touchPinMenu',
                            defaultValue: '15'
                        }
                    }
                },
                {
                    opcode: 'hallRead',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'esp32.hallRead',
                        default: 'get hall sensor value',
                        description: 'read hall sensor value'
                    })
                },
                "---",
                {
                    opcode: 'getMacAddr',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'esp32.getMacAddr',
                        default: 'get bluetooth Mac Address',
                        description: 'Gives Bluetooth MAc Address'
                    })
                },
                {
                    opcode: 'map',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'esp32.map',
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
                    '32', '33', '34', '35', '36', '39'
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
                digitalPins: [
                    '27', '26', '25', '22', '21', '19', '18', '17', '16', '15', '14', '13', '12', '5', '4', '2'
                ],
                pwmPins: [
                    '5', '12', '13', '14', '15', '25', '26', '27'
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
                touchPinMenu: [
                    { text: 'T0', value: '4' },
                    { text: 'T1', value: '0' },
                    { text: 'T2', value: '2' },
                    { text: 'T3', value: '15' },
                    { text: 'T4', value: '13' },
                    { text: 'T5', value: '12' },
                    { text: 'T6', value: '14' },
                    { text: 'T7', value: '27' },
                    { text: 'T8', value: '33' },
                    { text: 'T9', value: '32' }
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

    esp32StartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_esp32StartUp');
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

    touchRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.touchRead(args, util, this);
        }
        return RealtimeMode.touchRead(args, util, this);
    }

    hallRead(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.hallRead(args, util, this);
        }
        return RealtimeMode.hallRead(args, util, this);
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

    getMacAddr(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.getMacAddr(args, util, this);
        }
        return RealtimeMode.getMacAddr(args, util, this);
    }

    map(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.map(args, util, this);
        }
        return RealtimeMode.map(args, util, this);
    }

    ///////////////
    /*fillscreen (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillscreen(args, util, this);
        }
        return RealtimeMode.fillscreen(args, util, this);
    }

    setCursor (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setCursor(args, util, this);
        }
        return RealtimeMode.setCursor(args, util, this);
    }

    setTextColorSize (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.setTextColorSize(args, util, this);
        }
        return RealtimeMode.setTextColorSize(args, util, this);
    }

    write (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.write(args, util, this);
        }
        return RealtimeMode.write(args, util, this);
    }

    drawLine (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.drawLine(args, util, this);
        }
        return RealtimeMode.drawLine(args, util, this);
    }

    fillDrawRect (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawRect(args, util, this);
        }
        return RealtimeMode.fillDrawRect(args, util, this);
    }

    fillDrawRoundRect (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawRoundRect(args, util, this);
        }
        return RealtimeMode.fillDrawRoundRect(args, util, this);
    }

    fillDrawCircle (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawCircle(args, util, this);
        }
        return RealtimeMode.fillDrawCircle(args, util, this);
    }

    fillDrawEllipse (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawEllipse(args, util, this);
        }
        return RealtimeMode.fillDrawEllipse(args, util, this);
    }

    fillDrawTriangle (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.fillDrawTriangle(args, util, this);
        }
        return RealtimeMode.fillDrawTriangle(args, util, this);
    }

    displayMatrix3 (args, util) {
        if (this.runtime.getCode) {
            return CodingMode.displayMatrix3(args, util, this);
        }
        return RealtimeMode.displayMatrix3(args, util, this);
    }*/
    ///////////////

}

module.exports = esp32;
