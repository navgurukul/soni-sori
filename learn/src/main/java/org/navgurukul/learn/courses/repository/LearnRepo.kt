package org.navgurukul.learn.courses.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.asFlow
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CourseAssessmentContent
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.CourseContentProgress
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.courses.db.models.CourseContents
import org.navgurukul.learn.courses.db.models.CourseExerciseContent
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.network.AttemptResponse
import org.navgurukul.learn.courses.network.CertificateResponse
import org.navgurukul.learn.courses.network.EnrolResponse
import org.navgurukul.learn.courses.network.GetCompletedPortion
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.Status
import org.navgurukul.learn.courses.network.StudentResult
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.CompletedContentsIds
import org.navgurukul.learn.courses.network.networkBoundResourceFlow
import org.navgurukul.learn.courses.network.wrapper.BaseRepo
import org.navgurukul.learn.courses.network.wrapper.Resource
import org.navgurukul.learn.util.LearnUtils
import java.net.UnknownHostException

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val application: Application,
    private val database: CoursesDatabase
) : BaseRepo() {

    private val _batchFlow = MutableSharedFlow<List<Batch>?>(replay = 1)
    var lastUpdatedBatches: List<Batch>? = null
    var statusEnrolled: Resource<EnrolResponse>? = null
    lateinit var pathwayId : String
    private lateinit var selectedLang : String

    class OfflineException(message: String) : Exception(message)

    fun getPathwayData(forceUpdate: Boolean): Flow<List<Pathway>?> {
        try {
            val pathwayDao = database.pathwayDao()
            val courseDao = database.courseDao()
            return networkBoundResourceFlow(loadFromDb = {
                val data = pathwayDao.getAllPathways()
                data?.map { it.courses = courseDao.getCoursesByPathwayId(it.id) }
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
        } catch (e : UnknownHostException){
            Log.e("ERROR", "UnknownHostException occurred: ${e.message}")
            Toast.makeText(application, "No network connection", Toast.LENGTH_SHORT).show()
            null!!
        } catch (e : Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null!!
        }

    }


    fun getCoursesDataByPathway(pathwayId: Int, forceUpdate: Boolean): Flow<List<Course>?> {
        val courseDao = database.courseDao()
        return try {networkBoundResourceFlow(
                loadFromDb = {
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
        }catch (e : UnknownHostException){
            Log.e("ERROR", "UnknownHostException occurred: ${e.message}")
            Toast.makeText(application, "No network connection", Toast.LENGTH_SHORT).show()
            null!!
        } catch (e: Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            null!!
        }
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
        val assessmentDao = database.assessmentDao()
        val course = withContext(Dispatchers.IO) { courseDao.course(courseId) }
        selectedLang = course?.let {
            if (language in course.supportedLanguages) language else course.supportedLanguages[0]
        } ?: language
        if (forceUpdate && LearnUtils.isOnline(application)) {
            try {
                val result = courseApi.getCourseContentAsync(courseId, selectedLang)
                val mappedData = result.course.courseContents.map {
                    it.courseId = courseId
                    it.courseName = result.course.name
                    if(it.courseContentType == CourseContentType.exercise) {
                        it as CourseExerciseContent
                        it.lang =
                            course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                                ?: language
                    }else if (it.courseContentType == CourseContentType.class_topic){
                        it as CourseClassContent
                        it.lang =
                            course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                                ?: language
                    }else{
                        it as CourseAssessmentContent
                        it.lang =
                            course?.let { if (language in course.supportedLanguages) language else course.supportedLanguages[0] }
                                ?:language
                        if(it.attemptStatus?.selectedOption != null)
                            it.courseContentProgress = CourseContentProgress.COMPLETED
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
                try {
                    assessmentDao.insertAssessmentAsync(mappedData.filter { it.courseContentType == CourseContentType.assessment } as List<CourseAssessmentContent?>)
                }catch (ex: Exception){
                    Log.d("LearnRepo", "assessment dao insert exception = ${ex.printStackTrace()}")
                }
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
                Log.d("LearnRepo", "getCourseContentById exception = ${ex.printStackTrace()}")
            }
        }

        return when(courseContentType){
            CourseContentType.exercise -> exerciseDao.getExerciseById(contentId, selectedLang).asFlow()
            CourseContentType.class_topic -> classDao.getClassById(contentId, selectedLang).asFlow()
            CourseContentType.assessment -> assessmentDao.getAssessmentById(contentId, selectedLang).asFlow()
            else -> exerciseDao.getExerciseById(contentId, selectedLang).asFlow()
        }
    }


    fun fetchCourseContentDataWithCourse(courseId: String, pathwayId: Int, language: String): Flow<Course?> {
        val courseDao = database.courseDao()
        val exerciseDao = database.exerciseDao()
        val classDao = database.classDao()
        val assessmentDao = database.assessmentDao()

        return try {
            networkBoundResourceFlow(
                loadFromDb = {
                    val course = courseDao.getCourseById(courseId)
                    val exercises = exerciseDao.getAllExercisesForCourse(courseId, language)
                    val classes = classDao.getAllClassesForCourse(courseId, language)
                    val assessments = assessmentDao.getAllAssessmentForCourse(courseId, language)
                    val contentList = ArrayList<CourseContents>()
                    contentList.addAll(exercises)
                    contentList.addAll(classes)
                    contentList.addAll(assessments)
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
                            if (this.courseContentType == CourseContentType.exercise) {
                                (this as CourseExerciseContent).lang = lang
                            } else if (this.courseContentType == CourseContentType.class_topic) {
                                (this as CourseClassContent).lang = lang
                            } else if (this.courseContentType == CourseContentType.assessment) {
                                (this as CourseAssessmentContent).lang = lang
                            }
                        }
                    }.toList()

                    courseDao.insertCourse(course)

                    try {
                        exerciseDao.insertExercise(mappedData.filter { it.courseContentType == CourseContentType.exercise } as List<CourseExerciseContent>)
                    } catch (ex: Exception) {
                    }
                    try {
                        classDao.insertClass(mappedData.filter { it.courseContentType == CourseContentType.class_topic } as List<CourseClassContent>)
                    } catch (ex: Exception) {
                    }
                    try {
                        assessmentDao.insertAssessment(mappedData.filter { it.courseContentType == CourseContentType.assessment } as List<CourseAssessmentContent>)
                    } catch (ex: Exception) {
                    }
                }
            )
        }catch (e : UnknownHostException){
            Log.e("ERROR", "UnknownHostException occurred: ${e.message}")
            Toast.makeText(application, "No network connection", Toast.LENGTH_SHORT).show()
            null!!
        }
        catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            null!!
        }
    }

    suspend fun getCompletedContentsIds(courseId: String): Flow<Resource<CompletedContentsIds>> {
            return flow {
                if (LearnUtils.isOnline(application)) {
                    try {
                        val contentList = safeApiCall {courseApi.getCompletedContentsIds(courseId) }
                        updateCompletedContentInDb(contentList)
                        emit(contentList)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        e.printStackTrace()
                    }
                } else {
                    println("Error Occurred!")
                }
            }


    }

    suspend fun updateCompletedContentInDb(contentList: Resource<CompletedContentsIds>) {

        val exerciseDao = database.exerciseDao()
        val classesDao = database.classDao()
        val assessmentDao = database.assessmentDao()

        exerciseDao.markExerciseCompleted(
            CourseContentProgress.COMPLETED.name,
            contentList.data?.exercises?.map { it.toString() }
        )
        classesDao.markClassCompleted(
            CourseContentProgress.COMPLETED.name,
            contentList.data?.classes?.map { it.toString() }
        )
        assessmentDao.markAssessmentCompleted(
            CourseContentProgress.COMPLETED.name,
            contentList.data?.assessments?.map { it.toString() }
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

    suspend fun  markCourseAssessmentCompleted(assessmentId: String){
        val assessmentDao = database.assessmentDao()
        assessmentDao.markCourseAssessmentCompleted(
            CourseContentProgress.COMPLETED.name,
            assessmentId
        )
    }

    suspend fun fetchCurrentStudyForCourse(courseId: String): CurrentStudy? {
        val currentStudyDao = database.currentStudyDao()
        return currentStudyDao.getCurrentStudyForCourse(courseId)
    }

    suspend fun getRevisionClasses(classId: String): Resource<List<CourseClassContent>> {
        return safeApiCall { courseApi.getRevisionClasses(classId) }
    }

    suspend fun checkedStudentEnrolment(pathwayId: Int): Resource<EnrolResponse>? {
        if(LearnUtils.isOnline(application))
            statusEnrolled = safeApiCall {courseApi.checkedStudentEnrolment(pathwayId)}
        return statusEnrolled
    }

    suspend fun getBatchesListByPathway(pathwayId: Int): Resource<List<Batch>>? {
        if(LearnUtils.isOnline(application)) {
            return try {
                safeApiCall {courseApi.getBatchesAsync(pathwayId) }
            } catch (ex: Exception){
                FirebaseCrashlytics.getInstance().recordException(ex)
                throw ex
            }
        }
        return null
    }

    suspend fun getUpcomingClass(pathwayId: Int): Resource<List<CourseClassContent>> {
        return try {
           safeApiCall { courseApi.getUpcomingClass(pathwayId) }
        } catch (e: OfflineException) {
            throw OfflineException("No network connection")
        }
        catch (ex: Exception){
            FirebaseCrashlytics.getInstance().recordException(ex)
            throw ex
        }
    }

    suspend fun getStudentResult(assessmentId: Int) : Resource<AttemptResponse> {
        try {
            return safeApiCall {courseApi.getStudentResult(assessmentId) }
        } catch (e: OfflineException) {
            throw OfflineException("No network connection")
        }
        catch (ex: Exception){
            throw ex
        }
    }

    suspend fun getCompletedPortion(pathwayId: Int): Resource<GetCompletedPortion> {
        return safeApiCall {  courseApi.getCompletedPortionData(pathwayId)}
    }

    suspend fun getCertificate(pathwayCode : String): Resource<CertificateResponse>{
        return safeApiCall { courseApi.getCertificate(pathwayCode) }
    }
    suspend fun enrollToClass(classId: Int, enrolled: Boolean, shouldRegisterUnregisterAll: Boolean = false): Boolean {
        if (LearnUtils.isOnline(application)){
            return try {
                if (enrolled) {
                    courseApi.logOutToClassAsync(classId, shouldRegisterUnregisterAll)
                    updateEnrollStatus(classId, false)
                } else {
                    courseApi.enrollToClassAsync(classId, mutableMapOf(),shouldRegisterUnregisterAll)
                    updateEnrollStatus(classId, true)
                }
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
                false
            }
        }

        return false

    }

    private suspend fun updateEnrollStatus(classId: Int, enrolled: Boolean): Boolean {
        if(LearnUtils.isOnline(application)){
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
       return false
    }

    suspend fun postStudentResult(
        slugId: Int,
        courseId: Int,
        status: Status,
        selectedOption: List<Int>,
    ){
        try {
            val studentResult = StudentResult(slugId, courseId, status,selectedOption, selectedLang)
            safeApiCall { courseApi.postStudentResult(studentResult) }
        } catch (e: OfflineException) {
            throw OfflineException("No network connection")
        }
        catch (e: Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    suspend fun updateAssessmentListInLocalDb(currentStateList: List<BaseCourseContent>) {
        try {
            val assessmentDao = database.assessmentDao()
            val assessmentContentList = currentStateList.filterIsInstance<CourseAssessmentContent>()
            assessmentDao.insertAssessmentAsync(assessmentContentList)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun postExerciseCompleteStatus(
        slugId: Int,

    ){
        try {
             safeApiCall {courseApi.postExerciseCompleteStatus(slugId, selectedLang) }
        }catch (e: OfflineException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw OfflineException("No network connection")
        }
        catch (e: Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }
}
