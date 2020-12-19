package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.helper.widget.Flow.CHAIN_PACKED
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.session.room.send.SendState
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.reactions.widget.ReactionButton
import kotlin.math.min

/**
 * Base timeline item with reactions and read receipts.
 * Manages associated click listeners and send status.
 * Should not be used as this, use a subclass.
 */
abstract class AbsBaseMessageItem<H : AbsBaseMessageItem.Holder> : BaseEventItem<H>() {

    abstract val baseAttributes: Attributes

    open fun shouldShowReactionAtBottom(): Boolean {
        return true
    }

    override fun getEventIds(): List<String> {
        return listOf(baseAttributes.informationData.eventId)
    }

    override fun bind(holder: H) {
        super.bind(holder)

        holder.view.setOnClickListener(baseAttributes.itemClickListener)
        holder.view.setOnLongClickListener(baseAttributes.itemLongClickListener)

        val reactionsContainer = holder.reactionsContainer ?: return
        val reactions = baseAttributes.informationData.orderedReactionList
        setupReactions(reactionsContainer, reactions)
    }

    private var reactionClickListener: ReactionButton.ReactedListener = object : ReactionButton.ReactedListener {
        override fun onReacted(reactionButton: ReactionButton) {
            baseAttributes.reactionPillCallback?.onClickOnReactionPill(baseAttributes.informationData, reactionButton.reactionString, true)
        }

        override fun onUnReacted(reactionButton: ReactionButton) {
            baseAttributes.reactionPillCallback?.onClickOnReactionPill(baseAttributes.informationData, reactionButton.reactionString, false)
        }

        override fun onLongClick(reactionButton: ReactionButton) {
            baseAttributes.reactionPillCallback?.onLongClickOnReactionPill(baseAttributes.informationData, reactionButton.reactionString)
        }
    }

    private fun setupReactions(reactionsContainer: ConstraintLayout, reactions: List<ReactionInfoData>?) {
        if (!shouldShowReactionAtBottom() || reactions.isNullOrEmpty()) {
            reactionsContainer.isVisible = false
        } else {
            reactionsContainer.isVisible = true
            reactionsContainer.removeAllViews()
            val flowHelper = Flow(reactionsContainer.context).apply {
                if (baseAttributes.informationData.sentByMe) {
                    setHorizontalBias(1f)
                } else {
                    setHorizontalBias(0f)
                }

                setWrapMode(Flow.WRAP_ALIGNED)
                setHorizontalStyle(CHAIN_PACKED)
                setVerticalGap(resources.getDimensionPixelSize(R.dimen.spacing_1x))
                setHorizontalGap(resources.getDimensionPixelSize(R.dimen.spacing_2x))
            }
            val buttonIds = IntArray(min(reactions.size, 7))
            reactions.take(7).forEachIndexed { index, reaction ->
                val reactionButton = ReactionButton(reactionsContainer.context)
                reactionButton.id = View.generateViewId()
                reactionButton.reactedListener = reactionClickListener
                reactionButton.setTag(R.id.reactionsContainer, reaction.key)
                reactionButton.reactionString = reaction.key
                reactionButton.reactionCount = reaction.count
                reactionButton.setChecked(reaction.addedByMe)
                reactionButton.isEnabled = reaction.synced
                buttonIds[index] = (reactionButton.id)
                reactionsContainer.addView(reactionButton)
            }
            flowHelper.referencedIds = buttonIds
            reactionsContainer.addView(flowHelper, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT))
            reactionsContainer.setOnLongClickListener(baseAttributes.itemLongClickListener)
        }
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
        val reactionsContainer by bindNullable<ConstraintLayout?>(R.id.reactionsContainer)
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
    }
}