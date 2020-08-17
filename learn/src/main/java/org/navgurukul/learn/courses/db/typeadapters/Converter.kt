package org.navgurukul.learn.courses.db.typeadapters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.navgurukul.learn.courses.db.models.Exercise
import java.lang.reflect.Type


class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun listToString(exerciseList: List<Exercise>): String? {
            if (exerciseList.isNullOrEmpty()) return null
            val listType: Type = object : TypeToken<List<Exercise?>?>() {}.type
            return Gson().toJson(exerciseList, listType)
        }

        @TypeConverter
        @JvmStatic
        fun stringToList(stringValue: String?): List<Exercise> {
            if (stringValue.isNullOrEmpty()) return emptyList()
            val listType: Type = object : TypeToken<List<Exercise?>?>() {}.type
            return Gson().fromJson(stringValue, listType)
        }
    }
}