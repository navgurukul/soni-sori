package org.merakilearn.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.datasource.network.model.FakeUserLoginResponse
import org.merakilearn.datasource.network.model.LoginResponse

object AppUtils {

    private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
    private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"
    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"

    private const val KEY_FAKE_USER_RESPONSE = "KEY_FAKE_USER_RESPONSE"
    private const val KEY_IS_FAKE_LOGIN = "KEY_IS_FAKE_LOGIN"


    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN, false)
    }

    fun isFakeLogin(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_IS_FAKE_LOGIN, false)
    }

    fun getCurrentUser(application: Context): LoginResponse.User {
        var userLoginResponse: LoginResponse.User = LoginResponse.User()
        val userLoginResponseString = PreferenceManager.getDefaultSharedPreferences(application)
            .getString(KEY_USER_RESPONSE, "")
        if (userLoginResponseString.isNullOrEmpty() && isFakeLogin(application)) {
            val fakeUserLoginResponseString =
                PreferenceManager.getDefaultSharedPreferences(application)
                    .getString(KEY_FAKE_USER_RESPONSE, "")
            if (!fakeUserLoginResponseString.isNullOrEmpty()) {
                val fakeUserLoginResponse =
                    Gson().fromJson(
                        fakeUserLoginResponseString,
                        FakeUserLoginResponse.User::class.java
                    )
                userLoginResponse.email = fakeUserLoginResponse.email
                userLoginResponse.name = fakeUserLoginResponse?.name
            }
        } else
            userLoginResponse =
                Gson().fromJson(userLoginResponseString, LoginResponse.User::class.java)
        return userLoginResponse
    }

    fun getFakeLoginResponseId(application: Application): Int? {
        val fakeUserLoginResponseString =
            PreferenceManager.getDefaultSharedPreferences(application)
                .getString(KEY_FAKE_USER_RESPONSE, "")
        if (!fakeUserLoginResponseString.isNullOrEmpty()) {
            val fakeUserLoginResponse =
                Gson().fromJson(
                    fakeUserLoginResponseString,
                    FakeUserLoginResponse.User::class.java
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
        response: FakeUserLoginResponse,
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
        fragmentManager: FragmentManager, fragment: Fragment
        , frameId: Int
        , tag: String?
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment, tag)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commitAllowingStateLoss()
    }


    fun changeFragment(
        fragmentManager: FragmentManager
        , fragment: Fragment, frameId: Int
        , isAddToBackStack: Boolean, tag: String?
    ) {
        val transaction = fragmentManager.beginTransaction()
        if (null != tag) {
            transaction.replace(frameId, fragment, tag)
        } else {
            transaction.replace(frameId, fragment)
        }
        if (isAddToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commitAllowingStateLoss()
    }

    fun getClassSchedule(classes: Classes?): String {
        val teacher = classes?.facilitator?.name
        val date = DateTimeUtil.stringToDate(classes?.startTime)
        val startTime = DateTimeUtil.stringToTime(classes?.startTime)
        val endTime = DateTimeUtil.stringToTime(classes?.endTime)
        val day = DateTimeUtil.stringToDay(classes?.startTime)
        return """
            Teacher - $teacher
            Date - $date ($day),
            Time - $startTime - $endTime
        """.trimIndent()
    }

    fun getAboutClass(classes: Classes?): String {
        return """
            ${classes?.description}
            Class Type -  ${classes?.type}
        """.trimIndent()
    }


}