package org.merakilearn.ui.home.adapter

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
import org.merakilearn.datasource.network.model.MyClass
import org.merakilearn.datasource.network.model.sanitizedType
import org.merakilearn.datasource.network.model.timeRange
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class MyUpcomingClassAdapter(val callback: (MyClass) -> Unit) :

    DataBoundListAdapter<MyClass, ItemClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<MyClass>() {
            override fun areItemsTheSame(oldItem: MyClass, newItem: MyClass): Boolean {
                return oldItem.classes.id == newItem.classes.id
            }

            override fun areContentsTheSame(oldItem: MyClass, newItem: MyClass): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    private val backgroundColors = intArrayOf(R.color.upcoming_class_color_1, R.color.upcoming_class_color_2)
    private val backgroundDecorationColors = intArrayOf(R.color.upcoming_class_color_decoration_1, R.color.upcoming_class_color_decoration_2)


    override fun createBinding(parent: ViewGroup, viewType: Int): ItemClassBinding {
        val binding: ItemClassBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_class, parent, false
        )

        binding.root.post {
            binding.root.layoutParams.width = parent.context.resources.getDimensionPixelSize(R.dimen.dimen_width_upcoming)
        }

        return binding
    }

    override fun bind(holder: DataBoundViewHolder<ItemClassBinding>, item: MyClass) {
        val binding = holder.binding
        binding.classes = item.classes
        holder.binding.tvClassTitle.text = item.classes.sanitizedType()
        holder.binding.tvClassTiming.text = item.classes.timeRange()

        binding.root.setOnClickListener {
            callback.invoke(item)
        }

        val backgroundIndex = item.classes.type.first().toInt().rem(2)

        (binding.root as CardView).apply {
            setCardBackgroundColor(ContextCompat.getColor(context, backgroundColors[backgroundIndex]))
        }

        binding.ivDecoration.apply {
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, backgroundDecorationColors[backgroundIndex])))
        }
    }

}