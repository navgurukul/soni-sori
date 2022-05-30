package org.navgurukul.learn.ui.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_content.view.*
import kotlinx.android.synthetic.main.item_table_header.view.*
import kotlinx.android.synthetic.main.item_text_content.view.*
import org.navgurukul.commonui.platform.BaseViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.databinding.ItemHeaderContentBinding
import org.navgurukul.learn.databinding.ItemTableHeaderBinding
import org.navgurukul.learn.databinding.ItemTextContentBinding


class OutputAdapter(
    val dataList: List<BaseCourseContent>
):RecyclerView.Adapter<BaseViewHolder<BaseCourseContent>>(
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BaseCourseContent> {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            R.layout.item_text_content -> {
                val bindItemView = DataBindingUtil.inflate<ItemTextContentBinding>(inflater, viewType, parent, false)
                TextViewHolder(bindItemView)
            }

            R.layout.item_header_content ->{
                val bindedItemView = DataBindingUtil.inflate<ItemHeaderContentBinding>(inflater, viewType, parent, false)
                HeaderViewHolder(bindedItemView)

            }

//            else ->{ UnsupportedViewHolder(inflater.inflate(viewType, parent, false))}
            else -> {
                val bindedItemView = DataBindingUtil.inflate<ItemHeaderContentBinding>(inflater, viewType, parent, false)
                HeaderViewHolder(bindedItemView)
            }
        }
    }
    class UnsupportedViewHolder constructor(itemView: View) :
        BaseViewHolder<BaseCourseContent>(itemView)

    class HeaderViewHolder constructor(bindedItemView: ItemHeaderContentBinding): BaseViewHolder<BaseCourseContent>(bindedItemView.root) {
        override fun onBind(model: BaseCourseContent) {
            super.onBind(model)
            itemView.titleContent.text =
                HtmlCompat.fromHtml(model.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    class TextViewHolder constructor(bindedItemView: ItemTextContentBinding): BaseViewHolder<BaseCourseContent>(bindedItemView.root){
        override fun onBind(model: BaseCourseContent) {
            super.onBind(model)
            itemView.textContent.text = HtmlCompat.fromHtml(model.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseCourseContent>, position: Int) {
        holder.onBind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}