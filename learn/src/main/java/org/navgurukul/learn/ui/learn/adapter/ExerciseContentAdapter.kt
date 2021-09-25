package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.*
import org.navgurukul.learn.ui.learn.viewholder.*


class ExerciseContentAdapter(
    callback: (BaseCourseContent) -> Unit,
    urlCallback: (BannerAction?) -> Unit
) :
    ListAdapter<BaseCourseContent, BaseCourseViewHolder>(
        ContentDiffCallback()
    ) {

    private val mCallback = callback
    private val mUrlCallback = urlCallback

    class ContentDiffCallback : DiffUtil.ItemCallback<BaseCourseContent>() {
        override fun areItemsTheSame(
            oldItem: BaseCourseContent,
            newItem: BaseCourseContent
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BaseCourseContent,
            newItem: BaseCourseContent
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseCourseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bindedItemView = DataBindingUtil.inflate<ItemBaseCourseContentBinding>(
            inflater,
            R.layout.item_base_course_content,
            parent,
            false
        )

        return when (viewType) {
            R.layout.item_table_content -> TableCourseViewHolder(bindedItemView)
            R.layout.item_text_content -> TextCourseViewHolder(bindedItemView)
            R.layout.item_image_content -> ImageCourseViewHolder(bindedItemView)
            R.layout.item_header_content -> HeaderCourseViewHolder(bindedItemView)
            R.layout.item_youtube_content -> YoutubeCourseViewHolder(bindedItemView)
            R.layout.item_block_quote_content -> BlockQuoteCourseViewHolder(bindedItemView)
            R.layout.item_code_content -> CodeCourseViewHolder(bindedItemView)
            R.layout.item_banner_content -> BannerCourseViewHolder(bindedItemView)
            R.layout.item_link_content -> LinkCourseViewHolder(bindedItemView)
            else -> UnknownCourseViewHolder(bindedItemView)

        }
    }

    override fun onBindViewHolder(
        holder: BaseCourseViewHolder,
        position: Int
    ) {
        when (getItemViewType(position)) {
            R.layout.item_table_content ->
                (holder as TableCourseViewHolder).bindView(getItem(position) as TableBaseCourseContent)

            R.layout.item_text_content ->
                (holder as TextCourseViewHolder).bindView(getItem(position) as TextBaseCourseContent)

            R.layout.item_image_content ->
                (holder as ImageCourseViewHolder).bindView(getItem(position) as ImageBaseCourseContent)

            R.layout.item_header_content ->
                (holder as HeaderCourseViewHolder).bindView(getItem(position) as HeaderBaseCourseContent)

            R.layout.item_youtube_content ->
                (holder as YoutubeCourseViewHolder).bindView(getItem(position) as YoutubeBaseCourseContent)

            R.layout.item_block_quote_content ->
                (holder as BlockQuoteCourseViewHolder).bindView(getItem(position) as BlockQuoteBaseCourseContent)

            R.layout.item_code_content ->
                (holder as CodeCourseViewHolder).bindView(getItem(position) as CodeBaseCourseContent, mCallback)

            R.layout.item_banner_content ->
                (holder as BannerCourseViewHolder).bindView(getItem(position) as BannerBaseCourseContent, mUrlCallback)

            R.layout.item_link_content ->
                (holder as LinkCourseViewHolder).bindView(getItem(position) as LinkBaseCourseContent, mCallback)

            R.layout.item_base_course_content ->
                (holder as UnknownCourseViewHolder).bindView(getItem(position) as UnknownBaseCourseContent)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TableBaseCourseContent -> R.layout.item_table_content
            is ImageBaseCourseContent -> R.layout.item_image_content
            is HeaderBaseCourseContent -> R.layout.item_header_content
            is TextBaseCourseContent -> R.layout.item_text_content
            is YoutubeBaseCourseContent -> R.layout.item_youtube_content
            is BlockQuoteBaseCourseContent -> R.layout.item_block_quote_content
            is CodeBaseCourseContent -> R.layout.item_code_content
            is BannerBaseCourseContent -> R.layout.item_banner_content
            is LinkBaseCourseContent -> R.layout.item_link_content
            else -> R.layout.item_base_course_content
        }
    }
}
