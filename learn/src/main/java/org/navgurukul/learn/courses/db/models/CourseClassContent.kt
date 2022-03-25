package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity(tableName = "course_class", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class CourseClassContent(
    @Json(name = "id")
    @ColumnInfo(name = "id")
    override val id: String = "",

    @Json(name = "course_id")
    override var courseId: String = "",

    @Json(name = "courseName")
    override var courseName: String?,

    @Json(name = "sequence_num")
    override var sequenceNumber: Int,

    override var contentContentType: CourseContentType,

    override var courseContentProgress: CourseContentProgress?,

    @Json(name = "title")
    val title: String = "",

    @Json(name = "description")
    val description: String = "",

    var lang: String = "en",

    @Json(name = "facilitator")
    val facilitator: Facilitator?,

    @Json(name = "start_time")
    val startTime: Date,

    @Json(name = "end_time")
    val endTime: Date,

    @Json(name = "type")
    val type: ClassType,

    @Json(name = "meet_link")
    val meetLink: String?,

) : CourseContents {
    @Ignore
    var number: Int? = 0
}

@JsonClass(generateAdapter = true)
data class Facilitator(
    @Json(name = "name")
    val name: String?,

    @Json(name = "email")
    val email: String?,
)

enum class ClassType{
    BATCH_CLASS, REVISION_CLASS, DOUBT_CLASS
}

