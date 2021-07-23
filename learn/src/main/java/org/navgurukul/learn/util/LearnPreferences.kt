package org.navgurukul.learn.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class LearnPreferences(val context: Context) {

    companion object {
        const val LAST_SELECTED_PATHWAY_ID = "last_selected_pathway_id"
        const val SELECTED_LANGUAGE = "selected_language"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
    }

    var lastSelectedPathWayId: Int
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_SELECTED_PATHWAY_ID, value)
            }
        }
        get() {
            return sharedPreferences.getInt(LAST_SELECTED_PATHWAY_ID, -1)
        }

    var selectedLanguage: String
        set(value) {
            sharedPreferences.edit {
                putString(SELECTED_LANGUAGE, value)
            }
        }
        get() {
            return sharedPreferences.getString(SELECTED_LANGUAGE, "en") ?: "en"
        }
}