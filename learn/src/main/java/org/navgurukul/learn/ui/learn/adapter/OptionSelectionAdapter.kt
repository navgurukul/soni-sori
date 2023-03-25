package org.navgurukul.learn.ui.learn.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
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

        binding.tvOption.text = HtmlCompat.fromHtml(
            item.value
                ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        when(item.viewState){
            OptionViewState.SELECTED -> {
                binding.tvCardOption.setStrokeColor(Color.parseColor("#48A145"))
            }
            OptionViewState.NOT_SELECTED -> {
                binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
            }
            OptionViewState.INCORRECT -> {
                binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                binding.tvCardOption.setStrokeColor(Color.parseColor("#F44336"))
            }
            OptionViewState.CORRECT -> {
                binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                binding.tvCardOption.setStrokeColor(Color.parseColor("#48A145"))
            }
        }

        callback?.let {
            binding.root.setOnClickListener { view ->
                it.invoke(item)

            }
        }
    }

}