package org.navgurukul.chat.core.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.copyToClipboard
import org.navgurukul.commonui.themes.ThemeUtils

/**
 * Set a text in the TextView, or set visibility to GONE if the text is null
 */
fun TextView.setTextOrHide(newText: CharSequence?, hideWhenBlank: Boolean = true) {
    if (newText == null
            || (newText.isBlank() && hideWhenBlank)) {
        isVisible = false
    } else {
        this.text = newText
        isVisible = true
    }
}

/**
 * Set text with a colored part
 * @param fullTextRes the resource id of the full text. Value MUST contains a parameter for string, which will be replaced by the colored part
 * @param coloredTextRes the resource id of the colored part of the text
 * @param colorAttribute attribute of the color. Default to colorAccent
 * @param underline true to also underline the text. Default to false
 */
fun TextView.setTextWithColoredPart(@StringRes fullTextRes: Int,
                                    @StringRes coloredTextRes: Int,
                                    @AttrRes colorAttribute: Int = R.attr.colorAccent,
                                    underline: Boolean = false) {
    val coloredPart = resources.getString(coloredTextRes)
    // Insert colored part into the full text
    val fullText = resources.getString(fullTextRes, coloredPart)
    val color = ThemeUtils.getColor(context, colorAttribute)

    val foregroundSpan = ForegroundColorSpan(color)

    val index = fullText.indexOf(coloredPart)

    text = SpannableString(fullText)
            .apply {
                setSpan(foregroundSpan, index, index + coloredPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (underline) {
                    setSpan(UnderlineSpan(), index, index + coloredPart.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
}

/**
 * Set long click listener to copy the current text of the TextView to the clipboard and show a Snackbar
 */
fun TextView.copyOnLongClick() {
    setOnLongClickListener { view ->
        (view as? TextView)
            ?.text
            ?.let { text ->
                copyToClipboard(view.context, text, false)
                Snackbar.make(view, view.resources.getString(R.string.copied_to_clipboard), Snackbar.LENGTH_SHORT).show()
            }
        true
    }
}