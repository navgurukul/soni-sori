package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.commonui.platform.BaseViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.HeaderBaseCourseContent
import org.navgurukul.learn.courses.db.models.TextBaseCourseContent
import org.navgurukul.learn.ui.learn.OutputCourseViewHolder
import org.navgurukul.learn.ui.learn.viewholder.BaseCourseViewHolder
import org.navgurukul.learn.ui.learn.viewholder.HeaderCourseViewHolder
import org.navgurukul.learn.ui.learn.viewholder.TextCourseViewHolder
import org.navgurukul.learn.ui.learn.viewholder.UnknownCourseViewHolder

class IncorrectOutputAdapter
    (context: Context,
     callback: (BaseCourseContent)-> Unit
): RecyclerView.Adapter<BaseCourseViewHolder>(
    )
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCourseViewHolder {
           val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_base_course_content, parent, false)
           return when(viewType) {
               R.layout.item_text_content -> TextCourseViewHolder(itemView)
               R.layout.item_header_content -> HeaderCourseViewHolder(itemView)
               else -> UnknownCourseViewHolder(itemView)
           }
        }

        override fun onBindViewHolder(holder: BaseCourseViewHolder, position: Int) {
            when (getItemViewType(position)){
                R.layout.item_text_content ->
                    (holder as TextCourseViewHolder).bindView(getItemId(position) as TextBaseCourseContent)

                R.layout.item_header_content ->
                    (holder as HeaderCourseViewHolder).bindView(getItemId(position) as HeaderBaseCourseContent)
            }
        }


        override fun getItemCount(): Int {
           return itemCount
        }


    }


