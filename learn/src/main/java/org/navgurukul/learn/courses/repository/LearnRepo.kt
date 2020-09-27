package org.navgurukul.learn.courses.repository

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathWayCourseContainer
import org.navgurukul.learn.util.LearnUtils

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val application: Application,
    private val database: CoursesDatabase
) {

    fun getCoursesData(forceUpdate: Boolean): LiveData<List<Course>?> {
        val courseDao = database.courseDao()
        return object : NetworkBoundResource<List<Course>, PathWayCourseContainer>() {
            override suspend fun saveCallResult(data: PathWayCourseContainer) {
                data.courses?.map {
                    it.pathwayId = data.id
                    it.pathwayName = data.name
                }?.toList()
                courseDao.insertCourses(data.courses!!)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                return (forceUpdate && LearnUtils.isOnline(application))
                        || (LearnUtils.isOnline(application) && (data == null || data.isEmpty()))
            }

            override suspend fun makeApiCallAsync(): Deferred<PathWayCourseContainer> {
                return courseApi.getDefaultPathwayCoursesAsync(LearnUtils.getAuthToken(application))
            }

            override suspend fun loadFromDb(): List<Course>? {
                val data = courseDao.getAllCoursesDirect()
                data?.forEachIndexed { index, course ->
                    course.number = (index + 1)
                }
                return data
            }
        }.asLiveData()
    }

    fun getCoursesExerciseData(courseId: String): LiveData<List<Exercise>?> {
        val exerciseDao = database.exerciseDao()
        return object : NetworkBoundResource<List<Exercise>, CourseExerciseContainer>() {
            override suspend fun saveCallResult(data: CourseExerciseContainer) {
                val mappedData = data.course?.exercises?.map {
                    it.apply { this?.courseId = courseId }
                }?.toList()
                exerciseDao.insertExercise(mappedData)
            }

            override fun shouldFetch(data: List<Exercise>?): Boolean {
                return LearnUtils.isOnline(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<CourseExerciseContainer> {
                return courseApi.getExercisesAsync(courseId)
            }

            override suspend fun loadFromDb(): List<Exercise>? {
                val data = exerciseDao.getAllExercisesForCourseDirect(courseId)
                parseData(data)
                return data
            }
        }.asLiveData()
    }


    private fun parseData(data: List<Exercise>) {
        data.forEachIndexed { index, exercise ->
            exercise.number = (index + 1)
        }
    }

    suspend fun getExerciseSlugData(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean
    ): LiveData<List<Exercise>> {
        val exerciseDao = database.exerciseDao()
        if (forceUpdate && LearnUtils.isOnline(application)) {
            val result = courseApi.getExercisesAsync(courseId).await()
            val mappedData = result.course?.exercises?.map {
                it.apply { this?.courseId = courseId }
            }?.toList()
            exerciseDao.insertExercise(mappedData)
        }
        return exerciseDao.getExerciseById(exerciseId)
    }


    fun fetchCourseExerciseDataWithCourse(courseId: String): LiveData<List<Course>?> {
        val courseDao = database.courseDao()
        val exerciseDao = database.exerciseDao()
        return object : NetworkBoundResource<List<Course>, CourseExerciseContainer>() {
            override suspend fun saveCallResult(data: CourseExerciseContainer) {
                val mappedData = data.course?.exercises?.map {
                    it.apply { this?.courseId = courseId }
                }?.toList()
                courseDao.insertCourse(data.course)
                exerciseDao.insertExercise(mappedData)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                return LearnUtils.isOnline(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<CourseExerciseContainer> {
                return courseApi.getExercisesAsync(courseId)
            }

            override suspend fun loadFromDb(): List<Course>? {
                return courseDao.getCourseById(courseId)
            }
        }.asLiveData()
    }

    suspend fun saveCourseExerciseCurrent(currentStudy: CurrentStudy) {
        val currentStudyDao = database.currentStudyDao()
        currentStudyDao.saveCourseExerciseCurrent(currentStudy)
    }

    suspend fun fetchCurrentStudyForCourse(courseId: String): List<CurrentStudy> {
        val currentStudyDao = database.currentStudyDao()
        return currentStudyDao.getCurrentStudyForCourse(courseId)
    }


}
