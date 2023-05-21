package org.navgurukul.chat.features.media

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.file.FileService
import org.matrix.android.sdk.internal.crypto.attachments.ElementToDecrypt
import kotlinx.android.parcel.Parcelize
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.error.ErrorFormatter
import timber.log.Timber
import java.io.File

class VideoContentRenderer (private val activeSessionHolder: ActiveSessionHolder,
                            private val errorFormatter: ErrorFormatter
) {

    @Parcelize
    data class Data(
        override val eventId: String,
        override val filename: String,
        override val mimeType: String?,
        override val url: String?,
        override val elementToDecrypt: ElementToDecrypt?,
        // If true will load non mxc url, be careful to set it only for video sent by you
        override val allowNonMxcUrls: Boolean = false,
        val thumbnailMediaData: ImageContentRenderer.Data
    ) : AttachmentData

    fun render(data: Data,
               thumbnailView: ImageView,
               loadingView: ProgressBar,
               videoView: VideoView,
               errorView: TextView
    ) {
        val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()

        if (data.elementToDecrypt != null) {
            Timber.v("Decrypt video")
            videoView.isVisible = false

            if (data.url == null) {
                loadingView.isVisible = false
                errorView.isVisible = true
                errorView.setText(R.string.unknown_error)
            } else {
                thumbnailView.isVisible = true
                loadingView.isVisible = true

                activeSessionHolder.getActiveSession().fileService()
                        .downloadFile(
                                downloadMode = FileService.DownloadMode.FOR_INTERNAL_USE,
                                id = data.eventId,
                                fileName = data.filename,
                                mimeType = data.mimeType,
                                url = data.url,
                                elementToDecrypt = data.elementToDecrypt,
                                callback = object : MatrixCallback<File> {
                                    override fun onSuccess(data: File) {
                                        thumbnailView.isVisible = false
                                        loadingView.isVisible = false
                                        videoView.isVisible = true

                                        videoView.setVideoPath(data.path)
                                        videoView.start()
                                    }

                                    override fun onFailure(failure: Throwable) {
                                        loadingView.isVisible = false
                                        errorView.isVisible = true
                                        errorView.text = errorFormatter.toHumanReadable(failure)
                                    }
                                })
            }
        } else {
            val resolvedUrl = contentUrlResolver.resolveFullSize(data.url)

            if (resolvedUrl == null) {
                thumbnailView.isVisible = false
                loadingView.isVisible = false
                errorView.isVisible = true
                errorView.setText(R.string.unknown_error)
            } else {
                // Temporary code, some remote videos are not played by videoview setVideoUri
                // So for now we download them then play
                thumbnailView.isVisible = true
                loadingView.isVisible = true

                activeSessionHolder.getActiveSession().fileService()
                        .downloadFile(
                                downloadMode = FileService.DownloadMode.FOR_INTERNAL_USE,
                                id = data.eventId,
                                fileName = data.filename,
                                mimeType = data.mimeType,
                                url = data.url,
                                elementToDecrypt = null,
                                callback = object : MatrixCallback<File> {
                                    override fun onSuccess(data: File) {
                                        thumbnailView.isVisible = false
                                        loadingView.isVisible = false
                                        videoView.isVisible = true

                                        videoView.setVideoPath(data.path)
                                        videoView.start()
                                    }

                                    override fun onFailure(failure: Throwable) {
                                        loadingView.isVisible = false
                                        errorView.isVisible = true
                                        errorView.text = errorFormatter.toHumanReadable(failure)
                                    }
                                })
            }
        }
    }
}