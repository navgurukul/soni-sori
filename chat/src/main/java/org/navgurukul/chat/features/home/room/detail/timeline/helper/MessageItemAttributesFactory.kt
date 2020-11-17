package org.navgurukul.chat.features.home.room.detail.timeline.helper

import android.view.View
import org.navgurukul.chat.EmojiCompatFontProvider
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.item.AbsMessageItem
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData

class MessageItemAttributesFactory (
    private val avatarRenderer: AvatarRenderer,
    private val messageColorProvider: MessageColorProvider,
    private val avatarSizeProvider: AvatarSizeProvider,
    private val emojiCompatFontProvider: EmojiCompatFontProvider
) {

    fun create(messageContent: Any?,
               informationData: MessageInformationData,
               callback: TimelineEventController.Callback?): AbsMessageItem.Attributes {
        return AbsMessageItem.Attributes(
            avatarSize = avatarSizeProvider.avatarSize,
            informationData = informationData,
            avatarRenderer = avatarRenderer,
            messageColorProvider = messageColorProvider,
            itemLongClickListener = View.OnLongClickListener { view ->
                callback?.onEventLongClicked(informationData, messageContent, view) ?: false
            },
            itemClickListener = DebouncedClickListener(View.OnClickListener { view ->
                callback?.onEventCellClicked(informationData, messageContent, view)
            }),
            memberClickListener = DebouncedClickListener(View.OnClickListener {
                callback?.onMemberNameClicked(informationData)
            }),
            reactionPillCallback = callback,
            avatarCallback = callback,
            emojiTypeFace = emojiCompatFontProvider.typeface
        )
    }
}