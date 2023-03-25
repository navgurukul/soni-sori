package org.merakilearn.ui.profile

import android.app.AlertDialog
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.BuildConfig
import org.merakilearn.InstallReferrerManager
import org.merakilearn.R
import org.merakilearn.core.datasource.Config
import org.merakilearn.datasource.ClassesRepo
import org.merakilearn.datasource.SettingsRepo
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.Batches
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.datasource.network.model.PartnerDataResponse
import org.merakilearn.datasource.network.model.UserUpdateContainer
import org.merakilearn.ui.onboarding.OnBoardingPagesViewModel
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.playground.repo.PythonRepository
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import kotlin.math.min

class ProfileViewModel(
    private val pythonRepository: PythonRepository,
    private val stringProvider: StringProvider,
    private val userRepo: UserRepo,
    private val classesRepo: ClassesRepo,
    private val settingsRepo: SettingsRepo,
    private val config: Config,
    installReferrerManager: InstallReferrerManager
) : BaseViewModel<ProfileViewEvents, ProfileViewState>(ProfileViewState(serverUrl = settingsRepo.serverBaseUrl)) {
    private var isEnrolled: Boolean = false

    companion object {
        const val PARTNER_ID = "partner_id"
    }


    private val user: LoginResponse.User

    init {
        val appVersionText = BuildConfig.VERSION_NAME
        user = userRepo.getCurrentUser()!!
        val decodeReferrer =
            URLDecoder.decode(installReferrerManager.userRepo.installReferrer ?: "", "UTF-8")
        val partnerIdPattern = Regex("[^${OnBoardingPagesViewModel.PARTNER_ID}:]\\d+")
        val partnerIdValue = partnerIdPattern.find(decodeReferrer, 0)?.value


        setState {
            copy(
                appVersionText = appVersionText,
                userName = user.name,
                userEmail = user.email,
                profilePic = user.profilePicture
            )
        }
        if (partnerIdValue != null) {
            checkPartner(partnerIdValue)
        }

        viewModelScope.launch {
            updateFiles()
        }
        getEnrolledBatches()
        val id = user.partnerId.toString()
        if (id != null) {
            checkPartner(id)
        }

    }

    fun handle(action: ProfileViewActions) {
        when (action) {
            is ProfileViewActions.UpdateProfile -> updateProfile(action.userName, action.email)
            ProfileViewActions.ExpandFileList -> expandFilesList()
            ProfileViewActions.LogOut -> logOut()
            is ProfileViewActions.DeleteFile -> deleteFile(file = action.file)
            ProfileViewActions.EditProfileClicked -> setState {
                copy(showEditProfileLayout = true, showUpdateProfile = true)
            }
            is ProfileViewActions.ShareFile -> shareFile(action.file)
            is ProfileViewActions.UpdateServerUrlClicked -> _viewEvents.setValue(
                ProfileViewEvents.ShowUpdateServerDialog(
                    settingsRepo.serverBaseUrl
                )
            )
            is ProfileViewActions.UpdateServerUrl -> updateServerUrl(action.serverUrl)
            ProfileViewActions.ResetServerUrl -> updateServerUrl(BuildConfig.SERVER_URL)
            ProfileViewActions.PrivacyPolicyClicked -> _viewEvents.setValue(
                ProfileViewEvents.OpenUrl(
                    config.getValue(
                        Config.PRIVACY_POLICY
                    )
                )
            )
            is ProfileViewActions.ExploreOpportunityClicked -> openURL()
            is ProfileViewActions.DropOut -> dropOut(action.batchId)
            is ProfileViewActions.RefreshPage -> getEnrolledBatches()
        }
    }

    private fun dropOut(batchId: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = classesRepo.enrollToClass(batchId, true)
            setState { copy(isLoading = false) }
            if (result) {
                isEnrolled = false
                setState {
                    copy(
                        isLoading = false
                    )
                }
                getEnrolledBatches()
                _viewEvents.setValue(ProfileViewEvents.ShowToast(stringProvider.getString(R.string.log_out_class)))
            } else {
                setState { copy(isLoading = false) }
                _viewEvents.setValue(ProfileViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_drop)))
            }
        }
    }

    private fun checkPartner(partnerId: String?) {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = false) }
                if (partnerId != null) {
                    val partnerData = userRepo.getPartnerData(partnerId?.toInt())
                    _viewEvents.postValue(ProfileViewEvents.ShowPartnerData(partnerData))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateServerUrl(serverUrl: String) {
        if (!serverUrl.startsWith("http") || !serverUrl.startsWith("https")) {
            _viewEvents.setValue(ProfileViewEvents.ShowToast("A url should start with http or https"))
            return
        }
        settingsRepo.serverBaseUrl = serverUrl
        setState {
            copy(serverUrl = serverUrl)
        }
        _viewEvents.setValue(ProfileViewEvents.ShowToast("Server url set to $serverUrl. Please restart app"))
    }

    private fun openURL() {
        val decodeReferrer = URLDecoder.decode(userRepo.installReferrer ?: "", "UTF-8")
        val pattern = Regex("[^$PARTNER_ID:]\\d+")
        var url = config.getValue<String>(Config.OPPORTUNITY_URL)

        val value = pattern.find(decodeReferrer, 0)?.value

        if (value != null) {
            url = "$url?$PARTNER_ID=$value"
        }

        _viewEvents.setValue(ProfileViewEvents.OpenUrl(url))
    }

    private fun shareFile(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                file.bufferedReader().readLine().let {
                    _viewEvents.postValue(ProfileViewEvents.ShareText(it))
                }

            } catch (ex: IOException) {
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    private suspend fun updateFiles(expanded: Boolean = false) {
        var showAllButtonText: String? = null
        val savedFiles = pythonRepository.fetchSavedFiles().toMutableList().let {
            if (it.size > 4) {
                showAllButtonText =
                    stringProvider.getString(if (expanded) R.string.collapse else R.string.view_all)
            }
            if (expanded) it else it.subList(0, min(it.size, 4))
        }
        setState {
            copy(savedFiles = savedFiles, showAllButtonText = showAllButtonText)
        }
    }

    private fun updateProfile(userName: String, email: String) {
        setState {
            copy(showProgressBar = true, userName = userName, userEmail = email)
        }
        viewModelScope.launch {
            user.name = userName
            user.email = email
            val success = userRepo.updateProfile(user)
            setState {
                copy(
                    showProgressBar = false,
                    showEditProfileLayout = !success,
                    showUpdateProfile = !success
                )
            }
            val toastText =
                stringProvider.getString(if (success) R.string.profile_updated_successfully else R.string.unable_to_update)
            _viewEvents.setValue(ProfileViewEvents.ShowToast(toastText, success))
        }

    }

    private fun logOut() {
        viewModelScope.launch {
            val success = userRepo.logOut()
            if (success) {
                _viewEvents.setValue(ProfileViewEvents.RestartApp)
            } else {
                _viewEvents.setValue(ProfileViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_log_out)))
            }
        }
    }

    private fun deleteFile(file: File) {
        viewModelScope.launch {
            pythonRepository.deleteFile(file)
            updateFiles(viewState.value!!.filedExpanded)
        }
    }

    private fun expandFilesList() {
        val expanded = !viewState.value!!.filedExpanded
        setState {
            copy(filedExpanded = expanded)
        }
        viewModelScope.launch {
            updateFiles(expanded)
        }
    }

    private fun getEnrolledBatches(){
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val batches = classesRepo.getEnrolledBatches()
                batches?.let {
                    setState {
                        copy(
                            batches = it
                        )
                    }
                    if (it.isNotEmpty()) {
                        _viewEvents.postValue(ProfileViewEvents.ShowEnrolledBatches(batches))
                    }
                }
            }catch (e:Exception){
                Log.e("Exception",e.toString())
            }
        }
    }

    fun selectBatch(batches: Batches) {
        _viewEvents.postValue(ProfileViewEvents.BatchSelectClicked(batches))
    }
}

data class ProfileViewState(
    val isLoading: Boolean = false,
    val savedFiles: List<File> = emptyList(),
    val appVersionText: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val profilePic: String? = null,
    val showAllButtonText: String? = null,
    val filedExpanded: Boolean = false,
    val showProgressBar: Boolean = false,
    val showUpdateProfile: Boolean = false,
    val showEditProfileLayout: Boolean = false,
    val showServerUrl: Boolean = BuildConfig.DEBUG,
    val serverUrl: String,
    val batches: List<Batches> = arrayListOf(),
) : ViewState

sealed class ProfileViewEvents: ViewEvents {
    class ShowToast(val text: String, val finishActivity: Boolean = false): ProfileViewEvents()
    class ShareText(val text: String): ProfileViewEvents()
    class OpenUrl(val url: String): ProfileViewEvents()
    class ShowUpdateServerDialog(val serverUrl: String): ProfileViewEvents()
    object RestartApp: ProfileViewEvents()
    data class ShowEnrolledBatches(val batches: List<Batches>): ProfileViewEvents()
    data class BatchSelectClicked(val batch: Batches): ProfileViewEvents()
    data class ShowPartnerData(val partnerData: PartnerDataResponse): ProfileViewEvents()
}

sealed class ProfileViewActions: ViewModelAction {
    object ExpandFileList: ProfileViewActions()
    object LogOut: ProfileViewActions()
    object EditProfileClicked: ProfileViewActions()
    class DeleteFile(val file: File): ProfileViewActions()
    class ShareFile(val file: File): ProfileViewActions()
    class UpdateProfile(val userName: String, val email: String) : ProfileViewActions()
    object ExploreOpportunityClicked : ProfileViewActions()
    object UpdateServerUrlClicked : ProfileViewActions()
    class UpdateServerUrl(val serverUrl: String) : ProfileViewActions()
    object ResetServerUrl : ProfileViewActions()
    object PrivacyPolicyClicked : ProfileViewActions()
    object RefreshPage : ProfileViewActions()
    data class DropOut(val batchId: Int) : ProfileViewActions()
}