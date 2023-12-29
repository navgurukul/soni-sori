package org.navgurukul.learn.courses.network.model

import com.squareup.moshi.Json

data class EnrollStatus(
    @Json(name = "message")
    val message: String?,
    @Json(name = "code")
    val code : Int?
)
