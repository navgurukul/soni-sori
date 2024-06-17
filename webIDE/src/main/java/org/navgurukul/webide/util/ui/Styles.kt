package org.navgurukul.webide.util.ui

import android.content.Context
import org.navgurukul.webide.R

import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get

object Styles {

    fun getThemeInt(context: Context): Int {
        val prefs = defaultPrefs(context)
//        return if (prefs["dark_theme", false]!!) {
           return R.style.AppTheme_Dark
//        } else {
//            R.style.AppTheme
//        }
    }
}
