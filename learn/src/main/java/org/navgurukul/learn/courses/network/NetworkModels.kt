package org.navgurukul.learn.courses.network

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise


// These models represent the api responses
data class CoursesResponseContainer(
    val enrolledCourses: List<Course>,
    val availableCourses: List<Course>,
    val completedCourses: List<Course>
)

data class ExerciseResponseContainer(
    val data: List<Exercise>
)