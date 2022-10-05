package org.merakilearn.datasource.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassesContainer(
    @Json(name = "classes")
    val classes: List<Classes>
)