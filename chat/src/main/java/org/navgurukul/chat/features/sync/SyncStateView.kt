package org.navgurukul.chat.features.sync

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.session.sync.SyncState
import kotlinx.android.synthetic.main.view_sync_state.view.*
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.isAirplaneModeOn

class SyncStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    init {
        View.inflate(context, R.layout.view_sync_state, this)
    }

    fun render(newState: SyncState) {
        syncStateProgressBar.isVisible = newState is SyncState.Running && newState.afterPause

        if (newState == SyncState.NoNetwork) {
            val isAirplaneModeOn = isAirplaneModeOn(context)
            syncStateNoNetwork.isVisible = isAirplaneModeOn.not()
            syncStateNoNetworkAirplane.isVisible = isAirplaneModeOn
        } else {
            syncStateNoNetwork.isVisible = false
            syncStateNoNetworkAirplane.isVisible = false
        }
    }
}