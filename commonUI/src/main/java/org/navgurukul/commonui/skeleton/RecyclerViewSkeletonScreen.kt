package org.navgurukul.commonui.skeleton

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class SkeletonItem(@LayoutRes val layout: Int, val viewType: Int)

class RecyclerViewSkeletonScreen(
    private val recyclerView: RecyclerView,
    private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?,
    items: List<SkeletonItem>,
    private val frozen: Boolean = true,
    itemsCount: Int = 10
) : SkeletonScreen {

    private val skeletonAdapter = SkeletonAdapter(items, itemsCount, true)

    override fun show() {
        recyclerView.adapter = skeletonAdapter
        if (!recyclerView.isComputingLayout && frozen) {
            recyclerView.suppressLayout(true)
        }
    }

    override fun hide() {
        recyclerView.adapter = adapter
    }
}