package org.navgurukul.chat.features.sync

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.session.sync.SyncState
import org.navgurukul.chat.databinding.ViewSyncStateBinding
import org.navgurukul.chat.core.utils.isAirplaneModeOn

class SyncStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private val binding: ViewSyncStateBinding = ViewSyncStateBinding.inflate(LayoutInflater.from(context), this)

    fun render(newState: SyncState) {
        binding.syncStateProgressBar.isVisible = newState is SyncState.Running && newState.afterPause

        if (newState == SyncState.NoNetwork) {
            val isAirplaneModeOn = isAirplaneModeOn(context)
            binding.syncStateNoNetwork.isVisible = !isAirplaneModeOn
            binding.syncStateNoNetworkAirplane.isVisible = isAirplaneModeOn
        } else {
            binding.syncStateNoNetwork.isVisible = false
            binding.syncStateNoNetworkAirplane.isVisible = false
        }
    }
}