package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.OptionBaseCourseContent

class OptionCourseViewHolder(itemView: View):
    BaseCourseViewHolder(itemView) {
    private val optionContent: TextView = populateStub(R.layout.item_text_content)

    override val horizontalMargin: Int
        get() = optionContent.context.resources.getDimensionPixelOffset(R.dimen.dimen_course_content_margin)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: OptionBaseCourseContent) {
        super.bind(item)

        item.value?.let {
           optionContent.apply {
                this.text = HtmlCompat.fromHtml(it.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }
    }
}