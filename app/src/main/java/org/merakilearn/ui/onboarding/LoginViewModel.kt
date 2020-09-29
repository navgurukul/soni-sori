package org.merakilearn.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.network.model.LoginResponse

class LoginViewModel(private val applicationRepo: ApplicationRepo) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(applicationRepo.initLoginServer(authToken))
    }

    fun initFakeSignUp() = liveData {
        emit(applicationRepo.performFakeSignUp())
    }

    fun updateProfile(user: LoginResponse.User) = liveData {
        emit(applicationRepo.updateProfile(user))
    }

    fun logOut() = liveData {
        emit(applicationRepo.logOut())
    }
}