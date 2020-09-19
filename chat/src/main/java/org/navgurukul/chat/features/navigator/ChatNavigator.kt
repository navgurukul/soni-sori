package org.navgurukul.chat.features.navigator

import android.content.Context

internal interface ChatNavigator {
    fun openRoom(context: Context, roomId: String, eventId: String? = null, buildTask: Boolean = false)
}