package org.navgurukul.chat.core.fresco

import com.facebook.common.references.CloseableReference
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.decoder.ImageDecoder
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.image.QualityInfo
import com.facebook.imagepipeline.memory.PoolFactory
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.internal.crypto.attachments.ElementToDecrypt
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.core.extensions.callPrivateFunc
import timber.log.Timber
import java.io.InputStream

class SaralImageDecoder(private val elementToDecrypt: ElementToDecrypt?) : ImageDecoder {

    private val poolFactory: PoolFactory by inject(PoolFactory::class.java)

    val defaultDecoder: ImageDecoder by lazy {
        ImagePipelineFactory.getInstance().callPrivateFunc("getImageDecoder") as ImageDecoder
    }

    override fun decode(
        encodedImage: EncodedImage,
        length: Int,
        qualityInfo: QualityInfo,
        options: ImageDecodeOptions
    ): CloseableImage {

        var decryptedInputStream: InputStream? = null
        var clearEncodedImage: EncodedImage? = null
//        val transcodedImage: EncodedImage? = null
//        var transcodedRef: CloseableReference<PooledByteBuffer>? = null

        try {
            val inputStream = encodedImage.inputStream

            decryptedInputStream = elementToDecrypt?.let {
                Matrix.decryptStream(inputStream, elementToDecrypt)
            } ?: inputStream

            val bytes = poolFactory.pooledByteBufferFactory.newByteBuffer(decryptedInputStream)
            val bytesClosable = CloseableReference.of(bytes)
            clearEncodedImage = EncodedImage(bytesClosable)

//            val dimensions = BitmapUtil.decodeDimensions(clearEncodedImage.inputStream)
//            clearEncodedImage.width = dimensions?.first ?: -1
//            clearEncodedImage.height = dimensions?.second ?: -1
//            clearEncodedImage.rotationAngle = 0

//            val decodeOptions = options as? CryptoDecodeOptions ?: error("ImageOptions should be CryptoDecodeOptions")
//            val imageRequest = decodeOptions.imageRequest
//            val downsampleRatio = DownsampleUtil.determineSampleSize(imageRequest, clearEncodedImage)
//            val downsampleNumerator = calculateDownsampleNumerator(downsampleRatio)
//
//            if (downsampleNumerator == JpegTranscoder.SCALE_DENOMINATOR) {
            //No need to apply any transformation
            return defaultDecoder.decode(clearEncodedImage, bytes.size(), qualityInfo, options)
//            }

//            val outputStream = poolFactory.pooledByteBufferFactory.newOutputStream()

//            JpegTranscoder.transcodeJpeg(
//                    PooledByteBufferInputStream(bytes),
//                    outputStream,
//                    0, //Rotation is ignored
//                    downsampleNumerator,
//                    DEFAULT_JPEG_QUALITY)
//            val bb = outputStream.toByteBuffer()
//            transcodedRef = CloseableReference.of(bb)
//            transcodedImage = EncodedImage(transcodedRef)
//            transcodedImage.encodedCacheKey = encodedImage.encodedCacheKey
//            return defaultDecoder.decode(transcodedImage, bb.size(), qualityInfo, options)
        } catch (ex: Exception) {
            Timber.e("Something went wrong decoding the image")
            throw ex
        } finally {
            decryptedInputStream?.close()
            clearEncodedImage?.close()
//            transcodedImage?.close()
//            transcodedRef?.close()
        }
    }
}

/**
 * Dummy wrapper that hold a reference to the request that used this options, required
 * to perform jpeg resizing
 */
class SaralDecodeOptions(builder: ImageDecodeOptionsBuilder<*>) : ImageDecodeOptions(builder) {
    internal lateinit var imageRequest: ImageRequest
}

/**
 * Decoded images need the actual request to determine resize operations since
 * transcoding is not possible with encrypted images.
 */
fun ImageRequestBuilder.buildForSaralDecryption(elementToDecrypt: ElementToDecrypt?): ImageRequest {
    val cryptoDecodeOptions = SaralDecodeOptions(
        ImageDecodeOptions
            .newBuilder().setCustomImageDecoder(SaralImageDecoder(elementToDecrypt))
    )
    this.imageDecodeOptions = cryptoDecodeOptions
    return build().apply {
        cryptoDecodeOptions.imageRequest = this
    }
}