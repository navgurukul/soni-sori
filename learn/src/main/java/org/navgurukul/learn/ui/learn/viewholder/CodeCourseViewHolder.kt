package org.navgurukul.learn.ui.learn.viewholder

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeType
import org.navgurukul.playground.repo.PythonRepository
import org.navgurukul.playground.repo.PythonRepositoryImpl

class CodeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody: TextView = codeLayout.findViewById(R.id.code_body)
    private val imageViewPlay: AppCompatButton = codeLayout.findViewById(R.id.run_btn)


    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: CodeBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
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
                imageViewPlay.isVisible = true
                imageViewPlay.setOnClickListener {
                    val pythonCodeWithBreaks = item.value  // Get the Python code from your data
                    val pythonCode = cleanPythonCode(pythonCodeWithBreaks)
                    val outputTextView = codeLayout.findViewById<TextView>(R.id.Actual_outPut)

                    // Clear the output TextView
                    outputTextView.text = ""

                    // Access the context from the itemView
                    val context = itemView.context

                    GlobalScope.launch(Dispatchers.IO) {
                        val output = runPythonCode(context, pythonCode)
                        if (output.isNotEmpty()) {
                            outputTextView.text = output
                        }
                    }
                }


            }
            else -> {
                imageViewPlay.visibility = View.GONE
            }
        }

        codeBody.text = HtmlCompat.fromHtml(
            item.value
                ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT
        )

    }
    private fun cleanPythonCode(codeWithBreaks: String?): String {
        // Remove HTML line breaks ("<br>") from the Python code
        return codeWithBreaks?.replace("<br>", "\n") ?: ""
    }

    private suspend fun runPythonCode(context: Context, code: String?): String {
        try {
            Log.d("PythonExecution", "Executing Python code:\n$code")

            // Access shared preferences and other dependencies using the provided context
            val sharedPreferences = context.getSharedPreferences("YourPreferencesName", Context.MODE_PRIVATE)

            // Initialize PythonRepository and call the runCode method
            val pythonRepo = PythonRepositoryImpl(sharedPreferences, context)

            // Execute Python code and get the output
            val output = pythonRepo.runCode(code ?: "", "your_tag") ?: ""

            Log.d("PythonExecution", "Python code executed successfully. Output:\n$output")

            return output  // Return the actual Python code execution output
        } catch (e: Exception) {
            Log.e("PythonExecution", "Error executing Python code", e)
            // Handle the error here, e.g., return an error message
            return "Error executing Python code: ${e.message}"
        }
    }


}

