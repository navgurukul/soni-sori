
package org.navgurukul.chat.features.navigator

import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import org.navgurukul.chat.core.error.fatalError
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.home.room.detail.RoomDetailActivity
import org.navgurukul.chat.features.home.room.detail.RoomDetailArgs
import org.navgurukul.chat.features.settings.ChatPreferences

class DefaultChatNavigator(
    private val sessionHolder: ActiveSessionHolder,
    private val chatPreferences: ChatPreferences
) : ChatNavigator {

    override fun openRoom(context: Context, roomId: String, eventId: String?, buildTask: Boolean) {
        if (sessionHolder.getSafeActiveSession()?.getRoom(roomId) == null) {
            fatalError("Trying to open an unknown room $roomId", chatPreferences.failFast())
            return
        }
        val args = RoomDetailArgs(roomId, eventId)
        val intent = RoomDetailActivity.newIntent(context, args)
        startActivity(context, intent, buildTask)
    }


    private fun startActivity(context: Context, intent: Intent, buildTask: Boolean) {
        if (buildTask) {
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        } else {
            context.startActivity(intent)
        }
    }
}