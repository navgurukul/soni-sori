package org.merakilearn.core.extentions

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

sealed class TickerState {
    class Progress(val value: Long): TickerState()
    object End: TickerState()
}
fun tickerFlow(
    periodMs: Long,
    count: Long,
    start: Long = 0,
    initialDelayMs: Long = 0,
) = flow {
    delay(initialDelayMs)

    var counter = count
    while (counter >= 0 && currentCoroutineContext().isActive) {
        emit(TickerState.Progress(counter))
        counter -= 1

        delay(periodMs)
    }
    emit(TickerState.End)
}