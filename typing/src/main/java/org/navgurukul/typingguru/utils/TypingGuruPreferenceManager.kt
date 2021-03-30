package org.navgurukul.typingguru.utils

import android.content.SharedPreferences

class TypingGuruPreferenceManager(private val sharedPreferences: SharedPreferences) {

    fun setWebViewDisplayStatus(isShown: Boolean) {
        sharedPreferences.edit().putBoolean("isShown", isShown).apply()
    }

    fun iWebViewShown(): Boolean {
        return sharedPreferences.getBoolean("isShown", false)
    }
}