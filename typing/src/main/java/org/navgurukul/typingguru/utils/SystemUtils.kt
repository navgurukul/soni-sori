package org.navgurukul.typingguru.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import timber.log.Timber

class SystemUtils(val context: Context) {

    fun isOtgSupported(): Boolean {
        return try {
            context.getSystemService(Context.USB_SERVICE) ?: return false
            val isOtgSupported =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
            Timber.d("is Otg Supported :$isOtgSupported")
            isOtgSupported
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun buildUrl(url: String, vararg query: Pair<String, String>): String {
        return Uri.parse(url).buildUpon().apply {
            query.forEach {
                appendQueryParameter(it.first, it.second)
            }
        }.build().toString()
    }
}