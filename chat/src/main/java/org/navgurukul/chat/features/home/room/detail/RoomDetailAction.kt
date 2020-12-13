package org.navgurukul.chat.features.home.room.detail

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.MessageWithAttachmentContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.commonui.platform.ViewModelAction

sealed class RoomDetailAction : ViewModelAction {
    data class UserIsTyping(val isTyping: Boolean) : RoomDetailAction()
    data class SaveDraft(val draft: String) : RoomDetailAction()
//    data class SendSticker(val stickerContent: MessageStickerContent) : RoomDetailAction()
    data class SendMessage(val text: CharSequence, val autoMarkdown: Boolean) : RoomDetailAction()
//    data class SendMedia(val attachments: List<ContentAttachmentData>, val compressBeforeSending: Boolean) : RoomDetailAction()
    data class TimelineEventTurnsVisible(val event: TimelineEvent) : RoomDetailAction()
    data class TimelineEventTurnsInvisible(val event: TimelineEvent) : RoomDetailAction()
    data class LoadMoreTimelineEvents(val direction: Timeline.Direction) : RoomDetailAction()
    data class SendReaction(val targetEventId: String, val reaction: String) : RoomDetailAction()
    data class UndoReaction(val targetEventId: String, val reaction: String, val reason: String? = "") : RoomDetailAction()
    data class RedactAction(val targetEventId: String, val reason: String? = "") : RoomDetailAction()
    data class UpdateQuickReactAction(val targetEventId: String, val selectedReaction: String, val add: Boolean) : RoomDetailAction()
    data class NavigateToEvent(val eventId: String, val highlight: Boolean) : RoomDetailAction()
    object MarkAllAsRead : RoomDetailAction()
    data class DownloadOrOpen(val eventId: String, val messageFileContent: MessageWithAttachmentContent) : RoomDetailAction()
    data class HandleTombstoneEvent(val event: Event) : RoomDetailAction()
    object AcceptInvite : RoomDetailAction()
    object RejectInvite : RoomDetailAction()

    object EnterTrackingUnreadMessagesState : RoomDetailAction()
    object ExitTrackingUnreadMessagesState : RoomDetailAction()

    data class EnterEditMode(val eventId: String, val text: String) : RoomDetailAction()
    data class EnterQuoteMode(val eventId: String, val text: String) : RoomDetailAction()
    data class EnterReplyMode(val eventId: String, val text: String) : RoomDetailAction()
    data class EnterRegularMode(val text: String, val fromSharing: Boolean) : RoomDetailAction()

    data class ResendMessage(val eventId: String) : RoomDetailAction()
    data class RemoveFailedEcho(val eventId: String) : RoomDetailAction()

    data class ReplyToOptions(val eventId: String, val optionIndex: Int, val optionValue: String) : RoomDetailAction()

    data class ReportContent(
            val eventId: String,
            val senderId: String?,
            val reason: String,
            val spam: Boolean = false,
            val inappropriate: Boolean = false) : RoomDetailAction()

    data class IgnoreUser(val userId: String?) : RoomDetailAction()
//
//    object ClearSendQueue : RoomDetailAction()
//    object ResendAll : RoomDetailAction()
//    data class StartCall(val isVideo: Boolean) : RoomDetailAction()
//    object EndCall : RoomDetailAction()
//
//    data class AcceptVerificationRequest(val transactionId: String, val otherUserId: String) : RoomDetailAction()
//    data class DeclineVerificationRequest(val transactionId: String, val otherUserId: String) : RoomDetailAction()
//    data class RequestVerification(val userId: String) : RoomDetailAction()
    data class ResumeVerification(val transactionId: String, val otherUserId: String?) : RoomDetailAction()
    data class TapOnFailedToDecrypt(val eventId: String) : RoomDetailAction()
//    data class ReRequestKeys(val eventId: String) : RoomDetailAction()
//
//    object SelectStickerAttachment : RoomDetailAction()
//    object OpenIntegrationManager: RoomDetailAction()
}
