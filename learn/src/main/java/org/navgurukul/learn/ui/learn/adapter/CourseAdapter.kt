package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import org.navgurukul.commonui.platform.SvgLoader
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.network.PathwayData
import org.navgurukul.learn.databinding.ItemCourseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class CourseAdapter(private val context: Context, val callback: (Course) -> Unit) :

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

    fun submitList(list: List<Course>, logo: String?, pathwayData : List<PathwayData>) {
        submitList(list.map { CourseContainer(it, logo, pathwayData) })
    }

    override fun getItemViewType(position: Int): Int {

        //TODO return view type LOCKED or UNLOCKED
        return super.getItemViewType(position)
    }

    @SuppressLint("CheckResult")
    override fun bind(holder: DataBoundViewHolder<ItemCourseBinding>, item: CourseContainer) {
        val binding = holder.binding
        binding.course = item.course

        if (item.logo?.endsWith(".svg") == true) {
            SvgLoader(context).loadSvgFromUrl(item.logo, binding.ivLogo)
        } else{
            val thumbnail = Glide.with(holder.itemView)
                .load(R.drawable.ic_lock)
            Glide.with(binding.ivLogo)
                .load(item.logo)
                .thumbnail(thumbnail)
                .into(binding.ivLogo)
        }

        // TODO set progress from the object
        binding.progressBar.progress = item.pathwayData.firstOrNull { it.courseId.toString() == item.course.id }?.completedPortion ?: 0
        binding.tvName.text = item.course.name

        binding.root.setOnClickListener {
            callback.invoke(item.course)
        }
    }
}

data class CourseContainer(val course: Course, val logo: String?, val pathwayData: List<PathwayData>)