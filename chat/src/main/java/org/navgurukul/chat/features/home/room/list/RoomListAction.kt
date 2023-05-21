package org.navgurukul.chat.features.home.room.list

import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import org.navgurukul.commonui.platform.ViewModelAction

sealed class RoomListAction : ViewModelAction {
    data class SelectRoom(val roomSummary: RoomSummary) : RoomListAction()
//    data class AcceptInvitation(val roomSummary: RoomSummary) : RoomListAction()
//    data class RejectInvitation(val roomSummary: RoomSummary) : RoomListAction()
//    data class ChangeRoomNotificationState(val roomId: String, val notificationState: RoomNotificationState) : RoomListAction()
//    data class ToggleFavorite(val roomId: String) : RoomListAction()
//    data class LeaveRoom(val roomId: String) : RoomListAction()
//    object MarkAllRoomsRead : RoomListAction()
}