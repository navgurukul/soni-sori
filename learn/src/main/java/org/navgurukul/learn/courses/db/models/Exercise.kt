package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "course_exercise")
@JsonClass(generateAdapter = true)
data class Exercise(
    @Json(name = "content")
    var content: MutableList<ExerciseSlugDetail>? = mutableListOf(),
    @Json(name = "course_id")
    var courseId: String? = "",
    @Json(name = "github_link")
    var githubLink: String? = "",
    @Json(name = "id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String = "",
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "parent_exercise_id")
    var parentExerciseId: String? = "",
    @Json(name = "review_type")
    var reviewType: String? = "",
    @Json(name = "sequence_num")
    var sequenceNum: String? = String(),
    @Json(name = "slug")
    var slug: String? = "",
    @Json(name = "solution")
    var solution: String? = String(),
    @Json(name = "submission_type")
    var submissionType: String? = String(),
    @Ignore
    var number: Int? = 0,

    @Json(name = "courseName")
    var courseName: String? = ""
) : Serializable