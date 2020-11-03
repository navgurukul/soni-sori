package org.merakilearn.datasource

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.BuildConfig
import org.merakilearn.R
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.*
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Course
import timber.log.Timber

class ApplicationRepo(
    private val applicationApi: SaralApi,
    private val application: Application,
    private val courseDb: CoursesDatabase,
    private val authenticationRepository: AuthenticationRepository
) {

    suspend fun loginWithAuthToken(authToken: String?): Boolean {
        return try {
            val isFakeLogin = AppUtils.isFakeLogin(application)
            val loginRequest = LoginRequest(authToken)
            if (isFakeLogin) {
                loginRequest.id = AppUtils.getFakeLoginResponseId(application)
            }
            val response = applicationApi.initLoginAsync(loginRequest)
            AppUtils.saveUserLoginResponse(response, application)
            authenticationRepository.login(response.user.chatId, response.user.chatPassword)
            if (isFakeLogin)
                AppUtils.resetFakeLogin(application)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    fun fetchWhereYouLeftData(): LiveData<List<Course>?> {
        TODO()
    }

    suspend fun fetchOtherCourseData(): List<Course>? {
        return try {
            val response = applicationApi.getRecommendedCourseAsync(AppUtils.getAuthToken(application))
            response.forEachIndexed { index, course ->
                course.number = (index + 1)
            }
            response
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchOtherCourseData: ")
            mutableListOf()
        }
    }

    suspend fun fetchUpcomingClassData(langCode: String?): List<Classes>? {
        return try {
            val response =
                applicationApi.getUpComingClassesAsync(AppUtils.getAuthToken(application), langCode)
            response.classes
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            mutableListOf()
        }

    }

    suspend fun fetchMyClassData(): List<MyClass> {
        return try {
            val response = applicationApi.getMyClassesAsync(AppUtils.getAuthToken(application))
            response
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            mutableListOf()
        }

    }

    suspend fun fetchClassData(classId: Int): Classes? {
        return try {
            val response = applicationApi.fetchClassDataAsync(
                AppUtils.getAuthToken(application),
                classId
            )
            response
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            null
        }
    }

    suspend fun enrollToClass(classId: Int, enrolled: Boolean): Boolean {
        return try {
            if (enrolled) {
                applicationApi.logOutToClassAsync(AppUtils.getAuthToken(application), classId)
            } else {
                applicationApi.enrollToClassAsync(
                    AppUtils.getAuthToken(application), classId,
                    mutableMapOf()
                )
            }
            true
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "enrollToClass: ")
            false
        }
    }

    suspend fun performFakeSignUp(): LoginResponse? {
        return try {
            val response = applicationApi.initFakeSignUpAsync()
            AppUtils.saveFakeLoginResponse(response, application)
            authenticationRepository.login(response.user.chatId, response.user.chatPassword)
            response
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun updateProfile(user: LoginResponse.User): Boolean {
        return try {
            val response = applicationApi.initUserUpdateAsync(
                AppUtils.getAuthToken(application),
                UserUpdate(user.name)
            )
            AppUtils.saveUserResponse(response.user ,application)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
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

    companion object {
        private const val TAG = "ApplicationRepo"
    }
}