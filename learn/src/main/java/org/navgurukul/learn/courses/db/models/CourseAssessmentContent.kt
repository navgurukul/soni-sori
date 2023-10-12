package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "course_assessment", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class CourseAssessmentContent(
    @Json(name = "id")
    @ColumnInfo(name = "id")
    override val id: String = "",

    @Json(name = "course_id")
    override var courseId: String = "",

    @Json(name = "course_name")
    override var courseName: String?,

    @Json(name = "sequence_num")
    override var sequenceNumber: Int?,

    var lang: String = "en",

    @Json(name = "content_type")
    override var courseContentType: CourseContentType = CourseContentType.assessment,

    @Json(name = "progress")
    override var courseContentProgress: CourseContentProgress?,

    @Json(name = "content")
    val content: List<BaseCourseContent>,

    @Embedded(prefix = "assess_")
    @Json(name = "attempt_status")
    val attemptStatus: AttemptResponseStatus? = null,

    @Json(name = "assessment_type")
    val assessmentType: String? = null
    ):CourseContents



@JsonClass(generateAdapter = true)
data class AttemptResponseStatus(
    @Json(name = "selected_option")
    val selectedOption : List<Int>? = null,
    @Json(name =  "attempt_count")
    val attemptCount: Int
)