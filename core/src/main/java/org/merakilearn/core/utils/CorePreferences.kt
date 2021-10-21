package org.merakilearn.core.utils

import android.content.SharedPreferences
import androidx.core.content.edit

class CorePreferences(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val SELECTED_LANGUAGE = "selected_language"
        const val LAST_SELECTED_PATHWAY_ID = "last_selected_pathway_id"
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

    var lastSelectedPathWayId: Int
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_SELECTED_PATHWAY_ID, value)
            }
        }
        get() {
            return sharedPreferences.getInt(LAST_SELECTED_PATHWAY_ID, -1)
        }
}