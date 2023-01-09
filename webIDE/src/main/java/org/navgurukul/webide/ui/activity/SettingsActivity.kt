package org.navgurukul.webide.ui.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.widget_toolbar.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.ui.fragment.SettingsFragment

class SettingsActivity : ThemedActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsFragment, SettingsFragment())
                .commit()
    }
}
