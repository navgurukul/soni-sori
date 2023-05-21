package org.navgurukul.chat.features.push

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.RemoteMessage
import org.koin.java.KoinJavaComponent.inject
import org.merakilearn.core.push.FCMServiceDelegate
import org.navgurukul.chat.BuildConfig
import org.navgurukul.chat.core.pushers.PushersManager
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.settings.ChatPreferences
import timber.log.Timber

class ChatFCMServiceDelegate(private val context: Context): FCMServiceDelegate {

    private val fcmHelper: FcmHelper by inject(FcmHelper::class.java)
    private val pusherManager: PushersManager by inject(PushersManager::class.java)
    private val activeSessionHolder: ActiveSessionHolder by inject(ActiveSessionHolder::class.java)
    private val chatPreferences: ChatPreferences by inject(ChatPreferences::class.java)

    // UI handler
    private val mUIHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onNewToken(refreshedToken: String) {
        fcmHelper.storeFcmToken(context, refreshedToken)
        if (chatPreferences.areNotificationEnabledForDevice() && activeSessionHolder.hasActiveSession()) {
            pusherManager.registerPusherWithFcmKey(refreshedToken)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (!chatPreferences.areNotificationEnabledForDevice()) {
            Timber.i("Notification are disabled for this device")
            return
        }

        if (BuildConfig.DEBUG) {
            Timber.i("## onMessageReceived() %s", message.data.toString())
            Timber.i("## onMessageReceived() from FCM with priority %s", message.priority)
        }
        mUIHandler.post {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                // we are in foreground, let the sync do the things?
                Timber.v("PUSH received in a foreground state, ignore")
            } else {
                onMessageReceivedInternal(message.data)
            }
        }
    }

    /**
     * Internal receive method
     *
     * @param data Data map containing message data as key/value pairs.
     * For Set of keys use data.keySet().
     */
    private fun onMessageReceivedInternal(data: Map<String, String>) {
        try {
            if (BuildConfig.DEBUG) {
                Timber.i("## onMessageReceivedInternal() : $data")
            }
            val eventId = data["event_id"]
            val roomId = data["room_id"]
            if (eventId == null || roomId == null) {
                Timber.e("## onMessageReceivedInternal() missing eventId and/or roomId")
                return
            }
            // update the badge counter
//            val unreadCount = data.get("unread")?.let { Integer.parseInt(it) } ?: 0
//            BadgeProxy.updateBadgeCount(applicationContext, unreadCount)

            val session = activeSessionHolder.getSafeActiveSession()

            if (session == null) {
                Timber.w("## Can't sync from push, no current session")
            } else {
                if (isEventAlreadyKnown(eventId, roomId)) {
                    Timber.i("Ignoring push, event already known")
                } else {
                    Timber.v("Requesting background sync")
                    session.requireBackgroundSync()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "## onMessageReceivedInternal() failed")
        }
    }

    // check if the event was not yet received
    // a previous catchup might have already retrieved the notified event
    private fun isEventAlreadyKnown(eventId: String?, roomId: String?): Boolean {
        if (null != eventId && null != roomId) {
            try {
                val session = activeSessionHolder.getSafeActiveSession() ?: return false
                val room = session.getRoom(roomId) ?: return false
                return room.getTimeLineEvent(eventId) != null
            } catch (e: Exception) {
                Timber.e(e, "## isEventAlreadyKnown() : failed to check if the event was already defined")
            }
        }
        return false
    }

}