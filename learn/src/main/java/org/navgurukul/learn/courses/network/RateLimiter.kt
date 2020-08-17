package org.navgurukul.learn.courses.network

import android.os.SystemClock
import androidx.collection.ArrayMap
import java.util.concurrent.TimeUnit


class RateLimiter<String>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<String, Long>()
    private val timeout: Long = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: String): Boolean {
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            return true
        }
        return false
    }

    private fun now(): Long {
        return SystemClock.uptimeMillis()
    }

    @Synchronized
    fun reset(key: String) {
        timestamps.remove(key)
    }
}