function loadProjectUsingBase64(filename, fileData) {
    const loadProject = new CustomEvent("LOAD_PROJECT_USING_BASE64");
    loadProject.base64 = fileData;
    loadProject.filename = filename;
    window.dispatchEvent(loadProject);
}

function loadProjectUsingS3Url(url) {
    const loadProject = new CustomEvent("LOAD_PROJECT_USING_S3URL");
    loadProject.url = url;
    window.dispatchEvent(loadProject);
}

function loadProjectUsingFileName(filename) {
    const loadProject = new CustomEvent("LOAD_PROJECT_USING_FILENAME");
    loadProject.filename = filename;
    window.dispatchEvent(loadProject);
}

function postFile(token, filename) {
    console.log("Scratch--> POSTFILE 1");
    const postFile = new CustomEvent("POST_FILE");
    postFile.token = token;
    postFile.filename = filename;
    window.dispatchEvent(postFile);
}

function convertBase64ToFile(base64String, filename, token) {
    console.log("Scratch--> CONVERTBASE64TOFILE 1");
    const convertBase64ToFile = new CustomEvent("CONVERT_BASE64_TO_FILE");
    convertBase64ToFile.base64 = base64String;
    convertBase64ToFile.filename = filename;
    convertBase64ToFile.token = token;
    window.dispatchEvent(convertBase64ToFile);
}

function deleteProject(projectId) {
    const deleteProject = new CustomEvent("DELETE_PROJECT");
    const projectId = Scratch.getProjectIdString();
    deleteProject.projectId = projectId;
    window.dispatchEvent(deleteProject);
}

function downloadBlob(filename, blob) {
    //convert unit8array to blob
    var blob = new Blob([blob]);
    const downloadLink = document.createElement("a");
    document.body.appendChild(downloadLink);

    // Use special ms version if available to get it working on Edge.
    if (navigator.msSaveOrOpenBlob) {
        navigator.msSaveOrOpenBlob(blob, filename);
        return;
    }

    if ("download" in HTMLAnchorElement.prototype) {
        const url = URL.createObjectURL(blob);

        var reader = new window.FileReader();
        var base64data;

        reader.readAsDataURL(blob);
        reader.onloadend = function () {
            base64data = reader.result;
            const base64String = base64data.split(",")[1];
            // assign to base64 string to global variable
            console.log("calling here");
            const globalBase64 = new CustomEvent(
                "ASSIGN_BASE64DATA_TO_VARIABLE"
            );
            globalBase64.base64 = base64String;
            convertBase64ToFile(base64String, "randomTestFile");
            window.dispatchEvent(globalBase64);

            // downloadLink.href = base64data; //url
            // downloadLink.download = filename;
            // downloadLink.type = blob.type;
            // console.log(downloadLink);
            // downloadLink.click();
            // window.setTimeout(() => {
            //     document.body.removeChild(downloadLink);
            //     window.URL.revokeObjectURL(url);
            // }, 1000);
        };
    } else {
        // iOS 12 Safari, open a new page and set href to data-uri
        let popup = window.open("", "_blank");
        const reader = new FileReader();
        reader.onloadend = function () {
            popup.location.href = reader.result;
            popup = null;
        };
        reader.readAsDataURL(blob);
    }
}

function editSpriteParamters(handlerName, value) {
    var editSpriteParamtersEvent = new CustomEvent("EDIT_SPRITE_PARAMETERS");
    editSpriteParamtersEvent.data = {
        handlerName,
        value,
    };
    window.dispatchEvent(editSpriteParamtersEvent);
}

function closeLoaderScreen() {
    console.log("getting called");
    const closeLoader = new CustomEvent("CLOSE_LOADER_SCREEN");
    window.dispatchEvent(closeLoader);
}

function openLoaderScreen() {
    const openLoader = new CustomEvent("OPEN_LOADER_SCREEN");
    window.dispatchEvent(openLoader);
}

function requestMicrophoneAccess() {
    const requestMicrophoneAccess = new CustomEvent(
        "REQUEST_MICROPHONE_ACCESS"
    );
    window.dispatchEvent(requestMicrophoneAccess);
}

var globalBase64String = "";
var globalBase64Loading = "";
