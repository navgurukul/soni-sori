package org.navgurukul.chat.features.notifications

import androidx.core.app.NotificationCompat
import org.matrix.android.sdk.api.session.events.model.EventType
import org.navgurukul.chat.features.notifications.NotifiableEvent

data class NotifiableMessageEvent(
        override val eventId: String,
        override val editedEventId: String?,
        override var noisy: Boolean,
        override val timestamp: Long,
        var senderName: String?,
        var senderId: String?,
        var body: String?,
        var roomId: String,
        var roomName: String?,
        var roomIsDirect: Boolean = false
) : NotifiableEvent {

    override var matrixID: String? = null
    override var soundName: String? = null
    override var lockScreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    override var hasBeenDisplayed: Boolean = false
    override var isRedacted: Boolean = false

    var roomAvatarPath: String? = null
    var senderAvatarPath: String? = null

    override var isPushGatewayEvent: Boolean = false

    override val type: String
        get() = EventType.MESSAGE

    override val description: String?
        get() = body ?: ""

    override val title: String
        get() = senderName ?: ""

    // This is used for >N notification, as the result of a smart reply
    var outGoingMessage = false
    var outGoingMessageFailed = false
}
