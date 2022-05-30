package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_mcq_option.view.*
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.OptionBaseCourseContent
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.ui.learn.adapter.OptionSelectionAdapter

class OptionCourseViewHolder(itemView: View):
    BaseCourseViewHolder(itemView) {

        private val optionContentView: ConstraintLayout = populateStub(R.layout.item_option_content)
        private val optionContent: RecyclerView = optionContentView.findViewById(R.id.optionLayout)
        private lateinit var mClassAdapter: OptionSelectionAdapter




    override val horizontalMargin: Int
        get() = optionContent.context.resources.getDimensionPixelOffset(R.dimen.dimen_course_content_margin)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: OptionBaseCourseContent) {
        super.bind(item)


        item.value.let {
            optionContent.tvOption.text = HtmlCompat.fromHtml(
                (item.value
                    ?: "") as String, HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            mClassAdapter = OptionSelectionAdapter {

            }
            val layoutManager = LinearLayoutManager(optionContent.context, LinearLayoutManager.VERTICAL,false)
            optionContent.layoutManager = layoutManager
            optionContent.adapter = mClassAdapter


        }

    }
}