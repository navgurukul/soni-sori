package org.navgurukul.typingguru.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import org.navgurukul.commonui.themes.getThemedColor
import org.navgurukul.commonui.themes.getThemedFontStyle
import org.navgurukul.commonui.themes.getThemedUnit
import org.navgurukul.typingguru.R
import org.navgurukul.commonui.R as commonR

class CourseKeysView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defAttrRes: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attrs, defAttrRes, defStyleRes) {

    private val keyHeight = context.resources.getDimensionPixelSize(R.dimen.key_size)
    private val keyHorizontalPadding =
        context.resources.getDimensionPixelSize(commonR.dimen.spacing_4x)
    private val keyMargin = context.resources.getDimensionPixelSize(commonR.dimen.spacing_1x)

    var currentKeyIndex: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    private val selectedKeyIndicatorHeight = context.getThemedUnit(commonR.attr.borderWidth)

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.current_text)
        style = Paint.Style.FILL
    }

    private var currentKeyList: List<CourseKey>? = null

    private val inCorrectKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background_incorrect)
    private val correctKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background_correct)
    private val neutralKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background)

    init {
        orientation = HORIZONTAL
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val currentKeyIndex = currentKeyIndex ?: return
        val selectedKeyView = getChildAt(currentKeyIndex) ?: return

        if (canvas == null) {
            return
        }

        canvas.drawRect(
            selectedKeyView.x,
            (height - selectedKeyIndicatorHeight).toFloat(),
            selectedKeyView.x + selectedKeyView.width,
            height.toFloat(),
            paint
        )
    }

    fun setKeys(keyList: List<CourseKey>) {

        if (keyList == currentKeyList) {
            updateKeyState(keyList)
            return
        }

        currentKeyList = keyList

        removeAllViews()

        for (key in keyList) {
            addView(AppCompatTextView(context).apply {
                text = key.label.toString()
                TextViewCompat.setTextAppearance(
                    this,
                    context.getThemedFontStyle(commonR.attr.textAppearanceEmphasized2)
                )
                setTextColor(context.getThemedColor(commonR.attr.textPrimary))
                background = getKeyBackground(key.state)
                gravity = Gravity.CENTER
                setPaddingRelative(keyHorizontalPadding, 0, keyHorizontalPadding, 0)
                minimumWidth = keyHeight
            }, LayoutParams(LayoutParams.WRAP_CONTENT, keyHeight).apply {
                marginEnd = keyMargin
                bottomMargin = keyMargin
            })
        }

        invalidate()
    }

    private fun updateKeyState(keyList: List<CourseKey>) {
        keyList.forEachIndexed { index, key ->
            val selectedKeyView = getChildAt(index) ?: return
            val background = getKeyBackground(key.state)
            if (background != selectedKeyView.background) {
                selectedKeyView.background = background
            }
        }
    }

    private fun getKeyBackground(state: CourseKeyState) = when (state) {
        CourseKeyState.CORRECT -> correctKeyBackgroundDrawable
        CourseKeyState.INCORRECT -> inCorrectKeyBackgroundDrawable
        CourseKeyState.NEUTRAL -> neutralKeyBackgroundDrawable
    }

    fun shakeCurrentKey() {
        val currentKeyIndex = currentKeyIndex ?: return
        val selectedKeyView = getChildAt(currentKeyIndex) ?: return

        selectedKeyView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.shake))
    }

}