
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
        R.drawable.bg_unread_highlight
    )

    data class NotificationsAll(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_all,
        R.drawable.bg_unread_highlight
    )

    data class NotificationsMentionsOnly(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_mentions,
        R.drawable.bg_unread_highlight
    )

    data class NotificationsMute(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_notifications_mute,
        R.drawable.bg_unread_highlight
    )

    data class Settings(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_settings,
        R.drawable.bg_unread_highlight
    )

//    data class LowPriority(val roomId: String) : RoomListQuickActionsSharedAction(
//            R.string.room_list_quick_actions_low_priority_add,
//            R.drawable.ic_low_priority_24)

    data class Favorite(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_favorite_add,
        R.drawable.bg_unread_highlight)

    data class Leave(val roomId: String) : RoomListQuickActionsSharedAction(
            R.string.room_list_quick_actions_leave,
        R.drawable.bg_unread_highlight,
            true
    )
}
