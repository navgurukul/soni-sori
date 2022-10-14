const Runtime = require('../../../engine/runtime');

const formatMessage = require('format-message');

const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');

// eslint-disable-next-line max-len
const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfNSIgZGF0YS1uYW1lPSJMYXllciA1IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNkMmNlNDY7fS5jbHMtMntmaWxsOiM0MDk0NzI7fS5jbHMtM3tmaWxsOiMzNDYyOWI7fS5jbHMtNHtmaWxsOiMzMzYzOWM7fS5jbHMtNSwuY2xzLTl7ZmlsbDpub25lO30uY2xzLTZ7ZmlsbDojZmZmO30uY2xzLTd7ZmlsbDojZDFjZTQ1O30uY2xzLTh7ZmlsbDojM2Q5NTczO30uY2xzLTl7c3Ryb2tlOiNmZmY7c3Ryb2tlLWxpbmVjYXA6cm91bmQ7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjEuMnB4O308L3N0eWxlPjwvZGVmcz48dGl0bGU+VGVhY2hhYmxlIE1hY2hpbmUgX2ljb24tMDItMDMtMDM8L3RpdGxlPjxwb2x5Z29uIGNsYXNzPSJjbHMtMSIgcG9pbnRzPSI1LjM3IDExLjI5IDE5LjkzIDIwIDM0LjEzIDExLjI5IDE5Ljc1IDIuOTUgNS4zNyAxMS4yOSIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSI0LjkzIDI3Ljk4IDE5LjQyIDM2LjYyIDE5LjkzIDIwIDUuMzcgMTEuMjkgNC45MyAyNy45OCIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtMyIgcG9pbnRzPSIxOS45MyAyMCAxOS40MiAzNi42MiAzNC4xMyAyOC4zMSAzNC4xMyAxMS4yOSAxOS45MyAyMCIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjM4LDIwLjU5QTIuNDYsMi40NiwwLDAsMSwyOC41OSwyMiwwLjIsMC4yLDAsMCwwLDI5LDIyYTIuNzUsMi43NSwwLDAsMC0uMzMtMS43MmMtMC4xNi0uMi0wLjQ0LjA4LTAuMjgsMC4yOGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjQyLDIwLjE5QTE0LjIyLDE0LjIyLDAsMCwxLDI5LDIxLjk0YTAuMiwwLjIsMCwwLDAsLjM4LTAuMSwxNC4yMiwxNC4yMiwwLDAsMC0uNTctMS43NWMtMC4wOS0uMjMtMC40Ny0wLjEzLTAuMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc3LDIwLjcxYTE0LjUxLDE0LjUxLDAsMCwxLC40MywxLjQyLDAuMiwwLjIsMCwwLDAsLjM4LTAuMSwxNC41MSwxNC41MSwwLDAsMC0uNDMtMS40MmMtMC4wOC0uMjQtMC40Ny0wLjE0LTAuMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc5LDIwLjg2YTEyLjIsMTIuMiwwLDAsMSwuNDUsMi4wNmMwLDAuMjUuNDMsMC4yNSwwLjM5LDBhMTIuNzksMTIuNzksMCwwLDAtLjQ2LTIuMTcsMC4yLDAuMiwwLDAsMC0uMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljg4LDIxLjc0cTAuMDYsMC45LjA4LDEuNzlhMC4yLDAuMiwwLDAsMCwuMzksMHEwLS45LTAuMDgtMS43OWMwLS4yNS0wLjQxLTAuMjUtMC4zOSwwaDBaIi8+PHBhdGggY2xhc3M9ImNscy00IiBkPSJNMjguODEsMjIuMDhxMC4wNywxLjA2LDAsMi4xMWEwLjIsMC4yLDAsMCwwLC4zOSwwcTAtMS4wNiwwLTIuMTFjMC0uMjUtMC40MS0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI5LjE1LDIzYTUuNTQsNS41NCwwLDAsMS0uMjYsMS41LDAuMiwwLjIsMCwwLDAsLjM4LjEsNiw2LDAsMCwwLC4yNy0xLjYsMC4yLDAuMiwwLDAsMC0uMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljg4LDIzLjY4YTQuNTIsNC41MiwwLDAsMS0uMjksMS40OWMtMC4wOS4yNCwwLjI5LDAuMzQsMC4zOCwwLjFhNC44Niw0Ljg2LDAsMCwwLC4zMS0xLjYsMC4yLDAuMiwwLDAsMC0uMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjcsMjQuMjlBMywzLDAsMCwxLDI4LjQ0LDI2Yy0wLjExLjIzLDAuMjMsMC40MywwLjM0LDAuMmEzLjM3LDMuMzcsMCwwLDAsLjMxLTEuOTFjMC0uMjUtMC40My0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc0LDI0LjU4TDI4LjM3LDI1LjlsMC4zOSwwLjA1YTkuNTgsOS41OCwwLDAsMSwuMTItMS4wN2wtMC4zOC0uMS0wLjE1LDEsMC4zOSwwLjA1YTEuNjYsMS42NiwwLDAsMCwwLS43N2MwLS4xNy0wLjI3LTAuMy0wLjM3LTAuMUEzLjE0LDMuMTQsMCwwLDAsMjguMDcsMjZMMjguNDUsMjZsMC0uNTVIMjh2MC4xOGEwLjIsMC4yLDAsMCwwLC4zOS4wNSwxMC4xMSwxMC4xMSwwLDAsMCwuNDQtMi4yNywwLjIsMC4yLDAsMCwwLS4zOS0wLjA1QTEzLjQzLDEzLjQzLDAsMCwwLDI4LjE1LDI1YzAsMC4yLjI0LDAuMzcsMC4zNiwwLjE1YTIuOTEsMi45MSwwLDAsMCwuMzctMS44NGMwLS4yLTAuMzUtMC4yOS0wLjM5LTAuMDVsLTAuMTksMS4xNSwwLjM5LDAuMDVhMTEuNzgsMTEuNzgsMCwwLDAtLjE0LTMuNjQsMC4yLDAuMiwwLDAsMC0uMzYsMGMtMC40NSwxLC4xOCwxLjM2LjIyLDIuNDRhMC4yLDAuMiwwLDAsMCwuMzkuMDVBMy45MiwzLjkyLDAsMCwwLDI5LDIyLjA4YzAtLjI2LTAuMzctMC4yNS0wLjM5LDAsMCwwLjQxLS4wOC44MS0wLjE0LDEuMjIsMCwwLjI1LjM0LDAuMzYsMC4zOCwwLjFsMC4wOC0uNDgtMC4zOS0uMDUtMC4wNi41NWMwLDAuMjUuMzMsMC4zNSwwLjM4LDAuMWEyLjc1LDIuNzUsMCwwLDAsLjA3LTAuNjYsMC4yLDAuMiwwLDAsMC0uMzktMC4wNWwtMC4wOC40OCwwLjM4LDAuMUMyOC45MiwyMywyOSwyMi41MywyOSwyMi4wOEgyOC42MWEyLjUzLDIuNTMsMCwwLDEtLjIsMS4wNWwwLjM5LDAuMDVjMC0uNjItMC41LTEuNzMtMC4yOC0yLjI0bC0wLjM2LDBhMTEuNDgsMTEuNDgsMCwwLDEsLjEyLDMuNTMsMC4yLDAuMiwwLDAsMCwuMzkuMDVsMC4xOS0xLjE1LTAuMzktLjA1YTIuNTcsMi41NywwLDAsMS0uMzEsMS42NGwwLjM2LDAuMTVhMTMuNDMsMTMuNDMsMCwwLDEsLjMzLTEuNjFsLTAuMzktLjA1QTkuNzMsOS43MywwLDAsMSwyOCwyNS41N2wwLjM5LDAuMDVWMjUuNDVhMC4yLDAuMiwwLDAsMC0uMzksMCwxLjgyLDEuODIsMCwwLDAsMCwuNjZjMCwwLjI0LjM3LDAuMTUsMC4zOS0uMDVsMC4yNS0uODktMC4zNy0uMWExLjY2LDEuNjYsMCwwLDEsMCwuNzcsMC4yLDAuMiwwLDAsMCwuMzkuMDVsMC4xNS0xYzAtLjI1LTAuMzQtMC4zNi0wLjM4LTAuMXMtMC4xMS43OC0uMTQsMS4xOGEwLjIsMC4yLDAsMCwwLC4zOS4wNWwwLjM3LTEuMzFhMC4yLDAuMiwwLDAsMC0uMzgtMC4xaDBaIi8+PHBhdGggY2xhc3M9ImNscy00IiBkPSJNMjguMTYsMjAuNDNsMC4xMiwwLjg3YzAsMC4yNS40MywwLjI1LDAuMzksMGwtMC4xMi0uODdjMC0uMjUtMC40My0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjI3LDI0LjgzYTYuMzQsNi4zNCwwLDAsMC0uMzYsMSwwLjIsMC4yLDAsMCwwLC4zOC4xQTUuNzQsNS43NCwwLDAsMSwyOC42MSwyNWMwLjEtLjIzLTAuMjQtMC40My0wLjM0LTAuMmgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI3Ljc5LDI1LjIyYzAsMC4zMy0uMjUsMS4xNy4wNywxLjQxczAuNTUtLjMzLjYzLTAuNTVMMjguMSwyNmExLjc3LDEuNzcsMCwwLDAsLjA4LjU3LDAuMiwwLjIsMCwwLDAsLjIyLjA5aDBhMC4yLDAuMiwwLDAsMCwuMTQtMC4xOWgwYTAuMiwwLjIsMCwwLDAtLjM5LDBoMGwwLjE0LS4xOWgwbDAuMjIsMC4wOWExLjY4LDEuNjgsMCwwLDEsMC0uMzdBMC4yLDAuMiwwLDAsMCwyOC4xLDI2YTEsMSwwLDAsMS0uMTguMzZoMC4yYTUuMiw1LjIsMCwwLDEsLjA2LTEuMTFjMC0uMjUtMC4zOC0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI5LjA4LDE5LjE2YTE4LjIxLDE4LjIxLDAsMCwxLC40NiwxLjkxYzAsMC4yNS40MywwLjE0LDAuMzgtLjFhMTguMjEsMTguMjEsMCwwLDAtLjQ2LTEuOTEsMC4yLDAuMiwwLDAsMC0uMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjQ0LDI0LjRMMjguMjIsMjZhMC42NCwwLjY0LDAsMCwwLC4zNC44NUMyOS4yNiwyNywyOS44NCwyNiwzMCwyNS40M2EwLjM5LDAuMzksMCwwLDAtLjc2LTAuMjEsMS4zMiwxLjMyLDAsMCwxLS43LjkzbDAuMywwSDI4LjczTDI5LDI2LjI4YTYsNiwwLDAsMSwuMTMtMC44M2wwLjE0LTFjMC4wNy0uNS0wLjcyLTAuNS0wLjc5LDBoMFoiLz48cGF0aCBjbGFzcz0iY2xzLTQiIGQ9Ik0zMS4wOSwxNC43YTMuNzMsMy43MywwLDAsMSwwLC44OWMtMC4wOC41LDAuNjgsMC43MSwwLjc2LDAuMjFhNC42LDQuNiwwLDAsMCwuMDYtMS4xYzAtLjUtMC44Mi0wLjUxLTAuNzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTMxLjExLDE1LjY5YTAuMzksMC4zOSwwLDAsMCwwLS43OSwwLjM5LDAuMzksMCwwLDAsMCwuNzloMFoiLz48cmVjdCBjbGFzcz0iY2xzLTUiIHg9IjUuMzciIHk9IjIuOTUiIHdpZHRoPSIyMy4xNSIgaGVpZ2h0PSIxNy41Ii8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMTEuNTcsMTEuOTNhMC44NCwwLjg0LDAsMCwxLS4wOS0xLjRsNi42Ny00YTMsMywwLDAsMSwyLjI5LS4xM2wyLjM1LDEuNGExLDEsMCwwLDEsMS4xMS0uMTVsMiwxLjIzczAuNjUsMC4zNSwwLC44OWwxLjksMS4xMmEwLjgyLDAuODIsMCwwLDEsLjIsMS40NWwtNi42LDRhMy4yLDMuMiwwLDAsMS0yLjI5LjEzWiIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtNyIgY3g9IjE5Ljc1IiBjeT0iMTEuNTEiIHJ4PSI0LjE5IiByeT0iMi40NyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTIuMDUgNC42Mykgcm90YXRlKC0xMi43MSkiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTYiIGN4PSIxOS43NSIgY3k9IjExLjUxIiByeD0iMy4xNyIgcnk9IjEuODciIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0yLjA1IDQuNjMpIHJvdGF0ZSgtMTIuNzEpIi8+PGVsbGlwc2UgY2xhc3M9ImNscy02IiBjeD0iMTIuMzQiIGN5PSIyNC4xNSIgcng9IjQuNjUiIHJ5PSI4LjAxIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMTAuOTEgMTAuMTkpIHJvdGF0ZSgtMzEuOTUpIi8+PHBvbHlnb24gY2xhc3M9ImNscy04IiBwb2ludHM9IjEwLjQ4IDE5LjgyIDEwLjMyIDI2LjA4IDE1Ljg5IDI2LjI2IDEwLjQ4IDE5LjgyIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMjEuNzQsMjQuMzRsLTAuMTcsNC45YTAuNjcsMC42NywwLDAsMCwxLC41OUwyNCwyOC45YTAuNjcsMC42NywwLDAsMSwuNzgsMGwyLDEuNjNhMC42NywwLjY3LDAsMCwwLDEuMDktLjVMMjguMjQsMTYuOWEwLjI2LDAuMjYsMCwwLDAtLjQ4LTAuMTNsLTMuMDcsNS4zOGEwLjY3LDAuNjcsMCwwLDEtLjI0LjI0bC0yLjM3LDEuNEEwLjY3LDAuNjcsMCwwLDAsMjEuNzQsMjQuMzRaIi8+PHBhdGggY2xhc3M9ImNscy05IiBkPSJNMzEsMTguMjVhMTAuOSwxMC45LDAsMCwxLS4zLDkuODUiLz48L3N2Zz4=';
// eslint-disable-next-line max-len
const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfNSIgZGF0YS1uYW1lPSJMYXllciA1IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNkMmNlNDY7fS5jbHMtMntmaWxsOiM0MDk0NzI7fS5jbHMtM3tmaWxsOiMzNDYyOWI7fS5jbHMtNHtmaWxsOiMzMzYzOWM7fS5jbHMtNSwuY2xzLTl7ZmlsbDpub25lO30uY2xzLTZ7ZmlsbDojZmZmO30uY2xzLTd7ZmlsbDojZDFjZTQ1O30uY2xzLTh7ZmlsbDojM2Q5NTczO30uY2xzLTl7c3Ryb2tlOiNmZmY7c3Ryb2tlLWxpbmVjYXA6cm91bmQ7c3Ryb2tlLW1pdGVybGltaXQ6MTA7c3Ryb2tlLXdpZHRoOjEuMnB4O308L3N0eWxlPjwvZGVmcz48dGl0bGU+VGVhY2hhYmxlIE1hY2hpbmUgX2ljb24tMDItMDMtMDM8L3RpdGxlPjxwb2x5Z29uIGNsYXNzPSJjbHMtMSIgcG9pbnRzPSI1LjM3IDExLjI5IDE5LjkzIDIwIDM0LjEzIDExLjI5IDE5Ljc1IDIuOTUgNS4zNyAxMS4yOSIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSI0LjkzIDI3Ljk4IDE5LjQyIDM2LjYyIDE5LjkzIDIwIDUuMzcgMTEuMjkgNC45MyAyNy45OCIvPjxwb2x5Z29uIGNsYXNzPSJjbHMtMyIgcG9pbnRzPSIxOS45MyAyMCAxOS40MiAzNi42MiAzNC4xMyAyOC4zMSAzNC4xMyAxMS4yOSAxOS45MyAyMCIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjM4LDIwLjU5QTIuNDYsMi40NiwwLDAsMSwyOC41OSwyMiwwLjIsMC4yLDAsMCwwLDI5LDIyYTIuNzUsMi43NSwwLDAsMC0uMzMtMS43MmMtMC4xNi0uMi0wLjQ0LjA4LTAuMjgsMC4yOGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjQyLDIwLjE5QTE0LjIyLDE0LjIyLDAsMCwxLDI5LDIxLjk0YTAuMiwwLjIsMCwwLDAsLjM4LTAuMSwxNC4yMiwxNC4yMiwwLDAsMC0uNTctMS43NWMtMC4wOS0uMjMtMC40Ny0wLjEzLTAuMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc3LDIwLjcxYTE0LjUxLDE0LjUxLDAsMCwxLC40MywxLjQyLDAuMiwwLjIsMCwwLDAsLjM4LTAuMSwxNC41MSwxNC41MSwwLDAsMC0uNDMtMS40MmMtMC4wOC0uMjQtMC40Ny0wLjE0LTAuMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc5LDIwLjg2YTEyLjIsMTIuMiwwLDAsMSwuNDUsMi4wNmMwLDAuMjUuNDMsMC4yNSwwLjM5LDBhMTIuNzksMTIuNzksMCwwLDAtLjQ2LTIuMTcsMC4yLDAuMiwwLDAsMC0uMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljg4LDIxLjc0cTAuMDYsMC45LjA4LDEuNzlhMC4yLDAuMiwwLDAsMCwuMzksMHEwLS45LTAuMDgtMS43OWMwLS4yNS0wLjQxLTAuMjUtMC4zOSwwaDBaIi8+PHBhdGggY2xhc3M9ImNscy00IiBkPSJNMjguODEsMjIuMDhxMC4wNywxLjA2LDAsMi4xMWEwLjIsMC4yLDAsMCwwLC4zOSwwcTAtMS4wNiwwLTIuMTFjMC0uMjUtMC40MS0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI5LjE1LDIzYTUuNTQsNS41NCwwLDAsMS0uMjYsMS41LDAuMiwwLjIsMCwwLDAsLjM4LjEsNiw2LDAsMCwwLC4yNy0xLjYsMC4yLDAuMiwwLDAsMC0uMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljg4LDIzLjY4YTQuNTIsNC41MiwwLDAsMS0uMjksMS40OWMtMC4wOS4yNCwwLjI5LDAuMzQsMC4zOCwwLjFhNC44Niw0Ljg2LDAsMCwwLC4zMS0xLjYsMC4yLDAuMiwwLDAsMC0uMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjcsMjQuMjlBMywzLDAsMCwxLDI4LjQ0LDI2Yy0wLjExLjIzLDAuMjMsMC40MywwLjM0LDAuMmEzLjM3LDMuMzcsMCwwLDAsLjMxLTEuOTFjMC0uMjUtMC40My0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4Ljc0LDI0LjU4TDI4LjM3LDI1LjlsMC4zOSwwLjA1YTkuNTgsOS41OCwwLDAsMSwuMTItMS4wN2wtMC4zOC0uMS0wLjE1LDEsMC4zOSwwLjA1YTEuNjYsMS42NiwwLDAsMCwwLS43N2MwLS4xNy0wLjI3LTAuMy0wLjM3LTAuMUEzLjE0LDMuMTQsMCwwLDAsMjguMDcsMjZMMjguNDUsMjZsMC0uNTVIMjh2MC4xOGEwLjIsMC4yLDAsMCwwLC4zOS4wNSwxMC4xMSwxMC4xMSwwLDAsMCwuNDQtMi4yNywwLjIsMC4yLDAsMCwwLS4zOS0wLjA1QTEzLjQzLDEzLjQzLDAsMCwwLDI4LjE1LDI1YzAsMC4yLjI0LDAuMzcsMC4zNiwwLjE1YTIuOTEsMi45MSwwLDAsMCwuMzctMS44NGMwLS4yLTAuMzUtMC4yOS0wLjM5LTAuMDVsLTAuMTksMS4xNSwwLjM5LDAuMDVhMTEuNzgsMTEuNzgsMCwwLDAtLjE0LTMuNjQsMC4yLDAuMiwwLDAsMC0uMzYsMGMtMC40NSwxLC4xOCwxLjM2LjIyLDIuNDRhMC4yLDAuMiwwLDAsMCwuMzkuMDVBMy45MiwzLjkyLDAsMCwwLDI5LDIyLjA4YzAtLjI2LTAuMzctMC4yNS0wLjM5LDAsMCwwLjQxLS4wOC44MS0wLjE0LDEuMjIsMCwwLjI1LjM0LDAuMzYsMC4zOCwwLjFsMC4wOC0uNDgtMC4zOS0uMDUtMC4wNi41NWMwLDAuMjUuMzMsMC4zNSwwLjM4LDAuMWEyLjc1LDIuNzUsMCwwLDAsLjA3LTAuNjYsMC4yLDAuMiwwLDAsMC0uMzktMC4wNWwtMC4wOC40OCwwLjM4LDAuMUMyOC45MiwyMywyOSwyMi41MywyOSwyMi4wOEgyOC42MWEyLjUzLDIuNTMsMCwwLDEtLjIsMS4wNWwwLjM5LDAuMDVjMC0uNjItMC41LTEuNzMtMC4yOC0yLjI0bC0wLjM2LDBhMTEuNDgsMTEuNDgsMCwwLDEsLjEyLDMuNTMsMC4yLDAuMiwwLDAsMCwuMzkuMDVsMC4xOS0xLjE1LTAuMzktLjA1YTIuNTcsMi41NywwLDAsMS0uMzEsMS42NGwwLjM2LDAuMTVhMTMuNDMsMTMuNDMsMCwwLDEsLjMzLTEuNjFsLTAuMzktLjA1QTkuNzMsOS43MywwLDAsMSwyOCwyNS41N2wwLjM5LDAuMDVWMjUuNDVhMC4yLDAuMiwwLDAsMC0uMzksMCwxLjgyLDEuODIsMCwwLDAsMCwuNjZjMCwwLjI0LjM3LDAuMTUsMC4zOS0uMDVsMC4yNS0uODktMC4zNy0uMWExLjY2LDEuNjYsMCwwLDEsMCwuNzcsMC4yLDAuMiwwLDAsMCwuMzkuMDVsMC4xNS0xYzAtLjI1LTAuMzQtMC4zNi0wLjM4LTAuMXMtMC4xMS43OC0uMTQsMS4xOGEwLjIsMC4yLDAsMCwwLC4zOS4wNWwwLjM3LTEuMzFhMC4yLDAuMiwwLDAsMC0uMzgtMC4xaDBaIi8+PHBhdGggY2xhc3M9ImNscy00IiBkPSJNMjguMTYsMjAuNDNsMC4xMiwwLjg3YzAsMC4yNS40MywwLjI1LDAuMzksMGwtMC4xMi0uODdjMC0uMjUtMC40My0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjI3LDI0LjgzYTYuMzQsNi4zNCwwLDAsMC0uMzYsMSwwLjIsMC4yLDAsMCwwLC4zOC4xQTUuNzQsNS43NCwwLDAsMSwyOC42MSwyNWMwLjEtLjIzLTAuMjQtMC40My0wLjM0LTAuMmgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI3Ljc5LDI1LjIyYzAsMC4zMy0uMjUsMS4xNy4wNywxLjQxczAuNTUtLjMzLjYzLTAuNTVMMjguMSwyNmExLjc3LDEuNzcsMCwwLDAsLjA4LjU3LDAuMiwwLjIsMCwwLDAsLjIyLjA5aDBhMC4yLDAuMiwwLDAsMCwuMTQtMC4xOWgwYTAuMiwwLjIsMCwwLDAtLjM5LDBoMGwwLjE0LS4xOWgwbDAuMjIsMC4wOWExLjY4LDEuNjgsMCwwLDEsMC0uMzdBMC4yLDAuMiwwLDAsMCwyOC4xLDI2YTEsMSwwLDAsMS0uMTguMzZoMC4yYTUuMiw1LjIsMCwwLDEsLjA2LTEuMTFjMC0uMjUtMC4zOC0wLjI1LTAuMzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI5LjA4LDE5LjE2YTE4LjIxLDE4LjIxLDAsMCwxLC40NiwxLjkxYzAsMC4yNS40MywwLjE0LDAuMzgtLjFhMTguMjEsMTguMjEsMCwwLDAtLjQ2LTEuOTEsMC4yLDAuMiwwLDAsMC0uMzguMWgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTI4LjQ0LDI0LjRMMjguMjIsMjZhMC42NCwwLjY0LDAsMCwwLC4zNC44NUMyOS4yNiwyNywyOS44NCwyNiwzMCwyNS40M2EwLjM5LDAuMzksMCwwLDAtLjc2LTAuMjEsMS4zMiwxLjMyLDAsMCwxLS43LjkzbDAuMywwSDI4LjczTDI5LDI2LjI4YTYsNiwwLDAsMSwuMTMtMC44M2wwLjE0LTFjMC4wNy0uNS0wLjcyLTAuNS0wLjc5LDBoMFoiLz48cGF0aCBjbGFzcz0iY2xzLTQiIGQ9Ik0zMS4wOSwxNC43YTMuNzMsMy43MywwLDAsMSwwLC44OWMtMC4wOC41LDAuNjgsMC43MSwwLjc2LDAuMjFhNC42LDQuNiwwLDAsMCwuMDYtMS4xYzAtLjUtMC44Mi0wLjUxLTAuNzksMGgwWiIvPjxwYXRoIGNsYXNzPSJjbHMtNCIgZD0iTTMxLjExLDE1LjY5YTAuMzksMC4zOSwwLDAsMCwwLS43OSwwLjM5LDAuMzksMCwwLDAsMCwuNzloMFoiLz48cmVjdCBjbGFzcz0iY2xzLTUiIHg9IjUuMzciIHk9IjIuOTUiIHdpZHRoPSIyMy4xNSIgaGVpZ2h0PSIxNy41Ii8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMTEuNTcsMTEuOTNhMC44NCwwLjg0LDAsMCwxLS4wOS0xLjRsNi42Ny00YTMsMywwLDAsMSwyLjI5LS4xM2wyLjM1LDEuNGExLDEsMCwwLDEsMS4xMS0uMTVsMiwxLjIzczAuNjUsMC4zNSwwLC44OWwxLjksMS4xMmEwLjgyLDAuODIsMCwwLDEsLjIsMS40NWwtNi42LDRhMy4yLDMuMiwwLDAsMS0yLjI5LjEzWiIvPjxlbGxpcHNlIGNsYXNzPSJjbHMtNyIgY3g9IjE5Ljc1IiBjeT0iMTEuNTEiIHJ4PSI0LjE5IiByeT0iMi40NyIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTIuMDUgNC42Mykgcm90YXRlKC0xMi43MSkiLz48ZWxsaXBzZSBjbGFzcz0iY2xzLTYiIGN4PSIxOS43NSIgY3k9IjExLjUxIiByeD0iMy4xNyIgcnk9IjEuODciIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0yLjA1IDQuNjMpIHJvdGF0ZSgtMTIuNzEpIi8+PGVsbGlwc2UgY2xhc3M9ImNscy02IiBjeD0iMTIuMzQiIGN5PSIyNC4xNSIgcng9IjQuNjUiIHJ5PSI4LjAxIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMTAuOTEgMTAuMTkpIHJvdGF0ZSgtMzEuOTUpIi8+PHBvbHlnb24gY2xhc3M9ImNscy04IiBwb2ludHM9IjEwLjQ4IDE5LjgyIDEwLjMyIDI2LjA4IDE1Ljg5IDI2LjI2IDEwLjQ4IDE5LjgyIi8+PHBhdGggY2xhc3M9ImNscy02IiBkPSJNMjEuNzQsMjQuMzRsLTAuMTcsNC45YTAuNjcsMC42NywwLDAsMCwxLC41OUwyNCwyOC45YTAuNjcsMC42NywwLDAsMSwuNzgsMGwyLDEuNjNhMC42NywwLjY3LDAsMCwwLDEuMDktLjVMMjguMjQsMTYuOWEwLjI2LDAuMjYsMCwwLDAtLjQ4LTAuMTNsLTMuMDcsNS4zOGEwLjY3LDAuNjcsMCwwLDEtLjI0LjI0bC0yLjM3LDEuNEEwLjY3LDAuNjcsMCwwLDAsMjEuNzQsMjQuMzRaIi8+PHBhdGggY2xhc3M9ImNscy05IiBkPSJNMzEsMTguMjVhMTAuOSwxMC45LDAsMCwxLS4zLDkuODUiLz48L3N2Zz4=';

/**
 * Class for the AI related blocks in Scratch 3.0
 * @param {Runtime} runtime - the runtime instantiating this block package.
 * @constructor
 */

class teachableMachine {

    constructor(runtime, model = null, modelUrl = null, modelType = null) {
        /**
         * The runtime instantiating this block package.
         * @type {Runtime}
         */
        this.runtime = runtime;
        this.runtime.checkInternetConnection();
        this.runtime.tmModelUrl = modelUrl;
        this.runtime.tmModelType = modelType

        //URL of the model
        this.tmUrl = '';
        this.model = model;
        this.modelUrl = modelUrl;
        this.modelType = modelType;
        this.modelClasses = ['Class'];
        this.extensionName = 'Machine Learning';
    }

    get MODEL_CLASSES() {
        return this.modelClasses;
    }

    set MODEL_CLASSES(modelClasses) {
        this.modelClasses = modelClasses;
    }

    getInfo() {

        return {
            id: 'teachableMachine',
            name: formatMessage({
                id: 'teachableMachine.categoryName',
                default: 'Machine Learning',
                description: 'Name of extension'
            }),
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            menus: {
                modelClass: this.MODEL_CLASSES,
                sources: [
                    { text: 'web camera', value: 'webCam' },
                    'stage',
                    'costume',
                    'backdrop'
                ],
                sources1: [
                    { text: 'web camera', value: 'webCam' },
                    'stage'
                ],
                videoState: [
                    { text: 'off', value: 'off' },
                    { text: 'on', value: 'on' },
                    { text: 'on flipped', value: 'onFlipped' }
                ]
            },
            blocks: this.model ? this.modelType === "image" ? [
                {
                    opcode: 'createModel',
                    text: formatMessage({
                        id: 'teachableMachine.createModel',
                        default: 'Create a Model',
                        description: 'Create model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'CREATE_TM_MODEL'
                },
                {
                    opcode: 'loadModel',
                    text: formatMessage({
                        id: 'teachableMachine.loadModel',
                        default: 'Load a Model',
                        description: 'Load model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'LOAD_TM_MODEL'
                },
                {
                    opcode: 'refreshModel',
                    text: formatMessage({
                        id: 'teachableMachine.refreshModel',
                        default: 'Refresh Model',
                        description: 'Refresh loaded model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'REFRESH_TM_MODEL'
                },
                {
                    message: 'Image Model'
                },
                {
                    opcode: 'recogniseImageFromWebCam',
                    text: formatMessage({
                        id: 'teachableMachine.imageModel.recogniseImageFromWebCam',
                        default: 'open recognition window',
                        description: 'Recognise Class Label using Web Camera'
                    }),
                    blockType: BlockType.COMMAND
                },
                {
                    opcode: 'classLabels',
                    text: formatMessage({
                        id: 'teachableMachine.imageModel.classLabels',
                        default: '[CLASS]',
                        description: 'Returns ClassLabel'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        CLASS: {
                            type: ArgumentType.STRING,
                            menu: 'modelClass',
                            defaultValue: this.MODEL_CLASSES[0]
                        }
                    }
                },
                {
                    opcode: 'isImageIdentifiedClass',
                    text: formatMessage({
                        id: 'teachableMachine.imageModel.isImageIdentifiedClass',
                        default: 'is identified class from [SOURCE] is [CLASS]?',
                        description: 'Get Identified Class from web camera'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        SOURCE: {
                            type: ArgumentType.STRING,
                            menu: 'sources',
                            defaultValue: 'webCam'
                        },
                        CLASS: {
                            type: ArgumentType.STRING,
                            menu: 'modelClass',
                            defaultValue: this.MODEL_CLASSES[0]
                        }
                    }
                },
                {
                    opcode: 'getImageIdentifiedClass',
                    text: formatMessage({
                        id: 'teachableMachine.imageModel.getImageIdentifiedClass',
                        default: 'identify class from [SOURCE]',
                        description: 'Get Identified Class'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        SOURCE: {
                            type: ArgumentType.STRING,
                            menu: 'sources',
                            defaultValue: 'webCam'
                        }
                    }
                },
                {
                    opcode: 'getImageClassConfidence',
                    text: formatMessage({
                        id: 'teachableMachine.imageModel.getImageClassConfidence',
                        default: 'get confidence of class [CLASS] from [SOURCE]',
                        description: 'Get Identified Class from web camera'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        CLASS: {
                            type: ArgumentType.STRING,
                        },
                        SOURCE: {
                            type: ArgumentType.STRING,
                            menu: 'sources',
                            defaultValue: 'webCam'
                        }
                    }
                },
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'teachableMachine.toggleStageVideoFeed',
                        default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] transparency',
                        description: 'toggle video feed on stage'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        VIDEO_STATE: {
                            type: ArgumentType.STRING,
                            menu: 'videoState',
                            defaultValue: 'on'
                        },
                        TRANSPARENCY: {
                            type: ArgumentType.MATHSLIDER100,
                            default: 0
                        }
                    }
                }
            ] : this.modelType === "audio" ? [
                {
                    opcode: 'createModel',
                    text: formatMessage({
                        id: 'teachableMachine.createModel',
                        default: 'Create a Model',
                        description: 'Create model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'CREATE_TM_MODEL'
                },
                {
                    opcode: 'loadModel',
                    text: formatMessage({
                        id: 'teachableMachine.loadModel',
                        default: 'Load a Model',
                        description: 'Load model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'LOAD_TM_MODEL'
                },
                {
                    opcode: 'refreshModel',
                    text: formatMessage({
                        id: 'teachableMachine.refreshModel',
                        default: 'Refresh Model',
                        description: 'Refresh loaded model'
                    }),
                    blockType: BlockType.BUTTON,
                    func: 'REFRESH_TM_MODEL'
                },
                {
                    message: 'Audio Model'
                },
                {
                    opcode: 'recogniseAudioFromRecognisionWinodw',
                    text: formatMessage({
                        id: 'teachableMachine.audioModel.recogniseAudioFromRecognisionWinodw',
                        default: 'open recognition window',
                        description: 'Recognise Class Label using Web Camera'
                    }),
                    blockType: BlockType.COMMAND
                },
                {
                    opcode: 'classLabels',
                    text: formatMessage({
                        id: 'teachableMachine.audioModel.classLabels',
                        default: '[CLASS]',
                        description: 'Returns ClassLabel'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        CLASS: {
                            type: ArgumentType.STRING,
                            menu: 'modelClass',
                            defaultValue: this.MODEL_CLASSES[0]
                        }
                    }
                },
                {
                    opcode: 'isAudioIdentifiedClass',
                    text: formatMessage({
                        id: 'teachableMachine.audioModel.isAudioIdentifiedClass',
                        default: 'is identified class [CLASS]?',
                        description: 'Get Identified Class'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        CLASS: {
                            type: ArgumentType.STRING,
                            menu: 'modelClass',
                            defaultValue: this.MODEL_CLASSES[0]
                        }
                    }
                },
                {
                    opcode: 'getAudioIdentifiedClass',
                    text: formatMessage({
                        id: 'teachableMachine.audioModel.getAudioIdentifiedClass',
                        default: 'identify class',
                        description: 'Get Identified Class'
                    }),
                    blockType: BlockType.REPORTER
                },
                {
                    opcode: 'getAudioClassConfidence',
                    text: formatMessage({
                        id: 'teachableMachine.audioModel.getAudioClassConfidence',
                        default: 'get confidence of class [CLASS]',
                        description: 'Get Identified Class from web camera'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        CLASS: {
                            type: ArgumentType.STRING,
                        }
                    }
                }
            ] : [
                        {
                            opcode: 'createModel',
                            text: formatMessage({
                                id: 'teachableMachine.createModel',
                                default: 'Create a Model',
                                description: 'Create model'
                            }),
                            blockType: BlockType.BUTTON,
                            func: 'CREATE_TM_MODEL'
                        },
                        {
                            opcode: 'loadModel',
                            text: formatMessage({
                                id: 'teachableMachine.loadModel',
                                default: 'Load a Model',
                                description: 'Load model'
                            }),
                            blockType: BlockType.BUTTON,
                            func: 'LOAD_TM_MODEL'
                        },
                        {
                            opcode: 'refreshModel',
                            text: formatMessage({
                                id: 'teachableMachine.refreshModel',
                                default: 'Refresh Model',
                                description: 'Refresh loaded model'
                            }),
                            blockType: BlockType.BUTTON,
                            func: 'REFRESH_TM_MODEL'
                        },
                        {
                            message: 'Pose Model'
                        },
                        {
                            opcode: 'recognisePoseFromRecognisionWinodw',
                            text: formatMessage({
                                id: 'teachableMachine.poseModel.recognisePoseFromRecognisionWinodw',
                                default: 'open recognition window',
                                description: 'Recognise Class Label using Web Camera'
                            }),
                            blockType: BlockType.COMMAND
                        },
                        {
                            opcode: 'classLabels',
                            text: formatMessage({
                                id: 'teachableMachine.poseModel.classLabels',
                                default: '[CLASS]',
                                description: 'Returns ClassLabel'
                            }),
                            blockType: BlockType.REPORTER,
                            arguments: {
                                CLASS: {
                                    type: ArgumentType.STRING,
                                    menu: 'modelClass',
                                    defaultValue: this.MODEL_CLASSES[0]
                                }
                            }
                        },
                        {
                            opcode: 'isPoseIdentifiedClass',
                            text: formatMessage({
                                id: 'teachableMachine.poseModel.isPoseIdentifiedClass',
                                default: 'is identified class from [SOURCE] is [CLASS]?',
                                description: 'Get Identified Class'
                            }),
                            blockType: BlockType.BOOLEAN,
                            arguments: {
                                SOURCE: {
                                    type: ArgumentType.STRING,
                                    menu: 'sources1',
                                    defaultValue: 'webCam'
                                },
                                CLASS: {
                                    type: ArgumentType.STRING,
                                    menu: 'modelClass',
                                    defaultValue: this.MODEL_CLASSES[0]
                                }
                            }
                        },
                        {
                            opcode: 'getPoseIdentifiedClass',
                            text: formatMessage({
                                id: 'teachableMachine.poseModel.getPoseIdentifiedClass',
                                default: 'identify class from [SOURCE]',
                                description: 'Get Identified Class'
                            }),
                            blockType: BlockType.REPORTER,
                            arguments: {
                                SOURCE: {
                                    type: ArgumentType.STRING,
                                    menu: 'sources1',
                                    defaultValue: 'webCam'
                                }
                            }
                        },
                        {
                            opcode: 'getPoseClassConfidence',
                            text: formatMessage({
                                id: 'teachableMachine.poseModel.getPoseClassConfidence',
                                default: 'get confidence of class [CLASS] from [SOURCE]',
                                description: 'Get Identified Class from web camera'
                            }),
                            blockType: BlockType.REPORTER,
                            arguments: {
                                CLASS: {
                                    type: ArgumentType.STRING,
                                },
                                SOURCE: {
                                    type: ArgumentType.STRING,
                                    menu: 'sources1',
                                    defaultValue: 'webCam'
                                }
                            }
                        },
                        {
                            opcode: 'toggleStageVideoFeed',
                            text: formatMessage({
                                id: 'teachableMachine.toggleStageVideoFeed',
                                default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] transparency',
                                description: 'toggle video feed on stage'
                            }),
                            blockType: BlockType.COMMAND,
                            arguments: {
                                VIDEO_STATE: {
                                    type: ArgumentType.STRING,
                                    menu: 'videoState',
                                    defaultValue: 'on'
                                },
                                TRANSPARENCY: {
                                    type: ArgumentType.MATHSLIDER100,
                                    default: 0
                                }
                            }
                        }
                    ] : [
                    {
                        opcode: 'createModel',
                        text: formatMessage({
                            id: 'teachableMachine.createModel',
                            default: 'Create a Model',
                            description: 'Create model'
                        }),
                        blockType: BlockType.BUTTON,
                        func: 'CREATE_TM_MODEL'
                    },
                    {
                        opcode: 'loadModel',
                        text: formatMessage({
                            id: 'teachableMachine.loadModel',
                            default: 'Load a Model',
                            description: 'Load model'
                        }),
                        blockType: BlockType.BUTTON,
                        func: 'LOAD_TM_MODEL'
                    }
                ]

        }
    }

    recogniseImageFromWebCam(args, util) {
        if (this.model)
            util.runtime.emit('TEACHABLE_MACHINE_PREDICT', this.model, this.modelType);
    }

    toggleStageVideoFeed(args, util) {
        const state = args.VIDEO_STATE;
        this.globalVideoState = args.VIDEO_STATE;

        this.runtime.ioDevices.video.setPreviewGhost(args.TRANSPARENCY);

        if (state === 'off') {
            this.runtime.ioDevices.video.disableVideo();
        } else {
            this.runtime.ioDevices.video.enableVideo();
            // Mirror if state is ON. Do not mirror if state is ON_FLIPPED.
            this.runtime.ioDevices.video.mirror = state === 'on';
        }
    }

    classLabels(args, util) {
        return args.CLASS;
    }

    isImageIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            resolve(predictions.reduce(function (prev, current) {
                                return (prev.probability > current.probability) ? prev : current;
                            }).className === args.CLASS);
                        });
                    });
                case 'costume':
                    return new Promise(resolve => {
                        let costume = util.target.getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img');

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                resolve(predictions.reduce(function (prev, current) {
                                    return (prev.probability > current.probability) ? prev : current;
                                }).className === args.CLASS);
                            });
                        };

                        image.setAttribute("src", url);
                    });
                case 'backdrop':
                    return new Promise(resolve => {
                        let costume = util.runtime.getTargetForStage().getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img');

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                resolve(predictions.reduce(function (prev, current) {
                                    return (prev.probability > current.probability) ? prev : current;
                                }).className === args.CLASS);
                            });
                        };

                        image.setAttribute("src", url);
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.predict(image).then(predictions => {
                                    resolve(predictions.reduce(function (prev, current) {
                                        return (prev.probability > current.probability) ? prev : current;
                                    }).className === args.CLASS);
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });

                default:
                    return ''
            }
        }
    }

    getImageIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            resolve(predictions.reduce(function (prev, current) {
                                return (prev.probability > current.probability) ? prev : current;
                            }).className);
                        });
                    });
                case 'costume':
                    return new Promise(resolve => {
                        let costume = util.target.getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img');

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                resolve(predictions.reduce(function (prev, current) {
                                    return (prev.probability > current.probability) ? prev : current;
                                }).className);
                            });
                        };

                        image.setAttribute("src", url);
                    });
                case 'backdrop':
                    return new Promise(resolve => {
                        let costume = util.runtime.getTargetForStage().getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img');

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                resolve(predictions.reduce(function (prev, current) {
                                    return (prev.probability > current.probability) ? prev : current;
                                }).className);
                            });
                        };

                        image.setAttribute("src", url);
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.predict(image).then(predictions => {
                                    resolve(predictions.reduce(function (prev, current) {
                                        return (prev.probability > current.probability) ? prev : current;
                                    }).className);
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });

                default:
                    return ''
            }
        }
    }

    getImageClassConfidence(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            let classResult = predictions.find(prediction => {
                                return prediction.className === args.CLASS
                            })
                            resolve(classResult ? classResult.probability.toFixed(2) : '');
                        });
                    });
                case 'costume':
                    return new Promise(resolve => {
                        let costume = util.target.getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img', url);

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                let classResult = predictions.find(prediction => {
                                    return prediction.className === args.CLASS;
                                })
                                resolve(classResult ? classResult.probability.toFixed(2) : '');
                            });
                        }

                        image.setAttribute("src", url);
                    });
                case 'backdrop':
                    return new Promise(resolve => {
                        let costume = util.runtime.getTargetForStage().getCurrentCostume();

                        const data = costume.asset.data.reduce((data, byte) => {
                            return data + String.fromCharCode(byte);
                        }, '');
                        const url = 'data:' + costume.asset.assetType.contentType + ';base64,' + btoa(data);

                        let self = this;
                        let image = document.createElement('img');

                        image.onload = function () {
                            self.model.predict(image).then(predictions => {
                                let classResult = predictions.find(prediction => {
                                    return prediction.className === args.CLASS;
                                })
                                resolve(classResult ? classResult.probability.toFixed(2) : '');
                            });
                        }

                        image.setAttribute("src", url);
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.predict(image).then(predictions => {
                                    let classResult = predictions.find(prediction => {
                                        return prediction.className === args.CLASS;
                                    })
                                    resolve(classResult ? classResult.probability.toFixed(2) : '');
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });
                default:
                    return ''
            }
        }
    }

    recogniseAudioFromRecognisionWinodw(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model)
            util.runtime.emit('TEACHABLE_MACHINE_PREDICT', this.model, this.modelType);

    }

    isAudioIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            return new Promise(resolve => {
                let labels = this.model.wordLabels();
                util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, result => {
                    resolve(labels[result.scores.indexOf(Math.max(...result.scores))] === args.CLASS);
                });
            })
        }
    }

    getAudioIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            return new Promise(resolve => {
                let labels = this.model.wordLabels();
                util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, result => {
                    resolve(labels[result.scores.indexOf(Math.max(...result.scores))]);
                });
            })
        }
    }

    getAudioClassConfidence(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            return new Promise(resolve => {
                let labels = this.model.wordLabels();
                util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, result => {
                    if (labels.indexOf(args.CLASS) > -1)
                        resolve(result.scores[labels.indexOf(args.CLASS)].toFixed(2));
                    else
                        resolve();
                });
            })
        }
    }

    recognisePoseFromRecognisionWinodw(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model)
            util.runtime.emit('TEACHABLE_MACHINE_PREDICT', this.model, this.modelType);
    }

    isPoseIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            resolve(predictions.reduce(function (prev, current) {
                                return (prev.probability > current.probability) ? prev : current;
                            }).className === args.CLASS);
                        });
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.estimatePose(image).then(({ pose, posenetOutput }) => {
                                    self.model.predict(posenetOutput).then(predictions => {
                                        resolve(predictions.reduce(function (prev, current) {
                                            return (prev.probability > current.probability) ? prev : current;
                                        }).className === args.CLASS);
                                    });
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });
                default:
                    return '';
            }

        }
    }

    getPoseIdentifiedClass(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            resolve(predictions.reduce(function (prev, current) {
                                return (prev.probability > current.probability) ? prev : current;
                            }).className);
                        });
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.estimatePose(image).then(({ pose, posenetOutput }) => {
                                    self.model.predict(posenetOutput).then(predictions => {
                                        resolve(predictions.reduce(function (prev, current) {
                                            return (prev.probability > current.probability) ? prev : current;
                                        }).className);
                                    });
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });
                default:
                    return '';
            }
        }
    }

    getPoseClassConfidence(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.model) {
            switch (args.SOURCE) {
                case 'webCam':
                    return new Promise(resolve => {
                        util.runtime.emit('TEACHABLE_MACHINE_GET_CLASS', this.model, this.modelType, predictions => {
                            let classResult = predictions.find(prediction => {
                                return prediction.className === args.CLASS
                            })
                            resolve(classResult ? classResult.probability.toFixed(2) : '');
                        });
                    });
                case 'stage':
                    return new Promise(resolve => {
                        this.runtime.renderer.requestSnapshot(data => {
                            let self = this;
                            let image = document.createElement('img');
                            image.onload = function () {
                                self.model.estimatePose(image).then(({ pose, posenetOutput }) => {
                                    self.model.predict(posenetOutput).then(predictions => {
                                        let classResult = predictions.find(prediction => {
                                            return prediction.className === args.CLASS
                                        });
                                        resolve(classResult ? classResult.probability.toFixed(2) : '');
                                    });
                                });
                            };
                            image.setAttribute("src", data);
                        });
                    });
                default:
                    return '';
            }
        }
    }
}

module.exports = teachableMachine;