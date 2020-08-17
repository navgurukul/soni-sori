package org.navgurukul.learn.courses.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path


interface SaralCoursesApi {
    @GET("/api/courses")
    fun getCoursesAsync(): Deferred<CoursesResponseContainer>

    @GET("/api/courses/{course_id}/exercises")
    fun getExercisesAsync(@Path("course_id") course_id: String): Deferred<List<ExerciseResponseContainer>>
}
