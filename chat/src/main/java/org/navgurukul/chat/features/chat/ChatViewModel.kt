package org.navgurukul.chat.features.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import im.vector.matrix.android.api.MatrixCallback
import im.vector.matrix.android.api.auth.data.HomeServerConnectionConfig
import im.vector.matrix.android.api.auth.data.LoginFlowResult
import im.vector.matrix.android.api.auth.login.LoginWizard
import im.vector.matrix.android.api.session.Session
import org.navgurukul.chat.core.repo.ChatRepository
import timber.log.Timber

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val loginWizard: LoginWizard
        get() = chatRepository.authenticationService.getLoginWizard()

    init {
        //TODO remove this
        chatRepository.authenticationService.getLoginFlow(HomeServerConnectionConfig.Builder()
            .withHomeServerUri("https://m.navgurukul.org").build(), object: MatrixCallback<LoginFlowResult> {
            override fun onSuccess(data: LoginFlowResult) {
                loginWizard.login("t-saral", "hello123", "Android", object: MatrixCallback<Session> {
                    override fun onFailure(failure: Throwable) {
                        super.onFailure(failure)
                    }

                    override fun onSuccess(data: Session) {
                        super.onSuccess(data)
                        Timber.d("abcd")
                        Timber.d(data.sessionId)
                    }
                })
            }

            override fun onFailure(failure: Throwable) {
                super.onFailure(failure)
            }
        })
    }
}