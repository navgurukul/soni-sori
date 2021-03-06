package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.databinding.ItemCourseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class CourseAdapter(val callback: (Course) -> Unit) :

    DataBoundListAdapter<Course, ItemCourseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Course>() {
            override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemCourseBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_course, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemCourseBinding>, item: Course) {
        val binding = holder.binding
        binding.course = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}