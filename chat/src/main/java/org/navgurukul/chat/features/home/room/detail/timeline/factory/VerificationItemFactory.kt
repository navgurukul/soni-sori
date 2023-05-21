package org.navgurukul.chat.features.home.room.detail.timeline.factory

import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.safeValueOf
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageRelationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVerificationCancelContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.internal.session.room.VerificationState
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.resources.UserPreferencesProvider
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageItemAttributesFactory
import org.navgurukul.chat.features.home.room.detail.timeline.item.StatusTileTimelineItem
import org.navgurukul.chat.features.home.room.detail.timeline.item.StatusTileTimelineItem_
import org.navgurukul.commonui.resources.StringProvider

/**
 * Can creates verification conclusion items
 * Notice that not all KEY_VERIFICATION_DONE will be displayed in timeline,
 * several checks are made to see if this conclusion is attached to a known request
 */
class VerificationItemFactory(
    private val messageColorProvider: MessageColorProvider,
    private val messageItemAttributesFactory: MessageItemAttributesFactory,
    private val userPreferencesProvider: UserPreferencesProvider,
    private val stringProvider: StringProvider,
    private val sessionHolder: ActiveSessionHolder,
    private val messageInformationDataFactory: MessageInformationDataFactory,
    private val noticeItemFactory: NoticeItemFactory
) {


    fun create(
        event: TimelineEvent,
        highlight: Boolean,
        callback: TimelineEventController.Callback?
    ): MerakiEpoxyModel<*>? {
        if (event.root.eventId == null) return null

        val relContent: MessageRelationContent = event.root.content.toModel()
            ?: event.root.getClearContent().toModel()
            ?: return ignoredConclusion(event, highlight, callback)

        if (relContent.relatesTo?.type != RelationType.REFERENCE) return ignoredConclusion(
            event,
            highlight,
            callback
        )
        val refEventId = relContent.relatesTo?.eventId
            ?: return ignoredConclusion(event, highlight, callback)

        // If we cannot find the referenced request we do not display the done event
        val refEvent = sessionHolder.getActiveSession().getRoom(event.root.roomId ?: "")
            ?.getTimeLineEvent(refEventId)
            ?: return ignoredConclusion(event, highlight, callback)

        // If it's not a request ignore this event
        // if (refEvent.root.getClearContent().toModel<MessageVerificationRequestContent>() == null) return ignoredConclusion(event, highlight, callback)

        val referenceInformationData = messageInformationDataFactory.create(refEvent, null)

        val informationData = messageInformationDataFactory.create(event, null)
        val attributes = messageItemAttributesFactory.create(null, informationData, callback)

        when (event.root.getClearType()) {
            EventType.KEY_VERIFICATION_CANCEL -> {
                // Is the request referenced is actually really cancelled?
                val cancelContent =
                    event.root.getClearContent().toModel<MessageVerificationCancelContent>()
                        ?: return ignoredConclusion(event, highlight, callback)

                when (safeValueOf(cancelContent.code)) {
                    CancelCode.MismatchedCommitment,
                    CancelCode.MismatchedKeys,
                    CancelCode.MismatchedSas -> {
                        // We should display these bad conclusions
                        return StatusTileTimelineItem_()
                            .attributes(
                                StatusTileTimelineItem.Attributes(
                                    title = stringProvider.getString(R.string.verification_conclusion_warning),
                                    description = "${informationData.memberName} (${informationData.senderId})",
                                    shieldUIState = StatusTileTimelineItem.ShieldUIState.RED,
                                    informationData = informationData,
                                    avatarRenderer = attributes.avatarRenderer,
                                    messageColorProvider = messageColorProvider,
                                    emojiTypeFace = attributes.emojiTypeFace,
                                    itemClickListener = attributes.itemClickListener,
                                    reactionPillCallback = callback,
                                    itemLongClickListener = attributes.itemLongClickListener
                                )
                            )
                            .highlighted(highlight)
                    }
                    else -> return ignoredConclusion(event, highlight, callback)
                }
            }
            EventType.KEY_VERIFICATION_DONE -> {
                // Is the request referenced is actually really completed?
                if (referenceInformationData.referencesInfoData?.verificationStatus != VerificationState.DONE) {
                    return ignoredConclusion(event, highlight, callback)
                }
                // We only tale the one sent by me

                if (informationData.sentByMe) {
                    // We only display the done sent by the other user, the done send by me is ignored
                    return ignoredConclusion(event, highlight, callback)
                }
                return StatusTileTimelineItem_()
                    .attributes(
                        StatusTileTimelineItem.Attributes(
                            title = stringProvider.getString(R.string.sas_verified),
                            description = "${informationData.memberName} (${informationData.senderId})",
                            shieldUIState = StatusTileTimelineItem.ShieldUIState.GREEN,
                            informationData = informationData,
                            avatarRenderer = attributes.avatarRenderer,
                            messageColorProvider = messageColorProvider,
                            emojiTypeFace = attributes.emojiTypeFace,
                            itemClickListener = attributes.itemClickListener,
                            reactionPillCallback = callback,
                            itemLongClickListener = attributes.itemLongClickListener
                        )
                    )
                    .highlighted(highlight)
            }
        }
        return null
    }

    private fun ignoredConclusion(
        event: TimelineEvent,
        highlight: Boolean,
        callback: TimelineEventController.Callback?
    ): MerakiEpoxyModel<*>? {
        if (userPreferencesProvider.shouldShowHiddenEvents()) return noticeItemFactory.create(
            event,
            highlight,
            callback
        )
        return null
    }
}
