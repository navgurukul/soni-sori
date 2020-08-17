package org.navgurukul.learn.courses.network

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise

interface NetworkDataProvider {
    suspend fun fetchCourses(): List<Course>

    suspend fun fetchExerciseForCourse(courseId: String): List<Exercise>
}
