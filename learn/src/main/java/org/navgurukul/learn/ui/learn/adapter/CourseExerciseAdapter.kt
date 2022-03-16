package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseExerciseContent
import org.navgurukul.learn.courses.db.models.CourseContentProgress
import org.navgurukul.learn.databinding.ItemCourseExerciseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class CourseExerciseAdapter(callback: (CourseExerciseContent) -> Unit) :

    DataBoundListAdapter<CourseExerciseContent, ItemCourseExerciseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<CourseExerciseContent>() {
            override fun areItemsTheSame(oldItem: CourseExerciseContent, newItem: CourseExerciseContent): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: CourseExerciseContent, newItem: CourseExerciseContent): Boolean {
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

    override fun bind(holder: DataBoundViewHolder<ItemCourseExerciseBinding>, item: CourseExerciseContent) {
        val binding = holder.binding
        binding.exercise = item
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
        setImageUrl(binding.ivExerciseTypeProgress, item.courseContentProgress)
    }

    private fun setImageUrl(imageView: ImageView, progress: CourseContentProgress?) {
        val layoutId =
            when (progress) {
                CourseContentProgress.COMPLETED -> R.drawable.ic_type_text_complete
                CourseContentProgress.IN_PROGRESS -> R.drawable.ic_type_text_selected
                CourseContentProgress.NOT_STARTED -> R.drawable.ic_type_text_notstarted
                else -> R.drawable.ic_type_text_notstarted
            }
        imageView.setBackgroundResource(layoutId)
    }

}