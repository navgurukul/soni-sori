package org.navgurukul.learn.courses.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathwayContainer
import org.navgurukul.learn.courses.network.model.PathwayCourseContainer
import retrofit2.http.*
import java.util.*


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
    suspend fun getExercisesAsync(
        @Path("course_id") course_id: String,
        @Query("lang") language: String
    ): CourseExerciseContainer

    @POST("classes/{classId}/register")
    suspend fun enrollToClassAsync(
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>
    ): ResponseBody

    @DELETE("classes/{classId}/unregister")
    suspend fun logOutToClassAsync(
        @Path(value = "classId") classId: Int
    ): ResponseBody

    @GET("classes/studentEnrolment")
    suspend fun checkedStudentEnrolment(
        @Query("pathway_id") pathway_id: Int
    ):enrolStatus

    @GET("pathways/{pathwayId}/upcomingBatches")
    suspend fun getBatchesAsync(
        @Path(value = "pathwayId") pathwayId: Int
    ): List<Batch>


}
@JsonClass(generateAdapter = true)
data class enrolStatus(
    @Json(name = "enrolled")
    val enrolled: Boolean,
    @Json(name = "not_enrolled")
    val notEnrolled: Any?,
    @Json(name = "enroll_but_finished")
    val enrolButFinished: String,

)
