package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.UpcomingClass
import org.navgurukul.learn.databinding.ItemUpcomingClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class UpcomingEnrolAdapater(val callback: (UpcomingClass) -> Unit,):
    DataBoundListAdapter<ClassContainer, ItemUpcomingClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<ClassContainer>(){
            override fun areItemsTheSame(oldItem:ClassContainer, newItem:ClassContainer): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ClassContainer,
                newItem: ClassContainer
            ): Boolean {
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

    override fun bind(holder: DataBoundViewHolder<ItemUpcomingClassBinding>, item: ClassContainer) {
        val binding = holder.binding
        binding.upcomingClass = item.upcomingClass
        binding.subTitle.text = item.upcomingClass.sub_title
//        binding.subTitle.text = UpcomingClass
        binding.root.setOnClickListener {
            callback.invoke(item.upcomingClass)
        }
    }

}


data class ClassContainer(val upcomingClass: UpcomingClass)