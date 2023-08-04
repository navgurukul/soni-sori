package org.navgurukul.webide.ui.fragment

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import org.merakilearn.util.webide.ROOT_PATH
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.Prefs.set
import org.navgurukul.webide.R
import org.navgurukul.webide.extensions.intentFor
import org.navgurukul.webide.extensions.startAndFinish
import org.navgurukul.webide.ui.activity.SettingsActivity
import org.navgurukul.webide.util.Constants
import org.navgurukul.webide.util.Prefs.defaultPrefs
import timber.log.Timber
import java.io.File
import java.io.IOException

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val prefs = defaultPrefs(requireActivity())
        val darkTheme = preferenceManager.findPreference<SwitchPreference>("dark_theme")

        darkTheme?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            prefs["dark_theme"] = o
            activity?.let { with (it) { startAndFinish(intentFor<SettingsActivity>() ) }}
            true
        }

        val darkThemeEditor = preferenceManager.findPreference<SwitchPreference>("dark_theme_editor")
        darkThemeEditor?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            prefs["dark_theme_editor"] = o
            true
        }

        val lineNumbers = preferenceManager.findPreference<SwitchPreference>("show_line_numbers")
        lineNumbers?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            prefs["show_line_numbers"] = o
            true
        }

        val factoryReset = preferenceManager.findPreference<Preference>("factory_reset")
        val files = File(requireContext().ROOT_PATH()).list()
        factoryReset!!.isEnabled = files != null && files.isNotEmpty()
        factoryReset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(requireActivity())
                    .setTitle("Factory Reset")
                    .setMessage("Are you sure you want to delete ALL of your projects? This change cannot be undone!")
                    .setPositiveButton("RESET") { _, _ ->
                        try {
                            File(requireContext().ROOT_PATH()).walkTopDown().forEach { it -> it.deleteRecursively() }
                            factoryReset.isEnabled = false
                        } catch (e: IOException) {
                            Timber.e(e)
                        }
                    }
                    .setNegativeButton("CANCEL", null)
                    .show()

            true
        }

        val notices = preferenceManager.findPreference<Preference>("notices")
//        notices!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
//            requireContext().startActivity<OssLicensesMenuActivity>()
//            true
//        }

        if (prefs["dark_theme", false]!!) {
            arrayOf(darkTheme, darkThemeEditor, lineNumbers, factoryReset, notices).forEach {
                it?.icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
