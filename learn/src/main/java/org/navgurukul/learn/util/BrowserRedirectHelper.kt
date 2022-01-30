package org.navgurukul.learn.util

import android.content.Context
import android.net.Uri
import androidx.preference.PreferenceManager
import org.navgurukul.learn.R

object BrowserRedirectHelper {

    const val WEBSITE_REDIRECT_URL_DELIMITER = "?redirect"

    fun getRedirectUrl(
        context: Context,
        url: String,
        otherQueryParams: Map<String, String>? = null
    ): Uri {
        val merakiWebsiteBaseUrl = "https://www.merakilearn.org/"
        val redirectUrl = merakiWebsiteBaseUrl.plus("redirect?")

        val endPoint = url.substringAfter(merakiWebsiteBaseUrl).substringBefore(
            WEBSITE_REDIRECT_URL_DELIMITER
        )

        val uriBuilder = Uri.parse(redirectUrl)
            .buildUpon()
            .appendQueryParameter(context.getString(R.string.header_token_key),
                PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("KEY_AUTH_TOKEN", null)
            )
            .appendQueryParameter(context.getString(R.string.header_redirect_url_key), endPoint)

        //add all other query params
        otherQueryParams?.let { map ->
            map.entries.forEach {
                uriBuilder.appendQueryParameter(it.key, it.value)
            }
        }

        return uriBuilder.build()
    }
}