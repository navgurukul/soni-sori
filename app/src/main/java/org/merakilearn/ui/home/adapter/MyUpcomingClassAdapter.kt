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
import org.merakilearn.databinding.ItemUpcomingClassBinding
import org.merakilearn.datasource.network.model.MyClass
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import kotlin.random.Random


class MyUpcomingClassAdapter(val callback: (MyClass) -> Unit) :

    DataBoundListAdapter<MyClass, ItemUpcomingClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<MyClass>() {
            override fun areItemsTheSame(oldItem: MyClass, newItem: MyClass): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: MyClass, newItem: MyClass): Boolean {
                return false
            }
        }
    ) {

    private val backgroundColors = intArrayOf(R.color.upcoming_class_color_1, R.color.upcoming_class_color_2)
    private val backgroundDecorationColors = intArrayOf(R.color.upcoming_class_color_decoration_1, R.color.upcoming_class_color_decoration_2)


    override fun createBinding(parent: ViewGroup, viewType: Int): ItemUpcomingClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_upcoming_class, parent, false
        )
    }

    override fun bind(binding: ItemUpcomingClassBinding, item: MyClass) {
        binding.classes = item.classX
        binding.root.setOnClickListener {
            callback.invoke(item)
        }

        val backgroundIndex = Random.nextInt(0, backgroundColors.size)

        (binding.root as CardView).apply {
            setCardBackgroundColor(ContextCompat.getColor(context, backgroundColors[backgroundIndex]))
        }

        binding.ivDecoration.apply {
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, backgroundDecorationColors[backgroundIndex])))
        }
    }

}