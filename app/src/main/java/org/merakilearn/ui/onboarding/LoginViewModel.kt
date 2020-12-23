package org.merakilearn.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.merakilearn.datasource.ApplicationRepo

class LoginViewModel(
    private val applicationRepo: ApplicationRepo
) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(applicationRepo.loginWithAuthToken(authToken))
    }

    fun logOut() = liveData {
        emit(applicationRepo.logOut())
    }

}