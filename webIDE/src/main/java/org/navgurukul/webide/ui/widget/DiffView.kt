package org.navgurukul.webide.ui.widget

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import org.navgurukul.webide.util.editor.ResourceHelper
import java.util.regex.Pattern

class DiffView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    private lateinit var rectPaint: Paint

    init {
        init(context)
    }

    private fun init(context: Context) {
        typeface = Typeface.createFromAsset(context.assets, "fonts/Consolas.ttf")
        setBackgroundColor(-0xcccccd)
        setTextColor(-0x1)
        textSize = ResourceHelper.dpToPx(context, 4).toFloat()

        rectPaint = Paint()
        rectPaint.color = -0xbbbbbc
        rectPaint.style = Paint.Style.FILL
        rectPaint.isAntiAlias = true
    }

    fun setDiffText(text: SpannableString) {
        setText(highlight(text))
    }

    private fun highlight(input: SpannableString): SpannableString {
        run {
            val m = REMOVE_CHANGES.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0x3eaff), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        run {
            val m = ADD_CHANGES.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0xff009a), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        run {
            val m = LINE_CHANGES.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0x1f9901), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        run {
            val m = INFO_CHANGES.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0x1a00), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        run {
            val m = INFO_CHANGES_TWO.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0x1a00), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        run {
            val m = INFO_CHANGES_THREE.matcher(input)
            while (m.find()) {
                input.setSpan(ForegroundColorSpan(-0x1a00), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        val m = INFO_CHANGES_FOUR.matcher(input)
        while (m.find()) {
            input.setSpan(ForegroundColorSpan(-0x1a00), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return input
    }

    companion object {

        private val INFO_CHANGES = Pattern.compile("/\\**?\\*/|diff.*")
        private val INFO_CHANGES_TWO = Pattern.compile("/\\**?\\*/|index.*")
        private val INFO_CHANGES_THREE = Pattern.compile("/\\**?\\*/|\\+\\+\\+.*")
        private val INFO_CHANGES_FOUR = Pattern.compile("/\\**?\\*/|---.*")
        private val LINE_CHANGES = Pattern.compile("/\\**?\\*/|@@.*")
        private val ADD_CHANGES = Pattern.compile("/\\**?\\*/|\\+.*")
        private val REMOVE_CHANGES = Pattern.compile("/\\**?\\*/|-.*")
    }
}
