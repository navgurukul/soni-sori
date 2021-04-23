package org.navgurukul.chat.features.media

import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Size
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import kotlinx.android.parcel.Parcelize
import org.matrix.android.sdk.internal.crypto.attachments.ElementToDecrypt
import org.navgurukul.chat.R
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.glide.GlideRequest
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

    // If true will load non mxc url, be careful to set it only for attachments sent by you
    val allowNonMxcUrls: Boolean
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
        val maxWidth: Int,
        val isLocalFile: Boolean = url.isLocalFile(),
        // If true will load non mxc url, be careful to set it only for images sent by you
        override val allowNonMxcUrls: Boolean = false
    ) : AttachmentData

    enum class Mode {
        FULL_SIZE,
        THUMBNAIL,
        STICKER
    }

    /**
     * For gallery
     */
    fun render(data: Data, imageView: ImageView, size: Int) {
        // a11y
        imageView.contentDescription = data.filename

        createGlideRequest(data, Mode.THUMBNAIL, imageView, Size(size, size))
            .placeholder(R.drawable.ic_image)
            .into(imageView)
    }

    fun render(data: Data, mode: Mode, imageView: ImageView) {
        val size = processSize(data, mode)
        imageView.layoutParams.width = size.width
        imageView.layoutParams.height = size.height
        // a11y
        imageView.contentDescription = data.filename

        createGlideRequest(data, mode, imageView, size)
            .dontAnimate()
            .transform(RoundedCorners(dimensionConverter.dpToPx(8)))
            .thumbnail(0.3f)
            .into(imageView)
    }

    fun render(data: Data, contextView: View, target: CustomViewTarget<*, Drawable>) {
        val req = if (data.elementToDecrypt != null) {
            // Encrypted image
            GlideApp
                .with(contextView)
                .load(data)
        } else {
            // Clear image
            val resolvedUrl = activeSessionHolder.getActiveSession().contentUrlResolver().resolveFullSize(data.url)
            GlideApp
                .with(contextView)
                .load(resolvedUrl)
        }

        req.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .fitCenter()
            .into(target)
    }

    fun renderFitTarget(data: Data, mode: Mode, imageView: ImageView, callback: ((Boolean) -> Unit)? = null) {
        val size = processSize(data, mode)

        // a11y
        imageView.contentDescription = data.filename

        createGlideRequest(data, mode, imageView, size)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?,
                                          model: Any?,
                                          target: Target<Drawable>?,
                                          isFirstResource: Boolean): Boolean {
                    callback?.invoke(false)
                    return false
                }

                override fun onResourceReady(resource: Drawable?,
                                             model: Any?,
                                             target: Target<Drawable>?,
                                             dataSource: DataSource?,
                                             isFirstResource: Boolean): Boolean {
                    callback?.invoke(true)
                    return false
                }
            })
            .fitCenter()
            .into(imageView)
    }

    /**
     * onlyRetrieveFromCache is true!
     */
    fun renderForSharedElementTransition(data: Data, imageView: ImageView, callback: ((Boolean) -> Unit)? = null) {
        // a11y
        imageView.contentDescription = data.filename

        val req = if (data.elementToDecrypt != null) {
            // Encrypted image
            GlideApp
                .with(imageView)
                .load(data)
        } else {
            // Clear image
            val resolvedUrl = activeSessionHolder.getActiveSession().contentUrlResolver().resolveFullSize(data.url)
            GlideApp
                .with(imageView)
                .load(resolvedUrl)
        }

        req.listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?,
                                      model: Any?,
                                      target: Target<Drawable>?,
                                      isFirstResource: Boolean): Boolean {
                callback?.invoke(false)
                return false
            }

            override fun onResourceReady(resource: Drawable?,
                                         model: Any?,
                                         target: Target<Drawable>?,
                                         dataSource: DataSource?,
                                         isFirstResource: Boolean): Boolean {
                callback?.invoke(true)
                return false
            }
        })
            .onlyRetrieveFromCache(true)
            .fitCenter()
            .into(imageView)
    }

    private fun createGlideRequest(data: Data, mode: Mode, imageView: ImageView, size: Size): GlideRequest<Drawable> {
        return if (data.elementToDecrypt != null) {
            // Encrypted image
            GlideApp
                .with(imageView)
                .load(data)
        } else {
            // Clear image
            val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()
            val resolvedUrl = when (mode) {
                Mode.FULL_SIZE,
                Mode.STICKER   -> contentUrlResolver.resolveFullSize(data.url)
                Mode.THUMBNAIL -> contentUrlResolver.resolveThumbnail(data.url, size.width, size.height, ContentUrlResolver.ThumbnailMethod.SCALE)
            }
            // Fallback to base url
                ?: data.url

            GlideApp
                .with(imageView)
                .load(resolvedUrl)
                .apply {
                    if (mode == Mode.THUMBNAIL) {
                        error(
                            GlideApp
                                .with(imageView)
                                .load(contentUrlResolver.resolveFullSize(data.url))
                        )
                    }
                }
        }
    }

    /*fun render(data: Data, imageView: BigImageView) {
        // a11y
        imageView.contentDescription = data.filename

        val (width, height) = processSize(data, Mode.THUMBNAIL)
        val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()
        val fullSize = contentUrlResolver.resolveFullSize(data.url)
        val thumbnail = contentUrlResolver.resolveThumbnail(data.url, width, height, ContentUrlResolver.ThumbnailMethod.SCALE)

        if (fullSize.isNullOrBlank() || thumbnail.isNullOrBlank()) {
            Timber.w("Invalid urls")
            return
        }

        imageView.setImageLoaderCallback(object : DefaultImageLoaderCallback {
            override fun onSuccess(image: File?) {
                imageView.ssiv?.orientation = ORIENTATION_USE_EXIF
            }
        })

        imageView.showImage(
            Uri.parse(thumbnail),
            Uri.parse(fullSize)
        )
    }*/

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
                Mode.STICKER   -> {
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