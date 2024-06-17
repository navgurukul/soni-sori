package org.merakilearn.util.webide.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import org.merakilearn.R

class ScrimInsetsFrameLayout : FrameLayout {

    private var mInsetForeground: Drawable? = null

    private var mInsets: Rect? = null
    private val mTempRect = Rect()
    private var mOnInsetsCallback: OnInsetsCallback? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet, defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ScrimInsetsFrameLayout, defStyle, 0
        )
        mInsetForeground = a.getDrawable(
            R.styleable.ScrimInsetsFrameLayout_insetForeground
        )
        a.recycle()
        setWillNotDraw(true)
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        mInsets = Rect(insets)
        setWillNotDraw(mInsetForeground == null)
        ViewCompat.postInvalidateOnAnimation(this)
        mOnInsetsCallback?.let {
            mOnInsetsCallback!!.onInsetsChanged(insets)
        }

        return true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val width = width
        val height = height
        if (mInsets != null && mInsetForeground != null) {
            val sc = canvas.save()
            canvas.translate(scrollX.toFloat(), scrollY.toFloat())

            mTempRect.set(0, 0, width, mInsets!!.top)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            mTempRect.set(0, height - mInsets!!.bottom, width, height)
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            mTempRect.set(
                0,
                mInsets!!.top,
                mInsets!!.left,
                height - mInsets!!.bottom
            )
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            mTempRect.set(
                width - mInsets!!.right,
                mInsets!!.top, width,
                height - mInsets!!.bottom
            )
            mInsetForeground!!.bounds = mTempRect
            mInsetForeground!!.draw(canvas)

            canvas.restoreToCount(sc)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mInsetForeground?.let {
            mInsetForeground!!.callback = this
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mInsetForeground?.let {
            mInsetForeground!!.callback = null
        }
    }

    fun setOnInsetsCallback(onInsetsCallback: OnInsetsCallback) {
        mOnInsetsCallback = onInsetsCallback
    }

    interface OnInsetsCallback {
        fun onInsetsChanged(insets: Rect)
    }
}