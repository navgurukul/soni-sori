package org.merakilearn.datasource

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.merakilearn.core.extentions.jsonify
import org.merakilearn.core.extentions.objectify
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.*
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase

class UserRepo(
    private val saralApi: SaralApi,
    private val preferences: SharedPreferences,
    private val courseDb: CoursesDatabase,
    private val authenticationRepository: AuthenticationRepository
) {

    companion object {
        private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
        private const val KEY_USER_C4CA_RESPONSE = "KEY_USER_C4CA_RESPONSE"
//        private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"
//        private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_INSTALL_REFERRER = "KEY_INSTALL_REFERRER"
        private const val KEY_INSTALL_REFERRER_FETCHED = "KEY_INSTALL_REFERRER_FETCHED"
        private const val KEY_INSTALL_REFERRER_UPLOADED = "KEY_INSTALL_REFERRER_UPLOADED"
        private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"
        private const val KEY_USER_C4CA_LOGIN = "KEY_USER_C4CA_LOGIN"
        private const val IS_FOR_C4CA = "IS_FOR_C4CA"
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
        Log.d("ShowMainScreen", "Chekcing isUserLogin Details $KEY_USER_LOGIN")
    }

    fun isC4CAUserLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_USER_C4CA_LOGIN, false)
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

    fun getCurrentC4CAUser(): LoginResponseC4CA.DataC4CA? {
        val userLoginResponseString = preferences.getString(KEY_USER_C4CA_RESPONSE, null)
        return try {
            return if (userLoginResponseString.isNullOrEmpty()) {
                null
            } else {
                userLoginResponseString.objectify()
            }
        } catch (e : Exception){
            throw IllegalStateException("Current c4ca user is null")
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
//    fun getAuthTokenC4CA() = "Bearer ${preferences.getString(KEY_AUTH_TOKEN_C4CA, null)}"

    private fun saveUserResponse(user: LoginResponse.User) {
        preferences.edit {
            putString(KEY_USER_RESPONSE, user.jsonify())
        }
    }

    private fun saveUserResponseC4CA(user: LoginResponseC4CA.DataC4CA) {
        preferences.edit {
            putString(KEY_USER_C4CA_RESPONSE, user.jsonify())
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

    fun saveC4CAUserLoginResponse(
        response: LoginResponseC4CA,
    ) {
        response.data?.let { saveUserResponseC4CA(it) }
        preferences.edit {
            putString(KEY_AUTH_TOKEN, response.data?.token)
            putBoolean(KEY_USER_C4CA_LOGIN, true)
            putString(IS_FOR_C4CA, response.data?.flag)
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
            throw ex
        }
    }
}