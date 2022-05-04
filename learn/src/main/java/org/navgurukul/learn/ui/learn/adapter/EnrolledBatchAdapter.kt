package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.databinding.ItemEnrolledBatchBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class EnrolledBatchAdapter(val callback:(Batch) -> Unit):
    DataBoundListAdapter<Batch, ItemEnrolledBatchBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Batch>(){
            override fun areItemsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }

        }
    ){
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemEnrolledBatchBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_enrolled_batch, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemEnrolledBatchBinding>, item: Batch) {
        val binding = holder.binding
//        binding.batch = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}