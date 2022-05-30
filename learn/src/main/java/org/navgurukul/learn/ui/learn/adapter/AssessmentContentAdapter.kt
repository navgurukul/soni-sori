package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.ui.learn.OutputCourseViewHolder
import org.navgurukul.learn.ui.learn.viewholder.*

class AssessmentContentAdapter(
    context: Context,
    callback: (BaseCourseContent) -> Unit
) :
    ListAdapter<BaseCourseContent, BaseCourseViewHolder>(
        ContentDiffCallback()
    ){
    private val inflater = LayoutInflater.from(context)
    private val mCallback = callback

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCourseViewHolder {
        val itemView = inflater.inflate(R.layout.item_base_course_content, parent,false)

        return when (viewType){

            R.layout.item_text_content -> TextCourseViewHolder(itemView)
            R.layout.item_question_code_content -> QuestionCodeCourseViewHolder(itemView)
            R.layout.item_header_content -> HeaderCourseViewHolder(itemView)
            R.layout.item_option_content-> OptionCourseViewHolder(itemView)
            R.layout.item_output_content -> OutputCourseViewHolder(itemView)
            else -> UnknownCourseViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: BaseCourseViewHolder, position: Int) {
        when (getItemViewType(position)){
            R.layout.item_text_content ->
                (holder as TextCourseViewHolder).bindView(getItem(position) as TextBaseCourseContent)

            R.layout.item_question_code_content ->
                (holder as QuestionCodeCourseViewHolder).bindView(getItem(position) as QuestionCodeBaseCourseContent, mCallback)

            R.layout.item_header_content ->
                (holder as HeaderCourseViewHolder).bindView(getItem(position) as HeaderBaseCourseContent)

            R.layout.item_option_content ->
                (holder as OptionCourseViewHolder).bindView(getItem(position) as OptionBaseCourseContent)

            R.layout.item_output_content ->
                (holder as OutputCourseViewHolder).bindView(getItem(position) as OutputBaseCourseContent)

        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HeaderBaseCourseContent -> R.layout.item_header_content
            is TextBaseCourseContent -> R.layout.item_text_content
            is QuestionCodeBaseCourseContent -> R.layout.item_question_code_content
            is OptionBaseCourseContent -> R.layout.item_option_content
            is OutputBaseCourseContent -> R.layout.item_output_content
            else -> R.layout.item_base_course_content
        }
    }
}