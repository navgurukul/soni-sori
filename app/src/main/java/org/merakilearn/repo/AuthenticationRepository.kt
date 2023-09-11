package org.merakilearn.repo


import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.commonui.resources.StringProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepository(
    private val authService: AuthenticationService,
    private val activeSessionHolder: ActiveSessionHolder,
    private val stringProvider: StringProvider,
    private val saralPreferences: ChatPreferences,
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

    suspend fun login(id: String, pass: String): Session? {
        getLoginFlow()?.let { _ ->
            return suspendCoroutine {
                authService.getLoginWizard().login(id, pass, "Android",
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

    suspend fun logout(): Boolean {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            return suspendCoroutine {
                session.signOut(true, object : MatrixCallback<Unit> {
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
                Glide.get(appContext).clearDiskCache()

                // Also clear cache (Logs, etc...)
                deleteAllFiles(appContext.cacheDir)
            }
        }
    }

}