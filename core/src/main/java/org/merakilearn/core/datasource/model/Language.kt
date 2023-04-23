package org.merakilearn.core.datasource.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Language(
    @Json(name = "code")
    val code: String?,
    @Json(name = "label")
    val label: String? = null
)