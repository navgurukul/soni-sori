package org.merakilearn.util

import android.text.format.DateUtils
import org.merakilearn.R
import org.navgurukul.commonui.resources.StringProvider
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

fun Date.relativeDay(stringProvider: StringProvider): String {
    return when {
        DateUtils.isToday(time) -> {
            stringProvider.getString(R.string.today)
        }
        DateUtils.isToday(time - DateUtils.DAY_IN_MILLIS) -> {
            stringProvider.getString(R.string.tomorrow)
        }
        else -> {
            toDay().toLowerCase().capitalize()
        }
    }
}
