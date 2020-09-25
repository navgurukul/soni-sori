package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.facebook.drawee.view.SimpleDraweeView
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.ClickListener
import org.navgurukul.chat.core.epoxy.onClick
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

@EpoxyModelClass
abstract class NoticeItem : BaseEventItem<NoticeItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_base_noinfo

    @EpoxyAttribute
    lateinit var attributes: Attributes

    private val _readReceiptsClickListener = DebouncedClickListener(View.OnClickListener {
        attributes.readReceiptsCallback?.onReadReceiptsClicked(attributes.informationData.readReceipts)
    })

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.noticeTextView.text = attributes.noticeText
        attributes.avatarRenderer.render(attributes.informationData.matrixItem, holder.avatarImageView)
        holder.view.setOnLongClickListener(attributes.itemLongClickListener)
//        holder.readReceiptsView.render(attributes.informationData.readReceipts, attributes.avatarRenderer, _readReceiptsClickListener)
        holder.avatarImageView.onClick(attributes.avatarClickListener)

        when (attributes.informationData.e2eDecoration) {
            E2EDecoration.NONE                 -> {
                holder.e2EDecorationView.isVisible = false
            }
            E2EDecoration.WARN_IN_CLEAR,
            E2EDecoration.WARN_SENT_BY_UNVERIFIED,
            E2EDecoration.WARN_SENT_BY_UNKNOWN -> {
                holder.e2EDecorationView.setImageResource(R.drawable.ic_shield_warning)
                holder.e2EDecorationView.isVisible = true
            }
        }
    }

    override fun getEventIds(): List<String> {
        return listOf(attributes.informationData.eventId)
    }

    override fun getViewType() = STUB_ID

    class Holder : BaseHolder(STUB_ID) {
        val avatarImageView by bind<SimpleDraweeView>(R.id.itemNoticeAvatarView)
        val noticeTextView by bind<TextView>(R.id.itemNoticeTextView)
        val e2EDecorationView by bind<ImageView>(R.id.messageE2EDecoration)
    }

    data class Attributes(
        val avatarRenderer: AvatarRenderer,
        val informationData: MessageInformationData,
        val noticeText: CharSequence,
        val itemLongClickListener: View.OnLongClickListener? = null,
        val readReceiptsCallback: TimelineEventController.ReadReceiptsCallback? = null,
        val avatarClickListener: ClickListener? = null
    )

    companion object {
        private val STUB_ID = R.id.messageContentNoticeStub
    }
}