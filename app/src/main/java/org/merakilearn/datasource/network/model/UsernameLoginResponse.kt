package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsernameLoginResponse(
    @Json(name = "student")
    val student: StudentInfo?,
    @Json(name = "token")
    val token: String
){
    data class StudentInfo (
        @Json(name = "id")
        var id: Int,
        @Json(name = "user_name")
        val userName: String,
        @Json(name = "name")
        var name: String,
        @Json(name = "partner_id")
        val partnerId: Int,
        @Json(name = "created_at")
        val createdAt : String
    )
}



@JsonClass(generateAdapter = true)
data class UsernameLoginRequest(
    @Json(name = "user_name")
    val userName: String,

    @Json(name = "password")
    val password: String
)

@JsonClass(generateAdapter = true)
data class UserLoginErrorBlock(
    @Json(name = "error")
    val error : Boolean,
    @Json(name = "message")
    val message :String,
    @Json(name = "code")
    val code : Int
)