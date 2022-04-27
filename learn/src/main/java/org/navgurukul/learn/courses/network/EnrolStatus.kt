package org.navgurukul.learn.courses.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnrolResponse(
    val message: EnrolStatus,
)

enum class EnrolStatus {
    enrolled,
    not_enrolled,
    enrolled_but_finished
}
