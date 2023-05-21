package org.navgurukul.chat.features.notifications

import java.io.Serializable

/**
 * Parent interface for all events which can be displayed as a Notification
 */
interface NotifiableEvent : Serializable {
    var matrixID: String?
    val eventId: String
    val editedEventId: String?
    var noisy: Boolean
    val title: String
    val description: String?
    val type: String?
    val timestamp: Long
    // NotificationCompat.VISIBILITY_PUBLIC , VISIBILITY_PRIVATE , VISIBILITY_SECRET
    var lockScreenVisibility: Int
    // Compat: Only for android <7, for newer version the sound is defined in the channel
    var soundName: String?
    var hasBeenDisplayed: Boolean
    var isRedacted: Boolean
    // Used to know if event should be replaced with the one coming from eventstream
    var isPushGatewayEvent: Boolean
}
