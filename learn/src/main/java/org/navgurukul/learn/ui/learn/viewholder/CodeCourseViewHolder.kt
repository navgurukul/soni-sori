package org.navgurukul.learn.ui.learn.viewholder

import android.text.Editable
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


class CodeCourseViewHolder(itemView: View) : BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.example_editor)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.code_title)
    private val codeBody: EditText = codeLayout.findViewById(R.id.code_body)

    private val imageViewPlay: Button = codeLayout.findViewById(R.id.run_btn)
    private val outputTextView = codeLayout.findViewById<TextView>(R.id.Actual_outPut)
    private val outputTexts = codeLayout.findViewById<TextView>(R.id.out_put_txt)

    private lateinit var bottomSheetDialog: BottomSheetDialog

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

        val py = Python.getInstance()
        val pyObj = py.getModule("pyscript")

        imageViewPlay.visibility = View.VISIBLE

        imageViewPlay.setOnClickListener {
            outputTextView.visibility = View.VISIBLE
            outputTexts.visibility = View.VISIBLE
            val objs: PyObject = pyObj.callAttr("main", codeBody.text.toString())
            outputTextView.text = objs.toString()

            bottomSheetDialog.show()
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
}
