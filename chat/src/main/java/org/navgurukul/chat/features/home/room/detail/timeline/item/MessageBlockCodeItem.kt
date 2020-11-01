package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.setTextOrHide

@EpoxyModelClass
abstract class MessageBlockCodeItem : AbsMessageItem<MessageBlockCodeItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_code_block
        } else {
            R.layout.item_timeline_event_code_block
        }

    @EpoxyAttribute
    var message: CharSequence? = null
    @EpoxyAttribute
    var editedSpan: CharSequence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.messageView.text = message
        renderSendState(holder.messageView, holder.messageView)
        holder.messageView.setOnClickListener(attributes.itemClickListener)
        holder.messageView.setOnLongClickListener(attributes.itemLongClickListener)
        holder.editedView.movementMethod = BetterLinkMovementMethod.getInstance()
        holder.editedView.setTextOrHide(editedSpan)
    }

    override fun getViewType() = defaultLayout

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<TextView>(R.id.codeBlockTextView)
        val editedView by bind<TextView>(R.id.codeBlockEditedView)
    }

    companion object {
        private const val STUB_ID = 0
    }
}
