package org.navgurukul.learn.courses.network.model

import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.models.Course

data class PathwayCourseContainer(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courses")
    val courses: List<Course>
)