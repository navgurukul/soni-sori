package org.merakilearn.datasource

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.*
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Course
import timber.log.Timber
import java.io.File

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

            // To disable crashylytics in Debug application mode
            if(BuildConfig.DEBUG) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            }else { // release or any other variant
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            }

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

    companion object {
        private const val TAG = "ApplicationRepo"
    }
}