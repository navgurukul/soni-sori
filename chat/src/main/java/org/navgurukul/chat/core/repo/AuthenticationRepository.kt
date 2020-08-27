package org.navgurukul.chat.core.repo

import android.content.Context
import im.vector.matrix.android.api.MatrixCallback
import im.vector.matrix.android.api.auth.AuthenticationService
import im.vector.matrix.android.api.auth.data.HomeServerConnectionConfig
import im.vector.matrix.android.api.auth.data.LoginFlowResult
import im.vector.matrix.android.api.session.Session
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.configureAndStart
import org.navgurukul.chat.core.resources.StringProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepository(
    private val authService: AuthenticationService,
    private val activeSessionHolder: ActiveSessionHolder,
    private val stringProvider: StringProvider,
    private val appContext: Context
) {

    suspend fun getLoginFlow(): LoginFlowResult? = suspendCoroutine {
        authService.getLoginFlow(
            HomeServerConnectionConfig.Builder()
                .withHomeServerUri(stringProvider.getString(R.string.home_server_url))
                .build(),
            object : MatrixCallback<LoginFlowResult> {
                override fun onSuccess(data: LoginFlowResult) {
                    it.resume(data)
                }

                override fun onFailure(failure: Throwable) {
                    it.resume(null)
                }
            }
        )
    }

    suspend fun login(): Session? {
        getLoginFlow()?.let { _ ->
            return suspendCoroutine {
                authService.getLoginWizard().login(
                    "t-saral",
                    "hello123",
                    "Android",
                    object : MatrixCallback<Session> {
                        override fun onSuccess(data: Session) {
                            super.onSuccess(data)
                            activeSessionHolder.setActiveSession(data)
                            data.configureAndStart(appContext)
                            it.resume(data)
                        }

                        override fun onFailure(failure: Throwable) {
                            super.onFailure(failure)
                            it.resume(null)
                        }
                    }
                )
            }
        } ?: return null
    }
}