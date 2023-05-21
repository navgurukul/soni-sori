package org.navgurukul.chat.features.home.room.detail.timeline.factory

import android.view.View
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.AvatarSizeProvider
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import org.navgurukul.chat.features.home.room.detail.timeline.item.DefaultItem
import org.navgurukul.chat.features.home.room.detail.timeline.item.DefaultItem_
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData
import org.navgurukul.commonui.resources.StringProvider

class DefaultItemFactory(
    private val avatarSizeProvider: AvatarSizeProvider,
    private val avatarRenderer: AvatarRenderer,
    private val stringProvider: StringProvider,
    private val informationDataFactory: MessageInformationDataFactory
) {

    fun create(text: String,
               informationData: MessageInformationData,
               highlight: Boolean,
               callback: TimelineEventController.Callback?): DefaultItem {
        val attributes = DefaultItem.Attributes(
            avatarRenderer = avatarRenderer,
            informationData = informationData,
            text = text,
            itemLongClickListener = View.OnLongClickListener { view ->
                callback?.onEventLongClicked(informationData, null, view) ?: false
            }
        )
        return DefaultItem_()
            .highlighted(highlight)
            .attributes(attributes)
    }

    fun create(event: TimelineEvent,
               highlight: Boolean,
               callback: TimelineEventController.Callback?,
               throwable: Throwable? = null): DefaultItem {
        val text = if (throwable == null) {
            stringProvider.getString(R.string.rendering_event_error_type_of_event_not_handled, event.root.getClearType())
        } else {
            stringProvider.getString(R.string.rendering_event_error_exception, event.root.eventId)
        }
        val informationData = informationDataFactory.create(event, null)
        return create(text, informationData, highlight, callback)
    }
}
