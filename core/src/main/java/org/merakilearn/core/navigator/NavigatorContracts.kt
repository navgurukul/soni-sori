package org.merakilearn.core.navigator

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import java.io.File

interface AppModuleNavigator {
    fun launchIntentForLauncherActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForHomeActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForOnBoardingActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForLearnActivity(context: FragmentActivity,clearNotification: Boolean,pathway_name:String)

}

interface ChatModuleNavigator {
    fun launchIntentForRoom(context: Context, roomId: String): Intent
    fun launchIntentForRoomProfile(context: Context, roomId: String): Intent
}

interface PlaygroundModuleNavigator {
    fun launchPlaygroundActivity(context: Context, code: String): Intent
    fun openPlaygroundWithFileContent(context: Context, file: File): Intent
}

interface TypingAppModuleNavigator {
    sealed class Mode {
        object Playground : Mode()
        data class Course(val content: ArrayList<String>, val code: String) : Mode()
    }
    fun launchTypingApp(activity: FragmentActivity, mode : Mode)
}