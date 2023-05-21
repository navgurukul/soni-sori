package org.navgurukul.chat.core.extensions

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun TimelineEvent.canReact(): Boolean {
    // Only event of type Event.EVENT_TYPE_MESSAGE are supported for the moment
    return root.getClearType() == EventType.MESSAGE && root.sendState == SendState.SYNCED && !root.isRedacted()
}
