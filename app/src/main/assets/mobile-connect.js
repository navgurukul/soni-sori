function loadProjectUsingBase64(filename, fileData){
    const loadProject = new CustomEvent('LOAD_PROJECT_USING_BASE64');
    loadProject.base64 = fileData;
    loadProject.filename = filename;
    window.dispatchEvent(loadProject);
}

var globalBase64String = "";