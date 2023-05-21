package org.navgurukul.chat.core.extensions

import org.matrix.android.sdk.api.session.events.model.Event
import org.navgurukul.chat.core.resources.DateProvider
import org.threeten.bp.LocalDateTime

fun Event.localDateTime(): LocalDateTime {
    return DateProvider.toLocalDateTime(originServerTs)
}