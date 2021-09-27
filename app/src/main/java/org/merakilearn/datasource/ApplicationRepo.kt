package org.merakilearn.datasource

import android.app.Application
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.core.datasource.Config
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.LoginRequest
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.datasource.network.model.OnBoardingPageData
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase

class ApplicationRepo(
    private val applicationApi: SaralApi,
    private val application: Application,
    private val courseDb: CoursesDatabase,
    private val userRepo: UserRepo,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend fun loginWithAuthToken(authToken: String?): LoginResponse? {
        return try {
            val isFakeLogin = userRepo.isFakeLogin()
            val loginRequest = LoginRequest(authToken)
            if (isFakeLogin) {
                loginRequest.id = userRepo.getFakeLoginResponseId()
            }
            val response = applicationApi.initLoginAsync(loginRequest)
            authenticationRepository.login(response.user.chatId!!, response.user.chatPassword!!)
            if (isFakeLogin)
                userRepo.resetFakeLogin()
            userRepo.saveUserLoginResponse(response)
            response
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun retrieveDataFromConfig(language:String): OnBoardingPageData? {

            val config= Config()
            config.initialise()
            val res=config.getObjectifiedValue<OnBoardingPageData>(language)
            if(res==null)
                retrieveDataFromApi()

            return res
    }
    fun retrieveDataFromApi(): OnBoardingPageData?{

        val res=
        return null
    }

    suspend fun performFakeSignUp(): LoginResponse? {
        return try {
            val response = applicationApi.initFakeSignUpAsync()
            authenticationRepository.login(response.user.chatId!!, response.user.chatPassword!!)
            userRepo.saveFakeLoginResponse(response)
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