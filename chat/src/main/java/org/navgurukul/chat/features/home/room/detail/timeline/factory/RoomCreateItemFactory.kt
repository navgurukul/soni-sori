package org.navgurukul.chat.features.home.room.detail.timeline.factory

import im.vector.matrix.android.api.permalinks.PermalinkFactory
import im.vector.matrix.android.api.session.events.model.toModel
import im.vector.matrix.android.api.session.room.model.create.RoomCreateContent
import im.vector.matrix.android.api.session.room.timeline.TimelineEvent
import me.gujun.android.span.span
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.resources.StringProvider
import org.navgurukul.chat.core.resources.UserPreferencesProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.item.RoomCreateItem_

class RoomCreateItemFactory(
    private val stringProvider: StringProvider,
    private val userPreferencesProvider: UserPreferencesProvider,
    private val noticeItemFactory: NoticeItemFactory
) {

    fun create(event: TimelineEvent, callback: TimelineEventController.Callback?): MerakiEpoxyModel<*>? {
        val createRoomContent = event.root.getClearContent().toModel<RoomCreateContent>() ?: return null
        val predecessorId = createRoomContent.predecessor?.roomId ?: return defaultRendering(event, callback)
        val roomLink = PermalinkFactory.createPermalink(predecessorId) ?: return null
        val text = span {
            +stringProvider.getString(R.string.room_tombstone_continuation_description)
            +"\n"
            span(stringProvider.getString(R.string.room_tombstone_predecessor_link)) {
                textDecorationLine = "underline"
                onClick = { callback?.onRoomCreateLinkClicked(roomLink) }
            }
        }
        return RoomCreateItem_()
            .text(text)
    }

    private fun defaultRendering(event: TimelineEvent, callback: TimelineEventController.Callback?): MerakiEpoxyModel<*>? {
        return if (userPreferencesProvider.shouldShowHiddenEvents()) {
            noticeItemFactory.create(event, false, callback)
        } else {
            null
        }
    }
}
