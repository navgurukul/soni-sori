package org.navgurukul.playground.repo

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.navgurukul.playground.R
import org.navgurukul.playground.repo.model.PlaygroundItemModel
import org.navgurukul.playground.repo.model.PlaygroundTypes
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
        const val DIRECTORY_NAME = "Python"
    }

    override fun getAllPlaygrounds(): List<PlaygroundItemModel> {
        return arrayListOf(
            PlaygroundItemModel(
                PlaygroundTypes.PYTHON,
                R.string.python,
                R.drawable.ic_python_icon,
                R.color.python_item_bg_color
            ),
            PlaygroundItemModel(
                PlaygroundTypes.TYPING_APP,
                R.string.typing,
                R.drawable.ic_typing_icon,
                R.color.typing_item_bg_color
            )
        )
    }

    override fun cacheCode(code: String) {
        sharedPreferences.edit().putString(KEY_PREF_CODE_BACKUP, code).apply()
    }

    override fun getCachedCode(): String {
        return sharedPreferences.getString(KEY_PREF_CODE_BACKUP, "") ?: ""
    }

    override fun saveCode(code: String, fileName: String) {
        try {
            val directory = File(
                application.applicationContext?.getExternalFilesDir(null),
                DIRECTORY_NAME
            ).also {
                it.mkdirs()
            }
            val finalFileName = fileName + "_" + Date().time + ".py"
            val fileOutStream =
                FileOutputStream(File(directory.toString() + File.separator + finalFileName))
            val outputStreamWriter =
                OutputStreamWriter(fileOutStream)
            outputStreamWriter.write(code)
            outputStreamWriter.close()
        } catch (ex: IOException) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    override suspend fun fetchSavedFiles(): Array<File> {
        return try {
            withContext(Dispatchers.IO) {
                val directory = File(
                    application.applicationContext?.getExternalFilesDir(null),
                    DIRECTORY_NAME
                ).also {
                    it.mkdirs()
                }
                directory.listFiles() ?: emptyArray()
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            emptyArray()
        }
    }

    override suspend fun deleteFile(file: File): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                file.delete()
            }
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            false
        }
    }
}