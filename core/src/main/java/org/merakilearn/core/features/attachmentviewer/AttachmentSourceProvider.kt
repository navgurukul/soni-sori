package org.merakilearn.core.features.attachmentviewer

import android.content.Context
import android.view.View

sealed class AttachmentInfo(open val uid: String) {
    data class Image(override val uid: String, val url: String, val data: Any?) : org.merakilearn.core.features.attachmentviewer.AttachmentInfo(uid)
    data class AnimatedImage(override val uid: String, val url: String, val data: Any?) : org.merakilearn.core.features.attachmentviewer.AttachmentInfo(uid)
    data class Video(override val uid: String, val url: String, val data: Any, val thumbnail: org.merakilearn.core.features.attachmentviewer.AttachmentInfo.Image?) : org.merakilearn.core.features.attachmentviewer.AttachmentInfo(uid)
//    data class Audio(override val uid: String, val url: String, val data: Any) : AttachmentInfo(uid)
//    data class File(override val uid: String, val url: String, val data: Any) : AttachmentInfo(uid)
}

interface AttachmentSourceProvider {

    fun getItemCount(): Int

    fun getAttachmentInfoAt(position: Int): org.merakilearn.core.features.attachmentviewer.AttachmentInfo

    fun loadImage(target: org.merakilearn.core.features.attachmentviewer.ImageLoaderTarget, info: org.merakilearn.core.features.attachmentviewer.AttachmentInfo.Image)

    fun loadImage(target: _root_ide_package_.org.merakilearn.core.features.attachmentviewer.ImageLoaderTarget, info: _root_ide_package_.org.merakilearn.core.features.attachmentviewer.AttachmentInfo.AnimatedImage)

    fun loadVideo(target: _root_ide_package_.org.merakilearn.core.features.attachmentviewer.VideoLoaderTarget, info: _root_ide_package_.org.merakilearn.core.features.attachmentviewer.AttachmentInfo.Video)

    fun overlayViewAtPosition(context: Context, position: Int): View?

    fun clear(id: String)
}
