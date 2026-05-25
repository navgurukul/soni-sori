package org.navgurukul.learn.ui.learn.adapter

import androidx.annotation.DrawableRes
import org.navgurukul.commonui.R
import java.util.Locale

@Suppress("unused")

enum class CourseDefaultLogos(@DrawableRes val id: Int) {

    INTRODUCTIONTOPYTHON(R.drawable.python_logo),
    TYPING(R.drawable.ic_icon_typing),
    spOKENENGLISH(R.drawable.ic_icon_language),
    JAVASCRIPT(R.drawable.ic_javascript_logo),
    RESIDENTIALPROGRAMMES(R.drawable.residential_icon),
    INTRODUCTIONTOSCRATCH(R.drawable.ic_scratch_cat),
    MCDIGITALCOURSE_2_0(R.drawable.mc_digital_logo),
    DEFAULT(R.drawable.ic_typing_icon),

    ;

    companion object {
        fun resolveDrawable(vararg keys: String?): Int {
            keys.forEach { key ->
                fromKey(key)?.let { return it.id }
            }
            return DEFAULT.id
        }

        private fun fromKey(key: String?): CourseDefaultLogos? {
            val normalizedKey = normalize(key)
            return values().firstOrNull { normalize(it.name) == normalizedKey }
        }

        private fun normalize(value: String?): String {
            return value
                ?.trim()
                ?.uppercase(Locale.US)
                ?.replace(Regex("[^A-Z0-9]"), "")
                .orEmpty()
        }
    }
}
