package org.navgurukul.chat.core.resources

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

object DateProvider {

    private val zoneId = ZoneId.systemDefault()

    fun toLocalDateTime(timestamp: Long?): LocalDateTime {
        val instant = Instant.ofEpochMilli(timestamp ?: 0)
        return LocalDateTime.ofInstant(instant, zoneId)
    }

    fun currentLocalDateTime(): LocalDateTime {
        val instant = Instant.now()
        return LocalDateTime.ofInstant(instant, zoneId)
    }
}