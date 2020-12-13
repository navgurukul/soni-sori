package org.navgurukul.chat.features.home.room.detail.timeline.helper

import com.airbnb.epoxy.VisibilityState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.features.home.room.detail.RoomDetailAction
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
            callback?.onTimelineItemAction(RoomDetailAction.TimelineEventTurnsVisible(event))
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            callback?.onTimelineItemAction(RoomDetailAction.TimelineEventTurnsInvisible(event))
        }
    }
}

class MergedTimelineEventVisibilityStateChangedListener(private val callback: TimelineEventController.Callback?,
                                                        private val events: List<TimelineEvent>)
    : MerakiEpoxyModel.OnVisibilityStateChangedListener {

    override fun onVisibilityStateChanged(visibilityState: Int) {
        if (visibilityState == VisibilityState.VISIBLE) {
            events.forEach { callback?.onTimelineItemAction(RoomDetailAction.TimelineEventTurnsVisible(it)) }
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            events.forEach { callback?.onTimelineItemAction(RoomDetailAction.TimelineEventTurnsInvisible(it)) }
        }
    }
}
