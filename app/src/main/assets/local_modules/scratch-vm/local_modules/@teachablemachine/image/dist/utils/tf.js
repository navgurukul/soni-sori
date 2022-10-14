"use strict";
/**
 * @license
 * Copyright 2019 Google LLC. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================================
 */
Object.defineProperty(exports, "__esModule", { value: true });
var tf = require("@tensorflow/tfjs");
/**
 * Receives an image and normalizes it between -1 and 1.
 * Returns a batched image (1 - element batch) of shape [1, w, h, c]
 * @param rasterElement the element with pixels to convert to a Tensor
 */
function capture(rasterElement) {
    return tf.tidy(function () {
        var pixels = tf.browser.fromPixels(rasterElement);
        // crop the image so we're using the center square
        var cropped = cropTensor(pixels);
        // Expand the outer most dimension so we have a batch size of 1
        var batchedImage = cropped.expandDims(0);
        // Normalize the image between -1 and a1. The image comes in between 0-255
        // so we divide by 127 and subtract 1.
        return batchedImage.toFloat().div(tf.scalar(127)).sub(tf.scalar(1));
    });
}
exports.capture = capture;
function cropTensor(img) {
    var size = Math.min(img.shape[0], img.shape[1]);
    var centerHeight = img.shape[0] / 2;
    var beginHeight = centerHeight - (size / 2);
    var centerWidth = img.shape[1] / 2;
    var beginWidth = centerWidth - (size / 2);
    return img.slice([beginHeight, beginWidth, 0], [size, size, 3]);
}
exports.cropTensor = cropTensor;
//# sourceMappingURL=tf.js.map