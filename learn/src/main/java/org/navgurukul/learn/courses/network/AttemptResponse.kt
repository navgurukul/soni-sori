package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttemptResponse(
    @Json(name = "attempt_status")
    val attemptStatus: AttemptStatus,
    @Json(name = "selected_option")
    val selectedOption : Int? = null,
    @Json(name = "attempt_count")
    val attemptCount : Int,
    @Json(name = "multple_choice")
    val multipleChoice : String? = null
)

@JsonClass(generateAdapter = true)
data class AttemptResponseStatus(
    @Json(name = "selected_option")
    val selectedOption : Int?,
    @Json(name =  "attempt_count")
    val attemptCount: Int
)
enum class AttemptStatus{
    NOT_ATTEMPTED,
    CORRECT,
    INCORRECT,
    PARTIALLY_CORRECT,
    PARTIALLY_INCORRECT
}