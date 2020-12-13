package org.navgurukul.chat.features.home.room.detail

import androidx.lifecycle.Transformations
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.events.model.*
import org.matrix.android.sdk.api.session.file.FileService
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.*
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.read.ReadService
import org.matrix.android.sdk.api.session.room.send.UserDraft
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.internal.crypto.model.event.EncryptedEventContent
import org.matrix.android.sdk.internal.crypto.model.event.WithHeldCode
import org.matrix.android.sdk.rx.asObservable
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.matrix.android.sdk.api.session.room.timeline.*
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.resources.UserPreferencesProvider
import org.navgurukul.chat.core.utils.isValidUrl
import org.navgurukul.chat.core.utils.subscribeLogError
import org.navgurukul.chat.features.command.CommandParser
import org.navgurukul.chat.features.command.ParsedCommand
import org.navgurukul.chat.features.home.room.TypingHelper
import org.navgurukul.chat.features.home.room.detail.timeline.helper.RoomSummaryHolder
import org.navgurukul.chat.features.home.room.detail.timeline.helper.TimelineDisplayableEvents
import org.navgurukul.chat.features.powerlevel.PowerLevelsObservableFactory
import org.navgurukul.chat.features.settings.ChatPreferences
import org.navgurukul.commonui.model.Success
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.resources.StringProvider
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class RoomDetailFragmentViewModel(
    private val initialState: RoomDetailViewState,
    userPreferencesProvider: UserPreferencesProvider,
    private val chatPreferences: ChatPreferences,
    private val stringProvider: StringProvider,
    sessionHolder: ActiveSessionHolder,
    private val roomSummaryHolder: RoomSummaryHolder,
    private val typingHelper: TypingHelper
) : BaseViewModel<RoomDetailFragmentViewEvents, RoomDetailViewState>(initialState),
    Timeline.Listener {

    private val session = sessionHolder.getActiveSession()
    private val room = session.getRoom(initialState.roomId)!!
    private val eventId = initialState.eventId
    private var trackUnreadMessages = AtomicBoolean(false)
    private val visibleEventsObservable =
        BehaviorRelay.create<RoomDetailAction.TimelineEventTurnsVisible>()
    private val invisibleEventsObservable =
        BehaviorRelay.create<RoomDetailAction.TimelineEventTurnsInvisible>()
    private var mostRecentDisplayedEvent: TimelineEvent? = null

    private val timelineSettings = if (userPreferencesProvider.shouldShowHiddenEvents()) {
        TimelineSettings(
            30,
            filters = TimelineEventFilters(
                filterEdits = false,
                filterRedacted = userPreferencesProvider.shouldShowRedactedMessages().not(),
                filterUseless = false,
                filterTypes = false
            ),
            buildReadReceipts = userPreferencesProvider.shouldShowReadReceipts()
        )
    } else {
        TimelineSettings(
            30,
            filters = TimelineEventFilters(
                filterEdits = true,
                filterRedacted = userPreferencesProvider.shouldShowRedactedMessages().not(),
                filterUseless = true,
                filterTypes = true,
                allowedTypes = TimelineDisplayableEvents.DISPLAYABLE_TYPES
            ),
            buildReadReceipts = userPreferencesProvider.shouldShowReadReceipts()
        )
    }

    private var timelineEvents = PublishRelay.create<List<TimelineEvent>>()
    val timeline = room.createTimeline(eventId, timelineSettings)

    init {
        timeline.start()
        timeline.addListener(this)
        observeRoomSummary()
        observeMembershipChanges()
        observeRoomMemberSummary()
        observeSummaryState()
        getUnreadState()
        observeSyncState()
//        observeEventDisplayedActions()
        getDraftIfAny()
        observeUnreadState()
        observeMyRoomMember()
//        observeActiveRoomWidgets()
        observePowerLevel()
        room.getRoomSummaryLive()
        room.markAsRead(ReadService.MarkAsReadParams.READ_RECEIPT, NoOpMatrixCallback())
        room.rx().loadRoomMembersIfNeeded().subscribeLogError().disposeOnClear()
        // Inform the SDK that the room is displayed
        session.onRoomDisplayed(initialState.roomId)
    }

    fun handle(action: RoomDetailAction) {
        when (action) {
            is RoomDetailAction.UserIsTyping -> handleUserIsTyping(action)
            is RoomDetailAction.SaveDraft -> handleSaveDraft(action)
            is RoomDetailAction.SendMessage -> handleSendMessage(action)
//            is RoomDetailAction.SendMedia                        -> handleSendMedia(action)
//            is RoomDetailAction.SendSticker                      -> handleSendSticker(action)
            is RoomDetailAction.TimelineEventTurnsVisible -> handleEventVisible(action)
            is RoomDetailAction.TimelineEventTurnsInvisible -> handleEventInvisible(action)
            is RoomDetailAction.LoadMoreTimelineEvents -> handleLoadMore(action)
            is RoomDetailAction.SendReaction -> handleSendReaction(action)
            is RoomDetailAction.AcceptInvite -> handleAcceptInvite()
            is RoomDetailAction.RejectInvite -> handleRejectInvite()
            is RoomDetailAction.RedactAction -> handleRedactEvent(action)
            is RoomDetailAction.UndoReaction -> handleUndoReact(action)
            is RoomDetailAction.UpdateQuickReactAction -> handleUpdateQuickReaction(action)
            is RoomDetailAction.EnterRegularMode -> handleExitSpecialMode(action)
            is RoomDetailAction.EnterEditMode -> handleEditAction(action)
            is RoomDetailAction.EnterQuoteMode -> handleQuoteAction(action)
            is RoomDetailAction.EnterReplyMode -> handleReplyAction(action)
            is RoomDetailAction.DownloadOrOpen -> handleOpenOrDownloadFile(action)
            is RoomDetailAction.NavigateToEvent -> handleNavigateToEvent(action)
            is RoomDetailAction.HandleTombstoneEvent -> handleTombstoneEvent(action)
            is RoomDetailAction.ResendMessage -> handleResendEvent(action)
            is RoomDetailAction.RemoveFailedEcho -> handleRemove(action)
//            is RoomDetailAction.ClearSendQueue                   -> handleClearSendQueue()
//            is RoomDetailAction.ResendAll                        -> handleResendAll()
            is RoomDetailAction.MarkAllAsRead -> handleMarkAllAsRead()
            is RoomDetailAction.ReportContent -> handleReportContent(action)
            is RoomDetailAction.IgnoreUser -> handleIgnoreUser(action)
            is RoomDetailAction.EnterTrackingUnreadMessagesState -> startTrackingUnreadMessages()
            is RoomDetailAction.ExitTrackingUnreadMessagesState -> stopTrackingUnreadMessages()
            is RoomDetailAction.ReplyToOptions -> handleReplyToOptions(action)
//            is RoomDetailAction.AcceptVerificationRequest        -> handleAcceptVerification(action)
//            is RoomDetailAction.DeclineVerificationRequest       -> handleDeclineVerification(action)
//            is RoomDetailAction.RequestVerification              -> handleRequestVerification(action)
            is RoomDetailAction.ResumeVerification -> handleResumeRequestVerification(action)
//            is RoomDetailAction.ReRequestKeys                    -> handleReRequestKeys(action)
            is RoomDetailAction.TapOnFailedToDecrypt -> handleTapOnFailedToDecrypt(action)
//            is RoomDetailAction.SelectStickerAttachment          -> handleSelectStickerAttachment()
//            is RoomDetailAction.OpenIntegrationManager           -> handleOpenIntegrationManager()
//            is RoomDetailAction.StartCall                        -> handleStartCall(action)
//            is RoomDetailAction.EndCall                          -> handleEndCall()
        }
    }

    private fun handleNavigateToEvent(action: RoomDetailAction.NavigateToEvent) {
        stopTrackingUnreadMessages()
        val targetEventId: String = action.eventId
        val correctedEventId = timeline.getFirstDisplayableEventId(targetEventId) ?: targetEventId
        val indexOfEvent = timeline.getIndexOfEvent(correctedEventId)
        if (indexOfEvent == null) {
            // Event is not already in RAM
            timeline.restartWithEventId(targetEventId)
        }
        if (action.highlight) {
            setState { copy(highlightedEventId = correctedEventId) }
        }
        _viewEvents.setValue((RoomDetailFragmentViewEvents.NavigateToEvent(correctedEventId)))
    }

    private fun handleReplyAction(action: RoomDetailAction.EnterReplyMode) {
        saveCurrentDraft(action.text)

        room.getTimeLineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.REPLY(timelineEvent, action.text)) }
            viewState.value?.let { state ->
                // Save a new draft and keep the previously entered text, if it was not an edit
                timelineEvent.root.eventId?.let {
                    if (state.sendMode is SendMode.EDIT) {
                        room.saveDraft(UserDraft.REPLY(it, ""), NoOpMatrixCallback())
                    } else {
                        room.saveDraft(UserDraft.REPLY(it, action.text), NoOpMatrixCallback())
                    }
                }
            }
        }
    }

    private fun handleTombstoneEvent(action: RoomDetailAction.HandleTombstoneEvent) {
//        val tombstoneContent = action.event.getClearContent().toModel<RoomTombstoneContent>() ?: return
//
//        val roomId = tombstoneContent.replacementRoomId ?: ""
//        val isRoomJoined = session.getRoom(roomId)?.roomSummary()?.membership == Membership.JOIN
//        if (isRoomJoined) {
//            setState { copy(tombstoneEventHandling = Success(roomId)) }
//        } else {
//            val viaServers = MatrixPatterns.extractServerNameFromId(action.event.senderId)
//                ?.let { listOf(it) }
//                .orEmpty()
//            session.rx()
//                .joinRoom(roomId, viaServers = viaServers)
//                .map { roomId }
//                .subscribe {
//                    copy(tombstoneEventHandling = it)
//                }
//        }
    }

    private fun saveCurrentDraft(draft: String) {
        // Save the draft with the current text if any
        viewState.value?.let {
            if (draft.isNotBlank()) {
                when (it.sendMode) {
                    is SendMode.REGULAR -> room.saveDraft(
                        UserDraft.REGULAR(draft),
                        NoOpMatrixCallback()
                    )
                    is SendMode.REPLY -> room.saveDraft(
                        UserDraft.REPLY(
                            it.sendMode.timelineEvent.root.eventId!!,
                            draft
                        ), NoOpMatrixCallback()
                    )
                    is SendMode.QUOTE -> room.saveDraft(
                        UserDraft.QUOTE(
                            it.sendMode.timelineEvent.root.eventId!!,
                            draft
                        ), NoOpMatrixCallback()
                    )
                    is SendMode.EDIT -> room.saveDraft(
                        UserDraft.EDIT(
                            it.sendMode.timelineEvent.root.eventId!!,
                            draft
                        ), NoOpMatrixCallback()
                    )
                }
            }
        }
    }


    private fun handleExitSpecialMode(action: RoomDetailAction.EnterRegularMode) = setState {
        copy(sendMode = SendMode.REGULAR(action.text, action.fromSharing))
    }

    private fun handleEditAction(action: RoomDetailAction.EnterEditMode) {
        saveCurrentDraft(action.text)

        room.getTimeLineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.EDIT(timelineEvent, action.text)) }
            timelineEvent.root.eventId?.let {
                room.saveDraft(
                    UserDraft.EDIT(it, timelineEvent.getTextEditableContent() ?: ""),
                    NoOpMatrixCallback()
                )
            }
        }
    }

    private fun handleQuoteAction(action: RoomDetailAction.EnterQuoteMode) {
        saveCurrentDraft(action.text)

        room.getTimeLineEvent(action.eventId)?.let { timelineEvent ->
            setState { copy(sendMode = SendMode.QUOTE(timelineEvent, action.text)) }
            viewState.value?.let { state ->
                // Save a new draft and keep the previously entered text, if it was not an edit
                timelineEvent.root.eventId?.let {
                    if (state.sendMode is SendMode.EDIT) {
                        room.saveDraft(UserDraft.QUOTE(it, ""), NoOpMatrixCallback())
                    } else {
                        room.saveDraft(UserDraft.QUOTE(it, action.text), NoOpMatrixCallback())
                    }
                }
            }
        }
    }


    private fun getDraftIfAny() {
        val currentDraft = room.getDraft() ?: return
        setState {
            copy(
                // Create a sendMode from a draft and retrieve the TimelineEvent
                sendMode = when (currentDraft) {
                    is UserDraft.REGULAR -> SendMode.REGULAR(currentDraft.text, false)
                    is UserDraft.QUOTE -> {
                        room.getTimeLineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                            SendMode.QUOTE(timelineEvent, currentDraft.text)
                        }
                    }
                    is UserDraft.REPLY -> {
                        room.getTimeLineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                            SendMode.REPLY(timelineEvent, currentDraft.text)
                        }
                    }
                    is UserDraft.EDIT -> {
                        room.getTimeLineEvent(currentDraft.linkedEventId)?.let { timelineEvent ->
                            SendMode.EDIT(timelineEvent, currentDraft.text)
                        }
                    }
                } ?: SendMode.REGULAR("", fromSharing = false)
            )
        }
    }


    private fun observeSyncState() {
        session.rx()
            .liveSyncState()
            .subscribe { syncState ->
                setState {
                    copy(syncState = syncState)
                }
            }
            .disposeOnClear()
    }

    private fun getUnreadState() {
        Observable
            .combineLatest<List<TimelineEvent>, RoomSummary, UnreadState>(
                timelineEvents.observeOn(Schedulers.computation()),
                room.rx().liveRoomSummary().unwrap(),
                BiFunction { timelineEvents, roomSummary ->
                    computeUnreadState(timelineEvents, roomSummary)
                }
            )
            // We don't want live update of unread so we skip when we already had a HasUnread or HasNoUnread
            .distinctUntilChanged { previous, current ->
                when {
                    previous is UnreadState.Unknown || previous is UnreadState.ReadMarkerNotLoaded -> false
                    current is UnreadState.HasUnread || current is UnreadState.HasNoUnread -> true
                    else -> false
                }
            }
            .subscribe {
                setState { copy(unreadState = it) }
            }
            .disposeOnClear()
    }

    private fun computeUnreadState(
        events: List<TimelineEvent>,
        roomSummary: RoomSummary
    ): UnreadState {
        if (events.isEmpty()) return UnreadState.Unknown
        val readMarkerIdSnapshot = roomSummary.readMarkerId ?: return UnreadState.Unknown
        val firstDisplayableEventId = timeline.getFirstDisplayableEventId(readMarkerIdSnapshot)
        val firstDisplayableEventIndex = timeline.getIndexOfEvent(firstDisplayableEventId)
        if (firstDisplayableEventId == null || firstDisplayableEventIndex == null) {
            return if (timeline.isLive) {
                UnreadState.ReadMarkerNotLoaded(readMarkerIdSnapshot)
            } else {
                UnreadState.Unknown
            }
        }
        for (i in (firstDisplayableEventIndex - 1) downTo 0) {
            val timelineEvent = events.getOrNull(i) ?: return UnreadState.Unknown
            val eventId = timelineEvent.root.eventId ?: return UnreadState.Unknown
            val isFromMe = timelineEvent.root.senderId == session.myUserId
            if (!isFromMe) {
                return UnreadState.HasUnread(eventId)
            }
        }
        return UnreadState.HasNoUnread
    }


    private fun observeMembershipChanges() {
        session.rx()
            .liveRoomChangeMembershipState()
            .map {
                it[initialState.roomId] ?: ChangeMembershipState.Unknown
            }
            .distinctUntilChanged()
            .subscribe {
                setState { copy(changeMembershipState = it) }
            }
            .disposeOnClear()
    }

    private fun observeSummaryState() {
        Transformations.map(viewState) {
            it.asyncRoomSummary
        }.asObservable().filter {
            it is Success
        }.map {
            it.invoke()!!
        }.subscribe { summary ->
            roomSummaryHolder.set(summary)
            val typingMessage = typingHelper.getTypingMessage(summary.typingUsers)
            setState { copy(typingMessage = typingMessage) }
        }.disposeOnClear()
    }


    private fun observeRoomSummary() {
        room.rx().liveRoomSummary()
            .unwrap()
            .execute { async ->
                copy(
                    asyncRoomSummary = async
                )
            }
    }

    private fun observeRoomMemberSummary() {
        val roomMemberQueryParams = roomMemberQueryParams {
            displayName = QueryStringValue.IsNotEmpty
            memberships = Membership.activeMemberships()
        }

        room.rx().liveRoomMembers(roomMemberQueryParams)
            .subscribe { roomMembers ->
                setState {
                    copy(subtitle = stringProvider.getString(R.string.members, roomMembers.size))
                }
            }.disposeOnClear()
    }

    private fun observeUnreadState() {
        viewState.asObservable()
            .map { it.unreadState }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.v("Unread state: $it")
                if (it is UnreadState.HasNoUnread) {
                    startTrackingUnreadMessages()
                }
            }
            .disposeOnClear()
    }

    private fun startTrackingUnreadMessages() {
        trackUnreadMessages.set(true)
        setState { copy(canShowJumpToReadMarker = false) }
    }

    private fun stopTrackingUnreadMessages() {
        if (trackUnreadMessages.getAndSet(false)) {
            mostRecentDisplayedEvent?.root?.eventId?.also {
                room.setReadMarker(it, callback = NoOpMatrixCallback())
            }
            mostRecentDisplayedEvent = null
        }
        setState { copy(canShowJumpToReadMarker = true) }
    }


    private fun handleUserIsTyping(action: RoomDetailAction.UserIsTyping) {
        if (chatPreferences.sendTypingNotifs()) {
            if (action.isTyping) {
                room.userIsTyping()
            } else {
                room.userStopsTyping()
            }
        }
    }

    private fun handleOpenOrDownloadFile(action: RoomDetailAction.DownloadOrOpen) {
        val mxcUrl = action.messageFileContent.getFileUrl()
        val isDownloaded = mxcUrl?.let {
            session.fileService().isFileInCache(it, action.messageFileContent.mimeType)
        } ?: false
        if (isDownloaded) {
            // we can open it
            session.fileService()
                .getTemporarySharableURI(mxcUrl!!, action.messageFileContent.mimeType)?.let { uri ->
                _viewEvents.setValue(
                    (RoomDetailFragmentViewEvents.OpenFile(
                        action.messageFileContent.mimeType,
                        uri,
                        null
                    ))
                )
            }
        } else {
            session.fileService().downloadFile(
                downloadMode = FileService.DownloadMode.FOR_INTERNAL_USE,
                id = action.eventId,
                fileName = action.messageFileContent.getFileName(),
                mimeType = action.messageFileContent.mimeType,
                url = mxcUrl,
                elementToDecrypt = action.messageFileContent.encryptedFileInfo?.toElementToDecrypt(),
                callback = object : MatrixCallback<File> {
                    override fun onSuccess(data: File) {
                        _viewEvents.setValue(
                            (RoomDetailFragmentViewEvents.DownloadFileState(
                                action.messageFileContent.mimeType,
                                data,
                                null
                            ))
                        )
                    }

                    override fun onFailure(failure: Throwable) {
                        _viewEvents.setValue(
                            (RoomDetailFragmentViewEvents.DownloadFileState(
                                action.messageFileContent.mimeType,
                                null,
                                failure
                            ))
                        )
                    }
                })
        }
    }

    private fun handleSendMessage(action: RoomDetailAction.SendMessage) {
        viewState.value?.let { state ->
            when (state.sendMode) {
                is SendMode.REGULAR -> {
                    when (val slashCommandResult = CommandParser.parseSplashCommand(action.text)) {
                        is ParsedCommand.ErrorNotACommand -> {
                            // Send the text message to the room
                            room.sendTextMessage(action.text, autoMarkdown = action.autoMarkdown)
                            _viewEvents.setValue((RoomDetailFragmentViewEvents.MessageSent))
                            popDraft()
                        }
                        is ParsedCommand.ErrorSyntax -> {
                            _viewEvents.setValue(
                                (RoomDetailFragmentViewEvents.SlashCommandError(
                                    slashCommandResult.command
                                ))
                            )
                        }
                        is ParsedCommand.ErrorEmptySlashCommand -> {
                            _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandUnknown("/")))
                        }
                        is ParsedCommand.ErrorUnknownSlashCommand -> {
                            _viewEvents.setValue(
                                (RoomDetailFragmentViewEvents.SlashCommandUnknown(
                                    slashCommandResult.slashCommand
                                ))
                            )
                        }
                        is ParsedCommand.SendPlainText -> {
                            // Send the text message to the room, without markdown
                            room.sendTextMessage(slashCommandResult.message, autoMarkdown = false)
                            _viewEvents.setValue((RoomDetailFragmentViewEvents.MessageSent))
                            popDraft()
                        }
                        is ParsedCommand.Invite -> {
                            handleInviteSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.Invite3Pid -> {
                            handleInvite3pidSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.SetUserPowerLevel -> {
                            handleSetUserPowerLevel(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.SetMarkdown -> {
                            chatPreferences.setMarkdownEnabled(slashCommandResult.enable)
                            _viewEvents.setValue(
                                (RoomDetailFragmentViewEvents.SlashCommandHandled(
                                    if (slashCommandResult.enable) R.string.markdown_has_been_enabled else R.string.markdown_has_been_disabled
                                ))
                            )
                            popDraft()
                        }
                        is ParsedCommand.UnbanUser -> {
                            handleUnbanSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.BanUser -> {
                            handleBanSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.KickUser -> {
                            handleKickSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.JoinRoom -> {
                            handleJoinToAnotherRoomSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.ChangeTopic -> {
                            handleChangeTopicSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.ChangeDisplayName -> {
                            handleChangeDisplayNameSlashCommand(slashCommandResult)
                            popDraft()
                        }
                        is ParsedCommand.DiscardSession -> {
                            if (room.isEncrypted()) {
                                session.cryptoService().discardOutboundSession(room.roomId)
                                _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandHandled()))
                                popDraft()
                            } else {
                                _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandHandled()))
                                _viewEvents.setValue(
                                    (
                                            RoomDetailFragmentViewEvents
                                                .ShowMessage(stringProvider.getString(R.string.command_description_discard_session_not_handled))
                                            )
                                )
                            }
                        }
                    }
                }
                is SendMode.EDIT -> {
                    // is original event a reply?
                    val inReplyTo = state.sendMode.timelineEvent.root.getClearContent()
                        .toModel<MessageContent>()?.relatesTo?.inReplyTo?.eventId
                        ?: state.sendMode.timelineEvent.root.content.toModel<EncryptedEventContent>()?.relatesTo?.inReplyTo?.eventId
                    if (inReplyTo != null) {
                        // TODO check if same content?
                        room.getTimeLineEvent(inReplyTo)?.let {
                            room.editReply(state.sendMode.timelineEvent, it, action.text.toString())
                        }
                    } else {
                        val messageContent: MessageContent? =
                            state.sendMode.timelineEvent.annotations?.editSummary?.aggregatedContent.toModel()
                                ?: state.sendMode.timelineEvent.root.getClearContent().toModel()
                        val existingBody = messageContent?.body ?: ""
                        if (existingBody != action.text) {
                            room.editTextMessage(
                                state.sendMode.timelineEvent.root.eventId ?: "",
                                messageContent?.msgType ?: MessageType.MSGTYPE_TEXT,
                                action.text,
                                action.autoMarkdown
                            )
                        } else {
                            Timber.w("Same message content, do not send edition")
                        }
                    }
                    _viewEvents.setValue((RoomDetailFragmentViewEvents.MessageSent))
                    popDraft()
                }
                is SendMode.QUOTE -> {
                    val messageContent: MessageContent? =
                        state.sendMode.timelineEvent.annotations?.editSummary?.aggregatedContent.toModel()
                            ?: state.sendMode.timelineEvent.root.getClearContent().toModel()
                    val textMsg = messageContent?.body

                    val finalText = legacyRiotQuoteText(textMsg, action.text.toString())

                    // TODO check for pills?

                    // TODO Refactor this, just temporary for quotes
                    val parser = Parser.builder().build()
                    val document = parser.parse(finalText)
                    val renderer = HtmlRenderer.builder().build()
                    val htmlText = renderer.render(document)
                    if (finalText == htmlText) {
                        room.sendTextMessage(finalText)
                    } else {
                        room.sendFormattedTextMessage(finalText, htmlText)
                    }
                    _viewEvents.setValue(RoomDetailFragmentViewEvents.MessageSent)
                    popDraft()
                }
                is SendMode.REPLY -> {
                    state.sendMode.timelineEvent.let {
                        room.replyToMessage(it, action.text.toString(), action.autoMarkdown)
                        _viewEvents.setValue((RoomDetailFragmentViewEvents.MessageSent))
                        popDraft()
                    }
                }
            }
        }
    }

    private fun legacyRiotQuoteText(quotedText: String?, myText: String): String {
        val messageParagraphs =
            quotedText?.split("\n\n".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        return buildString {
            if (messageParagraphs != null) {
                for (i in messageParagraphs.indices) {
                    if (messageParagraphs[i].isNotBlank()) {
                        append("> ")
                        append(messageParagraphs[i])
                    }

                    if (i != messageParagraphs.lastIndex) {
                        append("\n\n")
                    }
                }
            }
            append("\n\n")
            append(myText)
        }
    }

    private fun popDraft() {
        viewState.value?.let {
            if (it.sendMode is SendMode.REGULAR && it.sendMode.fromSharing) {
                // If we were sharing, we want to get back our last value from draft
                getDraftIfAny()
            } else {
                // Otherwise we clear the composer and remove the draft from db
                setState { copy(sendMode = SendMode.REGULAR("", false)) }
                room.deleteDraft(NoOpMatrixCallback())
            }
        }
    }

    private fun handleLoadMore(action: RoomDetailAction.LoadMoreTimelineEvents) {
        timeline.paginate(action.direction, PAGINATION_COUNT)
    }

    private fun observePowerLevel() {
        PowerLevelsObservableFactory(room).createObservable()
            .subscribe {
                val canSendMessage = PowerLevelsHelper(it).isUserAllowedToSend(
                    session.myUserId,
                    false,
                    EventType.MESSAGE
                )
                setState {
                    copy(canSendMessage = canSendMessage)
                }
            }
            .disposeOnClear()
    }

    private fun handleTapOnFailedToDecrypt(action: RoomDetailAction.TapOnFailedToDecrypt) {
        room.getTimeLineEvent(action.eventId)?.let {
            val code = when (it.root.mCryptoError) {
                MXCryptoError.ErrorType.KEYS_WITHHELD -> {
                    WithHeldCode.fromCode(it.root.mCryptoErrorReason)
                }
                else -> null
            }

            _viewEvents.setValue((RoomDetailFragmentViewEvents.ShowE2EErrorMessage(code)))
        }
    }


    private fun observeMyRoomMember() {
        val queryParams = roomMemberQueryParams {
            this.userId = QueryStringValue.Equals(session.myUserId, QueryStringValue.Case.SENSITIVE)
        }
        room.rx()
            .liveRoomMembers(queryParams)
            .map {
                it.firstOrNull().toOptional()
            }
            .unwrap()
            .execute {
                copy(myRoomMember = it)
            }
    }

    fun getMember(userId: String): RoomMemberSummary? {
        return room.getRoomMember(userId)
    }

    override fun onNewTimelineEvents(eventIds: List<String>) {
        Timber.v("On new timeline events: $eventIds")
        _viewEvents.postValue(RoomDetailFragmentViewEvents.OnNewTimelineEvents(eventIds))
    }

    override fun onTimelineFailure(throwable: Throwable) {
        // If we have a critical timeline issue, we get back to live.
        timeline.restartWithEventId(null)
        _viewEvents.postValue(RoomDetailFragmentViewEvents.Failure(throwable))
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        timelineEvents.accept(snapshot)
    }

    private fun launchSlashCommandFlow(lambda: (MatrixCallback<Unit>) -> Unit) {
        _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandHandled()))
        val matrixCallback = object : MatrixCallback<Unit> {
            override fun onSuccess(data: Unit) {
                _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandResultOk))
            }

            override fun onFailure(failure: Throwable) {
                _viewEvents.setValue((RoomDetailFragmentViewEvents.SlashCommandResultError(failure)))
            }
        }
        lambda.invoke(matrixCallback)
    }

    private fun handleInviteSlashCommand(invite: ParsedCommand.Invite) {
        launchSlashCommandFlow {
            room.invite(invite.userId, invite.reason, it)
        }
    }

    private fun handleInvite3pidSlashCommand(invite: ParsedCommand.Invite3Pid) {
        launchSlashCommandFlow {
            room.invite3pid(invite.threePid, it)
        }
    }

    private fun handleSetUserPowerLevel(setUserPowerLevel: ParsedCommand.SetUserPowerLevel) {
        val currentPowerLevelsContent = room.getStateEvent(EventType.STATE_ROOM_POWER_LEVELS)
            ?.content
            ?.toModel<PowerLevelsContent>() ?: return

        launchSlashCommandFlow {
            currentPowerLevelsContent.setUserPowerLevel(
                setUserPowerLevel.userId,
                setUserPowerLevel.powerLevel
            )
            room.sendStateEvent(
                EventType.STATE_ROOM_POWER_LEVELS,
                null,
                currentPowerLevelsContent.toContent(),
                it
            )
        }
    }

    private fun handleEventVisible(action: RoomDetailAction.TimelineEventTurnsVisible) {
        if (action.event.root.sendState.isSent()) { // ignore pending/local events
            visibleEventsObservable.accept(action)
        }
        // We need to update this with the related m.replace also (to move read receipt)
        action.event.annotations?.editSummary?.sourceEvents?.forEach {
            room.getTimeLineEvent(it)?.let { event ->
                visibleEventsObservable.accept(RoomDetailAction.TimelineEventTurnsVisible(event))
            }
        }
    }

    private fun handleReplyToOptions(action: RoomDetailAction.ReplyToOptions) {
        if (action.optionValue.isValidUrl()) {
            _viewEvents.setValue(RoomDetailFragmentViewEvents.OpenDeepLink(action.optionValue))
        } else {
            handleSendMessage(RoomDetailAction.SendMessage(action.optionValue, false))
        }
    }

    private fun handleEventInvisible(action: RoomDetailAction.TimelineEventTurnsInvisible) {
        invisibleEventsObservable.accept(action)
    }

    private fun handleRejectInvite() {
        room.leave(null, NoOpMatrixCallback())
    }

    private fun handleRedactEvent(action: RoomDetailAction.RedactAction) {
        val event = room.getTimeLineEvent(action.targetEventId) ?: return
        room.redactEvent(event.root, action.reason)
    }

    private fun handleUndoReact(action: RoomDetailAction.UndoReaction) {
        room.undoReaction(action.targetEventId, action.reaction)
    }

    private fun handleResendEvent(action: RoomDetailAction.ResendMessage) {
        val targetEventId = action.eventId
        room.getTimeLineEvent(targetEventId)?.let {
            // State must be UNDELIVERED or Failed
            if (!it.root.sendState.hasFailed()) {
                Timber.e("Cannot resend message, it is not failed, Cancel first")
                return
            }
            when {
                it.root.isTextMessage() -> room.resendTextMessage(it)
                it.root.isImageMessage() -> room.resendMediaMessage(it)
                else -> {
                    // TODO
                }
            }
        }
    }

    private fun handleRemove(action: RoomDetailAction.RemoveFailedEcho) {
        val targetEventId = action.eventId
        room.getTimeLineEvent(targetEventId)?.let {
            // State must be UNDELIVERED or Failed
            if (!it.root.sendState.hasFailed()) {
                Timber.e("Cannot resend message, it is not failed, Cancel first")
                return
            }
            room.deleteFailedEcho(it)
        }
    }

    private fun handleReportContent(action: RoomDetailAction.ReportContent) {
        room.reportContent(action.eventId, -100, action.reason, object : MatrixCallback<Unit> {
            override fun onSuccess(data: Unit) {
                _viewEvents.setValue(RoomDetailFragmentViewEvents.ActionSuccess(action))
            }

            override fun onFailure(failure: Throwable) {
                _viewEvents.setValue(RoomDetailFragmentViewEvents.ActionFailure(action, failure))
            }
        })
    }

    private fun handleSendReaction(action: RoomDetailAction.SendReaction) {
        room.sendReaction(action.targetEventId, action.reaction)
    }

    private fun handleIgnoreUser(action: RoomDetailAction.IgnoreUser) {
        if (action.userId.isNullOrEmpty()) {
            return
        }

        session.ignoreUserIds(listOf(action.userId), object : MatrixCallback<Unit> {
            override fun onSuccess(data: Unit) {
                _viewEvents.setValue(RoomDetailFragmentViewEvents.ActionSuccess(action))
            }

            override fun onFailure(failure: Throwable) {
                _viewEvents.setValue(RoomDetailFragmentViewEvents.ActionFailure(action, failure))
            }
        })
    }

    private fun handleUpdateQuickReaction(action: RoomDetailAction.UpdateQuickReactAction) {
        if (action.add) {
            room.sendReaction(action.targetEventId, action.selectedReaction)
        } else {
            room.undoReaction(action.targetEventId, action.selectedReaction)
        }
    }

    private fun handleAcceptInvite() {
        room.join(callback = NoOpMatrixCallback())
    }

    private fun handleChangeDisplayNameSlashCommand(changeDisplayName: ParsedCommand.ChangeDisplayName) {
        launchSlashCommandFlow {
            session.setDisplayName(session.myUserId, changeDisplayName.displayName, it)
        }
    }

    private fun handleKickSlashCommand(kick: ParsedCommand.KickUser) {
        launchSlashCommandFlow {
            room.kick(kick.userId, kick.reason, it)
        }
    }

    private fun handleBanSlashCommand(ban: ParsedCommand.BanUser) {
        launchSlashCommandFlow {
            room.ban(ban.userId, ban.reason, it)
        }
    }

    private fun handleUnbanSlashCommand(unban: ParsedCommand.UnbanUser) {
        launchSlashCommandFlow {
            room.unban(unban.userId, unban.reason, it)
        }
    }

    private fun handleChangeTopicSlashCommand(changeTopic: ParsedCommand.ChangeTopic) {
        launchSlashCommandFlow {
            room.updateTopic(changeTopic.topic, it)
        }
    }

    private fun handleJoinToAnotherRoomSlashCommand(command: ParsedCommand.JoinRoom) {
        session.joinRoom(
            command.roomAlias,
            command.reason,
            emptyList(),
            object : MatrixCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    session.getRoomSummary(command.roomAlias)
                        ?.roomId
                        ?.let {
                            _viewEvents.setValue(
                                (RoomDetailFragmentViewEvents.JoinRoomCommandSuccess(
                                    it
                                ))
                            )
                        }
                }

                override fun onFailure(failure: Throwable) {
                    _viewEvents.setValue(
                        (RoomDetailFragmentViewEvents.SlashCommandResultError(
                            failure
                        ))
                    )
                }
            })
    }

    /**
     * Convert a send mode to a draft and save the draft
     */
    private fun handleSaveDraft(action: RoomDetailAction.SaveDraft) {
        viewState.value?.let {
            when {
                it.sendMode is SendMode.REGULAR && !it.sendMode.fromSharing -> {
                    setState { copy(sendMode = it.sendMode.copy(action.draft)) }
                    room.saveDraft(UserDraft.REGULAR(action.draft), NoOpMatrixCallback())
                }
                it.sendMode is SendMode.REPLY -> {
                    setState { copy(sendMode = it.sendMode.copy(text = action.draft)) }
                    room.saveDraft(
                        UserDraft.REPLY(
                            it.sendMode.timelineEvent.root.eventId!!,
                            action.draft
                        ), NoOpMatrixCallback()
                    )
                }
                it.sendMode is SendMode.QUOTE -> {
                    setState { copy(sendMode = it.sendMode.copy(text = action.draft)) }
                    room.saveDraft(
                        UserDraft.QUOTE(
                            it.sendMode.timelineEvent.root.eventId!!,
                            action.draft
                        ), NoOpMatrixCallback()
                    )
                }
                it.sendMode is SendMode.EDIT -> {
                    setState { copy(sendMode = it.sendMode.copy(text = action.draft)) }
                    room.saveDraft(
                        UserDraft.EDIT(
                            it.sendMode.timelineEvent.root.eventId!!,
                            action.draft
                        ), NoOpMatrixCallback()
                    )
                }
                else -> return
            }
        }
    }

    private fun handleMarkAllAsRead() {
        room.markAsRead(ReadService.MarkAsReadParams.BOTH, NoOpMatrixCallback())
    }

    private fun handleResumeRequestVerification(action: RoomDetailAction.ResumeVerification) {
        // Check if this request is still active and handled by me
        session.cryptoService().verificationService()
            .getExistingVerificationRequestInRoom(room.roomId, action.transactionId)?.let {
            if (it.handledByOtherSession) return
            if (!it.isFinished) {
                _viewEvents.setValue(
                    RoomDetailFragmentViewEvents.ActionSuccess(
                        action.copy(
                            otherUserId = it.otherUserId
                        )
                    )
                )
            }
        }
    }

    companion object {
        const val PAGINATION_COUNT = 50
    }
}