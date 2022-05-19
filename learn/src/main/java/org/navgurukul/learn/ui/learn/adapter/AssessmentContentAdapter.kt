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
            R.layout.item_code_content -> CodeCourseViewHolder(itemView)
            R.layout.item_header_content -> HeaderCourseViewHolder(itemView)
            R.layout.item_mcq_option -> OptionCourseViewHolder(itemView)
            else -> UnknownCourseViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: BaseCourseViewHolder, position: Int) {
        when (getItemViewType(position)){
            R.layout.item_text_content ->
                (holder as TextCourseViewHolder).bindView(getItem(position) as TextBaseCourseContent)

            R.layout.item_code_content ->
                (holder as CodeCourseViewHolder).bindView(getItem(position) as CodeBaseCourseContent, mCallback)

            R.layout.item_header_content ->
                (holder as HeaderCourseViewHolder).bindView(getItem(position) as HeaderBaseCourseContent)

            R.layout.item_mcq_option ->
                (holder as OptionCourseViewHolder).bindView(getItem(position) as OptionBaseCourseContent)

        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HeaderBaseCourseContent -> R.layout.item_header_content
            is TextBaseCourseContent -> R.layout.item_text_content
            is CodeBaseCourseContent -> R.layout.item_code_content
            is OptionBaseCourseContent -> R.layout.item_mcq_option

            else -> R.layout.item_base_course_content
        }
    }
}