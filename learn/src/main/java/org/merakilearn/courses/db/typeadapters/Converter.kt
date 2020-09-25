package org.merakilearn.courses.db.typeadapters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.merakilearn.courses.db.models.Exercise
import java.lang.reflect.Type


class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun listToString(list: List<Exercise.ExerciseSlugDetail>): String? {
            if (list.isNullOrEmpty()) return null
            val listType: Type = object : TypeToken<List<Exercise.ExerciseSlugDetail?>?>() {}.type
            return Gson().toJson(list, listType)
        }

        @TypeConverter
        @JvmStatic
        fun stringToList(stringValue: String?): List<Exercise.ExerciseSlugDetail> {
            if (stringValue.isNullOrEmpty()) return emptyList()
            val listType: Type = object : TypeToken<List<Exercise.ExerciseSlugDetail?>?>() {}.type
            return Gson().fromJson(stringValue, listType)
        }
    }
}