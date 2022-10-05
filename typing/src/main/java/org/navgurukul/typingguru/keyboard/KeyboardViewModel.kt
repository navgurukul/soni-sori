package org.navgurukul.typingguru.keyboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import org.merakilearn.core.extentions.TickerState
import org.merakilearn.core.extentions.tickerFlow
import org.merakilearn.core.navigator.Mode
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit.*
import kotlin.collections.ArrayList

enum class CourseKeyState {
    CORRECT, INCORRECT, NEUTRAL
}

data class CourseKey(val label: Char, val state: CourseKeyState) {
    override fun equals(other: Any?): Boolean {
        return other is CourseKey && label == other.label
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}

class KeyboardViewModel(private val keyboardActivityArgs: KeyboardActivityArgs) :
    BaseViewModel<KeyboardViewEvent, KeyboardViewState>(KeyboardViewState()) {

    private var timerStarted: Boolean = false
    private val maxTime = MINUTES.toSeconds(2)

    companion object {
        const val MAX_ALLOWED_KEYS = 8
    }

    init {
        setState {
            copy(
                courseKeys = generateCourseKeys(),
                activeKeyIndex = 0,
                maxProgress = maxTime.toInt()
            )
        }
    }

    fun handle(event: KeyboardViewAction) {
        when (event) {
            is KeyboardViewAction.OnKeyInput -> handleKeyInput(event.key)
        }
    }

    private fun handleKeyInput(key: Char) {

        val viewState = viewState.value ?: return
        val activeKeyIndex = viewState.activeKeyIndex ?: return
        val courseKeys = viewState.courseKeys
        var correctKeys = viewState.correctKeys
        var inCorrectKeys = viewState.inCorrectKeys
        val currentKeyState: CourseKeyState
        if (key.lowercaseChar() == courseKeys[activeKeyIndex].label) {
            currentKeyState = CourseKeyState.CORRECT
            correctKeys += 1
        } else {
            currentKeyState = CourseKeyState.INCORRECT
            inCorrectKeys += 1
            _viewEvents.setValue(KeyboardViewEvent.ShakeKey(key.lowercaseChar()))
        }

        if (activeKeyIndex == courseKeys.size - 1) {
            setState {
                copy(
                    courseKeys = generateCourseKeys(),
                    activeKeyIndex = 0,
                    correctKeys = correctKeys,
                    inCorrectKeys = inCorrectKeys
                )
            }
        } else {
            setState {
                copy(
                    courseKeys = courseKeys.mapIndexed { index, courseKey ->
                        if (index == activeKeyIndex) {
                            courseKey.copy(state = currentKeyState)
                        } else {
                            courseKey
                        }
                    },
                    activeKeyIndex = activeKeyIndex + 1,
                    correctKeys = correctKeys,
                    inCorrectKeys = inCorrectKeys
                )
            }
        }

        if (!timerStarted) {
            timerStarted = true
            tickerFlow(1_000L, maxTime)
                .onEach { tickerState ->

                    when (tickerState) {
                        TickerState.End -> {
                            super.viewState.value?.let {
                                _viewEvents.setValue(
                                    KeyboardViewEvent.OpenScoreActivity(
                                        it.correctKeys,
                                        it.inCorrectKeys,
                                        SECONDS.toMillis(it.maxProgress.toLong()),
                                        keyboardActivityArgs.mode
                                    )
                                )
                            }
                        }
                        is TickerState.Progress -> {
                            val timer =
                                SimpleDateFormat(
                                    "mm:ss",
                                    Locale.ENGLISH
                                )
                            timer.timeZone = TimeZone.getTimeZone("IST")
                            val timeText= timer.format(SECONDS.toMillis(tickerState.value))
                            val progress = maxTime-tickerState.value
                            setState {
                                copy(currentProgress = progress.toInt(), timerText = timeText)
                            }
                        }
                    }

                }.launchIn(viewModelScope)
        }

    }

    private fun generateCourseKeys() = when (keyboardActivityArgs.mode) {
        is Mode.Course -> {
            generateRandomWordList(keyboardActivityArgs.mode.content.distinct())
        }
        Mode.Playground -> {
            generateRandomWordList(('a'..'z').toList().map { it })
        }
    }.map {
        CourseKey(it, CourseKeyState.NEUTRAL)
    }

    private fun generateRandomWordList(list: List<Char>): ArrayList<Char> {
        return ArrayList<Char>().apply {
            val r = Random()
            repeat(MAX_ALLOWED_KEYS) {
                add(list[r.nextInt(list.size)])
            }
        }
    }

}

data class KeyboardViewState(
    val courseKeys: List<CourseKey> = listOf(),
    val activeKeyIndex: Int? = null,
    val correctKeys: Int = 0,
    val inCorrectKeys: Int = 0,
    val currentProgress: Int = 0,
    val maxProgress: Int = 0,
    val timerText: String = "00:00"
) : ViewState

sealed class KeyboardViewAction {
    class OnKeyInput(val key: Char) : KeyboardViewAction()
}

sealed class KeyboardViewEvent : ViewEvents {
    data class ShakeKey(val key: Char) : KeyboardViewEvent()
    data class OpenScoreActivity(
        val rightKeys: Int,
        val wrongKeys: Int,
        val timeTaken: Long,
        val mode: Mode
    ) : KeyboardViewEvent()
}