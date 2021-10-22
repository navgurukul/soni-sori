package org.navgurukul.playground.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import org.navgurukul.commonui.themes.getThemedUnit
import org.navgurukul.playground.R
import java.lang.Integer.max


/**
 * the simple implementation of an EditText where each line is numbered on the left
 * Code courtesy: https://gist.github.com/francisnnumbi/37ff8a3189a194b939d58d4ad79bf694
 */
class LineNumberedEditText(
    context: Context,
    attrs: AttributeSet?
) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    private val paint: Paint = Paint()

    /** the gap between the line number and the left margin of the text  */
    private val lineNumberHorizontalMargin =
        context.resources.getDimensionPixelSize(R.dimen.spacing_1x)
    private val lineWidth = context.getThemedUnit(R.attr.borderWidth)

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.editor_line_number)
        paint.textSize = textSize
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        paint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        var baseLine = baseline
        var lineNumber = ""
        for (i in 0 until lineCount) {
            lineNumber = (i + 1).toString()
            canvas.drawText(
                lineNumber,
                lineNumberHorizontalMargin.toFloat(),
                baseLine.toFloat(),
                paint
            )
            baseLine += lineHeight
        }

        val leftPadding =
            paint.measureText(lineNumber).toInt() + (lineNumberHorizontalMargin * 2)

        paint.strokeWidth = lineWidth.toFloat()
        canvas.drawLine(
            leftPadding.toFloat(), 0f, (leftPadding + lineWidth).toFloat(),
            baseLine.coerceAtLeast(height).toFloat(), paint
        )

        setPadding(
            leftPadding + lineWidth + lineNumberHorizontalMargin, paddingTop,
            paddingRight, paddingBottom
        )
        super.onDraw(canvas)
    }


}