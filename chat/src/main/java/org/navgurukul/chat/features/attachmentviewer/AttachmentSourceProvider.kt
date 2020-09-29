package org.navgurukul.chat.features.attachmentviewer

import android.content.Context
import android.view.View

sealed class AttachmentInfo(open val uid: String) {
    data class Image(override val uid: String, val url: String, val data: Any?) : AttachmentInfo(uid)
    data class AnimatedImage(override val uid: String, val url: String, val data: Any?) : AttachmentInfo(uid)
    data class Video(override val uid: String, val url: String, val data: Any, val thumbnail: Image?) : AttachmentInfo(uid)
//    data class Audio(override val uid: String, val url: String, val data: Any) : AttachmentInfo(uid)
//    data class File(override val uid: String, val url: String, val data: Any) : AttachmentInfo(uid)
}

interface AttachmentSourceProvider {

    fun getItemCount(): Int

    fun getAttachmentInfoAt(position: Int): AttachmentInfo

    fun loadImage(target: ImageLoaderTarget, info: AttachmentInfo.Image)

    fun loadImage(target: ImageLoaderTarget, info: AttachmentInfo.AnimatedImage)

    fun loadVideo(target: VideoLoaderTarget, info: AttachmentInfo.Video)

    fun overlayViewAtPosition(context: Context, position: Int): View?

    fun clear(id: String)
}
