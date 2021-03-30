package org.navgurukul.typingguru.navigator

import android.app.ActivityOptions
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.koin.core.context.loadKoinModules
import org.koin.java.KoinJavaComponent.inject
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.typingguru.di.typingModule
import org.navgurukul.typingguru.ui.KeyboardActivity
import org.navgurukul.typingguru.ui.KeyboardDialogActivity
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.utils.Utility

@AutoService(TypingAppModuleNavigator::class)
class TypingAppModuleNavigatorImpl : TypingAppModuleNavigator {

    override fun launchTypingApp(activity: FragmentActivity, mode : TypingAppModuleNavigator.Mode) {
        loadKoinModules(typingModule)
        val typingManager: TypingGuruPreferenceManager by inject(TypingGuruPreferenceManager::class.java)
        val utility: Utility by inject(Utility::class.java)
        if (typingManager.iWebViewShown()) {
            activity.startActivity(KeyboardActivity.newIntent(activity, mode, utility))
        } else {
            activity.startActivity(KeyboardDialogActivity.newIntent(activity, mode, utility),
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

    }
}