package org.navgurukul.learn.courses.network.model


import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.models.Course

data class CourseExerciseContainer(
    @SerializedName("course")
    var course: Course? = Course()
)