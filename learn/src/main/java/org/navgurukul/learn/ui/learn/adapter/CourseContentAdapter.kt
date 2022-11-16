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
        setImageUrl(binding.ivExerciseTypeProgress, item.courseContentProgress, item.courseContentType)
    }

    private fun setImageUrl(
        imageView: ImageView,
        progress: CourseContentProgress?,
        contentContentType: CourseContentType
    ) {
        val layoutId =
            when (progress) {
                CourseContentProgress.COMPLETED_RESELECT ->{
                    when(contentContentType){
                        CourseContentType.exercise -> R.drawable.ic_type_text_complete_reselect
                        CourseContentType.class_topic -> R.drawable.ic_type_class_complete_reselect
                        CourseContentType.assessment -> R.drawable.ic_type_question_complete_reselect
                    }
                }
                CourseContentProgress.COMPLETED -> {
                    when(contentContentType){
                        CourseContentType.exercise -> R.drawable.ic_type_text_complete
                        CourseContentType.class_topic -> R.drawable.ic_type_class_complete
                        CourseContentType.assessment -> R.drawable.ic_type_assessment_completed
                    }
                }
                CourseContentProgress.IN_PROGRESS -> {
                    when(contentContentType){
                        CourseContentType.exercise -> R.drawable.ic_type_text_selected
                        CourseContentType.class_topic -> R.drawable.ic_type_class_selected
                        CourseContentType.assessment -> R.drawable.ic_type_assessment_selected
                    }
                }
                CourseContentProgress.NOT_STARTED -> {
                    when(contentContentType){
                        CourseContentType.exercise -> R.drawable.ic_type_text_notstarted
                        CourseContentType.class_topic -> R.drawable.ic_type_class_notstarted
                        CourseContentType.assessment -> R.drawable.ic_type_assessment_notstarted
                    }
                }
                else ->{
                    when(contentContentType){
                        CourseContentType.exercise -> R.drawable.ic_type_text_notstarted
                        CourseContentType.class_topic -> R.drawable.ic_type_class_notstarted
                        CourseContentType.assessment -> R.drawable.ic_type_assessment_notstarted
                    }
                }
            }
        imageView.setBackgroundResource(layoutId)
    }

}