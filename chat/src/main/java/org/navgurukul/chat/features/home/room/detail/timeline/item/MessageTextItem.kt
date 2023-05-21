package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.text.method.MovementMethod
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.room.detail.timeline.tools.findPillsAndProcess

@EpoxyModelClass
abstract class MessageTextItem : AbsMessageItem<MessageTextItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_text_message
        } else {
            R.layout.item_timeline_event_text_message
        }

    @EpoxyAttribute
    var searchForPills: Boolean = false
    @EpoxyAttribute
    var message: CharSequence? = null
    @EpoxyAttribute
    var useBigFont: Boolean = false
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var movementMethod: MovementMethod? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.messageView.movementMethod = movementMethod
        if (useBigFont) {
            holder.messageView.textSize = 44F
        } else {
            holder.messageView.textSize = 14F
        }
        renderSendState(holder.messageView, holder.messageView)
        holder.messageView.setOnClickListener(attributes.itemClickListener)
        holder.messageView.setOnLongClickListener(attributes.itemLongClickListener)
        if (searchForPills) {
            message?.findPillsAndProcess(coroutineScope) { it.bind(holder.messageView) }
        }
        val textFuture = PrecomputedTextCompat.getTextFuture(
            message?.trim() ?: "",
            TextViewCompat.getTextMetricsParams(holder.messageView),
            null)
        holder.messageView.setTextFuture(textFuture)
    }

    override fun getViewType() = defaultLayout

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val messageView by bind<AppCompatTextView>(R.id.messageTextView)
    }

    companion object {
        private const val STUB_ID = 0
    }
}