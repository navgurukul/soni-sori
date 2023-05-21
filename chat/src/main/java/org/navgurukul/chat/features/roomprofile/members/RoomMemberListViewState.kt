
package org.navgurukul.chat.features.roomprofile.members

import androidx.annotation.StringRes
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.navgurukul.chat.R
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState

data class RoomMemberListViewState(
    val roomId: String,
    val roomSummary: Async<RoomSummary> = Uninitialized,
    val roomMemberSummaries: Async<RoomMemberSummaries> = Uninitialized,
    val filter: String = "",
    val threePidInvites: Async<List<Event>> = Uninitialized,
    val actionsPermissions: ActionPermissions = ActionPermissions()
) : ViewState

data class ActionPermissions(
        val canInvite: Boolean = false,
        val canRevokeThreePidInvite: Boolean = false
)

typealias RoomMemberSummaries = List<Pair<RoomMemberListCategories, List<RoomMemberSummary>>>

enum class RoomMemberListCategories(@StringRes val titleRes: Int) {
    ADMIN(R.string.room_member_power_level_admins),
    MODERATOR(R.string.room_member_power_level_moderators),
    CUSTOM(R.string.room_member_power_level_custom),
    INVITE(R.string.room_member_power_level_invites),
    USER(R.string.room_member_power_level_users)
}
