package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.graphics.Paint
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.room.detail.timeline.helper.ContentDownloadStateTrackerBinder
import org.navgurukul.chat.features.home.room.detail.timeline.helper.ContentUploadStateTrackerBinder

@EpoxyModelClass
abstract class MessageFileItem : AbsMessageItem<MessageFileItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_file
        } else {
            R.layout.item_timeline_event_file
        }

    @EpoxyAttribute
    var filename: CharSequence = ""

    @EpoxyAttribute
    var mxcUrl: String = ""

    @EpoxyAttribute

    @DrawableRes
    var iconRes: Int = 0

//    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
//    var clickListener: View.OnClickListener? = null

    @EpoxyAttribute
    var izLocalFile = false

    @EpoxyAttribute
    var izDownloaded = false

    @EpoxyAttribute
    lateinit var contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder

    @EpoxyAttribute
    lateinit var contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder

    override fun bind(holder: Holder) {
        super.bind(holder)
        renderSendState(holder.fileLayout, holder.filenameView)
        if (!attributes.informationData.sendState.hasFailed()) {
            contentUploadStateTrackerBinder.bind(
                attributes.informationData.eventId,
                izLocalFile,
                holder.progressLayout
            )
        } else {
            holder.fileImageView.setImageResource(R.drawable.ic_close)
            holder.progressLayout.isVisible = false
        }
        holder.filenameView.text = filename
        if (attributes.informationData.sendState.isSending()) {
            holder.fileImageView.setImageResource(iconRes)
        } else {
            if (izDownloaded) {
                holder.fileImageView.setImageResource(iconRes)
                holder.fileDownloadProgress.progress = 100
            } else {
                contentDownloadStateTrackerBinder.bind(mxcUrl, holder)
                holder.fileImageView.setImageResource(R.drawable.ic_download)
                holder.fileDownloadProgress.progress = 0
            }
        }
//        holder.view.setOnClickListener(clickListener)

        holder.filenameView.setOnClickListener(attributes.itemClickListener)
        holder.filenameView.setOnLongClickListener(attributes.itemLongClickListener)
        holder.fileImageWrapper.setOnClickListener(attributes.itemClickListener)
        holder.fileImageWrapper.setOnLongClickListener(attributes.itemLongClickListener)
        holder.filenameView.paintFlags =
            (holder.filenameView.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        contentUploadStateTrackerBinder.unbind(attributes.informationData.eventId)
        contentDownloadStateTrackerBinder.unbind(mxcUrl)
    }

    override fun getViewType() = defaultLayout

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val progressLayout by bind<ViewGroup>(R.id.messageFileUploadProgressLayout)
        val fileLayout by bind<ViewGroup>(R.id.messageFileLayout)
        val fileImageView by bind<ImageView>(R.id.messageFileIconView)
        val fileImageWrapper by bind<ViewGroup>(R.id.messageFileImageView)
        val fileDownloadProgress by bind<ProgressBar>(R.id.messageFileProgressbar)
        val filenameView by bind<TextView>(R.id.messageFilenameView)
    }

    companion object {
        private const val STUB_ID = 0
    }
}