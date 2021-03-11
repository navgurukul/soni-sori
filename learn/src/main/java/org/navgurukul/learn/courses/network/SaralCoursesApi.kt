package org.navgurukul.learn.courses.network

import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.network.model.CourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathwayContainer
import org.navgurukul.learn.courses.network.model.PathwayCourseContainer
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface SaralCoursesApi {

    @GET("/pathways")
    suspend fun getPathways(@Header(value = "Authorization") token: String?, @Query("appVersion") appVersion: Int = BuildConfig.VERSION_CODE): PathwayContainer

    @GET("/pathways/courses")
    suspend fun getDefaultPathwayCoursesAsync(@Header(value = "Authorization") token: String?): PathwayCourseContainer

    @GET("/pathways/{pathway_id}/courses")
    suspend fun getCoursesForPathway(
        @Header(value = "Authorization") token: String,
        @Path("pathway_id") pathway_id: Int
    ): PathwayCourseContainer

    @GET("/courses/{course_id}/exercises")
    suspend fun getExercisesAsync(@Path("course_id") course_id: String): CourseExerciseContainer

}
