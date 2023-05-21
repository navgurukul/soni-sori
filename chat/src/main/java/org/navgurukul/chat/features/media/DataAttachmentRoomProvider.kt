package org.navgurukul.chat.features.media

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.events.model.isVideoMessage
import org.matrix.android.sdk.api.session.file.FileService
import org.matrix.android.sdk.api.session.room.Room
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.extensions.localDateTime
import org.navgurukul.chat.features.attachmentviewer.AttachmentInfo
import java.io.File

class DataAttachmentRoomProvider(
        private val attachments: List<AttachmentData>,
        private val room: Room?,
        private val initialIndex: Int,
        imageContentRenderer: ImageContentRenderer,
        private val dateFormatter: SaralDateFormatter,
        fileService: FileService) : BaseAttachmentProvider(imageContentRenderer, fileService) {

    override fun getItemCount(): Int = attachments.size

    override fun getAttachmentInfoAt(position: Int): AttachmentInfo {
        return attachments[position].let {
            when (it) {
                is ImageContentRenderer.Data -> {
                    if (it.mimeType == "image/gif") {
                        AttachmentInfo.AnimatedImage(
                                uid = it.eventId,
                                url = it.url ?: "",
                                data = it
                        )
                    } else {
                        AttachmentInfo.Image(
                                uid = it.eventId,
                                url = it.url ?: "",
                                data = it
                        )
                    }
                }
                is VideoContentRenderer.Data -> {
                    AttachmentInfo.Video(
                            uid = it.eventId,
                            url = it.url ?: "",
                            data = it,
                            thumbnail = AttachmentInfo.Image(
                                    uid = it.eventId,
                                    url = it.thumbnailMediaData.url ?: "",
                                    data = it.thumbnailMediaData
                            )
                    )
                }
                else                         -> throw IllegalArgumentException()
            }
        }
    }

    override fun overlayViewAtPosition(context: Context, position: Int): View? {
        super.overlayViewAtPosition(context, position)
        val item = attachments[position]
        val timeLineEvent = room?.getTimeLineEvent(item.eventId)
        if (timeLineEvent != null) {
            val dateString = timeLineEvent.root.localDateTime().let {
                "${dateFormatter.formatMessageDay(it)} at ${dateFormatter.formatMessageHour(it)} "
            }
            overlayView?.updateWith("${position + 1} of ${attachments.size}", "${timeLineEvent.senderInfo.displayName} $dateString")
            overlayView?.videoControlsGroup?.isVisible = timeLineEvent.root.isVideoMessage()
        } else {
            overlayView?.updateWith("", "")
        }
        return overlayView
    }

    override fun getFileForSharing(position: Int, callback: (File?) -> Unit) {
        val item = attachments[position]
        fileService.downloadFile(
                downloadMode = FileService.DownloadMode.FOR_EXTERNAL_SHARE,
                id = item.eventId,
                fileName = item.filename,
                mimeType = item.mimeType,
                url = item.url ?: "",
                elementToDecrypt = item.elementToDecrypt,
                callback = object : MatrixCallback<File> {
                    override fun onSuccess(data: File) {
                        callback(data)
                    }

                    override fun onFailure(failure: Throwable) {
                        callback(null)
                    }
                }
        )
    }
}
