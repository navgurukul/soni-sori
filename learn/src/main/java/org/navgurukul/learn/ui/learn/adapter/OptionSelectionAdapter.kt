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

class OptionSelectionAdapter(val callback: (OptionResponse) -> Unit):
    DataBoundListAdapter<OptionResponse, ItemMcqOptionBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<OptionResponse>(){

            override fun areItemsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
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

    override fun bind(holder: DataBoundViewHolder<ItemMcqOptionBinding>, item: OptionResponse) {
        val binding = holder.binding
        binding.tvOption.text = item.value
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}