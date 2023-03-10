package org.navgurukul.chat.core.services

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.work.*
import org.matrix.android.sdk.internal.session.sync.job.SyncService
import org.koin.android.ext.android.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.features.notifications.NotificationUtils
import timber.log.Timber

class SaralSyncService : SyncService() {

    companion object {

        fun newOneShotIntent(context: Context, sessionId: String, timeoutSeconds: Int): Intent {
            return Intent(context, SaralSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, timeoutSeconds)
                it.putExtra(EXTRA_PERIODIC, false)
            }
        }

        fun newPeriodicIntent(
            context: Context,
            sessionId: String,
            timeoutSeconds: Int,
            delayInSeconds: Int
        ): Intent {
            return Intent(context, SaralSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, timeoutSeconds)
                it.putExtra(EXTRA_PERIODIC, true)
                it.putExtra(EXTRA_DELAY_SECONDS, delayInSeconds)
            }
        }

        fun newPeriodicNetworkBackIntent(
            context: Context,
            sessionId: String,
            timeoutSeconds: Int,
            delayInSeconds: Int
        ): Intent {
            return Intent(context, SaralSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, timeoutSeconds)
                it.putExtra(EXTRA_PERIODIC, true)
                it.putExtra(EXTRA_DELAY_SECONDS, delayInSeconds)
                it.putExtra(EXTRA_NETWORK_BACK_RESTART, true)
            }
        }

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
        val notification =
            notificationUtils.buildForegroundServiceNotification(notificationSubtitleRes, false)
        startForeground(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
    }

    override fun onDestroy() {
        removeForegroundNotification()
        super.onDestroy()
    }

    override fun onRescheduleAsked(
        sessionId: String,
        isInitialSync: Boolean,
        timeout: Int,
        delay: Int
    ) {
        reschedule(sessionId, timeout, delay)
    }

    override fun onNetworkError(
        sessionId: String,
        isInitialSync: Boolean,
        timeout: Int,
        delay: Int
    ) {
        Timber.d("## Sync: A network error occured during sync")
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<RestartWhenNetworkOn>()
                .setInputData(
                    Data.Builder()
                        .putString("sessionId", sessionId)
                        .putInt("timeout", timeout)
                        .putInt("delay", delay)
                        .build()
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        Timber.d("## Sync: Schedule a work to restart service when network will be on")
        WorkManager
            .getInstance(applicationContext)
            .enqueue(uploadWorkRequest)
    }

    private fun removeForegroundNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    private fun reschedule(sessionId: String, timeout: Int, delay: Int) {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                this,
                0,
                newPeriodicIntent(this, sessionId, timeout, delay),
                0 or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(this, 0, newPeriodicIntent(this, sessionId, timeout, delay), 0 or PendingIntent.FLAG_IMMUTABLE)
        }
        val firstMillis = System.currentTimeMillis() + delay * 1000L
        val alarmMgr = getSystemService<AlarmManager>()!!
        alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
    }

    class RestartWhenNetworkOn(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val sessionId = inputData.getString("sessionId") ?: return Result.failure()
            val timeout = inputData.getInt("timeout", 6)
            val delay = inputData.getInt("delay", 60)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    applicationContext,
                    0,
                    newPeriodicNetworkBackIntent(applicationContext, sessionId, timeout, delay),
                    0 or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                    PendingIntent.getService(
                        applicationContext,
                        0,
                        newPeriodicNetworkBackIntent(applicationContext, sessionId, timeout, delay),
                        0 or PendingIntent.FLAG_IMMUTABLE
                    )
            }
            val firstMillis = System.currentTimeMillis() + delay * 1000L
            val alarmMgr = ContextCompat.getSystemService<AlarmManager>(
                applicationContext,
                AlarmManager::class.java
            )!!
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
            // Indicate whether the work finished successfully with the Result
            return Result.success()
        }
    }
}
