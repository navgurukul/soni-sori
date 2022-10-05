package org.navgurukul.learn.courses.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.navgurukul.learn.courses.db.models.Pathway

@JsonClass(generateAdapter = true)
data class PathwayContainer(
    @Json(name = "pathways")
    val pathways: List<Pathway>
)