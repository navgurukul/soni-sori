package org.navgurukul.learn.courses.network.retrofit

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.network.NetworkDataRepository

class RetrofitDataProvider : NetworkDataRepository {
    override suspend fun fetchAvailableCourses(): List<Course> {
        val courses = RetrofitClient.client.getCourses().await()
        return courses.availableCourses
    }

    override suspend fun saveAvailableCourses(courses: List<Course>) {
        // NO OP
    }

    override suspend fun getExerciseForCourse(courseId: String): List<Exercise> {
        val exercises = RetrofitClient.client.getExercises(courseId).await()
        return exercises.data
    }

    override suspend fun saveExercise(exercises: List<Exercise>) {
        // NO OP
    }

}
