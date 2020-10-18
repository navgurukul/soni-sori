package org.navgurukul.chat.core.resources

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import org.navgurukul.commonui.themes.ThemeUtils

class DrawableProvider(private val context: Context) {

    fun getDrawable(@DrawableRes colorRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, colorRes)
    }
    fun getDrawable(@DrawableRes colorRes: Int, @ColorInt color: Int): Drawable? {
        return ContextCompat.getDrawable(context, colorRes)?.let {
            ThemeUtils.tintDrawableWithColor(it, color)
        }
    }
}