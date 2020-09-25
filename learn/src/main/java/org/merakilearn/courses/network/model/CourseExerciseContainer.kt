package org.merakilearn.courses.network.model


import com.google.gson.annotations.SerializedName
import org.merakilearn.courses.db.models.Course

data class CourseExerciseContainer(
    @SerializedName("course")
    var course: Course? = Course()
)