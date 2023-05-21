package org.navgurukul.chat.core

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.jakewharton.threetenabp.AndroidThreeTen
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.core.extensions.configureAndStart
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.repo.AppStateHandler
import org.navgurukul.chat.features.lifecycle.SaralActivityLifecycleCallbacks
import org.navgurukul.chat.features.notifications.NotificationDrawerManager
import org.navgurukul.chat.features.notifications.NotificationUtils
import org.navgurukul.chat.features.popup.PopupAlertManager
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

object ChatInitializer {

    private val isInitialized = AtomicBoolean(false)

    fun initialise(application: Application) {
        if (isInitialized.compareAndSet(false, true)) {
            AndroidThreeTen.init(application)
            val authenticationService: AuthenticationService by inject(AuthenticationService::class.java)
            val activeSessionHolder: ActiveSessionHolder by inject(ActiveSessionHolder::class.java)
            val appStateHandler: AppStateHandler by inject(AppStateHandler::class.java)
            val notificationUtils: NotificationUtils by inject(NotificationUtils::class.java)
            val popupAlertManager: PopupAlertManager by inject(PopupAlertManager::class.java)
            val notificationDrawerManager: NotificationDrawerManager by inject(NotificationDrawerManager::class.java)

            if (authenticationService.hasAuthenticatedSessions() && !activeSessionHolder.hasActiveSession()) {
                val lastAuthenticatedSession = authenticationService.getLastAuthenticatedSession()!!
                activeSessionHolder.setActiveSession(lastAuthenticatedSession)
                lastAuthenticatedSession.configureAndStart(application)
            }
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                fun entersForeground() {
                    Timber.i("App entered foreground")
                    activeSessionHolder.getSafeActiveSession()?.also {
                        it.stopAnyBackgroundSync()
                    }
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                fun entersBackground() {
                    Timber.i("App entered background") // call persistInfo
                    notificationDrawerManager.persistInfo()
                }
            })
            ProcessLifecycleOwner.get().lifecycle.addObserver(appStateHandler)

            notificationUtils.createNotificationChannels()

            application.registerActivityLifecycleCallbacks(SaralActivityLifecycleCallbacks(popupAlertManager))
        }

    }
}