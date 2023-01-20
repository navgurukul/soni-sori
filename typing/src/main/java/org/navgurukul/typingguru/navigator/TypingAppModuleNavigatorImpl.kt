package org.navgurukul.typingguru.navigator

import android.app.ActivityOptions
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.koin.core.context.loadKoinModules
import org.koin.java.KoinJavaComponent.inject
import org.merakilearn.core.navigator.Mode
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.typingguru.di.typingModule
import org.navgurukul.typingguru.keyboard.KeyboardActivity
import org.navgurukul.typingguru.keyboard.dialog.KeyboardDialogActivity
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager

@AutoService(TypingAppModuleNavigator::class)
class TypingAppModuleNavigatorImpl : TypingAppModuleNavigator {

    override fun launchTypingApp(activity: FragmentActivity, mode : Mode) {
        loadKoinModules(typingModule)
        val typingManager: TypingGuruPreferenceManager by inject(TypingGuruPreferenceManager::class.java)
        if (typingManager.iWebViewShown()) {
            activity.startActivity(KeyboardActivity.newIntent(activity, mode))
        } else {
            activity.startActivity(
                KeyboardDialogActivity.newIntent(activity, mode),
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

    }
}