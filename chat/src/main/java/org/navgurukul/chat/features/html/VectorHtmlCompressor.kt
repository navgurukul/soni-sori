package org.navgurukul.chat.features.html

import com.googlecode.htmlcompressor.compressor.Compressor
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

class SaralHtmlCompressor {

    // All default options are suitable so far
    private val htmlCompressor: Compressor = HtmlCompressor()

    fun compress(html: String): String {
        var result = htmlCompressor.compress(html)

        // Trim space after <br> and <p>, unfortunately the method setRemoveSurroundingSpaces() from the doc does not exist
        result = result.replace("<br> ", "<br>")
        result = result.replace("<br/> ", "<br/>")
        result = result.replace("<p> ", "<p>")

        return result
    }
}
