package org.navgurukul.learn.ui.learn.viewholder

import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeType
import org.navgurukul.learn.courses.network.model.ConstantString
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform


class CodeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody:EditText = codeLayout.findViewById(R.id.code_body)
    private val imageViewPlay: Button = codeLayout.findViewById(R.id.run_btn)
    private val output:TextView = codeLayout.findViewById(R.id.Actual_outPut)


    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }



    fun bindView(
        item: CodeBaseCourseContent,
        callback: (BaseCourseContent) -> Unit,
    ) {
        super.bind(item)

        if (item.title.isNullOrBlank()) {
            codeTitle.visibility = View.GONE
        } else {
            codeTitle.visibility = View.VISIBLE
            codeTitle.text = item.title
        }

        when (item.codeTypes) {
            CodeType.javascript -> {
                imageViewPlay.visibility = View.GONE
            }
            CodeType.python -> {
                imageViewPlay.visibility = View.VISIBLE
                imageViewPlay.setOnClickListener {
                    if (item.codeTypes == CodeType.python) {
                        item.value?.let { pythonCode ->
                            executePythonCode(pythonCode)
                        }
                    }
                }
            }
            else -> {
                imageViewPlay.visibility = View.GONE
            }
        }

        val editableText = Editable.Factory.getInstance().newEditable(item.value)
        editableText.replace(
            0, editableText.length,
            editableText.toString().replace(ConstantString.LINE_BREAK, ConstantString.LINE_BR_REPLACEMENT)
                .replace(ConstantString.EMSP, ConstantString.EMSP_REPLACEMENT)
        )
        codeBody.text = editableText

    }
    private fun executePythonCode(pythonCodeWithTags: String) {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(itemView.context))
        }

        val python = Python.getInstance()
        val pythonInterpreter = python.getModule("chaquopy.main")

        // Extract the Python code from the response (remove HTML tags and formatting)
        val pythonCode = pythonCodeWithTags
            .replace(Regex("<.*?>"), "")  // Remove all HTML tags
            .replace("&nbsp;", " ")      // Replace "&nbsp;" with space
            .trim()                      // Trim any leading or trailing whitespaces

        // Split the Python code into individual lines
        val lines = pythonCode.split("\n")

        try {
            for (line in lines) {
                val outputs = pythonInterpreter.callAttr("main", line)
                output.append(outputs.toString() + "\n")
            }
        } catch (e: Exception) {
            output.text = "Error executing Python code:\n${e.message}"
        }
    }
}