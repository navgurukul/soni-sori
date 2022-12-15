package org.navgurukul.hyper.navigator

import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.HyperAppModuleNavigator
import org.merakilearn.core.navigator.Mode
import org.navgurukul.hyper.MainActivity

@AutoService(HyperAppModuleNavigator::class)
class HyperAppModuleNavigatorImpl : HyperAppModuleNavigator {
    override fun launchHyperApp(activity: FragmentActivity, mode: Mode) {
        activity.startActivity(MainActivity.newIntent(activity, mode))
    }
}