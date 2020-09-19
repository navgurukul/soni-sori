package org.navgurukul.chat.features.html

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import org.navgurukul.chat.R
import org.navgurukul.chat.core.resources.ColorProvider

class SpoilerSpan(private val colorProvider: ColorProvider) : ClickableSpan() {

    override fun onClick(widget: View) {
        isHidden = !isHidden
        widget.invalidate()
    }

    private var isHidden = true

    override fun updateDrawState(tp: TextPaint) {
        if (isHidden) {
            tp.bgColor = colorProvider.getColor(R.color.spoiler_background_color)
            tp.color = Color.TRANSPARENT
        } else {
            tp.bgColor = colorProvider.getColor(R.color.block_background_color)
            tp.color = colorProvider.getColorFromAttribute(R.attr.textPrimary)
        }
    }
}