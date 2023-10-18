package org.merakilearn.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.flow
import org.merakilearn.datasource.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(loginRepository.loginWithAuthToken(authToken))
    }

//    fun login(username: String, password: String) = liveData {
//        emit(loginRepository.login(username, password))
//    }
    fun logOut() = liveData {
        emit(loginRepository.logOut())
    }

}