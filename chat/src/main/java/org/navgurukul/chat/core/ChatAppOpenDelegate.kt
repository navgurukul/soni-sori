package org.navgurukul.chat.core

import android.app.Activity
import org.merakilearn.core.appopen.AppOpenDelegate
import org.navgurukul.chat.core.pushers.PushersManager
import org.navgurukul.chat.features.notifications.NotificationDrawerManager
import org.navgurukul.chat.features.push.FcmHelper
import org.navgurukul.chat.features.settings.ChatPreferences

class ChatAppOpenDelegate(
    private val notificationDrawerManager: NotificationDrawerManager,
    private val fcmHelper: FcmHelper,
    private val pushersManager: PushersManager,
    private val chatPreferences: ChatPreferences
): AppOpenDelegate {

    override fun onAppOpened(activity: Activity, clearNotifications: Boolean) {
        if (clearNotifications) {
            notificationDrawerManager.clearAllEvents()
        }
    }

    override fun onHomeScreenOpened(activity: Activity, clearNotifications: Boolean) {
        if (clearNotifications) {
            notificationDrawerManager.clearAllEvents()
        }

        fcmHelper.ensureFcmTokenIsRetrieved(activity, pushersManager, chatPreferences.areNotificationEnabledForDevice())
    }
}