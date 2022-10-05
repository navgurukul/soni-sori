package org.navgurukul.chat.features.home.room.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.TextUtils
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
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
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChanges
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.message.*
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.internal.crypto.model.event.EncryptedEventContent
import org.matrix.android.sdk.internal.crypto.model.event.WithHeldCode
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_room_detail.*
import kotlinx.android.synthetic.main.merge_composer_layout.view.*
import org.commonmark.parser.Parser
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.hideKeyboard
import org.merakilearn.core.extentions.showKeyboard
import org.merakilearn.core.extentions.toast
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.chat.R
import org.navgurukul.chat.core.dialogs.ConfirmationDialogBuilder
import org.navgurukul.chat.core.dialogs.withColoredButton
import org.navgurukul.chat.core.extensions.*
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.utils.*
import org.navgurukul.chat.core.utils.createUIHandler
import org.navgurukul.chat.core.views.NotificationAreaView
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.composer.TextComposerView
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.action.EventSharedAction
import org.navgurukul.chat.features.home.room.detail.timeline.action.MessageActionsBottomSheet
import org.navgurukul.chat.features.home.room.detail.timeline.action.MessageSharedActionDataSource
import org.navgurukul.chat.features.home.room.detail.timeline.item.*
import org.navgurukul.chat.features.home.room.detail.timeline.reactions.ViewReactionsBottomSheet
import org.navgurukul.chat.features.html.EventHtmlRenderer
import org.navgurukul.chat.features.html.PillImageSpan
import org.navgurukul.chat.features.invite.SaralInviteView
import org.navgurukul.chat.features.media.ImageContentRenderer
import org.navgurukul.chat.features.media.VideoContentRenderer
import org.navgurukul.chat.features.navigator.ChatInternalNavigator
import org.navgurukul.chat.features.notifications.NotificationDrawerManager
import org.navgurukul.chat.features.reactions.EmojiReactionPickerActivity
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

private const val REACTION_SELECT_REQUEST_CODE = 0

class RoomDetailFragment : BaseFragment(),
    TimelineEventController.Callback,
    SaralInviteView.Callback,
    JumpToReadMarkerView.Callback, KoinScopeComponent {

    override val scope: Scope by lazy { fragmentScope() }

    private lateinit var layoutManager: LinearLayoutManager
    private val roomDetailArgs: RoomDetailArgs by fragmentArgs()

    private val timelineEventController: TimelineEventController by inject(parameters = { parametersOf(scope)})

    private val viewModel: RoomDetailFragmentViewModel by viewModel(parameters = {
        parametersOf(
            RoomDetailViewState(roomId = roomDetailArgs.roomId, eventId = roomDetailArgs.eventId),
            scope
        )
    })

    private val sharedActionDataSource: MessageSharedActionDataSource by lazy {
        (requireActivity() as KoinScopeComponent).scope.get()
    }

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

        roomToolbarContentView.debouncedClicks {
            navigator.openRoomProfile(requireActivity(), roomDetailArgs.roomId)
        }


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
                is RoomDetailFragmentViewEvents.ActionFailure                    -> displayRoomDetailActionFailure(it)
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
                is RoomDetailFragmentViewEvents.OpenFile                         -> startOpenFileIntent(it)
            }
        })

        sharedActionDataSource.observe(viewLifecycleOwner, Observer {
            handleActions(it)
        })
    }

    private fun openDeepLink(event: RoomDetailFragmentViewEvents.OpenDeepLink) {
        navigator.openDeepLink(requireActivity(), event.deepLink)
    }

    private fun startOpenFileIntent(action: RoomDetailFragmentViewEvents.OpenFile) {
        if (action.uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndTypeAndNormalize(action.uri, action.mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                requireActivity().startActivity(intent)
            } else {
                requireActivity().toast(R.string.error_no_external_application_found)
            }
        }
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

    private fun handleActions(action: EventSharedAction) {
        when (action) {
            is EventSharedAction.OpenUserProfile            -> {
                openRoomMemberProfile(action.userId)
            }
            is EventSharedAction.AddReaction                -> {
                startActivityForResult(EmojiReactionPickerActivity.intent(requireContext(), action.eventId), REACTION_SELECT_REQUEST_CODE)
            }
            is EventSharedAction.ViewReactions              -> {
                ViewReactionsBottomSheet.newInstance(roomDetailArgs.roomId, action.messageInformationData)
                    .show(requireActivity().supportFragmentManager, "DISPLAY_REACTIONS")
            }
            is EventSharedAction.Copy                       -> {
                // I need info about the current selected message :/
                copyToClipboard(requireContext(), action.content, false)
                showSnackWithMessage(getString(R.string.copied_to_clipboard), Snackbar.LENGTH_SHORT)
            }
            is EventSharedAction.Redact                     -> {
                promptConfirmationToRedactEvent(action)
            }
            is EventSharedAction.Share                      -> {
//                onShareActionClicked(action)
            }
            is EventSharedAction.Save                       -> {
//                onSaveActionClicked(action)
            }
            is EventSharedAction.ViewEditHistory            -> {
                onEditedDecorationClicked(action.messageInformationData)
            }
            is EventSharedAction.ViewSource                 -> {
//                JSonViewerDialog.newInstance(
//                    action.content,
//                    -1,
//                    createJSonViewerStyleProvider(colorProvider)
//                ).show(childFragmentManager, "JSON_VIEWER")
            }
            is EventSharedAction.ViewDecryptedSource        -> {
//                JSonViewerDialog.newInstance(
//                    action.content,
//                    -1,
//                    createJSonViewerStyleProvider(colorProvider)
//                ).show(childFragmentManager, "JSON_VIEWER")
            }
            is EventSharedAction.QuickReact                 -> {
                // eventId,ClickedOn,Add
                viewModel.handle(RoomDetailAction.UpdateQuickReactAction(action.eventId, action.clickedOn, action.add))
            }
            is EventSharedAction.Edit                       -> {
                viewModel.handle(RoomDetailAction.EnterEditMode(action.eventId, composerLayout.text.toString()))
            }
            is EventSharedAction.Quote                      -> {
                viewModel.handle(RoomDetailAction.EnterQuoteMode(action.eventId, composerLayout.text.toString()))
            }
            is EventSharedAction.Reply                      -> {
                viewModel.handle(RoomDetailAction.EnterReplyMode(action.eventId, composerLayout.text.toString()))
            }
//            is EventSharedAction.CopyPermalink              -> {
//                val permalink = PermalinkFactory.createPermalink(roomDetailArgs.roomId, action.eventId)
//                copyToClipboard(requireContext(), permalink, false)
//                showSnackWithMessage(getString(R.string.copied_to_clipboard), Snackbar.LENGTH_SHORT)
//            }
            is EventSharedAction.Resend                     -> {
                viewModel.handle(RoomDetailAction.ResendMessage(action.eventId))
            }
            is EventSharedAction.Remove                     -> {
                viewModel.handle(RoomDetailAction.RemoveFailedEcho(action.eventId))
            }
            is EventSharedAction.ReportContentSpam          -> {
                viewModel.handle(RoomDetailAction.ReportContent(
                    action.eventId, action.senderId, "This message is spam", spam = true))
            }
            is EventSharedAction.ReportContentInappropriate -> {
                viewModel.handle(RoomDetailAction.ReportContent(
                    action.eventId, action.senderId, "This message is inappropriate", inappropriate = true))
            }
            is EventSharedAction.ReportContentCustom        -> {
                promptReasonToReportContent(action)
            }
            is EventSharedAction.IgnoreUser                 -> {
                viewModel.handle(RoomDetailAction.IgnoreUser(action.senderId))
            }
            is EventSharedAction.OnUrlClicked               -> {
                onUrlClicked(action.url, action.title)
            }
            is EventSharedAction.OnUrlLongClicked           -> {
                onUrlLongClicked(action.url)
            }
//            is EventSharedAction.ReRequestKey               -> {
//                viewModel.handle(RoomDetailAction.ReRequestKeys(action.eventId))
//            }
//            is EventSharedAction.UseKeyBackup               -> {
//                context?.let {
//                    startActivity(KeysBackupRestoreActivity.intent(it))
//                }
//            }
            else                                            -> {
                Toast.makeText(context, "Action $action is not implemented yet", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun promptConfirmationToRedactEvent(action: EventSharedAction.Redact) {
        ConfirmationDialogBuilder
            .show(
                activity = requireActivity(),
                askForReason = action.askForReason,
                confirmationRes = R.string.delete_event_dialog_content,
                positiveRes = R.string.remove,
                reasonHintRes = R.string.delete_event_dialog_reason_hint,
                titleRes = R.string.delete_event_dialog_title
            ) { reason ->
                viewModel.handle(RoomDetailAction.RedactAction(action.eventId, reason))
            }
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
                viewModel.handle(RoomDetailAction.EnterRegularMode(composerLayout.text.toString(), false))
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

    override fun onClickOnReactionPill(informationData: MessageInformationData, reaction: String, on: Boolean) {
        if (on) {
            // we should test the current real state of reaction on this event
            viewModel.handle(RoomDetailAction.SendReaction(informationData.eventId, reaction))
        } else {
            // I need to redact a reaction
            viewModel.handle(RoomDetailAction.UndoReaction(informationData.eventId, reaction))
        }
    }

    override fun onLongClickOnReactionPill(informationData: MessageInformationData, reaction: String) {
        ViewReactionsBottomSheet.newInstance(roomDetailArgs.roomId, informationData)
            .show(requireActivity().supportFragmentManager, "DISPLAY_REACTIONS")
    }

    override fun onEditedDecorationClicked(informationData: MessageInformationData) {
//        ViewEditHistoryBottomSheet.newInstance(roomDetailArgs.roomId, informationData)
//            .show(requireActivity().supportFragmentManager, "DISPLAY_EDITS")
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
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        val roomId = roomDetailArgs.roomId

        this.view?.hideKeyboard()

        MessageActionsBottomSheet
            .newInstance(roomId, informationData)
            .show(requireActivity().supportFragmentManager, "MESSAGE_CONTEXTUAL_ACTIONS")
        return true
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

    override fun onReadMarkerVisible() {
        updateJumpToReadMarkerViewVisibility()
        viewModel.handle(RoomDetailAction.EnterTrackingUnreadMessagesState)
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

    private fun displayRoomDetailActionSuccess(result: RoomDetailFragmentViewEvents.ActionSuccess) {
        when (val data = result.action) {
            is RoomDetailAction.ReportContent             -> {
                when {
                    data.spam          -> {
                        AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.content_reported_as_spam_title)
                            .setMessage(R.string.content_reported_as_spam_content)
                            .setPositiveButton(R.string.ok, null)
                            .setNegativeButton(R.string.block_user) { _, _ ->
                                viewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                            }
                            .show()
                            .withColoredButton(DialogInterface.BUTTON_NEGATIVE)
                    }
                    data.inappropriate -> {
                        AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.content_reported_as_inappropriate_title)
                            .setMessage(R.string.content_reported_as_inappropriate_content)
                            .setPositiveButton(R.string.ok, null)
                            .setNegativeButton(R.string.block_user) { _, _ ->
                                viewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                            }
                            .show()
                            .withColoredButton(DialogInterface.BUTTON_NEGATIVE)
                    }
                    else               -> {
                        AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.content_reported_title)
                            .setMessage(R.string.content_reported_content)
                            .setPositiveButton(R.string.ok, null)
                            .setNegativeButton(R.string.block_user) { _, _ ->
                                viewModel.handle(RoomDetailAction.IgnoreUser(data.senderId))
                            }
                            .show()
                            .withColoredButton(DialogInterface.BUTTON_NEGATIVE)
                    }
                }
            }
//            is RoomDetailAction.RequestVerification       -> {
//                Timber.v("## SAS RequestVerification action")
//                VerificationBottomSheet.withArgs(
//                    roomDetailArgs.roomId,
//                    data.userId
//                ).show(parentFragmentManager, "REQ")
//            }
//            is RoomDetailAction.AcceptVerificationRequest -> {
//                Timber.v("## SAS AcceptVerificationRequest action")
//                VerificationBottomSheet.withArgs(
//                    roomDetailArgs.roomId,
//                    data.otherUserId,
//                    data.transactionId
//                ).show(parentFragmentManager, "REQ")
//            }
//            is RoomDetailAction.ResumeVerification        -> {
//                val otherUserId = data.otherUserId ?: return
//                VerificationBottomSheet().apply {
//                    arguments = Bundle().apply {
//                        putParcelable(MvRx.KEY_ARG, VerificationBottomSheet.VerificationArgs(
//                            otherUserId, data.transactionId, roomId = roomDetailArgs.roomId))
//                    }
//                }.show(parentFragmentManager, "REQ")
//            }
        }
    }

    private fun promptReasonToReportContent(action: EventSharedAction.ReportContentCustom) {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_report_content, null)

        val input = layout.findViewById<TextInputEditText>(R.id.dialog_report_content_input)

        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.report_content_custom_title)
            .setView(layout)
            .setPositiveButton(R.string.report_content_custom_submit) { _, _ ->
                val reason = input.text.toString()
                viewModel.handle(RoomDetailAction.ReportContent(action.eventId, action.senderId, reason))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun displayRoomDetailActionFailure(result: RoomDetailFragmentViewEvents.ActionFailure) {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.dialog_title_error)
            .setMessage(errorFormatter.toHumanReadable(result.throwable))
            .setPositiveButton(R.string.ok, null)
            .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val hasBeenHandled = attachmentsHelper.onActivityResult(requestCode, resultCode, data)
        if (/*!hasBeenHandled && */resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
//                AttachmentsPreviewActivity.REQUEST_CODE        -> {
//                    val sendData = AttachmentsPreviewActivity.getOutput(data)
//                    val keepOriginalSize = AttachmentsPreviewActivity.getKeepOriginalSize(data)
//                    roomDetailViewModel.handle(RoomDetailAction.SendMedia(sendData, !keepOriginalSize))
//                }
                REACTION_SELECT_REQUEST_CODE                   -> {
                    val (eventId, reaction) = EmojiReactionPickerActivity.getOutput(data) ?: return
                    viewModel.handle(RoomDetailAction.SendReaction(eventId, reaction))
                }
//                WidgetRequestCodes.STICKER_PICKER_REQUEST_CODE -> {
//                    val content = WidgetActivity.getOutput(data).toModel<MessageStickerContent>() ?: return
//                    roomDetailViewModel.handle(RoomDetailAction.SendSticker(content))
//                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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