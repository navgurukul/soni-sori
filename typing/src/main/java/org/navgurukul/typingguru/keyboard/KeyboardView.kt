package org.navgurukul.typingguru.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import org.navgurukul.commonui.themes.getThemedColor
import org.navgurukul.commonui.themes.getThemedUnit
import org.navgurukul.typingguru.R
import org.navgurukul.commonui.R as commonR
import kotlin.math.floor
import kotlin.math.roundToInt

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defAttrRes: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defAttrRes, defStyleRes) {

    companion object {
        const val DESIRED_ASPECT_RATIO = 2.77f
        const val MAX_NUM_KEYS = 14.5f
        const val MAX_ROWS = 5
        const val LEFT_HAND_MARGIN_MULTIPLIER = 0.77f
        const val RIGHT_HAND_MARGIN_MULTIPLIER = 2.07f
        const val HAND_IMAGE_WIDTH_MULTIPLIER = 6.26f
    }

    data class Key(
        val label: String,
        val value: Char,
        val widthMultiplier: Float = 1f,
        val startMargin: Float = 0f,
        val smallSize: Boolean = false,
        val ridge: Boolean = false,
        val textAlign: Paint.Align = Paint.Align.CENTER,
        val leftHandImage: Int? = null,
        val rightHandImage: Int? = null
    )

    private var keySize = 0

    private var currentWidth = 0
    private var currentHeight = 0
    private var marginStart = 0
    private var marginTop = 0

    private val keyboardCornerRadius = context.getThemedUnit(commonR.attr.cornerRadius400)
    private val keyCornerRadius = context.getThemedUnit(commonR.attr.cornerRadius200)

    private val keySpacing = context.resources.getDimensionPixelSize(commonR.dimen.spacing_1x)
    private val horizontalSpacing =
        context.resources.getDimensionPixelSize(commonR.dimen.spacing_3x)
    private val verticalSpacing = context.resources.getDimensionPixelSize(commonR.dimen.spacing_2x)
    private val keyTextSpacing = context.resources.getDimensionPixelSize(commonR.dimen.spacing_2x)
    private val borderWidth = context.getThemedUnit(commonR.attr.borderWidth)
    private val ridgeHeight = context.resources.getDimensionPixelSize(R.dimen.ridge_height)
    private val ridgeWidth = context.resources.getDimensionPixelSize(R.dimen.ridge_width)
    private val ridgeBottomMargin =
        context.resources.getDimensionPixelSize(commonR.dimen.spacing_1x)

    private val textNeutralColor = ContextCompat.getColor(context, R.color.key_board_default_text)
    private val textActiveColor = ContextCompat.getColor(context, R.color.colorWhite)

    private val ridgeColor = ContextCompat.getColor(context, R.color.accuracy_color)
    private val keyBackgroundColorNeutral = ContextCompat.getColor(context, R.color.key_background)
    private val keyBackgroundColorIncorrect = context.getThemedColor(commonR.attr.colorError)
    private val keyBackgroundColorActive = ContextCompat.getColor(context, R.color.current_text)

    private val leftHandRestingDrawable =
        AppCompatResources.getDrawable(context, R.drawable.img_left_hand_resting)
    private val rightHandRestingDrawable =
        AppCompatResources.getDrawable(context, R.drawable.img_right_hand_resting)

    private var leftHandDrawable: Drawable? = null
    private var rightHandDrawable: Drawable? = null

    private val backgroundPaint = Paint().apply {
        color =
            ContextCompat.getColor(context, R.color.keyboard_background)
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.border_color)
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = borderWidth.toFloat()
    }

    private val defaultTextSize =
        context.resources.getDimensionPixelSize(R.dimen.key_default_text_size).toFloat()
    private val smallTextSize =
        context.resources.getDimensionPixelSize(R.dimen.key_small_text_size).toFloat()

    private val textPaint = Paint().apply {
        color = textNeutralColor
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        textSize = defaultTextSize
    }

    private val rectF = RectF()

    private val keys: List<List<Key>> = listOf(
        listOf(
            Key("`", '`'),
            Key("1", '1', leftHandImage = R.drawable.img_left_hand_1),
            Key("2", '2', leftHandImage = R.drawable.img_left_hand_2),
            Key("3", '3', leftHandImage = R.drawable.img_left_hand_3),
            Key("4", '4', leftHandImage = R.drawable.img_left_hand_4),
            Key("5", '5', leftHandImage = R.drawable.img_left_hand_5),
            Key("6", '6', rightHandImage = R.drawable.img_right_hand_6),
            Key("7", '7', rightHandImage = R.drawable.img_right_hand_7),
            Key("8", '8', rightHandImage = R.drawable.img_right_hand_8),
            Key("9", '9', rightHandImage = R.drawable.img_right_hand_9),
            Key("0", '0', rightHandImage = R.drawable.img_right_hand_0),
            Key("-", '-'),
            Key("=", '='),
            Key("backspace", '\u0000', 1.54f, smallSize = true, textAlign = Paint.Align.RIGHT)
        ),
        listOf(
            Key(
                "tab",
                '\t',
                1.54f,
                smallSize = true,
                textAlign = Paint.Align.LEFT,
                leftHandImage = R.drawable.img_left_hand_tab
            ),
            Key("q", 'q', leftHandImage = R.drawable.img_left_hand_q),
            Key("w", 'w', leftHandImage = R.drawable.img_left_hand_w),
            Key("e", 'e', leftHandImage = R.drawable.img_left_hand_e),
            Key("r", 'r', leftHandImage = R.drawable.img_left_hand_r),
            Key("t", 't', leftHandImage = R.drawable.img_left_hand_t),
            Key("y", 'y', rightHandImage = R.drawable.img_right_hand_y),
            Key("u", 'u', rightHandImage = R.drawable.img_right_hand_u),
            Key("i", 'i', rightHandImage = R.drawable.img_right_hand_i),
            Key("o", 'o', rightHandImage = R.drawable.img_right_hand_o),
            Key("p", 'p', rightHandImage = R.drawable.img_right_hand_p),
            Key("[" ,'['),
            Key("]", ']'),
            Key("\\" ,'\\')
        ),
        listOf(
            Key("caps lock",  '\u0000',1.82f, smallSize = true, textAlign = Paint.Align.LEFT),
            Key("a", 'a', leftHandImage = R.drawable.img_left_hand_a),
            Key("s", 's', leftHandImage = R.drawable.img_left_hand_s),
            Key("d", 'd', leftHandImage = R.drawable.img_left_hand_d),
            Key("f", 'f', ridge = true, leftHandImage = R.drawable.img_left_hand_f),
            Key("g", 'g', leftHandImage = R.drawable.img_left_hand_g),
            Key("h", 'h', rightHandImage = R.drawable.img_right_hand_h),
            Key("j", 'j', ridge = true, rightHandImage = R.drawable.img_right_hand_j),
            Key("k", 'k', rightHandImage = R.drawable.img_right_hand_k),
            Key("l", 'l', rightHandImage = R.drawable.img_right_hand_l),
            Key(";", ';', rightHandImage = R.drawable.img_right_hand_colon),
            Key("'", '\''),
            Key("enter", '\n', 1.82f, smallSize = true, textAlign = Paint.Align.RIGHT)
        ),
        listOf(
            Key("shift", '\u0000', 2.35f, smallSize = true, textAlign = Paint.Align.LEFT),
            Key("z", 'z', leftHandImage = R.drawable.img_left_hand_z),
            Key("x", 'x', leftHandImage = R.drawable.img_left_hand_x),
            Key("c", 'c', leftHandImage = R.drawable.img_left_hand_c),
            Key("v", 'v', leftHandImage = R.drawable.img_left_hand_v),
            Key("b", 'b', leftHandImage = R.drawable.img_left_hand_b),
            Key("n", 'n', rightHandImage = R.drawable.img_right_hand_n),
            Key("m", 'm', rightHandImage = R.drawable.img_right_hand_m),
            Key(",", ',', rightHandImage = R.drawable.img_right_hand_comma),
            Key(".", '.', rightHandImage = R.drawable.img_right_hand_dot),
            Key("/", '/', rightHandImage = R.drawable.img_right_hand_slash),
            Key("shift", '\u0000', 2.35f, smallSize = true, textAlign = Paint.Align.RIGHT)
        ),
        listOf(Key("space", ' ',6.64f, 4.2f, smallSize = true))
    )

    private val keyMap = hashMapOf<Char, Key>().apply {
        this@KeyboardView.keys.forEach { keyList ->
            keyList.forEach {
                put(it.value, it)
            }
        }
    }

    var activeKey: Char? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            val key = keyMap[value] ?: return
            leftHandDrawable =
                key.leftHandImage?.let { AppCompatResources.getDrawable(context, it) }
                    ?: leftHandRestingDrawable
            rightHandDrawable =
                key.rightHandImage?.let { AppCompatResources.getDrawable(context, it) }
                    ?: rightHandRestingDrawable
            invalidate()
        }

    var incorrectKey: Char? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) {
            return
        }

        drawBackground(canvas)
        drawKeys(canvas)
        drawHands(canvas)
    }

    private fun drawHands(canvas: Canvas) {
        val scaledWidth = keySize * HAND_IMAGE_WIDTH_MULTIPLIER
        val y = borderWidth
        leftHandDrawable?.let {
            val x = marginStart + (keySize * LEFT_HAND_MARGIN_MULTIPLIER)

            it.setBounds(x.toInt(), y, (x + scaledWidth).toInt(), height)
            it.draw(canvas)
        }
        rightHandDrawable?.let {
            val x = width - marginStart - (keySize * RIGHT_HAND_MARGIN_MULTIPLIER) - scaledWidth
            it.setBounds(x.toInt(), y, (x + scaledWidth).toInt(), height)
            it.draw(canvas)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        backgroundPaint.color =
            ContextCompat.getColor(context, R.color.keyboard_background)
        rectF.set(
            marginStart.toFloat(),
            (marginTop + borderWidth).toFloat(),
            marginStart + currentWidth.toFloat(),
            marginTop + currentHeight.toFloat()
        )

        canvas.drawRoundRect(
            rectF,
            keyboardCornerRadius.toFloat(),
            keyboardCornerRadius.toFloat(),
            backgroundPaint
        )

        canvas.drawRoundRect(
            rectF,
            keyboardCornerRadius.toFloat(),
            keyboardCornerRadius.toFloat(),
            strokePaint
        )
    }

    private fun drawKeys(canvas: Canvas) {
        var currentY = marginTop + verticalSpacing + borderWidth
        for (keyRow in keys) {
            var currentX = marginStart + horizontalSpacing
            for (key in keyRow) {
                val isActive = activeKey == key.value
                val isIncorrect = incorrectKey == key.value
                backgroundPaint.color =
                    if (isActive) keyBackgroundColorActive else if (isIncorrect) keyBackgroundColorIncorrect else keyBackgroundColorNeutral
                val keyWidth = keySize * key.widthMultiplier
                currentX += (key.startMargin * keySize).toInt()

                rectF.set(
                    currentX.toFloat(),
                    currentY.toFloat(),
                    (currentX + keyWidth),
                    (currentY + keySize).toFloat()
                )

                canvas.drawRoundRect(
                    rectF,
                    keyCornerRadius.toFloat(),
                    keyCornerRadius.toFloat(),
                    backgroundPaint
                )
                canvas.drawRoundRect(
                    rectF,
                    keyCornerRadius.toFloat(),
                    keyCornerRadius.toFloat(),
                    strokePaint
                )

                textPaint.textSize = if (key.smallSize) smallTextSize else defaultTextSize
                textPaint.textAlign = key.textAlign
                textPaint.color =
                    if (isActive || isIncorrect) textActiveColor else textNeutralColor
                val centerX = when (key.textAlign) {
                    Paint.Align.LEFT -> rectF.left + keyTextSpacing
                    Paint.Align.CENTER -> rectF.centerX()
                    Paint.Align.RIGHT -> rectF.right - keyTextSpacing
                }
                canvas.drawText(
                    key.label,
                    centerX,
                    rectF.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2),
                    textPaint
                )

                if (key.ridge) {
                    backgroundPaint.color = ridgeColor

                    val x = rectF.centerX() - ridgeWidth / 2
                    val y = rectF.bottom - ridgeBottomMargin - ridgeHeight

                    canvas.drawRect(x, y, x + ridgeWidth, y + ridgeHeight, backgroundPaint)
                }

                currentX += keyWidth.toInt() + keySpacing
            }
            currentY += keySize + keySpacing
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec) - verticalSpacing - borderWidth

        val availableAspectRatio = maxWidth.toFloat() / maxHeight

        when {
            availableAspectRatio == DESIRED_ASPECT_RATIO -> {
                currentHeight = maxHeight
                currentWidth = maxWidth
            }
            availableAspectRatio < DESIRED_ASPECT_RATIO -> {
                keySize =
                    ((maxWidth - (2 * horizontalSpacing) - ((floor(MAX_NUM_KEYS).toInt() - 1) * keySpacing)) / MAX_NUM_KEYS).roundToInt()
                currentHeight =
                    (keySize * MAX_ROWS) + ((MAX_ROWS - 1) * keySpacing) + (2 * verticalSpacing)
                currentWidth = maxWidth

                marginTop = (maxHeight - currentHeight) / 2
                marginStart = 0
            }
            else -> {
                keySize =
                    ((maxHeight - (2 * verticalSpacing) - ((MAX_ROWS - 1) * keySpacing)) / MAX_ROWS)
                currentWidth =
                    (keySize * MAX_NUM_KEYS).toInt() + ((floor(MAX_NUM_KEYS).toInt() - 1) * keySpacing) + (2 * horizontalSpacing)
                currentHeight = maxHeight

                marginStart = (maxWidth - currentWidth) / 2
                marginTop = 0
            }
        }

        setMeasuredDimension(currentWidth, currentHeight)
    }
}