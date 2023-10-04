package org.merakilearn.core.appopen

import android.app.Activity

class AppOpenDelegateImpl:AppOpenDelegate {
    override fun onAppOpened(activity: Activity, clearNotifications: Boolean) {
            // Do nothing
    }

    override fun onHomeScreenOpened(activity: Activity, clearNotifications: Boolean) {
        // Do nothing
    }
}