package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_table_body.view.*
import kotlinx.android.synthetic.main.item_table_header.view.*
import org.navgurukul.commonui.platform.BaseViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ItemTableBodyBinding
import org.navgurukul.learn.databinding.ItemTableHeaderBinding

class TableAdapter(val noOfRows: Int, val dataList: List<String>): RecyclerView.Adapter<BaseViewHolder<String>>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_table_body -> {
                val bindedItemView = DataBindingUtil.inflate<ItemTableBodyBinding>(inflater, viewType, parent, false)
                TableContentViewHolder(bindedItemView)
            }
            R.layout.item_table_header  -> {
                val bindedItemView = DataBindingUtil.inflate<ItemTableHeaderBinding>(inflater, viewType, parent, false)
                TableHeaderViewHolder(bindedItemView)
            }
            else -> UnsupportedViewHolder(inflater.inflate(viewType, parent, false))
        }
    }

    class UnsupportedViewHolder constructor(itemView: View) :
        BaseViewHolder<String>(itemView)

    class TableContentViewHolder constructor(bindedItemView: ItemTableBodyBinding): BaseViewHolder<String>(bindedItemView.root){
        override fun onBind(model: String) {
            super.onBind(model)
            itemView.textValue.text = HtmlCompat.fromHtml(model, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    class TableHeaderViewHolder constructor(bindedItemView: ItemTableHeaderBinding): BaseViewHolder<String>(bindedItemView.root){
        override fun onBind(model: String) {
            super.onBind(model)
            itemView.textHeader.text = HtmlCompat.fromHtml(model, HtmlCompat.FROM_HTML_MODE_COMPACT)
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