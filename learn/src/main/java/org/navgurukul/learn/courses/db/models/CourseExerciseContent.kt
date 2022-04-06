package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "course_exercise", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class CourseExerciseContent(
    @Json(name = "id")
    @ColumnInfo(name = "id")
    override val id: String = "",

    @Json(name = "course_id")
    override var courseId: String = "",

    @Json(name = "course_name")
    override var courseName: String?,

    @Json(name = "sequence_num")
    override var sequenceNumber: Int?,

    @Json(name = "content_type")
    override var courseContentType: CourseContentType,

    @Json(name = "progress")
    override var courseContentProgress: CourseContentProgress?,

    @Json(name = "name")
    val name: String = "",

    @Json(name = "description")
    val description: String = "",

    var lang: String = "en",

    @Json(name = "content")
    val content: List<BaseCourseContent>,
) : CourseContents {
    @Ignore
    var number: Int? = 0
}
