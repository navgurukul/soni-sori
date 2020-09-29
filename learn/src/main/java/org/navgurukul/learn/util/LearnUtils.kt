package org.navgurukul.learn.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.preference.PreferenceManager

object LearnUtils {

    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
    private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
    private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"

    private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
    private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"

    fun getAuthToken(context: Context): String? {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        val token = preferenceManager.getString(KEY_AUTH_TOKEN, "")
        return "Bearer $token"
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN, false)
    }

    fun isFakeLogin(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_IS_FAKE_LOGIN, false)
    }

    fun launchOnBoardingActivity(activity: Activity) {
        try {
            val clazz = Class.forName("org.merakilearn.OnBoardingActivity")
            val method = clazz.getMethod("launch", Activity::class.java)
            method.invoke(clazz.newInstance(), activity)
        } catch (ex: Exception) {
            activity.finish()
        }
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
            return activeNetwork?.isConnectedOrConnecting!!
        }
        return false
    }
}