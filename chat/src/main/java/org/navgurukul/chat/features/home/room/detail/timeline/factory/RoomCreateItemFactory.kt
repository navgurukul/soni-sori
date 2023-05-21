package org.navgurukul.chat.features.home.room.detail.timeline.factory

import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.create.RoomCreateContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import me.gujun.android.span.span
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.resources.UserPreferencesProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.item.RoomCreateItem_
import org.navgurukul.commonui.resources.StringProvider

class RoomCreateItemFactory(
    private val stringProvider: StringProvider,
    private val userPreferencesProvider: UserPreferencesProvider,
    private val noticeItemFactory: NoticeItemFactory
) {

    fun create(event: TimelineEvent, callback: TimelineEventController.Callback?): MerakiEpoxyModel<*>? {
        val text = span {
            +stringProvider.getString(R.string.room_tombstone_continuation_description)
            +"\n"
            span(stringProvider.getString(R.string.room_tombstone_predecessor_link)) {
                textDecorationLine = "underline"
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
