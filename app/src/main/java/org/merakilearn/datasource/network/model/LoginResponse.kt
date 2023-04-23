package org.merakilearn.datasource.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token")
    var token: String,
    @Json(name = "user")
    var user: User,
    @Json(name = "room_id")
    val roomId: String?,
    @Json(name = "is_first_time")
    val is_first_time: Boolean = true
) {
    @JsonClass(generateAdapter = true)
    data class User(
        @Json(name = "center")
        var center: Any?,
        @Json(name = "created_at")
        var createdAt: String?,
        @Json(name = "email")
        var email: String,
        @Json(name = "github_link")
        var githubLink: Any?,
        @Json(name = "google_user_id")
        var googleUserId: String?,
        @Json(name = "id")
        var id: String,
        @Json(name = "linkedin_link")
        var linkedinLink: Any?,
        @Json(name = "medium_link")
        var mediumLink: Any?,
        @Json(name = "name")
        var name: String,
        @Json(name = "pathwaysV2")
        var pathways: List<Any?>?,
        @Json(name = "profile_picture")
        var profilePicture: String?,
        @Json(name = "rolesList")
        var rolesList: List<Any?>? = emptyList(),
        @Json(name = "partner_id")
        val partnerId: Int? = null,
    )
}