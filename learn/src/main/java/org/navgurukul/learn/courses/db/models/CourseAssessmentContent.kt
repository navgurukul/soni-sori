package org.navgurukul.learn.courses.db.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseAssessmentContent(
    @Json(name = "id")
    override val id: String = "",

    @Json(name = "course_id")
    override var courseId: String = "",

    @Json(name = "course_name")
    override var courseName: String?,

    @Json(name = "sequence_num")
    override var sequenceNumber: Int?,

    @Json(name = "exercise_id")
    var exerciseId: String?,

    @Json(name = "content_type")
    override var courseContentType: CourseContentType = CourseContentType.assessment,

    @Json(name = "progress")
    override var courseContentProgress: CourseContentProgress?,

    @Json(name = "exercise_name")
    var exerciseName: String?,

    @Json(name = "content")
    val content: List<BaseCourseContent>,

    ):CourseContents
