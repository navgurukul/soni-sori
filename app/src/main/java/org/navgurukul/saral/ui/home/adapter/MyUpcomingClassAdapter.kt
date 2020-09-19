package org.navgurukul.saral.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.ItemUpcomingClassBinding
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.datasource.network.model.MyClassContainer


class MyUpcomingClassAdapter(callback: (MyClassContainer.MyClass) -> Unit) :

    DataBoundListAdapter<MyClassContainer.MyClass, ItemUpcomingClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<MyClassContainer.MyClass>() {
            override fun areItemsTheSame(oldItem: MyClassContainer.MyClass, newItem: MyClassContainer.MyClass): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: MyClassContainer.MyClass, newItem: MyClassContainer.MyClass): Boolean {
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

    override fun bind(binding: ItemUpcomingClassBinding, item: MyClassContainer.MyClass) {
        binding.classes = item.classes.first()
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
    }

}