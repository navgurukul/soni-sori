package org.navgurukul.typingguru.utils

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.navgurukul.typingguru.R
import java.util.concurrent.TimeUnit

class RemoteConfig(private val mFirebaseRemoteConfig : FirebaseRemoteConfig) {
    private val TAG = "RemoteConfig"

    fun initialise() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(24)
        }
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG,"Config params updated")
                } else {
                    Log.d(TAG,"fetchRemoteConfigAndUpdate Failed")
                }
            }
    }

    fun getValueFromRemoteConfig(key : String) : String {
        val value = mFirebaseRemoteConfig.getString(key)
        Log.d(TAG, "value : $value")
        return value
    }
}