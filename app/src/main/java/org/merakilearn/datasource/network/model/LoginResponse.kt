package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    var token: String?,
    @SerializedName("user")
    var user: User?
) {
    data class User(
        @SerializedName("center")
        var center: Any? = null,
        @SerializedName("created_at")
        var createdAt: String? = null,
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("github_link")
        var githubLink: Any? = null,
        @SerializedName("google_user_id")
        var googleUserId: String? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("linkedin_link")
        var linkedinLink: Any? = null,
        @SerializedName("medium_link")
        var mediumLink: Any? = null,
        @SerializedName("name")
        var name: String? = "",
        @SerializedName("pathways")
        var pathways: List<Any?>? = null,
        @SerializedName("profile_picture")
        var profilePicture: String? = null,
        @SerializedName("rolesList")
        var rolesList: List<Any?>? = emptyList()
    )
}