package org.navgurukul.learn.courses.db.typeadapters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.navgurukul.learn.courses.db.models.ExerciseSlugDetail
import java.lang.reflect.Type

@ProvidedTypeConverter
class Converters(val moshi: Moshi) {
    val type: Type = Types.newParameterizedType(
        List::class.java,
        ExerciseSlugDetail::class.java
    )
    private val adapter = moshi.adapter<List<ExerciseSlugDetail>>(type)

    @TypeConverter
    fun listToString(list: List<ExerciseSlugDetail>): String? {
        if (list.isNullOrEmpty()) return null
        return adapter.toJson(list)
    }

    @TypeConverter
    fun stringToList(stringValue: String?): List<ExerciseSlugDetail>? {
        if (stringValue.isNullOrEmpty()) return emptyList()
        return adapter.fromJson(stringValue)
    }
}