package org.merakilearn.ui.onboarding

import androidx.lifecycle.*
import im.vector.matrix.android.api.session.InitialSyncProgressService
import im.vector.matrix.rx.asObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import org.merakilearn.R
import org.merakilearn.datasource.ApplicationRepo
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class WelcomeViewModel(
    private val applicationRepo: ApplicationRepo,
    private val stringProvider: StringProvider,
    private val activeSessionHolder: ActiveSessionHolder
) : BaseViewModel<WelcomeViewEvents, WelcomeViewState>(WelcomeViewState()) {

    fun handle(action: WelcomeViewActions) {
        when (action) {
            is WelcomeViewActions.InitiateFakeSignUp -> handleFakeSignUp()
            is WelcomeViewActions.LoginWithAuthToken -> loginWithAuthToken(action.authToken)
        }
    }

    private fun loginWithAuthToken(authToken: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val loginResponse = applicationRepo.loginWithAuthToken(authToken)
            setState { copy(isLoading = false) }
            if (loginResponse != null) {
                if (loginResponse.is_first_time) {
                    observeInitialSync(loginResponse.roomId)
                } else {
                    _viewEvents.setValue(WelcomeViewEvents.OpenHomeScreen)
                }
            } else {
                _viewEvents.setValue(WelcomeViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_sign)))
            }
        }
    }

    private fun handleFakeSignUp() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val fakeUserLoginResponse = applicationRepo.performFakeSignUp()
            setState { copy(isLoading = false) }
            fakeUserLoginResponse?.let {
                observeInitialSync(it.roomId)
            } ?: run {
                _viewEvents.setValue(WelcomeViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_process_request)))
            }
        }
    }

    private fun observeInitialSync(roomId: String) {
        val session = activeSessionHolder.getSafeActiveSession() ?: return

        session.getInitialSyncProgressStatus()
            .asObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { status ->
                when (status) {
                    is InitialSyncProgressService.Status.Progressing -> {
                        setState { copy(initialSyncProgress = status) }
                    }
                    is InitialSyncProgressService.Status.Idle -> {
                        setState { copy(initialSyncProgress = null) }
                        if (activeSessionHolder.getSafeActiveSession()
                                ?.hasAlreadySynced() == true
                        ) {
                            _viewEvents.setValue(WelcomeViewEvents.OpenMerakiChat(roomId))
                        }
                    }
                }
            }
            .disposeOnClear()
    }

}

sealed class WelcomeViewEvents : ViewEvents {
    class OpenMerakiChat(val roomId: String) : WelcomeViewEvents()
    object OpenHomeScreen: WelcomeViewEvents()
    class ShowToast(val toastText: String) : WelcomeViewEvents()
}

sealed class WelcomeViewActions : ViewModelAction {
    data class LoginWithAuthToken(val authToken: String) : WelcomeViewActions()

    object InitiateFakeSignUp : WelcomeViewActions()
}

data class WelcomeViewState(
    val isLoading: Boolean = false,
    val initialSyncProgress: InitialSyncProgressService.Status.Progressing? = null
) : ViewState
