
package org.navgurukul.chat.features.home.room.list.actions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.navgurukul.chat.R
import org.navgurukul.commonui.platform.ViewEvents

sealed class RoomListQuickActionsSharedAction(
        @StringRes val titleRes: Int,
        @DrawableRes val iconResId: Int,
        val destructive: Boolean = false)
    : ViewEvents {

    data class NotificationsAllNoisy(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_all_noisy,
            R.drawable.ic_room_actions_notifications_all_noisy
    )

    data class NotificationsAll(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_all,
            R.drawable.ic_room_actions_notifications_all
    )

    data class NotificationsMentionsOnly(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_mentions,
            R.drawable.ic_room_actions_notifications_mentions
    )

    data class NotificationsMute(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_mute,
            R.drawable.ic_room_actions_notifications_mutes
    )

    data class Settings(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_settings,
            R.drawable.ic_room_actions_settings
    )

//    data class LowPriority(val roomId: String) : RoomListQuickActionsSharedAction(
//            R.string.room_list_quick_actions_low_priority_add,
//            R.drawable.ic_low_priority_24)

    data class Favorite(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_favorite_add,
            R.drawable.ic_star_24dp)

    data class Leave(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_leave,
            R.drawable.ic_room_actions_leave,
            true
    )
}
