package org.navgurukul.chat

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class ChatBaseActivity: AppCompatActivity() {

    /**
     * Configure the Toolbar, with default back button.
     */
    protected fun configureToolbar(toolbar: Toolbar, displayBack: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(displayBack)
            it.setDisplayHomeAsUpEnabled(displayBack)
            it.title = null
        }
    }
}