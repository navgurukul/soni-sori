package org.merakilearn.ui.discover

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemClassBinding
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.datasource.network.model.dateFormatUI
import org.merakilearn.datasource.network.model.sanitizedType
import org.merakilearn.datasource.network.model.timeRange
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class DiscoverClassChildAdapter(var callback: ((Classes) -> Unit)? = null) :

    DataBoundListAdapter<Classes, ItemClassBinding>(
        mDiffCallback = DIFF_CALLBACK
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_class, parent, false
        )
    }

    private val backgroundColors = intArrayOf(R.color.upcoming_class_color_1, R.color.upcoming_class_color_2)
    private val backgroundDecorationColors = intArrayOf(R.color.upcoming_class_color_decoration_1, R.color.upcoming_class_color_decoration_2)

    override fun bind(holder: DataBoundViewHolder<ItemClassBinding>, item: Classes) {
        holder.binding.classes = item
        holder.binding.tvClassTitle.text = item.sanitizedType()
        holder.binding.tvClassTiming.text = item.timeRange()
        holder.binding.tvClassDate.text = item.dateFormatUI()
        holder.binding.root.setOnClickListener {
            callback?.invoke(item)
        }

        val backgroundIndex = item.type.first().toInt().rem(2)

        (holder.binding.root as CardView).apply {
            setCardBackgroundColor(ContextCompat.getColor(context, backgroundColors[backgroundIndex]))
        }

        holder.binding.ivDecoration.apply {
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, backgroundDecorationColors[backgroundIndex])))
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Classes>() {
                override fun areItemsTheSame(
                    oldItem: Classes,
                    newItem: Classes
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Classes,
                    newItem: Classes
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}