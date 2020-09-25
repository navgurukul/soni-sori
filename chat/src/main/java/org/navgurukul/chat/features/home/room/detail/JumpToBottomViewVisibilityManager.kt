package org.navgurukul.chat.features.home.room.detail

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.navgurukul.chat.core.utils.Debouncer

/**
 * Show or hide the jumpToBottomView, depending on the scrolling and if the timeline is displaying the more recent event
 * - When user scrolls up (i.e. going to the past): hide
 * - When user scrolls down: show if not displaying last event
 * - When user stops scrolling: show if not displaying last event
 */
class JumpToBottomViewVisibilityManager(
    private val jumpToBottomView: FloatingActionButton,
    private val debouncer: Debouncer,
    recyclerView: RecyclerView,
    private val layoutManager: LinearLayoutManager
) {

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                debouncer.cancel("jump_to_bottom_visibility")

                val scrollingToPast = dy < 0

                if (scrollingToPast) {
                    jumpToBottomView.hide()
                } else {
                    maybeShowJumpToBottomViewVisibility()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE     -> {
                        maybeShowJumpToBottomViewVisibilityWithDelay()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING,
                    RecyclerView.SCROLL_STATE_SETTLING -> Unit
                }
            }
        })
    }

    fun maybeShowJumpToBottomViewVisibilityWithDelay() {
        debouncer.debounce("jump_to_bottom_visibility", 250, Runnable {
            maybeShowJumpToBottomViewVisibility()
        })
    }

    private fun maybeShowJumpToBottomViewVisibility() {
        if (layoutManager.findFirstVisibleItemPosition() != 0) {
            jumpToBottomView.show()
        } else {
            jumpToBottomView.hide()
        }
    }
}

