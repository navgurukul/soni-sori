package org.navgurukul.webide.extensions

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log

fun String.replace(vararg pairs: Pair<String, String>) =
        pairs.fold(this) { it, (old, new) -> it.replace(old, new, true) }

fun Editable.span(color: Int, range: IntRange) =
        setSpan(ForegroundColorSpan(color), range.start, range.endInclusive, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

fun Editable.color(regex: Regex, color: Int) {
    regex.findAll(this).forEach {
        if (0 < it.range.start && it.range.start < it.range.endInclusive) {
            span(color, it.range)
        }else{
            Log.d("TAG", "color: ")
        }
    }
}