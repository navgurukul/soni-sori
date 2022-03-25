package org.navgurukul.learn.courses.db.typeadapters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.merakilearn.core.datasource.model.Language
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.Facilitator
import java.lang.reflect.Type
import java.util.*

@ProvidedTypeConverter
class Converters(val moshi: Moshi) {
    private val exerciseDetailListType: Type = Types.newParameterizedType(
        List::class.java,
        BaseCourseContent::class.java
    )
    private val stringListType: Type = Types.newParameterizedType(
        List::class.java,
        String::class.java
    )
    private val languageListType: Type = Types.newParameterizedType(
        List::class.java,
        Language::class.java
    )
    private val facilitatorListType: Type = Types.newParameterizedType(
        List::class.java,
        Facilitator::class.java
    )
    private val exerciseAdapter = moshi.adapter<List<BaseCourseContent>>(exerciseDetailListType)
    private val stringAdapter = moshi.adapter<List<String>>(stringListType)
    private val languageAdapter = moshi.adapter<List<Language>>(languageListType)
    private val facilitatorAdapter = moshi.adapter<Facilitator>(facilitatorListType)

    @TypeConverter
    fun exerciseDetailListToString(list: List<BaseCourseContent>): String? {
        if (list.isNullOrEmpty()) return null
        return exerciseAdapter.toJson(list)
    }

    @TypeConverter
    fun stringToExerciseSlugDetailList(stringValue: String?): List<BaseCourseContent>? {
        if (stringValue.isNullOrEmpty()) return emptyList()
        return exerciseAdapter.fromJson(stringValue)
    }

    @TypeConverter
    fun stringToLanguageList(stringValue: String?): List<Language> {
        if (stringValue.isNullOrEmpty()) return emptyList()
        return languageAdapter.fromJson(stringValue) ?: emptyList()
    }

    @TypeConverter
    fun languageListToString(list: List<Language>): String {
        if (list.isNullOrEmpty()) return "[]"
        return languageAdapter.toJson(list)
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        if (list.isNullOrEmpty()) return ""
        return stringAdapter.toJson(list)
    }

    @TypeConverter
    fun stringToStringList(stringValue: String?): List<String> {
        if (stringValue.isNullOrEmpty()) return emptyList()
        return stringAdapter.fromJson(stringValue) ?: emptyList()
    }

    @TypeConverter
    fun facilitatorListToString(facilitator: Facilitator?): String {
        if (facilitator == null) return ""
        return facilitatorAdapter.toJson(facilitator)
    }

    @TypeConverter
    fun stringToFacilitatorList(stringValue: String?): Facilitator? {
        if (stringValue.isNullOrEmpty()) return null
        return facilitatorAdapter.fromJson(stringValue) ?: null
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date): Long {
        return date.time
    }
}