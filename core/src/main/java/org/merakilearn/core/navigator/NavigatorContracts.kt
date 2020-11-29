package org.merakilearn.core.navigator

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

interface PlaygroundModuleNavigator{
    fun launchPlaygroundActivity(context: Context, code: String):Intent
}