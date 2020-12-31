package org.navgurukul.typing.dashboard.beginner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.typing.R

class BeginnerListAdapter(
    context: Context,
    beginnerList: ArrayList<BeginnerListData>?,
    listener: OnItemClickListener
) :
    RecyclerView.Adapter<BeginnerListAdapter.BeginnerListHolder>() {
    private var mListBeginnerData: ArrayList<BeginnerListData>?
    private val listener: OnItemClickListener
    private val mContext: Context

    interface OnItemClickListener {
        fun onItemClick(item: BeginnerListData?)
        fun onStart(item: BeginnerListData?)
    }

    inner class BeginnerListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mItemRow: CardView = view.findViewById<View>(R.id.row) as CardView
        val mTxtTitle: TextView = view.findViewById<View>(R.id.txt_title) as TextView
        val mTxtWpm: TextView = view.findViewById<View>(R.id.txt_wpm) as TextView
        val btnStart: Button = view.findViewById<View>(R.id.btn_start) as Button
    }

    fun updateBeginnerListData(listData: ArrayList<BeginnerListData>?) {
        mListBeginnerData = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BeginnerListHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return BeginnerListHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: BeginnerListHolder,
        position: Int
    ) {
        val beginnerListData: BeginnerListData = mListBeginnerData!![position]
        if (beginnerListData != null) {
            holder.mTxtTitle.setText(beginnerListData.title)
            holder.mTxtWpm.setText("WPM - ${beginnerListData.wpm}")
        }
        holder.btnStart.setOnClickListener { listener.onStart(beginnerListData) }
        holder.mItemRow.setOnClickListener { listener.onItemClick(beginnerListData) }
    }

    override fun getItemCount(): Int {
        return if (mListBeginnerData == null) 0 else mListBeginnerData!!.size
    }

    init {
        mListBeginnerData = beginnerList
        this.listener = listener
        mContext = context
    }
}