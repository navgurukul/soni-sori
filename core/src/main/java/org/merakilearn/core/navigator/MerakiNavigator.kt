package org.merakilearn.core.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.TaskStackBuilder

class MerakiNavigator(
    private val appModuleNavigator: AppModuleNavigator,
    private val chatModuleNavigator: ChatModuleNavigator
) {

    fun launcherIntent(context: Context, clearNotification: Boolean): Intent = appModuleNavigator.launchIntentForLauncherActivity(context, clearNotification)

    fun homeLauncherIntent(context: Context, clearNotification: Boolean): Intent = appModuleNavigator.launchIntentForHomeActivity(context, clearNotification)

    fun openLauncherActivity(context: Context, clearNotification: Boolean) {
        startActivity(context, launcherIntent(context, clearNotification), false)
    }

    fun openRoomIntent(context: Context, roomId: String) = chatModuleNavigator.launchIntentForRoom(context, roomId)

    fun openRoom(context: Context, roomId: String) {
        startActivity(context, openRoomIntent(context, roomId), false)
    }

    fun openDeepLink(context: Context, deepLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        startActivity(context, intent, false)
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