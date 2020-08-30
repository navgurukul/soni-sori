package org.navgurukul.saral.util

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import org.navgurukul.saral.datasource.network.model.LoginResponse

object AppUtils {

    private const val KEY_USER_RESPONSE = "KEY_USER_RESPONSE"
    private const val KEY_USER_LOGIN= "KEY_USER_LOGIN"

    fun isUserLoggedIn(context: Context): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getBoolean(KEY_USER_LOGIN,false)
    }

    fun saveUserLoginResponse(
        response: LoginResponse,
        application: Application
    ) {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)
        val editor  = preferenceManager.edit()
        editor.putString(KEY_USER_RESPONSE,Gson().toJson(response))
        editor.putBoolean(KEY_USER_LOGIN,true)
        editor.apply()
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



}