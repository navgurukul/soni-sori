package org.navgurukul.learn.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import org.navgurukul.commonui.themes.getThemedUnit
import org.navgurukul.learn.R
import java.lang.Integer.max


/**
 * the simple implementation of an EditText where each line is numbered on the left
 * Code courtesy: https://gist.github.com/francisnnumbi/37ff8a3189a194b939d58d4ad79bf694
 */

class LineNumberedEditText(
    context: Context,
    attrs: AttributeSet?
) : AppCompatEditText(context, attrs) {

    private val paint: Paint = Paint()
    private val lineNumberBoxWidth =
        resources.getDimensionPixelSize(R.dimen.line_number_box_width) // 15dp
    private val lineNumberHorizontalMargin =
        context.resources.getDimensionPixelSize(R.dimen.spacing_1x)
    private val lineWidth = context.getThemedUnit(R.attr.borderWidth)

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.colorBlack)
        paint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        var baseLine = baseline
        val redPaint = Paint()
        redPaint.color = ContextCompat.getColor(context, R.color.dotGrey)

        for (i in 0 until lineCount) {
            val lineNumber = (i + 1).toString()

            val lineNumberRect = Rect(
                0,
                baseLine - lineHeight,
                lineNumberBoxWidth,
                baseLine
            )
            canvas.drawRect(lineNumberRect, redPaint)

            paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
            canvas.drawText(
                lineNumber,
                lineNumberHorizontalMargin.toFloat(),
                baseLine.toFloat(),
                paint
            )
            baseLine += lineHeight
        }

        val leftPadding =
            paint.measureText(lineCount.toString()).toInt() + (lineNumberHorizontalMargin * 2)

        paint.strokeWidth = lineWidth.toFloat()

        setPadding(
            leftPadding + lineNumberBoxWidth,
            paddingTop,
            paddingRight,
            paddingBottom
        )
        super.onDraw(canvas)
    }
}