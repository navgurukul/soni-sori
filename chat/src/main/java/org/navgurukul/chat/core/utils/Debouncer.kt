package org.navgurukul.chat.core.utils

import android.os.Handler

class Debouncer(private val handler: Handler) {

    private val runnables = HashMap<String, Runnable>()

    fun debounce(identifier: String, millis: Long, r: Runnable): Boolean {
        // debounce
        cancel(identifier)

        insertRunnable(identifier, r, millis)
        return true
    }

    fun cancelAll() {
        handler.removeCallbacksAndMessages(null)
    }

    fun cancel(identifier: String) {
        if (runnables.containsKey(identifier)) {
            runnables[identifier]?.let {
                handler.removeCallbacks(it)
                runnables.remove(identifier)
            }
        }
    }

    private fun insertRunnable(identifier: String, r: Runnable, millis: Long) {
        val chained = Runnable {
            handler.post(r)
            runnables.remove(identifier)
        }
        runnables[identifier] = chained
        handler.postDelayed(chained, millis)
    }
}