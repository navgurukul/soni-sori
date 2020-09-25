package org.navgurukul.saral.ui.onboarding

import androidx.lifecycle.*
import org.navgurukul.saral.datasource.ApplicationRepo

class LoginViewModel(private val applicationRepo:ApplicationRepo) : ViewModel() {

    fun initLoginServer(authToken:String?) = liveData {
        emit(applicationRepo.initLoginServer(authToken))
    }
}