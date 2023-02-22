package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScratchResponse(
    @Json(name = "userId")
    val userId : Int ,
    @Json(name = "url")
    val url : String,
    @Json(name = "scratch_url")
    val scratch_url : String
)

@JsonClass(generateAdapter = true)
data class GetScratchesResponse(
    @Json(name ="userId")
    val userId: Int,
    @Json(name = "projectId")
    val projectId : String,
    @Json(name = "projectName")
    val projectName : String,
    @Json(name = "s3link")
    val s3link : String,
    @Json(name = "scratchUrl")
    val scratch_url: String,
    @Json(name = "updatedAt")
    val updateAt : String
)