package org.navgurukul.chat.features.home.room.detail.timeline.factory

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.*
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.internal.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.internal.crypto.model.event.EncryptedEventContent
import me.gujun.android.span.span
import org.commonmark.node.Document
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.core.utils.DimensionConverter
import org.navgurukul.chat.core.utils.containsOnlyEmojis
import org.navgurukul.chat.core.utils.isLocalFile
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.*
import org.navgurukul.chat.features.home.room.detail.timeline.item.*
import org.navgurukul.chat.features.home.room.detail.timeline.tools.createLinkMovementMethod
import org.navgurukul.chat.features.home.room.detail.timeline.tools.linkify
import org.navgurukul.chat.features.html.CodeVisitor
import org.navgurukul.chat.features.html.EventHtmlRenderer
import org.navgurukul.chat.features.html.SaralHtmlCompressor
import org.navgurukul.chat.features.media.ImageContentRenderer
import org.navgurukul.chat.features.media.VideoContentRenderer
import org.navgurukul.commonui.resources.StringProvider

class MessageItemFactory(
    private val colorProvider: ColorProvider,
    private val dimensionConverter: DimensionConverter,
    private val htmlCompressor: SaralHtmlCompressor,
    private val stringProvider: StringProvider,
    private val imageContentRenderer: ImageContentRenderer,
    private val messageItemAttributesFactory: MessageItemAttributesFactory,
    private val avatarSizeProvider: AvatarSizeProvider,
    private val sessionHolder: ActiveSessionHolder,
    private val htmlRenderer: EventHtmlRenderer,
    private val timelineMediaSizeProvider: TimelineMediaSizeProvider,
    private val contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder,
    private val contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder,
    private val messageInformationDataFactory: MessageInformationDataFactory,
    private val defaultItemFactory: DefaultItemFactory,
    private val noticeItemFactory: NoticeItemFactory

) {

    fun create(
        event: TimelineEvent,
        nextEvent: TimelineEvent?,
        highlight: Boolean,
        callback: TimelineEventController.Callback?
    ): MerakiEpoxyModel<*>? {
        event.root.eventId ?: return null

        val informationData = messageInformationDataFactory.create(event, nextEvent)

        if (event.root.isRedacted()) {
            // message is redacted
            val attributes = messageItemAttributesFactory.create(null, informationData, callback)
            return buildRedactedItem(attributes, highlight)
        }

        val messageContent = event.getLastMessageContent()
        if (messageContent == null) {
            val malformedText = stringProvider.getString(R.string.malformed_message)
            return defaultItemFactory.create(malformedText, informationData, highlight, callback)
        }
        if (messageContent.relatesTo?.type == RelationType.REPLACE
            || event.isEncrypted() && event.root.content.toModel<EncryptedEventContent>()?.relatesTo?.type == RelationType.REPLACE
        ) {
            // This is an edit event, we should display it when debugging as a notice event
            return noticeItemFactory.create(event, highlight, callback)
        }
        val attributes =
            messageItemAttributesFactory.create(messageContent, informationData, callback)

//        val all = event.root.toContent()
//        val ev = all.toModel<Event>()
        return when (messageContent) {
            is MessageEmoteContent -> buildEmoteMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessageTextContent -> buildItemForTextContent(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessageImageInfoContent -> buildImageMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessageNoticeContent -> buildNoticeMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessageVideoContent -> buildVideoMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessageFileContent -> buildFileMessageItem(messageContent, highlight, attributes)
            is MessageAudioContent -> buildAudioMessageItem(
                messageContent,
                informationData,
                highlight,
                attributes
            )
//            is MessageVerificationRequestContent -> buildVerificationRequestMessageItem(messageContent, informationData, highlight, callback, attributes)
            is MessageOptionsContent -> buildOptionsMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
            is MessagePollResponseContent -> noticeItemFactory.create(event, highlight, callback)
            else -> buildNotHandledMessageItem(
                messageContent,
                informationData,
                highlight,
                callback,
                attributes
            )
        }
    }

    private fun buildOptionsMessageItem(
        messageContent: MessageOptionsContent,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MerakiEpoxyModel<*>? {
        return when (messageContent.optionType) {
            OPTION_TYPE_BUTTONS -> {
                MessageOptionsItem_()
                    .attributes(attributes)
                    .callback(callback)
                    .informationData(informationData)
                    .optionsContent(messageContent)
                    .highlighted(highlight)
            }
            else -> {
                // Not supported optionType
                buildNotHandledMessageItem(
                    messageContent,
                    informationData,
                    highlight,
                    callback,
                    attributes
                )
            }
        }
    }

    private fun buildAudioMessageItem(
        messageContent: MessageAudioContent,
        @Suppress("UNUSED_PARAMETER")
        informationData: MessageInformationData,
        highlight: Boolean,
        attributes: AbsMessageItem.Attributes
    ): MessageFileItem? {
        return MessageFileItem_()
            .attributes(attributes)
            .izLocalFile(messageContent.getFileUrl().isLocalFile())
            .mxcUrl(messageContent.getFileUrl() ?: "")
            .contentUploadStateTrackerBinder(contentUploadStateTrackerBinder)
            .contentDownloadStateTrackerBinder(contentDownloadStateTrackerBinder)
            .highlighted(highlight)
            .filename(messageContent.body)
            .iconRes(R.drawable.ic_headphones)
    }

//    private fun buildVerificationRequestMessageItem(messageContent: MessageVerificationRequestContent,
//                                                    @Suppress("UNUSED_PARAMETER")
//                                                    informationData: MessageInformationData,
//                                                    highlight: Boolean,
//                                                    callback: TimelineEventController.Callback?,
//                                                    attributes: AbsMessageItem.Attributes): VerificationRequestItem? {
//        // If this request is not sent by me or sent to me, we should ignore it in timeline
//        val myUserId = session.myUserId
//        if (informationData.senderId != myUserId && messageContent.toUserId != myUserId) {
//            return null
//        }
//
//        val otherUserId = if (informationData.sentByMe) messageContent.toUserId else informationData.senderId
//        val otherUserName = if (informationData.sentByMe) session.getUser(messageContent.toUserId)?.displayName
//        else informationData.memberName
//        return VerificationRequestItem_()
//            .attributes(
//                VerificationRequestItem.Attributes(
//                    otherUserId = otherUserId,
//                    otherUserName = otherUserName.toString(),
//                    referenceId = informationData.eventId,
//                    informationData = informationData,
//                    avatarRenderer = attributes.avatarRenderer,
//                    messageColorProvider = attributes.messageColorProvider,
//                    itemLongClickListener = attributes.itemLongClickListener,
//                    itemClickListener = attributes.itemClickListener,
//                    reactionPillCallback = attributes.reactionPillCallback,
//                    readReceiptsCallback = attributes.readReceiptsCallback,
//                    emojiTypeFace = attributes.emojiTypeFace
//                )
//            )
//            .callback(callback)
//            .highlighted(highlight)
//            .leftGuideline(avatarSizeProvider.leftGuideline)
//    }

    private fun buildFileMessageItem(
        messageContent: MessageFileContent,
//                                     informationData: MessageInformationData,
        highlight: Boolean,
//                                     callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageFileItem? {
        val mxcUrl = messageContent.getFileUrl() ?: ""
        return MessageFileItem_()
            .attributes(attributes)
            .izLocalFile(messageContent.getFileUrl().isLocalFile())
            .izDownloaded(
                sessionHolder.getActiveSession().fileService()
                    .isFileInCache(mxcUrl, messageContent.mimeType)
            )
            .mxcUrl(mxcUrl)
            .contentUploadStateTrackerBinder(contentUploadStateTrackerBinder)
            .contentDownloadStateTrackerBinder(contentDownloadStateTrackerBinder)
            .highlighted(highlight)
            .filename(messageContent.body)
            .iconRes(R.drawable.ic_paperclip)
    }

    private fun buildNotHandledMessageItem(
        messageContent: MessageContent,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageTextItem? {
        // For compatibility reason we should display the body
        return buildMessageTextItem(
            messageContent.body,
            false,
            informationData,
            highlight,
            callback,
            attributes
        )
    }

    private fun buildImageMessageItem(
        messageContent: MessageImageInfoContent,
        @Suppress("UNUSED_PARAMETER")
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageImageVideoItem? {
        val (maxWidth, maxHeight) = timelineMediaSizeProvider.getMaxSize()
        val data = ImageContentRenderer.Data(
            eventId = informationData.eventId,
            filename = messageContent.body,
            mimeType = messageContent.mimeType,
            url = messageContent.getFileUrl(),
            elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt(),
            height = messageContent.info?.height,
            maxHeight = maxHeight,
            width = messageContent.info?.width,
            maxWidth = maxWidth
        )
        return MessageImageVideoItem_()
            .attributes(attributes)
            .imageContentRenderer(imageContentRenderer)
            .contentUploadStateTrackerBinder(contentUploadStateTrackerBinder)
            .playable(messageContent.info?.mimeType == "image/gif")
            .highlighted(highlight)
            .mediaData(data)
            .apply {
                if (messageContent.msgType == MessageType.MSGTYPE_STICKER_LOCAL) {
                    mode(ImageContentRenderer.Mode.STICKER)
                } else {
                    clickListener(
                        DebouncedClickListener(View.OnClickListener { view ->
                            callback?.onImageMessageClicked(messageContent, data, view)
                        })
                    )
                }
            }
    }

    private fun buildVideoMessageItem(
        messageContent: MessageVideoContent,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageImageVideoItem? {
        val (maxWidth, maxHeight) = timelineMediaSizeProvider.getMaxSize()
        val thumbnailData = ImageContentRenderer.Data(
            eventId = informationData.eventId,
            filename = messageContent.body,
            mimeType = messageContent.mimeType,
            url = messageContent.videoInfo?.thumbnailFile?.url
                ?: messageContent.videoInfo?.thumbnailUrl,
            elementToDecrypt = messageContent.videoInfo?.thumbnailFile?.toElementToDecrypt(),
            height = messageContent.videoInfo?.height,
            maxHeight = maxHeight,
            width = messageContent.videoInfo?.width,
            maxWidth = maxWidth
        )

        val videoData = VideoContentRenderer.Data(
            eventId = informationData.eventId,
            filename = messageContent.body,
            mimeType = messageContent.mimeType,
            url = messageContent.getFileUrl(),
            elementToDecrypt = messageContent.encryptedFileInfo?.toElementToDecrypt(),
            thumbnailMediaData = thumbnailData
        )

        return MessageImageVideoItem_()
            .attributes(attributes)
            .imageContentRenderer(imageContentRenderer)
            .contentUploadStateTrackerBinder(contentUploadStateTrackerBinder)
            .playable(true)
            .highlighted(highlight)
            .mediaData(thumbnailData)
            .clickListener { view ->
                callback?.onVideoMessageClicked(
                    messageContent,
                    videoData,
                    view.findViewById(R.id.messageThumbnailView)
                )
            }
    }

    private fun buildItemForTextContent(
        messageContent: MessageTextContent,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MerakiEpoxyModel<*>? {
        val isFormatted = messageContent.matrixFormattedBody.isNullOrBlank().not()
        return if (isFormatted) {
            // First detect if the message contains some code block(s) or inline code
            val localFormattedBody = htmlRenderer.parse(messageContent.body) as Document
            val codeVisitor = CodeVisitor()
            codeVisitor.visit(localFormattedBody)
            when (codeVisitor.codeKind) {
                CodeVisitor.Kind.BLOCK -> {
                    val codeFormattedBlock = htmlRenderer.render(localFormattedBody)
                    buildCodeBlockItem(
                        codeFormattedBlock,
                        informationData,
                        highlight,
                        callback,
                        attributes
                    )
                }
                CodeVisitor.Kind.INLINE -> {
                    val codeFormatted = htmlRenderer.render(localFormattedBody)
                    buildMessageTextItem(
                        codeFormatted,
                        false,
                        informationData,
                        highlight,
                        callback,
                        attributes
                    )
                }
                CodeVisitor.Kind.NONE -> {
                    val compressed = htmlCompressor.compress(messageContent.formattedBody!!)
                    val formattedBody = htmlRenderer.render(compressed)
                    buildMessageTextItem(
                        formattedBody,
                        true,
                        informationData,
                        highlight,
                        callback,
                        attributes
                    )
                }
            }
        } else {
            buildMessageTextItem(
                messageContent.body,
                false,
                informationData,
                highlight,
                callback,
                attributes
            )
        }
    }

    private fun buildMessageTextItem(
        body: CharSequence,
        isFormatted: Boolean,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageTextItem? {
        val linkifiedBody = body.linkify(callback)

        return MessageTextItem_().apply {
            if (informationData.hasBeenEdited) {
                val spannable = annotateWithEdited(linkifiedBody, callback, informationData)
                message(spannable)
            } else {
                message(linkifiedBody)
            }
        }
            .useBigFont(
                linkifiedBody.length <= MAX_NUMBER_OF_EMOJI_FOR_BIG_FONT * 2 && containsOnlyEmojis(
                    linkifiedBody.toString()
                )
            )
            .searchForPills(isFormatted)
            .attributes(attributes)
            .highlighted(highlight)
            .movementMethod(createLinkMovementMethod(callback))
    }

    private fun buildCodeBlockItem(
        formattedBody: CharSequence,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageBlockCodeItem? {
        return MessageBlockCodeItem_()
            .apply {
                if (informationData.hasBeenEdited) {
                    val spannable = annotateWithEdited("", callback, informationData)
                    editedSpan(spannable)
                }
            }
            .attributes(attributes)
            .highlighted(highlight)
            .message(formattedBody)
    }

    private fun annotateWithEdited(
        linkifiedBody: CharSequence,
        callback: TimelineEventController.Callback?,
        informationData: MessageInformationData
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder()
        spannable.append(linkifiedBody)
        val editedSuffix = stringProvider.getString(R.string.edited_suffix)
        spannable.append(" ").append(editedSuffix)
        val color = colorProvider.getColorFromAttribute(R.attr.textSecondary)
        val editStart = spannable.lastIndexOf(editedSuffix)
        val editEnd = editStart + editedSuffix.length
        spannable.setSpan(
            ForegroundColorSpan(color),
            editStart,
            editEnd,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        // Note: text size is set to 14sp
        spannable.setSpan(
            AbsoluteSizeSpan(dimensionConverter.spToPx(13)),
            editStart,
            editEnd,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    callback?.onEditedDecorationClicked(informationData)
                }

                override fun updateDrawState(ds: TextPaint) {
                    // nop
                }
            },
            editStart,
            editEnd,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun buildNoticeMessageItem(
        messageContent: MessageNoticeContent,
        @Suppress("UNUSED_PARAMETER")
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageTextItem? {
        val formattedBody = span {
            text = messageContent.getHtmlBody()
            textColor = colorProvider.getColorFromAttribute(R.attr.textSecondary)
            textStyle = "italic"
        }

        val message = formattedBody.linkify(callback)

        return MessageTextItem_()
            .attributes(attributes)
            .message(message)
            .highlighted(highlight)
            .movementMethod(createLinkMovementMethod(callback))
    }

    private fun buildEmoteMessageItem(
        messageContent: MessageEmoteContent,
        informationData: MessageInformationData,
        highlight: Boolean,
        callback: TimelineEventController.Callback?,
        attributes: AbsMessageItem.Attributes
    ): MessageTextItem? {
        val formattedBody = SpannableStringBuilder()
        formattedBody.append("* ${informationData.memberName} ")
        formattedBody.append(messageContent.getHtmlBody())

        val message = formattedBody.linkify(callback)

        return MessageTextItem_()
            .apply {
                if (informationData.hasBeenEdited) {
                    val spannable = annotateWithEdited(message, callback, informationData)
                    message(spannable)
                } else {
                    message(message)
                }
            }
            .attributes(attributes)
            .highlighted(highlight)
            .movementMethod(createLinkMovementMethod(callback))
    }

    private fun MessageContentWithFormattedBody.getHtmlBody(): CharSequence {
        return matrixFormattedBody
            ?.let { htmlCompressor.compress(it) }
            ?.let { htmlRenderer.render(it) }
            ?: body
    }

    private fun buildRedactedItem(
        attributes: AbsMessageItem.Attributes,
        highlight: Boolean
    ): RedactedMessageItem? {
        return RedactedMessageItem_()
            .attributes(attributes)
            .highlighted(highlight)
    }

    companion object {
        private const val MAX_NUMBER_OF_EMOJI_FOR_BIG_FONT = 5
    }
}
