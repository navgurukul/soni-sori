package org.navgurukul.learn.courses.network.model

import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LearningTrackStatus(
    @Json(name = "pathway_id")
    val pathwayId: Int,
    @Json(name = "course_id")
    val courseId: Int,
    @Json(name = "exercise_id")
    val exerciseId: Int
)


@Entity(tableName = "completed_contents_ids")
@JsonClass(generateAdapter = true)
data class CompletedContentsIds(
    @Json(name= "exercises")
    val exercises: List<Int>?,
    @Json(name= "assessments")
    val assessments : List<Int>?,
    @Json(name = "classes")
    val classes: List<Int>?
)