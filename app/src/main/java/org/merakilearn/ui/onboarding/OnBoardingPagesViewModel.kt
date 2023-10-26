package org.merakilearn.ui.onboarding

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.merakilearn.InstallReferrerManager
import org.merakilearn.R
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.datasource.Config.Companion.ON_BOARDING_DATA
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.LoginRepository
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.*
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import java.net.URLDecoder

class OnBoardingPagesViewModel(
    private val loginRepository: LoginRepository,
    private val stringProvider: StringProvider,
    private val installReferrerManager: InstallReferrerManager,
    private val preferences: CorePreferences,
    private val config: Config,
    private val userRepo: UserRepo,

    ) : BaseViewModel<OnBoardingPagesEvents, OnBoardingPagesViewState>(OnBoardingPagesViewState()) {

    private val _login = MutableLiveData<LoginResponseC4CA>()
    val login: LiveData<LoginResponseC4CA> = _login

    fun setLogin(loginResponseC4CA: LoginResponseC4CA) {
        _login.value = loginResponseC4CA
    }
//    val login = liveData {
//        emitSource(_login)
//    }

    companion object {
        const val PARTNER_ID = "partner_id"
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var c4caUser : LoginResponseC4CA.DataC4CA? = null
    private var user  : LoginResponse.User? = null


    fun handle(action: OnBoardingPagesAction) {
        val viewState = viewState.value!!

        when (action) {
//            is OnBoardingPagesAction.InitiateFakeSignUp -> handleFakeSignUp()
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
                checkPartner()
            } else {
                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.unable_to_sign)))
            }
        }
    }

//    private fun handleFakeSignUp() {
//        viewModelScope.launch {
//            setState { copy(isLoading = true) }
//            val fakeUserLoginResponse = loginRepository.performFakeSignUp()
//            setState { copy(isLoading = false) }
//            fakeUserLoginResponse?.let {
//                checkPartner()
//            } ?: run {
//                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.unable_to_process_request)))
//            }
//        }
//    }

    private fun checkPartner() {
        val decodeReferrer =
            URLDecoder.decode(installReferrerManager.userRepo.installReferrer ?: "", "UTF-8")
        val partnerIdPattern = Regex("[^$PARTNER_ID:]\\d+")
        val partnerNamePattern = Regex("utm_medium=\\D+utm_content")

        val partnerIdValue = partnerIdPattern.find(decodeReferrer, 0)?.value
        val partnerNameValue =
            partnerNamePattern.find(decodeReferrer)?.value?.removePrefix("utm_medium=")
                ?.removeSuffix("&utm_content")




        if (partnerIdValue != null) {

            setAnalytics(partnerIdValue, partnerNameValue)
            getPathwayForResidentialProgram()
        } else {
            _viewEvents.setValue(OnBoardingPagesEvents.OpenCourseSelection)
        }
    }


    private fun setAnalytics(partner_id: String, partner_name: String?) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setUserProperty("partner_id", partner_id)
        firebaseAnalytics.setUserProperty("partner_name", partner_name)
    }

    private fun getPathwayForResidentialProgram() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val residentialProgramPathwayIdResponse =
                loginRepository.getPathwayForResidentialProgram()
            setState { copy(isLoading = false) }

            residentialProgramPathwayIdResponse?.let {
                _viewEvents.setValue(OnBoardingPagesEvents.OpenCourseSelection)
            } ?: run { _viewEvents.setValue(OnBoardingPagesEvents.OpenCourseSelection) }
        }
    }

    fun loginC4CA(username: String, password: String) {
        viewModelScope.launch {
            val loginResponse = loginRepository.loginC4ca(username, password)
            val loginStatus = loginResponse.status
            if (loginStatus == "success"){
                c4caUser = userRepo.getCurrentC4CAUser()
                user = userRepo.getCurrentUser()
                Log.e("loginStatus", "c4caUser: $c4caUser")
                Log.e("loginStatus", "CommonUser: $user")
                Log.d("loginStatus", "Login $loginStatus $username congratulation")
//                _viewEvents.setValue(OnBoardingPagesEvents.OpenC4CAHomePage)
                _viewEvents.setValue(OnBoardingPagesEvents.ShowMainScreen)
            } else {
                Log.d("loginStatus", "Login failure $loginStatus $username Sorry for inconvinience")
                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.wrong_data)))
                _viewEvents.setValue(OnBoardingPagesEvents.ShowErrorMessage)
            }

//            if (loginResponse != null) {
//
//            } else {
//                _viewEvents.setValue(OnBoardingPagesEvents.ShowToast(stringProvider.getString(R.string.unable_to_sign)))
//            }
        }
    }
}

    sealed class OnBoardingPagesEvents : ViewEvents {
        object OpenCourseSelection : OnBoardingPagesEvents()
        data class NavigateToItem(val item: Int) : OnBoardingPagesEvents()
        data class ShowToast(val toastText: String) : OnBoardingPagesEvents()
        data class OpenHomePage(val id: Int) : OnBoardingPagesEvents()
        object OpenC4CAHomePage : OnBoardingPagesEvents()
        object OpenC4CAHomeFragment : OnBoardingPagesEvents()
        object ShowErrorMessage : OnBoardingPagesEvents()
        object ShowMainScreen : OnBoardingPagesEvents()
    }

    sealed class OnBoardingPagesAction : ViewModelAction {
        data class LoginWithAuthToken(val authToken: String) : OnBoardingPagesAction()
        data class Skip(val totalItems: Int) : OnBoardingPagesAction()
        data class Next(val currentItem: Int) : OnBoardingPagesAction()
        data class PageSelected(val currentItem: Int) : OnBoardingPagesAction()

        data class LoginC4CA(val username: String, val password: String) : OnBoardingPagesAction()
//    object InitiateFakeSignUp : OnBoardingPagesAction()
    }

    data class OnBoardingPagesViewState(
        val isLoading: Boolean = false,
        val onBoardingData: OnBoardingData? = null,
        val onBoardingTranslations: OnBoardingTranslations? = null,
        val isNavLayoutVisible: Boolean = true,
        val isLoginLayoutVisible: Boolean = false
    ) : ViewState
