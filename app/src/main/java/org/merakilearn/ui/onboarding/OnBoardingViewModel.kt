package org.merakilearn.ui.onboarding

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.LoginResponseC4CA
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.merakilearn.datasource.network.model.PartnerDataResponse
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import java.net.URLDecoder

class OnBoardingViewModel(
    onBoardingActivityArgs: OnBoardingActivityArgs?,
    private val userRepo: UserRepo,
    private val config: Config,
    private val corePreferences: CorePreferences,
    private val installReferrerManager: InstallReferrerManager
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

    private fun checkPartner() {    // To check that partner screen is need to show or not
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
            when {
                userRepo.isUserLoggedIn() -> {
                    Log.d("ShowMainScreen", "Checking isUserLogin Details in userRepo  ${userRepo.isUserLoggedIn()}")
                    _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen())
                    Log.d("ShowMainScreen", "User is logged in already by google")
                }
                userRepo.isC4CAUserLoggedIn() -> {
                    Log.d("ShowMainScreen", "User is logged in already by c4ca userepo ${userRepo.isC4CAUserLoggedIn()}")
                    _viewEvents.setValue(OnBoardingViewEvents.ShowC4CAScreen(userRepo.isC4CAUserLoggedIn()))
                    Log.d("ShowMainScreen", "User is logged in already by c4ca")
                }
                else -> {
                    _viewEvents.setValue(OnBoardingViewEvents.ShowOnBoardingPages)
                    Log.d("ShowMainScreen", "This is fluctuating in onboardingviewmodel")
                }
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
                Log.d("ShowMainScreen", "User is logged in by using action selectCOurse")
            }

            is OnBoardingViewActions.OpenHomeScreen -> {
                corePreferences.lastSelectedPathWayId = action.pathwayId
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen(pathwayId = action.pathwayId))
                Log.d("ShowMainScreen", "User is logged in by using action HomeScreen")
            }
            is OnBoardingViewActions.GetPartnerData -> {
                checkPartner()
            }
            is OnBoardingViewActions.NavigateNextFromPartnerDataScreen -> {
                _viewEvents.setValue(
                    OnBoardingViewEvents.ShowCourseSelectionScreen
                )

            }
        }
    }
}

sealed class OnBoardingViewEvents : ViewEvents {
    object ShowSelectLanguageFragment : OnBoardingViewEvents()
    data class ShowMainScreen(val pathwayId: Int? = null) : OnBoardingViewEvents()
    data class ShowC4CAScreen(val isC4CAUser : Boolean) : OnBoardingViewEvents()
    object ShowCourseSelectionScreen : OnBoardingViewEvents()
    object ShowOnBoardingPages : OnBoardingViewEvents()
    object ShowLoginScreen : OnBoardingViewEvents()
    object ShowPartnerScreen : OnBoardingViewEvents()
    data class ShowPartnerData(val partnerData: PartnerDataResponse) : OnBoardingViewEvents()
}

sealed class OnBoardingViewActions : ViewModelAction {
    object NavigateNextFromOnBoardingScreen : OnBoardingViewActions()
    data class SelectCourse(val pathwayId: Int) : OnBoardingViewActions()
    data class OpenHomeScreen(val pathwayId: Int) : OnBoardingViewActions()
    object GetPartnerData : OnBoardingViewActions()
    object NavigateNextFromPartnerDataScreen : OnBoardingViewActions()
}


data class OnBoardingViewState(
    val onBoardingData: OnBoardingData? = null,
    val onBoardingTranslations: OnBoardingTranslations? = null
) : ViewState