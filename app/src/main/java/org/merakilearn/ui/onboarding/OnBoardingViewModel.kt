package org.merakilearn.ui.onboarding

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.R
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.merakilearn.datasource.network.model.PartnerDataResponse
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import java.net.URLDecoder

class OnBoardingViewModel(
    onBoardingActivityArgs: OnBoardingActivityArgs?,
    private val userRepo: UserRepo,
    private val config: Config,
    private val corePreferences: CorePreferences,
    private val installReferrerManager: InstallReferrerManager,
    private val stringProvider: StringProvider,
) :
    BaseViewModel<OnBoardingViewEvents, OnBoardingViewState>(OnBoardingViewState()) {

    init {
        viewModelScope.launch {
            val selectedLanguage = corePreferences.selectedLanguage
            val data =
                config.getObjectifiedValue<OnBoardingData>(
                    Config.ON_BOARDING_DATA
                )!!
            setState {
                copy(
                    onBoardingData = data,
                    onBoardingTranslations = data.onBoardingTranslations[selectedLanguage]
                )
            }
        }
    }

    private fun checkPartner() {
        viewModelScope.launch {
            try {
                val decodeReferrer =
                    URLDecoder.decode(installReferrerManager.userRepo.installReferrer ?: "", "UTF-8")
                val partnerIdPattern = Regex("[^${OnBoardingPagesViewModel.PARTNER_ID}:]\\d+")

                val partnerId = partnerIdPattern.find(decodeReferrer, 0)?.value

                if (partnerId != null) {
                    val partnerData = userRepo.getPartnerData(partnerId.trim().toInt())
                    if (partnerData.name == null || partnerData.logo == null || partnerData.description == null) {
                        _viewEvents.setValue(
                            OnBoardingViewEvents.ShowCourseSelectionScreen
                        )
                    } else {
                        _viewEvents.postValue(OnBoardingViewEvents.ShowPartnerData(partnerData))
                    }
                } else {
                    _viewEvents.setValue(
                        OnBoardingViewEvents.ShowCourseSelectionScreen
                    )
                }
            }catch (e : Exception){
                e.printStackTrace()
                _viewEvents.setValue(
                    OnBoardingViewEvents.ShowCourseSelectionScreen
                )
            }
        }
    }


    enum class Language(val code: String) {
        ENGLISH("en"), HINDI("hi")
    }

    init {
        if (onBoardingActivityArgs?.showLoginFragment == true) {
            _viewEvents.setValue(OnBoardingViewEvents.ShowLoginScreen)
        } else {
            if (userRepo.isUserLoggedIn()) {
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen())
            } else {
                _viewEvents.setValue(
                    OnBoardingViewEvents.ShowOnBoardingPages
                )
            }
        }
    }

    fun handle(action: OnBoardingViewActions) {
        when (action) {
            OnBoardingViewActions.NavigateNextFromOnBoardingScreen -> _viewEvents.setValue(
                OnBoardingViewEvents.ShowPartnerScreen
            )
            is OnBoardingViewActions.SelectCourse -> {
                corePreferences.lastSelectedPathWayId = action.pathwayId
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen(pathwayId = action.pathwayId))
            }

            is OnBoardingViewActions.OpenHomeScreen -> {
                corePreferences.lastSelectedPathWayId = action.pathwayId
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen(pathwayId = action.pathwayId))
            }
            is OnBoardingViewActions.GetPartnerData -> {
                checkPartner()
            }
            is OnBoardingViewActions.NavigateNextFromPartnerDataScreen -> {
                _viewEvents.setValue(
                    OnBoardingViewEvents.ShowCourseSelectionScreen
                )
            }
            is OnBoardingViewActions.NavigateToUsernameLoginScreen -> {
                _viewEvents.setValue(OnBoardingViewEvents.ShowUserNameLoginScreen)
            }
            is OnBoardingViewActions.BackToOnboardingPages ->{
                _viewEvents.setValue(OnBoardingViewEvents.ShowOnBoardingPages)
            }
        }
    }

    fun loginWithUsername(username: String, password: String) {
        viewModelScope.launch {
            val loginResponse = userRepo.loginWithUserName(username, password)
            loginResponse?.let { it ->
                if (it.error) {
                    when (it.errorCode) {
                        2001 -> it.message?.let{_viewEvents.setValue(OnBoardingViewEvents.ShowUseIdErrorMessage(it))}
                        2002 -> it.message?.let{_viewEvents.setValue(OnBoardingViewEvents.ShowUserPassError(it))}
                        else -> _viewEvents.setValue(OnBoardingViewEvents.ShowErrorMessage)
                    }
                } else if (it.student != null) {
                    _viewEvents.setValue(
                        OnBoardingViewEvents.ShowCourseSelectionScreen
                    )
                } else {
                    _viewEvents.setValue(OnBoardingViewEvents.ShowErrorMessage)
                }
            } ?: run {
                _viewEvents.setValue(OnBoardingViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_sign)))
            }
        }
    }

}

sealed class OnBoardingViewEvents : ViewEvents {
    object ShowSelectLanguageFragment : OnBoardingViewEvents()
    data class ShowMainScreen(val pathwayId: Int? = null) : OnBoardingViewEvents()
    object ShowCourseSelectionScreen : OnBoardingViewEvents()
    object ShowOnBoardingPages : OnBoardingViewEvents()
    object ShowLoginScreen : OnBoardingViewEvents()
    object ShowPartnerScreen : OnBoardingViewEvents()
    data class ShowPartnerData(val partnerData: PartnerDataResponse) : OnBoardingViewEvents()
    data class ShowToast(val toastText: String) : OnBoardingViewEvents()
    object ShowUserNameLoginScreen : OnBoardingViewEvents()
    object ShowErrorMessage : OnBoardingViewEvents()
    data class ShowUseIdErrorMessage(val message : String) : OnBoardingViewEvents()
    data class ShowUserPassError(val message: String) : OnBoardingViewEvents()
}

sealed class OnBoardingViewActions : ViewModelAction {
    object NavigateNextFromOnBoardingScreen : OnBoardingViewActions()
    data class SelectCourse(val pathwayId: Int) : OnBoardingViewActions()
    data class OpenHomeScreen(val pathwayId: Int) : OnBoardingViewActions()
    object GetPartnerData : OnBoardingViewActions()
    object NavigateNextFromPartnerDataScreen : OnBoardingViewActions()
    object NavigateToUsernameLoginScreen : OnBoardingViewActions()
    object BackToOnboardingPages : OnBoardingViewActions()
}


data class OnBoardingViewState(
    val onBoardingData: OnBoardingData? = null,
    val onBoardingTranslations: OnBoardingTranslations? = null
) : ViewState