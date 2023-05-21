package org.merakilearn.core.core.repo

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.merakilearn.core.core.extensions.configureAndStart
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepository(
    private val authService: org.matrix.android.sdk.api.auth.AuthenticationService,
    private val activeSessionHolder: ActiveSessionHolder,
    private val stringProvider: org.navgurukul.commonui.resources.StringProvider,
    private val saralPreferences: org.navgurukul.chat.features.settings.ChatPreferences,
    private val appContext: Context
) {

    suspend fun getLoginFlow(): org.matrix.android.sdk.api.auth.data.LoginFlowResult? = suspendCoroutine {
        authService.getLoginFlow(
            org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig.Builder()
                .withHomeServerUri(stringProvider.getString(org.navgurukul.chat.R.string.home_server_url))
                .build(),
            object : org.matrix.android.sdk.api.MatrixCallback<org.matrix.android.sdk.api.auth.data.LoginFlowResult> {
                override fun onSuccess(data: org.matrix.android.sdk.api.auth.data.LoginFlowResult) {
                    it.resume(data)
                }

                override fun onFailure(failure: Throwable) {
                    it.resume(null)
                }
            }
        )
    }

    suspend fun login(id: String, pass: String): org.matrix.android.sdk.api.session.Session? {
        getLoginFlow()?.let { _ ->
            return suspendCoroutine {
                authService.getLoginWizard().login(id, pass, "Android",
                    object : org.matrix.android.sdk.api.MatrixCallback<org.matrix.android.sdk.api.session.Session> {
                        override fun onSuccess(data: org.matrix.android.sdk.api.session.Session) {
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

    suspend fun logout(): Boolean {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            return suspendCoroutine {
                session.signOut(true, object : org.matrix.android.sdk.api.MatrixCallback<Unit> {
                    override fun onSuccess(data: Unit) {
                        super.onSuccess(data)
                        activeSessionHolder.clearActiveSession()
                        doLocalCleanup()
                        it.resume(true)
                    }

                    override fun onFailure(failure: Throwable) {
                        super.onFailure(failure)
                        it.resume(false)
                    }
                }
                )
            }
        } ?: run {
            activeSessionHolder.clearActiveSession()
            doLocalCleanup()
            return true
        }
    }

    private fun doLocalCleanup() {
        GlobalScope.launch(Dispatchers.Main) {
            // On UI Thread
            saralPreferences.clearPreferences()
            withContext(Dispatchers.IO) {
                // On BG thread
                com.bumptech.glide.Glide.get(appContext).clearDiskCache()

                // Also clear cache (Logs, etc...)
                org.navgurukul.chat.core.utils.deleteAllFiles(appContext.cacheDir)
            }
        }
    }

}