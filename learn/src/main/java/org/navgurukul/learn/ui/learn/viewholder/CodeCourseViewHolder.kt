package org.navgurukul.learn.ui.learn.viewholder

import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaquo.python.Python
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import com.chaquo.python.PyObject
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.navgurukul.playground.editor.PythonEditorViewActions
import org.navgurukul.playground.editor.PythonEditorViewModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream


class CodeCourseViewHolder(itemView: View,private var viewModel: PythonEditorViewModel?) : BaseCourseViewHolder(itemView) {
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody: EditText = codeLayout.findViewById(R.id.code_body)

    private val imageViewPlay: Button = codeLayout.findViewById(R.id.run_btn)
    private val outputTextView: TextView = codeLayout.findViewById(R.id.Actual_outPut)
    private val outputTexts: TextView = codeLayout.findViewById(R.id.out_put_txt)
    private val customScope = CoroutineScope(Dispatchers.Main)

    private lateinit var inputEditText: EditText
    private lateinit var enterButton: MaterialButton

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
        // Initially hide the Output section
        outputTextView.visibility = View.GONE
        outputTexts.visibility = View.GONE

        bottomSheetDialog = BottomSheetDialog(itemView.context)
        val bottomSheetView = View.inflate(itemView.context, R.layout.bottom_sheet_input, null)
        bottomSheetDialog.setContentView(bottomSheetView)
    }

    fun bindView(item: CodeBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        if (item.title.isNullOrBlank()) {
            codeTitle.visibility = View.GONE
        } else {
            codeTitle.visibility = View.VISIBLE
            codeTitle.text = item.title
        }

        inputEditText = bottomSheetDialog.findViewById(R.id.etInputId)!!
        enterButton = bottomSheetDialog.findViewById(R.id.ibEnters)!!

        val py = Python.getInstance()
        val pyObj = py.getModule("pyscript")

        imageViewPlay.visibility = View.VISIBLE

        imageViewPlay.setOnClickListener {
            customScope.launch {
                executeCode(pyObj, inputEditText.text.toString())
            }
            bottomSheetDialog.show()
        }

        enterButton.setOnClickListener {
            val userInput = inputEditText.text.toString()
            viewModel?.handle(PythonEditorViewActions.OnInput(userInput))
            viewModel?.handle(PythonEditorViewActions.OnRunCode)
        }

        val resetCode: TextView = codeLayout.findViewById(R.id.reset_code)
        resetCode.setOnClickListener {
            codeBody.text = HtmlCompat.fromHtml(
                item.value ?: "",
                HtmlCompat.FROM_HTML_MODE_COMPACT
            ) as Editable?
        }

        codeBody.text = HtmlCompat.fromHtml(
            item.value ?: "",
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ) as Editable?
    }

    private fun executeCode(pyObj: PyObject, userInput: String?) {
        Log.d("Debug", "executeCode called")

        try {
            val scriptCode = codeBody.text.toString()
            val execResult = pyObj.callAttr("main", scriptCode)
            val output = execResult.toString()
            viewModel?.handle(PythonEditorViewActions.OnInput(userInput ?: ""))
            viewModel?.handle(PythonEditorViewActions.OnRunCode)
            val executionResultsTextView = bottomSheetDialog.findViewById<TextView>(R.id.tvExecutionResults)
            val outputToShow = "Output:\n$output"

            Log.d("Debug", "executionResultsTextView: $executionResultsTextView")

            Log.d("Debug", "Output to show: $outputToShow")
            executionResultsTextView?.text = outputToShow
            if (!userInput.isNullOrBlank()) {
                inputEditText?.setText("")
            }
            bottomSheetDialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Debug", "Error during execution: ${e.message}")
            val executionResultsTextView = bottomSheetDialog.findViewById<TextView>(R.id.tvExecutionResults)
            executionResultsTextView?.text = "Error during execution: End of input reached."
            bottomSheetDialog.show()

        }
    }

}
