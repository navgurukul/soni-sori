package org.navgurukul.saral.datasource.network.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    var token: String?,
    @SerializedName("user")
    var user: User?
) {
    data class User(
        @SerializedName("center")
        var center: Any?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("github_link")
        var githubLink: Any?,
        @SerializedName("google_user_id")
        var googleUserId: String?,
        @SerializedName("id")
        var id: String?,
        @SerializedName("linkedin_link")
        var linkedinLink: Any?,
        @SerializedName("medium_link")
        var mediumLink: Any?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("pathways")
        var pathways: List<Any?>?,
        @SerializedName("profile_picture")
        var profilePicture: String?,
        @SerializedName("rolesList")
        var rolesList: List<Any?>?
    )
}