package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.databinding.ItemCourseExerciseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class CourseExerciseAdapter(callback: (Pair<Exercise, ItemCourseExerciseBinding>) -> Unit) :

    DataBoundListAdapter<Exercise, ItemCourseExerciseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return false
            }
        }
    ) {
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemCourseExerciseBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_course_exercise, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemCourseExerciseBinding>, item: Exercise) {
        val binding = holder.binding
        binding.exercise = item
        binding.root.setOnClickListener {
            mCallback.invoke(Pair(item, binding))
        }
    }

}