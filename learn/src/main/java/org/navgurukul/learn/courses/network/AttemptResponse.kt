package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttemptResponse(
    @Json(name = "attempt_status")
    val attemptStatus: AttemptStatus,
    @Json(name = "selected_option")
    val selectedMultipleOption : List<Int>? = null,
    @Json(name = "attempt_count")
    val attemptCount : Int,
    @Json(name = "slug_id")
    val assessmentId : Int,
    @Json(name = "lang")
    val lang : String,
)


enum class AttemptStatus{
    NOT_ATTEMPTED,
    CORRECT,
    INCORRECT,
    PARTIALLY_INCORRECT,
    PARTIALLY_CORRECT,
}