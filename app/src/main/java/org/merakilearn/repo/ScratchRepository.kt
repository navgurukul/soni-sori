package org.merakilearn.repo

import java.io.File

interface ScratchRepository{
    suspend fun isFileNamePresent(fileName:String): Boolean
    suspend fun fetchSavedFiles(): Array<File>
    suspend fun deleteFile(file: File): Boolean
    fun saveScratchFile(base64Str: String, fileName: String, existingFile: Boolean)
}