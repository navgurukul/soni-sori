package org.navgurukul.chat.core.date

import android.content.Context
import android.text.format.DateUtils
import org.navgurukul.chat.core.resources.LocaleProvider
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class SaralDateFormatter(
    private val context: Context,
    private val localeProvider: LocaleProvider
) {

    private val messageHourFormatter by lazy {
        DateTimeFormatter.ofPattern("H:mm", localeProvider.current())
    }
    private val messageDayFormatter by lazy {
        DateTimeFormatter.ofPattern("EEE d MMM", localeProvider.current())
    }

    fun formatMessageHour(localDateTime: LocalDateTime): String {
        return messageHourFormatter.format(localDateTime)
    }

    fun formatMessageDay(localDateTime: LocalDateTime): String {
        return messageDayFormatter.format(localDateTime)
    }

    fun formatRelativeDateTime(time: Long?): String {
        if (time == null) {
            return ""
        }
        return DateUtils.getRelativeDateTimeString(
            context,
            time,
            DateUtils.DAY_IN_MILLIS,
            2 * DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_TIME
        ).toString()
    }
}