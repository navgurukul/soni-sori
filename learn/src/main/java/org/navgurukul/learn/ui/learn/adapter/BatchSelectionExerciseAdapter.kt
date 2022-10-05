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

    fun makeSelection(value: Int) {
        val mdl = currentList.find {
            it.id == value
        }
        if (mdl != null) {
            currentList.forEach {
                it.isSelected = false
            }
            mdl.isSelected = true
            notifyDataSetChanged()
        }
    }
    override fun bind(holder: DataBoundViewHolder<ItemBatchExerciseBinding>, item: Batch) {
        val binding = holder.binding
        binding.batchDate.text = item.dateRange()
        binding.title.text =item.title
        binding.bt1.isChecked = item.isSelected
        binding.bt1.setOnClickListener {
            callback.invoke(item)
            item.id?.let { it1 -> makeSelection(it1) }
        }
    }
}
