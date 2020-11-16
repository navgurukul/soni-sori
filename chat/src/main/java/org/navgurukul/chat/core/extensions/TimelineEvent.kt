package org.navgurukul.chat.core.extensions

import im.vector.matrix.android.api.session.events.model.EventType
import im.vector.matrix.android.api.session.room.send.SendState
import im.vector.matrix.android.api.session.room.timeline.TimelineEvent

fun TimelineEvent.canReact(): Boolean {
    // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
    return root.getClearType() == EventType.MESSAGE && root.sendState == SendState.SYNCED && !root.isRedacted()
}
