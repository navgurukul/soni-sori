package org.navgurukul.chat.core.animations

import android.view.View
import com.google.android.material.appbar.AppBarLayout

class MerakiItemAppBarStateChangeListener(private val toolbarViews: List<View>) : AppBarStateChangeListener() {

    override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
        if (state == State.COLLAPSED) {
            toolbarViews.forEach {
                it.animate().alpha(1f).duration = 150
            }
        } else {
            toolbarViews.forEach {
                it.animate().alpha(0f).duration = 150
            }
        }
    }
}