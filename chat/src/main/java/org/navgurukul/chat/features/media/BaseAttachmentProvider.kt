package org.navgurukul.chat.features.media

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.file.FileService
import org.navgurukul.chat.features.attachmentviewer.AttachmentInfo
import org.navgurukul.chat.features.attachmentviewer.AttachmentSourceProvider
import org.navgurukul.chat.features.attachmentviewer.ImageLoaderTarget
import org.navgurukul.chat.features.attachmentviewer.VideoLoaderTarget
import java.io.File

abstract class BaseAttachmentProvider(
    val imageContentRenderer: ImageContentRenderer,
    val fileService: FileService
) :
    AttachmentSourceProvider {

    interface InteractionListener {
        fun onDismissTapped()
        fun onShareTapped()
        fun onPlayPause(play: Boolean)
        fun videoSeekTo(percent: Int)
    }

    var interactionListener: InteractionListener? = null

    protected var overlayView: AttachmentOverlayView? = null

    override fun overlayViewAtPosition(context: Context, position: Int): View? {
        if (position == -1) return null
        if (overlayView == null) {
            overlayView = AttachmentOverlayView(context)
            overlayView?.onBack = {
                interactionListener?.onDismissTapped()
            }
            overlayView?.onShareCallback = {
                interactionListener?.onShareTapped()
            }
            overlayView?.onPlayPause = { play ->
                interactionListener?.onPlayPause(play)
            }
            overlayView?.videoSeekTo = { percent ->
                interactionListener?.videoSeekTo(percent)
            }
        }
        return overlayView
    }

    override fun loadImage(target: ImageLoaderTarget, info: AttachmentInfo.Image) {
        (info.data as? ImageContentRenderer.Data)?.let {
            imageContentRenderer.render(
                it,
                target.contextView(),
                object : CustomViewTarget<ImageView, Drawable>(target.contextView()) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        target.onLoadFailed(info.uid, errorDrawable)
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                        target.onResourceCleared(info.uid, placeholder)
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        target.onResourceReady(info.uid, resource)
                    }
                })
        }
    }

    override fun loadImage(target: ImageLoaderTarget, info: AttachmentInfo.AnimatedImage) {
        (info.data as? ImageContentRenderer.Data)?.let {
            imageContentRenderer.render(
                it,
                target.contextView(),
                object : CustomViewTarget<ImageView, Drawable>(target.contextView()) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        target.onLoadFailed(info.uid, errorDrawable)
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                        target.onResourceCleared(info.uid, placeholder)
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        target.onResourceReady(info.uid, resource)
                    }
                })
        }
    }

    override fun loadVideo(target: VideoLoaderTarget, info: AttachmentInfo.Video) {
        val data = info.data as? VideoContentRenderer.Data ?: return
//        videoContentRenderer.render(data,
//                holder.thumbnailImage,
//                holder.loaderProgressBar,
//                holder.videoView,
//                holder.errorTextView)
        imageContentRenderer.render(
            data.thumbnailMediaData,
            target.contextView(),
            object : CustomViewTarget<ImageView, Drawable>(target.contextView()) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    target.onThumbnailLoadFailed(info.uid, errorDrawable)
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    target.onThumbnailResourceCleared(info.uid, placeholder)
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    target.onThumbnailResourceReady(info.uid, resource)
                }
            })

        target.onVideoFileLoading(info.uid)
        fileService.downloadFile(
            downloadMode = FileService.DownloadMode.FOR_INTERNAL_USE,
            id = data.eventId,
            mimeType = data.mimeType,
            elementToDecrypt = data.elementToDecrypt,
            fileName = data.filename,
            url = data.url,
            callback = object : MatrixCallback<File> {
                override fun onSuccess(data: File) {
                    target.onVideoFileReady(info.uid, data)
                }

                override fun onFailure(failure: Throwable) {
                    target.onVideoFileLoadFailed(info.uid)
                }
            }
        )
    }

    override fun clear(id: String) {
        // TODO("Not yet implemented")
    }

    abstract fun getFileForSharing(position: Int, callback: ((File?) -> Unit))
}