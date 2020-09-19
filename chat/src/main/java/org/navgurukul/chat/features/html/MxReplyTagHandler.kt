package org.navgurukul.chat.features.html

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler
import org.commonmark.node.BlockQuote

class MxReplyTagHandler : TagHandler() {

    override fun supportedTags() = listOf("mx-reply")

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val configuration = visitor.configuration()
        val factory = configuration.spansFactory().get(BlockQuote::class.java)
        if (factory != null) {
            SpannableBuilder.setSpans(
                    visitor.builder(),
                    factory.getSpans(configuration, visitor.renderProps()),
                    tag.start(),
                    tag.end()
            )
            val replyText = visitor.builder().removeFromEnd(tag.end())
            visitor.builder().append("\n\n").append(replyText)
        }
    }
}