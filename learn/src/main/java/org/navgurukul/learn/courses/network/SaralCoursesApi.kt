package org.navgurukul.learn.courses.network

import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathwayContainer
import org.navgurukul.learn.courses.network.model.PathwayCourseContainer
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


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

}
//courses/{course_id}/exercises
//http://0420-103-152-180-22.ngrok.io/courses/101/exercises