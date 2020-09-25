package org.merakilearn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.courses.db.models.Course
import org.merakilearn.databinding.ItemCourseBinding
import org.merakilearn.ui.common.DataBoundListAdapter


class CourseAdapter(callback: (Pair<Course, ItemCourseBinding>) -> Unit) :

    DataBoundListAdapter<Course, ItemCourseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Course>() {
            override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
                return false
            }
        }
    ) {
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemCourseBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_course, parent, false
        )
    }

    override fun bind(binding: ItemCourseBinding, item: Course) {
        binding.course = item
        binding.root.setOnClickListener {
            mCallback.invoke(Pair(item, binding))
        }
    }

}