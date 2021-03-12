package org.navgurukul.typingguru.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import org.navgurukul.commonui.platform.BaseActivity

class KeyboardDialogActivity : BaseActivity() {

    //this method is required to get intent from other module
    companion object {

        const val CONTENT_KEY = "content"
        const val TYPE_KEY = "type"
        fun newIntent(context: Context, content : ArrayList<String>, type : String): Intent {
            return Intent(context, KeyboardDialogActivity::class.java).apply {
                putExtra(CONTENT_KEY, content)
                putExtra(TYPE_KEY, type)
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