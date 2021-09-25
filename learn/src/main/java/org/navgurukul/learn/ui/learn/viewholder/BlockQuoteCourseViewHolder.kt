package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.item_block_quote_content.view.*
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BlockQuoteBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemBlockQuoteContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class BlockQuoteCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {

    val binding: ItemBlockQuoteContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemBlockQuoteContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_block_quote_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)
        super.addPlaceholder(binding.root.id)

    }

    fun bindView(item: BlockQuoteBaseCourseContent) {
        super.bind(item)

        item.value?.let {
            binding.blockQuoteLayout.visibility = View.VISIBLE

            binding.blockQuoteLayout.blockQuoteText.apply {
                this.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }

    }
}

