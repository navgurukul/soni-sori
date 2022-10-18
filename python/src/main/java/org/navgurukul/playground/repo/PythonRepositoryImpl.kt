package org.navgurukul.playground.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import androidx.core.content.edit
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.playground.repo.PythonRepositoryImpl.PythonOutputInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


@Keep
class PythonRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : PythonRepository {

    companion object {
        const val KEY_PREF_CODE_BACKUP = "Playground.CodeBackup"
        const val DIRECTORY_NAME = "Python"
    }

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
    }

    private val py = Python.getInstance()
    private val sys: PyObject = py.getModule("sys")
    private val console: PyObject = py.getModule("chaquopy.utils.console")
    private val realStdout = sys["stdout"]
    private val realStdErr = sys["stderr"]

    private val _inputFlow = MutableSharedFlow<PythonInput>(
        extraBufferCapacity = 50,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _outputFlow = MutableSharedFlow<PythonOutput>(
        extraBufferCapacity = 50,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _errorFlow = MutableSharedFlow<PythonError>(
        extraBufferCapacity = 50,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val inputFlow = _inputFlow.asSharedFlow()
    override val outputFlow = _outputFlow.asSharedFlow()
    override val errorFlow = _errorFlow.asSharedFlow()

    private val pythonInput =
        PythonInputInterface { isBlocking ->
            if (!_inputFlow.tryEmit(PythonInput(currentTag, isBlocking))) {
                GlobalScope.launch {
                    _inputFlow.emit(PythonInput(currentTag, isBlocking))
                }
            }
        }

    private var currentTag: Any = Unit

    private val stdin: PyObject = console.callAttr("ConsoleInputStream", pythonInput)
    private val stdout: PyObject = console.callAttr("ConsoleOutputStream", PythonOutputInterface {
        if (!_outputFlow.tryEmit(PythonOutput(currentTag, it))) {
            GlobalScope.launch {
                _outputFlow.tryEmit(PythonOutput(currentTag, it))
            }
        }
    }, "output", realStdout)

    private val stderr: PyObject = console.callAttr("ConsoleOutputStream", PythonErrorInterface {
        if (!_errorFlow.tryEmit(PythonError(currentTag, it))) {
            GlobalScope.launch {
                _errorFlow.tryEmit(PythonError(currentTag, it))
            }
        }
    }, "error", realStdErr)

    init {
        sys["stdin"] = stdin
        sys["stdout"] = stdout
        sys["stderr"] = stderr
    }

    override var cachedCode: String?
        get() = sharedPreferences.getString(KEY_PREF_CODE_BACKUP, null)
        set(value) = sharedPreferences.edit { putString(KEY_PREF_CODE_BACKUP, value) }

    override fun saveCode(code: String, fileName: String,existingFile:Boolean) :String {
        var finalFileName =""
        try {
            val directory = File(
                context.getExternalFilesDir(null),
                DIRECTORY_NAME
            ).also {
                it.mkdirs()
            }

            finalFileName = if(existingFile) fileName else fileName + "_" + Date().time + ".py"
            val fileOutStream =
                FileOutputStream(File(directory.toString() + File.separator + finalFileName))
            val outputStreamWriter =
                OutputStreamWriter(fileOutStream)
            outputStreamWriter.write(code)
            outputStreamWriter.close()
        } catch (ex: IOException) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
        return finalFileName
    }

    override fun updateName(code: String,fileName: String, existingFile: Boolean,oldName:String): String {
        var finalFileName =fileName
        try {
            val directory = File(
                context.getExternalFilesDir(null),
                DIRECTORY_NAME

            ).also {
                it.mkdirs()
            }
            val old=oldName
            val new=fileName
            if (directory.exists()) {
                val from: File = File(directory, old)
                val to: File = File(directory, new)
                if (from.exists())
                    from.renameTo(to)
            }
            finalFileName = if(existingFile) fileName else fileName + "_" +  ".py"


        } catch (ex: IOException) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
        return finalFileName
    }



    override suspend fun isFileNamePresent(fileName:String): Boolean{
        val list=fetchSavedFiles()
        for(file_name in list) {
            if(fileName == file_name.name.replaceAfterLast("_", "").removeSuffix("_"))
                return true
        }
        return false
    }

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

    override suspend fun deleteFile(file: File): Boolean =
        withContext(Dispatchers.IO) { file.delete() }

    @Keep
    fun interface PythonOutputInterface {
        @Keep
        fun output(output: String)
    }

    @Keep
    fun interface PythonErrorInterface {
        @Keep
        fun error(output: String)
    }

    @Keep
    fun interface PythonInputInterface {
        @Keep
        fun onInputState(isBlocking: Boolean)
    }

    override suspend fun onInput(input: String): Unit = withContext(Dispatchers.IO) {
        stdin.callAttr("on_input", "$input\n")
    }

    override suspend fun runCode(code: String, tag: Any): String? {
        currentTag = tag
        return withContext(Dispatchers.IO) {
            val stackTrace = py.getModule("chaquopy.main").callAttr("main", code).toString()

            if (stackTrace.isNotEmpty()) {
                return@withContext stackTrace
            }
            return@withContext null
        }
    }
}