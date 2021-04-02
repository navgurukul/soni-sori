package org.navgurukul.typingguru.utils

import android.content.SharedPreferences
import androidx.core.content.edit

class TypingGuruPreferenceManager(private val sharedPreferences: SharedPreferences) {

    fun setWebViewDisplayStatus(isShown: Boolean) {
        sharedPreferences.edit { putBoolean("isShown", isShown) }
    }

    fun iWebViewShown(): Boolean {
        return sharedPreferences.getBoolean("isShown", false)
    }
}