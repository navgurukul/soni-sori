package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(val callback: (CourseClassContent) -> Unit):
    DataBoundListAdapter<CourseClassContent, ItemMcqOptionBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<CourseClassContent>(){
    override fun areItemsTheSame(oldItem:CourseClassContent, newItem:CourseClassContent): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CourseClassContent, newItem:CourseClassContent): Boolean {
        return oldItem == newItem
    }
})
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemMcqOptionBinding{
       return DataBindingUtil.inflate(
           LayoutInflater.from(parent.context),
           R.layout.item_mcq_option,parent,false
       )
    }

    override fun bind(
        holder: DataBoundViewHolder<ItemMcqOptionBinding>,
        item: CourseClassContent,
    ) {
       val binding = holder.binding
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}