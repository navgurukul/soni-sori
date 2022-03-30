package org.navgurukul.learn.courses.repository

import android.app.Application
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.SaralCoursesApi
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
            data.map { it.courses = courseDao.getCoursesByPathwayId(it.id) }
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
                courseDao.deleteAllCourses()
                courseDao.insertCourses(pathway.courses)
            }
            pathwayDao.insertPathways(data.pathways)

        })
    }

    fun getCoursesDataByPathway(pathwayId: Int, forceUpdate: Boolean): Flow<List<Course>?> {
        val courseDao = database.courseDao()
        return networkBoundResourceFlow(loadFromDb = {
            courseDao.getCoursesByPathwayId(pathwayId)
        }, shouldFetch = { data ->
            (forceUpdate && LearnUtils.isOnline(application)) || (LearnUtils.isOnline(application) && (data == null || data.isEmpty()))
        }, makeApiCallAsync = {
            courseApi.getCoursesForPathway(pathwayId, "json")
        }, saveCallResult = { data ->
            data.courses.map {
                it.pathwayId = data.id
            }.toList()
            courseDao.deleteAllCourses()
            courseDao.insertCourses(data.courses)
        })
    }

    suspend fun getCourseContentById(
        contentId: String,
        courseId: String,
        courseContentType: CourseContentType,
        forceUpdate: Boolean,
        language: String
    ): Flow<CourseContents?> {
        val exerciseDao = database.exerciseDao()
        val courseDao = database.courseDao()
        val classDao = database.classDao()
        val course = withContext(Dispatchers.IO) { courseDao.course(courseId) }
        val lang: String = course?.let {
            if (language in course.supportedLanguages) language else course.supportedLanguages[0]
        } ?: language
        if (forceUpdate && LearnUtils.isOnline(application)) {
            try {
                val result = courseApi.getExercisesAsync(courseId, lang)
                val mappedData = result.course.courseContents.map {
                    it.courseId = courseId
                    it.courseName = result.course.name
                    if(it.contentContentType == CourseContentType.EXERCISE) {
                        it as CourseExerciseContent
                        it.lang =
                            course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                                ?: language
                    }else{
                        it as CourseClassContent
                        it.lang =
                            course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                                ?: language
                    }
                    it
                }.toList()

                try {
                    exerciseDao.insertExerciseAsync(mappedData.filter { it.contentContentType == CourseContentType.EXERCISE } as List<CourseExerciseContent>)
                }catch (ex: Exception) {
                }
                try {
                    classDao.insertClassAsync(mappedData.filter { it.contentContentType == CourseContentType.CLASS_TOPIC } as List<CourseClassContent>)
                }catch (ex: Exception) {
                }
            } catch (ex: Exception) {
            }
        }


        return when(courseContentType){
            CourseContentType.EXERCISE -> exerciseDao.getExerciseById(contentId, lang).asFlow()
            CourseContentType.CLASS_TOPIC -> classDao.getClassById(contentId, lang).asFlow()
            else -> exerciseDao.getExerciseById(contentId, lang).asFlow()
        }
    }


    fun fetchCourseContentDataWithCourse(courseId: String, pathwayId: Int, language: String): Flow<Course?> {
        val courseDao = database.courseDao()
        val exerciseDao = database.exerciseDao()
        val classDao = database.classDao()
        return networkBoundResourceFlow(
            loadFromDb = {
                val course = courseDao.getCourseById(courseId)
                val exercises = exerciseDao.getAllExercisesForCourse(courseId, language)
                if (exercises.isNotEmpty()) {
                    course.apply { this.courseContents = exercises }
                } else {
                    null
                }
            },
            shouldFetch = { LearnUtils.isOnline(application) && (it == null || it.courseContents.isEmpty()) },
            makeApiCallAsync = { courseApi.getExercisesAsync(courseId, language) },
            saveCallResult = { courseExerciseContainer ->
                val course = courseExerciseContainer.course.apply {
                    this.pathwayId = pathwayId
                }
                val lang =
                    if (language in course.supportedLanguages) language else course.supportedLanguages[0]
                val mappedData = course.courseContents.map {
                    it.apply {
                        this.courseId = courseId
                        if(this.contentContentType == CourseContentType.EXERCISE) {
                            (this as CourseExerciseContent).lang = lang
                        }else if(this.contentContentType == CourseContentType.CLASS_TOPIC){
                            (this as CourseClassContent).lang = lang
                        }
                    }
                }.toList()

                courseDao.insertCourse(course)

                try {
                    exerciseDao.insertExercise(mappedData.filter { it.contentContentType == CourseContentType.EXERCISE } as List<CourseExerciseContent>)
                }catch (ex: Exception) {
                }
                try {
                    classDao.insertClass(mappedData.filter { it.contentContentType == CourseContentType.CLASS_TOPIC } as List<CourseClassContent>)
                }catch (ex: Exception) {
                }
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
            CourseContentProgress.COMPLETED.name,
            exerciseId
        )
    }

    suspend fun markCourseClassCompleted(classId: String) {
        val classDao = database.classDao()
        classDao.markCourseClassCompleted(
            CourseContentProgress.COMPLETED.name,
            classId
        )
    }

    suspend fun fetchCurrentStudyForCourse(courseId: String): CurrentStudy? {
        val currentStudyDao = database.currentStudyDao()
        return currentStudyDao.getCurrentStudyForCourse(courseId)
    }


}
