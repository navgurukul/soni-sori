package org.merakilearn.datasource.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserUpdateName(

    @Json(name = "name")
    val name:String,

    @Json (name = "profile_picture")
    val profile_picture:String?,
//
//    @Json(name = "contact")
//    val contact:String


)
