package org.merakilearn.datasource.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserUpdate(
    @Json(name = "name")
    val name: String,
    @Json(name = "referrer")
    val referrer: String?
)