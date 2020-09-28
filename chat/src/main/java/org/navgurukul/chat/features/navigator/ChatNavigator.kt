package org.navgurukul.chat.features.navigator

import android.content.Context

interface ChatNavigator {
    fun openRoom(context: Context, roomId: String, eventId: String? = null, buildTask: Boolean = false)
    fun openDeepLink(context: Context, deepLink: String)
}