package org.navgurukul.learn.courses.db.models

import androidx.room.*
import org.navgurukul.learn.courses.db.typeadapters.Converters
import com.google.gson.annotations.SerializedName

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
    val sequence: String?
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
    val courseId: String,

    @ColumnInfo(name = "parent_exercise_id")
    val parentExerciseId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "slug")
    val slug: String,

    @ColumnInfo(name = "sequence_num")
    val sequenceNumber: Int,

    @ColumnInfo(name = "review_type")
    val reviewType: String,

    @ColumnInfo(name = "github_link")
    val githubLink: String,

    @ColumnInfo(name = "submission_type")
    val submissionType: String, // todo Banty:  confirm on the type of this field

    @TypeConverters(Converters::class)
    @ColumnInfo(name = "child_exercises")
    val childExercises: List<Exercise>
)