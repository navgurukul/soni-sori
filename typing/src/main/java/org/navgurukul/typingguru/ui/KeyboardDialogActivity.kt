package org.navgurukul.typingguru.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typingguru.utils.Utility
import org.navgurukul.typingguru.utils.Utility.TYPE_PRACTICE_TYPING

class KeyboardDialogActivity : BaseActivity() {

    //this method is required to get intent from other module
    companion object {

        const val CONTENT_KEY = "content"
        const val TYPE_KEY = "type"
        fun newIntent(context: Context, mode : TypingAppModuleNavigator.Mode): Intent {
            return Intent(context, KeyboardDialogActivity::class.java).apply {
                when (mode) {
                    is TypingAppModuleNavigator.Mode.Playground -> {
                        putExtra(CONTENT_KEY, Utility.getAlphabetList())
                        putExtra(TYPE_KEY, TYPE_PRACTICE_TYPING)
                    }
                    is TypingAppModuleNavigator.Mode.Course -> {
                        putExtra(CONTENT_KEY, mode.content)
                        putExtra(TYPE_KEY, mode.code)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set set the transition to be shown when the user enters this activity
            enterTransition = Fade()
            // set the transition to be shown when the user leaves this activity
            exitTransition = Fade()
        }


        val content = intent.getStringArrayListExtra(CONTENT_KEY) as ArrayList<String>
        val type = intent.getStringExtra(TYPE_KEY) as String

        val fragment = TypingKeyboardDialogFragment.newInstance(content, type)
        fragment.show(supportFragmentManager, "TypingKeyboardDialogFragment")
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}