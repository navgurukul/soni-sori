package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import com.airbnb.epoxy.EpoxyAttribute
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

/**
 * Base timeline item that adds an optional information bar with the sender avatar, name and time
 * Adds associated click listeners (on avatar, displayname)
 */
abstract class AbsMessageItem<H : AbsMessageItem.Holder> : AbsBaseMessageItem<H>() {

    override val baseAttributes: AbsBaseMessageItem.Attributes
        get() = attributes

    @EpoxyAttribute
    lateinit var attributes: Attributes

    private val _avatarClickListener = DebouncedClickListener(View.OnClickListener {
        attributes.avatarCallback?.onAvatarClicked(attributes.informationData)
    })
    private val _memberNameClickListener = DebouncedClickListener(View.OnClickListener {
        attributes.avatarCallback?.onMemberNameClicked(attributes.informationData)
    })

    override fun bind(holder: H) {
        super.bind(holder)
        if (attributes.informationData.showInformation) {
            holder.avatarImageView?.let {
                it.layoutParams = it.layoutParams?.apply {
                    height = attributes.avatarSize
                    width = attributes.avatarSize
                }
                it.visibility = View.VISIBLE
                it.setOnClickListener(_avatarClickListener)
                it.setOnLongClickListener(attributes.itemLongClickListener)

                attributes.avatarRenderer.render(attributes.informationData.matrixItem, it)
            }


            holder.memberNameView.visibility = View.VISIBLE
            holder.topContainer.visibility = View.VISIBLE
            holder.memberNameView.setOnClickListener(_memberNameClickListener)
            holder.timeView.visibility = View.VISIBLE
            holder.timeView.text = attributes.informationData.time
            holder.memberNameView.text = attributes.informationData.memberName
            holder.memberNameView.setTextColor(attributes.getMemberNameColor())
            holder.memberNameView.setOnLongClickListener(attributes.itemLongClickListener)
        } else {
            holder.avatarImageView?.let {
                it.setOnClickListener(null)
                it.setOnLongClickListener(null)
                it.visibility = View.INVISIBLE
            }

            holder.memberNameView.setOnClickListener(null)
            holder.memberNameView.visibility = View.GONE
            holder.timeView.visibility = View.GONE
            holder.topContainer.visibility = View.GONE
            holder.memberNameView.setOnLongClickListener(null)
        }
    }

    override fun unbind(holder: H) {
        holder.avatarImageView?.let {
            it.setOnClickListener(null)
            it.setOnLongClickListener(null)
        }
        holder.memberNameView.setOnClickListener(null)
        holder.memberNameView.setOnLongClickListener(null)
        super.unbind(holder)
    }

    private fun Attributes.getMemberNameColor() =
        messageColorProvider.getMemberNameTextColor(informationData.senderId)

    abstract class Holder(@IdRes stubId: Int) : AbsBaseMessageItem.Holder(stubId) {
        val avatarImageView by bindNullable<ImageView?>(R.id.messageAvatarImageView)
        val memberNameView by bind<TextView>(R.id.messageMemberNameView)
        val topContainer by bind<ViewGroup>(R.id.item_timeline_event_top)
        val timeView by bind<TextView>(R.id.messageTimeView)
    }

    /**
     * This class holds all the common attributes for timeline items.
     */
    data class Attributes(
        val avatarSize: Int,
        override val informationData: MessageInformationData,
        override val avatarRenderer: AvatarRenderer,
        override val messageColorProvider: MessageColorProvider,
        override val itemLongClickListener: View.OnLongClickListener? = null,
        override val itemClickListener: View.OnClickListener? = null,
        val memberClickListener: View.OnClickListener? = null,
        val avatarCallback: TimelineEventController.AvatarCallback? = null,
        val emojiTypeFace: Typeface? = null,
        override val reactionPillCallback: TimelineEventController.ReactionPillCallback?
    ) : AbsBaseMessageItem.Attributes {

        // Have to override as it's used to diff epoxy items
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Attributes

            if (avatarSize != other.avatarSize) return false
            if (informationData != other.informationData) return false

            return true
        }

        override fun hashCode(): Int {
            var result = avatarSize
            result = 31 * result + informationData.hashCode()
            return result
        }
    }
}
