package org.merakilearn.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemRecommendedClassBinding
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class OtherCourseAdapter(callback: (Course) -> Unit) :

    DataBoundListAdapter<Course, ItemRecommendedClassBinding>(
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
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemRecommendedClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_recommended_class, parent, false
        )
    }

    override fun bind(binding: ItemRecommendedClassBinding, item: Course) {
        binding.course = item
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
    }

}