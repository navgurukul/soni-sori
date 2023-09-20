package org.merakilearn.core.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.io.File

interface AppModuleNavigator {
    fun launchIntentForHomeActivity(context: Context, clearNotification: Boolean): Intent
    fun launchIntentForOnBoardingActivity(context: Context, clearNotification: Boolean): Intent
}

interface ChatModuleNavigator {
    fun launchIntentForRoom(context: Context, roomId: String): Intent
    fun launchIntentForRoomProfile(context: Context, roomId: String): Intent
}

interface PlaygroundModuleNavigator {
    fun launchPlaygroundActivity(context: Context, code: String?, isFromCourse: Boolean): Intent
    fun openPlaygroundWithFileContent(context: Context, file: File): Intent
}

sealed class Mode: Parcelable {
    @Parcelize
    object Playground : Mode()
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Course(val content: List<Char>, val code: String) : Mode()
}

interface TypingAppModuleNavigator {
    fun launchTypingApp(activity: FragmentActivity, mode : Mode)
}