package org.navgurukul.chat.features.share

import android.os.Parcelable
import im.vector.matrix.android.api.session.content.ContentAttachmentData
import kotlinx.android.parcel.Parcelize

sealed class SharedData: Parcelable {

    @Parcelize
    data class Text(val text: String): SharedData()

    @Parcelize
    data class Attachments(val attachmentData: List<ContentAttachmentData>): SharedData()
}
