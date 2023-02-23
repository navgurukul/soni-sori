package org.merakilearn.repo

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.DeleteScratchResponse
import org.merakilearn.datasource.network.model.GetScratchesResponse
import org.merakilearn.datasource.network.model.LoginResponse
import java.io.*
import java.nio.charset.Charset


class ScratchViewModel(
    private val userRepo: UserRepo,
): ViewModel() {
    private val DIRECTORY_NAME = "Scratch"


    private val user: LoginResponse.User = userRepo.getCurrentUser()!!

    suspend fun fetchSavedFiles(): List<GetScratchesResponse>{
           return try {
                userRepo.getScratchFiles(user.id.toInt())
            } catch (e: Exception){
                throw e
            }
    }

    suspend fun getScratchProject(projectId: String) {
        viewModelScope.launch {
            userRepo.getScratchProject(projectId).s3link
        }

    }

    suspend fun deleteScratchProject(projectId : String): DeleteScratchResponse {
        return try {
            userRepo.deleteScratchProject(projectId)
        }catch (e : Exception){
            throw e
        }
    }

    fun postFile( base64String: String, projectName: String,existingFile: Boolean)
        : Boolean {
        val file = convertBase64StringToFile(base64String)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                projectName,
                RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
            )
            .addFormDataPart(
                "project_name",
                projectName
            )
            .build()

        val request = Request.Builder()
            .url("https://dev-api.navgurukul.org/scratch/FileUploadS3")
            .addHeader("Authorization", "Bearer ${userRepo.getAuthToken()}")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            Log.w("ScratchViewModel", "Success -- ${responseBody}")
            println(responseBody)
            return true
        } else {
            Log.w("ScratchViewModel", "Request failed with code ${response.code}")
            println("Request failed with code ${response.code}")
            return false
        }
    }

     private fun convertBase64StringToFile(base64String: String): File {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val fileContent = decodedBytes.toString(Charset.defaultCharset())
        val file = File.createTempFile("sb3", null)
        file.writeText(fileContent)
        return file
    }

}
