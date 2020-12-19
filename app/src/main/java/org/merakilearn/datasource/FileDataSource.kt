package org.merakilearn.datasource

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileDataSource
constructor(private val application: Application){

    suspend fun fetchSavedFile(): List<Pair<String, String>> {
        return try {
            withContext(Dispatchers.IO) {
                val savedFiles = mutableListOf<Pair<String, String>>()
                val directory = application.applicationContext?.obbDir
                directory?.listFiles()?.forEach {
                    if (it.isFile) {
                        savedFiles.add(Pair(it.path, it.name.split("_")[0]))
                    }
                }
                savedFiles
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteFile(first: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val file = File(first)
                file.delete()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
}