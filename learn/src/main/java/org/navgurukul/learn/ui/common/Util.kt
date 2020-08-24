package org.navgurukul.learn.ui.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkInfo
import android.os.Build

object Util {

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (null != capabilities)
                return capabilities.hasCapability(NET_CAPABILITY_INTERNET)
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