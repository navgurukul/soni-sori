package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CodeType
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemCodeContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter


class CodeCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {

    val binding: ItemCodeContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemCodeContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_code_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)
        super.addPlaceholder(binding.root.id)

    }

    fun bindView(item: CodeBaseCourseContent, callback: (BaseCourseContent) -> Unit) {
        super.bind(item)

        binding.codeLayout.visibility = View.VISIBLE

        binding.codeTitle.visibility = View.GONE
        item.title?.let {
            binding.codeTitle.visibility = View.VISIBLE
            binding.codeTitle.text = it
        }

        when (item.codeTypes) {
            CodeType.javascript -> {
                binding.imageViewPlay.visibility = View.GONE
            }
            CodeType.python -> {
                binding.imageViewPlay.visibility = View.VISIBLE
                binding.imageViewPlay.setOnClickListener{
                    callback.invoke(item)
                }
            }
            else -> {
                binding.imageViewPlay.visibility = View.GONE
            }
        }

        binding.codeBody.text = HtmlCompat.fromHtml(
            item.value
                ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT
        )

    }
}

