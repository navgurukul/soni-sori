package org.navgurukul.learn.courses.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class UpcomingClass(
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
    val facilitator: Batch.Facilitator?,
    @Json(name = "facilitator_id")
    val facilitatorId: Int?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "rules")
    val rules: Batch.Rules?,
    @Json(name = "start_time")
    val startTime: Date,
    @Json(name = "title")
    var title: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "video_id")
    val videoId: String?,
    @Json(name = "meet_link")
    val meetLink: String,
    @Json(name = "subTitle")
    val sub_title: String?,
    @Json(name = "pathway_id")
    val pathway_id : Int
)
{
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

fun UpcomingClass.displayableLanguage(): String = Locale(lang).getDisplayLanguage(Locale.ENGLISH)