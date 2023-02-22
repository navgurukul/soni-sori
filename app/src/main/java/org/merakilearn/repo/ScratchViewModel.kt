package org.merakilearn.repo

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.GetScratchesResponse
import org.merakilearn.datasource.network.model.LoginResponse
import java.io.*
import java.nio.charset.Charset


class ScratchViewModel(
    private val context : Context,
    private val userRepo: UserRepo,
): ViewModel() {
    private val DIRECTORY_NAME = "Scratch"


    private val user: LoginResponse.User

    init {
        user = userRepo.getCurrentUser()!!
    }

//     suspend fun fetchSavedFiles(): Array<File> {
//        return withContext(Dispatchers.IO) {
//            val directory = File(
//                context.getExternalFilesDir(null),
//                ScratchRepositoryImpl.DIRECTORY_NAME
//            ).also {
//                it.mkdirs()
//            }
//            directory.listFiles() ?: emptyArray()
//
//            try {
//                userRepo.getScratchFiles(user.id.toInt())
//            }
//        }
//    }

    suspend fun fetchSavedFiles(): List<GetScratchesResponse>{
           return try {
                userRepo.getScratchFiles(user.id.toInt())
            } catch (e: Exception){
                throw e
            }
    }

    suspend fun getScratchProject(projectId : Int) : GetScratchesResponse {
        return try {
            userRepo.getScratchProject(projectId)
        }catch (e : Exception){
            throw e
        }
    }

//    suspend fun saveScratchFile(base64Str: String, fileName: String, existingFile: Boolean) {
////        var finalFileName = ""
////        finalFileName = "$fileName.sb3"
////        val file = convertBase64StringToFile(base64Str)
////
////        try {
////            userRepo.uploadScratchFile(file, fileName)
////        }catch (e: Exception){
////
////        }
//
//
//
//    }

    fun postFile( base64String: String, projectName: String)
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

    fun convertBase64StringToFile(base64String: String): File {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val fileContent = decodedBytes.toString(Charset.defaultCharset())
        val file = File.createTempFile("sb3", null)
        file.writeText(fileContent)
        return file
    }



}
