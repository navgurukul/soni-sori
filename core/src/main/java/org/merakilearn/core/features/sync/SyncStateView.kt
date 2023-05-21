package org.merakilearn.core.features.sync

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import org.matrix.android.sdk.api.session.sync.SyncState
import org.navgurukul.chat.R
import org.navgurukul.chat.core.utils.isAirplaneModeOn
import org.navgurukul.chat.databinding.ViewSyncStateBinding

class SyncStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    val mBinding: ViewSyncStateBinding
    init {
        mBinding = ViewSyncStateBinding.inflate(LayoutInflater.from(context), this)
    }

    fun render(newState: SyncState) {
        mBinding.apply {
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
}