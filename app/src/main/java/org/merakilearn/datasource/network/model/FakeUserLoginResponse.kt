package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FakeUserLoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("room_id")
val roomId: String
) : Serializable {
    data class User(
        @SerializedName("chat_id")
        val chatId: String,
        @SerializedName("chat_password")
        val chatPassword: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
    ) : Serializable
}