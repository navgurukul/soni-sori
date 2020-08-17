package org.navgurukul.learn.courses.network.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import org.navgurukul.learn.courses.network.CoursesResponseContainer
import org.navgurukul.learn.courses.network.ExerciseResponseContainer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://saral.navgurukul.org/"

interface SaralCoursesApi {
    @GET("/api/courses")
    fun getCourses(): Deferred<CoursesResponseContainer>

    @GET("/api/courses/{course_id}/exercises")
    fun getExercises(@Path("course_id") course_id: String): Deferred<ExerciseResponseContainer>
}

// we can add interceptors here for logging etc
private val okHttpClient = OkHttpClient.Builder().build()

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val client = retrofit.create(
        SaralCoursesApi::class.java)
}