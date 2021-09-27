package org.merakilearn.ui.onboarding

import androidx.lifecycle.*
import org.matrix.android.sdk.api.session.InitialSyncProgressService
import org.matrix.android.sdk.rx.asObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.R
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.network.model.OnBoardingPageData
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class OnBoardingViewModel(
    private val applicationRepo: ApplicationRepo,
    private val stringProvider: StringProvider,
    private val activeSessionHolder: ActiveSessionHolder,
    private val installReferrerManager: InstallReferrerManager
) : BaseViewModel<WelcomeViewEvents, WelcomeViewState>(WelcomeViewState()) {

    fun handle(action: WelcomeViewActions) {
        when (action) {
            is WelcomeViewActions.InitiateFakeSignUp -> handleFakeSignUp()
            is WelcomeViewActions.LoginWithAuthToken -> loginWithAuthToken(action.authToken)
            is WelcomeViewActions.RetrieveDataFromConfig ->getDataFromConfig(action.language)
            is WelcomeViewActions.SetPathWayData->  intialisePathWayData(action.args)

        }
    }

    private fun intialisePathWayData(args: SelectCourseFragmentArgs) {
        viewModelScope.launch{
            _viewEvents.setValue(WelcomeViewEvents.ViewPathWayData(args))
        }
    }

    private fun getDataFromConfig(language:String){

        viewModelScope.launch {
            val data=applicationRepo.retrieveDataFromConfig(language)
            if(data!=null){
                _viewEvents.setValue(WelcomeViewEvents.DisplayData(data))
            }
            else{
                _viewEvents.setValue(WelcomeViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_update)))
            }
        }
    }
    private fun loginWithAuthToken(authToken: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val loginResponse = applicationRepo.loginWithAuthToken(authToken)
            setState { copy(isLoading = false) }
            if (loginResponse != null) {
                installReferrerManager.checkReferrer()
                if (loginResponse.is_first_time) {
                    observeInitialSync()
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
                observeInitialSync()
            } ?: run {
                _viewEvents.setValue(WelcomeViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_process_request)))
            }
        }
    }

    private fun observeInitialSync() {
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
                            _viewEvents.setValue(WelcomeViewEvents.OpenCourseSelection)
                        }
                    }
                }
            }
            .disposeOnClear()
    }

}

sealed class WelcomeViewEvents : ViewEvents {
    object OpenHomeScreen: WelcomeViewEvents()
    object OpenCourseSelection:WelcomeViewEvents()
    class ShowToast(val toastText: String) : WelcomeViewEvents()
    class DisplayData( val data: OnBoardingPageData?):WelcomeViewEvents()
    class ViewPathWayData(val args:SelectCourseFragmentArgs):WelcomeViewEvents()

}

sealed class WelcomeViewActions : ViewModelAction {
    data class LoginWithAuthToken(val authToken: String) : WelcomeViewActions()

    object InitiateFakeSignUp : WelcomeViewActions()
    data class RetrieveDataFromConfig(val language: String):WelcomeViewActions()
    data class SetPathWayData(val args:SelectCourseFragmentArgs):WelcomeViewActions()
}

data class WelcomeViewState(
    val isLoading: Boolean = false,
    val initialSyncProgress: InitialSyncProgressService.Status.Progressing? = null
) : ViewState
