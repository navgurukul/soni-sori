package org.navgurukul.chat.features.home.room.list

import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.matrix.android.api.session.sync.SyncState
import org.navgurukul.chat.core.model.Async
import org.navgurukul.chat.core.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState

data class RoomListViewState(
    val asyncRooms: Async<List<RoomSummary>> = Uninitialized,
    val syncState: SyncState = SyncState.Idle
) : ViewState