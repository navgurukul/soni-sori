package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class UserUpdate(
    @SerializedName("name")
    val name: String,
    @SerializedName("partner_id")
    val partnerId: String?
)