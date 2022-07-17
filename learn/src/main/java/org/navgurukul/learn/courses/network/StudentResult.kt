package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentResult(
    @Json(name = "assessment_id")
    val assessmentId : Int,
    @Json(name = "status")
    val status : Status
)


enum class Status {
    Pass,
    Fail
}