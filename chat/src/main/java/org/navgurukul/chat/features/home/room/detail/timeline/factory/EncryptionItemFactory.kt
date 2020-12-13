package org.navgurukul.chat.features.home.room.detail.timeline.factory

import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.internal.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.internal.crypto.model.event.EncryptionEventContent
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageItemAttributesFactory
import org.navgurukul.chat.features.home.room.detail.timeline.item.StatusTileTimelineItem
import org.navgurukul.chat.features.home.room.detail.timeline.item.StatusTileTimelineItem_
import org.navgurukul.commonui.resources.StringProvider

class EncryptionItemFactory(
    private val messageItemAttributesFactory: MessageItemAttributesFactory,
    private val messageColorProvider: MessageColorProvider,
    private val stringProvider: StringProvider,
    private val informationDataFactory: MessageInformationDataFactory
) {

    fun create(
        event: TimelineEvent,
        highlight: Boolean,
        callback: TimelineEventController.Callback?
    ): StatusTileTimelineItem? {
        val algorithm = event.root.getClearContent().toModel<EncryptionEventContent>()?.algorithm
        val informationData = informationDataFactory.create(event, null)
        val attributes = messageItemAttributesFactory.create(null, informationData, callback)

        val isSafeAlgorithm = algorithm == MXCRYPTO_ALGORITHM_MEGOLM
        val title: String
        val description: String
        val shield: StatusTileTimelineItem.ShieldUIState
        if (isSafeAlgorithm) {
            title = stringProvider.getString(R.string.encryption_enabled)
            description = stringProvider.getString(R.string.encryption_enabled_tile_description)
            shield = StatusTileTimelineItem.ShieldUIState.BLACK
        } else {
            title = stringProvider.getString(R.string.encryption_not_enabled)
            description =
                stringProvider.getString(R.string.encryption_unknown_algorithm_tile_description)
            shield = StatusTileTimelineItem.ShieldUIState.RED
        }
        return StatusTileTimelineItem_()
            .attributes(
                StatusTileTimelineItem.Attributes(
                    title = title,
                    description = description,
                    shieldUIState = shield,
                    informationData = informationData,
                    avatarRenderer = attributes.avatarRenderer,
                    messageColorProvider = messageColorProvider,
                    emojiTypeFace = attributes.emojiTypeFace,
                    reactionPillCallback = callback,
                    itemClickListener = attributes.itemClickListener,
                    itemLongClickListener = attributes.itemLongClickListener
                )
            )
            .highlighted(highlight)
    }
}
