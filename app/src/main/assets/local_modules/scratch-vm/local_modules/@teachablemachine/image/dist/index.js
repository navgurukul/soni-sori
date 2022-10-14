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
var custom_mobilenet_1 = require("./custom-mobilenet");
exports.IMAGE_SIZE = custom_mobilenet_1.IMAGE_SIZE;
exports.CustomMobileNet = custom_mobilenet_1.CustomMobileNet;
exports.load = custom_mobilenet_1.load;
exports.loadFromFiles = custom_mobilenet_1.loadFromFiles;
exports.loadTruncatedMobileNet = custom_mobilenet_1.loadTruncatedMobileNet;
var teachable_mobilenet_1 = require("./teachable-mobilenet");
exports.TeachableMobileNet = teachable_mobilenet_1.TeachableMobileNet;
exports.createTeachable = teachable_mobilenet_1.createTeachable;
var webcam_1 = require("./utils/webcam");
exports.Webcam = webcam_1.Webcam;
var version_1 = require("./version");
exports.version = version_1.version;
//# sourceMappingURL=index.js.map