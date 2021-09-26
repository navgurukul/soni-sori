package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.HeaderBaseCourseContent


class HeaderCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val titleContent: TextView = populateStub(R.layout.item_header_content)

    fun bindView(item: HeaderBaseCourseContent) {
        super.bind(item)

        item.value?.let {
            titleContent.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }
    }
}

