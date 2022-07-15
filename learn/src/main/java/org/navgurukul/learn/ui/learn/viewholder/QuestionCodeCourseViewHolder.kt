package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.QuestionCodeBaseCourseContent

class QuestionCodeCourseViewHolder(itemview: View):
BaseCourseViewHolder(itemview){
    private val questionCodeLayout: ConstraintLayout = populateStub(R.layout.item_question_code_content)
    private val codeBody: TextView = questionCodeLayout.findViewById(R.id.questionCodeBody)

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: QuestionCodeBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        codeBody.text = HtmlCompat.fromHtml(
            item.value
                ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }

}
