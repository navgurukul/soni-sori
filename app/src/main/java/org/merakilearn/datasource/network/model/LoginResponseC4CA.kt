package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseC4CA(
    @Json(name = "data")
    val `data`: DataC4CA? = null,
    @Json(name = "status")
    val status: String
)

@JsonClass(generateAdapter = true)
data class DataC4CA(
    @Json(name = "flag")
    val flag: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "login_id")
    val login_id: String,
    @Json(name = "password")
    val password: String,
    @Json(name = "teacher_id")
    val teacher_id: Int,
    @Json(name = "team_name")
    val team_name: String,
    @Json(name = "team_size")
    val team_size: Int,
    @Json(name = "token")
    val token: String
)