package org.navgurukul.chat.core.extensions

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyVisibilityTracker
import org.navgurukul.chat.R

/**
 * Apply a Vertical LinearLayout Manager to the recyclerView and set the adapter from the epoxy controller
 */
fun RecyclerView.configureWith(epoxyController: EpoxyController,
                               itemAnimator: RecyclerView.ItemAnimator? = null,
                               viewPool: RecyclerView.RecycledViewPool? = null,
                               showDivider: Boolean = false,
                               hasFixedSize: Boolean = true,
                               disableItemAnimation: Boolean = false) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
        recycleChildrenOnDetach = viewPool != null
    }
    setRecycledViewPool(viewPool)
    if (disableItemAnimation) {
        this.itemAnimator = null
    } else {
        itemAnimator?.let { this.itemAnimator = it }
    }
    if (showDivider) {
        addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    ContextCompat.getDrawable(context, R.drawable.divider_horizontal)?.let {
                        setDrawable(it)
                    }
                }
        )
    }
    setHasFixedSize(hasFixedSize)
    adapter = epoxyController.adapter
}

/**
 * To call from Fragment.onDestroyView()
 */
fun RecyclerView.cleanup() {
    adapter = null
}

fun RecyclerView.trackItemsVisibilityChange() = EpoxyVisibilityTracker().attach(this)
