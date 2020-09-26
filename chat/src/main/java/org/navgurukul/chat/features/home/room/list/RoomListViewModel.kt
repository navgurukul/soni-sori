package org.navgurukul.chat.features.home.room.list

import androidx.lifecycle.viewModelScope
import im.vector.matrix.rx.rx
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.navgurukul.chat.core.repo.ActiveSessionDataSource
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.chat.features.home.HomeRoomListDataSource
import org.navgurukul.commonui.platform.BaseViewModel

class RoomListViewModel(
    initialState: RoomListViewState,
    authenticationRepository: AuthenticationRepository,
    activeSessionHolder: ActiveSessionHolder,
    private val activeSessionDataSource: ActiveSessionDataSource,
    private val homeRoomListDataSource: HomeRoomListDataSource
) : BaseViewModel<RoomListViewEvents, RoomListViewState>(initialState) {

    init {
        //TODO remove this
        if (!activeSessionHolder.hasActiveSession()) {
            viewModelScope.launch {
                authenticationRepository.login()
            }
        }

        observeRoomSummaries()
        observeSyncState()
    }

    private fun observeSyncState() {
        activeSessionDataSource
            .observe()
            .switchMap {
                it.orNull()?.rx()?.liveSyncState()
            }
            .subscribe { syncState ->
                setState {
                    copy(syncState = syncState)
                }
            }
            .disposeOnClear()
    }

    private fun observeRoomSummaries() {
        homeRoomListDataSource
            .observe()
            .observeOn(Schedulers.computation())
            .execute { asyncRooms ->
                copy(asyncRooms = asyncRooms)
            }
    }

    private fun handleSelectRoom(action: RoomListAction.SelectRoom) = viewState.value?.let {
        _viewEvents.value = RoomListViewEvents.SelectRoom(action.roomSummary)
    }

    fun handle(action: RoomListAction) {
        when (action) {
            is RoomListAction.SelectRoom                  -> handleSelectRoom(action)
//            is RoomListAction.AcceptInvitation            -> handleAcceptInvitation(action)
//            is RoomListAction.RejectInvitation            -> handleRejectInvitation(action)
//            is RoomListAction.MarkAllRoomsRead            -> handleMarkAllRoomsRead()
//            is RoomListAction.LeaveRoom                   -> handleLeaveRoom(action)
//            is RoomListAction.ChangeRoomNotificationState -> handleChangeNotificationMode(action)
//            is RoomListAction.ToggleFavorite              -> handleToggleFavorite(action)
        }
    }

}