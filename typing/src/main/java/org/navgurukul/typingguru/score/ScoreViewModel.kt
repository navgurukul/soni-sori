package org.navgurukul.typingguru.score

import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.typingguru.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ScoreViewModel(scoreActivityArgs: ScoreActivityArgs, stringProvider: StringProvider) :
    BaseViewModel<EmptyViewEvents, ScoreViewState>(ScoreViewState()) {

    init {
        val accuracy =
            (scoreActivityArgs.rightKeys.toDouble() / (scoreActivityArgs.rightKeys + scoreActivityArgs.wrongKeys)) * 100

        val timer = SimpleDateFormat("mm:ss", Locale.ENGLISH)
        timer.timeZone = TimeZone.getTimeZone("IST")
        val ms = timer.format(scoreActivityArgs.timeTaken)

        val wpm =
            (((scoreActivityArgs.rightKeys + scoreActivityArgs.wrongKeys) / 5) / (TimeUnit.MILLISECONDS.toMinutes(scoreActivityArgs.timeTaken))).toInt()
        setState {
            copy(
                wpm = wpm,
                timeTaken = ms,
                accuracy = stringProvider.getString(
                    R.string.accuracy_with_percentage,
                    accuracy.toInt()
                )
            )
        }
    }
}

data class ScoreViewState(
    val wpm: Int = 0,
    val timeTaken: String = "",
    val accuracy: String = ""
) : ViewState