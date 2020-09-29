package org.merakilearn.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemDiscoverClassChildBinding
import org.merakilearn.datasource.network.model.Classes
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class DiscoverClassChildAdapter(callback: (Classes) -> Unit) :

    DataBoundListAdapter<Classes, ItemDiscoverClassChildBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Classes>() {
            override fun areItemsTheSame(
                oldItem: Classes,
                newItem: Classes
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: Classes,
                newItem: Classes
            ): Boolean {
                return false
            }
        }
    ) {
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemDiscoverClassChildBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_discover_class_child, parent, false
        )
    }

    override fun bind(binding: ItemDiscoverClassChildBinding, item: Classes) {
        binding.data = item
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
    }
}