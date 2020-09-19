package org.navgurukul.chat.features.home.room.detail.timeline.helper

import com.airbnb.epoxy.VisibilityState
import im.vector.matrix.android.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

class ReadMarkerVisibilityStateChangedListener(private val callback: TimelineEventController.Callback?)
    : MerakiEpoxyModel.OnVisibilityStateChangedListener {

    override fun onVisibilityStateChanged(visibilityState: Int) {
        if (visibilityState == VisibilityState.VISIBLE) {
            callback?.onReadMarkerVisible()
        }
    }
}

class TimelineEventVisibilityStateChangedListener(private val callback: TimelineEventController.Callback?,
                                                  private val event: TimelineEvent)
    : MerakiEpoxyModel.OnVisibilityStateChangedListener {

    override fun onVisibilityStateChanged(visibilityState: Int) {
        if (visibilityState == VisibilityState.VISIBLE) {
            callback?.onEventVisible(event)
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            callback?.onEventInvisible(event)
        }
    }
}

class MergedTimelineEventVisibilityStateChangedListener(private val callback: TimelineEventController.Callback?,
                                                        private val events: List<TimelineEvent>)
    : MerakiEpoxyModel.OnVisibilityStateChangedListener {

    override fun onVisibilityStateChanged(visibilityState: Int) {
        if (visibilityState == VisibilityState.VISIBLE) {
            events.forEach { callback?.onEventVisible(it) }
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            events.forEach { callback?.onEventInvisible(it) }
        }
    }
}
