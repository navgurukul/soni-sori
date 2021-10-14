package org.navgurukul.learn.ui.learn.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.LinkBaseCourseContent

class LinkCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val linkContent: TextView = populateStub(R.layout.item_link_content)

    override val horizontalMargin: Int
        get() = linkContent.context.resources.getDimensionPixelOffset(R.dimen.dimen_course_content_margin)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: LinkBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        item.value?.let {

            linkContent.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
                this.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                this.setOnClickListener {
                    callback.invoke(item)
                }
            }

        }
    }
}
