package org.navgurukul.commonui.skeleton

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import org.navgurukul.commonui.R

class SkeletonAdapter(
    private val items: List<SkeletonItem>,
    private val mItemCount: Int,
    private val mShimmer: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeLayoutMap: Map<Int, Int> = items.map { it.viewType to it.layout }.toMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (mShimmer) {
            ShimmerViewHolder(inflater, parent, viewTypeLayoutMap[viewType]!!)
        } else object :
            RecyclerView.ViewHolder(inflater.inflate(viewType, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mShimmer) {
            val layout = holder.itemView as ShimmerFrameLayout
            layout.startShimmer()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mItemCount
    }

    private fun getItem(position: Int): SkeletonItem {
        return items[position % items.size]
    }
}

class ShimmerViewHolder(inflater: LayoutInflater, parent: ViewGroup?, innerViewResId: Int) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_shimmer, parent, false)) {

    init {
        (itemView as ViewGroup).addView(inflater.inflate(innerViewResId, itemView, false))
    }
}