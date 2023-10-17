package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentResult(
    @Json(name = "assessment_id")
    val assessmentId : Int,
    @Json(name = "status")
    val status : Status,
    @Json(name = "selected_option")
    val selectedOption: List<Int>,
)


enum class Status {
    Pass,
    Fail,
    Partially_Correct,
    Partially_Incorrect,
}

@JsonClass(generateAdapter = true)
data class StudentResponse(
    @Json(name = "assessment_id")
    val assessmentId : Int,
    @Json(name = "status")
    val status : Status,
    @Json(name = "selected_option")
    val selectedOption: List<Int>,
    @Json(name = "user_id")
    val userId : Int,
    @Json(name = "id")
    val id : Int
)