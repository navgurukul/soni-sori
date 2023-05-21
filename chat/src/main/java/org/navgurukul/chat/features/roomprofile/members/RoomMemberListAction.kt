package org.navgurukul.chat.features.roomprofile.members

import org.navgurukul.commonui.platform.ViewModelAction

sealed class RoomMemberListAction : ViewModelAction {
    data class RevokeThreePidInvite(val stateKey: String) : RoomMemberListAction()
    data class FilterMemberList(val searchTerm: String) : RoomMemberListAction()
    data class OpenOrCreateDm(val userId: String) : RoomMemberListAction()
}
