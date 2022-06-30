package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.core.extentions.capitalizeWords
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.displayableLanguage
import org.navgurukul.learn.databinding.ItemUpcomingClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.learn.util.toDate


class UpcomingEnrolAdapater(val callback: (CourseClassContent) -> Unit):
    DataBoundListAdapter<CourseClassContent, ItemUpcomingClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<CourseClassContent>(){
            override fun areItemsTheSame(oldItem:CourseClassContent, newItem:CourseClassContent): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CourseClassContent, newItem:CourseClassContent): Boolean {
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

    override fun bind(holder: DataBoundViewHolder<ItemUpcomingClassBinding>, item: CourseClassContent) {
        val binding = holder.binding
        binding.upcomingClass = item
        binding.subTitle.text = item.subTitle ?: item.title
        binding.tvClassType.text  = item.type.name.capitalizeWords()
        binding.tvClassDate.text = item.startTime.toDate()
        binding.tvClassLang.text = item.displayableLanguage()
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}


