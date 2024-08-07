package org.merakilearn.core.impl

import android.app.Activity
import org.merakilearn.core.appopen.AppOpenDelegate
import timber.log.Timber

class AppOpenDelegateImpl : AppOpenDelegate {
    override fun onAppOpened(activity: Activity, clearNotifications: Boolean) {
        Timber.d("App opened: ${activity.localClassName}, clearNotifications: $clearNotifications")
    }

    override fun onHomeScreenOpened(activity: Activity, clearNotifications: Boolean) {
        Timber.d("Home screen opened: ${activity.localClassName}, clearNotifications: $clearNotifications")
    }
}