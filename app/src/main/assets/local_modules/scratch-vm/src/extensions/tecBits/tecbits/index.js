const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const Clone = require('../../../util/clone');
const formatMessage = require('format-message');
const CodingMode = require('./coding-mode.js');
const RealtimeMode = require('./realtime-mode.js');
const ExtensionMenu = require('../../../util/hardware-extension-menus.js');
const blockIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiMwMDZGOEY7fQ0KCS5zdDF7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojMDAwMDAyO30NCgkuc3Qye2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0NCQ0FDQTt9DQoJLnN0M3tmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiNGRkZGRkY7fQ0KCS5zdDR7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojOEM4NjY0O30NCgkuc3Q1e2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6IzJGMkYyRTt9DQoJLnN0NntmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM5Njk2OTc7fQ0KCS5zdDd7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojRDhCQjc0O30NCgkuc3Q4e2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0U4RTlFODt9DQoJLnN0OXtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiMwMDAwMDE7fQ0KPC9zdHlsZT4NCjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0wLDI5LjI4VjEwLjY3YzAtMC41NCwwLjQ1LTAuOTksMC45OS0wLjk5SDM5YzAuNTUsMCwwLjk5LDAuNDUsMC45OSwwLjk5djE4LjYxYzAsMC41NC0wLjQ1LDAuOTktMC45OSwwLjk5DQoJSDAuOTlDMC40NSwzMC4yNywwLDI5LjgyLDAsMjkuMjh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMzIuNzEsMjAuODh2MC40NWMwLjIsMCwxLjQ4LTAuMDIsMS41NSwwLjAxYzAuMDQsMC4wMiwwLjM0LDAuMzMsMC40LDAuMzljMC4wNywwLjA3LDAuMTMsMC4xMywwLjIsMC4xOQ0KCWMwLjA4LDAuMDgsMC4xMSwwLjEyLDAuMjcsMC4xMmMwLjIxLDAsMi41OSwwLjAyLDIuNzItMC4wMWMwLjIxLTAuMDQsMC4yNy0wLjM2LDAuMDItMC40NGMtMC4xMy0wLjA0LTIuNjMsMC4wMy0yLjcyLTAuMDINCglsLTAuNTktMC41OWMtMC4wOS0wLjA5LTAuMS0wLjExLTAuMjgtMC4xMUMzMy43NSwyMC44OCwzMy4yMywyMC44OCwzMi43MSwyMC44OHoiLz4NCjxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik0zMi42NSwxOC40NHYwLjQ1YzAuNTIsMCwxLjAzLDAsMS41NSwwYzAuMTksMCwwLjIsMC4wMSwwLjMtMC4wOWwwLjU5LTAuNThjMC4wNi0wLjA0LDAuMjctMC4wMiwwLjM2LTAuMDINCgljMC4yOSwwLDIuMjcsMC4wMSwyLjM0LDBjMC4xNS0wLjAyLDAuMjQtMC4yMSwwLjEzLTAuMzZjLTAuMDktMC4xMy0wLjM1LTAuMDgtMC41LTAuMDhoLTIuMzRjLTAuMDMsMC0wLjA3LDAtMC4wOSwwDQoJYy0wLjAzLDAtMC4wNSwwLjAxLTAuMDcsMC4wMmMtMC4xMiwwLjA2LTAuNjIsMC42NS0wLjcsMC42N0MzNC4xNiwxOC40NSwzMi44MiwxOC40NCwzMi42NSwxOC40NHoiLz4NCjxyZWN0IHg9IjI4LjQzIiB5PSIxNy42NSIgY2xhc3M9InN0MiIgd2lkdGg9IjEuMTIiIGhlaWdodD0iMC44MyIvPg0KPHJlY3QgeD0iMzAuODYiIHk9IjE3LjY1IiBjbGFzcz0ic3QyIiB3aWR0aD0iMS4xMiIgaGVpZ2h0PSIwLjgzIi8+DQo8cmVjdCB4PSIyOC40NSIgeT0iMjEuNTEiIGNsYXNzPSJzdDIiIHdpZHRoPSIxLjEyIiBoZWlnaHQ9IjAuODMiLz4NCjxyZWN0IHg9IjMwLjg4IiB5PSIyMS41IiBjbGFzcz0ic3QyIiB3aWR0aD0iMS4xMiIgaGVpZ2h0PSIwLjgzIi8+DQo8cmVjdCB4PSIyOC44OCIgeT0iMTguMzMiIGNsYXNzPSJzdDMiIHdpZHRoPSIzLjg3IiBoZWlnaHQ9IjMuMzQiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yMS4wMywxOC45NWgwLjk2YzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djEuNTVjMCwwLjA0LTAuMDQsMC4wOC0wLjA4LDAuMDhoLTAuOTYNCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0xLjU1QzIwLjk2LDE4Ljk4LDIwLjk5LDE4Ljk1LDIxLjAzLDE4Ljk1eiIvPg0KPGNpcmNsZSBjbGFzcz0ic3Q0IiBjeD0iMjEuNTIiIGN5PSIxOS44IiByPSIwLjQiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDUiIHBvaW50cz0iMTMuMjYsMTkuMiAxMy4yNCwxOC42NSAxMy41MiwxOC4zNyAxNS4xNiwxOC4zNyAxNS40MSwxOC42MiAxNS40MSwxOS4xOCAxNS4xNiwxOS40MyAxNS40MSwxOS42OSANCgkxNS40MSwyMC4zMSAxNS4xNiwyMC41NiAxNS40MSwyMC43OSAxNS40MSwyMS4zNyAxNS4xOSwyMS41OCAxMy40NSwyMS41OCAxMy4yNSwyMS4zOCAxMy4yNSwyMC44MSAxMy41LDIwLjU3IDEzLjI1LDIwLjMyIA0KCTEzLjI1LDE5LjcxIDEzLjQ5LDE5LjQ0ICIvPg0KPHBhdGggY2xhc3M9InN0NiIgZD0iTTEzLjcsMjAuOTljMC4xNiwwLDAuMTQsMC4wMSwwLjE4LTAuMDRjLTAuMDUtMC4wMy0wLjE3LTAuMDEtMC4yNS0wLjAxQzEzLjY1LDIwLjk4LDEzLjY1LDIwLjk5LDEzLjcsMjAuOTl6Ig0KCS8+DQo8cGF0aCBjbGFzcz0ic3QzIiBkPSJNOC44OCwxOS44NkM4Ljg4LDE5Ljg2LDguODgsMTkuODYsOC44OCwxOS44NkM4Ljg4LDE5Ljg2LDguODgsMTkuODYsOC44OCwxOS44NnoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NiwxOC42OWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjAzLTAuMDgtMC4wOHYtMC4xOUMxMy41OSwxOC43MiwxMy42MiwxOC42OSwxMy42NiwxOC42OXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC43NywxOC42OWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjAzLTAuMDgtMC4wOHYtMC4xOUMxNC42OSwxOC43MiwxNC43MywxOC42OSwxNC43NywxOC42OXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NywxOS44aDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzEzLjU5LDE5LjgzLDEzLjYzLDE5LjgsMTMuNjcsMTkuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC43OCwxOS44aDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzE0LjcxLDE5Ljg0LDE0Ljc0LDE5LjgsMTQuNzgsMTkuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NywyMC45MWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4xOUMxMy41OSwyMC45NSwxMy42MywyMC45MSwxMy42NywyMC45MXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC44LDIwLjkxaDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzE0LjcyLDIwLjk1LDE0Ljc1LDIwLjkxLDE0LjgsMjAuOTF6Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDUiIGN4PSIyMS41MiIgY3k9IjE5LjgiIHI9IjAuMzYiLz4NCjxwYXRoIGNsYXNzPSJzdDciIGQ9Ik0xOC40OSwxNy42MWgxLjAyYzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djAuNDRjMCwwLjA0LTAuMDMsMC4wOC0wLjA4LDAuMDhoLTEuMDINCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0wLjQ0QzE4LjQxLDE3LjY0LDE4LjQ1LDE3LjYxLDE4LjQ5LDE3LjYxeiIvPg0KPHBhdGggY2xhc3M9InN0MiIgZD0iTTE4LjYyLDE3LjY0aDAuNzVjMC4wNSwwLDAuMDgsMC4wNCwwLjA4LDAuMDh2MC4zN2MwLDAuMDUtMC4wNCwwLjA4LTAuMDgsMC4wOGgtMC43NQ0KCWMtMC4wNSwwLTAuMDgtMC4wNC0wLjA4LTAuMDh2LTAuMzdDMTguNTQsMTcuNjgsMTguNTcsMTcuNjQsMTguNjIsMTcuNjR6Ii8+DQo8cGF0aCBjbGFzcz0ic3Q3IiBkPSJNMTguNDcsMTkuMWgxLjAyYzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djAuNDRjMCwwLjA0LTAuMDMsMC4wOC0wLjA4LDAuMDhoLTEuMDINCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0wLjQ0QzE4LjM5LDE5LjE0LDE4LjQzLDE5LjEsMTguNDcsMTkuMXoiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0xOC42LDE5LjE0aDAuNzVjMC4wNSwwLDAuMDgsMC4wNCwwLjA4LDAuMDh2MC4zNmMwLDAuMDUtMC4wNCwwLjA4LTAuMDgsMC4wOEgxOC42DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4zNkMxOC41MiwxOS4xNywxOC41NSwxOS4xNCwxOC42LDE5LjE0eiIvPg0KPHBhdGggY2xhc3M9InN0NyIgZD0iTTE4LjQ3LDIwLjU4aDEuMDJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC40NGMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMS4wMg0KCWMtMC4wNCwwLTAuMDgtMC4wNC0wLjA4LTAuMDh2LTAuNDRDMTguMzksMjAuNjIsMTguNDIsMjAuNTgsMTguNDcsMjAuNTh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTguNTksMjAuNjFoMC43NWMwLjA1LDAsMC4wOCwwLjA0LDAuMDgsMC4wOHYwLjM3YzAsMC4wNS0wLjA0LDAuMDgtMC4wOCwwLjA4aC0wLjc1DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOFYyMC43QzE4LjUxLDIwLjY1LDE4LjU1LDIwLjYxLDE4LjU5LDIwLjYxeiIvPg0KPHBhdGggY2xhc3M9InN0NyIgZD0iTTE4LjQ2LDIxLjc4aDEuMDJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC40NGMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMS4wMg0KCWMtMC4wNCwwLTAuMDgtMC4wMy0wLjA4LTAuMDh2LTAuNDRDMTguMzksMjEuODEsMTguNDIsMjEuNzgsMTguNDYsMjEuNzh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTguNTksMjEuODFoMC43NWMwLjA1LDAsMC4wOCwwLjA0LDAuMDgsMC4wOHYwLjM3YzAsMC4wNC0wLjA0LDAuMDgtMC4wOCwwLjA4aC0wLjc1DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4zN0MxOC41MSwyMS44NSwxOC41NCwyMS44MSwxOC41OSwyMS44MXoiLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjE2Ljk3IiBjeT0iMTIuNzMiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjExLjgiIGN5PSIxMi43NCIgcj0iMS42NyIvPg0KPGNpcmNsZSBjbGFzcz0ic3Q4IiBjeD0iNi42NyIgY3k9IjEyLjc5IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIyMi4wOCIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIyNy4xNyIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzMi4xOSIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzNyIgY3k9IjE1LjEiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjM3IiBjeT0iMjQuNjUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjMyLjE5IiBjeT0iMjcuMzUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjI3LjExIiBjeT0iMjcuMzUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjIyLjA0IiBjeT0iMjcuMzIiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjE2Ljg5IiBjeT0iMjcuMzYiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjExLjc3IiBjeT0iMjcuMzYiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjYuNjciIGN5PSIyNy4zIiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzLjUzIiBjeT0iMjIuNDkiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjMuNTMiIGN5PSIxNy4wMiIgcj0iMS42NyIvPg0KPHJlY3QgeD0iMjMuNzciIHk9IjE4LjMiIHRyYW5zZm9ybT0ibWF0cml4KDAuNzA3IC0wLjcwNzIgMC43MDcyIDAuNzA3IC02LjU5ODYgMjMuNjY4NykiIGNsYXNzPSJzdDkiIHdpZHRoPSIzIiBoZWlnaHQ9IjMiLz4NCjwvc3ZnPg0K';// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4xLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA0MCA0MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNDAgNDA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+DQoJLnN0MHtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiMwMDZGOEY7fQ0KCS5zdDF7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojMDAwMDAyO30NCgkuc3Qye2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0NCQ0FDQTt9DQoJLnN0M3tmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiNGRkZGRkY7fQ0KCS5zdDR7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojOEM4NjY0O30NCgkuc3Q1e2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6IzJGMkYyRTt9DQoJLnN0NntmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiM5Njk2OTc7fQ0KCS5zdDd7ZmlsbC1ydWxlOmV2ZW5vZGQ7Y2xpcC1ydWxlOmV2ZW5vZGQ7ZmlsbDojRDhCQjc0O30NCgkuc3Q4e2ZpbGwtcnVsZTpldmVub2RkO2NsaXAtcnVsZTpldmVub2RkO2ZpbGw6I0U4RTlFODt9DQoJLnN0OXtmaWxsLXJ1bGU6ZXZlbm9kZDtjbGlwLXJ1bGU6ZXZlbm9kZDtmaWxsOiMwMDAwMDE7fQ0KPC9zdHlsZT4NCjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0wLDI5LjI4VjEwLjY3YzAtMC41NCwwLjQ1LTAuOTksMC45OS0wLjk5SDM5YzAuNTUsMCwwLjk5LDAuNDUsMC45OSwwLjk5djE4LjYxYzAsMC41NC0wLjQ1LDAuOTktMC45OSwwLjk5DQoJSDAuOTlDMC40NSwzMC4yNywwLDI5LjgyLDAsMjkuMjh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QxIiBkPSJNMzIuNzEsMjAuODh2MC40NWMwLjIsMCwxLjQ4LTAuMDIsMS41NSwwLjAxYzAuMDQsMC4wMiwwLjM0LDAuMzMsMC40LDAuMzljMC4wNywwLjA3LDAuMTMsMC4xMywwLjIsMC4xOQ0KCWMwLjA4LDAuMDgsMC4xMSwwLjEyLDAuMjcsMC4xMmMwLjIxLDAsMi41OSwwLjAyLDIuNzItMC4wMWMwLjIxLTAuMDQsMC4yNy0wLjM2LDAuMDItMC40NGMtMC4xMy0wLjA0LTIuNjMsMC4wMy0yLjcyLTAuMDINCglsLTAuNTktMC41OWMtMC4wOS0wLjA5LTAuMS0wLjExLTAuMjgtMC4xMUMzMy43NSwyMC44OCwzMy4yMywyMC44OCwzMi43MSwyMC44OHoiLz4NCjxwYXRoIGNsYXNzPSJzdDEiIGQ9Ik0zMi42NSwxOC40NHYwLjQ1YzAuNTIsMCwxLjAzLDAsMS41NSwwYzAuMTksMCwwLjIsMC4wMSwwLjMtMC4wOWwwLjU5LTAuNThjMC4wNi0wLjA0LDAuMjctMC4wMiwwLjM2LTAuMDINCgljMC4yOSwwLDIuMjcsMC4wMSwyLjM0LDBjMC4xNS0wLjAyLDAuMjQtMC4yMSwwLjEzLTAuMzZjLTAuMDktMC4xMy0wLjM1LTAuMDgtMC41LTAuMDhoLTIuMzRjLTAuMDMsMC0wLjA3LDAtMC4wOSwwDQoJYy0wLjAzLDAtMC4wNSwwLjAxLTAuMDcsMC4wMmMtMC4xMiwwLjA2LTAuNjIsMC42NS0wLjcsMC42N0MzNC4xNiwxOC40NSwzMi44MiwxOC40NCwzMi42NSwxOC40NHoiLz4NCjxyZWN0IHg9IjI4LjQzIiB5PSIxNy42NSIgY2xhc3M9InN0MiIgd2lkdGg9IjEuMTIiIGhlaWdodD0iMC44MyIvPg0KPHJlY3QgeD0iMzAuODYiIHk9IjE3LjY1IiBjbGFzcz0ic3QyIiB3aWR0aD0iMS4xMiIgaGVpZ2h0PSIwLjgzIi8+DQo8cmVjdCB4PSIyOC40NSIgeT0iMjEuNTEiIGNsYXNzPSJzdDIiIHdpZHRoPSIxLjEyIiBoZWlnaHQ9IjAuODMiLz4NCjxyZWN0IHg9IjMwLjg4IiB5PSIyMS41IiBjbGFzcz0ic3QyIiB3aWR0aD0iMS4xMiIgaGVpZ2h0PSIwLjgzIi8+DQo8cmVjdCB4PSIyOC44OCIgeT0iMTguMzMiIGNsYXNzPSJzdDMiIHdpZHRoPSIzLjg3IiBoZWlnaHQ9IjMuMzQiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0yMS4wMywxOC45NWgwLjk2YzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djEuNTVjMCwwLjA0LTAuMDQsMC4wOC0wLjA4LDAuMDhoLTAuOTYNCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0xLjU1QzIwLjk2LDE4Ljk4LDIwLjk5LDE4Ljk1LDIxLjAzLDE4Ljk1eiIvPg0KPGNpcmNsZSBjbGFzcz0ic3Q0IiBjeD0iMjEuNTIiIGN5PSIxOS44IiByPSIwLjQiLz4NCjxwb2x5Z29uIGNsYXNzPSJzdDUiIHBvaW50cz0iMTMuMjYsMTkuMiAxMy4yNCwxOC42NSAxMy41MiwxOC4zNyAxNS4xNiwxOC4zNyAxNS40MSwxOC42MiAxNS40MSwxOS4xOCAxNS4xNiwxOS40MyAxNS40MSwxOS42OSANCgkxNS40MSwyMC4zMSAxNS4xNiwyMC41NiAxNS40MSwyMC43OSAxNS40MSwyMS4zNyAxNS4xOSwyMS41OCAxMy40NSwyMS41OCAxMy4yNSwyMS4zOCAxMy4yNSwyMC44MSAxMy41LDIwLjU3IDEzLjI1LDIwLjMyIA0KCTEzLjI1LDE5LjcxIDEzLjQ5LDE5LjQ0ICIvPg0KPHBhdGggY2xhc3M9InN0NiIgZD0iTTEzLjcsMjAuOTljMC4xNiwwLDAuMTQsMC4wMSwwLjE4LTAuMDRjLTAuMDUtMC4wMy0wLjE3LTAuMDEtMC4yNS0wLjAxQzEzLjY1LDIwLjk4LDEzLjY1LDIwLjk5LDEzLjcsMjAuOTl6Ig0KCS8+DQo8cGF0aCBjbGFzcz0ic3QzIiBkPSJNOC44OCwxOS44NkM4Ljg4LDE5Ljg2LDguODgsMTkuODYsOC44OCwxOS44NkM4Ljg4LDE5Ljg2LDguODgsMTkuODYsOC44OCwxOS44NnoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NiwxOC42OWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjAzLTAuMDgtMC4wOHYtMC4xOUMxMy41OSwxOC43MiwxMy42MiwxOC42OSwxMy42NiwxOC42OXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC43NywxOC42OWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjAzLTAuMDgtMC4wOHYtMC4xOUMxNC42OSwxOC43MiwxNC43MywxOC42OSwxNC43NywxOC42OXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NywxOS44aDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzEzLjU5LDE5LjgzLDEzLjYzLDE5LjgsMTMuNjcsMTkuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC43OCwxOS44aDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzE0LjcxLDE5Ljg0LDE0Ljc0LDE5LjgsMTQuNzgsMTkuOHoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xMy42NywyMC45MWgwLjJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC4xOWMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMC4yDQoJYy0wLjA0LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4xOUMxMy41OSwyMC45NSwxMy42MywyMC45MSwxMy42NywyMC45MXoiLz4NCjxwYXRoIGNsYXNzPSJzdDQiIGQ9Ik0xNC44LDIwLjkxaDAuMmMwLjA0LDAsMC4wOCwwLjAzLDAuMDgsMC4wOHYwLjE5YzAsMC4wNC0wLjAzLDAuMDgtMC4wOCwwLjA4aC0wLjJjLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4DQoJdi0wLjE5QzE0LjcyLDIwLjk1LDE0Ljc1LDIwLjkxLDE0LjgsMjAuOTF6Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDUiIGN4PSIyMS41MiIgY3k9IjE5LjgiIHI9IjAuMzYiLz4NCjxwYXRoIGNsYXNzPSJzdDciIGQ9Ik0xOC40OSwxNy42MWgxLjAyYzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djAuNDRjMCwwLjA0LTAuMDMsMC4wOC0wLjA4LDAuMDhoLTEuMDINCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0wLjQ0QzE4LjQxLDE3LjY0LDE4LjQ1LDE3LjYxLDE4LjQ5LDE3LjYxeiIvPg0KPHBhdGggY2xhc3M9InN0MiIgZD0iTTE4LjYyLDE3LjY0aDAuNzVjMC4wNSwwLDAuMDgsMC4wNCwwLjA4LDAuMDh2MC4zN2MwLDAuMDUtMC4wNCwwLjA4LTAuMDgsMC4wOGgtMC43NQ0KCWMtMC4wNSwwLTAuMDgtMC4wNC0wLjA4LTAuMDh2LTAuMzdDMTguNTQsMTcuNjgsMTguNTcsMTcuNjQsMTguNjIsMTcuNjR6Ii8+DQo8cGF0aCBjbGFzcz0ic3Q3IiBkPSJNMTguNDcsMTkuMWgxLjAyYzAuMDQsMCwwLjA4LDAuMDMsMC4wOCwwLjA4djAuNDRjMCwwLjA0LTAuMDMsMC4wOC0wLjA4LDAuMDhoLTEuMDINCgljLTAuMDQsMC0wLjA4LTAuMDMtMC4wOC0wLjA4di0wLjQ0QzE4LjM5LDE5LjE0LDE4LjQzLDE5LjEsMTguNDcsMTkuMXoiLz4NCjxwYXRoIGNsYXNzPSJzdDIiIGQ9Ik0xOC42LDE5LjE0aDAuNzVjMC4wNSwwLDAuMDgsMC4wNCwwLjA4LDAuMDh2MC4zNmMwLDAuMDUtMC4wNCwwLjA4LTAuMDgsMC4wOEgxOC42DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4zNkMxOC41MiwxOS4xNywxOC41NSwxOS4xNCwxOC42LDE5LjE0eiIvPg0KPHBhdGggY2xhc3M9InN0NyIgZD0iTTE4LjQ3LDIwLjU4aDEuMDJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC40NGMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMS4wMg0KCWMtMC4wNCwwLTAuMDgtMC4wNC0wLjA4LTAuMDh2LTAuNDRDMTguMzksMjAuNjIsMTguNDIsMjAuNTgsMTguNDcsMjAuNTh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTguNTksMjAuNjFoMC43NWMwLjA1LDAsMC4wOCwwLjA0LDAuMDgsMC4wOHYwLjM3YzAsMC4wNS0wLjA0LDAuMDgtMC4wOCwwLjA4aC0wLjc1DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOFYyMC43QzE4LjUxLDIwLjY1LDE4LjU1LDIwLjYxLDE4LjU5LDIwLjYxeiIvPg0KPHBhdGggY2xhc3M9InN0NyIgZD0iTTE4LjQ2LDIxLjc4aDEuMDJjMC4wNCwwLDAuMDgsMC4wMywwLjA4LDAuMDh2MC40NGMwLDAuMDQtMC4wMywwLjA4LTAuMDgsMC4wOGgtMS4wMg0KCWMtMC4wNCwwLTAuMDgtMC4wMy0wLjA4LTAuMDh2LTAuNDRDMTguMzksMjEuODEsMTguNDIsMjEuNzgsMTguNDYsMjEuNzh6Ii8+DQo8cGF0aCBjbGFzcz0ic3QyIiBkPSJNMTguNTksMjEuODFoMC43NWMwLjA1LDAsMC4wOCwwLjA0LDAuMDgsMC4wOHYwLjM3YzAsMC4wNC0wLjA0LDAuMDgtMC4wOCwwLjA4aC0wLjc1DQoJYy0wLjA1LDAtMC4wOC0wLjA0LTAuMDgtMC4wOHYtMC4zN0MxOC41MSwyMS44NSwxOC41NCwyMS44MSwxOC41OSwyMS44MXoiLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjE2Ljk3IiBjeT0iMTIuNzMiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjExLjgiIGN5PSIxMi43NCIgcj0iMS42NyIvPg0KPGNpcmNsZSBjbGFzcz0ic3Q4IiBjeD0iNi42NyIgY3k9IjEyLjc5IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIyMi4wOCIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIyNy4xNyIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzMi4xOSIgY3k9IjEyLjc0IiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzNyIgY3k9IjE1LjEiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjM3IiBjeT0iMjQuNjUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjMyLjE5IiBjeT0iMjcuMzUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjI3LjExIiBjeT0iMjcuMzUiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjIyLjA0IiBjeT0iMjcuMzIiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjE2Ljg5IiBjeT0iMjcuMzYiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjExLjc3IiBjeT0iMjcuMzYiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjYuNjciIGN5PSIyNy4zIiByPSIxLjY3Ii8+DQo8Y2lyY2xlIGNsYXNzPSJzdDgiIGN4PSIzLjUzIiBjeT0iMjIuNDkiIHI9IjEuNjciLz4NCjxjaXJjbGUgY2xhc3M9InN0OCIgY3g9IjMuNTMiIGN5PSIxNy4wMiIgcj0iMS42NyIvPg0KPHJlY3QgeD0iMjMuNzciIHk9IjE4LjMiIHRyYW5zZm9ybT0ibWF0cml4KDAuNzA3IC0wLjcwNzIgMC43MDcyIDAuNzA3IC02LjU5ODYgMjMuNjY4NykiIGNsYXNzPSJzdDkiIHdpZHRoPSIzIiBoZWlnaHQ9IjMiLz4NCjwvc3ZnPg0K';

class tecBits {
    constructor(runtime) {
        this.runtime = runtime;
        this._onTargetCreated = this._onTargetCreated.bind(this);
        this.runtime.on('targetWasCreated', this._onTargetCreated);
        this.runtime.on('READ_FROM_PERIPHERAL', this._serialRead.bind(this));
        this._isSerialRead = () => { };
    }

    static get STATE_KEY() {
        return 'tecBits';
    }

    _onTargetCreated(newTarget, sourceTarget) {
        if (sourceTarget) {
            const eviveState = sourceTarget.getCustomState(tecBits.STATE_KEY);
            if (eviveState) {
                newTarget.setCustomState(tecBits.STATE_KEY, Clone.simple(eviveState));
            }
        }
    }

    _serialRead(data) {
        this._isSerialRead(data);
    }

    getInfo() {
        return {
            id: 'tecBits',
            name: formatMessage({
                id: 'tecBits.tecBits',
                default: 'TECbits',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#2a7cbd',
            colourSecondary: '#246ea7',
            colourTertiary: '#276293',
            blocks: [
                {
                    opcode: 'tecBitsStartUp',
                    blockType: BlockType.EVENT,
                    text: formatMessage({
                        id: 'tecBits.tecBitsStartUp',
                        default: 'when TECbits starts up',
                        description: 'TECbits Start up block'
                    }),
                    isEdgeActivated: false,
                    shouldRestartExistingThreads: true
                },
                '---',
                {
                    opcode: 'digitalRead',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'tecBits.digitalRead',
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
                        id: 'tecBits.analogRead',
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
                        id: 'tecBits.digitalWrite',
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
                {
                    opcode: 'setPWM',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tecBits.setPWM',
                        default: 'set PWM pin [PIN] output as [VALUE]',
                        description: 'Set pwm pin'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
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
                        id: 'tecBits.playTone',
                        default: 'play tone on [PIN] of note [NOTE] & beat [BEATS]',
                        description: 'Play a tone'
                    }),
                    arguments: {
                        PIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'digitalPins',
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
                        id: 'tecBits.readTimer',
                        default: 'get timer value',
                        description: 'Reports timer value'
                    })
                },
                {
                    opcode: 'resetTimer',
                    blockType: BlockType.COMMAND,
                    text: formatMessage({
                        id: 'tecBits.resetTimer',
                        default: 'reset timer',
                        description: 'Resets timer'
                    })
                },
                '---',
                {
                    opcode: 'cast',
                    blockType: BlockType.REPORTER,
                    text: formatMessage({
                        id: 'tecBits.cast',
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
                        id: 'tecBits.map',
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
                    opcode: 'isSensorTouched',
                    blockType: BlockType.BOOLEAN,
                    text: formatMessage({
                        id: 'tecBits.isSensorTouched',
                        default: 'set sensor threshold to [SENSORTHRESHOLD] and check is sensor [SENSORPIN][STATUS]?',
                        description: 'check sensor pin'
                    }),
                    arguments: {
                        SENSORPIN: {
                            type: ArgumentType.NUMBER,
                            menu: 'sensorPins',
                            defaultValue: '2'
                        },
                        STATUS: {
                            type: ArgumentType.MATHSLIDER255,
                            menu: 'sensorState',
                            defaultValue: '1'
                        },
                        SENSORTHRESHOLD: {
                            type: ArgumentType.NUMBER,
                            defaultValue: '100'
                        }

                    }
                }
            ],
            menus: {
                digitalPins: [
                    { text: '1', value: '3' },
                    { text: '2', value: '5' },
                    { text: '3', value: '6' },
                    { text: '4', value: '9' },
                    { text: '5', value: '10' },
                    { text: '6', value: '11' }
                ],
                analogPins: [
                    { text: '7', value: '0' },
                    { text: '8', value: '1' }
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
                sensorPins: [
                    { text: 'I0', value: '1' },
                    { text: 'I1', value: '2' },
                    { text: 'I2', value: '3' },
                    { text: 'I3', value: '4' },
                    { text: 'I4', value: '5' },
                    { text: 'I5', value: '6' }
                ],
                sensorState: [
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensorState.option1',
                            default: 'ON',
                            description: 'State of sensor'
                        }), value: '1'
                    },
                    {
                        text: formatMessage({
                            id: 'extension.menu.sensorState.option2',
                            default: 'OFF',
                            description: 'State of sensor'
                        }), value: '0'
                    }
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

    tecBitsStartUp() {
        if (this.runtime.getCode) {
            console.log('Hardware_tecBitsStartUp');
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

    isSensorTouched(args, util) {
        if (this.runtime.getCode) {
            return CodingMode.isSensorTouched(args, util, this);
        }
        return RealtimeMode.isSensorTouched(args, util, this);
    }
}

module.exports = tecBits;
