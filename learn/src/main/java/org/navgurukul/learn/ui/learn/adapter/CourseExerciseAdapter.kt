package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.ExerciseProgress
import org.navgurukul.learn.courses.db.models.ExerciseType
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
        setImageUrl(binding.ivExerciseTypeProgress, item.exerciseType, item.exerciseProgress)
    }

    fun setImageUrl(imageView: ImageView, type: ExerciseType?, progress: ExerciseProgress?) {
        if (type == null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context,R.drawable.ic_type_text_notstarted))
        } else {
            val layoutId = when(type){
                ExerciseType.TEXT -> {
                    when (progress) {
                        ExerciseProgress.COMPLETED -> R.drawable.ic_type_text_complete
                        ExerciseProgress.IN_PROGRESS -> R.drawable.ic_type_text_selected
                        ExerciseProgress.NOT_STARTED -> R.drawable.ic_type_text_notstarted
                        else -> R.drawable.ic_type_text_notstarted
                    }
                }
                else -> R.drawable.ic_type_text_notstarted
            }
            imageView.setBackgroundResource(layoutId)
        }
    }

}