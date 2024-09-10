package org.merakilearn.scratchjr

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.ScratchJrAppModuleNavigator


@AutoService(ScratchJrAppModuleNavigator::class)
class ScratchJrAppModuleNavigatorImpl : ScratchJrAppModuleNavigator {
    override fun launchScratchJrApp(activity: FragmentActivity) {
        val intent = Intent(activity, ScratchJrActivity::class.java)
        activity.startActivity(intent)
    }

}