package org.navgurukul.learn.courses.network.retrofit

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.network.NetworkDataProvider

class RetrofitDataProvider(private val apiClient: SaralCoursesApi) : NetworkDataProvider {

    override suspend fun fetchCourses(): List<Course> {
        val courses = apiClient.getCourses().await()
        return courses.availableCourses
    }

    override suspend fun fetchExerciseForCourse(courseId: String): List<Exercise> {
        val exercises = apiClient.getExercises(courseId).await()
        return exercises.data
    }
}
