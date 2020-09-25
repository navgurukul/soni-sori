package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("name")
    var name: String? = ""
)