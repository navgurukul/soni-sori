package org.merakilearn.util

import org.merakilearn.R
import org.navgurukul.commonui.resources.StringProvider
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun Date.toDay(): String {
    val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return outputFormat.format(this)
}

fun Date.toTime(): String {
    val outputFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    return outputFormat.format(this)
}

fun Date.toDate(): String {
    val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
    return outputFormat.format(this)
}

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("dd MMMM, yyyy"))

fun LocalDate.relativeDay(stringProvider: StringProvider): String {
    val now = LocalDate.now()
    return if (this == now) {
        stringProvider.getString(R.string.today)
    } else if (this == now.plusDays(1)) {
        stringProvider.getString(R.string.tomorrow)
    } else {
        dayOfWeek.name.toLowerCase().capitalize()
    }
}
