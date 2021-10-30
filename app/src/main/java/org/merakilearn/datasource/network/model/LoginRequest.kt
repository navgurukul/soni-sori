package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "idToken")
    val idToken: String?,
    @Json(name = "mode")
    val mode: String = "android",
    @Json(name = "id")
    val id: Int? = null,
    @Json(name = "lang")
    val language: String
) : Serializable