package org.navgurukul.chat.features.html

import android.content.Context
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.TagHandlerNoOp
import org.commonmark.node.Node
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.features.home.AvatarRenderer

class EventHtmlRenderer(
    context: Context,
    htmlConfigure: MatrixHtmlPluginConfigure
) {

    private val markwon = Markwon.builder(context)
            .usePlugin(HtmlPlugin.create(htmlConfigure))
            .build()

    fun parse(text: String): Node {
        return markwon.parse(text)
    }

    fun render(text: String): CharSequence {
        return markwon.toMarkdown(text)
    }

    fun render(node: Node): CharSequence {
        return markwon.render(node)
    }
}

class MatrixHtmlPluginConfigure (private val context: Context,
                                 private val colorProvider: ColorProvider,
                                 private val avatarRenderer: AvatarRenderer,
                                 private val session: ActiveSessionHolder
) : HtmlPlugin.HtmlConfigure {

    override fun configureHtml(plugin: HtmlPlugin) {
        plugin
                .addHandler(TagHandlerNoOp.create("a"))
                .addHandler(FontTagHandler())
                .addHandler(MxLinkTagHandler(GlideApp.with(context), context, avatarRenderer, session))
                .addHandler(MxReplyTagHandler())
                .addHandler(SpanHandler(colorProvider))
    }
}