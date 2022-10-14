const ArgumentType = require('../../../extension-support/argument-type');
const BlockType = require('../../../extension-support/block-type');
const formatMessage = require('format-message');
const posenetModel = require('@tensorflow-models/posenet');
const Runtime = require('../../../engine/runtime');
const handPoseModel = require('@tensorflow-models/handpose');
const Video = require('../../../io/video');
const StageLayering = require('../../../engine/stage-layering');
const { cos } = require('@tensorflow/tfjs-core');

const { ModelLoadingType } = require('../../../util/board-config');

let isStage = false;
let stageWidth = 0;
let stageHeight = 0;

let poseDetected = [];
let handDetected = [];

let drawOnStage = false;

let MakerAttributes = [];

for (let i = 0; i < 20; i++) {
    MakerAttributes[i] = {
        color4f: [Math.random(), Math.random(), Math.random(), 0.7],
    };
}

let netModel2;
let netModel2Hand;

const blockIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNlNWIyYWE7c3Ryb2tlOiM1OTRhNDI7c3Ryb2tlLXdpZHRoOjAuMTZweDt9LmNscy0xLC5jbHMtMntzdHJva2UtbWl0ZXJsaW1pdDoxMDt9LmNscy0ye2ZpbGw6bm9uZTtzdHJva2U6I2ZmZjIwMDtzdHJva2Utd2lkdGg6MC4zMXB4O30uY2xzLTN7ZmlsbDojZWQyMDI0O308L3N0eWxlPjwvZGVmcz48dGl0bGU+aGFuZCBwb3NlPC90aXRsZT48cGF0aCBjbGFzcz0iY2xzLTEiIGQ9Ik04LjQyLDIxLjI4QzcuMywxOC41LDYsMTcuNDYsNC43OCwxNC4zYTE3LjM4LDE3LjM4LDAsMCwxLS42NS0yLjEzLDEuNjIsMS42MiwwLDAsMSwuMjEtMS44OEEwLjg2LDAuODYsMCwwLDEsNSwxMC4yM2MwLjkxLDAuMjIsMS4zNCwxLjQ4LDEuNSwxLjg0YTI1LjgzLDI1LjgzLDAsMCwwLDQsNi4wNiwxLjE2LDEuMTYsMCwwLDAsMSwuMzQsMS4wOCwxLjA4LDAsMCwwLC4xOS0xYy0wLjI1LTItMS4zLTcuMDYtMS4zMS0xMC40MmExNi4zNSwxNi4zNSwwLDAsMSwuMS0xLjc5LDEuMzksMS4zOSwwLDAsMSwxLjIxLS44N0ExLjQ2LDEuNDYsMCwwLDEsMTIuOTIsNmEzMy44NywzMy44NywwLDAsMCwuNzgsNS40N2MwLjQ2LDIsMS4zMSw0Ljg0LDEuMzEsNC44NGEwLjU4LDAuNTgsMCwwLDAsLjM5LjM0LDAuNDYsMC40NiwwLDAsMCwuMjQtMC4zNEMxNi4xMSwxNC43NiwxNS45MSw4LDE1LjkxLDhBMjYuOTMsMjYuOTMsMCwwLDEsMTYsNC45MWE0LjM5LDQuMzksMCwwLDEsLjMzLTIuMDlsMCwwYTEuNzIsMS43MiwwLDAsMSwxLjI0LTEsMS43NywxLjc3LDAsMCwxLDEuNSwxLjY1bDAsMTEuOTJhMC44NCwwLjg0LDAsMCwwLC41MiwxLDAuNzEsMC43MSwwLDAsMCwuNS0wLjM4bDIuMTQsNy40M2E2My4yMyw2My4yMywwLDAsMCwxLDguMzUsMzYuMDYsMzYuMDYsMCwwLDAsMSw0LjEyLDMzLjQ2LDMzLjQ2LDAsMCwwLDEuNDQsMy43NmwtMTIuMzEtLjEzYy0wLjIzLTEuMjUtLjM4LTIuMjYtMC40OC0yLjk1YTEzLjI3LDEzLjI3LDAsMCwwLS42Ni0zLjE3Yy0wLjIyLS41Ny0wLjQ0LTEtMC41NS0xLjJhNDkuNzcsNDkuNzcsMCwwLDEtMi42Mi03LjgyQzguNDEsMjEuNTksOC43NCwyMi4xLDguNDIsMjEuMjhaIi8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNMjAuMDksMTZsNS0xMC42MWExLjcyLDEuNzIsMCwwLDEsMS41NS0xYzAuNTYsMC4xNywxLDEuMjcuNTMsMi42MkwyMy42MywxNy43NGE3LjY3LDcuNjcsMCwwLDAsMCw0LjQxLDEuNiwxLjYsMCwwLDAsLjc3Ljk0YzAuMzMsMC4xNS43NiwwLjE1LDEuODMtLjMzYTE3Ljc4LDE3Ljc4LDAsMCwwLDIuMjEtMS4yN0E4Ljg0LDguODQsMCwwLDEsMzEsMjAuMjhhMy40NCwzLjQ0LDAsMCwxLDIuNjQuMTMsMi45NCwyLjk0LDAsMCwxLDEuMjYsMS43LDE3LDE3LDAsMCwwLTQuNDgsMi44MWMtMC4yNy4yNC0xLjIzLDEuMjMtMy4xNCwzLjE5YTM4LjQsMzguNCwwLDAsMS0yLjc2LDIuNzEsMTIuMjQsMTIuMjQsMCwwLDEtMy4xNCwyYy0wLjMuMTMtLjU1LDAuMjMtMC43MywwLjI5Ii8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNMTEuMjEsMzAuOTRhNC43Miw0LjcyLDAsMCwwLDIuNTIsMi43NCw0LjI5LDQuMjksMCwwLDAsMS45Mi4zIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIxMS42OSA1Ljk3IDExLjc1IDguODkgMTEuOTQgMTEuOTkgMTMuMzkgMTkuODQgMTcuMTcgMzAuMzIgOS45MSAyMS4yNyA3LjU3IDE2LjI5IDUuODUgMTMuOTcgNS4xMyAxMS41OSIvPjxwb2x5bGluZSBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMjYuMjEgNS41NiAyNC44NSA4Ljc1IDIzLjU4IDEyLjYgMjAuNzIgMTkuMzIgMTcuMTcgMzAuMzIgMTcuMjkgMTguNzEgMTcuMjQgMTAuMDcgMTcuNDMgNi4xNyAxNy41MiAzLjIxIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIzMi44MyAyMS4yNSAyOC40MiAyMy41OSAyNS4xOCAyNi40NiAyMSAyOC40OCAxNy4xNyAzMC4zMiIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMzIuNzMiIGN5PSIyMS40IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyOC4zOSIgY3k9IjIzLjcxIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyNS41MSIgY3k9IjI2LjEyIiByPSIwLjU5IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMTAuMzggMTkuNDMpIHJvdGF0ZSgtMzUuMTIpIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxNy4zMiIgY3k9IjMwLjg0IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyNi4zMSIgY3k9IjUuNSIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMjQuOTIiIGN5PSI4LjU1IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyMi45MiIgY3k9IjEzLjgyIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyMC44OCIgY3k9IjE5LjEiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjE3LjI5IiBjeT0iMTkuMTMiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjEzLjU0IiBjeT0iMjAuMTkiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjkuOTkiIGN5PSIyMS4zOCIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTcuNDciIGN5PSIzLjM2IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxMS42MyIgY3k9IjUuNzciIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjUuMiIgY3k9IjExLjY3IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxNy40IiBjeT0iNi41MyIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTcuMyIgY3k9IjEwLjg0IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxMS43NSIgY3k9IjguOTQiIHI9IjAuNTkiIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUzIDE3LjEzKSByb3RhdGUoLTcwLjk4KSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTEuOTIiIGN5PSIxMS45NCIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iNS45NiIgY3k9IjE0LjExIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSI3LjYxIiBjeT0iMTYuNDIiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjIxLjM4IiBjeT0iMjguNDMiIHI9IjAuNTkiLz48L3N2Zz4=';

const menuIconURI = 'data:image/svg+xml;base64,PHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCI+PGRlZnM+PHN0eWxlPi5jbHMtMXtmaWxsOiNlNWIyYWE7c3Ryb2tlOiM1OTRhNDI7c3Ryb2tlLXdpZHRoOjAuMTZweDt9LmNscy0xLC5jbHMtMntzdHJva2UtbWl0ZXJsaW1pdDoxMDt9LmNscy0ye2ZpbGw6bm9uZTtzdHJva2U6I2ZmZjIwMDtzdHJva2Utd2lkdGg6MC4zMXB4O30uY2xzLTN7ZmlsbDojZWQyMDI0O308L3N0eWxlPjwvZGVmcz48dGl0bGU+aGFuZCBwb3NlPC90aXRsZT48cGF0aCBjbGFzcz0iY2xzLTEiIGQ9Ik04LjQyLDIxLjI4QzcuMywxOC41LDYsMTcuNDYsNC43OCwxNC4zYTE3LjM4LDE3LjM4LDAsMCwxLS42NS0yLjEzLDEuNjIsMS42MiwwLDAsMSwuMjEtMS44OEEwLjg2LDAuODYsMCwwLDEsNSwxMC4yM2MwLjkxLDAuMjIsMS4zNCwxLjQ4LDEuNSwxLjg0YTI1LjgzLDI1LjgzLDAsMCwwLDQsNi4wNiwxLjE2LDEuMTYsMCwwLDAsMSwuMzQsMS4wOCwxLjA4LDAsMCwwLC4xOS0xYy0wLjI1LTItMS4zLTcuMDYtMS4zMS0xMC40MmExNi4zNSwxNi4zNSwwLDAsMSwuMS0xLjc5LDEuMzksMS4zOSwwLDAsMSwxLjIxLS44N0ExLjQ2LDEuNDYsMCwwLDEsMTIuOTIsNmEzMy44NywzMy44NywwLDAsMCwuNzgsNS40N2MwLjQ2LDIsMS4zMSw0Ljg0LDEuMzEsNC44NGEwLjU4LDAuNTgsMCwwLDAsLjM5LjM0LDAuNDYsMC40NiwwLDAsMCwuMjQtMC4zNEMxNi4xMSwxNC43NiwxNS45MSw4LDE1LjkxLDhBMjYuOTMsMjYuOTMsMCwwLDEsMTYsNC45MWE0LjM5LDQuMzksMCwwLDEsLjMzLTIuMDlsMCwwYTEuNzIsMS43MiwwLDAsMSwxLjI0LTEsMS43NywxLjc3LDAsMCwxLDEuNSwxLjY1bDAsMTEuOTJhMC44NCwwLjg0LDAsMCwwLC41MiwxLDAuNzEsMC43MSwwLDAsMCwuNS0wLjM4bDIuMTQsNy40M2E2My4yMyw2My4yMywwLDAsMCwxLDguMzUsMzYuMDYsMzYuMDYsMCwwLDAsMSw0LjEyLDMzLjQ2LDMzLjQ2LDAsMCwwLDEuNDQsMy43NmwtMTIuMzEtLjEzYy0wLjIzLTEuMjUtLjM4LTIuMjYtMC40OC0yLjk1YTEzLjI3LDEzLjI3LDAsMCwwLS42Ni0zLjE3Yy0wLjIyLS41Ny0wLjQ0LTEtMC41NS0xLjJhNDkuNzcsNDkuNzcsMCwwLDEtMi42Mi03LjgyQzguNDEsMjEuNTksOC43NCwyMi4xLDguNDIsMjEuMjhaIi8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNMjAuMDksMTZsNS0xMC42MWExLjcyLDEuNzIsMCwwLDEsMS41NS0xYzAuNTYsMC4xNywxLDEuMjcuNTMsMi42MkwyMy42MywxNy43NGE3LjY3LDcuNjcsMCwwLDAsMCw0LjQxLDEuNiwxLjYsMCwwLDAsLjc3Ljk0YzAuMzMsMC4xNS43NiwwLjE1LDEuODMtLjMzYTE3Ljc4LDE3Ljc4LDAsMCwwLDIuMjEtMS4yN0E4Ljg0LDguODQsMCwwLDEsMzEsMjAuMjhhMy40NCwzLjQ0LDAsMCwxLDIuNjQuMTMsMi45NCwyLjk0LDAsMCwxLDEuMjYsMS43LDE3LDE3LDAsMCwwLTQuNDgsMi44MWMtMC4yNy4yNC0xLjIzLDEuMjMtMy4xNCwzLjE5YTM4LjQsMzguNCwwLDAsMS0yLjc2LDIuNzEsMTIuMjQsMTIuMjQsMCwwLDEtMy4xNCwyYy0wLjMuMTMtLjU1LDAuMjMtMC43MywwLjI5Ii8+PHBhdGggY2xhc3M9ImNscy0xIiBkPSJNMTEuMjEsMzAuOTRhNC43Miw0LjcyLDAsMCwwLDIuNTIsMi43NCw0LjI5LDQuMjksMCwwLDAsMS45Mi4zIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIxMS42OSA1Ljk3IDExLjc1IDguODkgMTEuOTQgMTEuOTkgMTMuMzkgMTkuODQgMTcuMTcgMzAuMzIgOS45MSAyMS4yNyA3LjU3IDE2LjI5IDUuODUgMTMuOTcgNS4xMyAxMS41OSIvPjxwb2x5bGluZSBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMjYuMjEgNS41NiAyNC44NSA4Ljc1IDIzLjU4IDEyLjYgMjAuNzIgMTkuMzIgMTcuMTcgMzAuMzIgMTcuMjkgMTguNzEgMTcuMjQgMTAuMDcgMTcuNDMgNi4xNyAxNy41MiAzLjIxIi8+PHBvbHlsaW5lIGNsYXNzPSJjbHMtMiIgcG9pbnRzPSIzMi44MyAyMS4yNSAyOC40MiAyMy41OSAyNS4xOCAyNi40NiAyMSAyOC40OCAxNy4xNyAzMC4zMiIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMzIuNzMiIGN5PSIyMS40IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyOC4zOSIgY3k9IjIzLjcxIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyNS41MSIgY3k9IjI2LjEyIiByPSIwLjU5IiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMTAuMzggMTkuNDMpIHJvdGF0ZSgtMzUuMTIpIi8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxNy4zMiIgY3k9IjMwLjg0IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyNi4zMSIgY3k9IjUuNSIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMjQuOTIiIGN5PSI4LjU1IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyMi45MiIgY3k9IjEzLjgyIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIyMC44OCIgY3k9IjE5LjEiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjE3LjI5IiBjeT0iMTkuMTMiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjEzLjU0IiBjeT0iMjAuMTkiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjkuOTkiIGN5PSIyMS4zOCIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTcuNDciIGN5PSIzLjM2IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxMS42MyIgY3k9IjUuNzciIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjUuMiIgY3k9IjExLjY3IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxNy40IiBjeT0iNi41MyIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTcuMyIgY3k9IjEwLjg0IiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSIxMS43NSIgY3k9IjguOTQiIHI9IjAuNTkiIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUzIDE3LjEzKSByb3RhdGUoLTcwLjk4KSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iMTEuOTIiIGN5PSIxMS45NCIgcj0iMC41OSIvPjxjaXJjbGUgY2xhc3M9ImNscy0zIiBjeD0iNS45NiIgY3k9IjE0LjExIiByPSIwLjU5Ii8+PGNpcmNsZSBjbGFzcz0iY2xzLTMiIGN4PSI3LjYxIiBjeT0iMTYuNDIiIHI9IjAuNTkiLz48Y2lyY2xlIGNsYXNzPSJjbHMtMyIgY3g9IjIxLjM4IiBjeT0iMjguNDMiIHI9IjAuNTkiLz48L3N2Zz4=';

class posenet {
    constructor(runtime) {
        this.runtime = runtime;
        let self = this;
        this.modelLoaded = false;

        // To identify the MODEL_FILES_LOCATED from multiple listeners in this class
        this.modelLoadingId = "_" + ModelLoadingType.poseDetection;
        this.runtime.emitModelLoading(ModelLoadingType.poseDetection, this.modelLoadingId);

        this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
            console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
            if (isAbort) {
                return self.runtime.emit('MODEL_LOADING_FINISHED', false);
            }
            var netModel = new Promise(resolve => {
                let modelObj = posenetModel.load({
                    architecture: 'MobileNetV1',
                    outputStride: 16,
                    inputResolution: { width: 480, height: 360 },
                    multiplier: 0.75,
                    modelUrl: `${modelPath}/modelStride16.json`
                }).then((net) => {
                    netModel2 = net;
                    // console.log(net)
                    handPoseModel.load().then(function (netHand) {
                        netModel2Hand = netHand;
                        runtime.renderer.requestSnapshot(data => {
                            let image = document.createElement('img');
                            image.onload = function () {
                                isStage = true;
                                stageWidth = image.width;
                                stageHeight = image.height;
                                netModel2.estimateMultiplePoses(image, { flipHorizontal: false }).then(function (poses) {
                                    poseDetected = poses;
                                    netModel2Hand.estimateHands(image, { flipHorizontal: false }).then(function (posesHand) {
                                        handDetected = posesHand;
                                        self.modelLoaded = true;
                                        runtime.emit('MODEL_LOADING_FINISHED', true);
                                        resolve('Done');
                                        return 'Done';
                                    })
                                })
                            };
                            image.setAttribute("src", data);
                        });
                    })
                        .catch(err => {
                            runtime.emit('MODEL_LOADING_FINISHED', true);
                        })
                })
                    .catch(err => {
                        runtime.emit('MODEL_LOADING_FINISHED', true);
                    })
            });
        });

        this.globalVideoState = 'off';
        this.runtime.ioDevices.video.disableVideo();
        this.extensionName = "Human Body Detection";

        this._canvas = document.querySelector('canvas');
        this._penSkinId = this.runtime.renderer.createPenSkin();
        const penDrawableId = this.runtime.renderer.createDrawable(StageLayering.IMAGE_LAYER);
        this.runtime.renderer.updateDrawableProperties(penDrawableId, { skinId: this._penSkinId });
    }

    static get DIMENSIONS() {
        return [480, 360];
    }

    getInfo() {
        return {
            id: 'posenet',
            name: 'Human Body',
            blockIconURI: blockIconURI,
            menuIconURI: menuIconURI,
            colour: '#c64342',
            colourSecondary: '#b63535',
            colourTertiary: '#a42b2b',
            blocks: [
                {
                    opcode: 'toggleStageVideoFeed',
                    text: formatMessage({
                        id: 'posenet.toggleStageVideoFeed',
                        default: 'turn [VIDEO_STATE] video on stage with [TRANSPARENCY] % transparency',
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
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'drawBoundingBox',
                    text: formatMessage({
                        id: 'posenet.drawBoundingBox',
                        default: '[OPTION] detections',
                        description: 'Draw bounding box'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        OPTION: {
                            type: ArgumentType.NUMBER,
                            menu: 'drawBox',
                            defaultValue: '1'
                        }
                    }
                },
                {
                    message: 'Pose Detection'
                },
                {
                    opcode: 'analyseImage',
                    text: formatMessage({
                        id: 'posenet.analyseImage',
                        default: 'analyse image for human pose from [FEED]',
                        description: 'Analyse Image'
                    }),
                    blockType: BlockType.COMMAND,
                    arguments: {
                        FEED: {
                            type: ArgumentType.NUMBER,
                            menu: 'feed',
                            defaultValue: '1'
                        }
                    }
                },
                "---",
                {
                    opcode: 'getPeopleCount',
                    text: formatMessage({
                        id: 'posenet.getPeopleCount',
                        default: 'get # of people',
                        description: 'Get # of people'
                    }),
                    blockType: BlockType.REPORTER,
                },
                {
                    opcode: 'getX',
                    text: formatMessage({
                        id: 'posenet.getX',
                        default: 'X position of [PART] of person [PERSON]',
                        description: 'X Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        PERSON: {
                            type: ArgumentType.STRING,
                            menu: 'personNumbers',
                            defaultValue: '1'
                        },
                        PART: {
                            type: ArgumentType.STRING,
                            menu: 'parts',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'getY',
                    text: formatMessage({
                        id: 'posenet.getY',
                        default: 'Y position of [PART] of person [PERSON]',
                        description: 'Y Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        PERSON: {
                            type: ArgumentType.STRING,
                            menu: 'personNumbers',
                            defaultValue: '1'
                        },
                        PART: {
                            type: ArgumentType.STRING,
                            menu: 'parts',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    opcode: 'isDetected',
                    text: formatMessage({
                        id: 'posenet.isDetected',
                        default: 'is [PART] of person [PERSON] detected?',
                        description: 'Is part detected?'
                    }),
                    blockType: BlockType.BOOLEAN,
                    arguments: {
                        PERSON: {
                            type: ArgumentType.STRING,
                            menu: 'personNumbers',
                            defaultValue: '1'
                        },
                        PART: {
                            type: ArgumentType.STRING,
                            menu: 'parts',
                            defaultValue: '0'
                        }
                    }
                },
                {
                    message: 'Hand Detection'
                },
                {
                    opcode: 'analyseImageHand',
                    text: formatMessage({
                        id: 'posenet.analyseImageHand',
                        default: 'analyse image for hand from camera',
                        description: 'Analyse Image'
                    }),
                    blockType: BlockType.COMMAND,
                },
                "---",
                {
                    opcode: 'isDetectedHand',
                    text: formatMessage({
                        id: 'posenet.isDetectedHand',
                        default: 'is hand detected',
                        description: 'Analyse Image'
                    }),
                    blockType: BlockType.BOOLEAN,
                },
                {
                    opcode: 'getPositionHand',
                    text: formatMessage({
                        id: 'posenet.getPositionHand',
                        default: '[POSITION] position of [PART] of [FINGER]',
                        description: 'X Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'position',
                            defaultValue: '1'
                        },
                        FINGER: {
                            type: ArgumentType.NUMBER,
                            menu: 'finger',
                            defaultValue: '1'
                        },
                        PART: {
                            type: ArgumentType.NUMBER,
                            menu: 'partsHand',
                            defaultValue: '4'
                        }
                    }
                },
                {
                    opcode: 'getPositionNumberHand',
                    text: formatMessage({
                        id: 'posenet.getPositionNumberHand',
                        default: '[POSITION] position of [PART]',
                        description: 'X Position of Parts'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'position',
                            defaultValue: '1'
                        },
                        PART: {
                            type: ArgumentType.NUMBER,
                            menu: 'fingerNumber',
                            defaultValue: '4'
                        }
                    }
                },
                {
                    opcode: 'getHand',
                    text: formatMessage({
                        id: 'posenet.getHand',
                        default: '[POSITION] of hand',
                        description: 'Position of Hand'
                    }),
                    blockType: BlockType.REPORTER,
                    arguments: {
                        POSITION: {
                            type: ArgumentType.NUMBER,
                            menu: 'handPosition',
                            defaultValue: '1'
                        }
                    }
                },
            ],
            menus: {
                videoState: [
                    { text: 'off', value: 'off' },
                    { text: 'on', value: 'on' },
                    { text: 'on flipped', value: 'onFlipped' }
                ],
                personNumbers: {
                    acceptReporters: true,
                    items: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
                },
                parts: {
                    acceptReporters: true,
                    items: [
                        { text: 'nose', value: '0' },
                        { text: 'left eye', value: '1' },
                        { text: 'right eye', value: '2' },
                        { text: 'left ear', value: '3' },
                        { text: 'right ear', value: '4' },
                        { text: 'left shoulder', value: '5' },
                        { text: 'right shoulder', value: '6' },
                        { text: 'left elbow', value: '7' },
                        { text: 'right elbow', value: '8' },
                        { text: 'left wrist', value: '9' },
                        { text: 'right wrist', value: '10' },
                        { text: 'left hip', value: '11' },
                        { text: 'right hip', value: '12' },
                        { text: 'left knee', value: '13' },
                        { text: 'right knee', value: '14' },
                        { text: 'left ankle', value: '15' },
                        { text: 'right ankle', value: '16' }
                    ],
                },
                feed: {
                    items: [
                        { text: 'camera', value: '1' },
                        { text: 'stage', value: '2' },
                    ]
                },
                partsHand: {
                    acceptReporters: true,
                    items: [
                        { text: 'top', value: '4' },
                        { text: 'middle 1', value: '3' },
                        { text: 'middle 2', value: '2' },
                        { text: 'bottom', value: '1' },
                    ]
                },
                handPosition: {
                    items: [
                        { text: 'x position', value: '1' },
                        { text: 'y position', value: '2' },
                        { text: 'width', value: '3' },
                        { text: 'height', value: '4' },
                    ]
                },
                position: {
                    items: [
                        { text: 'x', value: '1' },
                        { text: 'y', value: '2' },
                    ]
                },
                finger: {
                    acceptReporters: true,
                    items: [
                        { text: 'thumb', value: '1' },
                        { text: 'index finger', value: '2' },
                        { text: 'middle finger', value: '3' },
                        { text: 'ring finger', value: '4' },
                        { text: 'pinky finger', value: '5' },
                    ],
                },
                fingerNumber: {
                    acceptReporters: true,
                    items: [
                        { text: 'top of thumb', value: '4' },
                        { text: 'middle 1 of thumb', value: '3' },
                        { text: 'middle 2 of thumb', value: '2' },
                        { text: 'bottom of thumb', value: '1' },
                        { text: 'top of index finger', value: '8' },
                        { text: 'middle 1 of index finger', value: '7' },
                        { text: 'middle 2 of index finger', value: '6' },
                        { text: 'bottom of index finger', value: '5' },
                        { text: 'top of middle finger', value: '12' },
                        { text: 'middle 1 of middle finger', value: '11' },
                        { text: 'middle 2 of middle finger', value: '10' },
                        { text: 'bottom of middle finger', value: '9' },
                        { text: 'top of ring finger', value: '16' },
                        { text: 'middle 1 of ring finger', value: '15' },
                        { text: 'middle 2 of ring finger', value: '14' },
                        { text: 'bottom of ring finger', value: '13' },
                        { text: 'top of pinky finger', value: '20' },
                        { text: 'middle 1 of pinky finger', value: '19' },
                        { text: 'middle 2 of pinky finger', value: '18' },
                        { text: 'bottom of pinky finger', value: '17' },
                    ],
                },
                drawBox: [
                    { text: 'show', value: '1' },
                    { text: 'hide', value: '2' }
                ],
            }
        };
    }

    toggleStageVideoFeed(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
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

    analyseImage(args, util) {
        let self2 = this;
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.modelLoaded) {
            if (args.FEED === '1') {
                isStage = false;
                const translatePromise = new Promise(resolve => {
                    const frame = this.runtime.ioDevices.video.getFrame({
                        format: Video.FORMAT_IMAGE_DATA,
                        dimensions: posenet.DIMENSIONS
                    });
                    netModel2.estimateMultiplePoses(frame, { flipHorizontal: false, maxDetections: 5, scoreThreshold: 0.25 }).then(function (poses) {
                        poseDetected = poses;
                        console.log(poseDetected);
                        if (poseDetected) {
                            let index = 0;
                            for (let i = 0; i < poseDetected.length; i++) {
                                if (poseDetected[i] > 0.15) {
                                    index = i;
                                }
                            }
                            if (poseDetected.length !== (index + 1)) {
                                poseDetected.splice(index + 1, poseDetected.length - index - 1);
                            }
                        }

                        console.log(poseDetected);

                        if (drawOnStage) {
                            self2._clearMark();
                            for (let i = 0; i < poseDetected.length; i++) {
                                self2._drawMark(poseDetected[i].keypoints, posenet.DIMENSIONS[0], posenet.DIMENSIONS[1], i);
                            }
                        }
                        resolve('Done');
                        return 'Done';
                    })
                        .catch(err => {
                            poseDetected = [];
                            resolve('Camera not ready!');
                            return 'Camera not ready!';
                        });
                });
                return translatePromise;
            }
            else if (args.FEED === '2') {
                const translatePromise = new Promise(resolve => {
                    this.runtime.renderer.requestSnapshot(data => {
                        let image = document.createElement('img');
                        image.onload = function () {
                            isStage = true;
                            stageWidth = image.width;
                            stageHeight = image.height;
                            netModel2.estimateMultiplePoses(image, { flipHorizontal: false }).then(function (poses) {
                                poseDetected = poses;
                                if (poseDetected) {
                                    let index = 0;
                                    for (let i = 0; i < poseDetected.length; i++) {
                                        if (poseDetected[i] > 0.15) {
                                            index = i;
                                        }
                                    }
                                    if (poseDetected.length !== (index + 1)) {
                                        poseDetected.splice(index + 1, poseDetected.length - index - 1);
                                    }
                                }

                                console.log(poseDetected);

                                if (drawOnStage) {
                                    self2._clearMark();
                                    for (let i = 0; i < poseDetected.length; i++) {
                                        self2._drawMark(poseDetected[i].keypoints, stageWidth, stageHeight, i);
                                    }
                                }
                                resolve('Done');
                                return 'Done';
                            })
                        };
                        image.setAttribute("src", data);
                    });
                });
                return translatePromise;
            }
        }
        else {
            let self = this;
            this.modelLoadingId = "_" + ModelLoadingType.poseDetection + "_1";
            this.runtime.emitModelLoading(ModelLoadingType.poseDetection, this.modelLoadingId);
            return new Promise((resolve, reject) => {
                this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
                    console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
                    if (isAbort) {
                        reject();
                        return self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    }
                    posenetModel.load({
                        architecture: 'MobileNetV1',
                        outputStride: 16,
                        inputResolution: { width: 480, height: 360 },
                        multiplier: 0.75,
                        modelUrl: `${modelPath}/modelStride16.json`
                    }).then(function (net) {
                        handPoseModel.load().then(function (netHand) {
                            netModel2Hand = netHand;
                            self.runtime.emit('MODEL_LOADING_FINISHED', true);
                            netModel2 = net;
                            self.modelLoaded = true;

                            if (args.FEED === '1') {
                                isStage = false;

                                const frame = this.runtime.ioDevices.video.getFrame({
                                    format: Video.FORMAT_IMAGE_DATA,
                                    dimensions: posenet.DIMENSIONS
                                });
                                netModel2.estimateMultiplePoses(frame, { flipHorizontal: false }).then(function (poses) {
                                    poseDetected = poses;
                                    if (poseDetected) {
                                        let index = 0;
                                        for (let i = 0; i < poseDetected.length; i++) {
                                            if (poseDetected[i] > 0.15) {
                                                index = i;
                                            }
                                        }
                                        if (poseDetected.length !== (index + 1)) {
                                            poseDetected.splice(index + 1, poseDetected.length - index - 1);
                                        }
                                    }

                                    console.log(poseDetected);

                                    if (drawOnStage) {
                                        self2._clearMark();
                                        for (let i = 0; i < poseDetected.length; i++) {
                                            self2._drawMark(poseDetected[i].keypoints, posenet.DIMENSIONS[0], posenet.DIMENSIONS[1], i);
                                        }
                                    }
                                    resolve('Done');
                                    return 'Done';
                                }).catch(err => {
                                    poseDetected = [];
                                    resolve('Camera not ready!');
                                    return 'Camera not ready!';
                                });
                            }
                            else if (args.FEED === '2') {
                                self.runtime.renderer.requestSnapshot(data => {
                                    let image = document.createElement('img');
                                    image.onload = function () {
                                        isStage = true;
                                        stageWidth = image.width;
                                        stageHeight = image.height;
                                        netModel2.estimateMultiplePoses(image, { flipHorizontal: false }).then(function (poses) {
                                            poseDetected = poses;
                                            if (poseDetected) {
                                                let index = 0;
                                                for (let i = 0; i < poseDetected.length; i++) {
                                                    if (poseDetected[i] > 0.15) {
                                                        index = i;
                                                    }
                                                }
                                                if (poseDetected.length !== (index + 1)) {
                                                    poseDetected.splice(index + 1, poseDetected.length - index - 1);
                                                }
                                            }

                                            console.log(poseDetected);

                                            if (drawOnStage) {
                                                self2._clearMark();
                                                for (let i = 0; i < poseDetected.length; i++) {
                                                    self2._drawMark(poseDetected[i].keypoints, stageWidth, stageHeight, i);
                                                }
                                            }
                                            resolve('Done');
                                            return 'Done';
                                        })
                                    };
                                    image.setAttribute("src", data);
                                });
                            }
                        }).catch(err => {
                            self.runtime.emit('MODEL_LOADING_FINISHED', false);
                        })
                    }).catch(err => {
                        self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    });
                });
            });
        }
    }

    _drawMark(keypoints, width, height, num) {
        let widthScale = 480 / width;
        let heightScale = 360 / height;

        for (let i = 0; i < 17; i++) {
            let MakerAttributes1 = {
                color4f: MakerAttributes[num].color4f,
                diameter: 10
            }
            if (keypoints[i].score > 0.5) {
                this.runtime.renderer.penPoint(this._penSkinId, MakerAttributes1,
                    keypoints[i].position.x * widthScale - width / 2 * widthScale,
                    height / 2 * heightScale - keypoints[i].position.y * heightScale);
            }
        }

        ad = posenetModel.getAdjacentKeyPoints(keypoints, 0.5);

        console.log(ad);
        console.log(MakerAttributes);

        for (let i = 0; i < ad.length; i++) {
            let MakerAttributes1 = {
                color4f: MakerAttributes[num].color4f,
                diameter: 3
            }
            this.runtime.renderer.penLine(this._penSkinId, MakerAttributes1,
                ad[i][0].position.x * widthScale - width / 2 * widthScale,
                height / 2 * heightScale - ad[i][0].position.y * heightScale,
                ad[i][1].position.x * widthScale - width / 2 * widthScale,
                height / 2 * heightScale - ad[i][1].position.y * heightScale);
        }

    }

    _clearMark() {
        this.runtime.renderer.penClear(this._penSkinId);
    }



    getPeopleCount(args, util) {
        return poseDetected.length;
    }

    getX(args, util) {
        if (poseDetected[parseInt(args.PERSON, 10) - 1] && poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)]) {
            if (poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].score > 0.5) {
                if (!isStage) {
                    let XPos = poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].position.x - 240;
                    return XPos.toFixed(1);
                }
                else {
                    let XPos = -1 * (240 - poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].position.x * 480 / stageWidth);
                    return XPos.toFixed(1);
                }
            }
            return "NULL";
        } else {
            return "NULL";
        }
    }

    getY(args, util) {
        if (poseDetected[parseInt(args.PERSON, 10) - 1] && poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)]) {
            if (poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].score > 0.5) {
                if (!isStage) {
                    let YPos = 180 - poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].position.y;
                    return YPos.toFixed(1);
                }
                else {
                    let YPos = 180 - poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].position.y * 360 / stageHeight;
                    return YPos.toFixed(1);
                }
            }
            return "NULL";
        } else {
            return "NULL";
        }
    }

    isDetected(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (poseDetected[parseInt(args.PERSON, 10) - 1] && poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)]) {
            if (poseDetected[parseInt(args.PERSON, 10) - 1].keypoints[parseInt(args.PART, 10)].score > 0.5) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    analyseImageHand(args, util) {
        let self2 = this;
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (this.modelLoaded) {
            const translatePromise = new Promise(resolve => {
                const frame = this.runtime.ioDevices.video.getFrame({
                    format: Video.FORMAT_IMAGE_DATA,
                    dimensions: posenet.DIMENSIONS
                });
                netModel2Hand.estimateHands(frame, { flipHorizontal: false }).then(function (posesHand) {
                    handDetected = posesHand;
                    console.log(handDetected);
                    if (drawOnStage) {
                        self2._clearMarkHand();
                        for (let i = 0; i < handDetected.length; i++) {
                            self2._drawMarkHand(handDetected[i], posenet.DIMENSIONS[0], posenet.DIMENSIONS[1], i);
                        }
                    }
                    resolve('Done');
                    return 'Done';
                })
                    .catch(err => {
                        handDetected = [];
                        resolve('Camera not ready!');
                        return 'Camera not ready!';
                    });
            });
            return translatePromise;
        }
        else {
            let self = this;
            this.modelLoadingId = "_" + ModelLoadingType.poseDetection + "_2";
            this.runtime.emitModelLoading(ModelLoadingType.poseDetection, this.modelLoadingId);
            return new Promise((resolve, reject) => {
                this.runtime.on(Runtime.MODEL_FILES_LOCATED + this.modelLoadingId, (modelPath, isAbort) => {
                    if (isAbort) {
                        reject();
                        return self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    }
                    console.log("MODEL_FILES_LOCATED" + this.modelLoadingId);
                    posenetModel.load({
                        architecture: 'MobileNetV1',
                        outputStride: 16,
                        inputResolution: { width: 480, height: 360 },
                        multiplier: 0.75,
                        modelUrl: `${modelPath}/modelStride16.json`
                    }).then((net) => {
                        handPoseModel.load().then((netHand) => {
                            netModel2Hand = netHand;
                            self.runtime.emit('MODEL_LOADING_FINISHED', true);
                            netModel2 = net;
                            self.modelLoaded = true;

                            const translatePromise = new Promise(resolve => {

                                const frame = this.runtime.ioDevices.video.getFrame({
                                    format: Video.FORMAT_IMAGE_DATA,
                                    dimensions: posenet.DIMENSIONS
                                });
                                netModel2Hand.estimateHands(frame, { flipHorizontal: false }).then(function (posesHand) {
                                    handDetected = posesHand;
                                    if (drawOnStage) {
                                        self2._clearMarkHand();
                                        for (let i = 0; i < handDetected.length; i++) {
                                            self2._drawMarkHand(handDetected[i], posenet.DIMENSIONS[0], posenet.DIMENSIONS[1], i);
                                        }
                                    }
                                    resolve('Done');
                                    return 'Done';
                                }).catch(err => {
                                    handDetected = [];
                                    resolve('Camera not ready!');
                                    return 'Camera not ready!';
                                });
                            });
                            return translatePromise;
                        }).catch(err => {
                            self.runtime.emit('MODEL_LOADING_FINISHED', false);
                        })
                    }).catch(err => {
                        self.runtime.emit('MODEL_LOADING_FINISHED', false);
                    });
                });
            });
        }
    }

    _drawMarkHand(handDetected, width, height, num) {
        let widthScale = 480 / width;
        let heightScale = 360 / height;

        for (let i = 0; i < 21; i++) {
            let MakerAttributes1 = {
                color4f: MakerAttributes[num].color4f,
                diameter: 10
            }
            this.runtime.renderer.penPoint(this._penSkinId, MakerAttributes1,
                handDetected.landmarks[i][0] * widthScale - width / 2 * widthScale,
                height / 2 * heightScale - handDetected.landmarks[i][1] * heightScale);
        }

        for (let i = 0; i < 5; i++) {
            let MakerAttributes1 = {
                color4f: MakerAttributes[num].color4f,
                diameter: 3
            }
            for (let j = 0; j < 3; j++) {
                this.runtime.renderer.penLine(this._penSkinId, MakerAttributes1,
                    handDetected.landmarks[i * 4 + j + 1][0] * widthScale - width / 2 * widthScale,
                    height / 2 * heightScale - handDetected.landmarks[i * 4 + j + 1][1] * heightScale,
                    handDetected.landmarks[i * 4 + j + 2][0] * widthScale - width / 2 * widthScale,
                    height / 2 * heightScale - handDetected.landmarks[i * 4 + j + 2][1] * heightScale);
            }
        }

        for (let i = 0; i < 5; i++) {
            let MakerAttributes1 = {
                color4f: MakerAttributes[num].color4f,
                diameter: 3
            }
            this.runtime.renderer.penLine(this._penSkinId, MakerAttributes1,
                handDetected.landmarks[0][0] * widthScale - width / 2 * widthScale,
                height / 2 * heightScale - handDetected.landmarks[0][1] * heightScale,
                handDetected.landmarks[i * 4 + 1][0] * widthScale - width / 2 * widthScale,
                height / 2 * heightScale - handDetected.landmarks[i * 4 + 1][1] * heightScale);
        }

    }

    _clearMarkHand() {
        this.runtime.renderer.penClear(this._penSkinId);
    }

    isDetectedHand(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (handDetected.length === 0) {
            return false;
        }
        else {
            return true;
        }
    }

    getPositionHand(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        console.log(handDetected);
        if (handDetected.length === 1) {
            if (args.FINGER === '1') {
                if (args.POSITION === '1') {
                    let XPos = handDetected[0].annotations.thumb[parseInt(args.PART, 10) - 1][0];
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === '2') {
                    let YPos = handDetected[0].annotations.thumb[parseInt(args.PART, 10) - 1][1];
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.FINGER === '2') {
                if (args.POSITION === '1') {
                    let XPos = handDetected[0].annotations.indexFinger[parseInt(args.PART, 10) - 1][0];
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === '2') {
                    let YPos = handDetected[0].annotations.indexFinger[parseInt(args.PART, 10) - 1][1];
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.FINGER === '3') {
                if (args.POSITION === '1') {
                    let XPos = handDetected[0].annotations.middleFinger[parseInt(args.PART, 10) - 1][0];
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === '2') {
                    let YPos = handDetected[0].annotations.middleFinger[parseInt(args.PART, 10) - 1][1];
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.FINGER === '4') {
                if (args.POSITION === '1') {
                    let XPos = handDetected[0].annotations.ringFinger[parseInt(args.PART, 10) - 1][0];
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === '2') {
                    let YPos = handDetected[0].annotations.ringFinger[parseInt(args.PART, 10) - 1][1];
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            else if (args.FINGER === '5') {
                if (args.POSITION === '1') {
                    let XPos = handDetected[0].annotations.pinky[parseInt(args.PART, 10) - 1][0];
                    XPos = XPos - 240;
                    return XPos.toFixed(1);
                }
                else if (args.POSITION === '2') {
                    let YPos = handDetected[0].annotations.pinky[parseInt(args.PART, 10) - 1][1];
                    YPos = 180 - YPos;
                    return YPos.toFixed(1);
                }
            }
            return "NULL";
        } else {
            return "NULL";
        }
    }

    getPositionNumberHand(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (handDetected.length === 1) {
            if (args.POSITION === '1') {
                let XPos = handDetected[0].landmarks[parseInt(args.PART, 10)][0];
                XPos = XPos - 240;
                return XPos.toFixed(1);
            }
            else if (args.POSITION === '2') {
                let YPos = handDetected[0].landmarks[parseInt(args.PART, 10)][1];
                YPos = 180 - YPos;
                return YPos.toFixed(1);
            }
        } else {
            return "NULL";
        }
    }

    getHand(args, util) {
        if (!this.runtime.checkSessionExists(this.extensionName)) return;
        if (handDetected.length === 1) {
            if (args.POSITION === '1') {
                let XPos = (handDetected[0].boundingBox.topLeft[0] + handDetected[0].boundingBox.bottomRight[0]) / 2;
                XPos = XPos - 240;
                return XPos.toFixed(1);
            }
            else if (args.POSITION === '2') {
                let YPos = (handDetected[0].boundingBox.topLeft[1] + handDetected[0].boundingBox.bottomRight[1]) / 2;
                YPos = 180 - YPos;
                return YPos.toFixed(1);
            }
            else if (args.POSITION === '3') {
                let YPos = handDetected[0].boundingBox.bottomRight[0] - handDetected[0].boundingBox.topLeft[0];
                return YPos.toFixed(1);
            }
            else if (args.POSITION === '4') {
                let YPos = handDetected[0].boundingBox.bottomRight[1] - handDetected[0].boundingBox.topLeft[1];
                return YPos.toFixed(1);
            }
        } else {
            return "NULL";
        }
    }

    drawBoundingBox(args, util) {
        let self2 = this;
        if (args.OPTION === "1") {
            drawOnStage = true;
            self2._clearMark();
            for (let i = 0; i < poseDetected.length; i++) {
                self2._drawMark(poseDetected[i].keypoints, stageWidth, stageHeight, i);
            }

            for (let i = 0; i < handDetected.length; i++) {
                self2._drawMarkHand(handDetected[i], posenet.DIMENSIONS[0], posenet.DIMENSIONS[1], i);
            }
        }
        else {
            drawOnStage = false;
            this._clearMark();
        }
    }

}

module.exports = posenet;