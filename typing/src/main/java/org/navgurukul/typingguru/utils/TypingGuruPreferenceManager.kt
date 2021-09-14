package org.navgurukul.typingguru.utils

import android.content.SharedPreferences
import androidx.core.content.edit

class TypingGuruPreferenceManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val IS_SHOWN = "isShown"
    }

    fun setWebViewDisplayStatus(isShown: Boolean) {
        sharedPreferences.edit { putBoolean(IS_SHOWN, isShown) }
    }

    fun iWebViewShown(): Boolean {
        return sharedPreferences.getBoolean(IS_SHOWN, false)
    }
}