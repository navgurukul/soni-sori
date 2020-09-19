package org.navgurukul.saral.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.datasource.network.model.LoginResponse

object AppUtils {

    private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
    private const val KEY_USER_LOGIN = "KEY_USER_LOGIN"
    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"

    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN, false)
    }

    fun saveUserLoginResponse(
        response: LoginResponse,
        application: Application
    ) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor = preferenceManager.edit()
        editor.putString(KEY_USER_RESPONSE, Gson().toJson(response))
        editor.putString(KEY_AUTH_TOKEN, response.token)
        editor.putBoolean(KEY_USER_LOGIN, true)
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

    fun getClassSchedule(classes: ClassesContainer.Classes): String {
        //todo
        val teacher = "Anjali"
        val date = DateTimeUtil.stringToDate(classes.startTime)
        val startTime = DateTimeUtil.stringToTime(classes.startTime)
        val endTime = DateTimeUtil.stringToTime(classes.endTime)
        val day = DateTimeUtil.stringToDay(classes.startTime)
        return """
            Teacher - $teacher
            Date - $date ($day),
            Time - $startTime - $endTime
        """.trimIndent()
    }

    fun getAboutClass(classes: ClassesContainer.Classes): String {
        return """
            ${classes.description}
            
            Class Type -  ${classes.classType}
        """.trimIndent()
    }

    fun getSpecialInstruction(classes: ClassesContainer.Classes): String {
        return """
            ${classes.title}
            ${classes.description}
        """.trimIndent()
    }

}