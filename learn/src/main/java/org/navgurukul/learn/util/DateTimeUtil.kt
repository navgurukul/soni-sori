package org.navgurukul.learn.util

import android.text.format.DateUtils
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDay(): String {
    val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return outputFormat.format(this)
}

fun Date.toTime(): String {
    val outputFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    return outputFormat.format(this)
}

fun Date.toDate(): String {
    val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
    return outputFormat.format(this)
}

fun String.toDate(): Date = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH).parse(this)!!


