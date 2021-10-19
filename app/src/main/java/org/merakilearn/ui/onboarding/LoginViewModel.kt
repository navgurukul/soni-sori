package org.merakilearn.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.merakilearn.datasource.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(loginRepository.loginWithAuthToken(authToken))
    }

    fun logOut() = liveData {
        emit(loginRepository.logOut())
    }

}