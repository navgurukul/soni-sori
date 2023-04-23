package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectNameAndUrl (
    @Json(name = "project_name")
    val project_name: String,
    @Json(name = "url")
    val url: String
)

@JsonClass(generateAdapter = true)
data class UpdateSuccessS3UploadResponse(
    @Json(name="message")
    val message: String
)