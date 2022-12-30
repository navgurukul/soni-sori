package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerDataResponse(
    @Json(name = "name")
    val name: String?= null,
    @Json(name = "website_link")
    val websiteLink: String?= null,
    @Json(name = "description")
    val description: String?= null,
    @Json(name = "logo")
    val logo: String?= null
)