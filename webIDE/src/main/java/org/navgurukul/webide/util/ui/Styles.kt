package org.navgurukul.webide.util.ui

import android.content.Context
import org.navgurukul.webIDE.R

import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get

object Styles {

    fun getThemeInt(context: Context): Int {
        val prefs = defaultPrefs(context)
        return if (prefs["dark_theme", false]!!) {
            R.style.Hyper_Dark
        } else {
            R.style.Hyper
        }
    }
}
