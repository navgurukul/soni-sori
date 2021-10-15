package org.navgurukul.learn.ui.learn.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class DotItemDecoration : RecyclerView.ItemDecoration() {

    private val cy1Padding = 80;
    private val cy2Padding = 160;

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = 200
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)

            //skip last item
            if (position != state.itemCount - 1) {
                val mPaint = Paint()
                mPaint.color = Color.rgb(72, 161, 69)
                mPaint.style = Paint.Style.FILL

                val cx: Float = (view.right / 2).toFloat() // center of x
                val cy1: Float = (view.bottom + cy1Padding).toFloat()
                val cy2: Float = (view.bottom + cy2Padding).toFloat()

                c.drawCircle(cx, cy1, 13F, mPaint)
                c.drawCircle(cx, cy2, 13F, mPaint)
            }
        }
    }
}
