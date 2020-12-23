package org.navgurukul.playground.repo

import java.io.File

interface PlaygroundRepository {
    fun getCachedCode(): String
    fun cacheCode(code: String)
    fun saveCode(code: String, fileName: String)
    suspend fun fetchSavedFiles(): Array<File>
    suspend fun deleteFile(file: File): Boolean
}