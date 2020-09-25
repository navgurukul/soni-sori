package org.navgurukul.chat.features.html

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler

/**
 * custom to matrix for IRC-style font coloring
 */
class FontTagHandler : SimpleTagHandler() {

    override fun supportedTags() = listOf("font")

    override fun getSpans(configuration: MarkwonConfiguration, renderProps: RenderProps, tag: HtmlTag): Any? {
        val colorString = tag.attributes()["color"]?.let { parseColor(it) } ?: Color.BLACK
        return ForegroundColorSpan(colorString)
    }

    private fun parseColor(color_name: String): Int {
        try {
            return Color.parseColor(color_name)
        } catch (e: Exception) {
            // try other w3c colors?
            return when (color_name) {
                "white"   -> Color.WHITE
                "yellow"  -> Color.YELLOW
                "fuchsia" -> Color.parseColor("#FF00FF")
                "red"     -> Color.RED
                "silver"  -> Color.parseColor("#C0C0C0")
                "gray"    -> Color.GRAY
                "olive"   -> Color.parseColor("#808000")
                "purple"  -> Color.parseColor("#800080")
                "maroon"  -> Color.parseColor("#800000")
                "aqua"    -> Color.parseColor("#00FFFF")
                "lime"    -> Color.parseColor("#00FF00")
                "teal"    -> Color.parseColor("#008080")
                "green"   -> Color.GREEN
                "blue"    -> Color.BLUE
                "orange"  -> Color.parseColor("#FFA500")
                "navy"    -> Color.parseColor("#000080")
                else      -> Color.BLACK
            }
        }
    }
}