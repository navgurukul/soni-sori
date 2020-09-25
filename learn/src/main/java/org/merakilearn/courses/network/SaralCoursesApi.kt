package org.merakilearn.courses.network

import kotlinx.coroutines.Deferred
import org.merakilearn.courses.network.model.CourseExerciseContainer
import org.merakilearn.courses.network.model.PathWayCourseContainer
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface SaralCoursesApi {
    @GET("/pathways/courses")
    fun getDefaultPathwayCoursesAsync(@Header(value = "Authorization") token: String?): Deferred<PathWayCourseContainer>

    @GET("/courses/{course_id}/exercises")
    fun getExercisesAsync(@Path("course_id") course_id: String): Deferred<CourseExerciseContainer>

}
