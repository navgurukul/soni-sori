package org.navgurukul.chat.features.home.room.list

import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.SyncState
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState

data class RoomListViewState(
    val asyncRooms: Async<List<RoomSummary>> = Uninitialized,
    val syncState: SyncState = SyncState.Idle
) : ViewState