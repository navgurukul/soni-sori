package org.navgurukul.learn.courses.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.networkBoundResourceFlow
import org.navgurukul.learn.util.LearnUtils

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
            courseApi.getPathways()
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
            courseApi.getCoursesForPathway(pathwayId, "json")
        }, saveCallResult = { data ->
            data.courses.map {
                it.pathwayId = data.id
            }.toList()
            courseDao.insertCourses(data.courses)
        })
    }

    fun getCoursesExerciseData(courseId: String, language: String): Flow<List<Exercise>?> {
        val exerciseDao = database.exerciseDao()
        val courseDao = database.courseDao()
        return object : NetworkBoundResource<List<Exercise>, CourseExerciseContainer>() {
            override suspend fun saveCallResult(data: CourseExerciseContainer) {
                val course = data.course
                val lang =
                    if (language in course.supportedLanguages) language else course.supportedLanguages[0]
                val mappedData = course.exercises.map {
                    it.apply {
                        this.courseId = courseId
                        this.lang = lang
                    }
                }.toList()
                exerciseDao.insertExercise(mappedData)
            }

            override fun shouldFetch(data: List<Exercise>?): Boolean {
                return LearnUtils.isOnline(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): CourseExerciseContainer {

                return courseApi.getExercisesAsync(courseId, language)
            }

            override suspend fun loadFromDb(): List<Exercise> {
                val course = courseDao.course(courseId)
                val lang: String = course?.let {
                    if (language in course.supportedLanguages) language else course.supportedLanguages[0]
                } ?: language
                val data = exerciseDao.getAllExercisesForCourseDirect(courseId, lang)
                parseData(data)
                return data
            }
        }.asLiveData().asFlow()
    }


    private fun parseData(data: List<Exercise>) {
        data.forEachIndexed { index, exercise ->
            exercise.number = (index + 1)
        }
    }

    suspend fun getExerciseContents(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean,
        language: String
    ): Flow<Exercise?> {
        val exerciseDao = database.exerciseDao()
        val courseDao = database.courseDao()
        val course = withContext(Dispatchers.IO) { courseDao.course(courseId) }
        val lang: String = course?.let {
            if (language in course.supportedLanguages) language else course.supportedLanguages[0]
        } ?: language
        if (forceUpdate && LearnUtils.isOnline(application)) {
            try {
                val result = courseApi.getExercisesAsync(courseId, lang)
                val mappedData = result.course.exercises.map {
                    it.courseId = courseId
                    it.courseName = result.course.name
                    it.lang =
                        course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                            ?: language
                    it
                }.toList()
                exerciseDao.insertExerciseAsync(mappedData)
            } catch (ex: Exception) {
            }
        }

        return exerciseDao.getExerciseById(exerciseId, lang).asFlow()
    }


    fun fetchCourseExerciseDataWithCourse(courseId: String, language: String): Flow<Course?> {
        val courseDao = database.courseDao()
        val exerciseDao = database.exerciseDao()
        return networkBoundResourceFlow(
            loadFromDb = {
                val course = courseDao.getCourseById(courseId)
                val exercises = exerciseDao.getAllExercisesForCourse(courseId, language)
                if (exercises.isNotEmpty()) {
                    course.apply { this.exercises = exercises }
                } else {
                    null
                }
            },
            shouldFetch = { LearnUtils.isOnline(application) && (it == null || it.exercises.isEmpty()) },
            makeApiCallAsync = { courseApi.getExercisesAsync(courseId, language) },
            saveCallResult = { courseExerciseContainer ->
                val course = courseExerciseContainer.course
                val lang =
                    if (language in course.supportedLanguages) language else course.supportedLanguages[0]
                val mappedData = course.exercises.map {
                    it.apply {
                        this.courseId = courseId
                        this.lang = lang
                    }
                }.toList()
                courseDao.insertCourse(course)
                exerciseDao.insertExercise(mappedData)
            }
        )
    }

    suspend fun saveCourseExerciseCurrent(currentStudy: CurrentStudy) {
        val currentStudyDao = database.currentStudyDao()
        currentStudyDao.saveCourseExerciseCurrent(currentStudy)
    }

    suspend fun markCourseExerciseCompleted(exerciseId: String) {
        val exerciseDao = database.exerciseDao()
        exerciseDao.markCourseExerciseCompleted(
            ExerciseProgress.COMPLETED.name,
            exerciseId
        )
    }

    suspend fun fetchCurrentStudyForCourse(courseId: String): CurrentStudy? {
        val currentStudyDao = database.currentStudyDao()
        return currentStudyDao.getCurrentStudyForCourse(courseId)
    }


}
