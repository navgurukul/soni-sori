package org.navgurukul.chat.features.notifications

import org.matrix.android.sdk.api.pushrules.Action
import org.matrix.android.sdk.api.pushrules.PushRuleService
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.Event
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class PushRuleTriggerListener(
        private val resolver: NotifiableEventResolver
) : PushRuleService.PushRuleListener {

    private val notificationDrawerManager: NotificationDrawerManager by inject(NotificationDrawerManager::class.java)

    private var session: Session? = null

    override fun onMatchRule(event: Event, actions: List<Action>) {
        Timber.v("Push rule match for event ${event.eventId}")
        val safeSession = session ?: return Unit.also {
            Timber.e("Called without active session")
        }

        val notificationAction = actions.toNotificationAction()
        if (notificationAction.shouldNotify) {
            val notifiableEvent = resolver.resolveEvent(event, safeSession)
            if (notifiableEvent == null) {
                Timber.v("## Failed to resolve event")
                // TODO
            } else {
                notifiableEvent.noisy = !notificationAction.soundName.isNullOrBlank()
                Timber.v("New event to notify")
                notificationDrawerManager.onNotifiableEventReceived(notifiableEvent)
            }
        } else {
            Timber.v("Matched push rule is set to not notify")
        }
    }

    override fun onRoomLeft(roomId: String) {
        notificationDrawerManager.clearMessageEventOfRoom(roomId)
        notificationDrawerManager.clearMemberShipNotificationForRoom(roomId)
    }

    override fun onRoomJoined(roomId: String) {
        notificationDrawerManager.clearMemberShipNotificationForRoom(roomId)
    }

    override fun onEventRedacted(redactedEventId: String) {
        notificationDrawerManager.onEventRedacted(redactedEventId)
    }

    override fun batchFinish() {
        notificationDrawerManager.refreshNotificationDrawer()
    }

    fun startWithSession(session: Session) {
        if (this.session != null) {
            stop()
        }
        this.session = session
        session.addPushRuleListener(this)
    }

    fun stop() {
        session?.removePushRuleListener(this)
        session = null
        notificationDrawerManager.clearAllEvents()
    }
}
