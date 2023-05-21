package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.google.android.material.button.MaterialButton
import org.matrix.android.sdk.api.session.room.model.message.MessageOptionsContent
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.home.room.detail.RoomDetailAction
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

@EpoxyModelClass
abstract class MessageOptionsItem : AbsMessageItem<MessageOptionsItem.Holder>() {

    override fun getDefaultLayout(): Int =
        if (attributes.informationData.sentByMe) {
            R.layout.sent_item_timeline_event_option_buttons
        } else {
            R.layout.item_timeline_event_option_button
        }

    @EpoxyAttribute
    var optionsContent: MessageOptionsContent? = null

    @EpoxyAttribute
    var callback: TimelineEventController.Callback? = null

    @EpoxyAttribute
    var informationData: MessageInformationData? = null

    override fun getViewType() = defaultLayout

    override fun bind(holder: Holder) {
        super.bind(holder)

        renderSendState(holder.view, holder.labelText)

        holder.labelText.setTextOrHide(optionsContent?.label)

        holder.buttonContainer.removeAllViews()

        val relatedEventId = informationData?.eventId ?: return
        val options = optionsContent?.options?.takeIf { it.isNotEmpty() } ?: return
        // Now add back the buttons
        options.forEachIndexed { index, option ->
            val materialButton = LayoutInflater.from(holder.view.context).inflate(R.layout.option_buttons, holder.buttonContainer, false)
                    as MaterialButton
            holder.buttonContainer.addView(materialButton, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            materialButton.text = option.label
            materialButton.setOnClickListener {
                callback?.onTimelineItemAction(RoomDetailAction.ReplyToOptions(relatedEventId, index, option.value ?: "$index"))
            }
        }
    }

    class Holder : AbsMessageItem.Holder(STUB_ID) {

        val labelText by bind<TextView>(R.id.optionLabelText)

        val buttonContainer by bind<ViewGroup>(R.id.optionsButtonContainer)
    }

    companion object {
        private const val STUB_ID = 0
    }
}