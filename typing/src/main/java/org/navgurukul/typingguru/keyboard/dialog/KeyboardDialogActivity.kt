package org.navgurukul.typingguru.keyboard.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import org.merakilearn.core.extentions.activityArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.navgurukul.commonui.platform.BaseActivity

class KeyboardDialogActivity : BaseActivity() {

    //this method is required to get intent from other module
    companion object {

        fun newIntent(context: Context, mode : Mode): Intent {
            return Intent(context, KeyboardDialogActivity::class.java).apply {
                putExtras(KeyboardDialogArgs(mode).toBundle()!!)
            }
        }
    }

    private val keyboardDialogArgs: KeyboardDialogArgs by activityArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            // set set the transition to be shown when the user enters this activity
            enterTransition = Fade()
            // set the transition to be shown when the user leaves this activity
            exitTransition = Fade()
        }


        val fragment = KeyboardDialogFragment.newInstance(keyboardDialogArgs.mode)
        fragment.show(supportFragmentManager, "TypingKeyboardDialogFragment")
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}