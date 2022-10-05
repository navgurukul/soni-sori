package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PathwayData(
    val id: Int,
    val resource:String,
    val logo: String,
    val name: String
) : Serializable