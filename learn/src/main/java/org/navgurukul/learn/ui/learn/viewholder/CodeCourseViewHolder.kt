package org.navgurukul.learn.ui.learn.viewholder

import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeType
import org.navgurukul.learn.courses.network.model.ConstantString
import org.navgurukul.playground.editor.CodeResponseModel
import org.navgurukul.playground.editor.PythonEditorViewActions
import org.navgurukul.playground.editor.PythonEditorViewModel

class CodeCourseViewHolder(itemView: View) : BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody: EditText = codeLayout.findViewById(R.id.code_body)
    private val imageViewPlay: Button = codeLayout.findViewById(R.id.run_btn)
    private val output: TextView = codeLayout.findViewById(R.id.Actual_outPut)

    private var pythonEditorViewModel: PythonEditorViewModel? = null
    private var codeObserver: Observer<CodeResponseModel>? = null

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
                imageViewPlay.visibility = View.VISIBLE
                if (pythonEditorViewModel != null) {
                    // Set the click listener for the "Run" button
                    imageViewPlay.setOnClickListener {
                        val pythonCode = codeBody.text.toString()
                        pythonEditorViewModel?.handle(PythonEditorViewActions.OnCodeUpdated(pythonCode))
                        pythonEditorViewModel?.handle(PythonEditorViewActions.OnRunCode)
                    }
                } else {
                    imageViewPlay.isEnabled = false
                    imageViewPlay.visibility = View.GONE
                }
            }
            else -> {
                imageViewPlay.visibility = View.GONE
            }
        }

        val editableText = Editable.Factory.getInstance().newEditable(item.value)
        editableText.replace(
            0, editableText.length,
            editableText.toString()
                .replace(ConstantString.LINE_BREAK, ConstantString.LINE_BR_REPLACEMENT)
                .replace(ConstantString.EMSP, ConstantString.EMSP_REPLACEMENT)
        )
        codeBody.text = editableText
    }

    fun setViewModel(viewModel: PythonEditorViewModel, lifecycleOwner: LifecycleOwner) {
        pythonEditorViewModel = viewModel

        // Remove the previous observer if any
        pythonEditorViewModel?.codeResponse?.removeObservers(lifecycleOwner)

        // Observe the codeResponse LiveData with the given lifecycleOwner
        pythonEditorViewModel?.codeResponse?.observe(lifecycleOwner, Observer { codeResponse ->
            codeResponse?.let { response ->
                when (response) {
                    is CodeResponseModel.Output -> {
                        // Update the output TextView with the new output
                        val outputTextView: TextView = codeLayout.findViewById(R.id.Actual_outPut)
                        outputTextView.text = response.output.toString()
                    }
                    // Handle other cases if needed
                }
            } ?: run {
                // Handle the case when codeResponse is null
                val outputTextView: TextView = codeLayout.findViewById(R.id.Actual_outPut)
                outputTextView.text = ""
            }
        })
    }

}


