package org.merakilearn.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import org.merakilearn.R
import org.merakilearn.databinding.ItemDiscoverClassParentBinding
import org.merakilearn.datasource.network.model.Classes
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class DiscoverClassParentAdapter(val callback: (Classes) -> Unit) :
    DataBoundListAdapter<DiscoverData, ItemDiscoverClassParentBinding>(mDiffCallback = DIFF_UTIL, viewHolderCreator = {
        DiscoverChildViewHolder(it)
    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemDiscoverClassParentBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_discover_class_parent, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemDiscoverClassParentBinding>, item: DiscoverData) {
        (holder as DiscoverChildViewHolder).let {
            holder.childAdapter.callback = {
                callback.invoke(it)
            }
            holder.childAdapter.submitList(item.data)
        }
        holder.binding.date = item.title
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DiscoverData>() {
            override fun areItemsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class DiscoverChildViewHolder(binding: ItemDiscoverClassParentBinding):
    DataBoundListAdapter.DataBoundViewHolder<ItemDiscoverClassParentBinding>(binding) {

    val childAdapter = DiscoverClassChildAdapter()

    init {
        val padding = binding.root.context.resources.getDimensionPixelSize(R.dimen.spacing_2x)
        binding.recyclerview.layoutManager = GridLayoutManager(binding.root.context, 2)
        binding.recyclerview.addItemDecoration(GridSpacingDecorator(padding, padding, 2))
        binding.recyclerview.adapter = childAdapter
    }

}