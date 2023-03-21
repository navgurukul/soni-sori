package org.navgurukul.learn.courses.network

import okhttp3.ResponseBody
import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.network.model.*
import retrofit2.http.*


interface SaralCoursesApi {

    @GET("pathways")
    suspend fun getPathways(
        @Query("appVersion") appVersion: Int = BuildConfig.VERSION_CODE,
        @Query("courseType") coursetype: String = "json",
    ): PathwayContainer

    @GET("pathways/courses")
    suspend fun getDefaultPathwayCoursesAsync(): PathwayCourseContainer

    @GET("pathways/{pathway_id}/courses")
    suspend fun getCoursesForPathway(
        @Path("pathway_id") pathway_id: Int,
        @Query("courseType") coursetype: String = "json",
    ): PathwayCourseContainer

    @GET("courses/{course_id}/exercises")
    suspend fun getCourseContentAsync(
        @Path("course_id") course_id: String,
        @Query("lang") language: String
    ): CourseExerciseContainer

    @GET("classes/{classId}/revision")
    suspend fun getRevisionClasses(
        @Path("classId") classId: String
    ):List<CourseClassContent>

    @POST("classes/{classId}/register")
    suspend fun enrollToClassAsync(
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>,
        @Query("register-all") shouldRegisterAll: Boolean
    ): ResponseBody

    @DELETE("classes/{classId}/unregister")
    suspend fun logOutToClassAsync(
        @Path(value = "classId") classId: Int,
        @Query("unregister-all") shouldUnregisterAll: Boolean
    ): ResponseBody

    @GET("classes/studentEnrolment")
    suspend fun checkedStudentEnrolment(
        @Query("pathway_id") pathway_id: Int
    ):EnrolResponse

    @GET("pathways/{pathwayId}/upcomingBatches")
    suspend fun getBatchesAsync(
        @Path(value = "pathwayId") pathwayId: Int
    ): List<Batch>

    @GET("pathways/{pathwayId}/upcomingEnrolledClasses")
    suspend fun getUpcomingClass(
        @Path(value = "pathwayId") pathwayId: Int
    ):List<CourseClassContent>

    @POST("assessment/student/result")
    suspend fun postStudentResult(
        @Body studentResult : StudentResult
    ) : StudentResponse

    @GET("assessment/{assessmentId}/student/result")
    suspend fun getStudentResult(
        @Path(value = "assessmentId")  assessmentId : Int
    ): AttemptResponse

    @GET("pathways/{pathwayId}/completePortion")
    suspend fun getCompletedPortionData(
        @Path(value = "pathwayId") pathwayId: Int
    ) : GetCompletedPortion

    @POST("progressTracking/learningTrackStatus")
    suspend fun postLearningTrackStatus(
        @Body learningTrackStatus : LearningTrackStatus
    ) : ResponseBody

    @GET("progressTracking/{courseId}/completedCourseContentIds")
    suspend fun getCompletedContentsIds(
        @Path(value = "courseId") courseId: String
    ): CompletedContentsIds

    @GET("certificate")
    suspend fun getCertificate(): CertificateResponse
}



