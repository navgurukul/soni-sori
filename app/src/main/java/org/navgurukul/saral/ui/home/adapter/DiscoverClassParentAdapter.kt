package org.navgurukul.saral.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.ItemDiscoverClassParentBinding
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.util.DateTimeUtil


class DiscoverClassParentAdapter(callback: (ClassesContainer.Classes) -> Unit) :
    DataBoundListAdapter<DiscoverClassParentAdapter.DiscoverData, ItemDiscoverClassParentBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<DiscoverData>() {
            override fun areItemsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return false
            }
        }
    ) {
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemDiscoverClassParentBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_discover_class_parent, parent, false
        )
    }

    override fun bind(binding: ItemDiscoverClassParentBinding, item: DiscoverData) {
        binding.date = DateTimeUtil.stringToDate(item.date)
        initChildAdapter(item.data, binding)
    }

    private fun initChildAdapter(
        data: List<ClassesContainer.Classes>,
        binding: ItemDiscoverClassParentBinding
    ) {
        val discoverClassChildAdapter = DiscoverClassChildAdapter {
            mCallback.invoke(it)
        }
        val layoutManager =
            GridLayoutManager(binding.root.context, 2)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = discoverClassChildAdapter
        discoverClassChildAdapter.submitList(data)
    }

    data class DiscoverData(var date: String?, var data: MutableList<ClassesContainer.Classes>)
}