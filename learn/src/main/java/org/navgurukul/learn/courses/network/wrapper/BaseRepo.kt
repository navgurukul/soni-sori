package org.navgurukul.learn.courses.network.wrapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.navgurukul.learn.R
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


abstract class BaseRepo() {

    private val errorText = "Something went wrong"
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                val errorMessage = "${response.code()} ${response.message()}"

                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    Resource.Error(
                        errorMessage = (errorMessage ?: R.string.error_text) as String
                    )
                }

            } catch (e: HttpException) {
                Resource.Error(errorMessage = e.message ?: errorText)
            } catch (e: IOException) {
                Resource.Error("Please check your network connection")

            } catch (e: Exception) {
                Resource.Error(errorMessage = errorText)
            }
        }
    }
}
