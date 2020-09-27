
package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.room.detail.timeline.helper.ContentUploadStateTrackerBinder
import org.navgurukul.chat.features.media.ImageContentRenderer

@EpoxyModelClass
abstract class MessageImageVideoItem : AbsMessageItem<MessageImageVideoItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_base
        } else {
            R.layout.item_timeline_event_base
        }

    @EpoxyAttribute
    lateinit var mediaData: ImageContentRenderer.Data
    @EpoxyAttribute
    var playable: Boolean = false
    @EpoxyAttribute
    var mode = ImageContentRenderer.Mode.THUMBNAIL
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null
    @EpoxyAttribute
    lateinit var imageContentRenderer: ImageContentRenderer
    @EpoxyAttribute
    lateinit var contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder

    override fun bind(holder: Holder) {
        super.bind(holder)
        imageContentRenderer.render(mediaData, mode, holder.imageView)
        if (!attributes.informationData.sendState.hasFailed()) {
            contentUploadStateTrackerBinder.bind(attributes.informationData.eventId, mediaData.isLocalFile(), holder.progressLayout)
        } else {
            holder.progressLayout.isVisible = false
        }
        holder.imageView.setOnClickListener(clickListener)
        holder.imageView.setOnLongClickListener(attributes.itemLongClickListener)
        ViewCompat.setTransitionName(holder.imageView, "imagePreview_${id()}")
        holder.mediaContentView.setOnClickListener(attributes.itemClickListener)
        holder.mediaContentView.setOnLongClickListener(attributes.itemLongClickListener)
        // The sending state color will be apply to the progress text
        renderSendState(holder.imageView, null, holder.failedToSendIndicator)
        holder.playContentView.visibility = if (playable) View.VISIBLE else View.GONE
    }

    override fun unbind(holder: Holder) {
        contentUploadStateTrackerBinder.unbind(attributes.informationData.eventId)
        holder.imageView.setOnClickListener(null)
        holder.imageView.setOnLongClickListener(null)
        super.unbind(holder)
    }

    override fun getViewType() = if (attributes.informationData.sentByMe) {
        STUB_ID + R.drawable.sent_timeline_item_background
    } else {
        STUB_ID + R.drawable.received_timeline_item_background
    }

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val progressLayout by bind<ViewGroup>(R.id.messageMediaUploadProgressLayout)
        val imageView by bind<ImageView>(R.id.messageThumbnailView)
        val playContentView by bind<ImageView>(R.id.messageMediaPlayView)

        val mediaContentView by bind<ViewGroup>(R.id.messageContentMedia)
        val failedToSendIndicator by bind<ImageView>(R.id.messageFailToSendIndicator)
    }

    companion object {
        private val STUB_ID = R.id.messageContentMediaStub
    }
}
