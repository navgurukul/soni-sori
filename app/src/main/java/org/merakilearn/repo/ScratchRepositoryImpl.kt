package org.merakilearn.repo

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.realm.internal.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.navgurukul.playground.repo.PythonRepositoryImpl
import java.io.*
import java.util.*
import android.util.Base64

@Keep
class ScratchRepositoryImpl(
    private val context: Context,
): ScratchRepository{

    companion object {
        const val DIRECTORY_NAME = "Scratch"
    }

    override suspend fun isFileNamePresent(fileName: String): Boolean {
        val list = fetchSavedFiles()
        for (file_name in list) {
            if (fileName == file_name.name)
                return true
        }
        return false
    }

    override suspend fun deleteFile(file: File): Boolean =
        withContext(Dispatchers.IO) { file.delete() }

    override suspend fun fetchSavedFiles(): Array<File> {
        return withContext(Dispatchers.IO) {
            val directory = File(
                context.getExternalFilesDir(null),
                DIRECTORY_NAME
            ).also {
                it.mkdirs()
            }
            directory.listFiles() ?: emptyArray()
        }
    }

    override fun saveScratchFile(base64Str: String, fileName: String, existingFile: Boolean) {
        var finalFileName = ""
        finalFileName = "$fileName.sb3"
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null
        var file: File?
        try {
            val directory = File(
                context.getExternalFilesDir(null),
                DIRECTORY_NAME
            ).also {
                it.mkdirs()
            }
            file = File(directory, finalFileName)
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            val bfile = Base64.decode(base64Str, 2)
            bos.write(bfile)
            println("File Saved")

        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: IOException) {
            throw e
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
    }
}