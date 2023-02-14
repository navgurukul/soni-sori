package org.navgurukul.webide.navigator

import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.WebIDEAppModuleNavigator
import org.navgurukul.webide.ui.activity.ProjectActivity

@AutoService(WebIDEAppModuleNavigator::class)
class WebIDEAppModuleNavigatorImpl : WebIDEAppModuleNavigator {
    override fun launchWebIDEApp(activity: FragmentActivity, projectName: String) {
        activity.startActivity(ProjectActivity.newIntent(activity, projectName))
    }
}