package org.navgurukul.chat.core.services

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import im.vector.matrix.android.internal.session.sync.job.SyncService
import org.koin.android.ext.android.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.features.notifications.NotificationUtils

class SaralSyncService : SyncService() {

    companion  object {

        fun newIntent(context: Context, sessionId: String): Intent {
            return Intent(context, SaralSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
            }
        }
    }

    private val notificationUtils: NotificationUtils by inject()

    override fun onStart(isInitialSync: Boolean) {
        val notificationSubtitleRes = if (isInitialSync) {
            R.string.notification_initial_sync
        } else {
            R.string.notification_listening_for_events
        }
        val notification = notificationUtils.buildForegroundServiceNotification(notificationSubtitleRes, false)
        startForeground(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
    }

    override fun onRescheduleAsked(sessionId: String, isInitialSync: Boolean, delay: Long) {
        reschedule(sessionId, delay)
    }

    override fun onDestroy() {
        removeForegroundNotification()
        super.onDestroy()
    }

    private fun removeForegroundNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    private fun reschedule(sessionId: String, delay: Long) {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 0, newIntent(this, sessionId), 0)
        } else {
            PendingIntent.getService(this, 0, newIntent(this, sessionId), 0)
        }
        val firstMillis = System.currentTimeMillis() + delay
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
        }
    }
}
