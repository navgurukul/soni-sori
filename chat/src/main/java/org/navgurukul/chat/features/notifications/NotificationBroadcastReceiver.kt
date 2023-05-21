package org.navgurukul.chat.features.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.read.ReadService
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import timber.log.Timber

/**
 * Receives actions broadcast by notification (on click, on dismiss, inline replies, etc.)
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    private val activeSessionHolder: ActiveSessionHolder by inject(ActiveSessionHolder::class.java)
    private val notificationDrawerManager: NotificationDrawerManager by inject(NotificationDrawerManager::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        Timber.v("NotificationBroadcastReceiver received : $intent")
        when (intent.action) {
            NotificationUtils.SMART_REPLY_ACTION        ->
                handleSmartReply(intent, context)
            NotificationUtils.DISMISS_ROOM_NOTIF_ACTION ->
                intent.getStringExtra(KEY_ROOM_ID)?.let {
                    notificationDrawerManager.clearMessageEventOfRoom(it)
                }
            NotificationUtils.DISMISS_SUMMARY_ACTION    ->
                notificationDrawerManager.clearAllEvents()
            NotificationUtils.MARK_ROOM_READ_ACTION     ->
                intent.getStringExtra(KEY_ROOM_ID)?.let {
                    notificationDrawerManager.clearMessageEventOfRoom(it)
                    handleMarkAsRead(it)
                }
            NotificationUtils.JOIN_ACTION               -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let {
                    notificationDrawerManager.clearMemberShipNotificationForRoom(it)
                    handleJoinRoom(it)
                }
            }
            NotificationUtils.REJECT_ACTION             -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let {
                    notificationDrawerManager.clearMemberShipNotificationForRoom(it)
                    handleRejectRoom(it)
                }
            }
        }
    }

    private fun handleJoinRoom(roomId: String) {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            session.getRoom(roomId)
                    ?.join(callback = NoOpMatrixCallback())
        }
    }

    private fun handleRejectRoom(roomId: String) {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            session.getRoom(roomId)
                    ?.leave(callback = NoOpMatrixCallback())
        }
    }

    private fun handleMarkAsRead(roomId: String) {
        activeSessionHolder.getActiveSession().let { session ->
            session.getRoom(roomId)
                    ?.markAsRead(ReadService.MarkAsReadParams.READ_RECEIPT, NoOpMatrixCallback())
        }
    }

    private fun handleSmartReply(intent: Intent, context: Context) {
        val message = getReplyMessage(intent)
        val roomId = intent.getStringExtra(KEY_ROOM_ID)

        if (message.isNullOrBlank() || roomId.isNullOrBlank()) {
            // ignore this event
            // Can this happen? should we update notification?
            return
        }
        activeSessionHolder.getActiveSession().let { session ->
            session.getRoom(roomId)?.let { room ->
                sendMatrixEvent(message, session, room, context)
            }
        }
    }

    private fun sendMatrixEvent(message: String, session: Session, room: Room, context: Context?) {
        room.sendTextMessage(message)

        /*
        // TODO Error cannot be managed the same way than in Riot

        val event = Event(mxMessage, session.credentials.userId, roomId)
        room.storeOutgoingEvent(event)
        room.sendEvent(event, object : MatrixCallback<Void?> {
            override fun onSuccess(info: Void?) {
                Timber.v("Send message : onSuccess ")
            }

            override fun onNetworkError(e: Exception) {
                Timber.e(e, "Send message : onNetworkError")
                onSmartReplyFailed(e.localizedMessage)
            }

            override fun onMatrixError(e: MatrixError) {
                Timber.v("Send message : onMatrixError " + e.message)
                if (e is MXCryptoError) {
                    Toast.makeText(context, e.detailedErrorDescription, Toast.LENGTH_SHORT).show()
                    onSmartReplyFailed(e.detailedErrorDescription)
                } else {
                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    onSmartReplyFailed(e.localizedMessage)
                }
            }

            override fun onUnexpectedError(e: Exception) {
                Timber.e(e, "Send message : onUnexpectedError " + e.message)
                onSmartReplyFailed(e.message)
            }


            fun onSmartReplyFailed(reason: String?) {
                val notifiableMessageEvent = NotifiableMessageEvent(
                        event.eventId,
                        false,
                        System.currentTimeMillis(),
                        session.myUser?.displayname
                                ?: context?.getString(R.string.notification_sender_me),
                        session.myUserId,
                        message,
                        roomId,
                        room.getRoomDisplayName(context),
                        room.isDirect)
                notifiableMessageEvent.outGoingMessage = true
                notifiableMessageEvent.outGoingMessageFailed = true

                VectorApp.getInstance().notificationDrawerManager.onNotifiableEventReceived(notifiableMessageEvent)
                VectorApp.getInstance().notificationDrawerManager.refreshNotificationDrawer(null)
            }
        })
        */
    }

    private fun getReplyMessage(intent: Intent?): String? {
        if (intent != null) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                return remoteInput.getCharSequence(KEY_TEXT_REPLY)?.toString()
            }
        }
        return null
    }

    companion object {
        const val KEY_ROOM_ID = "roomID"
        const val KEY_TEXT_REPLY = "key_text_reply"
    }
}
