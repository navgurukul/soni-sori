package org.navgurukul.learn.courses.repository

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathwayCourseContainer
import org.navgurukul.learn.courses.network.networkBoundResourceFlow
import org.navgurukul.learn.util.LearnUtils
import java.lang.Exception

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val application: Application,
    private val database: CoursesDatabase
) {

    fun getPathwayData(forceUpdate: Boolean): Flow<List<Pathway>?> {
        val pathwayDao = database.pathwayDao()
        val courseDao = database.courseDao()
        return networkBoundResourceFlow(loadFromDb = {
            val data = pathwayDao.getAllPathways()
            data.map {
                it.courses = courseDao.getCoursesByPathwayId(it.id).apply {
                    forEachIndexed { index, course ->
                        course.number = (index + 1)
                    }
                }
            }
            data
        }, shouldFetch = { data ->
            (forceUpdate && LearnUtils.isOnline(application)) || (LearnUtils.isOnline(application) && (data == null || data.isEmpty()))
        }, makeApiCallAsync = {
            courseApi.getPathways(LearnUtils.getAuthToken(application))
        }, saveCallResult = { data ->
            data.pathways.forEach { pathway ->
                pathway.courses.map {
                    it.pathwayId = pathway.id
                }
                courseDao.insertCourses(pathway.courses)
            }
            pathwayDao.insertPathways(data.pathways)

        })
    }

    fun getCoursesDataByPathway(pathwayId: Int, forceUpdate: Boolean): Flow<List<Course>?> {
        val courseDao = database.courseDao()
        return networkBoundResourceFlow(loadFromDb = {
            val data = courseDao.getCoursesByPathwayId(pathwayId)
            data.forEachIndexed { index, course ->
                course.number = (index + 1)
            }
            data
        }, shouldFetch = { data ->
            (forceUpdate && LearnUtils.isOnline(application)) || (LearnUtils.isOnline(application) && (data == null || data.isEmpty()))
        }, makeApiCallAsync = {
            courseApi.getDefaultPathwayCoursesAsync(LearnUtils.getAuthToken(application))
        }, saveCallResult = { data ->
            data.courses.map {
                it.pathwayId = data.id
            }.toList()
            courseDao.insertCourses(data.courses)
        })
    }

    fun getCoursesData(forceUpdate: Boolean): LiveData<List<Course>?> {
        val courseDao = database.courseDao()
        return object : NetworkBoundResource<List<Course>, PathwayCourseContainer>() {
            override suspend fun saveCallResult(data: PathwayCourseContainer) {
                data.courses.map {
                    it.pathwayId = data.id
                }.toList()
                courseDao.insertCourses(data.courses)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                return (forceUpdate && LearnUtils.isOnline(application))
                        || (LearnUtils.isOnline(application) && (data == null || data.isEmpty()))
            }

            override suspend fun makeApiCallAsync(): PathwayCourseContainer {
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

            override suspend fun makeApiCallAsync(): CourseExerciseContainer {
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
            try {
                val result = courseApi.getExercisesAsync(courseId)
                val mappedData = result.course?.exercises?.map {
                    it.apply {
                        this?.courseId = courseId
                        this?.courseName = result.course?.name
                    }
                }?.toList()
                exerciseDao.insertExerciseAsync(mappedData)
            } catch (ex: Exception) {
            }
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

            override suspend fun makeApiCallAsync(): CourseExerciseContainer {
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
