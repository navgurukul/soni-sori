package org.navgurukul.chat.features.home.room.list.actions

import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewState


/**
 * Activity shared view model to handle room list quick actions
 */
class RoomListQuickActionsSharedActionViewModel : BaseViewModel<RoomListQuickActionsSharedAction, EmptyViewState>(EmptyViewState) {
    fun handle(sharedAction: RoomListQuickActionsSharedAction) {
        _viewEvents.setValue(sharedAction)
    }
}
