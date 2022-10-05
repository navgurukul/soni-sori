package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeType


class CodeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val codeLayout: ConstraintLayout = populateStub(R.layout.item_code_content)
    private val codeTitle: TextView = codeLayout.findViewById(R.id.codeTitle)
    private val codeBody: TextView = codeLayout.findViewById(R.id.codeBody)
    private val imageViewPlay: AppCompatButton = codeLayout.findViewById(R.id.imageViewPlay)


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
                imageViewPlay.setOnClickListener {
                    callback.invoke(item)
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
}

