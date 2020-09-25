package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.facebook.drawee.view.SimpleDraweeView
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

@EpoxyModelClass
abstract class DefaultItem : BaseEventItem<DefaultItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_base_noinfo

    @EpoxyAttribute
    lateinit var attributes: Attributes

    private val _readReceiptsClickListener = DebouncedClickListener(View.OnClickListener {
        attributes.readReceiptsCallback?.onReadReceiptsClicked(attributes.informationData.readReceipts)
    })

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.messageTextView.text = attributes.text
        attributes.avatarRenderer.render(attributes.informationData.matrixItem, holder.avatarImageView)
        holder.view.setOnLongClickListener(attributes.itemLongClickListener)
//        holder.readReceiptsView.render(attributes.informationData.readReceipts, attributes.avatarRenderer, _readReceiptsClickListener)
    }

    override fun getEventIds(): List<String> {
        return listOf(attributes.informationData.eventId)
    }

    override fun getViewType() = STUB_ID

    class Holder : BaseHolder(STUB_ID) {
        val avatarImageView by bind<SimpleDraweeView>(R.id.itemDefaultAvatarView)
        val messageTextView by bind<TextView>(R.id.itemDefaultTextView)
    }

    data class Attributes(
        val avatarRenderer: AvatarRenderer,
        val informationData: MessageInformationData,
        val text: CharSequence,
        val itemLongClickListener: View.OnLongClickListener? = null,
        val readReceiptsCallback: TimelineEventController.ReadReceiptsCallback? = null
    )

    companion object {
        private val STUB_ID = R.id.messageContentDefaultStub
    }
}
