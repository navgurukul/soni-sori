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
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.ui.learn.viewholder.BaseCourseViewHolder

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
        TODO("Not yet implemented")
//        val itemView = inflater.inflate(R.layout.item_base_course_content,
//            parent,
//            false)
//
//        return when (viewType){
//
//            R.layout.item_assesment_content ->
//
//            else ->
//        }
    }

    override fun onBindViewHolder(holder: BaseCourseViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}