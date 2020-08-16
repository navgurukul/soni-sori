package org.navgurukul.chat.features.notifications

import androidx.core.app.NotificationCompat
import org.navgurukul.chat.features.notifications.NotifiableEvent

data class SimpleNotifiableEvent(
        override var matrixID: String?,
        override val eventId: String,
        override val editedEventId: String?,
        override var noisy: Boolean,
        override val title: String,
        override val description: String,
        override val type: String?,
        override val timestamp: Long,
        override var soundName: String?,
        override var isPushGatewayEvent: Boolean = false) : NotifiableEvent {

    override var hasBeenDisplayed: Boolean = false
    override var isRedacted: Boolean = false
    override var lockScreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
}
