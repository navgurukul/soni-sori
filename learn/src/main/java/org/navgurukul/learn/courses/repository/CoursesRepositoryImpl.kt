package org.navgurukul.learn.courses.repository

import androidx.lifecycle.LiveData
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.network.NetworkDataProvider


class CoursesRepositoryImpl(
    coursesDatabase: CoursesDatabase,
    private val networkDataProvider: NetworkDataProvider
) {
    private val coursesDao = coursesDatabase.courseDao()
    private val exerciseDao = coursesDatabase.exerciseDao()

    val allCourses = coursesDao.getAllCourses()

    fun getExerciseForCourse(courseId: String): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercisesForCourse(courseId)
    }

    suspend fun fetchCoursesFromApi() {
        val courses = networkDataProvider.fetchCourses()
        coursesDao.insertCourses(courses)
    }

    suspend fun fetchExerciseForCourseFromApi(courseId: String) {
        val exercises = networkDataProvider.fetchExerciseForCourse(courseId)
        exerciseDao.insertExercise(exercises)
    }
}