import {assert} from '../util/assert';

import {PNGFile} from '../coders/png-file';

class FakeZipFile {
    constructor (file) {
        this.file = file;
    }

    async (outputType) {
        assert(outputType === 'uint8array', 'SB1FakeZipFile only supports uint8array');

        return Promise.resolve(this.file.bytes);
    }
}

export {FakeZipFile};

class FakeZip {
    constructor (files) {
        this.files = files;
    }

    file (file) {
        if (file in this.files) {
            return new FakeZipFile(this.files[file]);
        }
    }
}

export {FakeZip};

const toSb2ImageExtension = imageMedia => {
    if (imageMedia.extension === 'uncompressed') {
        return 'png';
    }
    return 'jpg';
};

const toSb2ImageMedia = imageMedia => {
    if (imageMedia.extension === 'uncompressed') {
        return new Uint8Array(PNGFile.encode(
            imageMedia.width,
            imageMedia.height,
            imageMedia.decoded
        ));
    }
    return imageMedia.decoded;
};

const toSb2SoundMedia = soundMedia => soundMedia.wavEncodedData;

const toSb2FakeZipApi = ({images, sounds}) => {
    const files = {};

    let index = 0;
    for (const image of images) {
        files[`${index++}.${toSb2ImageExtension(image)}`] = {
            bytes: toSb2ImageMedia(image)
        };
    }

    index = 0;
    for (const sound of sounds) {
        files[`${index++}.wav`] = {
            bytes: toSb2SoundMedia(sound)
        };
    }

    return new FakeZip(files);
};

export {toSb2FakeZipApi};
