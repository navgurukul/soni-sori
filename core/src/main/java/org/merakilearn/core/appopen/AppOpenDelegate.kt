package org.merakilearn.core.appopen

import android.app.Activity

interface AppOpenDelegate {
    fun onAppOpened(activity: Activity, clearNotifications: Boolean)
    fun onHomeScreenOpened(activity: Activity, clearNotifications: Boolean)
}