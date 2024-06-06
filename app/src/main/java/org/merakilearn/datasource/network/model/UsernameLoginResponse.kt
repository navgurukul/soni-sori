package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class UsernameLoginResponse(
    @Json(name = "error")
    val error : Boolean,
    @Json(name = "user")
    val student: StudentInfo,
    @Json(name = "token")
    val token: String
){
    @JsonClass(generateAdapter = true)
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
        val createdAt : String,
        @Json(name = "rolesList")
        val rolesList : List<String>,
        @Json(name = "flag")
        val flag : String?,
    ): Serializable

}



@JsonClass(generateAdapter = true)
data class UsernameLoginRequest(
    @Json(name = "user_name")
    val userName: String,

    @Json(name = "password")
    val password: String
): Serializable

@JsonClass(generateAdapter = true)
data class UserLoginErrorBlock(
    @Json(name = "error")
    val error : Boolean,
    @Json(name = "message")
    val message :String,
    @Json(name = "code")
    val code : Int
)


sealed class UsernameLoginResponses {
    data class UsernameLoginResponse(val error: Boolean, val response: UsernameLoginResponse?) : UsernameLoginResponses()
    data class UserLoginError(val error: Boolean, val response: UserLoginErrorBlock) : UsernameLoginResponses()
}