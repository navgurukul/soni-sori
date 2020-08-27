package org.navgurukul.chat.core.extensions

import im.vector.matrix.android.api.session.events.model.Event
import org.navgurukul.chat.core.resources.DateProvider
import org.threeten.bp.LocalDateTime

fun Event.localDateTime(): LocalDateTime {
    return DateProvider.toLocalDateTime(originServerTs)
}