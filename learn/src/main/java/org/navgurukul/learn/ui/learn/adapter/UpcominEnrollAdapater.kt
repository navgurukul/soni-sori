package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.core.extentions.capitalizeWords
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.UpcomingClass
import org.navgurukul.learn.courses.network.model.displayableLanguage
import org.navgurukul.learn.databinding.ItemUpcomingClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.learn.util.toDate
import org.navgurukul.learn.util.toDay


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
        binding.subTitle.text = item.title
        binding.tvClassType.text  = item.type.capitalizeWords()
        binding.tvClassDate.text = item.startTime.toDate()
        binding.tvFacilatorName.text = item.facilitator?.name
        binding.tvClassLang.text = item.displayableLanguage()
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}


