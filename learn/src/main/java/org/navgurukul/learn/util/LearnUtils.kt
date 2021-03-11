package org.navgurukul.learn.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.preference.PreferenceManager

//TODO preference related code to appropriate class
object LearnUtils {

    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
    private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"

    fun getAuthToken(context: Context): String? {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        val token = preferenceManager.getString(KEY_AUTH_TOKEN, "")
        return "Bearer $token"
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN, false)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (null != capabilities)
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            @Suppress("DEPRECATION")
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            @Suppress("DEPRECATION")
            activeNetwork?.isConnectedOrConnecting?.let {
                return it
            }?.run {
                return false
            }
        }
        return false
    }
}