package org.navgurukul.chat.core.pushers

import org.matrix.android.sdk.api.MatrixCallback
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.resources.LocaleProvider
import org.navgurukul.commonui.resources.StringProvider
import kotlin.math.abs

private const val DEFAULT_PUSHER_FILE_TAG = "mobile"

class PushersManager(
    private val activeSessionHolder: ActiveSessionHolder,
    private val localeProvider: LocaleProvider,
    private val stringProvider: StringProvider
) {

    fun registerPusherWithFcmKey(pushKey: String) {
        val currentSession = activeSessionHolder.getSafeActiveSession() ?: return
        val profileTag = DEFAULT_PUSHER_FILE_TAG + "_" + abs(currentSession.myUserId.hashCode())

        currentSession.addHttpPusher(
                pushKey,
                stringProvider.getString(R.string.pusher_app_id),
                profileTag,
                localeProvider.current().language,
                stringProvider.getString(R.string.app_name),
                currentSession.sessionParams.deviceId ?: "MOBILE",
                stringProvider.getString(R.string.pusher_http_url),
                append = false,
                withEventIdOnly = true
        )
    }

    fun unregisterPusher(pushKey: String, callback: MatrixCallback<Unit>) {
        val currentSession = activeSessionHolder.getSafeActiveSession() ?: return
        currentSession.removeHttpPusher(pushKey, stringProvider.getString(R.string.pusher_app_id), callback)
    }
}
