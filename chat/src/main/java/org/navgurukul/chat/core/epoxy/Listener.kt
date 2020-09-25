package org.navgurukul.chat.core.epoxy

import android.view.View

/**
 * Generally we do not care about the View parameter in [View.OnClickListener.onClick()], so create facility to remove it.
 */
typealias ClickListener = () -> Unit

fun View.onClick(listener: ClickListener?) {
    setOnClickListener { listener?.invoke() }
}