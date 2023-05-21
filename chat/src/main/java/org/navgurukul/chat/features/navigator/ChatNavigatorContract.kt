
package org.navgurukul.chat.features.navigator

import android.content.Context
import android.content.Intent
import org.merakilearn.core.navigator.ChatModuleNavigator
import org.navgurukul.chat.core.error.fatalError
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.home.room.detail.RoomDetailActivity
import org.navgurukul.chat.features.home.room.detail.RoomDetailArgs
import org.navgurukul.chat.features.roomprofile.RoomProfileActivity
import org.navgurukul.chat.features.settings.ChatPreferences

class ChatNavigatorContract(
    private val sessionHolder: ActiveSessionHolder,
    private val chatPreferences: ChatPreferences
) : ChatModuleNavigator {

    override fun launchIntentForRoom(context: Context, roomId: String): Intent {
        if (sessionHolder.getSafeActiveSession()?.getRoom(roomId) == null) {
            fatalError("Trying to open an unknown room $roomId", chatPreferences.failFast())
        }
        val args = RoomDetailArgs(roomId)
        return RoomDetailActivity.newIntent(context, args)
    }

    override fun launchIntentForRoomProfile(context: Context, roomId: String): Intent {
        return RoomProfileActivity.newIntent(context, roomId)
    }
}