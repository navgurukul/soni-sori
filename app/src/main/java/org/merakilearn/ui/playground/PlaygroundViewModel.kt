package org.merakilearn.ui.playground

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.R
import org.merakilearn.datasource.PlaygroundRepo
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.repo.ScratchRepository
import org.merakilearn.util.webide.ROOT_PATH
import org.merakilearn.util.webide.project.ProjectManager
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.playground.repo.PythonRepository
import java.io.File
import java.util.*

class PlaygroundViewModel(
    private val repository: PlaygroundRepo,
    private val pythonRepository: PythonRepository,
    private val scratchRepository: ScratchRepository,
    private val context: Context
) :
    BaseViewModel<PlaygroundViewEvents, PlaygroundViewState>(PlaygroundViewState()) {

    private var currentQuery: String? = null
    private lateinit var playgroundsList: MutableList<PlaygroundItemModel>

    fun handle(action: PlaygroundActions) {
        when (action) {
            is PlaygroundActions.Query -> {
                currentQuery = action.query
                filterList()
            }
            is PlaygroundActions.RefreshLayout -> init()
            is PlaygroundActions.DeleteFile -> deleteFile(action.file)
        }
    }

    fun init() {
        viewModelScope.launch {
            setList()
        }
    }

    private fun filterList() {
        val list = playgroundsList ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filterList = list.filter {
                val filterQuery = currentQuery?.let { currentQuery ->
                    if (currentQuery.isNotEmpty()) {
                        val wordsToCompare = (it.name).split(" ") + it.file.name.replaceAfterLast("_", "").removeSuffix("_").split(" ")
                        wordsToCompare.find { word ->
                            word.startsWith(
                                currentQuery,
                                true
                            )
                        } != null
                    } else {
                        true
                    }
                } ?: true

                return@filter filterQuery
            }
            updateState(filterList)
        }
    }

    private fun updateState(list: List<PlaygroundItemModel>) {
        setState {
            copy(playgroundsList = list)
        }
    }

    private suspend fun setList() {
        playgroundsList = repository.getAllPlaygrounds().toMutableList()
        val savedFiles = pythonRepository.fetchSavedFiles()
        for (file in savedFiles) {
            playgroundsList.add(
                PlaygroundItemModel(
                    PlaygroundTypes.PYTHON_FILE,
                    name = "",
                    file = file,
                    iconResource = R.drawable.ic_saved_file
                )
            )
        }
        val savedFiles2 = scratchRepository.fetchSavedFiles()
        for (file in savedFiles2) {
            playgroundsList.add(
                PlaygroundItemModel(
                    PlaygroundTypes.SCRATCH_FILE,
                    name = file.name.removeSuffix(".sb3"),
                    file = file,
                    iconResource = R.drawable.ic_scratch
                )
            )
        }

        // Fetch savedFiles3 from contents
        val contents = File(context.ROOT_PATH()).list { dir, name ->
            dir.isDirectory && name != ".git" && ProjectManager.isValid(
                context,
                name
            )
        }
        val contentsList = if (contents != null) {
            ArrayList(Arrays.asList(*contents))
        } else {
            ArrayList()
        }

        if (contentsList != null) {
            for (filePath in contentsList) {
                val file = File(filePath)
                playgroundsList.add(
                    PlaygroundItemModel(
                        PlaygroundTypes.WEB_DEV_IDE,
                        name = "",
                        file = file, // Update this line
                        iconResource = R.drawable.ic_web_file
                    )
                )
            }
        }

        updateState(playgroundsList)
    }



    fun selectPlayground(playgroundItemModel: PlaygroundItemModel) {
        when (playgroundItemModel.type) {
            PlaygroundTypes.TYPING_APP -> _viewEvents.setValue(PlaygroundViewEvents.OpenTypingApp)
            PlaygroundTypes.PYTHON -> _viewEvents.postValue(PlaygroundViewEvents.OpenPythonPlayground)
            PlaygroundTypes.PYTHON_FILE -> _viewEvents.setValue(PlaygroundViewEvents.OpenPythonPlaygroundWithFile(
                playgroundItemModel.file))
            PlaygroundTypes.SCRATCH -> _viewEvents.postValue(PlaygroundViewEvents.OpenScratch)
            PlaygroundTypes.SCRATCH_FILE -> _viewEvents.postValue(PlaygroundViewEvents.OpenScratchWithFile(
                playgroundItemModel.file))
            PlaygroundTypes.WEB_DEV_IDE -> _viewEvents.postValue(PlaygroundViewEvents.OpenDialogToCreateWebProject)
        }
    }

    private fun deleteFile(file: File) {
        viewModelScope.launch {
            scratchRepository.deleteFile(file)
            pythonRepository.deleteFile(file)
            init()
        }
    }
}

sealed class PlaygroundViewEvents : ViewEvents {
    object OpenTypingApp : PlaygroundViewEvents()
    object OpenPythonPlayground : PlaygroundViewEvents()
    class OpenPythonPlaygroundWithFile(val file: File) : PlaygroundViewEvents()
    object OpenScratch : PlaygroundViewEvents()
    object OpenWebIDE : PlaygroundViewEvents()
    object OpenDialogToCreateWebProject : PlaygroundViewEvents()
    class OpenScratchWithFile(val file: File) : PlaygroundViewEvents()

}

sealed class PlaygroundActions : ViewModelAction {
    data class Query(val query: String?) : PlaygroundActions()
    object RefreshLayout : PlaygroundActions()
    class DeleteFile(val file: File) : PlaygroundActions()
}

data class PlaygroundViewState(
    val playgroundsList: List<PlaygroundItemModel> = arrayListOf()
) : ViewState