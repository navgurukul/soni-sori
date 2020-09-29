package org.navgurukul.chat.features.attachmentviewer

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.isVisible
import java.io.File

interface VideoLoaderTarget {
    fun contextView(): ImageView

    fun onThumbnailResourceLoading(uid: String, placeholder: Drawable?)

    fun onThumbnailLoadFailed(uid: String, errorDrawable: Drawable?)

    fun onThumbnailResourceCleared(uid: String, placeholder: Drawable?)

    fun onThumbnailResourceReady(uid: String, resource: Drawable)

    fun onVideoFileLoading(uid: String)
    fun onVideoFileLoadFailed(uid: String)
    fun onVideoFileReady(uid: String, file: File)
    fun onVideoURLReady(uid: String, path: String)
}

internal class DefaultVideoLoaderTarget(val holder: VideoViewHolder, private val contextView: ImageView) :
    VideoLoaderTarget {
    override fun contextView(): ImageView = contextView

    override fun onThumbnailResourceLoading(uid: String, placeholder: Drawable?) {
    }

    override fun onThumbnailLoadFailed(uid: String, errorDrawable: Drawable?) {
    }

    override fun onThumbnailResourceCleared(uid: String, placeholder: Drawable?) {
        if (holder.boundResourceUid != uid) return
        holder.thumbnailImage.setImageDrawable(placeholder)
    }

    override fun onThumbnailResourceReady(uid: String, resource: Drawable) {
        if (holder.boundResourceUid != uid) return
        holder.thumbnailImage.setImageDrawable(resource)
    }

    override fun onVideoFileLoading(uid: String) {
        if (holder.boundResourceUid != uid) return
        holder.thumbnailImage.isVisible = true
        holder.loaderProgressBar.isVisible = true
        holder.videoView.isVisible = false
    }

    override fun onVideoFileLoadFailed(uid: String) {
        if (holder.boundResourceUid != uid) return
        holder.videoFileLoadError()
    }

    override fun onVideoFileReady(uid: String, file: File) {
        if (holder.boundResourceUid != uid) return
        arrangeForVideoReady()
        holder.videoReady(file)
    }

    override fun onVideoURLReady(uid: String, path: String) {
        if (holder.boundResourceUid != uid) return
        arrangeForVideoReady()
        holder.videoReady(path)
    }

    private fun arrangeForVideoReady() {
        holder.thumbnailImage.isVisible = false
        holder.loaderProgressBar.isVisible = false
        holder.videoView.isVisible = true
    }
}
