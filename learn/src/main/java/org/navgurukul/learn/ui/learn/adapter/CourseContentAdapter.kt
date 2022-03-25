package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseContents
import org.navgurukul.learn.courses.db.models.CourseContentProgress
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.databinding.ItemCourseExerciseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class CourseContentAdapter(callback: (CourseContents) -> Unit) :

    DataBoundListAdapter<CourseContents, ItemCourseExerciseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<CourseContents>() {
            override fun areItemsTheSame(oldItem: CourseContents, newItem: CourseContents): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: CourseContents, newItem: CourseContents): Boolean {
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

    override fun bind(holder: DataBoundViewHolder<ItemCourseExerciseBinding>, item: CourseContents) {
        val binding = holder.binding
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
        setImageUrl(binding.ivExerciseTypeProgress, item.courseContentProgress, item.contentContentType)
    }

    private fun setImageUrl(
        imageView: ImageView,
        progress: CourseContentProgress?,
        contentContentType: CourseContentType
    ) {
        val layoutId =
            when (progress) {
                CourseContentProgress.COMPLETED -> {
                    when(contentContentType){
                        CourseContentType.EXERCISE -> R.drawable.ic_type_text_complete
                        CourseContentType.CLASS_TOPIC -> R.drawable.ic_type_text_complete
                    }
                }
                CourseContentProgress.IN_PROGRESS -> {
                    when(contentContentType){
                        CourseContentType.EXERCISE -> R.drawable.ic_type_text_selected
                        CourseContentType.CLASS_TOPIC -> R.drawable.ic_type_text_complete
                    }
                }
                CourseContentProgress.NOT_STARTED -> {
                    when(contentContentType){
                        CourseContentType.EXERCISE -> R.drawable.ic_type_text_notstarted
                        CourseContentType.CLASS_TOPIC -> R.drawable.ic_type_text_complete
                    }
                }
                else -> R.drawable.ic_type_text_notstarted
            }
        imageView.setBackgroundResource(layoutId)
    }

}