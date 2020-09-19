package org.navgurukul.chat.features.home.room.detail

import com.jakewharton.rxrelay2.BehaviorRelay
import im.vector.matrix.android.api.query.QueryStringValue
import im.vector.matrix.android.api.session.Session
import im.vector.matrix.android.api.session.events.model.EventType
import im.vector.matrix.android.api.session.room.Room
import im.vector.matrix.android.api.session.room.model.Membership
import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.matrix.android.api.util.Optional
import im.vector.matrix.rx.rx
import im.vector.matrix.rx.unwrap
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.navgurukul.chat.R
import org.navgurukul.chat.core.resources.StringProvider
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewState
import org.navgurukul.commonui.platform.ViewEvents

sealed class RoomDetailViewEvents: ViewEvents {
    data class RoomLeft(val leftMessage: String?) : RoomDetailViewEvents()
}

class RoomDetailViewModel(roomId: String,
                          private val stringProvider: StringProvider,
                          private val session: Session
): BaseViewModel<RoomDetailViewEvents, EmptyViewState>(EmptyViewState) {

    private val roomIdObservable = BehaviorRelay.createDefault(Optional.from(roomId))

    init {
        observeRoomSummary()
    }

    private fun observeRoomSummary() {
        roomIdObservable
            .unwrap()
            .switchMap { roomId ->
                val room = session.getRoom(roomId) ?: return@switchMap Observable.just(Optional.empty<RoomDetailViewEvents.RoomLeft>())
                room.rx()
                    .liveRoomSummary()
                    .unwrap()
                    .observeOn(Schedulers.computation())
                    .map { mapToLeftViewEvent(room, it) }
            }
            .unwrap()
            .subscribe { event ->
                _viewEvents.postValue(event)
            }
            .disposeOnClear()
    }

    private fun mapToLeftViewEvent(room: Room, roomSummary: RoomSummary): Optional<RoomDetailViewEvents.RoomLeft> {
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
                RoomDetailViewEvents.RoomLeft(message)
            }
            Membership.KNOCK -> {
                val message = senderDisplayName?.let {
                    stringProvider.getString(R.string.has_been_kicked, roomSummary.displayName, it)
                }
                RoomDetailViewEvents.RoomLeft(message)
            }
            Membership.BAN   -> {
                val message = senderDisplayName?.let {
                    stringProvider.getString(R.string.has_been_banned, roomSummary.displayName, it)
                }
                RoomDetailViewEvents.RoomLeft(message)
            }
            else             -> null
        }
        return Optional.from(viewEvent)
    }


}