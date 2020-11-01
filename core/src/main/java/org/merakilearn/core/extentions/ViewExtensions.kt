package org.merakilearn.core.extentions

import android.view.View
import android.view.ViewGroup

fun View.enableChildren(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.enableChildren(enabled)
        }
    }
}