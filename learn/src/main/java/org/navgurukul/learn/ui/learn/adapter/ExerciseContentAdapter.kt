package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.ui.learn.viewholder.*


class ExerciseContentAdapter(
    context: Context,
    callback: (BaseCourseContent) -> Unit,
    urlCallback: (BannerAction?) -> Unit,
    optionCallback: ((OptionResponse) -> Unit) ?= null,

) :
    ListAdapter<BaseCourseContent, BaseCourseViewHolder>(
        ContentDiffCallback()
    ) {

    private val inflater = LayoutInflater.from(context)
    private val mCallback = callback
    private val mOptionCallback = optionCallback
    private val mUrlCallback = urlCallback
    private val glideRequests: RequestManager by lazy { Glide.with(context) }

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

        val itemView = inflater.inflate(R.layout.item_base_course_content,
            parent,
            false)

        return when (viewType) {
            R.layout.item_table_content -> TableCourseViewHolder(itemView)
            R.layout.item_text_content -> TextCourseViewHolder(itemView)
            R.layout.item_image_content -> ImageCourseViewHolder(itemView, glideRequests)
            R.layout.item_header_content -> HeaderCourseViewHolder(itemView)
            R.layout.item_youtube_content -> YoutubeCourseViewHolder(itemView)
            R.layout.item_block_quote_content -> BlockQuoteCourseViewHolder(itemView)
            R.layout.item_code_content -> CodeCourseViewHolder(itemView)
            R.layout.item_banner_content -> BannerCourseViewHolder(itemView)
            R.layout.item_link_content -> LinkCourseViewHolder(itemView)
            R.layout.item_question_code_content -> QuestionCodeCourseViewHolder(itemView)
            R.layout.item_question_expression_content -> QuestionExpressionCourseViewHolder(itemView)
            R.layout.item_options_list_content-> OptionCourseViewHolder(itemView)
            else -> UnknownCourseViewHolder(itemView)

        }
    }

    override fun onBindViewHolder(
        holder: BaseCourseViewHolder,
        position: Int
    ) {

        if (getItemViewType(position)== BaseCourseContent.COMPONENT_SOLUTION.length){
            notifyItemRemoved(position)
        }
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

            R.layout.item_question_code_content ->
                (holder as QuestionCodeCourseViewHolder).bindView(getItem(position) as QuestionCodeBaseCourseContent, mCallback)

            R.layout.item_question_expression_content ->
                (holder as QuestionExpressionCourseViewHolder).bindView(getItem(position) as QuestionExpressionBaseCourseContent, mCallback)

            R.layout.item_options_list_content ->
                (holder as OptionCourseViewHolder).bindView(getItem(position) as OptionsBaseCourseContent, mOptionCallback)

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
            is QuestionCodeBaseCourseContent -> R.layout.item_question_code_content
            is QuestionExpressionBaseCourseContent -> R.layout.item_question_expression_content
            is OptionsBaseCourseContent -> R.layout.item_options_list_content
            else -> R.layout.item_base_course_content
        }
    }
}
