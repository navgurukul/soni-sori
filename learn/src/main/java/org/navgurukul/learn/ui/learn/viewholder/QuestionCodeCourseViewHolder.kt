package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.QuestionCodeBaseCourseContent
import org.navgurukul.learn.courses.network.model.ConstantString


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

        codeBody.text = item.value?.replace(ConstantString.LINE_BREAK, ConstantString.LINE_BR_REPLACEMENT)?.replace(ConstantString.EMSP, ConstantString.EMSP_REPLACEMENT)

    }

}
