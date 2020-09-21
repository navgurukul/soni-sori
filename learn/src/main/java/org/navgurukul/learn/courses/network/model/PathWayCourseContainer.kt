package org.navgurukul.learn.courses.network.model


import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.models.Course

data class PathWayCourseContainer(
    @SerializedName("code")
    var code: String?,
    @SerializedName("courses")
    var courses: List<Course>?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("tracking_day_of_week")
    var trackingDayOfWeek: Any?,
    @SerializedName("tracking_days_lock_before_cycle")
    var trackingDaysLockBeforeCycle: Any?,
    @SerializedName("tracking_enabled")
    var trackingEnabled: Boolean?,
    @SerializedName("tracking_frequency")
    var trackingFrequency: Any?
)