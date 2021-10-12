package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "course_exercise", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class Exercise(
    @Json(name = "content")
    val content: List<BaseCourseContent>,
    @Json(name = "course_id")
    var courseId: String = "",
    @Json(name = "id")
    @ColumnInfo(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "slug")
    //TODO remove this and other non used fields
    var slug: String?,
    var lang: String = "en",

    @Json(name = "courseName")
    var courseName: String?,
    @Json(name = "type")
    var exerciseType: ExerciseType = ExerciseType.TEXT,
    @Json(name = "progress")
    var exerciseProgress: ExerciseProgress?
) : Serializable {
    @Ignore
    var number: Int? = 0
}

enum class ExerciseType{
CODE, TEXT, QUESTION
}

enum class ExerciseProgress{
NOT_STARTED, IN_PROGRESS, COMPLETED
}