package org.merakilearn.ui.onboarding

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.R
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.datasource.Config.Companion.ON_BOARDING_DATA
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.LoginRepository
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class OnBoardingPagesViewModel(
    private val loginRepository: LoginRepository,
    private val stringProvider: StringProvider,
    private val installReferrerManager: InstallReferrerManager,
    private val preferences: CorePreferences,
    private val config: Config
) : BaseViewModel<OnBoardingPagesEvents, OnBoardingPagesViewState>(OnBoardingPagesViewState()) {

    fun handle(action: OnBoardingPagesAction) {
        val viewState = viewState.value!!

        when (action) {
            is OnBoardingPagesAction.InitiateFakeSignUp -> handleFakeSignUp()
            is OnBoardingPagesAction.LoginWithAuthToken -> loginWithAuthToken(action.authToken)
            is OnBoardingPagesAction.Next -> {
                val nextItem = action.currentItem + 1
                _viewEvents.setValue(OnBoardingPagesEvents.NavigateToItem(nextItem))
                if (nextItem == viewState.onBoardingData!!.onBoardingPagesList.size - 1) {
                    setState {
                        copy(isLoginLayoutVisible = true, isNavLayoutVisible = false)
                    }
                }
            }
            is OnBoardingPagesAction.Skip -> {
                setState {
                    copy(isLoginLayoutVisible = true, isNavLayoutVisible = false)
                }
                _viewEvents.setValue(OnBoardingPagesEvents.NavigateToItem(action.totalItems))
            }
            is OnBoardingPagesAction.PageSelected -> {
                val isLoginLayoutVisible =
                    action.currentItem == viewState.onBoardingData!!.onBoardingPagesList.size - 1
                setState {
                    copy(
                        isLoginLayoutVisible = isLoginLayoutVisible,
                        isNavLayoutVisible = !isLoginLayoutVisible
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val selectedLanguage = preferences.selectedLanguage
            val data =
                config.getObjectifiedValue<OnBoardingData>(
                    ON_BOARDING_DATA
                )!!
            setState {
                copy(
                    onBoardingData = data,
                    onBoardingTranslations = data.onBoardingTranslations[selectedLanguage]
                )
            }
        }
    }

    private fun loginWithAuthToken(authToken: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val loginResponse = loginRepository.loginWithAuthToken(authToken)
            setState { copy(isLoading = false) }
            if (loginResponse != null) {
                installReferrerManager.checkReferrer()
                _viewEvents.setValue(OnBoardingPagesEvents.OpenCourseSelection)
            } else {
                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.unable_to_sign)))
            }
        }
    }

    private fun handleFakeSignUp() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val fakeUserLoginResponse = loginRepository.performFakeSignUp()
            setState { copy(isLoading = false) }
            fakeUserLoginResponse?.let {
                _viewEvents.setValue(OnBoardingPagesEvents.OpenCourseSelection)
            } ?: run {
                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.unable_to_process_request)))
            }
        }
    }
}

sealed class OnBoardingPagesEvents : ViewEvents {
    object OpenCourseSelection : OnBoardingPagesEvents()
    data class NavigateToItem(val item: Int) : OnBoardingPagesEvents()
    data class ShowToast(val toastText: String) : OnBoardingPagesEvents()
}

sealed class OnBoardingPagesAction : ViewModelAction {
    data class LoginWithAuthToken(val authToken: String) : OnBoardingPagesAction()
    data class Skip(val totalItems: Int) : OnBoardingPagesAction()
    data class Next(val currentItem: Int) : OnBoardingPagesAction()
    data class PageSelected(val currentItem: Int) : OnBoardingPagesAction()
    object InitiateFakeSignUp : OnBoardingPagesAction()
}

data class OnBoardingPagesViewState(
    val isLoading: Boolean = false,
    val onBoardingData: OnBoardingData? = null,
    val onBoardingTranslations: OnBoardingTranslations? = null,
    val isNavLayoutVisible: Boolean = true,
    val isLoginLayoutVisible: Boolean = false
) : ViewState
