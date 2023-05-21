package org.navgurukul.chat.features.navigator

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import org.navgurukul.chat.features.media.AttachmentData
import org.navgurukul.chat.features.media.MerakiAttachmentViewerActivity

class ChatInternalNavigator {

    fun openMediaViewer(activity: Activity,
                        roomId: String,
                        mediaData: AttachmentData,
                        view: View,
                        inMemory: List<AttachmentData> =  emptyList(),
                        options: ((MutableList<Pair<View, String>>) -> Unit)?) {
        MerakiAttachmentViewerActivity.newIntent(activity,
            mediaData,
            roomId,
            mediaData.eventId,
            inMemory,
            ViewCompat.getTransitionName(view)).let { intent ->
            val pairs = ArrayList<Pair<View, String>>()
            activity.window.decorView.findViewById<View>(android.R.id.statusBarBackground)?.let {
                pairs.add(Pair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
            }
            activity.window.decorView.findViewById<View>(android.R.id.navigationBarBackground)?.let {
                pairs.add(Pair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
            }

            pairs.add(Pair(view, ViewCompat.getTransitionName(view) ?: ""))
            options?.invoke(pairs)

            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs.toTypedArray()).toBundle()
            activity.startActivity(intent, bundle)
        }
    }
}