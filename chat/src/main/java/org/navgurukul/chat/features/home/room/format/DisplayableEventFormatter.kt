package org.navgurukul.chat.features.home.room.format

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent
import me.gujun.android.span.span
import org.matrix.android.sdk.api.session.room.timeline.isReply
import org.navgurukul.chat.R
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.commonui.resources.StringProvider

class DisplayableEventFormatter(
    private val stringProvider: StringProvider,
    private val colorProvider: ColorProvider,
    private val noticeEventFormatter: NoticeEventFormatter
) {

    fun format(timelineEvent: TimelineEvent, appendAuthor: Boolean): CharSequence {
        if (timelineEvent.root.isRedacted()) {
            return noticeEventFormatter.formatRedactedEvent(timelineEvent.root)
        }

        if (timelineEvent.root.isEncrypted()
                && timelineEvent.root.mxDecryptionResult == null) {
            return stringProvider.getString(R.string.encrypted_message)
        }

        val senderName = timelineEvent.senderInfo.disambiguatedDisplayName

        when (timelineEvent.root.getClearType()) {
            EventType.STICKER -> {
                return simpleFormat(senderName, stringProvider.getString(R.string.send_a_sticker), appendAuthor)
            }
            EventType.MESSAGE -> {
                timelineEvent.getLastMessageContent()?.let { messageContent ->
                    when (messageContent.msgType) {
                        MessageType.MSGTYPE_VERIFICATION_REQUEST -> {
                            return simpleFormat(senderName, stringProvider.getString(R.string.verification_request), appendAuthor)
                        }
                        MessageType.MSGTYPE_IMAGE                -> {
                            return simpleFormat(senderName, stringProvider.getString(R.string.sent_an_image), appendAuthor)
                        }
                        MessageType.MSGTYPE_AUDIO                -> {
                            return simpleFormat(senderName, stringProvider.getString(R.string.sent_an_audio_file), appendAuthor)
                        }
                        MessageType.MSGTYPE_VIDEO                -> {
                            return simpleFormat(senderName, stringProvider.getString(R.string.sent_a_video), appendAuthor)
                        }
                        MessageType.MSGTYPE_FILE                 -> {
                            return simpleFormat(senderName, stringProvider.getString(R.string.sent_a_file), appendAuthor)
                        }
                        MessageType.MSGTYPE_TEXT                 -> {
                            if (timelineEvent.isReply()) {
                                // Skip reply prefix, and show important
                                // TODO add a reply image span ?
                                return simpleFormat(senderName, timelineEvent.getTextEditableContent()
                                        ?: messageContent.body, appendAuthor)
                            } else {
                                return simpleFormat(senderName, messageContent.body, appendAuthor)
                            }
                        }
                        else                                     -> {
                            return simpleFormat(senderName, messageContent.body, appendAuthor)
                        }
                    }
                }
            }
            else              -> {
                return span {
                    text = noticeEventFormatter.format(timelineEvent) ?: ""
                    textStyle = "italic"
                }
            }
        }

        return span { }
    }

    private fun simpleFormat(senderName: String, body: CharSequence, appendAuthor: Boolean): CharSequence {
        return if (appendAuthor) {
            span {
                text = senderName
                textColor = colorProvider.getColorFromAttribute(R.attr.textPrimary)
            }
                    .append(": ")
                    .append(body)
        } else {
            body
        }
    }
}
