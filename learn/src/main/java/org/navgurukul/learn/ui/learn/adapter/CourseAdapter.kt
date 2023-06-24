package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.databinding.ItemCourseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class CourseAdapter(val callback: (Course) -> Unit) :

    DataBoundListAdapter<CourseContainer, ItemCourseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<CourseContainer>() {
            override fun areItemsTheSame(
                oldItem: CourseContainer,
                newItem: CourseContainer
            ): Boolean {
                return oldItem.course == newItem.course
            }

            override fun areContentsTheSame(
                oldItem: CourseContainer,
                newItem: CourseContainer
            ): Boolean {
                return oldItem.course == newItem.course
            }
        }
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemCourseBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_course, parent, false
        )
    }

    fun submitList(list: List<Course>, logo: String?) {
        submitList(list.map { CourseContainer(it, logo) })
    }

    override fun getItemViewType(position: Int): Int {

        //TODO return view type LOCKED or UNLOCKED
        return super.getItemViewType(position)
    }

    override fun bind(holder: DataBoundViewHolder<ItemCourseBinding>, item: CourseContainer) {
        val binding = holder.binding
        binding.course = item.course

        val thumbnail = Glide.with(holder.itemView)
            .load(R.drawable.ic_lock)
        Glide.with(binding.ivLogo)
            .load(item.logo)
            .thumbnail(thumbnail)
            .into(binding.ivLogo)

        // TODO set progress from the object
        binding.progressBar.progress = item.course.completedPortion?:0
        binding.tvName.text = item.course.name

        binding.root.setOnClickListener {
            callback.invoke(item.course)
        }
    }
}

data class CourseContainer(val course: Course, val logo: String?)