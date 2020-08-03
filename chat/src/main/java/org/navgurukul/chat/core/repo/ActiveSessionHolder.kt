package org.navgurukul.chat.core.repo

import arrow.core.Option
import im.vector.matrix.android.api.auth.AuthenticationService
import im.vector.matrix.android.api.session.Session
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

class ActiveSessionHolder(private val authenticationService: AuthenticationService,
                                              private val sessionObservableStore: ActiveSessionDataSource
) {

    private var activeSession: AtomicReference<Session?> = AtomicReference()

    fun setActiveSession(session: Session) {
        Timber.w("setActiveSession of ${session.myUserId}")
        activeSession.set(session)

    }

    fun clearActiveSession() {
        // Do some cleanup first
        activeSession.set(null)
        sessionObservableStore.post(Option.empty())
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
