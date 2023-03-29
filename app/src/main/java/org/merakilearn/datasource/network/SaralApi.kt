package org.merakilearn.datasource.network

import okhttp3.ResponseBody
import org.merakilearn.datasource.network.model.*
import retrofit2.Response
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/v2/google")
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
    suspend fun initFakeSignUpAsync(@Body loginRequest: LoginRequest): LoginResponse

    @PUT("users/me")
    suspend fun initUserUpdateAsync(
        @Body loginResponse: UserUpdate
    ): UserUpdateContainer

    @GET("classes/{classId}")
    suspend fun fetchClassDataAsync(
        @Path(value = "classId") classId: Int?
    ): Classes

    @GET("pathways/ResidentialPathway ")
    suspend fun getResidentialProgramPathway(): ResidentialProgramPathwayResponse

    @GET("users/EnrolledBatches")
    suspend fun getEnrolledBatches(): Response<List<Batches>>

    @GET("partners/{partnerID}")
    suspend fun getPartnerData(
        @Path(value = "partnerID") partnerID: Int
    ): PartnerDataResponse
}
