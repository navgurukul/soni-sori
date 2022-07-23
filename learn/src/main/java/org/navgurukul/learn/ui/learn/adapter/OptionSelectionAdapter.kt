package org.navgurukul.learn.ui.learn.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R.*
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.courses.db.models.OptionViewState
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(val callback: ((OptionResponse) -> Unit)? = null):
    DataBoundListAdapter<OptionResponse, ItemMcqOptionBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<OptionResponse>(){

            override fun areItemsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
                return oldItem == newItem
            }
        })
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemMcqOptionBinding{
       return DataBindingUtil.inflate(
           LayoutInflater.from(parent.context),
           layout.item_mcq_option,parent,false
       )
    }


    override fun bind(holder: DataBoundListAdapter.DataBoundViewHolder<ItemMcqOptionBinding>, item: OptionResponse) {
        val binding = holder.binding
        binding.tvOption.text = item.value

        when(item.viewState){
            OptionViewState.SELECTED -> {binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#F5F5F5"))}
            OptionViewState.NOT_SELECTED -> {binding.tvCardOption.setBackgroundColor(Color.parseColor("#FFFFFF"))}
            OptionViewState.INCORRECT -> {binding.tvCardOption.setBackgroundColor(Color.parseColor("#FFE5E3"))}
            OptionViewState.CORRECT -> {binding.tvCardOption.setBackgroundColor(Color.parseColor("#E9F5E9"))}
        }

        callback?.let {
            binding.root.setOnClickListener { view ->
                it.invoke(item)

            }
        }
    }

}