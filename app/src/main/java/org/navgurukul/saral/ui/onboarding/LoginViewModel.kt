package org.navgurukul.saral.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.navgurukul.saral.datasource.ApplicationRepo

class LoginViewModel(private val applicationRepo:ApplicationRepo) : ViewModel() {

    fun initLoginServer(authToken: String?) = liveData {
        emit(applicationRepo.initLoginServer(authToken))
    }

    fun initFakeSignUp() = liveData {
        emit(applicationRepo.initFakeSignUp())
    }
}