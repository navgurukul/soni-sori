package org.navgurukul.learn.courses.repository

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import org.navgurukul.learn.courses.db.CourseDao
import org.navgurukul.learn.courses.db.ExerciseDao
import org.navgurukul.learn.courses.db.ExerciseSlugDao
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.ExerciseSlug
import org.navgurukul.learn.courses.network.CoursesResponseContainer
import org.navgurukul.learn.courses.network.ExerciseResponseContainer
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.ui.common.Util

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val application: Application,
    private val courseDao: CourseDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseSlugDao: ExerciseSlugDao
) {

    fun getCoursesData(): LiveData<List<Course>?> {
        return object : NetworkBoundResource<List<Course>, CoursesResponseContainer>() {
            override suspend fun saveCallResult(data: CoursesResponseContainer) {
                courseDao.insertCourses(data.availableCourses)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                //if network avail && shared pref
                return Util.isConnected(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<CoursesResponseContainer> {
                return courseApi.getCoursesAsync()
            }

            override suspend fun loadFromDb(): List<Course>? {
                val data =  courseDao.getAllCoursesDirect()
                data?.forEachIndexed { index, course ->
                    course.number = index+1
                }
                return  data
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
                return Util.isConnected(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<ExerciseResponseContainer> {
                return courseApi.getExercisesAsync(courseId)
            }

            override suspend fun loadFromDb(): List<Exercise>? {
                return exerciseDao.getAllExercisesForCourseDirect(courseId)
            }
        }.asLiveData()
    }

    fun getExerciseSlugData(courseId: String, slug: String): LiveData<List<ExerciseSlug>?> {
        return object : NetworkBoundResource<List<ExerciseSlug>, ExerciseSlug>() {
            override suspend fun saveCallResult(data: ExerciseSlug) {
                exerciseSlugDao.insertExerciseSlug(data)
            }

            override fun shouldFetch(data: List<ExerciseSlug>?): Boolean {
                //if network avail && shared pref
                return Util.isConnected(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<ExerciseSlug> {
                return courseApi.getExercisesSlugAsync(courseId, slug)
            }

            override suspend fun loadFromDb(): List<ExerciseSlug>? {
                return exerciseSlugDao.getSlugForExercisesDirect(slug)
            }
        }.asLiveData()
    }
}
