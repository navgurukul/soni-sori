package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.OptionBaseCourseContent
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.databinding.ItemOptionContentBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(val callback: (List<OptionResponse>) -> Unit):
    DataBoundListAdapter<List<OptionResponse>, ItemOptionContentBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<List<OptionResponse>>(){
    override fun areItemsTheSame(oldItem:List<OptionResponse>, newItem:List<OptionResponse>): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: List<OptionResponse>, newItem:List<OptionResponse>): Boolean {
        return oldItem == newItem
    }
})
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemOptionContentBinding{
       return DataBindingUtil.inflate(
           LayoutInflater.from(parent.context),
           R.layout.item_option_content,parent,false
       )
    }

    override fun bind(
        holder: DataBoundViewHolder<ItemOptionContentBinding>,
        item: List<OptionResponse>,
    ) {
       val binding = holder.binding
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}