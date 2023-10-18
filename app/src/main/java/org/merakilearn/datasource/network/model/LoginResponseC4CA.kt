package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseC4CA(
    val `data`: DataC4CA,
    val status: String
)

@JsonClass(generateAdapter = true)
data class DataC4CA(
    val flag: String,
    val id: Int,
    val login_id: String,
    val password: String,
    val teacher_id: Int,
    val team_name: String,
    val team_size: Int,
    val token: String
)