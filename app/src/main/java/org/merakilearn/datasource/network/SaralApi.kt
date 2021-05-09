package org.merakilearn.datasource.network

import okhttp3.ResponseBody
import org.merakilearn.datasource.network.model.*
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/google")
    suspend fun initLoginAsync(@Body loginRequest: LoginRequest): LoginResponse

    @GET("classes")
    suspend fun getMyClassesAsync(): List<Classes>

    @POST("classes/{classId}/register")
    suspend fun enrollToClassAsync(
        @Path(value = "classId") classId: Int,
        @Body hashMap: MutableMap<String, Any>
    ): ResponseBody

    @DELETE("classes/{classId}/unregister")
    suspend fun logOutToClassAsync(
        @Path(value = "classId") classId: Int
    ): ResponseBody

    @POST("users/create")
    suspend fun initFakeSignUpAsync(): LoginResponse

    @PUT("users/me")
    suspend fun initUserUpdateAsync(
        @Body loginResponse: UserUpdate
    ): UserUpdateContainer

    @GET("classes/{classId}")
    suspend fun fetchClassDataAsync(
        @Path(value = "classId") classId: Int?
    ): Classes
}
