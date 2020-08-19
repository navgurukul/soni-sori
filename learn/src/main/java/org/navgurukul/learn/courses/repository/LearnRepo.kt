package org.navgurukul.learn.courses.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import org.navgurukul.learn.courses.db.CourseDao
import org.navgurukul.learn.courses.db.ExerciseDao
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.network.CoursesResponseContainer
import org.navgurukul.learn.courses.network.ExerciseResponseContainer
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.courses.network.SaralCoursesApi

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val courseDao: CourseDao,
    private val exerciseDao: ExerciseDao
) {

    fun getCoursesData(): LiveData<List<Course>?> {
        return object : NetworkBoundResource<List<Course>, CoursesResponseContainer>() {
            override suspend fun saveCallResult(data: CoursesResponseContainer) {
                courseDao.insertCourses(data.availableCourses)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                //if network avail && shared pref
                return data == null || data.isEmpty()
            }

            override suspend fun makeApiCallAsync(): Deferred<CoursesResponseContainer> {
                return courseApi.getCoursesAsync()
            }

            override suspend fun loadFromDb(): List<Course>? {
                return courseDao.getAllCoursesDirect()
            }
        }.asLiveData()
    }

    fun getCoursesExerciseData(courseId: String): LiveData<List<Exercise>?> {
        return object : NetworkBoundResource<List<Exercise>, ExerciseResponseContainer>() {
            override suspend fun saveCallResult(data: ExerciseResponseContainer) {
                val mappedData = data.data.map {
                    it.apply { this.courseId = courseId }
                }.toList()
                exerciseDao.insertExercise(mappedData)
            }

            override fun shouldFetch(data: List<Exercise>?): Boolean {
                //if network avail && shared pref
                return data == null || data.isEmpty()
            }

            override suspend fun makeApiCallAsync(): Deferred<ExerciseResponseContainer> {
                return courseApi.getExercisesAsync(courseId)
            }

            override suspend fun loadFromDb(): List<Exercise>? {
                return exerciseDao.getAllExercisesForCourseDirect(courseId)
            }
        }.asLiveData()
    }
}
