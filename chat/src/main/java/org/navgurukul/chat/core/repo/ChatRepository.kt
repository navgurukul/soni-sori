package org.navgurukul.chat.core.repo

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import im.vector.matrix.android.api.auth.AuthenticationService
import org.navgurukul.chat.core.extensions.configureAndStart
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class ChatRepository(
    val authenticationService: AuthenticationService,
    private val activeSessionHolder: ActiveSessionHolder,
    private val applicationContext: Context,
    private val appStateHandler: AppStateHandler
) {

    private val isInitialized = AtomicBoolean(false)

    fun initialise() {
        if (isInitialized.compareAndSet(false, true)) {
            if (authenticationService.hasAuthenticatedSessions() && !activeSessionHolder.hasActiveSession()) {
                val lastAuthenticatedSession = authenticationService.getLastAuthenticatedSession()!!
                activeSessionHolder.setActiveSession(lastAuthenticatedSession)
                lastAuthenticatedSession.configureAndStart(applicationContext)
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
                }
            })
            ProcessLifecycleOwner.get().lifecycle.addObserver(appStateHandler)
        }
    }
}