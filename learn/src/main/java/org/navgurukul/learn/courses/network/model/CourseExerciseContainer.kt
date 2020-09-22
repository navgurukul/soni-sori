package org.navgurukul.learn.courses.network.model


import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.models.Exercise

data class CourseExerciseContainer(
    @SerializedName("course")
    var course: Course? = Course()
) {
    data class Course(
        @SerializedName("days_to_complete")
        var daysToComplete: String? = String(),
        @SerializedName("exercises")
        var exercises: List<Exercise?>? = listOf(),
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("logo")
        var logo: String? = "",
        @SerializedName("name")
        var name: String? = "",
        @SerializedName("notes")
        var notes: String? = String(),
        @SerializedName("short_description")
        var shortDescription: String? = "",
        @SerializedName("type")
        var type: String? = ""
    )
}