package org.navgurukul.chat.features.home.room.detail

import com.jakewharton.rxrelay2.BehaviorRelay
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewState
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.resources.StringProvider

sealed class RequireActiveMembershipViewEvents: ViewEvents {
    data class RoomLeft(val leftMessage: String?) : RequireActiveMembershipViewEvents()
}

class RequireActiveMembershipViewModel(roomId: String,
                                       private val stringProvider: StringProvider,
                                       private val activeSessionHolder: ActiveSessionHolder
): BaseViewModel<RequireActiveMembershipViewEvents, EmptyViewState>(EmptyViewState) {

    private val roomIdObservable = BehaviorRelay.createDefault(Optional.from(roomId))

    init {
        observeRoomSummary()
    }

    private fun observeRoomSummary() {
        roomIdObservable
            .unwrap()
            .switchMap { roomId ->
                val session = activeSessionHolder.getSafeActiveSession() ?: return@switchMap Observable.just(Optional.empty<RequireActiveMembershipViewEvents.RoomLeft>())
                val room = session.getRoom(roomId) ?: return@switchMap Observable.just(Optional.empty<RequireActiveMembershipViewEvents.RoomLeft>())
                room.rx()
                    .liveRoomSummary()
                    .unwrap()
                    .observeOn(Schedulers.computation())
                    .map { mapToLeftViewEvent(session, room, it) }
            }
            .unwrap()
            .subscribe { event ->
                _viewEvents.postValue(event)
            }
            .disposeOnClear()
    }

    private fun mapToLeftViewEvent(session: Session, room: Room, roomSummary: RoomSummary): Optional<RequireActiveMembershipViewEvents.RoomLeft> {
        if (roomSummary.membership.isActive()) {
            return Optional.empty()
        }
        val senderId = room.getStateEvent(EventType.STATE_ROOM_MEMBER, QueryStringValue.Equals(session.myUserId))?.senderId
        val senderDisplayName = senderId?.takeIf { it != session.myUserId }?.let {
            room.getRoomMember(it)?.displayName ?: it
        }
        val viewEvent = when (roomSummary.membership) {
            Membership.LEAVE -> {
                val message = senderDisplayName?.let {
                    stringProvider.getString(R.string.has_been_kicked, roomSummary.displayName, it)
                }
                RequireActiveMembershipViewEvents.RoomLeft(message)
            }
            Membership.KNOCK -> {
                val message = senderDisplayName?.let {
                    stringProvider.getString(R.string.has_been_kicked, roomSummary.displayName, it)
                }
                RequireActiveMembershipViewEvents.RoomLeft(message)
            }
            Membership.BAN   -> {
                val message = senderDisplayName?.let {
                    stringProvider.getString(R.string.has_been_banned, roomSummary.displayName, it)
                }
                RequireActiveMembershipViewEvents.RoomLeft(message)
            }
            else             -> null
        }
        return Optional.from(viewEvent)
    }


}