package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentResult(
    @Json(name = "assessment_id")
    val assessmentId : Int,
    @Json(name = "status")
    val status : Status,
    @Json(name = "selected_multiple_option")
    val selectedMultipleOption: List<Int>
)


enum class Status {
    Pass,
    Fail,
    Partially_Correct,
    Partially_Incorrect
}

@JsonClass(generateAdapter = true)
data class StudentResponse(
    @Json(name = "id")
    val id : Int,
    @Json(name = "user_id")
    val userId : Int,
    @Json(name = "assessment_id")
    val assessmentId : Int,
    @Json(name = "status")
    val status : Status,
    @Json(name = "selected_multiple_option")
    val selectedMultipleOption: List<Int>,
    @Json(name = "attempt_count")
    val attemptCount : Int,
    @Json(name = "team_id")
    val teamId : Int? = null,

    )