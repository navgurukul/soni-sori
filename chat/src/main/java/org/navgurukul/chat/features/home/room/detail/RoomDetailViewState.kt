package org.navgurukul.chat.features.home.room.detail

import im.vector.matrix.android.api.session.events.model.Event
import im.vector.matrix.android.api.session.room.members.ChangeMembershipState
import im.vector.matrix.android.api.session.room.model.RoomMemberSummary
import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.matrix.android.api.session.room.timeline.TimelineEvent
import im.vector.matrix.android.api.session.sync.SyncState
import im.vector.matrix.android.api.session.user.model.User
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState

/**
 * Describes the current send mode:
 * REGULAR: sends the text as a regular message
 * QUOTE: User is currently quoting a message
 * EDIT: User is currently editing an existing message
 *
 * Depending on the state the bottom toolbar will change (icons/preview/actions...)
 */
sealed class SendMode(open val text: String) {
        data class REGULAR(override val text: String) : SendMode(text)
        data class QUOTE(val timelineEvent: TimelineEvent, override val text: String) : SendMode(text)
        data class EDIT(val timelineEvent: TimelineEvent, override val text: String) : SendMode(text)
        data class REPLY(val timelineEvent: TimelineEvent, override val text: String) : SendMode(text)
}

sealed class UnreadState {
        object Unknown : UnreadState()
        object HasNoUnread : UnreadState()
        data class ReadMarkerNotLoaded(val readMarkerId: String) : UnreadState()
        data class HasUnread(val firstUnreadEventId: String) : UnreadState()
}

data class RoomDetailViewState(
    val roomId: String,
    val eventId: String?,
    val myRoomMember: Async<RoomMemberSummary> = Uninitialized,
    val asyncInviter: Async<User> = Uninitialized,
    val asyncRoomSummary: Async<RoomSummary> = Uninitialized,
    val subtitle: String? = null,
    val sendMode: SendMode = SendMode.REGULAR(""),
    val tombstoneEvent: Event? = null,
    val tombstoneEventHandling: Async<String> = Uninitialized,
    val syncState: SyncState = SyncState.Idle,
    val highlightedEventId: String? = null,
    val unreadState: UnreadState = UnreadState.Unknown,
    val canShowJumpToReadMarker: Boolean = true,
    val changeMembershipState: ChangeMembershipState = ChangeMembershipState.Unknown,
    val canSendMessage: Boolean = true
) : ViewState {

}