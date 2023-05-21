package org.navgurukul.chat.features.home.room.list.actions

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import org.matrix.android.sdk.api.util.toMatrixItem
import org.navgurukul.chat.core.epoxy.bottomsheet.bottomSheetActionItem
import org.navgurukul.chat.core.epoxy.bottomsheet.bottomSheetRoomPreviewItem
import org.navgurukul.chat.core.epoxy.dividerItem
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.resources.StringProvider

/**
 * Epoxy controller for room list actions
 */
class RoomListQuickActionsEpoxyController(
    private val avatarRenderer: AvatarRenderer,
    private val stringProvider: StringProvider
) : TypedEpoxyController<RoomListQuickActionsState>() {

    var listener: Listener? = null

    override fun buildModels(state: RoomListQuickActionsState) {
        val roomSummary = state.roomSummary() ?: return
        val showAll = state.mode == RoomListActionsArgs.Mode.FULL

        if (showAll) {
            // Preview, favorite, settings
            bottomSheetRoomPreviewItem {
                id("room_preview")
                avatarRenderer(avatarRenderer)
                matrixItem(roomSummary.toMatrixItem())
                stringProvider(stringProvider)
//                izLowPriority(roomSummary.isLowPriority)
                izFavorite(roomSummary.isFavorite)
                settingsClickListener { listener?.didSelectMenuAction(
                    RoomListQuickActionsSharedAction.Settings(roomSummary.roomId)
                ) }
                favoriteClickListener { listener?.didSelectMenuAction(
                    RoomListQuickActionsSharedAction.Favorite(roomSummary.roomId)
                ) }
//                lowPriorityClickListener { listener?.didSelectMenuAction(
//                    RoomListQuickActionsSharedAction.LowPriority(roomSummary.roomId)
//                ) }
            }

            // Notifications
            dividerItem {
                id("notifications_separator")
            }
        }

        val selectedRoomState = state.roomNotificationState()
        RoomListQuickActionsSharedAction.NotificationsAllNoisy(roomSummary.roomId)
            .toBottomSheetItem(0, selectedRoomState)
        RoomListQuickActionsSharedAction.NotificationsAll(roomSummary.roomId)
            .toBottomSheetItem(1, selectedRoomState)
        RoomListQuickActionsSharedAction.NotificationsMentionsOnly(roomSummary.roomId)
            .toBottomSheetItem(2, selectedRoomState)
        RoomListQuickActionsSharedAction.NotificationsMute(roomSummary.roomId)
            .toBottomSheetItem(3, selectedRoomState)

        if (showAll) {
            RoomListQuickActionsSharedAction.Leave(roomSummary.roomId).toBottomSheetItem(5)
        }
    }

    private fun RoomListQuickActionsSharedAction.toBottomSheetItem(index: Int, roomNotificationState: RoomNotificationState? = null) {
        val selected = when (this) {
            is RoomListQuickActionsSharedAction.NotificationsAllNoisy -> roomNotificationState == RoomNotificationState.ALL_MESSAGES_NOISY
            is RoomListQuickActionsSharedAction.NotificationsAll -> roomNotificationState == RoomNotificationState.ALL_MESSAGES
            is RoomListQuickActionsSharedAction.NotificationsMentionsOnly -> roomNotificationState == RoomNotificationState.MENTIONS_ONLY
            is RoomListQuickActionsSharedAction.NotificationsMute -> roomNotificationState == RoomNotificationState.MUTE
            else                                                          -> false
        }
        return bottomSheetActionItem {
            id("action_$index")
            selected(selected)
            iconRes(iconResId)
            textRes(titleRes)
            destructive(this@toBottomSheetItem.destructive)
            listener(View.OnClickListener { listener?.didSelectMenuAction(this@toBottomSheetItem) })
        }
    }

    interface Listener {
        fun didSelectMenuAction(quickAction: RoomListQuickActionsSharedAction)
    }
}
