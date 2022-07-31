package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.dateRange
import org.navgurukul.learn.databinding.ItemBatchBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class BatchSelectionAdapter(val callback: (Batch) -> Unit):
    DataBoundListAdapter<Batch, ItemBatchBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Batch>(){
            override fun areItemsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }
        }
    ){
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemBatchBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_batch, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemBatchBinding>, item: Batch) {
        val binding = holder.binding
        binding.batch = item
        binding.tvTitleBatch.text = item.title
        binding.tvBatchDate.text = item.dateRange()
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}
