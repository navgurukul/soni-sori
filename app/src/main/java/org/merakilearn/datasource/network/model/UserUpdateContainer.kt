package org.merakilearn.datasource.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserUpdateContainer(
    @Json(name = "user")
    val user: LoginResponse.User
)