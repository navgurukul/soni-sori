package org.navgurukul.typingguru.utils

import android.content.Context
import android.content.SharedPreferences

object TypingGuruPreferenceManager {
    private const val PREF_NAME = "typing-guru"
    private var mSharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    private val editor: SharedPreferences.Editor
        private get() = mSharedPreferences!!.edit()

    fun setWebViewDisplayStatus(isShown: Boolean) {
        val editor = editor
        editor.putBoolean("isShown", isShown)
        editor.commit()
    }

    fun iWebViewShown(): Boolean {
        return mSharedPreferences!!.getBoolean("isShown", false)
    }
}