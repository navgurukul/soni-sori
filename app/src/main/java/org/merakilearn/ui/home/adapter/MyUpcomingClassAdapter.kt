package org.merakilearn.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemUpcomingClassBinding
import org.merakilearn.datasource.network.model.MyClass
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class MyUpcomingClassAdapter(callback: (MyClass) -> Unit) :

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
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemUpcomingClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_upcoming_class, parent, false
        )
    }

    override fun bind(binding: ItemUpcomingClassBinding, item: MyClass) {
        binding.classes = item.classX
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
    }

}