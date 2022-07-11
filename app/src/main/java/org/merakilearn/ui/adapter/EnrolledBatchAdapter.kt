package org.merakilearn.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemEnrolledBatchBinding
import org.merakilearn.datasource.network.model.Batches
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class EnrolledBatchAdapter(val callback: (Batches) -> Unit):
    DataBoundListAdapter<Batches, ItemEnrolledBatchBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Batches>(){
            override fun areItemsTheSame(oldItem: Batches, newItem: Batches): Boolean {
               return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Batches, newItem: Batches): Boolean {
                return oldItem == newItem
            }
        }
    )
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemEnrolledBatchBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_enrolled_batch,parent,false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemEnrolledBatchBinding>, item: Batches) {
       val binding = holder.binding
        binding.tvBatchTitle.text = item.title
        binding.tvPathwayTitle.text = item.pathwayName
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}


