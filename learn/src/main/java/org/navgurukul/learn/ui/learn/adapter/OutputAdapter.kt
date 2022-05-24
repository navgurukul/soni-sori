//package org.navgurukul.learn.ui.learn.adapter
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.ListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import org.navgurukul.learn.R
//import org.navgurukul.learn.courses.db.models.*
//import org.navgurukul.learn.ui.learn.viewholder.*
//
//
//class OutputAdapter(context: Context, callback: (BaseCourseContent)-> Unit):
//    ListAdapter<BaseCourseContent, BaseCourseViewHolder>(
//        ContentDiffCallback()
//    ){
//    private val inflater = LayoutInflater.from(context)
//    private val mCallback = callback
//
//    class ContentDiffCallback : DiffUtil.ItemCallback<BaseCourseContent>() {
//        override fun areItemsTheSame(
//            oldItem: BaseCourseContent,
//            newItem: BaseCourseContent
//        ): Boolean {
//            return oldItem == newItem
//        }
//
//        @SuppressLint("DiffUtilEquals")
//        override fun areContentsTheSame(
//            oldItem: BaseCourseContent,
//            newItem: BaseCourseContent
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCourseViewHolder {
//        val itemView = inflater.inflate(R.layout.item_base_course_content, parent,false)
//
//        return when (viewType){
//            R.layout.item_text_content -> TextCourseViewHolder(itemView)
//            R.layout.item_header_content -> HeaderCourseViewHolder(itemView)
//            else -> UnknownCourseViewHolder(itemView)
//        }
//    }
//
//    fun onBindViewHolder(holder: BaseCourseViewHolder, position: Int) {
//        when (getItemViewType(position)){
//            R.layout.item_text_content ->
//                (holder as TextCourseViewHolder)
//            R.layout.item_header_content ->
//                (holder as HeaderCourseViewHolder).bindView(getItem(position) as HeaderBaseCourseContent)
//
//
//        }
//    }
//    fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is HeaderBaseCourseContent -> R.layout.item_header_content
//            is TextBaseCourseContent -> R.layout.item_text_content
//            else -> R.layout.item_base_course_content
//        }
//    }
//    }