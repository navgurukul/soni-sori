package org.navgurukul.learn.util

import android.text.format.DateUtils
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

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


fun Long.toDisplayableInterval(stringProvider: StringProvider): String {
    val days = TimeUnit.MILLISECONDS.toDays(this).toInt()
    if (days > 0) {
        return stringProvider.getQuantityString(R.plurals.days, days, days)
    }
    val hours = TimeUnit.MILLISECONDS.toHours(this).toInt()
    if (hours > 0) {
        return stringProvider.getQuantityString(R.plurals.hours, hours, hours)
    }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(this).toInt()
    if (minutes > 0) {
        return stringProvider.getQuantityString(R.plurals.minutes, minutes, minutes)
    }

    return stringProvider.getQuantityString(R.plurals.minutes, 0, 0)
}