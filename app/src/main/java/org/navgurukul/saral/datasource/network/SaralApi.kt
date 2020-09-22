package org.navgurukul.saral.datasource.network

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.datasource.network.model.LoginRequest
import org.navgurukul.saral.datasource.network.model.LoginResponse
import org.navgurukul.saral.datasource.network.model.MyClassContainer
import retrofit2.http.*


interface SaralApi {
    @POST("users/auth/google")
    fun initLoginAsync(@Body loginRequest: LoginRequest): Deferred<LoginResponse>

    @GET("classes/upcoming")
    fun getUpComingClassesAsync(@Header(value = "Authorization") token: String?): Deferred<ClassesContainer>

    @GET("classes/recommended")
    fun getRecommendedClassAsync(@Header(value = "Authorization") token: String?): Deferred<List<ClassesContainer.Classes>>

    @GET("classes")
    fun getMyClassesAsync(@Header(value = "Authorization") token: String?): Deferred<MyClassContainer>

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


}
