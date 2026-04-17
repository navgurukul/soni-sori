package me.gujun.android.span

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.ImageSpan
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View

inline fun span(text: CharSequence = "", init: SpanBuilder.() -> Unit): CharSequence {
    val builder = SpanBuilder(SpannableStringBuilder(text))
    builder.init()
    return builder.build()
}

inline fun span(init: SpanBuilder.() -> Unit): CharSequence {
    val builder = SpanBuilder(SpannableStringBuilder())
    builder.init()
    return builder.build()
}

class SpanBuilder(val builder: SpannableStringBuilder) {
    var onClick: (() -> Unit)? = null
    var textDecorationLine: String? = null
    var text: CharSequence = ""
    var textColor: Int? = null
    var textStyle: String? = null

    operator fun CharSequence.unaryPlus() {
        builder.append(this)
    }

    fun append(text: CharSequence) {
        builder.append(text)
    }

    inline fun span(text: CharSequence = "", init: SpanBuilder.() -> Unit) {
        val start = builder.length
        val child = SpanBuilder(SpannableStringBuilder(text))
        child.init()

        builder.append(child.text)
        builder.append(child.build())

        if (child.onClick != null) {
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    child.onClick?.invoke()
                }
            }, start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (child.textDecorationLine == "underline") {
             builder.setSpan(UnderlineSpan(), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (child.textColor != null) {
            builder.setSpan(android.text.style.ForegroundColorSpan(child.textColor!!), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (child.textStyle == "bold") {
             builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (child.textStyle == "italic") {
             builder.setSpan(StyleSpan(Typeface.ITALIC), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (child.textStyle == "bold_italic") {
             builder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    inline fun italic(init: SpanBuilder.() -> Unit) {
        val start = builder.length
        val child = SpanBuilder(SpannableStringBuilder())
        child.init()
        builder.append(child.build())
        builder.setSpan(StyleSpan(Typeface.ITALIC), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    inline fun bold(init: SpanBuilder.() -> Unit) {
        val start = builder.length
        val child = SpanBuilder(SpannableStringBuilder())
        child.init()
        builder.append(child.build())
        builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun image(drawable: android.graphics.drawable.Drawable, align: Int = android.text.style.DynamicDrawableSpan.ALIGN_BOTTOM) {
        val span = ImageSpan(drawable, align)
        val start = builder.length
        builder.append("\uFFFC")
        builder.setSpan(span, start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun image(span: ImageSpan) {
        val start = builder.length
        builder.append("\uFFFC")
        builder.setSpan(span, start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun build(): CharSequence {
        if (text.isNotEmpty() && builder.isEmpty()) {
             builder.append(text)
        }
        return builder
    }
}









