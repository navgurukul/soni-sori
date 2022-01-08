package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "course_exercise", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class CourseExerciseContent(
    @Json(name = "content")
    val content: List<BaseCourseContent>,
    @Json(name = "course_id")
    override var courseId: String = "",
    @Json(name = "id")
    @ColumnInfo(name = "id")
    override val id: String = "",
    @Json(name = "name")
    val name: String = "",
    override var lang: String = "en",
    @Json(name = "courseName")
    override var courseName: String?,
    @Json(name = "progress")
    var exerciseProgress: ExerciseProgress?,
    override var sequenceNumber: Int,
    override var contentContentType: CourseContentType
) : CourseContents {
    @Ignore
    var number: Int? = 0
}

enum class ExerciseProgress{
NOT_STARTED, IN_PROGRESS, COMPLETED
}