package org.navgurukul.learn.ui.learn.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.chaquo.python.Python
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import com.chaquo.python.PyObject
import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

class CodeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody: EditText = codeLayout.findViewById(R.id.code_body)

    // Store the colored text separately
    private var coloredText: Spannable? = null

    private val imageViewPlay: Button = codeLayout.findViewById(R.id.run_btn)
    val outputTextView = codeLayout.findViewById<TextView>(R.id.Actual_outPut)
    val outputTexts = codeLayout.findViewById<TextView>(R.id.out_put_txt)

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
        // Initially hide the Output section
        outputTextView.visibility = View.GONE
        outputTexts.visibility = View.GONE
    }

    @SuppressLint("SuspiciousIndentation")
    fun bindView(item: CodeBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        if (item.title.isNullOrBlank()) {
            codeTitle.visibility = View.GONE
        } else {
            codeTitle.visibility = View.VISIBLE
            codeTitle.text = item.title
        }

        val py = Python.getInstance()
        val pyObj = py.getModule("pyscript")

        imageViewPlay.visibility = View.VISIBLE

        imageViewPlay.setOnClickListener {
            outputTextView.visibility = View.VISIBLE
            outputTexts.visibility = View.VISIBLE
            val objs: PyObject = pyObj.callAttr("main", codeBody.text.toString())
            outputTextView.text = objs.toString()
        }

    var resetCode: TextView = codeLayout.findViewById(R.id.reset_code)
        resetCode.setOnClickListener {
            // Set the colored text when resetting
            coloredText?.let { codeBody.text = it as Editable? }
        }

        val codeValue = item.value ?: ""
        val codeText = HtmlCompat.fromHtml(codeValue, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

        val spannableStringBuilder = SpannableStringBuilder()
        val regex = "\\(([^)]*)\\)".toRegex()
        var lastIndex = 0

        regex.findAll(codeText).forEach { match ->
            val start = match.range.start
            val end = match.range.endInclusive
            val content = codeText.substring(lastIndex, start)

            spannableStringBuilder.append(content)

            spannableStringBuilder.append("(")
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.BLACK),
                spannableStringBuilder.length - 1,
                spannableStringBuilder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val parenContent = codeText.substring(start + 1, end)

            val words = parenContent.split("\\s+".toRegex())

            var wordStart = 0

            for (word in words) {
                val isNumber = word.trim().toDoubleOrNull() != null

                val spaces = parenContent.substring(wordStart, parenContent.indexOf(word, wordStart))
                spannableStringBuilder.append(spaces)

                spannableStringBuilder.append(word)
                if (isNumber) {
                    spannableStringBuilder.setSpan(
                        ForegroundColorSpan(Color.RED),
                        spannableStringBuilder.length - word.length,
                        spannableStringBuilder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannableStringBuilder.setSpan(
                        ForegroundColorSpan(0xFF34C9EB.toInt()),
                        spannableStringBuilder.length - word.length,
                        spannableStringBuilder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                wordStart = parenContent.indexOf(word, wordStart) + word.length
            }

            spannableStringBuilder.append(")")
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(Color.BLACK),
                spannableStringBuilder.length - 1,
                spannableStringBuilder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            lastIndex = end + 1
        }

        spannableStringBuilder.append(codeText.substring(lastIndex))

        // Store the colored text
        coloredText = spannableStringBuilder

        // Set the colored text to codeBody
        codeBody.text = coloredText as SpannableStringBuilder
    }
}

