package org.navgurukul.chat.features.home.room.detail.timeline

import androidx.annotation.ColorInt
import org.matrix.android.sdk.api.session.room.send.SendState
import org.navgurukul.chat.R
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.core.utils.getColorFromUserId

class MessageColorProvider(
    private val colorProvider: ColorProvider
) {

    @ColorInt
    fun getMemberNameTextColor(userId: String): Int {
        return colorProvider.getColor(getColorFromUserId(userId))
    }

    @ColorInt
    fun getMessageTextColor(sendState: SendState): Int {
        // When not in developer mode, we do not use special color for the encrypting state
        return when (sendState) {
            SendState.UNKNOWN,
            SendState.UNSENT,
            SendState.ENCRYPTING,
            SendState.SENDING -> colorProvider.getColorFromAttribute(R.attr.textPrimary)
            SendState.SENT,
            SendState.SYNCED -> colorProvider.getColorFromAttribute(R.attr.textPrimary)
            SendState.UNDELIVERED,
            SendState.FAILED_UNKNOWN_DEVICES -> colorProvider.getColorFromAttribute(R.attr.textPrimary)
        }
    }
}
