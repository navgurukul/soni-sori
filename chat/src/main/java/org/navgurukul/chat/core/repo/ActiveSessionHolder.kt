package org.navgurukul.chat.core.repo

import arrow.core.Option
import org.matrix.android.sdk.api.session.Session
import org.navgurukul.chat.core.di.ImageManager
import org.navgurukul.chat.features.crypto.KeyRequestHandler
import org.navgurukul.chat.features.notifications.PushRuleTriggerListener
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

class ActiveSessionHolder(
    private val sessionObservableStore: ActiveSessionDataSource,
    private val imageManager: ImageManager,
    private val pushRuleTriggerListener: PushRuleTriggerListener,
    private val keyRequestHandler: KeyRequestHandler
) {

    private var activeSession: AtomicReference<Session?> = AtomicReference()

    fun setActiveSession(session: Session) {
        Timber.w("setActiveSession of ${session.myUserId}")
        activeSession.set(session)
        keyRequestHandler.start(session)
        imageManager.onSessionStarted(session)
        pushRuleTriggerListener.startWithSession(session)
        sessionObservableStore.post(Option.just(session))
    }

    fun clearActiveSession() {
        // Do some cleanup first
        activeSession.set(null)
        keyRequestHandler.stop()
        sessionObservableStore.post(Option.empty())
        pushRuleTriggerListener.stop()
    }

    fun hasActiveSession(): Boolean {
        return activeSession.get() != null
    }

    fun getSafeActiveSession(): Session? {
        return activeSession.get()
    }

    fun getActiveSession(): Session {
        return activeSession.get()
                ?: throw IllegalStateException("You should authenticate before using this")
    }

    // TODO: Stop sync ?
//    fun switchToSession(sessionParams: SessionParams) {
//        val newActiveSession = authenticationService.getSession(sessionParams)
//        activeSession.set(newActiveSession)
//    }
}
