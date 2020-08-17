package org.navgurukul.learn.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CourseDao
import org.navgurukul.learn.courses.db.ExerciseDao
import org.navgurukul.learn.courses.network.SaralCoursesApi

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val courseDao: CourseDao,
    private val exerciseDao: ExerciseDao
) {

    val courses = courseDao.getAllCourses()

    suspend fun getCourses() {
        withContext(Dispatchers.IO) {
            val courses = courseApi.getCoursesAsync().await()
            courseDao.insertCourses(courses.availableCourses)
        }
    }
}
