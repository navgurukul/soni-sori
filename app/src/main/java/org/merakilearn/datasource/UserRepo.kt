package org.merakilearn.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.core.extentions.jsonify
import org.merakilearn.core.extentions.objectify
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.datasource.network.model.PartnerDataResponse
import org.merakilearn.datasource.network.model.UserUpdate
import org.merakilearn.datasource.network.model.UsernameLoginRequest
import org.merakilearn.datasource.network.model.UsernameLoginResponse
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.network.wrapper.BaseRepo

class UserRepo(
    private val saralApi: SaralApi,
    private val preferences: SharedPreferences,
    private val courseDb: CoursesDatabase,
    private val authenticationRepository: AuthenticationRepository
): BaseRepo() {

    companion object {
        private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
//        private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"
//        private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_INSTALL_REFERRER = "KEY_INSTALL_REFERRER"
        private const val KEY_INSTALL_REFERRER_FETCHED = "KEY_INSTALL_REFERRER_FETCHED"
        private const val KEY_INSTALL_REFERRER_UPLOADED = "KEY_INSTALL_REFERRER_UPLOADED"
        private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_USER_PASSWORD = "KEY_USER_PASSWORD"
        private const val KEY_USERID_LOGIN_RESPONSE = "KEY_USERID_LOGIN_RESPONSE"
    }

    var installReferrerFetched: Boolean
        get() = preferences.getBoolean(KEY_INSTALL_REFERRER_FETCHED, false)
        set(value) {
            preferences.edit { putBoolean(KEY_INSTALL_REFERRER_FETCHED, value) }
        }

    var installReferrerUploaded: Boolean
        get() = preferences.getBoolean(KEY_INSTALL_REFERRER_UPLOADED, false)
        set(value) {
            preferences.edit { putBoolean(KEY_INSTALL_REFERRER_UPLOADED, value) }
        }

    var installReferrer: String?
        get() = preferences.getString(KEY_INSTALL_REFERRER, null)
        set(value) {
            preferences.edit { putString(KEY_INSTALL_REFERRER, value) }
        }

//    fun isFakeLogin(): Boolean {
//        return preferences.getBoolean(KEY_IS_FAKE_LOGIN, false)
//    }

    fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_USER_LOGIN, false)
    }

//    fun getFakeLoginResponseId(): Int? {
//        val fakeUserLoginResponseString =
//            preferences.getString(KEY_FAKE_USER_RESPONSE, null)
//        if (!fakeUserLoginResponseString.isNullOrEmpty()) {
//            val fakeUserLoginResponse: LoginResponse.User? = fakeUserLoginResponseString.objectify()
//            return fakeUserLoginResponse?.id?.toIntOrNull()
//        }
//        return 0
//    }
    fun getCurrentUser(): LoginResponse.User? {
        val userLoginResponseString = preferences.getString(KEY_USER_RESPONSE, null)
        return try {
//            if (userLoginResponseString.isNullOrEmpty()) {
//                val fakeUserLoginResponseString =
//                    preferences.getString(KEY_FAKE_USER_RESPONSE, null)
//                fakeUserLoginResponseString?.objectify()
//            } else {
                return if (userLoginResponseString.isNullOrEmpty()) {
                    null
                } else {
                    userLoginResponseString.objectify()
                }
        } catch (e : Exception){
            throw IllegalStateException("Current user is null")
        }
    }


    fun getCurrentUserFromID(): UsernameLoginResponse.StudentInfo? {
        val userLoginResponseStringUsername = preferences.getString(KEY_USERID_LOGIN_RESPONSE, null)
        return try {
            if (userLoginResponseStringUsername.isNullOrEmpty()) {
                null
            } else {
                userLoginResponseStringUsername.objectify()
            }
        } catch (e : Exception){
            throw IllegalStateException("Current user is null after username login" )
        }
    }

    suspend fun updateProfile(user: LoginResponse.User, referrer: String? = null): Boolean {
        return try {
            val response = saralApi.initUserUpdateAsync(
                UserUpdate(user.name, referrer)
            )
            saveUserResponse(response.user)
            true
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            false
        }
    }

    fun getAuthToken() = "Bearer ${preferences.getString(KEY_AUTH_TOKEN, null)}"

    private fun saveUserResponse(user: LoginResponse.User) {
        preferences.edit {
            putString(KEY_USER_RESPONSE, user.jsonify())
        }
    }

    private fun saveUserResponseFromUSERId(student: UsernameLoginResponse.StudentInfo){
        preferences.edit{
            putString(KEY_USERID_LOGIN_RESPONSE, student.jsonify())
        }
    }
    fun saveUserLoginResponse(
        response: LoginResponse,
    ) {
        saveUserResponse(response.user)
        preferences.edit {
            putString(KEY_AUTH_TOKEN, response.token)
            putBoolean(KEY_USER_LOGIN, true)
        }
    }


    private fun saveLoginUsernameResponse(
        response: UsernameLoginResponse,
    ) {
        response.student?.let { saveUserResponseFromUSERId(it) }
        preferences.edit{
            putString(KEY_AUTH_TOKEN, response.token)
            putBoolean(KEY_USER_LOGIN, true)
        }
    }

//    fun saveFakeLoginResponse(
//        response: LoginResponse,
//    ) {
//        preferences.edit {
//            putString(KEY_FAKE_USER_RESPONSE, response.user.jsonify())
//            putString(KEY_AUTH_TOKEN, response.token)
//            putBoolean(KEY_USER_LOGIN, true)
//            putBoolean(KEY_IS_FAKE_LOGIN, true)
//        }
//    }

//    fun resetFakeLogin() {
//        preferences.edit {
//            putBoolean(KEY_IS_FAKE_LOGIN, false)
//        }
//    }

    suspend fun logOut(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val result = authenticationRepository.logout()
                if (result) {
                    courseDb.clearAllTables()
                    preferences.edit { clear() }
                    true
                } else {
                    false
                }
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            false
        }
    }

    suspend fun getPartnerData(partnerId: Int): PartnerDataResponse {
        return try {
            saralApi.getPartnerData(partnerId)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            null!!
        }
    }

    suspend fun loginWithUserName(userName : String, password : String) : UsernameLoginResponse? {
        return try {
            val request = UsernameLoginRequest(userName, password)
            val response = saralApi.loginWithUsername(request)
            if (!response.error){
                saveLoginUsernameResponse(response)
            }
            response
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            null
        }

    }


}