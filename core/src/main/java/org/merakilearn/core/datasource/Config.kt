package org.merakilearn.core.datasource

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.merakilearn.core.R
import org.merakilearn.core.extentions.objectify
import org.merakilearn.core.extentions.objectifyToList
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Config {

    val remoteConfig = Firebase.remoteConfig

    companion object {
        const val KEY_AVAILABLE_LANG = "available_lang"
        const val ON_BOARDING_DATA = "on_boarding_data"
        const val KEYBOARD_URL_KEY = "keyboard_purchase_url"
        const val OPPORTUNITY_URL="opportunity_url"
        const val PRIVACY_POLICY = "privacy_policy"
    }

    /**
     * Should be called on app start
     */
    fun initialise() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(24)
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Config params updated: ${task.result}  with values ${remoteConfig.all}")
                } else {
                    Timber.w("fetchRemoteConfigAndUpdate Failed")
                }
            }
    }

    inline fun <reified T> getObjectifiedValue(key: String): T? = getValue<String>(key).objectify()

    inline fun <reified T> getObjectifiedList(key: String): List<T>? = getValue<String>(key).objectifyToList()

    inline fun <reified T> getValue(key: String): T {
        return when (T::class) {
            String::class -> remoteConfig.getString(key) as T
            Long::class -> remoteConfig.getLong(key) as T
            Double::class -> remoteConfig.getDouble(key) as T
            Boolean::class -> remoteConfig.getBoolean(key) as T
            else -> throw IllegalArgumentException("generic type not handled ${T::class.java.name}")
        }
    }
}