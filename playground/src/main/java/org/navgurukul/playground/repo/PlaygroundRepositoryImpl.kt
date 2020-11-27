package org.navgurukul.playground.repo

import android.R.attr.data
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


class PlaygroundRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val application: Application
) :
    PlaygroundRepository {
    companion object {
        const val KEY_PREF_CODE_BACKUP = "Playground.CodeBackup"
    }

    override fun cacheCode(code: String) {
        sharedPreferences.edit().putString(KEY_PREF_CODE_BACKUP, code).apply()
    }

    override fun getCachedCode(): String {
        return sharedPreferences.getString(KEY_PREF_CODE_BACKUP, "") ?: ""
    }

    override fun saveCode(code: String, fileName: String) {
        val directory = application.applicationContext?.obbDir
        val finalFileName = fileName + "_" + Date().time+".py"
        try {
            val fileOutStream= FileOutputStream(File(directory.toString()+File.separator+finalFileName))
            val outputStreamWriter =
                OutputStreamWriter(fileOutStream)
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }

    }
}