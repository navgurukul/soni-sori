package org.navgurukul.chat.features.home.room.detail

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.commonui.platform.DefaultListUpdateCallback
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

/**
 * This handles scrolling to an event which wasn't yet loaded when scheduled.
 */
class ScrollOnHighlightedEventCallback(private val recyclerView: RecyclerView,
                                       private val layoutManager: LinearLayoutManager,
                                       private val timelineEventController: TimelineEventController
) : DefaultListUpdateCallback {

    private val scheduledEventId = AtomicReference<String?>()

    var timeline: Timeline? = null

    override fun onInserted(position: Int, count: Int) {
        scrollIfNeeded()
    }

    override fun onChanged(position: Int, count: Int, tag: Any?) {
        scrollIfNeeded()
    }

    private fun scrollIfNeeded() {
        val eventId = scheduledEventId.get() ?: return
        val nonNullTimeline = timeline ?: return
        val correctedEventId = nonNullTimeline.getFirstDisplayableEventId(eventId)
        val positionToScroll = timelineEventController.searchPositionOfEvent(correctedEventId)
        if (positionToScroll != null) {
            val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()

            // Do not scroll it item is already visible
            if (positionToScroll !in firstVisibleItem..lastVisibleItem) {
                Timber.v("Scroll to $positionToScroll")
                recyclerView.stopScroll()
                layoutManager.scrollToPosition(positionToScroll)
            }
            scheduledEventId.set(null)
        }
    }

    fun scheduleScrollTo(eventId: String?) {
        scheduledEventId.set(eventId)
    }
}