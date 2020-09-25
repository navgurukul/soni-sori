package org.navgurukul.chat.features.media

import android.graphics.drawable.Animatable
import android.os.Parcelable
import android.util.Size
import androidx.core.net.toUri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import im.vector.matrix.android.api.session.content.ContentUrlResolver
import im.vector.matrix.android.internal.crypto.attachments.ElementToDecrypt
import kotlinx.android.parcel.Parcelize
import org.navgurukul.chat.R
import org.navgurukul.chat.core.fresco.buildForSaralDecryption
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.utils.DimensionConverter
import org.navgurukul.chat.core.utils.isLocalFile
import kotlin.math.min


interface AttachmentData : Parcelable {
    val eventId: String
    val filename: String
    val mimeType: String?
    val url: String?
    val elementToDecrypt: ElementToDecrypt?
}

class ImageContentRenderer(
    private val activeSessionHolder: ActiveSessionHolder,
    private val dimensionConverter: DimensionConverter
) {

    @Parcelize
    data class Data(
        override val eventId: String,
        override val filename: String,
        override val mimeType: String?,
        override val url: String?,
        override val elementToDecrypt: ElementToDecrypt?,
        val height: Int?,
        val maxHeight: Int,
        val width: Int?,
        val maxWidth: Int
    ) : AttachmentData {

        fun isLocalFile() = url.isLocalFile()
    }

    enum class Mode {
        FULL_SIZE,
        THUMBNAIL,
        STICKER
    }

    /**
     * For gallery
     */
    fun render(data: Data, imageView: SimpleDraweeView, size: Int) {
        // a11y
        imageView.contentDescription = data.filename

        imageView.hierarchy.setPlaceholderImage(R.drawable.ic_image)

        createFrescoRequest(data, imageView, Size(size, size), Mode.THUMBNAIL)
    }

    fun render(data: Data, mode: Mode, imageView: SimpleDraweeView) {
        val size = processSize(data, mode)
        imageView.layoutParams.width = size.width
        imageView.layoutParams.height = size.height
        // a11y
        imageView.contentDescription = data.filename

        createFrescoRequest(data, imageView, size, mode)
    }

//    fun render(data: Data, contextView: View, target: CustomViewTarget<*, Drawable>) {
//        val req = if (data.elementToDecrypt != null) {
//            // Encrypted image
//            GlideApp
//                .with(contextView)
//                .load(data)
//        } else {
//            // Clear image
//            val resolvedUrl = activeSessionHolder.getActiveSession().contentUrlResolver()
//                .resolveFullSize(data.url)
//            GlideApp
//                .with(contextView)
//                .load(resolvedUrl)
//        }
//
//        req.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//            .fitCenter()
//            .into(target)
//    }

    fun renderFitTarget(
        data: Data,
        mode: Mode,
        imageView: SimpleDraweeView,
        callback: ((Boolean) -> Unit)? = null
    ) {
        val size = processSize(data, mode)

        // a11y
        imageView.contentDescription = data.filename

        createFrescoRequest(data, imageView, size, mode, callback = callback)
    }

    /**
     * onlyRetrieveFromCache is true!
     */
    fun renderForSharedElementTransition(
        data: Data,
        imageView: SimpleDraweeView,
        callback: ((Boolean) -> Unit)? = null
    ) {
        // a11y
        imageView.contentDescription = data.filename

        createFrescoRequest(data, imageView, fromCache = true, callback = callback)
    }

    private fun createFrescoRequest(
        data: Data,
        imageView: SimpleDraweeView,
        size: Size? = null,
        mode: Mode = Mode.FULL_SIZE,
        fromCache: Boolean = false,
        callback: ((Boolean) -> Unit)? = null
    ) {
        val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()

        val imageRequest = if (data.elementToDecrypt != null) {
            val resolvedUrl = contentUrlResolver.resolveFullSize(data.url)
            // Encrypted image
            ImageRequestBuilder
                .newBuilderWithSource(resolvedUrl?.toUri())
                .setLowestPermittedRequestLevel(if (fromCache) ImageRequest.RequestLevel.DISK_CACHE else ImageRequest.RequestLevel.FULL_FETCH)
                .buildForSaralDecryption(data.elementToDecrypt)
        } else {
            // Clear image

            val resolvedUrl = when (mode) {
                Mode.FULL_SIZE,
                Mode.STICKER -> contentUrlResolver.resolveFullSize(data.url)
                Mode.THUMBNAIL -> contentUrlResolver.resolveThumbnail(
                    data.url,
                    size?.width ?: 0,
                    size?.height ?: 0,
                    ContentUrlResolver.ThumbnailMethod.SCALE
                )
            }
            // Fallback to base url
                ?: data.url

            ImageRequestBuilder.newBuilderWithSource(resolvedUrl?.toUri())
                .setLowestPermittedRequestLevel(if (fromCache) ImageRequest.RequestLevel.DISK_CACHE else ImageRequest.RequestLevel.FULL_FETCH)
                .build()
        }

        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .setControllerListener(object : BaseControllerListener<ImageInfo>() {
                override fun onFailure(id: String?, throwable: Throwable?) {
                    super.onFailure(id, throwable)
                    if (data.elementToDecrypt == null) {
                        imageView.setImageURI(data.url)
                    }
                    callback?.invoke(false)
                }

                override fun onFinalImageSet(
                    id: String?,
                    imageInfo: ImageInfo?,
                    animatable: Animatable?
                ) {
                    super.onFinalImageSet(id, imageInfo, animatable)
                    callback?.invoke(true)
                }
            })
            .build()


        imageView.controller = controller
    }


//    fun render(data: Data, imageView: BigImageView) {
//        // a11y
//        imageView.contentDescription = data.filename
//
//        val (width, height) = processSize(data, Mode.THUMBNAIL)
//        val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()
//        val fullSize = contentUrlResolver.resolveFullSize(data.url)
//        val thumbnail = contentUrlResolver.resolveThumbnail(
//            data.url,
//            width,
//            height,
//            ContentUrlResolver.ThumbnailMethod.SCALE
//        )
//
//        if (fullSize.isNullOrBlank() || thumbnail.isNullOrBlank()) {
//            Timber.w("Invalid urls")
//            return
//        }
//
//        imageView.setImageLoaderCallback(object : DefaultImageLoaderCallback {
//            override fun onSuccess(image: File?) {
//                imageView.ssiv?.orientation = ORIENTATION_USE_EXIF
//            }
//        })
//
//        imageView.showImage(
//            Uri.parse(thumbnail),
//            Uri.parse(fullSize)
//        )
//    }

    private fun processSize(data: Data, mode: Mode): Size {
        val maxImageWidth = data.maxWidth
        val maxImageHeight = data.maxHeight
        val width = data.width ?: maxImageWidth
        val height = data.height ?: maxImageHeight
        var finalWidth = -1
        var finalHeight = -1

        // if the image size is known
        // compute the expected height
        if (width > 0 && height > 0) {
            when (mode) {
                Mode.FULL_SIZE -> {
                    finalHeight = height
                    finalWidth = width
                }
                Mode.THUMBNAIL -> {
                    finalHeight = min(maxImageWidth * height / width, maxImageHeight)
                    finalWidth = finalHeight * width / height
                }
                Mode.STICKER -> {
                    // limit on width
                    val maxWidthDp = min(dimensionConverter.dpToPx(120), maxImageWidth / 2)
                    finalWidth = min(dimensionConverter.dpToPx(width), maxWidthDp)
                    finalHeight = finalWidth * height / width
                }
            }
        }
        // ensure that some values are properly initialized
        if (finalHeight < 0) {
            finalHeight = maxImageHeight
        }
        if (finalWidth < 0) {
            finalWidth = maxImageWidth
        }
        return Size(finalWidth, finalHeight)
    }
}