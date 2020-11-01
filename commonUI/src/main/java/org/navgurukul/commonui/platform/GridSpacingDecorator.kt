package org.navgurukul.commonui.platform

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingDecorator(private val verticalSpacing: Int, private val horizontalSpacing: Int, private val spanSize: Int, private val adjustSides: Boolean = true) : RecyclerView.ItemDecoration() {

    /**
     * Set different margins for the items inside the recyclerView: no top itemOffset for the first row
     * and no left itemOffset for the first column.
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (view.layoutParams is GridLayoutManager.LayoutParams && adjustSides) {
            if (spanSize == (view.layoutParams as GridLayoutManager.LayoutParams).spanSize) {
                outRect.set(0, horizontalSpacing, 0, horizontalSpacing)
                return
            }
            val modulus = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex % spanSize
            when (modulus) {
                0 -> outRect.set(horizontalSpacing * 2, verticalSpacing, horizontalSpacing, verticalSpacing)
                spanSize - 1 -> outRect.set(horizontalSpacing, verticalSpacing, horizontalSpacing * 2, verticalSpacing)
                else -> outRect.set(horizontalSpacing, verticalSpacing, horizontalSpacing, verticalSpacing)
            }
        } else {
            outRect.set(horizontalSpacing, verticalSpacing, horizontalSpacing, verticalSpacing)
        }
    }
}