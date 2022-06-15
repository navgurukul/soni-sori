package org.navgurukul.learn.ui.learn.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R


class DotItemDecoration(val context: Context) : RecyclerView.ItemDecoration() {

    companion object {
        const val NUMBER_DOTS = 2
    }

    private val dotRadius = context.resources.getDimensionPixelSize(R.dimen.dot_radius)
    private val dotPadding = context.resources.getDimensionPixelSize(R.dimen.spacing_2x)
    private val verticalPadding = context.resources.getDimensionPixelSize(R.dimen.spacing_4x)
    private val color = ContextCompat.getColor(context, R.color.courseBackground)
    private val paint =  Paint().apply {
        color = this@DotItemDecoration.color
        style = Paint.Style.FILL
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = (verticalPadding * 2) + (dotPadding) + (dotRadius * 2 * NUMBER_DOTS)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)

            //skip last item
            if (position != state.itemCount - 1) {

                val cx: Float = (view.right / 2).toFloat() // center of x
                var cy: Float = (view.bottom + verticalPadding + dotRadius).toFloat()

                repeat(NUMBER_DOTS) {
                    c.drawCircle(cx, cy, dotRadius.toFloat(), paint)
                    cy += dotPadding + dotRadius
                }
            }
        }
    }
}
