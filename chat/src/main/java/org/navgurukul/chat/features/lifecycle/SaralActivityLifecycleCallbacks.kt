package org.navgurukul.chat.features.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import org.navgurukul.chat.features.popup.PopupAlertManager

class SaralActivityLifecycleCallbacks constructor(private val popupAlertManager: PopupAlertManager) : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        popupAlertManager.onNewActivityDisplayed(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }
}