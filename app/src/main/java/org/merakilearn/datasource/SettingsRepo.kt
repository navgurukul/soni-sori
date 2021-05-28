package org.merakilearn.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import org.merakilearn.BuildConfig

class SettingsRepo(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val BASE_URL = "base_url"
    }

    var serverBaseUrl: String
        get() = sharedPreferences.getString(BASE_URL, BuildConfig.SERVER_URL)
            ?: BuildConfig.SERVER_URL
        set(value) = sharedPreferences.edit { putString(BASE_URL, value) }

}