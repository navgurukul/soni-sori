package org.navgurukul.learn.courses.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.navgurukul.learn.courses.db.models.Course

@JsonClass(generateAdapter = true)
data class PathwayCourseContainer(
    @Json(name = "id")
    val id: Int,
    @Json(name = "courses")
    val courses: List<Course>
)