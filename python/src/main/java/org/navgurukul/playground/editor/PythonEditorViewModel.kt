package org.navgurukul.playground.editor

import android.text.TextUtils
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.playground.R
import org.navgurukul.playground.editor.PythonEditorActivity.Companion.EMPTY_FILE
import org.navgurukul.playground.repo.PythonRepository


class PythonEditorViewModel(
    private val pythonEditorArgs: PythonEditorArgs,
    private val pythonRepository: PythonRepository,
    private val stringProvider: StringProvider,
    private val errorColor: Int
) : BaseViewModel<PythonEditorViewEvents, PythonEditorViewState>(PythonEditorViewState()) {

    companion object {
        const val PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE = "File \"<string>\","
    }
    var newFile:Boolean=false

    init {
        val existingCode = ""//pythonRepository.cachedCode

        setState {
            copy(title="Untitled", code = existingCode)
        }

        if (!pythonEditorArgs.code.isNullOrEmpty()) {
            _viewEvents.postValue(PythonEditorViewEvents.ShowUpdateCodeDialog)
        }

        viewModelScope.launch {
            pythonRepository.outputFlow.collect {
                if (it.tag == this@PythonEditorViewModel) {
                    updateOutput(it.output)
                }

            }
        }

        viewModelScope.launch {
            pythonRepository.inputFlow.collect {
                if (it.tag == this@PythonEditorViewModel) {
                    setState { copy(inputEnabled = it.inputEnabled) }
                }
            }
        }

        viewModelScope.launch {
            pythonRepository.errorFlow.collect {
                if (it.tag == this@PythonEditorViewModel) {
                    updateError(it.error)
                }
            }
        }

        newFile=pythonEditorArgs.newFile
    }

    private fun updateOutput(output: CharSequence) {
        setState {
            val codeResponse = if (codeResponse is CodeResponseModel.Output) {
                CodeResponseModel.Output(TextUtils.concat(codeResponse.output, output))
            } else {
                CodeResponseModel.Output(output)
            }
            copy(codeResponse = codeResponse)
        }
    }

    private fun updateError(error: CharSequence) {
        val formattedError = buildSpannedString {
            color(errorColor) {
                append("At" + error.substring(
                    error.indexOf(PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE)
                            + PATTERN_TO_BE_SEARCHED_IN_PYTHON_STACKTRACE.length
                ).trimEnd())
            }
        }
        setState {
            val codeResponse = if (codeResponse is CodeResponseModel.Output) {
                CodeResponseModel.Output(TextUtils.concat(codeResponse.output, formattedError))
            } else {
                CodeResponseModel.Output(formattedError)
            }
            copy(codeResponse = codeResponse)
        }
    }

    fun handle(action: PythonEditorViewActions) {
        when (action) {
            is PythonEditorViewActions.OnCodeUpdated -> updateCode(action.code)
            is PythonEditorViewActions.OnFileNameEntered -> onFileNameEntered(action.fileName)
            is PythonEditorViewActions.OnFileRenameEntered->onFileRenameEntered(action.reName)
            PythonEditorViewActions.OnOverrideCode -> overrideCode()
            PythonEditorViewActions.OnRunCode -> runCode()
            PythonEditorViewActions.ClearCode -> updateCode("")
            PythonEditorViewActions.ShareCode -> shareCode()
            PythonEditorViewActions.OnSaveAction -> saveCode()
            PythonEditorViewActions.OnRenameFile -> renameCode()
            is PythonEditorViewActions.OnInput -> onInput(action.input)
        }
    }

    private fun renameCode() {
        val viewState = viewState.value!!
        if (!TextUtils.isEmpty(viewState.code)) {
            _viewEvents.postValue(PythonEditorViewEvents.ShowRenameDialog)
        } else {
            _viewEvents.postValue(PythonEditorViewEvents.ShowToast(stringProvider.getString(R.string.nothing_to_rename)))
        }
    }

    private fun shareCode() {
        val viewState = viewState.value!!
        if (!TextUtils.isEmpty(viewState.code)) {
            _viewEvents.postValue(PythonEditorViewEvents.ShowShareIntent(viewState.code))
        } else {
            _viewEvents.postValue(PythonEditorViewEvents.ShowToast(stringProvider.getString(R.string.nothing_to_share)))
        }
    }

    private fun onInput(input: String) {
        updateOutput(" $input \n")
        viewModelScope.launch { pythonRepository.onInput(input) }
        setState { copy(inputEnabled = false) }
    }

    private fun runCode() {
        val viewState = viewState.value!!
        viewModelScope.launch {
            setState { copy(codeResponse = CodeResponseModel.Output("")) }
            val error = pythonRepository.runCode(viewState.code, this@PythonEditorViewModel)
            if (error != null) {
                updateError(error)
            }
        }
    }

    private fun overrideCode() {
        setState {
            copy(
                code = pythonEditorArgs.code!!,
                fileName=pythonEditorArgs.file.name,
                title = pythonEditorArgs.file.name.replaceAfterLast("_", "").removeSuffix("_"),
                fileSaved = !newFile
            )
        }
    }

    private fun updateCode(code: String) {
        pythonRepository.cachedCode = code
        setState {
            copy(
                code = code,
                codeResponse = null,
                fileSaved=false
            )
        }
    }
    private fun updateName(name:String){
        val viewState = viewState.value!!
        pythonRepository.saveCode(viewState.code, viewState.fileName,true)
        _viewEvents.postValue(PythonEditorViewEvents.ShowFileSavedDialog(false))
        setState {
            copy(
                fileSaved = true
            )
        }
    }

    private fun saveCode() {
        val viewState = viewState.value!!
        if (!TextUtils.isEmpty(viewState.code)) {
            if(newFile)
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameDialog)
            else{
                pythonRepository.saveCode(viewState.code, viewState.fileName,true)
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileSavedDialog(false))
                setState {
                    copy(
                        fileSaved = true
                    )
                }
            }
        } else {
            _viewEvents.postValue(PythonEditorViewEvents.ShowToast(stringProvider.getString(R.string.nothing_to_save)))
        }
    }

    private fun onFileNameEntered(fileName: String) {
        viewModelScope.launch {
            val viewState = viewState.value!!
            if(fileName.isNotBlank()) {
                if (pythonRepository.isFileNamePresent(fileName)) {
                    _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameError(stringProvider.getString(R.string.filename_error)))
                } else {

                    val savedFileName=pythonRepository.saveCode(viewState.code, fileName,false)
                    _viewEvents.postValue(PythonEditorViewEvents.ShowFileSavedDialog(true))
                    newFile=true
                    setState {
                        copy(
                            fileName=savedFileName ,
                            title = fileName,
                            fileSaved = true
                        )
                    }
                }
            }
            else{
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameError(stringProvider.getString(R.string.filename_required)))
            }
        }
    }
    private fun onFileRenameEntered(fileName: String) {
        viewModelScope.launch {
            val viewState = viewState.value!!
            if(fileName.isNotBlank()) {
                if (pythonRepository.isFileNamePresent(fileName)) {
                    _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameError(stringProvider.getString(R.string.filename_error)))
                } else {
                    var oldName=pythonEditorArgs.file.name
                    val savedFileName=pythonRepository.updateName( viewState.code,fileName,true,oldName)
                    _viewEvents.postValue(PythonEditorViewEvents.ShowFileRenamedDialog(true))
                    newFile=false
                    setState {
                        copy(
                            fileName=savedFileName ,
                            title = fileName,
                            fileSaved = true
                        )
                    }
                }
            }
            else{
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameError(stringProvider.getString(R.string.filename_required)))
            }
        }
    }
}



sealed class PythonEditorViewActions : ViewModelAction {
    data class OnCodeUpdated(val code: String) : PythonEditorViewActions()
    data class OnFileNameEntered(val fileName: String) : PythonEditorViewActions()
    object OnOverrideCode : PythonEditorViewActions()
    object OnRunCode : PythonEditorViewActions()
    object OnSaveAction : PythonEditorViewActions()
    object ClearCode : PythonEditorViewActions()
    object ShareCode : PythonEditorViewActions()
    object OnRenameFile : PythonEditorViewActions()

    data class OnInput(val input: String) : PythonEditorViewActions()
    data class OnFileRenameEntered(val reName: String) : PythonEditorViewActions()


}

sealed class PythonEditorViewEvents : ViewEvents {
    object ShowUpdateCodeDialog : PythonEditorViewEvents()
    object ShowFileNameDialog : PythonEditorViewEvents()
    object ShowRenameDialog:PythonEditorViewEvents()
    data class ShowToast(val message: String) : PythonEditorViewEvents()
    data class ShowShareIntent(val code: String) : PythonEditorViewEvents()
    class ShowFileNameError(val message: String): PythonEditorViewEvents()
    class ShowFileSavedDialog(val closeDialog: Boolean): PythonEditorViewEvents()
    class ShowFileRenamedDialog(val closeDialog: Boolean): PythonEditorViewEvents()
    object OnRenameFile : PythonEditorViewEvents()
}

data class PythonEditorViewState(
    val code: String = "",
    val codeResponse: CodeResponseModel? = null,
    val inputEnabled: Boolean = false,
    val fileName:String = "",
    var fileSaved: Boolean = false,
    val title:String=" "
) : ViewState

sealed class CodeResponseModel {
    data class Output(val output: CharSequence) : CodeResponseModel()
}