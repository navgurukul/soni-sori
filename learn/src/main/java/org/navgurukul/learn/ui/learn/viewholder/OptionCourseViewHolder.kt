package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.courses.db.models.OptionsBaseCourseContent
import org.navgurukul.learn.ui.learn.adapter.OptionSelectionAdapter

class OptionCourseViewHolder(itemView: View):
    BaseCourseViewHolder(itemView) {

        private val optionContentView: ConstraintLayout = populateStub(R.layout.item_options_list_content)
        private val optionContent: RecyclerView = optionContentView.findViewById(R.id.optionLayout)
        private lateinit var optionsAdapter: OptionSelectionAdapter


    override val horizontalMargin: Int
        get() = optionContent.context.resources.getDimensionPixelOffset(R.dimen.dimen_course_content_margin)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: OptionsBaseCourseContent, mOptionCallback: ((OptionResponse) -> Unit)?) {
        super.bind(item)

        item.value.let {
            mOptionCallback?.let { callback ->
                optionsAdapter = OptionSelectionAdapter() {
                    callback.invoke(it)
                }
            }?: kotlin.run {
                optionsAdapter = OptionSelectionAdapter()
            }
            val layoutManager = LinearLayoutManager(optionContent.context, LinearLayoutManager.VERTICAL,false)
            optionContent.layoutManager = layoutManager
            optionContent.adapter = optionsAdapter
            optionsAdapter.submitList(it)

        }

    }
}