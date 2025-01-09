package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.commonui.platform.BaseViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ItemTableBodyBinding
import org.navgurukul.learn.databinding.ItemTableHeaderBinding

class TableAdapter(val noOfRows: Int, val dataList: List<String>): RecyclerView.Adapter<BaseViewHolder<String>>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_table_body -> {
                val binding = ItemTableBodyBinding.inflate(inflater, parent, false)
                TableContentViewHolder(binding)
            }
            R.layout.item_table_header -> {
                val binding = ItemTableHeaderBinding.inflate(inflater, parent, false)
                TableHeaderViewHolder(binding)
            }
            else -> UnsupportedViewHolder(inflater.inflate(viewType, parent, false))
        }
    }

    class UnsupportedViewHolder constructor(itemView: View) :
        BaseViewHolder<String>(itemView)

    class TableContentViewHolder(private val binding: ItemTableBodyBinding) :
        BaseViewHolder<String>(binding.root) {
        override fun onBind(model: String) {
            super.onBind(model)
            binding.textValue.text = HtmlCompat.fromHtml(model, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    class TableHeaderViewHolder(private val binding: ItemTableHeaderBinding) :
        BaseViewHolder<String>(binding.root) {
        override fun onBind(model: String) {
            super.onBind(model)
            binding.textHeader.text = HtmlCompat.fromHtml(model, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position % noOfRows == 0)
            return R.layout.item_table_header
        else
            return R.layout.item_table_body

    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {
        holder.onBind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}