package org.navgurukul.learn.courses.network

import kotlinx.coroutines.Deferred
import org.navgurukul.learn.courses.db.models.ExerciseSlug
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SaralCoursesApi {
    @GET("/api/courses")
    fun getCoursesAsync(): Deferred<CoursesResponseContainer>

    @GET("/api/courses/{course_id}/exercises")
    fun getExercisesAsync(@Path("course_id") course_id: String): Deferred<ExerciseResponseContainer>

    @GET("/api/courses/{course_id}/exercise/getBySlug")
    fun getExercisesSlugAsync(
        @Path("course_id") course_id: String,
        @Query("slug") slug: String
    ): Deferred<ExerciseSlug>
}
