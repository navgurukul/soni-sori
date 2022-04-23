package org.navgurukul.learn.courses.network.model

import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.merakilearn.core.extentions.capitalizeWords
import org.navgurukul.learn.util.toDate
import org.navgurukul.learn.util.toDay
import org.navgurukul.learn.util.toTime
import java.util.*

@JsonClass(generateAdapter = true)
data class Batch(
    @Json(name = "category_id")
    val categoryId: Int,
    @Json(name = "course_id")
    val courseId: String?,
    @Json(name = "description")
    val description: String,
    @Json(name = "end_time")
    val endTime: Date?,
    @Json(name = "enrolled")
    val enrolled: Boolean? = false,
    @Json(name = "exercise_id")
    val exerciseId: Any?,
    @Json(name = "facilitator")
    val facilitator: Facilitator?,
    @Json(name = "facilitator_id")
    val facilitatorId: Int?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "lang")
    val lang: String?,
    @Json(name = "rules")
    val rules: Rules?,
    @Json(name = "start_time")
    val startTime: Date?,
    @Json(name = "title")
    var title: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "meet_link")
    val meetLink: String?,
    @Json(name= "sub_title")
    val sub_title: String?,
    @Json(name = "pathway_id")
    val pathway_id : Int?,
    @Ignore
    @Json(name="is_Selected")
    var isSelected: Boolean = false

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
fun Batch.sanitizedType(): String = type?.replace("_", " ")?.capitalizeWords() ?: ""
fun Batch.dateRange(): String = "${startTime?.toDate()} to ${endTime?.toDate()}"
