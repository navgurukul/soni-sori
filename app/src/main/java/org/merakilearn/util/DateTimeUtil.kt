package org.merakilearn.util

import android.text.format.DateUtils
import org.merakilearn.R
import org.navgurukul.commonui.resources.StringProvider
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

fun Date.relativeDay(stringProvider: StringProvider): String {
    return when {
        DateUtils.isToday(time) -> {
            stringProvider.getString(R.string.today)
        }
        DateUtils.isToday(time - DateUtils.DAY_IN_MILLIS) -> {
            stringProvider.getString(R.string.tomorrow)
        }
        else -> {
            toDay().toLowerCase(Locale.ENGLISH).capitalize(Locale.ENGLISH)
        }
    }
}

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