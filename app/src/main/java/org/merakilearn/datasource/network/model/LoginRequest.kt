package org.merakilearn.datasource.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginRequest(
    @SerializedName("idToken")
    var idToken: String?,
    @SerializedName("mode")
    var mode: String = "android",
    @SerializedName("id")
    var id: Int? = null
) : Serializable