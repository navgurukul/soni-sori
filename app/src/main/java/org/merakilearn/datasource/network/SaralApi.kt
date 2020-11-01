package org.merakilearn.datasource.network

import okhttp3.ResponseBody
import org.merakilearn.datasource.network.model.*
import org.navgurukul.learn.courses.db.models.Course
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/google")
    suspend fun initLoginAsync(@Body loginRequest: LoginRequest): LoginResponse

    @GET("classes/upcoming")
    suspend fun getUpComingClassesAsync(@Header(value = "Authorization") token: String?): ClassesContainer

    @GET("courses/recommended")
    suspend fun getRecommendedCourseAsync(@Header(value = "Authorization") token: String?): List<Course>

    @GET("classes")
    suspend fun getMyClassesAsync(@Header(value = "Authorization") token: String?): List<MyClass>

    @POST("classes/{classId}/register")
    suspend fun enrollToClassAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>
    ): ResponseBody

    @DELETE("classes/{classId}/unregister")
    suspend fun logOutToClassAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int
    ): ResponseBody

    @POST("users/create")
    suspend fun initFakeSignUpAsync(): LoginResponse

    @PUT("users/me")
    suspend fun initUserUpdateAsync(
        @Header(value = "Authorization") token: String?,
        @Body loginResponse: UserUpdate
    ): UserUpdateContainer

    @GET("classes/{classId}")
    suspend fun fetchClassDataAsync(
        @Header(value = "Authorization") token: String?,
        @Path(value = "classId") classId: Int?
    ): Classes
}
