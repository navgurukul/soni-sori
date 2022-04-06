package org.navgurukul.learn.courses.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.*
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.util.LearnUtils
import java.util.ArrayList

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val application: Application,
    private val database: CoursesDatabase
) {

    private val _batchFlow = MutableSharedFlow<List<Batch>?>(replay = 1)
    var lastUpdatedBatches: List<Batch>? = null


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
                val result = courseApi.getCourseContentAsync(courseId, lang)
                val mappedData = result.course.courseContents.map {
                    it.courseId = courseId
                    it.courseName = result.course.name
                    if(it.courseContentType == CourseContentType.exercise) {
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
                    exerciseDao.insertExerciseAsync(mappedData.filter { it.courseContentType == CourseContentType.exercise } as List<CourseExerciseContent>)
                }catch (ex: Exception) {
                    Log.d("LearnRepo", "exercise dao insert exception = ${ex.printStackTrace()}")
                }
                try {
                    classDao.insertClassAsync(mappedData.filter { it.courseContentType == CourseContentType.class_topic } as List<CourseClassContent>)
                }catch (ex: Exception) {
                    Log.d("LearnRepo", "class dao insert exception = ${ex.printStackTrace()}")
                }
            } catch (ex: Exception) {
                Log.d("LearnRepo", "getCourseContentById exception = ${ex.printStackTrace()}")
            }
        }


        return when(courseContentType){
            CourseContentType.exercise -> exerciseDao.getExerciseById(contentId, lang).asFlow()
            CourseContentType.class_topic -> classDao.getClassById(contentId, lang).asFlow()
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
                val classes = classDao.getAllClassesForCourse(courseId, language)
                val contentList = ArrayList<CourseContents>()
                contentList.addAll(exercises)
                contentList.addAll(classes)
                contentList.sortBy { it.sequenceNumber }

                if (contentList.isNotEmpty()) {
                    course.apply { this.courseContents = contentList }
                } else {
                    null
                }
            },
            shouldFetch = { LearnUtils.isOnline(application) && (it == null || it.courseContents.isEmpty()) },
            makeApiCallAsync = { courseApi.getCourseContentAsync(courseId, language) },
            saveCallResult = { courseExerciseContainer ->
                val course = courseExerciseContainer.course.apply {
                    this.pathwayId = pathwayId
                }
                val lang =
                    if (language in course.supportedLanguages) language else course.supportedLanguages[0]
                val mappedData = course.courseContents.map {
                    it.apply {
                        this.courseId = courseId
                        if(this.courseContentType == CourseContentType.exercise) {
                            (this as CourseExerciseContent).lang = lang
                        }else if(this.courseContentType == CourseContentType.class_topic){
                            (this as CourseClassContent).lang = lang
                        }
                    }
                }.toList()

                courseDao.insertCourse(course)

                try {
                    exerciseDao.insertExercise(mappedData.filter { it.courseContentType == CourseContentType.exercise } as List<CourseExerciseContent>)
                }catch (ex: Exception) {
                }
                try {
                    classDao.insertClass(mappedData.filter { it.courseContentType == CourseContentType.class_topic } as List<CourseClassContent>)
                }catch (ex: Exception) {
                }
            }
        )
    }

    suspend fun saveCourseContentCurrent(currentStudy: CurrentStudy) {
        val currentStudyDao = database.currentStudyDao()
        currentStudyDao.saveCourseContentCurrent(currentStudy)
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

    suspend fun getRevisionClasses(classId: String): List<CourseClassContent> {
            return courseApi.getRevisionClasses(classId)
    }

    suspend fun checkedStudentEnrolment(pathwayId: Int): EnrolResponse {
        return courseApi.checkedStudentEnrolment(pathwayId)
    }

    suspend fun getBatchesListByPathway(pathwayId: Int): List<Batch> {
        return courseApi.getBatchesAsync(pathwayId)
    }


    suspend fun getUpcomingClass(pathwayId: Int): List<CourseClassContent> {
        return courseApi.getUpcomingClass(pathwayId)
    }

    suspend fun enrollToClass(classId: Int, enrolled: Boolean): Boolean {
        return try {
            if (enrolled) {
                courseApi.logOutToClassAsync(classId)
                updateEnrollStatus(classId, false)
            } else {
                courseApi.enrollToClassAsync(classId, mutableMapOf())
                updateEnrollStatus(classId, true)
            }
        } catch (ex: Exception) {
            false
        }
    }

    private suspend fun updateEnrollStatus(classId: Int, enrolled: Boolean): Boolean {
        val classes = lastUpdatedBatches ?: return true
        classes.forEachIndexed loop@ { index, classItem ->
            if (classId == classItem.id) {
                val updatedClass = classItem.copy(enrolled = enrolled)
                lastUpdatedBatches = mutableListOf(*classes.toTypedArray()).apply {
                    this[index] = updatedClass
                }
                _batchFlow.emit(lastUpdatedBatches)

                return@loop
            }
        }
        return true
    }

}
