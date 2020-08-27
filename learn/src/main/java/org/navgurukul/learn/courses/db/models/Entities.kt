package org.navgurukul.learn.courses.db.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.typeadapters.Converters
import java.io.Serializable

// This class all model classes which represent our DB entities.

// Table which stores all the courses
@Entity(tableName = "saral_courses")
data class Course(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "type")
    @SerializedName("type")
    val type: String?,

    @ColumnInfo(name = "logo")
    @SerializedName("logo")
    val logoUrl: String?,

    @ColumnInfo(name = "short_description")
    @SerializedName("short_description")
    val description: String?,

    @ColumnInfo(name = "sequence_number")
    @SerializedName("sequence_num")
    val sequence: String?,

    var number: String? = "0"
)


// Since one course can have multiple exercises. Course id will be a foreign key for exercise
@Entity(
    tableName = "course_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("course_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "course_id")
    var courseId: String?,

    @ColumnInfo(name = "parent_exercise_id")
    @SerializedName("parent_exercise_id")
    val parentExerciseId: Int?,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "slug")
    @SerializedName("slug")
    val slug: String?,

    @ColumnInfo(name = "sequence_num")
    @SerializedName("sequence_num")
    val sequenceNumber: Int?,

    @ColumnInfo(name = "review_type")
    @SerializedName("review_type")
    val reviewType: String?,

    @ColumnInfo(name = "github_link")
    @SerializedName("github_link")
    val githubLink: String?,

    @ColumnInfo(name = "submission_type")
    @SerializedName("submission_type")
    val submissionType: String?, // todo Banty:  confirm on the type of this field

    @TypeConverters(Converters::class)
    @ColumnInfo(name = "child_exercises")
    val childExercises: List<Exercise>?,

    var number: String? = "00"
)

@Entity(
    tableName = "exercise_slug"
)
data class ExerciseSlug(
    @SerializedName("content")
    var content: String? = "", // ### You will discuss this topic with your partner for 30 min and see what you understood:@[youtube](ImEQFprFKWA)[click for more ](http://alistapart.com/article/amoreuseful404)## Now we will discuss the questions that are given below:### Questions:1. Why do we get a 404 for error?2. try to find how can you solve that error.3. how is HTTP related to 404 error?4. how many errors can you find like this discuss with your partner.5. Discuss more option given more to know different types of errors
    @SerializedName("github_link")
    var githubLink: String? = "", // https://github.com/navgurukul/newton/tree/master/samvaad-tech-201/error(404).md
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String, // 1742
    @SerializedName("name")
    var name: String? = "", // Error(404)
    @SerializedName("parent_exercise_id")
    var parentExerciseId: String? = null, // null
    @SerializedName("review_type")
    var reviewType: String? = "", // peer
    @SerializedName("sequence_num")
    var sequenceNum: Int? = 0, // 2000
    @SerializedName("slug")
    var slug: String? = "", // samvaad-tech-201__error(404)
    @SerializedName("solution")
    var solution: String? = null, // null
    @SerializedName("submission_type")
    var submissionType: String? = null // null
)

@Entity(tableName = "user_current_study")
data class CurrentStudy(
    @PrimaryKey(autoGenerate = false)
    var courseId: String,
    var courseName: String,
    var exerciseSlugName: String,
    var exerciseName: String,
    var exerciseId: String
) : Serializable
