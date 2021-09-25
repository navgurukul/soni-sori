package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.HeaderBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemHeaderContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter


class HeaderCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {
    val binding: ItemHeaderContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemHeaderContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_header_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)

        super.addPlaceholder(binding.root.id)
    }

    fun bindView(item: HeaderBaseCourseContent) {
        super.bind(item)


        item.value?.let {
            binding.titleContent.visibility = View.VISIBLE

            binding.titleContent.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }
    }
}

