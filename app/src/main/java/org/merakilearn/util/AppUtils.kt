package org.merakilearn.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import org.merakilearn.datasource.network.model.LoginResponse

object AppUtils {

    private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
    private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"
    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"

    private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
    private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"

    private const val KEY_AVAIL_LANGUAGE = "KEY_AVAIL_LANGUAGE"

    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN, false)
    }

    fun isFakeLogin(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_IS_FAKE_LOGIN, false)
    }

    fun getCurrentUser(application: Context): LoginResponse.User? {
        val userLoginResponseString = PreferenceManager.getDefaultSharedPreferences(application)
            .getString(KEY_USER_RESPONSE, "")
        return if (userLoginResponseString.isNullOrEmpty() && isFakeLogin(application)) {
            val fakeUserLoginResponseString =
                PreferenceManager.getDefaultSharedPreferences(application)
                    .getString(KEY_FAKE_USER_RESPONSE, "")
            Gson().fromJson(
                fakeUserLoginResponseString,
                LoginResponse.User::class.java
            )
        } else {
            if (userLoginResponseString.isNullOrEmpty()) {
                PreferenceManager.getDefaultSharedPreferences(application).edit().clear().apply()
                return null
            } else
                Gson().fromJson(userLoginResponseString, LoginResponse.User::class.java)
        }
    }

    fun getFakeLoginResponseId(application: Application): Int? {
        val fakeUserLoginResponseString =
            PreferenceManager.getDefaultSharedPreferences(application)
                .getString(KEY_FAKE_USER_RESPONSE, "")
        if (!fakeUserLoginResponseString.isNullOrEmpty()) {
            val fakeUserLoginResponse =
                Gson().fromJson(
                    fakeUserLoginResponseString,
                    LoginResponse.User::class.java
                )
            return fakeUserLoginResponse?.id?.toIntOrNull()
        }
        return 0
    }

    fun saveUserLoginResponse(
        response: LoginResponse,
        application: Application
    ) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor = preferenceManager.edit()
        editor.putString(KEY_USER_RESPONSE, Gson().toJson(response.user))
        editor.putString(KEY_AUTH_TOKEN, response.token)
        editor.putBoolean(KEY_USER_LOGIN, true)
        editor.apply()
    }

    fun saveFakeLoginResponse(
        response: LoginResponse,
        application: Application
    ) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor = preferenceManager.edit()
        editor.putString(KEY_FAKE_USER_RESPONSE, Gson().toJson(response.user))
        editor.putString(KEY_AUTH_TOKEN, response.token)
        editor.putBoolean(KEY_USER_LOGIN, true)
        editor.putBoolean(KEY_IS_FAKE_LOGIN, true)
        editor.apply()
    }

    fun saveUserResponse(user: LoginResponse.User, application: Application) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor = preferenceManager.edit()
        editor.putString(KEY_USER_RESPONSE, Gson().toJson(user))
        editor.apply()
    }

    fun resetFakeLogin(application: Application) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor = preferenceManager.edit()
        editor.putBoolean(KEY_IS_FAKE_LOGIN, false)
        editor.apply()
    }


    fun getAuthToken(context: Context): String? {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        val token = preferenceManager.getString(KEY_AUTH_TOKEN, "")
        return "Bearer $token"
    }


    fun addFragmentToActivity(
        fragmentManager: FragmentManager, fragment: Fragment, frameId: Int, tag: String?
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment, tag)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commitAllowingStateLoss()
    }
}