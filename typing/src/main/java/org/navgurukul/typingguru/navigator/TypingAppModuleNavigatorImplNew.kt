package org.navgurukul.typingguru.navigator

import android.app.ActivityOptions
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import org.koin.core.context.loadKoinModules
import org.koin.java.KoinJavaComponent.inject
import org.merakilearn.core.navigator.ModeNew
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.merakilearn.core.navigator.TypingAppModuleNavigatorNew
import org.navgurukul.typingguru.di.typingModule
import org.navgurukul.typingguru.keyboard.KeyboardWordActivity
import org.navgurukul.typingguru.keyboard.dialog.KeyboardDialogNewActivity
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager

@AutoService(TypingAppModuleNavigatorNew::class)
class TypingAppModuleNavigatorImplNew : TypingAppModuleNavigatorNew {
    override fun launchTypingAppNew(activity: FragmentActivity, mode: ModeNew) {
        loadKoinModules(typingModule)
        val typingManager: TypingGuruPreferenceManager by inject(TypingGuruPreferenceManager::class.java)
        activity.startActivity(KeyboardWordActivity.newIntent(activity, mode))
//
//        if (typingManager.iWebViewShown()) {
//            activity.startActivity(KeyboardWordActivity.newIntent(activity, mode))
//        } else {
//            activity.startActivity(
//                KeyboardDialogNewActivity.newIntent(activity, mode),
//                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
//        }
    }


}