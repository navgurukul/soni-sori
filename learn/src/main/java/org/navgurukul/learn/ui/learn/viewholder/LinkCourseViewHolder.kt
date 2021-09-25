package org.navgurukul.learn.ui.learn.viewholder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.LinkBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemLinkContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class LinkCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {

    val binding: ItemLinkContentBinding

    init {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_link_content, itemBinding.root as ViewGroup, false
        )

        super.addView(binding.root)

        super.addPlaceholder(binding.root.id)

    }

    fun bindView(item: LinkBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        item.value?.let {

            binding.linkContent.visibility = View.VISIBLE

            binding.linkContent.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
                this.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

                this.setOnClickListener{
                    callback.invoke(item)
                }
            }

        }
    }
}
