package org.navgurukul.typingguru.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
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

class ParagraphKeysView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defAttrRes: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attrs, defAttrRes, defStyleRes) {

    private val keyHeight = context.resources.getDimensionPixelSize(R.dimen.key_size)
    private val keyHorizontalPadding =
        context.resources.getDimensionPixelSize(commonR.dimen.spacing_4x)
    private val keyMargin = 0

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
    private var currentKeyListNew: List<CourseKeyWord>? = null


    private val inCorrectKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background_incorrect)
    private val correctKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background_correct)
    private val neutralKeyBackgroundDrawable =
        AppCompatResources.getDrawable(context, R.drawable.key_background)

    init {
        orientation = HORIZONTAL
        this.background = AppCompatResources.getDrawable(context, R.drawable.rounded_rectangle)
        setWillNotDraw(false)
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
                //background = getKeyBackground(key.state)
                gravity = Gravity.CENTER
                minimumWidth = keyHeight
            }, LayoutParams(LayoutParams.WRAP_CONTENT, keyHeight).apply {
                marginEnd = keyMargin
                bottomMargin = keyMargin
            })
        }

        invalidate()
    }
    fun setKey(keyList: List<CourseKeyWord>) {

        if (keyList == currentKeyList) {
            updateKeyStateNew(keyList)
            return
        }

        currentKeyListNew = keyList

        removeAllViews()

        for (key in keyList) {
            addView(AppCompatTextView(context).apply {
                text = key.label.toString()
                TextViewCompat.setTextAppearance(
                    this,
                    context.getThemedFontStyle(commonR.attr.textAppearanceEmphasized2)
                )
                setTextColor(context.getThemedColor(commonR.attr.textPrimary))
                //background = getKeyBackgroundNew(key.state)
                gravity = Gravity.CENTER
                minimumWidth = keyHeight
            }, LayoutParams(LayoutParams.WRAP_CONTENT, keyHeight).apply {
                marginEnd = keyMargin
                bottomMargin = keyMargin
            })
        }

        invalidate()
    }

    private fun updateKeyStateNew(keyList: List<CourseKeyWord>) {
        keyList.forEachIndexed { index, key ->
            val selectedKeyView = getChildAt(index) ?: return
            val background = getKeyBackgroundNew(key.state)
            if (background != selectedKeyView.background) {
                selectedKeyView.background = background
            }
        }
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
    private fun getKeyBackgroundNew(state: CourseKeyStateWord) = when (state) {
        CourseKeyStateWord.CORRECT -> correctKeyBackgroundDrawable
        CourseKeyStateWord.INCORRECT -> inCorrectKeyBackgroundDrawable
        CourseKeyStateWord.NEUTRAL -> neutralKeyBackgroundDrawable
    }

    fun shakeCurrentKey() {
        val currentKeyIndex = currentKeyIndex ?: return
        val selectedKeyView = getChildAt(currentKeyIndex) ?: return

        selectedKeyView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.shake))
    }

}