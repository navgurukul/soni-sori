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
import org.merakilearn.datasource.network.model.UserUpdate
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
        private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"
        private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_INSTALL_REFERRER = "KEY_INSTALL_REFERRER"
        private const val KEY_INSTALL_REFERRER_FETCHED = "KEY_INSTALL_REFERRER_FETCHED"
        private const val KEY_INSTALL_REFERRER_UPLOADED = "KEY_INSTALL_REFERRER_UPLOADED"
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

    private fun isFakeLogin(): Boolean {
        return preferences.getBoolean(KEY_IS_FAKE_LOGIN, false)
    }

    fun getCurrentUser(): LoginResponse.User? {
        val userLoginResponseString = preferences.getString(KEY_USER_RESPONSE, null)
        return if (userLoginResponseString.isNullOrEmpty() && isFakeLogin()) {
            val fakeUserLoginResponseString =
                preferences.getString(KEY_FAKE_USER_RESPONSE, null)
            fakeUserLoginResponseString?.objectify()
        } else {
            return if (userLoginResponseString.isNullOrEmpty()) {
                null
            } else {
                userLoginResponseString.objectify()
            }
        }
    }

    suspend fun updateProfile(user: LoginResponse.User, referrer: String? = null): Boolean {
        return try {
            val response = saralApi.initUserUpdateAsync(
                getAuthToken(),
                UserUpdate(user.name, referrer)
            )
            saveUserResponse(response.user)
            true
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            false
        }
    }

    private fun getAuthToken() = "Bearer ${preferences.getString(KEY_AUTH_TOKEN, null)}"

    private fun saveUserResponse(user: LoginResponse.User) {
        preferences.edit {
            putString(KEY_USER_RESPONSE, user.jsonify())
        }
    }

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
}