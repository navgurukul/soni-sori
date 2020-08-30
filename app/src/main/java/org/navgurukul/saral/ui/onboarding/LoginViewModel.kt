package org.navgurukul.saral.ui.onboarding

import androidx.lifecycle.*
import org.navgurukul.saral.datasource.LoginRepo

class LoginViewModel(private val loginRepo:LoginRepo) : ViewModel() {

    fun initLoginServer(authToken:String?) = liveData {
        emit(loginRepo.initLoginServer(authToken))
    }
}