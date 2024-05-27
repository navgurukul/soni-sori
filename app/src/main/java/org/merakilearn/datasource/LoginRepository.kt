package org.merakilearn.datasource

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.LoginRequest
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.datasource.network.model.ResidentialProgramPathwayResponse
import org.merakilearn.datasource.network.model.UsernameLoginRequest
import org.merakilearn.datasource.network.model.UsernameLoginResponse
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase

class LoginRepository(
    private val applicationApi: SaralApi,
    private val application: Application,
    private val courseDb: CoursesDatabase,
    private val userRepo: UserRepo,
    private val corePreferences: CorePreferences,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend fun loginWithAuthToken(authToken: String?): LoginResponse? {
        return try {
//            val isFakeLogin = userRepo.isFakeLogin()
//            val id = if (isFakeLogin) {
//                userRepo.getFakeLoginResponseId()
//            } else {
//                null
//            }
//            val loginRequest = LoginRequest(authToken, id = id, language = corePreferences.selectedLanguage)
            val loginRequest = LoginRequest(authToken,  language = corePreferences.selectedLanguage)
            val response = applicationApi.initLoginAsync(loginRequest)
//            if (isFakeLogin) {
//                userRepo.resetFakeLogin()
//            }
            userRepo.saveUserLoginResponse(response)
            response
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            null
        }
    }

    suspend fun loginWithUserName(userName : String, password : String) : UsernameLoginResponse {
        return try {
            val request = UsernameLoginRequest(userName, password)
            val response = applicationApi.loginWithUsername(request)
            Log.d("Username", "loginWithUserName in loginRepository: $response")
            userRepo.saveLoginUsernameResponse(response)
            response
        } catch (ex: Exception) {
            Log.d("Username", "loginWithUserName in loginRepository failed here: $ex")
            FirebaseCrashlytics.getInstance().recordException(ex)
            UsernameLoginResponse(null, "")
        }

    }



//    suspend fun performFakeSignUp(): LoginResponse? {
//        return try {
//            val loginRequest =
//                LoginRequest(null, language = corePreferences.selectedLanguage)
//            val response = applicationApi.initFakeSignUpAsync(loginRequest)
//            userRepo.saveFakeLoginResponse(response)
//            response
//        } catch (ex: Exception) {
//            FirebaseCrashlytics.getInstance().recordException(ex)
//            null
//        }
//    }

    suspend fun getPathwayForResidentialProgram(): ResidentialProgramPathwayResponse? {
        return try {
            val response = applicationApi.getResidentialProgramPathway()
            response
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
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
            FirebaseCrashlytics.getInstance().recordException(ex)
            false
        }
    }
}