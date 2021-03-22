package org.navgurukul.typingguru.navigator

import android.app.ActivityOptions
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.typingguru.ui.KeyboardActivity
import org.navgurukul.typingguru.ui.KeyboardDialogActivity
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager

@AutoService(TypingAppModuleNavigator::class)
class TypingAppModuleNavigatorImpl : TypingAppModuleNavigator {

    override fun launchTypingApp(activity: FragmentActivity, mode : TypingAppModuleNavigator.Mode) {
        TypingGuruPreferenceManager.init(activity)

        if (TypingGuruPreferenceManager.iWebViewShown()) {
            activity.startActivity(KeyboardActivity.newIntent(activity, mode))
        } else {
            activity.startActivity(KeyboardDialogActivity.newIntent(activity, mode),
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

    }
}