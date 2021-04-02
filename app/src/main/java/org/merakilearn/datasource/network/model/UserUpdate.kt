package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("name")
    val name: String,
    @SerializedName("referrer")
    val referrer: String?
)