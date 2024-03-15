package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCompletedPortion(
    @Json(name = "total_completed_portion")
    val totalCompletedPortion : Int,
    @Json(name = "pathway") val pathway: List<PathwayData>
)

@JsonClass(generateAdapter = true)
data class CertificateResponse(
    @Json(name = "url")
    val url : String
)

@JsonClass(generateAdapter = true)
data class PathwayData(
    @Json(name = "course_id")
    val courseId: Int,
    @Json(name = "completed_portion")
    val completedPortion: Int
)

