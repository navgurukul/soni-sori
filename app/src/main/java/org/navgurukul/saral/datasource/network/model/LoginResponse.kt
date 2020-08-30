package org.navgurukul.saral.datasource.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("jwt")
    var jwt: String?,
    @SerializedName("user")
    var user: User?
):Serializable {
    data class User(
        @SerializedName("center")
        var center: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("github_link")
        var githubLink: String?,
        @SerializedName("google_user_id")
        var googleUserId: String?,
        @SerializedName("id")
        var id: String?,
        @SerializedName("isAdmin")
        var isAdmin: Boolean?,
        @SerializedName("isAlumni")
        var isAlumni: Boolean?,
        @SerializedName("isFacilitator")
        var isFacilitator: Boolean?,
        @SerializedName("linkedin_link")
        var linkedinLink: String?,
        @SerializedName("medium_link")
        var mediumLink: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("profile_picture")
        var profilePicture: String?
    ):Serializable
}