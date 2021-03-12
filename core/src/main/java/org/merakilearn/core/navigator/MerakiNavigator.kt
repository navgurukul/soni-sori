package org.merakilearn.core.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.FragmentActivity
import java.net.URL
import java.util.*

class MerakiNavigator(
    private val appModuleNavigator: AppModuleNavigator,
    private val chatModuleNavigator: ChatModuleNavigator,
    private val playgroundModuleNavigator: PlaygroundModuleNavigator
) {

    private val typingAppModuleNavigator: TypingAppModuleNavigator? by lazy {
        val serviceIterator = ServiceLoader.load(
            TypingAppModuleNavigator::class.java,
            TypingAppModuleNavigator::class.java.classLoader
        ).iterator()
        if (serviceIterator.hasNext()) {
            serviceIterator.next()
        } else {
            null
        }
    }

    fun launcherIntent(context: Context, clearNotification: Boolean): Intent =
        appModuleNavigator.launchIntentForLauncherActivity(context, clearNotification)

    fun homeLauncherIntent(context: Context, clearNotification: Boolean): Intent =
        appModuleNavigator.launchIntentForHomeActivity(context, clearNotification)

    fun openLauncherActivity(context: Context, clearNotification: Boolean) {
        startActivity(context, launcherIntent(context, clearNotification), false)
    }

    fun openPlayground(context: Context, code: String) {
        startActivity(
            context,
            playgroundModuleNavigator.launchPlaygroundActivity(context, code),
            false
        )
    }

    fun openPlaygroundWithFileContent(context: Context, fileName: String) {
        startActivity(
            context,
            playgroundModuleNavigator.openPlaygroundWithFileContent(context, fileName),
            false
        )
    }

    fun openRoomProfile(context: Context, roomId: String) {
        startActivity(context, chatModuleNavigator.launchIntentForRoomProfile(context, roomId), false)
    }

    fun openHome(context: Context, clearNotification: Boolean) =
        startActivity(context, homeLauncherIntent(context, clearNotification), false)

    fun openRoomIntent(context: Context, roomId: String) =
        chatModuleNavigator.launchIntentForRoom(context, roomId)

    fun openRoom(context: Context, roomId: String, buildTask: Boolean = false) {
        startActivity(context, openRoomIntent(context, roomId), buildTask)
    }

    fun openDeepLink(context: Context, deepLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        if (isMerakiUrl(deepLink)) {
            intent.setPackage(context.packageName)
        }
        startActivity(context, intent, false)
    }

    fun restartApp(context: Context, clearNotification: Boolean) {
        startActivity(
            context,
            appModuleNavigator.launchIntentForOnBoardingActivity(context, clearNotification),
            buildTask = false,
            newTask = true
        )
    }

    fun launchTypingApp(activity: FragmentActivity, content: ArrayList<String>, code: String) {
        typingAppModuleNavigator?.launchTypingApp(activity, content, code)
    }

    private fun startActivity(
        context: Context,
        intent: Intent,
        buildTask: Boolean,
        newTask: Boolean = false
    ) {
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        if (buildTask) {
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        } else {
            context.startActivity(intent)
        }
    }

    companion object {
        const val MERAKI_DEEP_LINK_URL = "merakilearn.org"

        fun isMerakiUrl(url: String): Boolean {
            return try {
                URL(url).host == MERAKI_DEEP_LINK_URL
            } catch (e: Exception) {
                false
            }
        }
    }
}