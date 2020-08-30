package org.navgurukul.learn.courses.network

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.navgurukul.learn.courses.db.models.ExerciseSlug
import org.navgurukul.saral.datasource.network.model.LoginRequest
import org.navgurukul.saral.datasource.network.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*


interface SaralLoginApi {
    @POST("/api/users/auth/google")
    fun initLoginAsync(@Body loginRequest: LoginRequest): Deferred<LoginResponse>

}
