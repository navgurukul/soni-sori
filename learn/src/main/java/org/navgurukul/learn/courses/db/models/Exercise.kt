package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "course_exercise")
data class Exercise(
    @SerializedName("content")
    var content: MutableList<ExerciseSlugDetail>? = mutableListOf(),
    @SerializedName("course_id")
    var courseId: String? = "",
    @SerializedName("github_link")
    var githubLink: String? = "",
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("parent_exercise_id")
    var parentExerciseId: String? = "",
    @SerializedName("review_type")
    var reviewType: String? = "",
    @SerializedName("sequence_num")
    var sequenceNum: String? = String(),
    @SerializedName("slug")
    var slug: String? = "",
    @SerializedName("solution")
    var solution: String? = String(),
    @SerializedName("submission_type")
    var submissionType: String? = String(),
    @Ignore
    var number: Int? = 0,

    var courseName: String? = ""
) : Serializable {
    data class ExerciseSlugDetail(
        @SerializedName("type")
        var type: String? = "",
        @SerializedName("value")
        var value: Any? = null
    ) : Serializable
}