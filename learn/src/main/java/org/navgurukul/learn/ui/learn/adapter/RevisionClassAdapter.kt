package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.databinding.ItemRevisionClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class RevisionClassAdapter(val callback: (CourseClassContent) -> Unit) :
    DataBoundListAdapter<CourseClassContent, ItemRevisionClassBinding>(
        mDiffCallback =
        object : DiffUtil.ItemCallback<CourseClassContent>() {
            override fun areItemsTheSame(
                oldItem: CourseClassContent,
                newItem: CourseClassContent
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CourseClassContent,
                newItem: CourseClassContent
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemRevisionClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_revision_class, parent, false
        )
    }

    override fun bind(
        holder: DataBoundListAdapter.DataBoundViewHolder<ItemRevisionClassBinding>,
        item: CourseClassContent
    ) {
        val binding = holder.binding
//        binding.batch = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}