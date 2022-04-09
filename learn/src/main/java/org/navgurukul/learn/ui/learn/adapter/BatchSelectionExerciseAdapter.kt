package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.dateRange
import org.navgurukul.learn.databinding.ItemBatchBinding
import org.navgurukul.learn.databinding.ItemBatchExerciseBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class BatchSelectionExerciseAdapter(val callback: (Batch) -> Unit):
    DataBoundListAdapter<Batch, ItemBatchExerciseBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Batch>(){
            override fun areItemsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Batch, newItem: Batch): Boolean {
                return oldItem == newItem
            }
        }
    )
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemBatchExerciseBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_batch_exercise, parent, false
        )
    }

//    override fun getItemCount(): Int {
//        return 4
//    }

    override fun bind(holder: DataBoundViewHolder<ItemBatchExerciseBinding>, item: Batch) {
        val binding = holder.binding
        binding.bt1.text = item.title
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }
}
