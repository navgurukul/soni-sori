package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "idToken")
    var idToken: String?,
    @Json(name = "mode")
    var mode: String = "android",
    @Json(name = "id")
    var id: Int? = null
) : Serializable