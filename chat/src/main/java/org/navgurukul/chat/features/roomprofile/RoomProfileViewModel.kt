package org.navgurukul.chat.features.roomprofile

import androidx.core.content.pm.ShortcutInfoCompat
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import org.matrix.android.sdk.rx.RxRoom
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class RoomProfileViewModel(
    initialState: RoomProfileViewState,
    private val stringProvider: StringProvider,
    activeSessionHolder: ActiveSessionHolder
) : BaseViewModel<RoomProfileViewEvents, RoomProfileViewState>(initialState) {


    private val session = activeSessionHolder.getActiveSession()
    private val room = session.getRoom(initialState.roomId)!!

    init {
        val rxRoom = room.rx()
        observeRoomSummary(rxRoom)
        observeBannedRoomMembers(rxRoom)
    }

    private fun observeRoomSummary(rxRoom: RxRoom) {
        rxRoom.liveRoomSummary()
            .unwrap()
            .execute {
                copy(roomSummary = it)
            }
    }

    private fun observeBannedRoomMembers(rxRoom: RxRoom) {
        rxRoom.liveRoomMembers(roomMemberQueryParams { memberships = listOf(Membership.BAN) })
            .execute {
                copy(bannedMembership = it)
            }
    }

    fun handle(action: RoomProfileAction) {
        when (action) {
            RoomProfileAction.LeaveRoom -> handleLeaveRoom()
            is RoomProfileAction.ChangeRoomNotificationState -> handleChangeNotificationMode(action)
        }
    }

    private fun handleChangeNotificationMode(action: RoomProfileAction.ChangeRoomNotificationState) {
        room.setRoomNotificationState(action.notificationState, object : MatrixCallback<Unit> {
            override fun onFailure(failure: Throwable) {
                _viewEvents.setValue(RoomProfileViewEvents.Failure(failure))
            }
        })
    }

    private fun handleLeaveRoom() {
        _viewEvents.setValue(RoomProfileViewEvents.Loading(stringProvider.getString(R.string.room_profile_leaving_room)))
        room.leave(null, object : MatrixCallback<Unit> {
            override fun onSuccess(data: Unit) {
                // Do nothing, we will be closing the room automatically when it will get back from sync
            }

            override fun onFailure(failure: Throwable) {
                _viewEvents.setValue(RoomProfileViewEvents.Failure(failure))
            }
        })
    }
}

data class RoomProfileViewState(
    val roomId: String,
    val roomSummary: Async<RoomSummary> = Uninitialized,
    val bannedMembership: Async<List<RoomMemberSummary>> = Uninitialized
) : ViewState

/**
 * Transient events for RoomProfile
 */
sealed class RoomProfileViewEvents : ViewEvents {
    data class Loading(val message: CharSequence? = null) : RoomProfileViewEvents()
    data class Failure(val throwable: Throwable) : RoomProfileViewEvents()
}

sealed class RoomProfileAction : ViewModelAction {
    object LeaveRoom : RoomProfileAction()
    data class ChangeRoomNotificationState(val notificationState: RoomNotificationState) : RoomProfileAction()
}
