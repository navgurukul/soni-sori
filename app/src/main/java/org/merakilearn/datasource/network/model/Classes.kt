package org.merakilearn.datasource.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.util.toTime
import java.util.*

@JsonClass(generateAdapter = true)
data class Classes(
    @Json(name = "category_id")
    val categoryId: Int,
    @Json(name = "course_id")
    val courseId: Any?,
    @Json(name = "description")
    val description: String,
    @Json(name = "end_time")
    val endTime: Date,
    @Json(name = "enrolled")
    val enrolled: Boolean = false,
    @Json(name = "exercise_id")
    val exerciseId: Any?,
    @Json(name = "facilitator")
    val facilitator: Facilitator?,
    @Json(name = "facilitator_id")
    val facilitatorId: Int?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "rules")
    val rules: Rules?,
    @Json(name = "start_time")
    val startTime: Date,
    @Json(name = "title")
    val title: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "video_id")
    val videoId: String?,
    @Json(name = "meet_link")
    val meetLink: String
) {
    @JsonClass(generateAdapter = true)
    data class Facilitator(
        @Json(name = "name")
        val name: String?
    )
    @JsonClass(generateAdapter = true)
    data class Rules(
        @Json(name = "en")
        val en: String?
    )
}

fun Classes.sanitizedType(): String = type.replace("_", " ").capitalizeWords()
fun Classes.timeRange(): String = "${startTime.toTime()} - ${endTime.toTime()}"
fun Classes.displayableLanguage(): String = Locale(lang).getDisplayLanguage(Locale.ENGLISH)