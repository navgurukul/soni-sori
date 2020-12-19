package org.merakilearn.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.FileDataSource
import org.merakilearn.datasource.network.model.LoginResponse

class LoginViewModel(
    private val applicationRepo: ApplicationRepo,
    private val fileDataSource: FileDataSource
) : ViewModel() {

    private val _deleteFile = MutableLiveData<String>()

    fun initLoginServer(authToken: String?) = liveData {
        emit(applicationRepo.loginWithAuthToken(authToken))
    }

    fun updateProfile(user: LoginResponse.User) = liveData {
        emit(applicationRepo.updateProfile(user))
    }

    fun logOut() = liveData {
        emit(applicationRepo.logOut())
    }

    fun deleteFile(first: String) {
        _deleteFile.postValue(first)
    }

    val deleteFile = _deleteFile.switchMap {
        liveData {
            emit(fileDataSource.deleteFile(it))
        }
    }
    val fetchSavedFile = liveData {
        emit(fileDataSource.fetchSavedFile())
    }
}