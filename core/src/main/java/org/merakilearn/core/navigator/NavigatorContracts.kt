package org.merakilearn.core.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent

interface AppModuleNavigator {
    fun launchIntentForLauncherActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForHomeActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForOnBoardingActivity(context: Context, clearNotification: Boolean): Intent
}

interface ChatModuleNavigator {
    fun launchIntentForRoom(context: Context, roomId: String): Intent
    fun launchIntentForRoomProfile(context: Context, roomId: String): Intent
}

interface PlaygroundModuleNavigator {
    fun launchPlaygroundActivity(context: Context, code: String): Intent
    fun openPlaygroundWithFileContent(context: Context, fileName: String): Intent
}

interface TypingAppModuleNavigator {
    sealed class Mode {
        object Playground : Mode()
        data class Course(val content: ArrayList<String>, val code: String) : Mode()
    }
    fun launchTypingApp(activity: Activity, mode : Mode)
}