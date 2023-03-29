package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCompletedPortion(
    @Json(name = "total_completed_portion")
    val totalCompletedPortion : Int
)

@JsonClass(generateAdapter = true)
data class CertificateResponse(
    @Json(name = "url")
    val url : String
)
