package org.navgurukul.chat.features.home.room.detail.timeline.factory

import android.view.View
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.AvatarSizeProvider
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import org.navgurukul.chat.features.home.room.detail.timeline.item.NoticeItem
import org.navgurukul.chat.features.home.room.detail.timeline.item.NoticeItem_
import org.navgurukul.chat.features.home.room.format.NoticeEventFormatter

class NoticeItemFactory(
    private val eventFormatter: NoticeEventFormatter,
    private val avatarRenderer: AvatarRenderer,
    private val avatarSizeProvider: AvatarSizeProvider,
    private val informationDataFactory: MessageInformationDataFactory
) {

    fun create(event: TimelineEvent,
               highlight: Boolean,
               callback: TimelineEventController.Callback?): NoticeItem? {
        val formattedText = eventFormatter.format(event) ?: return null
        val informationData = informationDataFactory.create(event, null)
        val attributes = NoticeItem.Attributes(
            avatarRenderer = avatarRenderer,
            informationData = informationData,
            noticeText = formattedText,
            itemLongClickListener = View.OnLongClickListener { view ->
                callback?.onEventLongClicked(informationData, null, view) ?: false
            },
            avatarClickListener = { callback?.onAvatarClicked(informationData) }
        )
        return NoticeItem_()
            .highlighted(highlight)
            .attributes(attributes)
    }
}
