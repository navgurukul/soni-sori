package org.navgurukul.learn.courses.repository

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise

interface DataRepository {
    suspend fun fetchAvailableCourses(): List<Course>
    suspend fun saveAvailableCourses(courses: List<Course>)
    suspend fun getExerciseForCourse(courseId: String): List<Exercise>
    suspend fun saveExercise(exercises: List<Exercise>)
}

