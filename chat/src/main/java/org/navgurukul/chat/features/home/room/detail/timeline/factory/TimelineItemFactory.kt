package org.navgurukul.chat.features.home.room.detail.timeline.factory

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.resources.UserPreferencesProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.item.EmptyItem_
import timber.log.Timber

class TimelineItemFactory(
    private val messageItemFactory: MessageItemFactory,
    private val encryptedItemFactory: EncryptedItemFactory,
    private val noticeItemFactory: NoticeItemFactory,
    private val defaultItemFactory: DefaultItemFactory,
    private val encryptionItemFactory: EncryptionItemFactory,
    private val roomCreateItemFactory: RoomCreateItemFactory,
    private val verificationConclusionItemFactory: VerificationItemFactory,
    private val userPreferencesProvider: UserPreferencesProvider
) {

    fun create(
        event: TimelineEvent,
        nextEvent: TimelineEvent?,
        eventIdToHighlight: String?,
        callback: TimelineEventController.Callback?
    ): MerakiEpoxyModel<*> {
        val highlight = event.root.eventId == eventIdToHighlight

        val computedModel = try {
            when (event.root.getClearType()) {
                EventType.STICKER,
                EventType.MESSAGE -> messageItemFactory.create(
                    event,
                    nextEvent,
                    highlight,
                    callback
                )
                // State and call
                EventType.STATE_ROOM_TOMBSTONE,
                EventType.STATE_ROOM_NAME,
                EventType.STATE_ROOM_TOPIC,
                EventType.STATE_ROOM_AVATAR,
                EventType.STATE_ROOM_MEMBER,
                EventType.STATE_ROOM_THIRD_PARTY_INVITE,
                EventType.STATE_ROOM_ALIASES,
                EventType.STATE_ROOM_CANONICAL_ALIAS,
                EventType.STATE_ROOM_JOIN_RULES,
                EventType.STATE_ROOM_HISTORY_VISIBILITY,
                EventType.STATE_ROOM_GUEST_ACCESS,
                EventType.STATE_ROOM_WIDGET_LEGACY,
                EventType.STATE_ROOM_WIDGET,
                EventType.CALL_INVITE,
                EventType.CALL_HANGUP,
                EventType.CALL_ANSWER,
                EventType.STATE_ROOM_POWER_LEVELS,
                EventType.REACTION,
                EventType.REDACTION -> noticeItemFactory.create(event, highlight, callback)
                EventType.STATE_ROOM_ENCRYPTION -> {
                    encryptionItemFactory.create(event, highlight, callback)
                }
                // State room create
                EventType.STATE_ROOM_CREATE -> roomCreateItemFactory.create(event, callback)
                // Crypto
                EventType.ENCRYPTED -> {
                    if (event.root.isRedacted()) {
                        // Redacted event, let the MessageItemFactory handle it
                        messageItemFactory.create(event, nextEvent, highlight, callback)
                    } else {
                        encryptedItemFactory.create(event, nextEvent, highlight, callback)
                    }
                }
                EventType.KEY_VERIFICATION_ACCEPT,
                EventType.KEY_VERIFICATION_START,
                EventType.KEY_VERIFICATION_KEY,
                EventType.KEY_VERIFICATION_READY,
                EventType.KEY_VERIFICATION_MAC,
                EventType.CALL_CANDIDATES -> {
                    // TODO These are not filtered out by timeline when encrypted
                    // For now manually ignore
                    if (userPreferencesProvider.shouldShowHiddenEvents()) {
                        noticeItemFactory.create(event, highlight, callback)
                    } else {
                        null
                    }
                }
                EventType.KEY_VERIFICATION_CANCEL,
                EventType.KEY_VERIFICATION_DONE -> {
                    verificationConclusionItemFactory.create(event, highlight, callback)
                }

                // Unhandled event types
                else -> {
                    // Should only happen when shouldShowHiddenEvents() settings is ON
                    Timber.v("Type ${event.root.getClearType()} not handled")
                    defaultItemFactory.create(event, highlight, callback)
                }
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable, "failed to create message item")
            defaultItemFactory.create(event, highlight, callback, throwable)
        }
        return (computedModel ?: EmptyItem_())
    }
}
