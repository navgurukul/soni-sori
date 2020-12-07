package org.merakilearn.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.network.model.LoginResponse

class LoginViewModel(private val applicationRepo: ApplicationRepo) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(applicationRepo.loginWithAuthToken(authToken))
    }

    fun updateProfile(user: LoginResponse.User) = liveData {
        emit(applicationRepo.updateProfile(user))
    }

    fun logOut() = liveData {
        emit(applicationRepo.logOut())
    }

    fun deleteFile(first: String) = liveData {
        emit(applicationRepo.deleteFile(first))
    }

    val fetchSavedFile = liveData {
        emit(applicationRepo.fetchSavedFile())
    }
}