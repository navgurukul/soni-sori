package org.merakilearn.ui.onboarding

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.*
import org.navgurukul.commonui.platform.*
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.network.SaralCoursesApi
import java.net.URLDecoder

class OnBoardingViewModel(
    onBoardingActivityArgs: OnBoardingActivityArgs?,
    private val userRepo: UserRepo,
    private val config: Config,
    private val corePreferences: CorePreferences,
    private val installReferrerManager: InstallReferrerManager,
    private val saralCoursesApi: SaralCoursesApi
) :
    BaseViewModel<OnBoardingViewEvents, OnBoardingViewState>(OnBoardingViewState()) {

    private var cardViewsAdded = false

    init {
        viewModelScope.launch {
            val selectedLanguage = corePreferences.selectedLanguage
            val data = config.getObjectifiedValue<OnBoardingData>(Config.ON_BOARDING_DATA)
            val translations = data?.onBoardingTranslations?.get(selectedLanguage)

            if (data != null && translations != null) {
                setState {
                    copy(
                        onBoardingData = data,
                        onBoardingTranslations = translations
                    )
                }
                if (!cardViewsAdded) {
                    loadPathways()
                }
            }
        }
    }

    fun loadPathways() {
        viewModelScope.launch {
            try {
                val pathwaysResponse = saralCoursesApi.getPathways()
                val pathways = pathwaysResponse.pathways
                setState { copy(onBoardingData = createOnBoardingData(pathways)) }
                cardViewsAdded = true
            } catch (e: Exception) {

            }
        }
    }
    private fun createOnBoardingData(pathways: List<Pathway>): OnBoardingData {
        val onBoardingPathwayList = pathways.map { pathway ->
            OnBoardingPathwayData(OnBoardingImage(pathway.logo), pathway.id, pathway.name)
        }
        return OnBoardingData(emptyList(), onBoardingPathwayList, emptyMap())
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