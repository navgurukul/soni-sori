package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.util.toDate
import org.merakilearn.util.toDay
import org.merakilearn.util.toTime
import java.util.*

data class Classes(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("course_id")
    val courseId: Any?,
    @SerializedName("description")
    val description: String,
    @SerializedName("end_time")
    val endTime: Date,
    @SerializedName("enrolled")
    val enrolled: Boolean,
    @SerializedName("exercise_id")
    val exerciseId: Any?,
    @SerializedName("facilitator")
    val facilitator: Facilitator?,
    @SerializedName("facilitator_id")
    val facilitatorId: Int?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("rules")
    val rules: Rules?,
    @SerializedName("start_time")
    val startTime: Date,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("video_id")
    val videoId: String?,
    @SerializedName("meet_link")
    val meetLink: String
) {
    data class Facilitator(
        @SerializedName("name")
        val name: String?
    )

    data class Rules(
        @SerializedName("en")
        val en: String?
    )
}

fun Classes.sanitizedType(): String = type.replace("_", " ").capitalizeWords()
fun Classes.timeRange(): String = "${startTime.toTime()} - ${endTime.toTime()}"
fun Classes.displayableLanguage(): String = Locale(lang).getDisplayLanguage(Locale.ENGLISH)