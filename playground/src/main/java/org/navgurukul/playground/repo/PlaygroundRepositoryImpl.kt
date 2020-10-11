package org.navgurukul.playground.repo

import android.content.SharedPreferences

class PlaygroundRepositoryImpl(private val sharedPreferences: SharedPreferences): PlaygroundRepository {
    companion object{
        const val KEY_PREF_CODE_BACKUP = "Playground.CodeBackup"
    }
    override fun cacheCode(code: String) {
        sharedPreferences.edit().putString(KEY_PREF_CODE_BACKUP, code).apply()
    }

    override fun getCachedCode(): String {
        return sharedPreferences.getString(KEY_PREF_CODE_BACKUP, "") ?: ""
    }
}