package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import im.vector.matrix.android.api.session.room.send.SendState
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

/**
 * Base timeline item with reactions and read receipts.
 * Manages associated click listeners and send status.
 * Should not be used as this, use a subclass.
 */
abstract class AbsBaseMessageItem<H : AbsBaseMessageItem.Holder> : BaseEventItem<H>() {

    abstract val baseAttributes: Attributes

    private val _readReceiptsClickListener = DebouncedClickListener(View.OnClickListener {
        baseAttributes.readReceiptsCallback?.onReadReceiptsClicked(baseAttributes.informationData.readReceipts)
    })

    open fun shouldShowReactionAtBottom(): Boolean {
        return true
    }

    override fun getEventIds(): List<String> {
        return listOf(baseAttributes.informationData.eventId)
    }

    override fun bind(holder: H) {
        super.bind(holder)
        when (baseAttributes.informationData.e2eDecoration) {
            E2EDecoration.NONE -> {
                holder.e2EDecorationView.isVisible = false
            }
            E2EDecoration.WARN_IN_CLEAR,
            E2EDecoration.WARN_SENT_BY_UNVERIFIED,
            E2EDecoration.WARN_SENT_BY_UNKNOWN -> {
                holder.e2EDecorationView.setImageResource(R.drawable.ic_shield_warning)
                holder.e2EDecorationView.isVisible = true
            }
        }

        holder.view.setOnClickListener(baseAttributes.itemClickListener)
        holder.view.setOnLongClickListener(baseAttributes.itemLongClickListener)
    }


    protected open fun renderSendState(
        root: View,
        textView: TextView?,
        failureIndicator: ImageView? = null
    ) {
        root.isClickable = baseAttributes.informationData.sendState.isSent()
        val state =
            if (baseAttributes.informationData.hasPendingEdits) SendState.UNSENT else baseAttributes.informationData.sendState
        textView?.setTextColor(baseAttributes.messageColorProvider.getMessageTextColor(state))
        failureIndicator?.isVisible = baseAttributes.informationData.sendState.hasFailed()
    }

    abstract class Holder(@IdRes stubId: Int) : BaseEventItem.BaseHolder(stubId) {
        val e2EDecorationView by bind<ImageView>(R.id.messageE2EDecoration)
    }

    /**
     * This class holds all the common attributes for timeline items.
     */
    interface Attributes {
        val informationData: MessageInformationData
        val avatarRenderer: AvatarRenderer
        val messageColorProvider: MessageColorProvider
        val itemLongClickListener: View.OnLongClickListener?
        val itemClickListener: View.OnClickListener?
        val reactionPillCallback: TimelineEventController.ReactionPillCallback?
        val readReceiptsCallback: TimelineEventController.ReadReceiptsCallback?
    }
}