package org.navgurukul.learn.util

import android.content.Context
import android.net.Uri
import androidx.preference.PreferenceManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidApplication
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.learn.R

object BrowserRedirectHelper{

        fun getRedirectUrl(
            context: Context,
            endPoint: String,
            otherQueryParams: Map<String, String>? = null
        ): Uri {
            val baseUrl = "https://dev-api.merakilearn.org/redirect".plus("?")

            val uriBuilder = Uri.parse(baseUrl)
                .buildUpon()
                .appendQueryParameter(context.getString(R.string.header_token_key), PreferenceManager.getDefaultSharedPreferences(context).getString("KEY_AUTH_TOKEN", null))
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