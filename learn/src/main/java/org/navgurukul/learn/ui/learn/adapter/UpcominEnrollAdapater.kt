package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.UpcomingClass
import org.navgurukul.learn.databinding.ItemUpcomingClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class UpcomingEnrolAdapater(val callback: (UpcomingClass) -> Unit):
    DataBoundListAdapter<UpcomingClass, ItemUpcomingClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<UpcomingClass>(){
            override fun areItemsTheSame(oldItem:UpcomingClass, newItem:UpcomingClass): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UpcomingClass, newItem:UpcomingClass): Boolean {
                return oldItem == newItem
            }
        }
    ){
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemUpcomingClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_upcoming_class,parent,false
        )
    }


    override fun bind(holder: DataBoundViewHolder<ItemUpcomingClassBinding>, item: UpcomingClass) {
        val binding = holder.binding
        binding.upcomingClass = item
        binding.subTitle.text = item.sub_title
        binding.tvTitle.text  = item.title
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}


//data class ClassContainer(val upcomingClass: UpcomingClass)