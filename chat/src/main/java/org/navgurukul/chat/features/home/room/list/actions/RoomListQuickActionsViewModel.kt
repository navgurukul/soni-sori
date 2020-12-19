package org.navgurukul.chat.features.home.room.list.actions

import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents

class RoomListQuickActionsViewModel(
    initialState: RoomListQuickActionsState,
    activeSessionHolder: ActiveSessionHolder
) : BaseViewModel<EmptyViewEvents, RoomListQuickActionsState>(initialState) {

    private val room = activeSessionHolder.getActiveSession().getRoom(initialState.roomId)!!

    init {
        observeRoomSummary()
        observeNotificationState()
    }

    private fun observeNotificationState() {
        room
            .rx()
            .liveNotificationState()
            .execute {
                copy(roomNotificationState = it)
            }
    }

    private fun observeRoomSummary() {
        room
            .rx()
            .liveRoomSummary()
            .unwrap()
            .execute {
                copy(roomSummary = it)
            }
    }
}
