package org.navgurukul.chat.features.home.room.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import im.vector.matrix.android.api.session.room.model.RoomSummary
import org.navgurukul.chat.R
import org.navgurukul.commonui.BaseViewHolder
import org.navgurukul.commonui.DiffUtilCallback

class ChatListAdapter(context: Context): RecyclerView.Adapter<BaseViewHolder<RoomSummaryItem>>() {

    private val dataList: ArrayList<RoomSummaryItem> = arrayListOf()

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RoomSummaryItem> {
        return ChatListItemViewHolder(layoutInflater.inflate(R.layout.chat_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RoomSummaryItem>, position: Int) {
        holder.onBind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun update(rooms: List<RoomSummaryItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(dataList, rooms))
        with(dataList) {
            clear()
            addAll(rooms)
        }
        diffResult.dispatchUpdatesTo(this)
    }
}