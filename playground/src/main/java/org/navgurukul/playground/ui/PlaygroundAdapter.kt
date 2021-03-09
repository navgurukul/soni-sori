package org.navgurukul.playground.ui

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.commonui.platform.BaseViewHolder
import org.navgurukul.playground.R
import org.navgurukul.playground.repo.model.PlaygroundItemModel

class PlaygroundAdapter(val context: Context, val listener: (PlaygroundItemModel) -> Unit) :
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
            listener(playgroundItemModel)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

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
        ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ivIcon.context, model.backgroundColor))
        tvName.text = itemView.context.getString(model.name)
    }
}

