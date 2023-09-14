package org.merakilearn.datasource.network

import okhttp3.ResponseBody
import org.merakilearn.datasource.network.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/v2/google")
    suspend fun initLoginAsync(@Body loginRequest: LoginRequest): LoginResponse


    @POST("users/create")
    suspend fun initFakeSignUpAsync(@Body loginRequest: LoginRequest): LoginResponse

    @PUT("users/me")
    suspend fun initUserUpdateAsync(
        @Body loginResponse: UserUpdate,
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

    @GET("scratch/uploadCredentials")
    suspend fun getUploadCredentials(): UploadCredentials

    @PUT("scratch/withoutFile/{projectId}")
    suspend fun updateSuccessS3Upload(
        @Path("projectId") projectId: String,
        @Body projectNameAndUrl: ProjectNameAndUrl
    ): UpdateSuccessS3UploadResponse
}
