package org.navgurukul.webide.navigator

import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.WebIDEAppModuleNavigator
import org.merakilearn.core.navigator.Mode
import org.navgurukul.webide.ui.activity.MainActivity
import org.navgurukul.webide.ui.activity.SplashActivity

@AutoService(WebIDEAppModuleNavigator::class)
class WebIDEAppModuleNavigatorImpl : WebIDEAppModuleNavigator {
    override fun launchWebIDEApp(activity: FragmentActivity, mode: Mode) {
        activity.startActivity(SplashActivity.newIntent(activity, mode))
    }
}