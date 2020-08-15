package org.navgurukul.learn.courses.db.models

/**
 * @version 1
 * @author: Banty
 * @date: 08/15/2020
 */
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// This class all model classes which represent our DB entities.

// Table which stores all the courses
@Entity(tableName = "saral_courses")
data class Course(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "logo_url")
    val logoUrl: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "sequence_number")
    val sequence: String
)

@Entity(tableName = "course_content")
data class Exercise(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

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

    @ColumnInfo(name = "child_exercises")
    val childExercises: List<Exercise>
)