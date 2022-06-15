package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.timeDateRange
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
    fun makeSelection(value: String) {
        val mdl = currentList.find {
            it.id == value
        }
        if (mdl != null) {
            currentList.forEach {
                it.isSelected = false
            }
            mdl.isSelected = true
            notifyDataSetChanged()
        }
    }

    override fun bind(
        holder: DataBoundListAdapter.DataBoundViewHolder<ItemRevisionClassBinding>,
        item: CourseClassContent
    ) {
        val binding = holder.binding
        binding.tvClassRevision.text = item.timeDateRange()
        binding.tvClassRevision.isChecked = item.isSelected
        binding.root.setOnClickListener {
            callback.invoke(item)
            item.id.let { it1 -> makeSelection(it1) }
        }
    }

}