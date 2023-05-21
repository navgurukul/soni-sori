package org.navgurukul.chat.features.reactions

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs

/**
 * We want to use a custom view for rendering an emoji.
 * With generic textview, the performance in the recycler view are very bad
 */
class EmojiDrawView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mLayout: StaticLayout? = null
        set(value) {
            field = value
            invalidate()
        }

    var emoji: String? = null

    override fun onDraw(canvas: Canvas?) {
        EmojiRecyclerAdapter.beginTraceSession("EmojiDrawView.onDraw")
        super.onDraw(canvas)
        canvas?.save()
        val space = abs((width - emojiSize) / 2f)
        if (mLayout != null) {
            canvas?.translate(space, space)
            mLayout!!.draw(canvas)
        }
        canvas?.restore()
        EmojiRecyclerAdapter.endTraceSession()
    }

    companion object {
        val tPaint = TextPaint()

        var emojiSize = 40

        fun configureTextPaint(context: Context, typeface: Typeface?) {
            tPaint.isAntiAlias = true
            tPaint.textSize = 24 * context.resources.displayMetrics.density
            tPaint.color = Color.LTGRAY
            typeface?.let {
                tPaint.typeface = it
            }

            emojiSize = tPaint.measureText("😅").toInt()
        }
    }
}
