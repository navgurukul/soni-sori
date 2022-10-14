module.exports = function() {
  return new Worker(__webpack_public_path__ + "extension-worker.js");
};