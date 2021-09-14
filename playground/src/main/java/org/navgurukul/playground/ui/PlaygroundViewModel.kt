package org.navgurukul.playground.ui

import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.playground.repo.PlaygroundRepository
import org.navgurukul.playground.repo.model.PlaygroundItemModel
import org.navgurukul.playground.repo.model.PlaygroundTypes

class PlaygroundViewModel(
    private val repository: PlaygroundRepository
) :
    BaseViewModel<PlaygroundViewEvents, PlaygroundViewState>(PlaygroundViewState()) {

    init {
        setState {
            copy(playgroundsList = repository.getAllPlaygrounds())
        }
    }

    fun selectPlayground(playgroundItemModel: PlaygroundItemModel) {
        when (playgroundItemModel.type) {
            PlaygroundTypes.TYPING_APP -> _viewEvents.setValue(PlaygroundViewEvents.OpenTypingApp)
            PlaygroundTypes.PYTHON -> _viewEvents.postValue(PlaygroundViewEvents.OpenPythonPlayground)
        }
    }
}

sealed class PlaygroundViewEvents : ViewEvents {
    object OpenTypingApp : PlaygroundViewEvents()
    object OpenPythonPlayground : PlaygroundViewEvents()
}

data class PlaygroundViewState(
    val playgroundsList: List<PlaygroundItemModel> = arrayListOf()
) : ViewState
