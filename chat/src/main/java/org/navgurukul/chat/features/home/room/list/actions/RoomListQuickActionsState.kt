package org.navgurukul.chat.features.home.room.list.actions

import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState

data class RoomListQuickActionsState(
    val roomId: String,
    val mode: RoomListActionsArgs.Mode,
    val roomSummary: Async<RoomSummary> = Uninitialized,
    val roomNotificationState: Async<RoomNotificationState> = Uninitialized
) : ViewState