package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.databinding.ItemRevisionClassBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class RevisionClassSelectionAdapter(val callback:(Batch) -> Unit):
    DataBoundListAdapter<Batch, ItemRevisionClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Batch>(){
            override fun areItemsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }
        }
    ){
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemRevisionClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_revision_class, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemRevisionClassBinding>, item: Batch) {
        val binding = holder.binding
//        binding.batch = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}
