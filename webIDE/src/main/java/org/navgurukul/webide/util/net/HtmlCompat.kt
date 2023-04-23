package org.navgurukul.webide.util.net

import android.os.Build
import android.text.Html
import android.text.Spanned

object HtmlCompat {

    @Suppress("DEPRECATION")
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT < 24) {
            Html.fromHtml(html)
        } else {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        }
    }
}
