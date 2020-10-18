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
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class WelcomeViewModel(
    private val applicationRepo: ApplicationRepo,
    private val stringProvider: StringProvider,
    private val activeSessionHolder: ActiveSessionHolder
) : BaseViewModel<WelcomeViewEvents, WelcomeViewState>(WelcomeViewState()) {

    val idToken = MutableLiveData<String?>()

    val loginResult = idToken.switchMap {
        liveData { emit(applicationRepo.initLoginServer(it)) }
    }

    fun handle(action: WelcomeViewActions) {
        when (action) {
            is WelcomeViewActions.InitiateFakeSignUp -> handleFakeSignUp()
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
                _viewEvents.setValue(WelcomeViewEvents.ShowToast(stringProvider.getString(R.string.please_login)))
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
    class ShowToast(val toastText: String) : WelcomeViewEvents()
}

sealed class WelcomeViewActions : ViewEvents {
    object InitiateFakeSignUp : WelcomeViewActions()
}

data class WelcomeViewState(
    var isLoading: Boolean = false,
    val initialSyncProgress: InitialSyncProgressService.Status.Progressing? = null
) : ViewState {

}
