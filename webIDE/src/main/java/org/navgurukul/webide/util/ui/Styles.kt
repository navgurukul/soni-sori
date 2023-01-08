//package org.navgurukul.webide.util.ui
//
//import android.content.Context
//
//import io.geeteshk.hyper.R
//import io.geeteshk.hyper.util.Prefs.defaultPrefs
//import io.geeteshk.hyper.util.Prefs.get
//
//object Styles {
//
//    fun getThemeInt(context: Context): Int {
//        val prefs = defaultPrefs(context)
//        return if (prefs["dark_theme", false]!!) {
//            R.style.Hyper_Dark
//        } else {
//            R.style.Hyper
//        }
//    }
//}
