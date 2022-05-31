package org.navgurukul.playground.editor

import android.text.TextUtils
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.playground.R
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

    init {
        val existingCode = pythonRepository.cachedCode

        if (!existingCode.isNullOrEmpty()) {
            setState { copy(code = existingCode) }
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
            PythonEditorViewActions.OnOverrideCode -> overrideCode()
            PythonEditorViewActions.OnRunCode -> runCode()
            PythonEditorViewActions.ClearCode -> updateCode("")
            PythonEditorViewActions.ShareCode -> shareCode()
            PythonEditorViewActions.OnSaveAction -> saveCode()
            is PythonEditorViewActions.OnInput -> onInput(action.input)
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
            copy(code = pythonEditorArgs.code!!)
        }
    }

    private fun updateCode(code: String) {
        pythonRepository.cachedCode = code
        setState {
            copy(code = code, codeResponse = null)
        }
    }

    private fun saveCode() {
        val viewState = viewState.value!!
        if (!TextUtils.isEmpty(viewState.code)) {
            _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameDialog)
        } else {
            _viewEvents.postValue(PythonEditorViewEvents.ShowToast(stringProvider.getString(R.string.nothing_to_save)))
        }
    }

    private fun onFileNameEntered(fileName: String) {
        viewModelScope.launch {
            val viewState = viewState.value!!
            if(pythonRepository.isFileNamePresent(fileName)){
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileNameError)
            }
            else {
                pythonRepository.saveCode(viewState.code, fileName)
                _viewEvents.postValue(PythonEditorViewEvents.ShowFileSavedDialog)
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

    data class OnInput(val input: String) : PythonEditorViewActions()
}

sealed class PythonEditorViewEvents : ViewEvents {
    object ShowUpdateCodeDialog : PythonEditorViewEvents()
    object ShowFileNameDialog : PythonEditorViewEvents()
    data class ShowToast(val message: String) : PythonEditorViewEvents()
    data class ShowShareIntent(val code: String) : PythonEditorViewEvents()
    object ShowFileNameError:PythonEditorViewEvents()
    object ShowFileSavedDialog:PythonEditorViewEvents()
}

data class PythonEditorViewState(
    val code: String = "",
    val codeResponse: CodeResponseModel? = null,
    val inputEnabled: Boolean = false
) : ViewState

sealed class CodeResponseModel {
    data class Output(val output: CharSequence) : CodeResponseModel()
}