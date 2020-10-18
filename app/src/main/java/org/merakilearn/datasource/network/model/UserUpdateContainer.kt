package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class UserUpdateContainer(
    @SerializedName("user")
    val user: LoginResponse.User
)