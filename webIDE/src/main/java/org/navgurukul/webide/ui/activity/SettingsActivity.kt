package org.navgurukul.webide.ui.activity

import android.os.Bundle
import org.navgurukul.webIDE.R
import org.navgurukul.webIDE.databinding.ActivitySettingsBinding
import org.navgurukul.webide.ui.fragment.SettingsFragment

class SettingsActivity : ThemedActivity() {

    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.include.toolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsFragment, SettingsFragment())
                .commit()
    }
}
