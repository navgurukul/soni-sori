package org.navgurukul.typingguru.keyboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import org.merakilearn.core.extentions.TickerState
import org.merakilearn.core.extentions.tickerFlow
import org.merakilearn.core.navigator.Mode
import org.merakilearn.core.navigator.ModeNew
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit.*
import kotlin.collections.ArrayList

enum class CourseKeyStateWord {
    CORRECT, INCORRECT, NEUTRAL
}

data class CourseKeyWord(val label: Char, val state: CourseKeyStateWord) {
    override fun equals(other: Any?): Boolean {
        return other is CourseKey && label == other.label
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}

class KeyboardWordViewModel(private val keyboardActivityArgs: KeyboardWordActivityArgs) :
    BaseViewModel<KeyboardViewEventWord, KeyboardViewStateWord>(KeyboardViewStateWord()) {

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

    fun handle(event: KeyboardViewActionWord) {
        when (event) {
            is KeyboardViewActionWord.OnKeyInput -> handleKeyInput(event.key)
        }
    }

    private fun handleKeyInput(key: Char) {

        val viewState = viewState.value ?: return
        val activeKeyIndex = viewState.activeKeyIndex ?: return
        val courseKeys = viewState.courseKeys
        var correctKeys = viewState.correctKeys
        var inCorrectKeys = viewState.inCorrectKeys
        val currentKeyState: CourseKeyStateWord
        if (key.lowercaseChar() == courseKeys[activeKeyIndex].label) {
            currentKeyState = CourseKeyStateWord.CORRECT
            correctKeys += 1
        } else {
            currentKeyState = CourseKeyStateWord.INCORRECT
            inCorrectKeys += 1
            _viewEvents.setValue(KeyboardViewEventWord.ShakeKey(key.lowercaseChar()))
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
                                    KeyboardViewEventWord.OpenScoreActivity(
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
                            val timeText = timer.format(SECONDS.toMillis(tickerState.value))
                            val progress = maxTime - tickerState.value
                            setState {
                                copy(currentProgress = progress.toInt(), timerText = timeText)
                            }
                        }
                    }

                }.launchIn(viewModelScope)
        }

    }

    private fun generateCourseKeys() = when (keyboardActivityArgs.mode) {
        is ModeNew.Course -> {

            generateMeaningfulCharList(keyboardActivityArgs.mode.content)

        }
        ModeNew.Playground -> {
            generateRandomWordList(('a'..'z').toList().map { it })
        }
    }.map {
        CourseKeyWord(it, CourseKeyStateWord.NEUTRAL)
    }

    private fun generateRandomWordList(list: List<Char>): ArrayList<Char> {
        return ArrayList<Char>().apply {
            val r = Random()
            repeat(MAX_ALLOWED_KEYS) {
                add(list[r.nextInt(list.size)])
            }
        }
    }

    private fun generateMeaningfulWordList(list: List<String>): String {
        return list.random()
    }

    private fun generateMeaningfulCharList(list: List<String>): ArrayList<Char> {
        val string = generateMeaningfulWordList(list)
        val chars: ArrayList<Char> = ArrayList()
        string.forEach { chars.add(it) }
        return chars
    }

}

data class KeyboardViewStateWord(
    val courseKeys: List<CourseKeyWord> = listOf(),
    val activeKeyIndex: Int? = null,
    val correctKeys: Int = 0,
    val inCorrectKeys: Int = 0,
    val currentProgress: Int = 0,
    val maxProgress: Int = 0,
    val timerText: String = "00:00"
) : ViewState

sealed class KeyboardViewActionWord {
    class OnKeyInput(val key: Char) : KeyboardViewActionWord()
}

sealed class KeyboardViewEventWord : ViewEvents {
    data class ShakeKey(val key: Char) : KeyboardViewEventWord()
    data class OpenScoreActivity(
        val rightKeys: Int,
        val wrongKeys: Int,
        val timeTaken: Long,
        val mode: ModeNew
    ) : KeyboardViewEventWord()
}