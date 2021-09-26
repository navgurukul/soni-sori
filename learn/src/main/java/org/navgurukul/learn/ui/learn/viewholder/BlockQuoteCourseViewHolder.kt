package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BlockQuoteBaseCourseContent

class BlockQuoteCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val blockQuoteText: TextView = populateStub(R.layout.item_block_quote_content)


    fun bindView(item: BlockQuoteBaseCourseContent) {
        super.bind(item)

        item.value?.let {

            blockQuoteText.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }

    }
}

