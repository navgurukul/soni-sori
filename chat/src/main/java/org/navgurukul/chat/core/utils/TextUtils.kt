package org.navgurukul.chat.core.utils

import android.content.Context
import android.os.Build
import android.text.format.Formatter
import java.util.*

object TextUtils {

    /**
     * Since Android O, the system considers that 1ko = 1000 bytes instead of 1024 bytes. We want to avoid that for the moment.
     */
    fun formatFileSize(context: Context, sizeBytes: Long, useShortFormat: Boolean = false): String {
        val normalizedSize = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            sizeBytes
        } else {
            // First convert the size
            when {
                sizeBytes < 1024               -> sizeBytes
                sizeBytes < 1024 * 1024        -> sizeBytes * 1000 / 1024
                sizeBytes < 1024 * 1024 * 1024 -> sizeBytes * 1000 / 1024 * 1000 / 1024
                else                           -> sizeBytes * 1000 / 1024 * 1000 / 1024 * 1000 / 1024
            }
        }

        return if (useShortFormat) {
            Formatter.formatShortFileSize(context, normalizedSize)
        } else {
            Formatter.formatFileSize(context, normalizedSize)
        }
    }

    private val suffixes = TreeMap<Int, String>().also {
        it[1000] = "k"
        it[1000000] = "M"
        it[1000000000] = "G"
    }

    fun formatCountToShortDecimal(value: Int): String {
        try {
            if (value < 0) return "-" + formatCountToShortDecimal(-value)
            if (value < 1000) return value.toString() // deal with easy case

            val e = suffixes.floorEntry(value)
            val divideBy = e?.key
            val suffix = e?.value

            val truncated = value / (divideBy!! / 10) // the number part of the output times 10
            val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
            return if (hasDecimal) "${truncated / 10.0}$suffix" else "${truncated / 10}$suffix"
        } catch (t: Throwable) {
            return value.toString()
        }
    }

}