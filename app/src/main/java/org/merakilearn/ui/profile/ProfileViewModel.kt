package org.merakilearn.ui.profile

import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.BuildConfig
import org.merakilearn.R
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.LoginResponse
import org.navgurukul.commonui.platform.*
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.playground.repo.PlaygroundRepository
import java.io.File
import java.io.IOException
import kotlin.math.min

class ProfileViewModel(
    private val playgroundRepository: PlaygroundRepository,
    private val stringProvider: StringProvider,
    private val userRepo: UserRepo
) : BaseViewModel<ProfileViewEvents, ProfileViewState>(ProfileViewState()) {

    private val user: LoginResponse.User

    init {
        val appVersionText =
            stringProvider.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        user = userRepo.getCurrentUser()!!
        setState {
            copy(appVersionText = appVersionText, userName = user.name, userEmail = user.email, profilePic = user.profilePicture)
        }

        viewModelScope.launch {
            updateFiles()
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
        }
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
        val savedFiles = playgroundRepository.fetchSavedFiles().toMutableList().let {
            if (it.size > 4) {
                showAllButtonText = stringProvider.getString(if (expanded) R.string.collapse else R.string.view_all)
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
            playgroundRepository.deleteFile(file)
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
}

data class ProfileViewState(
    val savedFiles: List<File> = emptyList(),
    val appVersionText: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val profilePic: String? = null,
    val showAllButtonText: String? = null,
    val filedExpanded: Boolean = false,
    val showProgressBar: Boolean = false,
    val showUpdateProfile: Boolean = false,
    val showEditProfileLayout: Boolean = false
) : ViewState

sealed class ProfileViewEvents : ViewEvents {
    class ShowToast(val text: String, val finishActivity: Boolean = false) : ProfileViewEvents()
    class ShareText(val text: String) : ProfileViewEvents()
    object RestartApp : ProfileViewEvents()
}

sealed class ProfileViewActions : ViewModelAction {
    object ExpandFileList : ProfileViewActions()
    object LogOut : ProfileViewActions()
    object EditProfileClicked : ProfileViewActions()
    class DeleteFile(val file: File) : ProfileViewActions()
    class ShareFile(val file: File) : ProfileViewActions()
    class UpdateProfile(val userName: String, val email: String) : ProfileViewActions()
}