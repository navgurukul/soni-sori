package org.navgurukul.playground.repo

import kotlinx.coroutines.flow.Flow
import java.io.File

interface PythonRepository {
    var cachedCode: String?
    fun saveCode(code: String, fileName: String,existingFile:Boolean) : String
    fun updateName(code:String,fileName: String,existingFile: Boolean,oldName:String):String
    suspend fun fetchSavedFiles(): Array<File>
    suspend fun deleteFile(file: File): Boolean
    suspend fun runCode(code: String, tag: Any): String?
    suspend fun onInput(input: String)
    suspend fun isFileNamePresent(fileName:String): Boolean


    val inputFlow: Flow<PythonInput>
    val outputFlow: Flow<PythonOutput>
    val errorFlow: Flow<PythonError>
}

data class PythonOutput(val tag: Any, val output: String)
data class PythonError(val tag: Any, val error: String)
data class PythonInput(val tag: Any, val inputEnabled: Boolean)