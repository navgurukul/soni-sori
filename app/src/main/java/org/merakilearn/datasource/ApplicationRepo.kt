package org.merakilearn.datasource

import android.app.Application
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.LoginRequest
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase

class ApplicationRepo(
    private val applicationApi: SaralApi,
    private val application: Application,
    private val courseDb: CoursesDatabase,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend fun loginWithAuthToken(authToken: String?): LoginResponse? {
        return try {
            val isFakeLogin = AppUtils.isFakeLogin(application)
            val loginRequest = LoginRequest(authToken)
            if (isFakeLogin) {
                loginRequest.id = AppUtils.getFakeLoginResponseId(application)
            }
            val response = applicationApi.initLoginAsync(loginRequest)
            authenticationRepository.login(response.user.chatId, response.user.chatPassword)
            if (isFakeLogin)
                AppUtils.resetFakeLogin(application)
            AppUtils.saveUserLoginResponse(response, application)
            response
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun performFakeSignUp(): LoginResponse? {
        return try {
            val response = applicationApi.initFakeSignUpAsync()
            authenticationRepository.login(response.user.chatId, response.user.chatPassword)
            AppUtils.saveFakeLoginResponse(response, application)
            response
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun logOut(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                courseDb.clearAllTables()
                PreferenceManager.getDefaultSharedPreferences(application).edit().clear().apply()
                authenticationRepository.logout()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
}