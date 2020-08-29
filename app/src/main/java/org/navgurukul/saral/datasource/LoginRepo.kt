package org.navgurukul.saral.datasource

import android.app.Application
import org.navgurukul.learn.courses.network.SaralLoginApi
import org.navgurukul.saral.datasource.network.model.LoginRequest
import org.navgurukul.saral.util.AppUtils

class LoginRepo(
    private val loginApi: SaralLoginApi,
    private val application: Application
) {

    suspend fun initLoginServer(authToken: String?): Boolean {
        try {
            val req = loginApi.initLoginAsync(LoginRequest(authToken))
            val response = req.await()
            AppUtils.saveUserLoginResponse(response,application)
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }
}