package org.navgurukul.chat.features.home.room.detail.timeline.action

import androidx.lifecycle.Transformations
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.isTextMessage
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.*
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.hasBeenEdited
import org.matrix.android.sdk.rx.asObservable
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.canReact
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.home.room.format.NoticeEventFormatter
import org.navgurukul.chat.features.html.EventHtmlRenderer
import org.navgurukul.chat.features.html.SaralHtmlCompressor
import org.navgurukul.chat.features.powerlevel.PowerLevelsObservableFactory
import org.navgurukul.chat.features.reactions.data.EmojiDataSource
import org.navgurukul.chat.features.settings.ChatPreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.SingleLiveEvent
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.resources.StringProvider

/**
 * Activity shared view model to handle message actions
 */
class MessageSharedActionDataSource : SingleLiveEvent<EventSharedAction>()

sealed class MessageActionsAction : ViewModelAction {
    object ToggleReportMenu : MessageActionsAction()
}


/**
 * Information related to an event and used to display preview in contextual bottom sheet.
 */
class MessageActionsViewModel(
    initialState: MessageActionState,
    private val htmlCompressor: SaralHtmlCompressor,
    activeSessionHolder: ActiveSessionHolder,
    private val noticeEventFormatter: NoticeEventFormatter,
    private val stringProvider: StringProvider,
    private val chatPreferences: ChatPreferences
) : BaseViewModel<EmptyViewEvents, MessageActionState>(initialState) {

    private val eventHtmlRenderer: EventHtmlRenderer by inject(EventHtmlRenderer::class.java)

    private val eventId = initialState.eventId
    private val informationData = initialState.informationData
    private val session = activeSessionHolder.getActiveSession()
    private val room = session.getRoom(initialState.roomId)

    init {
        observeEvent()
        observeReactions()
        observePowerLevel()
        observeTimelineEventState()
    }

    fun handle(action: MessageActionsAction) {
        when (action) {
            MessageActionsAction.ToggleReportMenu -> toggleReportMenu()
        }
    }

    private fun toggleReportMenu() = setState {
        copy(
            expendedReportContentMenu = expendedReportContentMenu.not()
        )
    }

    private fun observePowerLevel() {
        if (room == null) {
            return
        }
        PowerLevelsObservableFactory(room).createObservable()
            .subscribe {
                val powerLevelsHelper = PowerLevelsHelper(it)
                val canReact = powerLevelsHelper.isUserAllowedToSend(
                    session.myUserId,
                    false,
                    EventType.REACTION
                )
                val canRedact = powerLevelsHelper.isUserAbleToRedact(session.myUserId)
                val canSendMessage = powerLevelsHelper.isUserAllowedToSend(
                    session.myUserId,
                    false,
                    EventType.MESSAGE
                )
                val permissions = ActionPermissions(
                    canSendMessage = canSendMessage,
                    canRedact = canRedact,
                    canReact = canReact
                )
                setState {
                    copy(actionPermissions = permissions)
                }
            }.disposeOnClear()
    }

    private fun observeEvent() {
        if (room == null) return
        room.rx()
            .liveTimelineEvent(eventId)
            .unwrap()
            .execute {
                copy(timelineEvent = it)
            }
    }

    private fun observeReactions() {
        if (room == null) return
        room.rx()
            .liveAnnotationSummary(eventId)
            .map { annotations ->
                EmojiDataSource.quickEmojis.map { emoji ->
                    ToggleState(
                        emoji,
                        annotations.getOrNull()?.reactionsSummary?.firstOrNull { it.key == emoji }?.addedByMe
                            ?: false
                    )
                }
            }
            .execute {
                copy(quickStates = it)
            }
    }

    private fun observeTimelineEventState() {
        Transformations.map(viewState) {
            it.timelineEvent to it.actionPermissions
        }.asObservable()
            .distinctUntilChanged()
            .subscribe { (timelineEvent, permissions) ->
                val nonNullTimelineEvent = timelineEvent() ?: return@subscribe
                setState {
                    copy(
                        messageBody = computeMessageBody(nonNullTimelineEvent),
                        actions = actionsForEvent(nonNullTimelineEvent, permissions)
                    )
                }
            }.disposeOnClear()
    }

    private fun computeMessageBody(timelineEvent: TimelineEvent): CharSequence {
        if (timelineEvent.root.isRedacted()) {
            return noticeEventFormatter.formatRedactedEvent(timelineEvent.root)
        }

        return when (timelineEvent.root.getClearType()) {
            EventType.MESSAGE,
            EventType.STICKER -> {
                val messageContent: MessageContent? = timelineEvent.getLastMessageContent()
                if (messageContent is MessageTextContent && messageContent.format == MessageFormat.FORMAT_MATRIX_HTML) {
                    val html = messageContent.formattedBody
                        ?.takeIf { it.isNotBlank() }
                        ?.let { htmlCompressor.compress(it) }
                        ?: messageContent.body

                    eventHtmlRenderer.render(html)
                } else if (messageContent is MessageVerificationRequestContent) {
                    stringProvider.getString(R.string.verification_request)
                } else {
                    messageContent?.body
                }
            }
            EventType.STATE_ROOM_NAME,
            EventType.STATE_ROOM_TOPIC,
            EventType.STATE_ROOM_AVATAR,
            EventType.STATE_ROOM_MEMBER,
            EventType.STATE_ROOM_ALIASES,
            EventType.STATE_ROOM_CANONICAL_ALIAS,
            EventType.STATE_ROOM_HISTORY_VISIBILITY,
            EventType.CALL_INVITE,
            EventType.CALL_CANDIDATES,
            EventType.CALL_HANGUP,
            EventType.CALL_ANSWER -> {
                noticeEventFormatter.format(timelineEvent)
            }
            else -> null
        } ?: ""
    }

    private fun getRedactionReason(timelineEvent: TimelineEvent): String {
        return (timelineEvent
            .root
            .unsignedData
            ?.redactedEvent
            ?.content
            ?.get("reason") as? String)
            ?.takeIf { it.isNotBlank() }
            .let { reason ->
                if (reason == null) {
                    if (timelineEvent.root.isRedactedBySameUser()) {
                        stringProvider.getString(R.string.event_redacted_by_user_reason)
                    } else {
                        stringProvider.getString(R.string.event_redacted_by_admin_reason)
                    }
                } else {
                    if (timelineEvent.root.isRedactedBySameUser()) {
                        stringProvider.getString(
                            R.string.event_redacted_by_user_reason_with_reason,
                            reason
                        )
                    } else {
                        stringProvider.getString(
                            R.string.event_redacted_by_admin_reason_with_reason,
                            reason
                        )
                    }
                }
            }
    }

    private fun actionsForEvent(
        timelineEvent: TimelineEvent,
        actionPermissions: ActionPermissions
    ): List<EventSharedAction> {
        val messageContent: MessageContent? =
            timelineEvent.annotations?.editSummary?.aggregatedContent.toModel()
                ?: timelineEvent.root.getClearContent().toModel()
        val msgType = messageContent?.msgType

        return arrayListOf<EventSharedAction>().apply {
            if (timelineEvent.root.sendState.hasFailed()) {
                if (canRetry(timelineEvent, actionPermissions)) {
                    add(EventSharedAction.Resend(eventId))
                }
                add(EventSharedAction.Remove(eventId))
            } else if (timelineEvent.root.sendState.isSending()) {
                // TODO is uploading attachment?
                if (canCancel(timelineEvent)) {
                    add(EventSharedAction.Cancel(eventId))
                }
            } else if (timelineEvent.root.sendState == SendState.SYNCED) {
                if (!timelineEvent.root.isRedacted()) {
                    if (canReply(timelineEvent, messageContent, actionPermissions)) {
                        add(EventSharedAction.Reply(eventId))
                    }

                    if (canEdit(timelineEvent, session.myUserId, actionPermissions)) {
                        add(EventSharedAction.Edit(eventId))
                    }

                    if (canRedact(timelineEvent, actionPermissions)) {
                        add(
                            EventSharedAction.Redact(
                                eventId,
                                askForReason = informationData.senderId != session.myUserId
                            )
                        )
                    }

                    if (canCopy(msgType)) {
                        // TODO copy images? html? see ClipBoard
                        add(EventSharedAction.Copy(messageContent!!.body))
                    }

                    if (timelineEvent.canReact() && actionPermissions.canReact) {
                        add(EventSharedAction.AddReaction(eventId))
                    }

                    if (canQuote(timelineEvent, messageContent, actionPermissions)) {
                        add(EventSharedAction.Quote(eventId))
                    }

                    if (canViewReactions(timelineEvent)) {
                        add(EventSharedAction.ViewReactions(informationData))
                    }

                    if (timelineEvent.hasBeenEdited()) {
                        add(EventSharedAction.ViewEditHistory(informationData))
                    }

                    if (canShare(msgType) && messageContent is MessageWithAttachmentContent) {
                        add(EventSharedAction.Share(timelineEvent.eventId, messageContent))
                    }

                    if (canSave(msgType) && messageContent is MessageWithAttachmentContent) {
                        add(EventSharedAction.Save(timelineEvent.eventId, messageContent))
                    }

                    if (timelineEvent.root.sendState == SendState.SENT) {
                        // TODO Can be redacted

                        // TODO sent by me or sufficient power level
                    }
                }

                if (chatPreferences.developerMode()) {
                    if (timelineEvent.isEncrypted() && timelineEvent.root.mCryptoError != null) {
                        val keysBackupService = session.cryptoService().keysBackupService()
                        if (keysBackupService.state == KeysBackupState.NotTrusted
                            || (keysBackupService.state == KeysBackupState.ReadyToBackUp
                                    && keysBackupService.canRestoreKeys())
                        ) {
                            add(EventSharedAction.UseKeyBackup)
                        }
                        if (session.cryptoService()
                                .getCryptoDeviceInfo(session.myUserId).size > 1
                        ) {
                            add(EventSharedAction.ReRequestKey(timelineEvent.eventId))
                        }
                    }

                    add(EventSharedAction.ViewSource(timelineEvent.root.toContentStringWithIndent()))
                    if (timelineEvent.isEncrypted() && timelineEvent.root.mxDecryptionResult != null) {
                        val decryptedContent = timelineEvent.root.toClearContentStringWithIndent()
                            ?: stringProvider.getString(R.string.encryption_information_decryption_error)
                        add(EventSharedAction.ViewDecryptedSource(decryptedContent))
                    }
                }
//                add(EventSharedAction.CopyPermalink(eventId))
                if (session.myUserId != timelineEvent.root.senderId) {
                    // not sent by me
                    if (timelineEvent.root.getClearType() == EventType.MESSAGE) {
                        add(EventSharedAction.ReportContent(eventId, timelineEvent.root.senderId))
                    }

                    add(EventSharedAction.Separator)
                    add(EventSharedAction.IgnoreUser(timelineEvent.root.senderId))
                }
            }
        }
    }

    private fun canCancel(@Suppress("UNUSED_PARAMETER") event: TimelineEvent): Boolean {
        return false
    }

    private fun canReply(
        event: TimelineEvent,
        messageContent: MessageContent?,
        actionPermissions: ActionPermissions
    ): Boolean {
        // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
        if (event.root.getClearType() != EventType.MESSAGE) return false
        if (!actionPermissions.canSendMessage) return false
        return when (messageContent?.msgType) {
            MessageType.MSGTYPE_TEXT,
            MessageType.MSGTYPE_NOTICE,
            MessageType.MSGTYPE_EMOTE,
            MessageType.MSGTYPE_IMAGE,
            MessageType.MSGTYPE_VIDEO,
            MessageType.MSGTYPE_AUDIO,
            MessageType.MSGTYPE_FILE -> true
            else -> false
        }
    }

    private fun canQuote(
        event: TimelineEvent,
        messageContent: MessageContent?,
        actionPermissions: ActionPermissions
    ): Boolean {
        // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
        if (event.root.getClearType() != EventType.MESSAGE) return false
        if (!actionPermissions.canSendMessage) return false
        return when (messageContent?.msgType) {
            MessageType.MSGTYPE_TEXT,
            MessageType.MSGTYPE_NOTICE,
            MessageType.MSGTYPE_EMOTE,
            MessageType.MSGTYPE_LOCATION -> {
                true
            }
            else -> false
        }
    }

    private fun canRedact(event: TimelineEvent, actionPermissions: ActionPermissions): Boolean {
        // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
        if (event.root.getClearType() != EventType.MESSAGE) return false
        // Message sent by the current user can always be redacted
        if (event.root.senderId == session.myUserId) return true
        // Check permission for messages sent by other users
        return actionPermissions.canRedact
    }

    private fun canRetry(event: TimelineEvent, actionPermissions: ActionPermissions): Boolean {
        return event.root.sendState.hasFailed() && event.root.isTextMessage() && actionPermissions.canSendMessage
    }

    private fun canViewReactions(event: TimelineEvent): Boolean {
        // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
        if (event.root.getClearType() != EventType.MESSAGE) return false
        // TODO if user is admin or moderator
        return event.annotations?.reactionsSummary?.isNotEmpty() ?: false
    }

    private fun canEdit(
        event: TimelineEvent,
        myUserId: String,
        actionPermissions: ActionPermissions
    ): Boolean {
        // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
        if (event.root.getClearType() != EventType.MESSAGE) return false
        if (!actionPermissions.canSendMessage) return false
        // TODO if user is admin or moderator
        val messageContent = event.root.getClearContent().toModel<MessageContent>()
        return event.root.senderId == myUserId && (
                messageContent?.msgType == MessageType.MSGTYPE_TEXT
                        || messageContent?.msgType == MessageType.MSGTYPE_EMOTE
                )
    }

    private fun canCopy(msgType: String?): Boolean {
        return when (msgType) {
            MessageType.MSGTYPE_TEXT,
            MessageType.MSGTYPE_NOTICE,
            MessageType.MSGTYPE_EMOTE,
            MessageType.MSGTYPE_LOCATION -> true
            else -> false
        }
    }

    private fun canShare(msgType: String?): Boolean {
        return when (msgType) {
            MessageType.MSGTYPE_IMAGE,
            MessageType.MSGTYPE_AUDIO,
            MessageType.MSGTYPE_VIDEO,
            MessageType.MSGTYPE_FILE -> true
            else -> false
        }
    }

    private fun canSave(msgType: String?): Boolean {
        return when (msgType) {
            MessageType.MSGTYPE_IMAGE,
            MessageType.MSGTYPE_AUDIO,
            MessageType.MSGTYPE_VIDEO,
            MessageType.MSGTYPE_FILE -> true
            else -> false
        }
    }
}