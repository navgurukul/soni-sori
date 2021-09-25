package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.TextBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemTextContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class TextCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {
    val viewBinding: ItemTextContentBinding

    init {
        viewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_text_content, itemBinding.root as ViewGroup, false
        )

        super.addView(viewBinding.root)
        super.addPlaceholder(viewBinding.root.id)
    }

    fun bindView(item: TextBaseCourseContent) {

        super.bind(item)

        item.value?.let { text ->

            setTextContent(viewBinding.textContent, text)

        }
    }

    private fun setTextContent(textContent: TextView, text: String) {
        textContent.visibility = View.VISIBLE

        textContent.apply {
            this.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }
}

