package org.merakilearn.ui.playground

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.merakilearn.R
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.navgurukul.commonui.platform.BaseViewHolder

class PlaygroundAdapter(val context: Context, val listener: (PlaygroundItemModel,View,Boolean) -> Unit) :
    RecyclerView.Adapter<BaseViewHolder<PlaygroundItemModel>>() {

    private val dataList = arrayListOf<PlaygroundItemModel>()
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<PlaygroundItemModel> {
        return PlaygroundItemViewHolder(inflater.inflate(R.layout.item_playground, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<PlaygroundItemModel>, position: Int) {
        val playgroundItemModel = dataList[position]
        holder.onBind(playgroundItemModel)
        holder.itemView.setOnClickListener {
            listener(playgroundItemModel,it,false)
        }
        holder.itemView.setOnLongClickListener{
            if(playgroundItemModel.file.name == " ")
                return@setOnLongClickListener false
            else {
                listener(playgroundItemModel, it, true)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    //his will only be called once when he data is set
    fun setData(list: List<PlaygroundItemModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}

class PlaygroundItemViewHolder(itemView: View) : BaseViewHolder<PlaygroundItemModel>(itemView) {

    private val ivIcon = itemView.findViewById<ImageView>(R.id.iv_playground_icon)
    private val tvName = itemView.findViewById<TextView>(R.id.tv_playground_name)

    override fun onBind(model: PlaygroundItemModel) {
        ivIcon.setImageResource(model.iconResource)
        tvName.text = if(model.name.isNotBlank()) model.name else model.file.name.replaceAfterLast("_", "").removeSuffix("_")
    }
}


