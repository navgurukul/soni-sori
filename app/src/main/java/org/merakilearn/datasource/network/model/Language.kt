package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("code")
    val code: String?,
    @SerializedName("label")
    val label: String?
)