package org.navgurukul.playground.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import org.navgurukul.playground.R


/**
 * the simple implementation of an EditText where each line is numbered on the left
 * Code courtesy: https://gist.github.com/francisnnumbi/37ff8a3189a194b939d58d4ad79bf694
 */
class LineNumberedEditText(
    context: Context,
    attrs: AttributeSet?
) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    private val _rect: Rect = Rect()
    private val lpaint: Paint = Paint()

    /** the gap between the line number and the left margin of the text  */
    private val lineNumberMarginGap = 12

    /**
     * the difference between line text size and the normal text size.
     * line text size is preferabl smaller than the normal text size
     */
    val LINE_NUMBER_TEXTSIZE_GAP = 2

    init {
        lpaint.isAntiAlias = true
        lpaint.style = Paint.Style.FILL
        lpaint.color = ContextCompat.getColor(context, R.color.editor_line_number)
        lpaint.textSize = textSize - LINE_NUMBER_TEXTSIZE_GAP
    }


    var lineNumberTextColor: Int
        get() = lpaint.color
        set(textColor) {
            lpaint.color = textColor
        }

    /** whether to set the lines visible or not	 */
    var isLineNumberVisible = true

    override fun onDraw(canvas: Canvas) {
        if (isLineNumberVisible) {

//set the size in case it changed after the last update
            lpaint.textSize = textSize - LINE_NUMBER_TEXTSIZE_GAP
            var baseLine = baseline
            var t = ""
            for (i in 0 until lineCount) {
                t = "" + (i + 1)
                canvas.drawText(t, _rect.left.toFloat(), baseLine.toFloat(), lpaint)
                baseLine += lineHeight
            }

// set padding again, adjusting only the left padding
            setPadding(
                lpaint.measureText(t).toInt() + lineNumberMarginGap, paddingTop,
                paddingRight, paddingBottom
            )
        }
        super.onDraw(canvas)
    }


}