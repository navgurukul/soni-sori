package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PathwayData(
    val id: Int,
    val logo: String,
    val name: String
) : Serializable