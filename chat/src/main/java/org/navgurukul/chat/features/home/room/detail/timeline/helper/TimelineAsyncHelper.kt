package org.navgurukul.chat.features.home.room.detail.timeline.helper

import android.os.Handler
import android.os.HandlerThread

private const val THREAD_NAME = "Timeline_Building_Thread"

object TimelineAsyncHelper {

    private var backgroundHandler: Handler? = null

    fun getBackgroundHandler(): Handler {
        return backgroundHandler ?: createBackgroundHandler().also { backgroundHandler = it }
    }

    private fun createBackgroundHandler(): Handler {
        val handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        return Handler(handlerThread.looper)
    }
}