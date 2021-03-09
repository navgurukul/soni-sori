package org.navgurukul.playground.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chaquo.python.Python
import org.navgurukul.playground.chaquopy.utils.BufferedLiveEvent
import org.navgurukul.playground.repo.PlaygroundRepository
import kotlin.coroutines.CoroutineContext


class PythonPlaygroundViewModel(private val playgroundRepository: PlaygroundRepository) : ViewModel() {
    companion object {
        const val PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE = "File \"<string>\","
    }
    private var state = Thread.State.NEW
    private val _inputEnabled = MutableLiveData<Boolean>(false)
    private val _error = MutableLiveData<String?>(null)
    val inputEnabled: LiveData<Boolean> = _inputEnabled
    val error: LiveData<String?> = _error
    var output = BufferedLiveEvent<CharSequence>()


    protected val py = Python.getInstance()
    private val sys = py.getModule("sys")
    private val console = py.getModule("chaquopy.utils.console")
    private val realStdout = sys["stdout"]
    private val realStdin = sys["stdin"]
    private val stdout = console.callAttr("ConsoleOutputStream", this, "output", realStdout);
    private val stdin = console.callAttr("ConsoleInputStream", this)

    override fun onCleared() {
        super.onCleared()
        if (stdin != null) {
            onInput(null) // Signals EOF
        }
    }

    fun resumeStreams() {
        sys["stdin"] = stdin
        sys["stdout"] = stdout

    }

    fun pauseStreams() {
        sys["stdin"] = realStdin
        sys["stdout"] = realStdout
    }

    // Called from Python
    // don't make private
    fun onInputState(blocked: Boolean) {
        if (blocked) {
            _inputEnabled.postValue(true)
        }
    }

    fun onInput(text: String?) {
        if (text != null) {
            // Messages which are empty (or only consist of newlines) will not be logged.
            Log.i("python.stdin", if (text == "\n") " " else text)
        }
        stdin.callAttr("on_input", text)
    }


    fun start(code: String) {
        startThread(Runnable {
            try {
                // Post null initially
                _error.postValue(null)
                // Call to the main function

                val stackTrace = py.getModule("chaquopy.main").callAttr("main", code).toString()
                if (stackTrace.isEmpty()) {
                   // output("Finished")
                } else {
                    error(stackTrace)
                }
            } finally {
                _inputEnabled.postValue(false)
                state = Thread.State.TERMINATED
            }
        })
        state = Thread.State.RUNNABLE
    }

    // Todo: may be use coroutine here to run the snippet
    private fun startThread(runnable: Runnable?) {
        Thread(runnable).start()
    }

    fun getState(): Thread.State? {
        return state
    }

    // Called from Python
    // don't make private
    fun output(text: CharSequence?) {
        if (text.isNullOrEmpty()) return
        output.postValue(text)
    }

    // HACK HACK HACK
    // TODO: To test thoroughly
    private fun error(stackTrace: String) {
        Log.d("TEST", stackTrace)

/*        Traceback (most recent call last):
       File "/data/user/0/org.navgurukul.saral/files/chaquopy/AssetFinder/app/chaquopy/main.py", line 6, in main
        exec(codeSnippet)
        File "<string>", line 1, in <module>
        NameError: name 'hjk' is not defined*/

//        The above comment shows the sample stacktrace output, we will omit everything above the first occurence
//        of pattern [File "<string>,"]
        _error.postValue(
            "At" + stackTrace.substring(
                stackTrace.indexOf(PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE)
                        + PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE.length
            ).trimEnd()
        )
    }

    fun getCachedCode() = playgroundRepository.getCachedCode()

    fun cacheCode(code: String) = playgroundRepository.cacheCode(code)
    fun saveCode(code: String, fileName: String) {
        playgroundRepository.saveCode(code,fileName)
    }


}