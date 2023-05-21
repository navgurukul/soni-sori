package org.navgurukul.chat.features.home.room.detail.timeline.item

import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R

@EpoxyModelClass
abstract class RedactedMessageItem : AbsMessageItem<RedactedMessageItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_redacted
        } else {
            R.layout.item_timeline_event_redacted
        }

    override fun getViewType() = defaultLayout

    override fun shouldShowReactionAtBottom() = false

    class Holder : AbsMessageItem.Holder(STUB_ID)

    companion object {
        private const val STUB_ID = 0
    }
}
