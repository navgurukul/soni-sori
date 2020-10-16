package org.merakilearn.datasource.network

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.merakilearn.datasource.network.model.*
import org.navgurukul.learn.courses.db.models.Course
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/google")
    fun initLoginAsync(@Body loginRequest: LoginRequest): Deferred<LoginResponse>

    @GET("classes/upcoming")
    fun getUpComingClassesAsync(@Header(value = "Authorization") token: String?): Deferred<ClassesContainer?>?

    @GET("courses/recommended")
    fun getRecommendedCourseAsync(@Header(value = "Authorization") token: String?): Deferred<List<Course>>

    @GET("classes")
    fun getMyClassesAsync(@Header(value = "Authorization") token: String?): Deferred<List<MyClass>>

    @POST("classes/{classId}/register")
    fun enrollToClassAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>
    ): Deferred<ResponseBody>

    @DELETE("classes/{classId}/unregister")
    fun logOutToClassAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int
    ): Deferred<ResponseBody>

    @POST("users/create")
    fun initFakeSignUpAsync(): Deferred<LoginResponse>

    @PUT("users/update")
    fun initUserUpdateAsync(
        @Header(value = "Authorization") token: String?,
        @Body loginResponse: UserUpdate
    ): Deferred<LoginResponse.User>

    @GET("classes/{classId}")
    fun fetchClassDataAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int?
    ): Deferred<Classes>
}
