package org.navgurukul.learn.courses.network

import okhttp3.ResponseBody
import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.network.model.*
import retrofit2.Response
import retrofit2.http.*


interface SaralCoursesApi {

    @GET("pathways")
    suspend fun getPathways(
        @Query("appVersion") appVersion: Int = BuildConfig.VERSION_CODE,
        @Query("courseType") coursetype: String = "json",
    ): PathwayContainer

    @GET("pathways/courses")
    suspend fun getDefaultPathwayCoursesAsync(): PathwayCourseContainer   //Api not in use

    @GET("pathways/{pathway_id}/courses")
    suspend fun getCoursesForPathway(
        @Path("pathway_id") pathway_id: Int,
        @Query("courseType") coursetype: String = "json",
    ): PathwayCourseContainer

    @GET("courses/{courseId}/content/slug")
    suspend fun getCourseContentAsync(
        @Path("courseId") courseId: String,
        @Query("lang") language: String
    ): CourseExerciseContainer


    @GET("classes/{classId}/revision")
    suspend fun getRevisionClasses(
        @Path("classId") classId: String
    ):Response<List<CourseClassContent>>

    @POST("classes/{classId}/register")
    suspend fun enrollToClassAsync(
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>,
        @Query("register-all") shouldRegisterAll: Boolean
    ): Response<ResponseBody>

    @DELETE("classes/{classId}/unregister")
    suspend fun logOutToClassAsync(
        @Path(value = "classId") classId: Int,
        @Query("unregister-all") shouldUnregisterAll: Boolean
    ): Response<ResponseBody>

    @GET("classes/studentEnrolment")
    suspend fun checkedStudentEnrolment(
        @Query("pathway_id") pathway_id: Int
    ):Response<EnrolResponse>

    @GET("pathways/{pathwayId}/upcomingBatches")
    suspend fun getBatchesAsync(
        @Path(value = "pathwayId") pathwayId: Int
    ): Response<List<Batch>>

    @GET("pathways/{pathwayId}/upcomingEnrolledClasses")
    suspend fun getUpcomingClass(
        @Path(value = "pathwayId") pathwayId: Int
    ):Response<List<CourseClassContent>>

    @POST("assessment/slug/complete")
    suspend fun postStudentResult(
        @Body studentResult : StudentResult
    ) : Response<StudentResponse>

    @GET("assessment/{slugId}/complete")
    suspend fun getStudentResult(
        @Path(value = "slugId")  slugId : Int
    ): Response<AttemptResponse>

    @GET("pathways/{pathwayId}/totalProgress")
    suspend fun getCompletedPortionData(
        @Path(value = "pathwayId") pathwayId: Int
    ) : Response<GetCompletedPortion>

    @POST("exercises/{slug_id}/markcomplete")
    suspend fun postExerciseCompleteStatus(
        @Path(value = "slug_id") slugId: Int,
        @Query("lang") language: String
    ): Response<ResponseBody>


    @GET("progressTracking/{courseId}/completedContent")
    suspend fun getCompletedContentsIds(
        @Path(value = "courseId") courseId: String
    ): Response<CompletedContentsIds>

    @GET("certificate")
    suspend fun getCertificate(
        @Query(value = "pathway_code") pathway_code: String
    ): Response<CertificateResponse>
}
