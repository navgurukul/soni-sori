package org.navgurukul.chat.features.home.room.detail

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.TextUtils
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import im.vector.matrix.android.api.session.events.model.Event
import im.vector.matrix.android.api.session.room.model.Membership
import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.matrix.android.api.session.room.model.message.*
import im.vector.matrix.android.api.session.room.send.SendState
import im.vector.matrix.android.api.session.room.timeline.Timeline
import im.vector.matrix.android.api.session.room.timeline.TimelineEvent
import im.vector.matrix.android.api.session.room.timeline.getLastMessageContent
import im.vector.matrix.android.api.util.MatrixItem
import im.vector.matrix.android.api.util.toMatrixItem
import im.vector.matrix.android.internal.crypto.model.event.EncryptedEventContent
import im.vector.matrix.android.internal.crypto.model.event.WithHeldCode
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_room_detail.*
import kotlinx.android.synthetic.main.merge_composer_layout.view.*
import org.commonmark.parser.Parser
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.*
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.utils.*
import org.navgurukul.chat.core.utils.createUIHandler
import org.navgurukul.chat.core.views.NotificationAreaView
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.composer.TextComposerView
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.item.*
import org.navgurukul.chat.features.html.EventHtmlRenderer
import org.navgurukul.chat.features.html.PillImageSpan
import org.navgurukul.chat.features.invite.SaralInviteView
import org.navgurukul.chat.features.media.ImageContentRenderer
import org.navgurukul.chat.features.media.VideoContentRenderer
import org.navgurukul.chat.features.navigator.ChatInternalNavigator
import org.navgurukul.chat.features.notifications.NotificationDrawerManager
import org.navgurukul.chat.features.settings.ChatPreferences
import org.navgurukul.chat.features.share.SharedData
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.views.JumpToReadMarkerView
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Parcelize
data class RoomDetailArgs(
    val roomId: String,
    val eventId: String? = null,
    val sharedData: SharedData? = null
) : Parcelable

class RoomDetailFragment : BaseFragment(),
    TimelineEventController.Callback,
    SaralInviteView.Callback,
    JumpToReadMarkerView.Callback {

    private lateinit var layoutManager: LinearLayoutManager
    private val roomDetailArgs: RoomDetailArgs by args()

    private val timelineEventController: TimelineEventController by inject(parameters = { parametersOf(lifecycleScope)})

    private val viewModel: RoomDetailFragmentViewModel by viewModel(parameters = {
        parametersOf(
            RoomDetailViewState(roomId = roomDetailArgs.roomId, eventId = roomDetailArgs.eventId),
            lifecycleScope
        )
    })

    private val glideRequests by lazy {
        GlideApp.with(this)
    }

    private val navigator: MerakiNavigator by inject()
    private val chatInternalNavigator: ChatInternalNavigator by inject()

    private val notificationDrawerManager: NotificationDrawerManager by inject()

    private var lockSendButton = false

    private val avatarRenderer: AvatarRenderer by inject()

    private val sessionHolder: ActiveSessionHolder by inject()
    private val session = sessionHolder.getActiveSession()

    private val debouncer = Debouncer(createUIHandler())

    private val chatPreferences: ChatPreferences by inject()

    private val eventHtmlRenderer: EventHtmlRenderer by inject()

    private lateinit var scrollOnNewMessageCallback: ScrollOnNewMessageCallback
    private lateinit var scrollOnHighlightedEventCallback: ScrollOnHighlightedEventCallback

    private lateinit var jumpToBottomViewVisibilityManager: JumpToBottomViewVisibilityManager

    private var modelBuildListener: OnModelBuildFinishedListener? = null

    override fun getLayoutResId(): Int = R.layout.fragment_room_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(roomToolbar)
        setupRecyclerView()
        setupComposer()
        setupInviteView()
        setupNotificationView()
        setupJumpToReadMarkerView()
        setupJumpToBottomView()

        viewModel.selectSubscribe(RoomDetailViewState::sendMode, RoomDetailViewState::canSendMessage).observe(viewLifecycleOwner, Observer { (mode, canSend) ->
            if (!canSend) {
                return@Observer
            }
            when (mode) {
                is SendMode.REGULAR -> renderRegularMode(mode.text)
                is SendMode.EDIT    -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_edit, R.string.edit, mode.text)
                is SendMode.QUOTE   -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_quote, R.string.quote, mode.text)
                is SendMode.REPLY   -> renderSpecialMode(mode.timelineEvent, R.drawable.ic_reply, R.string.reply, mode.text)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            invalidateState(it)
        })

        viewModel.selectSubscribe(RoomDetailViewState::syncState).observe(viewLifecycleOwner, Observer { syncState ->
            syncStateView.render(syncState)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RoomDetailFragmentViewEvents.Failure                          -> showErrorInSnackbar(it.throwable)
                is RoomDetailFragmentViewEvents.OnNewTimelineEvents              -> scrollOnNewMessageCallback.addNewTimelineEventIds(it.eventIds)
                is RoomDetailFragmentViewEvents.ActionSuccess                    -> displayRoomDetailActionSuccess(it)
//                is RoomDetailFragmentViewEvents.ActionFailure                    -> displayRoomDetailActionFailure(it)
                is RoomDetailFragmentViewEvents.ShowMessage                      -> showSnackWithMessage(it.message, Snackbar.LENGTH_LONG)
                is RoomDetailFragmentViewEvents.NavigateToEvent                  -> navigateToEvent(it)
                is RoomDetailFragmentViewEvents.OpenDeepLink                     -> openDeepLink(it)
//                is RoomDetailFragmentViewEvents.FileTooBigError                  -> displayFileTooBigError(it)
//                is RoomDetailFragmentViewEvents.DownloadFileState                -> handleDownloadFileState(it)
                is RoomDetailFragmentViewEvents.JoinRoomCommandSuccess           -> handleJoinedToAnotherRoom(it)
                is RoomDetailFragmentViewEvents.SendMessageResult                -> renderSendMessageResult(it)
                is RoomDetailFragmentViewEvents.ShowE2EErrorMessage              -> displayE2eError(it.withHeldCode)
//                is RoomDetailFragmentViewEvents.DisplayPromptForIntegrationManager  -> displayPromptForIntegrationManager()
//                is RoomDetailFragmentViewEvents.OpenStickerPicker                -> openStickerPicker(it)
//                is RoomDetailFragmentViewEvents.DisplayEnableIntegrationsWarning -> displayDisabledIntegrationDialog()
//                is RoomDetailFragmentViewEvents.OpenIntegrationManager           -> openIntegrationManager()
//                is RoomDetailFragmentViewEvents.OpenFile                         -> startOpenFileIntent(it)
            }
        })
    }

    private fun openDeepLink(event: RoomDetailFragmentViewEvents.OpenDeepLink) {
        navigator.openDeepLink(requireContext(), event.deepLink)
    }

    private fun invalidateState(state: RoomDetailViewState) {
        val summary = state.asyncRoomSummary()
        val subtitle = if (TextUtils.isEmpty(state.typingMessage)) state.subtitle else state.typingMessage
        renderToolbar(summary, subtitle)
        val inviter = state.asyncInviter()
        if (summary?.membership == Membership.JOIN) {
//            roomWidgetsBannerView.render(state.activeRoomWidgets())
            jumpToBottomView.count = summary.notificationCount
            jumpToBottomView.drawBadge = summary.hasUnreadMessages
            scrollOnHighlightedEventCallback.timeline = viewModel.timeline
            timelineEventController.update(state)
            inviteView.visibility = View.GONE
            val meMember = state.myRoomMember()
            val userId = session.myUserId
            avatarRenderer.render(MatrixItem.UserItem(userId, meMember?.displayName, meMember?.avatarUrl), composerLayout.composerAvatarImageView)
            if (state.tombstoneEvent == null) {
                if (state.canSendMessage) {
                    composerLayout.visibility = View.VISIBLE
//                    composerLayout.setRoomEncrypted(summary.isEncrypted, summary.roomEncryptionTrustLevel)
                    notificationAreaView.render(NotificationAreaView.State.Hidden)
                } else {
                    composerLayout.visibility = View.GONE
                    notificationAreaView.render(NotificationAreaView.State.NoPermissionToPost)
                }
            } else {
                composerLayout.visibility = View.GONE
                notificationAreaView.render(NotificationAreaView.State.Tombstone(state.tombstoneEvent))
            }
        } else if (summary?.membership == Membership.INVITE && inviter != null) {
            inviteView.visibility = View.VISIBLE
            inviteView.render(inviter, SaralInviteView.Mode.LARGE, state.changeMembershipState)
            // Intercept click event
            inviteView.setOnClickListener { }
        } else if (state.asyncInviter.complete) {
            activity?.finish()
        }
    }

    private fun renderRegularMode(text: String) {
//        autoCompleter.exitSpecialMode()
        composerLayout.collapse()

        updateComposerText(text)
        composerLayout.sendButton.contentDescription = getString(R.string.send)
    }

    private fun renderSpecialMode(event: TimelineEvent,
                                  @DrawableRes iconRes: Int,
                                  @StringRes descriptionRes: Int,
                                  defaultContent: String) {
//        autoCompleter.enterSpecialMode()
        // switch to expanded bar
        composerLayout.composerRelatedMessageTitle.apply {
            text = event.senderInfo.disambiguatedDisplayName
            setTextColor(ContextCompat.getColor(requireContext(), getColorFromUserId(event.root.senderId)))
        }

        val messageContent: MessageContent? = event.getLastMessageContent()
        val nonFormattedBody = messageContent?.body ?: ""
        var formattedBody: CharSequence? = null
        if (messageContent is MessageTextContent && messageContent.format == MessageFormat.FORMAT_MATRIX_HTML) {
            val parser = Parser.builder().build()
            val document = parser.parse(messageContent.formattedBody ?: messageContent.body)
            formattedBody = eventHtmlRenderer.render(document)
        }
        composerLayout.composerRelatedMessageContent.text = (formattedBody ?: nonFormattedBody)

        updateComposerText(defaultContent)

        composerLayout.composerRelatedMessageActionIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
        composerLayout.sendButton.contentDescription = getString(descriptionRes)

        avatarRenderer.render(event.senderInfo.toMatrixItem(), composerLayout.composerRelatedMessageAvatar)

        composerLayout.expand {
            if (isAdded) {
                // need to do it here also when not using quick reply
                focusComposerAndShowKeyboard()
            }
        }
        focusComposerAndShowKeyboard()
    }


    private fun setupNotificationView() {
        notificationAreaView.delegate = object : NotificationAreaView.Delegate {
            override fun onTombstoneEventClicked(tombstoneEvent: Event) {
                viewModel.handle(RoomDetailAction.HandleTombstoneEvent(tombstoneEvent))
            }
        }
    }

    private fun setupInviteView() {
        inviteView.callback = this
    }

    private fun setupJumpToReadMarkerView() {
        jumpToReadMarkerView.callback = this
    }

    private fun setupJumpToBottomView() {
        jumpToBottomView.visibility = View.INVISIBLE
        jumpToBottomView.debouncedClicks {
            viewModel.handle(RoomDetailAction.ExitTrackingUnreadMessagesState)
            jumpToBottomView.visibility = View.INVISIBLE
            if (!viewModel.timeline.isLive) {
                scrollOnNewMessageCallback.forceScrollOnNextUpdate()
                viewModel.timeline.restartWithEventId(null)
            } else {
                layoutManager.scrollToPosition(0)
            }
        }

        jumpToBottomViewVisibilityManager = JumpToBottomViewVisibilityManager(
            jumpToBottomView,
            debouncer,
            recyclerView,
            layoutManager
        )
    }

    private fun setupComposer() {
//        autoCompleter.setup(composerLayout.composerEditText)

        observerUserTyping()

        composerLayout.callback = object : TextComposerView.Callback {
            override fun onAddAttachment() {
//                if (!::attachmentTypeSelector.isInitialized) {
//                    attachmentTypeSelector = AttachmentTypeSelectorView(vectorBaseActivity, vectorBaseActivity.layoutInflater, this@RoomDetailFragment)
//                }
//                attachmentTypeSelector.show(composerLayout.attachmentButton, keyboardStateUtils.isKeyboardShowing)
            }

            override fun onSendMessage(text: CharSequence) {
                if (lockSendButton) {
                    Timber.w("Send button is locked")
                    return
                }
                if (text.isNotBlank()) {
                    // We collapse ASAP, if not there will be a slight anoying delay
                    composerLayout.collapse(true)
                    lockSendButton = true
                    viewModel.handle(RoomDetailAction.SendMessage(text, chatPreferences.isMarkdownEnabled()))
                }
            }

            override fun onCloseRelatedMessage() {
                viewModel.handle(RoomDetailAction.ExitSpecialMode(composerLayout.text.toString()))
            }

            override fun onRichContentSelected(contentUri: Uri): Boolean {
                // We need WRITE_EXTERNAL permission
//                return if (checkPermissions(PERMISSIONS_FOR_WRITING_FILES, this@RoomDetailFragment, PERMISSION_REQUEST_CODE_INCOMING_URI)) {
//                    sendUri(contentUri)
//                } else {
//                    viewModel.pendingUri = contentUri
//                    // Always intercept when we request some permission
//                    true
//                }

                return true
            }
        }
    }

    private fun observerUserTyping() {
        composerLayout.composerEditText.textChanges()
            .skipInitialValue()
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.isNotEmpty() }
            .subscribe {
                Timber.d("Typing: User is typing: $it")
                viewModel.handle(RoomDetailAction.UserIsTyping(it))
            }
            .disposeOnDestroyView()
    }



    private fun setupRecyclerView() {
        timelineEventController.callback = this
        timelineEventController.timeline = viewModel.timeline

        recyclerView.trackItemsVisibilityChange()
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)

//        val stateRestorer = LayoutManagerStateRestorer(layoutManager).register()
        scrollOnNewMessageCallback = ScrollOnNewMessageCallback(layoutManager, timelineEventController)
        scrollOnHighlightedEventCallback = ScrollOnHighlightedEventCallback(recyclerView, layoutManager, timelineEventController)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = null
        recyclerView.setHasFixedSize(true)
        modelBuildListener = OnModelBuildFinishedListener {
//            it.dispatchTo(stateRestorer)
            it.dispatchTo(scrollOnNewMessageCallback)
            it.dispatchTo(scrollOnHighlightedEventCallback)
            updateJumpToReadMarkerViewVisibility()
            jumpToBottomViewVisibilityManager.maybeShowJumpToBottomViewVisibilityWithDelay()
        }
        timelineEventController.addModelBuildListener(modelBuildListener)
        recyclerView.adapter = timelineEventController.adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(recyclerView.context.resources.getDimensionPixelSize(R.dimen.spacing_2x), 0))

        if (chatPreferences.swipeToReplyIsEnabled()) {
            val quickReplyHandler = object : RoomMessageTouchHelperCallback.QuickReplayHandler {
                override fun performQuickReplyOnHolder(model: EpoxyModel<*>) {
                    (model as? AbsMessageItem)?.attributes?.informationData?.let {
                        val eventId = it.eventId
                        viewModel.handle(RoomDetailAction.EnterReplyMode(eventId, composerLayout.composerEditText.text.toString()))
                    }
                }

                override fun canSwipeModel(model: EpoxyModel<*>): Boolean {
                    val canSendMessage = withState(viewModel) {
                        it.canSendMessage
                    }
                    if (!canSendMessage) {
                        return false
                    }
                    return when (model) {
                        is MessageFileItem,
                        is MessageImageVideoItem,
                        is MessageTextItem -> {
                            return (model as AbsMessageItem).attributes.informationData.sendState == SendState.SYNCED
                        }
                        else               -> false
                    }
                }
            }
            val swipeCallback = RoomMessageTouchHelperCallback(requireContext(), R.drawable.ic_reply, quickReplyHandler)
            val touchHelper = ItemTouchHelper(swipeCallback)
            touchHelper.attachToRecyclerView(recyclerView)
        }
    }

    private fun renderToolbar(roomSummary: RoomSummary?, typingMessage: String?) {
        roomToolbarBackButton.setOnClickListener {
            activity?.finish()
        }

        roomToolbarAvatarImageView.setOnClickListener {
            activity?.finish()
        }

        if (roomSummary == null) {
            roomToolbarContentView.isClickable = false
        } else {
            roomToolbarContentView.isClickable = roomSummary.membership == Membership.JOIN
            roomToolbarTitleView.text = roomSummary.displayName
            avatarRenderer.render(roomSummary.toMatrixItem(), roomToolbarAvatarImageView)

            renderSubTitle(typingMessage, roomSummary.topic)
        }
    }

    private fun renderSubTitle(typingMessage: String?, topic: String) {
        // TODO Temporary place to put typing data
        val subtitle = typingMessage?.takeIf { it.isNotBlank() } ?: topic
        roomToolbarSubtitleView.apply {
            setTextOrHide(subtitle)
        }
    }

    override fun onLoadMore(direction: Timeline.Direction) {
        viewModel.handle(RoomDetailAction.LoadMoreTimelineEvents(direction))
    }

    override fun onEventInvisible(event: TimelineEvent) {
        viewModel.handle(RoomDetailAction.TimelineEventTurnsInvisible(event))
    }

    override fun onEventVisible(event: TimelineEvent) {
        viewModel.handle(RoomDetailAction.TimelineEventTurnsVisible(event))
    }

    override fun onRoomCreateLinkClicked(url: String) {
    }


    override fun onEditedDecorationClicked(informationData: MessageInformationData) {
    }

    override fun onTimelineItemAction(itemAction: RoomDetailAction) {
        viewModel.handle(itemAction)
    }

    override fun onEventCellClicked(
        informationData: MessageInformationData,
        messageContent: Any?,
        view: View
    ) {
        when (messageContent) {
            is MessageVerificationRequestContent -> {
                viewModel.handle(RoomDetailAction.ResumeVerification(informationData.eventId, null))
            }
            is MessageWithAttachmentContent -> {
                val action = RoomDetailAction.DownloadOrOpen(informationData.eventId, messageContent)
                viewModel.handle(action)
            }
            is EncryptedEventContent -> {
                viewModel.handle(RoomDetailAction.TapOnFailedToDecrypt(informationData.eventId))
            }
        }
    }

    override fun onEventLongClicked(
        informationData: MessageInformationData,
        messageContent: Any?,
        view: View
    ): Boolean {
        return true
    }

    override fun onClickOnReactionPill(
        informationData: MessageInformationData,
        reaction: String,
        on: Boolean
    ) {
    }

    override fun onLongClickOnReactionPill(
        informationData: MessageInformationData,
        reaction: String
    ) {
    }

    override fun onAvatarClicked(informationData: MessageInformationData) {
        openRoomMemberProfile(informationData.senderId)
    }

    private fun openRoomMemberProfile(userId: String) {
//        navigator.openRoomMemberProfile(userId = userId, roomId = roomDetailArgs.roomId, context = requireActivity())
    }

    override fun onMemberNameClicked(informationData: MessageInformationData) {
        insertUserDisplayNameInTextEditor(informationData.senderId)
    }

    override fun onUrlClicked(url: String, title: String): Boolean {
        context?.let { openUrlInExternalBrowser(it, url) }
        return true
    }

    override fun onUrlLongClicked(url: String): Boolean {
        if (url != getString(R.string.edited_suffix) && url.isValidUrl()) {
            // Copy the url to the clipboard
            copyToClipboard(requireContext(), url, true, R.string.link_copied_to_clipboard)
        }
        return true
    }

    override fun onReadReceiptsClicked(readReceipts: List<ReadReceiptData>) {
    }

    override fun onReadMarkerVisible() {
    }

    override fun onImageMessageClicked(
        messageImageContent: MessageImageInfoContent,
        mediaData: ImageContentRenderer.Data,
        view: View
    ) {
        chatInternalNavigator.openMediaViewer(
            activity = requireActivity(),
            roomId = roomDetailArgs.roomId,
            mediaData = mediaData,
            view = view
        ) { pairs ->
            pairs.add(Pair(roomToolbar, ViewCompat.getTransitionName(roomToolbar) ?: ""))
            pairs.add(Pair(composerLayout, ViewCompat.getTransitionName(composerLayout) ?: ""))
        }
    }

    override fun onVideoMessageClicked(
        messageVideoContent: MessageVideoContent,
        mediaData: VideoContentRenderer.Data,
        view: View
    ) {
        chatInternalNavigator.openMediaViewer(
            activity = requireActivity(),
            roomId = roomDetailArgs.roomId,
            mediaData = mediaData,
            view = view
        ) { pairs ->
            pairs.add(Pair(roomToolbar, ViewCompat.getTransitionName(roomToolbar) ?: ""))
            pairs.add(Pair(composerLayout, ViewCompat.getTransitionName(composerLayout) ?: ""))
        }
    }

    private fun updateJumpToReadMarkerViewVisibility() {
        jumpToReadMarkerView?.post {
            withState(viewModel) {
                val showJumpToUnreadBanner = when (it.unreadState) {
                    UnreadState.Unknown,
                    UnreadState.HasNoUnread            -> false
                    is UnreadState.ReadMarkerNotLoaded -> true
                    is UnreadState.HasUnread           -> {
                        if (it.canShowJumpToReadMarker) {
                            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                            val positionOfReadMarker = timelineEventController.getPositionOfReadMarker()
                            if (positionOfReadMarker == null) {
                                false
                            } else {
                                positionOfReadMarker > lastVisibleItem
                            }
                        } else {
                            false
                        }
                    }
                }
                jumpToReadMarkerView?.isVisible = showJumpToUnreadBanner
            }
        }
    }

    /**
     * Insert a user displayName in the message editor.
     *
     * @param userId the userId.
     */
    @SuppressLint("SetTextI18n")
    private fun insertUserDisplayNameInTextEditor(userId: String) {
        val startToCompose = composerLayout.composerEditText.text.isNullOrBlank()

//        if (startToCompose
//            && userId == session.myUserId) {
            // Empty composer, current user: start an emote
//            composerLayout.composerEditText.setText(Command.EMOTE.command + " ")
//            composerLayout.composerEditText.setSelection(Command.EMOTE.length)
//        } else {
            val roomMember = viewModel.getMember(userId)
            // TODO move logic outside of fragment
            sanitizeDisplayName((roomMember?.displayName ?: userId))
                .let { displayName ->
                    buildSpannedString {
                        append(displayName)
                        setSpan(
                            PillImageSpan(glideRequests,
                                avatarRenderer,
                                requireContext(),
                                MatrixItem.UserItem(userId, displayName, roomMember?.avatarUrl)
                            )
                                .also { it.bind(composerLayout.composerEditText) },
                            0,
                            displayName.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(if (startToCompose) ": " else " ")
                    }.let { pill ->
                        if (startToCompose) {
                            if (displayName.startsWith("/")) {
                                // Ensure displayName will not be interpreted as a Slash command
                                composerLayout.composerEditText.append("\\")
                            }
                            composerLayout.composerEditText.append(pill)
                        } else {
                            composerLayout.composerEditText.text?.insert(composerLayout.composerEditText.selectionStart, pill)
                        }
                    }
                }
//        }
        focusComposerAndShowKeyboard()
    }

    private fun focusComposerAndShowKeyboard() {
        if (composerLayout.isVisible) {
            composerLayout.composerEditText.showKeyboard(andRequestFocus = true)
        }
    }


    companion object {
        /**
         * Sanitize the display name.
         *
         * @param displayName the display name to sanitize
         * @return the sanitized display name
         */
        private fun sanitizeDisplayName(displayName: String): String {
            if (displayName.endsWith(ircPattern)) {
                return displayName.substring(0, displayName.length - ircPattern.length)
            }

            return displayName
        }

        private const val ircPattern = " (IRC)"
    }

    override fun onAcceptInvite() {
        viewModel.handle(RoomDetailAction.AcceptInvite)
    }

    override fun onRejectInvite() {
        viewModel.handle(RoomDetailAction.RejectInvite)
    }

    override fun onJumpToReadMarkerClicked() = withState(viewModel) {
        jumpToReadMarkerView.isVisible = false
        if (it.unreadState is UnreadState.HasUnread) {
            viewModel.handle(RoomDetailAction.NavigateToEvent(it.unreadState.firstUnreadEventId, false))
        }
        if (it.unreadState is UnreadState.ReadMarkerNotLoaded) {
            viewModel.handle(RoomDetailAction.NavigateToEvent(it.unreadState.readMarkerId, false))
        }
    }

    override fun onClearReadMarkerClicked() {
        viewModel.handle(RoomDetailAction.MarkAllAsRead)
    }

    private fun showSnackWithMessage(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireView(), message, duration).show()
    }

    private fun navigateToEvent(action: RoomDetailFragmentViewEvents.NavigateToEvent) {
        val scrollPosition = timelineEventController.searchPositionOfEvent(action.eventId)
        if (scrollPosition == null) {
            scrollOnHighlightedEventCallback.scheduleScrollTo(action.eventId)
        } else {
            recyclerView.stopScroll()
            layoutManager.scrollToPosition(scrollPosition)
        }
    }

    private fun handleJoinedToAnotherRoom(action: RoomDetailFragmentViewEvents.JoinRoomCommandSuccess) {
        updateComposerText("")
        lockSendButton = false
        activity?.let { navigator.openRoom(it, action.roomId) }
    }

    private fun updateComposerText(text: String) {
        // Do not update if this is the same text to avoid the cursor to move
        if (text != composerLayout.composerEditText.text.toString()) {
            // Ignore update to avoid saving a draft
            composerLayout.composerEditText.setText(text)
            composerLayout.composerEditText.setSelection(composerLayout.composerEditText.text?.length
                ?: 0)
        }
    }

    //TODO
    private fun displayRoomDetailActionSuccess(result: RoomDetailFragmentViewEvents.ActionSuccess) {
//        when (val data = result.action) {
//            is RoomDetailAction.ResumeVerification        -> {
//                val otherUserId = data.otherUserId ?: return
//                VerificationBottomSheet().apply {
//                    arguments = Bundle().apply {
//                        putParcelable(MvRx.KEY_ARG, VerificationBottomSheet.VerificationArgs(
//                            otherUserId, data.transactionId, roomId = roomDetailArgs.roomId))
//                    }
//                }.show(parentFragmentManager, "REQ")
//            }
//        }
    }


    private fun renderSendMessageResult(sendMessageResult: RoomDetailFragmentViewEvents.SendMessageResult) {
        when (sendMessageResult) {
            is RoomDetailFragmentViewEvents.MessageSent                -> {
                updateComposerText("")
            }
            is RoomDetailFragmentViewEvents.SlashCommandHandled        -> {
                sendMessageResult.messageRes?.let { showSnackWithMessage(getString(it)) }
                updateComposerText("")
            }
            is RoomDetailFragmentViewEvents.SlashCommandError          -> {
                displayCommandError(getString(R.string.command_problem_with_parameters, sendMessageResult.command.command))
            }
            is RoomDetailFragmentViewEvents.SlashCommandUnknown        -> {
                displayCommandError(getString(R.string.unrecognized_command, sendMessageResult.command))
            }
            is RoomDetailFragmentViewEvents.SlashCommandResultOk       -> {
                updateComposerText("")
            }
            is RoomDetailFragmentViewEvents.SlashCommandResultError    -> {
                displayCommandError(errorFormatter.toHumanReadable(sendMessageResult.throwable))
            }
            is RoomDetailFragmentViewEvents.SlashCommandNotImplemented -> {
                displayCommandError(getString(R.string.not_implemented))
            }
        } // .exhaustive

        lockSendButton = false
    }

    private fun displayCommandError(message: String) {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.command_error)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun displayE2eError(withHeldCode: WithHeldCode?) {
        val msgId = when (withHeldCode) {
            WithHeldCode.BLACKLISTED -> R.string.crypto_error_withheld_blacklisted
            WithHeldCode.UNVERIFIED  -> R.string.crypto_error_withheld_unverified
            WithHeldCode.UNAUTHORISED,
            WithHeldCode.UNAVAILABLE -> R.string.crypto_error_withheld_generic
            else                     -> R.string.notice_crypto_unable_to_decrypt_friendly_desc
        }
        AlertDialog.Builder(requireActivity())
            .setMessage(msgId)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        notificationDrawerManager.setCurrentRoom(roomDetailArgs.roomId)
    }

    override fun onPause() {
        super.onPause()

        notificationDrawerManager.setCurrentRoom(null)

        viewModel.handle(RoomDetailAction.SaveDraft(composerLayout.composerEditText.text.toString()))
    }


    override fun onDestroyView() {
        timelineEventController.callback = null
        timelineEventController.removeModelBuildListener(modelBuildListener)
        modelBuildListener = null
        debouncer.cancelAll()
        recyclerView.cleanup()

        super.onDestroyView()
    }

    override fun onDestroy() {
        viewModel.handle(RoomDetailAction.ExitTrackingUnreadMessagesState)
        super.onDestroy()
    }



}