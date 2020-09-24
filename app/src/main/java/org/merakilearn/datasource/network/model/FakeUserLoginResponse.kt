package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FakeUserLoginResponse(
    @SerializedName("token")
    var token: String? = "",
    @SerializedName("user")
    var user: User? = User()
) : Serializable {
    data class User(
        @SerializedName("chat_id")
        var chatId: String? = "",
        @SerializedName("chat_password")
        var chatPassword: String? = "",
        @SerializedName("created_at")
        var createdAt: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("name")
        var name: String? = ""
    ) : Serializable
}