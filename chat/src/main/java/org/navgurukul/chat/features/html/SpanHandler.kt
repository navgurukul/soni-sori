package org.navgurukul.chat.features.html

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler
import org.navgurukul.chat.core.resources.ColorProvider

class SpanHandler(private val colorProvider: ColorProvider) : TagHandler() {

    override fun supportedTags() = listOf("span")

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val mxSpoiler = tag.attributes()["data-mx-spoiler"]
        if (mxSpoiler != null) {
            SpannableBuilder.setSpans(
                    visitor.builder(),
                    SpoilerSpan(colorProvider),
                    tag.start(),
                    tag.end()
            )
        } else {
            // default thing?
        }
    }
}
