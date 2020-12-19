package org.navgurukul.chat.features.html

import android.content.Context
import android.text.style.URLSpan
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.permalinks.PermalinkParser
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.MatrixItem
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.tag.LinkHandler
import org.navgurukul.chat.core.glide.GlideRequests
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.home.AvatarRenderer

class MxLinkTagHandler(
    private val glideRequests: GlideRequests,
    private val context: Context,
    private val avatarRenderer: AvatarRenderer,
    private val sessionHolder: ActiveSessionHolder
) : LinkHandler() {

    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
        val link = tag.attributes()["href"]
        if (link != null) {
            val permalinkData = PermalinkParser.parse(link)
            val matrixItem = when (permalinkData) {
                is PermalinkData.UserLink -> {
                    val user = sessionHolder.getSafeActiveSession()?.getUser(permalinkData.userId)
                    MatrixItem.UserItem(permalinkData.userId, user?.displayName, user?.avatarUrl)
                }
                is PermalinkData.RoomLink -> {
                    if (permalinkData.eventId == null) {
                        val room: RoomSummary? = sessionHolder.getSafeActiveSession()
                            ?.getRoomSummary(permalinkData.roomIdOrAlias)
                        if (permalinkData.isRoomAlias) {
                            MatrixItem.RoomAliasItem(
                                permalinkData.roomIdOrAlias,
                                room?.displayName,
                                room?.avatarUrl
                            )
                        } else {
                            MatrixItem.RoomItem(
                                permalinkData.roomIdOrAlias,
                                room?.displayName,
                                room?.avatarUrl
                            )
                        }
                    } else {
                        // Exclude event link (used in reply events, we do not want to pill the "in reply to")
                        null
                    }
                }
                is PermalinkData.GroupLink -> {
                    val group =
                        sessionHolder.getSafeActiveSession()?.getGroupSummary(permalinkData.groupId)
                    MatrixItem.GroupItem(
                        permalinkData.groupId,
                        group?.displayName,
                        group?.avatarUrl
                    )
                }
                else -> null
            }

            if (matrixItem == null) {
                super.handle(visitor, renderer, tag)
            } else {
                val span = PillImageSpan(glideRequests, avatarRenderer, context, matrixItem)
                SpannableBuilder.setSpans(
                    visitor.builder(),
                    span,
                    tag.start(),
                    tag.end()
                )
                SpannableBuilder.setSpans(
                    visitor.builder(),
                    URLSpan(link),
                    tag.start(),
                    tag.end()
                )
            }
        } else {
            super.handle(visitor, renderer, tag)
        }
    }
}