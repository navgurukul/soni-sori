package org.merakilearn.core.navigator

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.FragmentActivity
import java.io.File
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

    fun openPlaygroundWithFileContent(context: Context, file: File) {
        startActivity(
            context,
            playgroundModuleNavigator.openPlaygroundWithFileContent(context, file),
            false
        )
    }

    fun openRoomProfile(context: Context, roomId: String) {
        startActivity(
            context,
            chatModuleNavigator.launchIntentForRoomProfile(context, roomId),
            false
        )
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

    fun launchTypingApp(activity: FragmentActivity, mode: Mode) {
        typingAppModuleNavigator?.launchTypingApp(activity, mode)
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

    fun openCustomTab(url: String, context: Context) {
        val packages = getCustomTabsPackages(context)
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
        if (packages.isNotEmpty()) {
            val preferredPackage: String =
                packages.map { it.activityInfo.packageName }.firstOrNull { it.contains("chrome") }
                    ?: packages[0].activityInfo.packageName
            customTabsIntent.intent.setPackage(preferredPackage)
        }
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    private fun getCustomTabsPackages(context: Context): ArrayList<ResolveInfo> {
        val pm = context.packageManager
        // Get default VIEW intent handler.
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs: ArrayList<ResolveInfo> = ArrayList()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info)
            }
        }
        return packagesSupportingCustomTabs
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